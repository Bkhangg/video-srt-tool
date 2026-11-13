import struct
import zipfile
import pathlib

src = pathlib.Path('/root/.buddy/work/apk-analysis/Video_SRT_Tool_2.9.1.apk')
out = pathlib.Path('/root/.buddy/work/apk-analysis/unsigned.apk')
dexdir = pathlib.Path('/root/.buddy/work/apk-analysis/dexout')
dexes = {p.name: p.read_bytes() for p in sorted(dexdir.glob('classes*.dex'))}

NEW_VERSION_CODE = 42
NEW_VERSION_NAME = '3.1.2-BKggDs'
OLD_PACKAGE = 'com.buddyapps.videotool'
NEW_PACKAGE = 'com.bkggds.videotool'


def rename_axml_string(data, old, new):
    """Rebuild the AXML string pool replacing one string (UTF-16 pool)."""
    d = bytearray(data)
    typ, hsize, size = struct.unpack_from('<HHI', d, 8)
    assert typ == 0x0001 and hsize == 28, 'unexpected string pool'
    cnt, style_cnt, flags, sstart, ststart = struct.unpack_from('<IIIII', d, 16)
    assert not (flags & 0x100), 'pool is not utf-16'
    offs = list(struct.unpack_from('<%dI' % cnt, d, 8 + hsize))
    base = 8 + sstart
    strings = []
    for o in offs:
        p = base + o
        n = struct.unpack_from('<H', d, p)[0]
        p += 2
        if n & 0x8000:
            n = ((n & 0x7fff) << 16) | struct.unpack_from('<H', d, p)[0]
            p += 2
        strings.append(bytes(d[p:p + 2 * n]).decode('utf-16-le'))
    assert old in strings, 'package string not found in manifest pool'
    strings = [new if s == old else s for s in strings]
    style_offs = []
    if style_cnt:
        style_offs = list(struct.unpack_from('<%dI' % style_cnt, d, 8 + hsize + 4 * cnt))
        old_styles = bytes(d[8 + ststart:8 + size])
    blob = bytearray()
    new_offs = []
    for s in strings:
        new_offs.append(len(blob))
        n = len(s)
        if n > 0x7fff:
            blob += struct.pack('<HH', 0x8000 | (n >> 16), n & 0xffff)
        else:
            blob += struct.pack('<H', n)
        blob += s.encode('utf-16-le') + b'\x00\x00'
    while len(blob) % 4:
        blob += b'\x00'
    styles = old_styles if style_cnt else b''
    while len(styles) % 4:
        styles += b'\x00'
    sstart_new = hsize + 4 * cnt + 4 * style_cnt
    size_new = sstart_new + len(blob) + len(styles)
    chunk = bytearray(struct.pack('<HHI', 0x0001, hsize, size_new))
    chunk += struct.pack('<IIIII', cnt, style_cnt, 0, sstart_new,
                         sstart_new + len(blob) if style_cnt else 0)
    chunk += struct.pack('<%dI' % cnt, *new_offs)
    if style_cnt:
        chunk += struct.pack('<%dI' % style_cnt, *style_offs)
    chunk += blob + styles
    d[8:8 + size] = chunk
    struct.pack_into('<I', d, 4, len(d))
    return bytes(d)


def axml_pool_strings(data):
    """Read the UTF-16 string pool of a binary XML file."""
    typ, hsize, size = struct.unpack_from('<HHI', data, 8)
    assert typ == 0x0001 and hsize == 28, 'unexpected string pool'
    cnt, style_cnt, flags, sstart, ststart = struct.unpack_from('<IIIII', data, 16)
    assert not (flags & 0x100), 'pool is not utf-16'
    assert style_cnt == 0, 'styled string pool not supported'
    offs = struct.unpack_from('<%dI' % cnt, data, 8 + hsize)
    base = 8 + sstart
    strings = []
    for o in offs:
        p = base + o
        n = struct.unpack_from('<H', data, p)[0]
        p += 2
        if n & 0x8000:
            n = ((n & 0x7fff) << 16) | struct.unpack_from('<H', data, p)[0]
            p += 2
        strings.append(bytes(data[p:p + 2 * n]).decode('utf-16-le'))
    return strings, size


def axml_build_pool(strings):
    """Build a string pool chunk for the given strings (UTF-16, no styles)."""
    hsize = 28
    cnt = len(strings)
    blob = bytearray()
    new_offs = []
    for s in strings:
        new_offs.append(len(blob))
        n = len(s)
        if n > 0x7fff:
            blob += struct.pack('<HH', 0x8000 | (n >> 16), n & 0xffff)
        else:
            blob += struct.pack('<H', n)
        blob += s.encode('utf-16-le') + b'\x00\x00'
    while len(blob) % 4:
        blob += b'\x00'
    sstart_new = hsize + 4 * cnt
    size_new = sstart_new + len(blob)
    chunk = bytearray(struct.pack('<HHI', 0x0001, hsize, size_new))
    chunk += struct.pack('<IIIII', cnt, 0, 0, sstart_new, 0)
    chunk += struct.pack('<%dI' % cnt, *new_offs)
    chunk += blob
    return bytes(chunk)


def add_view_intent_filter(data):
    """Add <intent-filter VIEW/DEFAULT data mimeType=video/*> to the launcher activity.

    The manifest is binary AXML: append the new strings to the pool, extend the
    resource map so android:mimeType (0x01010026) resolves, then splice the new
    element chunks after the existing launcher intent-filter.
    """
    d = bytearray(data)
    strings, pool_size = axml_pool_strings(d)
    extra = ['android.intent.action.VIEW', 'android.intent.category.DEFAULT',
             'mimeType', 'video/*', 'data']
    for s in extra:
        if s not in strings:
            strings.append(s)
    idx_view = strings.index('android.intent.action.VIEW')
    idx_default = strings.index('android.intent.category.DEFAULT')
    idx_mime = strings.index('mimeType')
    idx_video = strings.index('video/*')
    idx_data = strings.index('data')
    idx_intent = strings.index('intent-filter')
    idx_action = strings.index('action')
    idx_category = strings.index('category')
    idx_name = strings.index('name')
    idx_android = strings.index('http://schemas.android.com/apk/res/android')

    map_pos = 8 + pool_size
    t, hs, sz = struct.unpack_from('<HHI', d, map_pos)
    assert t == 0x0180, 'resource map chunk not found'
    entries = list(struct.unpack_from('<%dI' % ((sz - hs) // 4), d, map_pos + hs))
    entries += [0] * (len(strings) - len(entries))
    entries[idx_mime] = 0x01010026  # android.R.attr.mimeType
    map_chunk = struct.pack('<HHI', 0x0180, 8, 8 + 4 * len(entries))
    map_chunk += struct.pack('<%dI' % len(entries), *entries)

    nodes = bytes(d[map_pos + sz:])
    insert_at = None
    depth = 0
    pos = 0
    while pos + 8 <= len(nodes):
        t, hs, sz = struct.unpack_from('<HHI', nodes, pos)
        if sz == 0:
            break
        if t == 0x0102 and struct.unpack_from('<I', nodes, pos + hs + 4)[0] == idx_intent:
            depth += 1
        elif t == 0x0103 and struct.unpack_from('<I', nodes, pos + hs + 4)[0] == idx_intent:
            depth -= 1
            if depth == 0:
                insert_at = pos + sz
                break
        pos += sz
    assert insert_at is not None, 'launcher intent-filter not found'

    def start(name, attrs, line):
        ext = struct.pack('<IIHHHHHH', 0xFFFFFFFF, name, 20, 20, len(attrs), 0, 0, 0)
        body = b''
        for ans, aname, araw, vtype, vdata in attrs:
            body += struct.pack('<IIIHBBI', ans, aname, araw, 8, 0, vtype, vdata)
        payload = ext + body
        return struct.pack('<HHIII', 0x0102, 16, 16 + len(payload), line, 0xFFFFFFFF) + payload

    def end(name, line):
        return struct.pack('<HHIII', 0x0103, 16, 24, line, 0xFFFFFFFF) + struct.pack('<II', 0xFFFFFFFF, name)

    line = 20
    block = start(idx_intent, [], line)
    block += start(idx_action, [(idx_android, idx_name, idx_view, 3, idx_view)], line + 1)
    block += end(idx_action, line + 1)
    block += start(idx_category, [(idx_android, idx_name, idx_default, 3, idx_default)], line + 2)
    block += end(idx_category, line + 2)
    block += start(idx_data, [(idx_android, idx_mime, idx_video, 3, idx_video)], line + 3)
    block += end(idx_data, line + 3)
    block += end(idx_intent, line + 4)

    out = bytearray(struct.pack('<HHI', 0x0003, 8, 0))
    out += axml_build_pool(strings)
    out += map_chunk
    out += nodes[:insert_at] + block + nodes[insert_at:]
    struct.pack_into('<I', out, 4, len(out))
    return bytes(out)


def rename_arsc_package(data, new):
    """Overwrite the fixed-size package name field of the resource table."""
    d = bytearray(data)
    typ, hsize, size = struct.unpack_from('<HHI', d, 0)
    assert typ == 0x0002, 'not a resource table'
    pos = hsize
    while pos + 8 <= len(d):
        t, hs, sz = struct.unpack_from('<HHI', d, pos)
        if t == 0x0200:  # RES_TABLE_PACKAGE_TYPE
            raw = new.encode('utf-16-le')
            assert len(raw) <= 256, 'package name too long'
            d[pos + 12:pos + 12 + 256] = raw + b'\x00' * (256 - len(raw))
            return bytes(d)
        if sz == 0:
            break
        pos += sz
    raise SystemExit('resource table package chunk not found')



def patch_manifest(data):
    data = bytearray(data)
    old = '2.9.1-BKggDs'.encode('utf-16le')
    new = NEW_VERSION_NAME.encode('utf-16le')
    assert len(old) == len(new), 'versionName length must stay the same'
    pos = data.find(old)
    assert pos >= 0, 'versionName not found'
    data[pos:pos + len(old)] = new

    # locate the <manifest> start element and its versionCode attribute
    o = 8
    while o + 8 <= len(data):
        t, hs, sz = struct.unpack_from('<HHI', data, o)
        if sz == 0:
            break
        if t == 0x0102:  # START_ELEMENT
            _ns, _name, astart, asize, acount = struct.unpack_from('<IIHHH', data, o + hs)
            for k in range(acount):
                a = o + hs + astart + k * asize
                name_idx = struct.unpack_from('<I', data, a + 4)[0]
                if name_idx == 7:  # versionCode
                    struct.pack_into('<I', data, a + 16, NEW_VERSION_CODE)
                    return bytes(data)
        o += sz
    raise SystemExit('versionCode attribute not found')


zin = zipfile.ZipFile(src)
items = []
for info in zin.infolist():
    data = zin.read(info.filename)
    if info.filename == 'AndroidManifest.xml':
        data = rename_axml_string(data, OLD_PACKAGE, NEW_PACKAGE)
        data = add_view_intent_filter(data)
        data = patch_manifest(data)
    elif info.filename == 'resources.arsc':
        data = rename_arsc_package(data, NEW_PACKAGE)
    items.append((info, data))
zin.close()

with zipfile.ZipFile(out, 'w') as z:
    for info, data in items:
        name = info.filename
        if name.startswith('META-INF/') and (name.endswith('.SF') or name.endswith('.RSA') or name.endswith('.DSA') or name.endswith('.MF')):
            continue
        if name == 'classes.dex':
            data = dexes['classes.dex']
        zi = zipfile.ZipInfo(name, date_time=info.date_time)
        zi.compress_type = info.compress_type
        zi.external_attr = info.external_attr
        z.writestr(zi, data)
print('wrote', out, out.stat().st_size, 'versionCode', NEW_VERSION_CODE, NEW_VERSION_NAME, 'dex:', sorted(dexes))
