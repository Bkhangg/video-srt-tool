import struct
import zipfile
import pathlib

src = pathlib.Path('/root/.buddy/work/apk-analysis/Video_SRT_Tool_2.9.1.apk')
out = pathlib.Path('/root/.buddy/work/apk-analysis/unsigned.apk')
dexdir = pathlib.Path('/root/.buddy/work/apk-analysis/dexout')
dexes = {p.name: p.read_bytes() for p in sorted(dexdir.glob('classes*.dex'))}

NEW_VERSION_CODE = 40
NEW_VERSION_NAME = '3.1.0-BKggDs'
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
