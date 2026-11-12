package com.bkggds.videotool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class SrtText {
    private static final Pattern TIME = Pattern.compile("^(\\d{1,3}):([0-5]\\d):([0-5]\\d)[,.](\\d{1,3})\\s*-->\\s*(\\d{1,3}):([0-5]\\d):([0-5]\\d)[,.](\\d{1,3})(?:\\s+.*)?$");
    private static final Pattern ASS_PAYLOAD = Pattern.compile("(?s)^\\s*\\d+,\\s*-?\\d+,[^,\\r\\n]*\\p{L}[^,\\r\\n]*,[^,\\r\\n]*,\\s*\\d+,\\s*\\d+,\\s*\\d+,[^,\\r\\n]*,(.*)$");

    public static final class Cue {
        public final String body;
        public final long end;
        public final long start;

        Cue(long j, long j2, String str) {
            this.start = j;
            this.end = j2;
            this.body = str;
        }
    }

    public static String cleanAssPayload(String str) {
        Matcher matcher = ASS_PAYLOAD.matcher(str);
        return matcher.matches() ? matcher.group(1) : str;
    }

    private static long time(Matcher matcher, int i) {
        String group = matcher.group(i + 3);
        while (group.length() < 3) {
            group = String.valueOf(group) + "0";
        }
        return (Long.parseLong(matcher.group(i)) * 3600000) + (Long.parseLong(matcher.group(i + 1)) * 60000) + (Long.parseLong(matcher.group(i + 2)) * 1000) + Long.parseLong(group);
    }

    private static String stamp(long j) {
        return String.format(Locale.US, "%02d:%02d:%02d,%03d", Long.valueOf(j / 3600000), Long.valueOf((j / 60000) % 60), Long.valueOf((j / 1000) % 60), Long.valueOf(j % 1000));
    }

    public static String visible(String str) {
        return str.replaceAll("<[^>]*>", "").replaceAll("[\\s\\p{Cf}\\u00a0]+", "").replace("&nbsp;", "");
    }

    public static List<Cue> parse(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Invalid SRT");
        }
        String str2 = str.replace("\ufeff", "").replace("\r\n", "\n").replace('\r', '\n').trim();
        if (str2.startsWith("```")) {
            int indexOf = str2.indexOf(10);
            if (indexOf >= 0 && str2.endsWith("```")) {
                str2 = str2.substring(indexOf + 1, str2.length() - 3).trim();
            }
        }
        List<Cue> arrayList = new ArrayList<>();
        String[] split = str2.split("\\n[\\t ]*\\n+");
        int i = 0;
        while (i < split.length) {
            String[] split2 = split[i].trim().split("\n", -1);
            int i2 = (split2.length <= 0 || !split2[0].trim().matches("\\d+")) ? 0 : 1;
            if (i2 >= split2.length) {
                throw new IllegalArgumentException("Invalid SRT");
            }
            Matcher matcher = TIME.matcher(split2[i2].trim());
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid SRT");
            }
            long time = time(matcher, 1);
            long time2 = time(matcher, 5);
            if (time2 <= time) {
                throw new IllegalArgumentException("Invalid SRT");
            }
            StringBuilder sb = new StringBuilder();
            while (true) {
                i2++;
                if (i2 >= split2.length) {
                    String trim = cleanAssPayload(sb.toString()).replaceAll("\\{\\\\[^}]*\\}", "").replace("\\N", "\n").replace("\\h", " ").replaceAll("(?i)<br\\s*/?>", "\n").replaceAll("(?i)<(?!/?(?:b|i|u)\\s*>)[^>]*>", "").trim();
                    if (visible(trim).length() == 0) {
                        throw new IllegalArgumentException("Invalid SRT");
                    }
                    arrayList.add(new Cue(time, time2, trim));
                    i++;
                    break;
                }
                if (split2[i2].contains("-->")) {
                    throw new IllegalArgumentException("Invalid SRT");
                }
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(split2[i2]);
            }
        }
        if (!arrayList.isEmpty()) {
            Collections.sort(arrayList, new Comparator<Cue>() { // from class: com.bkggds.videotool.SrtText.1
                @Override // java.util.Comparator
                public int compare(Cue cue, Cue cue2) {
                    return Long.compare(cue.start, cue2.start);
                }
            });
            return arrayList;
        }
        throw new IllegalArgumentException("Invalid SRT");
    }

    private static String activeBody(Set<Cue> set) {
        StringBuilder sb = new StringBuilder();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Cue cue : set) {
            if (linkedHashSet.add(cue.body)) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(cue.body);
            }
        }
        return sb.toString();
    }

    public static String normalize(String str) {
        int i;
        List<Cue> parse = parse(str);
        TreeMap treeMap = new TreeMap();
        Iterator<Cue> it = parse.iterator();
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            Cue next = it.next();
            long[] jArr = {next.start, next.end};
            while (i < 2) {
                long j = jArr[i];
                List list = (List) treeMap.get(Long.valueOf(j));
                if (list == null) {
                    list = new ArrayList();
                    treeMap.put(Long.valueOf(j), list);
                }
                list.add(next);
                i++;
            }
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        ArrayList<Cue> arrayList = new ArrayList();
        long longValue = ((Long) treeMap.firstKey()).longValue();
        long j2 = longValue;
        for (Object item : treeMap.entrySet()) {
            Map.Entry entry = (Map.Entry) item;
            long longValue2 = ((Long) entry.getKey()).longValue();
            if (longValue2 > j2 && !linkedHashSet.isEmpty()) {
                String activeBody = activeBody(linkedHashSet);
                int size = arrayList.size() - 1;
                if (size >= 0 && ((Cue) arrayList.get(size)).end == j2 && ((Cue) arrayList.get(size)).body.equals(activeBody)) {
                    arrayList.add(new Cue(((Cue) arrayList.remove(size)).start, longValue2, activeBody));
                } else {
                    arrayList.add(new Cue(j2, longValue2, activeBody));
                }
            }
            for (Object item2 : (List) entry.getValue()) {
                Cue cue = (Cue) item2;
                if (cue.end == longValue2) {
                    linkedHashSet.remove(cue);
                }
            }
            for (Object item3 : (List) entry.getValue()) {
                Cue cue2 = (Cue) item3;
                if (cue2.start == longValue2) {
                    linkedHashSet.add(cue2);
                }
            }
            j2 = longValue2;
        }
        StringBuilder sb = new StringBuilder();
        for (Cue cue3 : arrayList) {
            i++;
            sb.append(i).append('\n').append(stamp(cue3.start)).append(" --> ").append(stamp(cue3.end)).append('\n').append(cue3.body).append("\n\n");
        }
        return sb.toString();
    }

    public static boolean hasCueWithin(String str, double d) {
        for (Cue cue : parse(str)) {
            if (d <= 0.0d || cue.start < d) {
                return true;
            }
        }
        return false;
    }

    public static String decode(byte[] bArr) throws IOException {
        if (bArr.length >= 2) {
            if ((bArr[0] & 255) == 255 && (bArr[1] & 255) == 254) {
                return new String(bArr, 2, bArr.length - 2, "UTF-16LE").replace("\ufeff", "");
            }
            if ((bArr[0] & 255) == 254 && (bArr[1] & 255) == 255) {
                return new String(bArr, 2, bArr.length - 2, "UTF-16BE").replace("\ufeff", "");
            }
        }
        return new String(bArr, 0, bArr.length, "UTF-8").replace("\ufeff", "");
    }

    /* JADX WARN: Finally extract failed */
    public static String read(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("Could not open the subtitle file.");
        }
        Throwable th = null;
        try {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    byte[] bArr = new byte[8192];
                    while (true) {
                        int read = inputStream.read(bArr);
                        if (read == -1) {
                            String decode = decode(byteArrayOutputStream.toByteArray());
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            return decode;
                        }
                        if (byteArrayOutputStream.size() + read > 33554432) {
                            throw new IOException("Subtitle file is too large.");
                        }
                        byteArrayOutputStream.write(bArr, 0, read);
                    }
                } finally {
                    byteArrayOutputStream.close();
                }
            } catch (Throwable th2) {
                if (0 == 0) {
                    throw th2;
                }
                if (null != th2) {
                    th.addSuppressed(th2);
                }
                throw null;
            }
        } finally {
        }
    }

    public static boolean verifies(String str, String str2) {
        try {
            List<Cue> parse = parse(str);
            List<Cue> parse2 = parse(str2);
            for (Cue cue : parse) {
                long j = cue.start;
                boolean z = false;
                for (Cue cue2 : parse2) {
                    if (cue2.end > cue.start && cue2.start < cue.end && visible(cue2.body).contains(visible(cue.body))) {
                        if (cue2.start > 100 + j) {
                            return false;
                        }
                        j = Math.max(j, cue2.end);
                        z = true;
                    }
                }
                if (!z || j < cue.end - 100) {
                    return false;
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String convertedName(String str) {
        if (str == null || !str.toLowerCase(Locale.US).endsWith(".txt")) {
            return str;
        }
        String substring = str.substring(0, str.length() - 4);
        return substring.toLowerCase(Locale.US).endsWith(".srt") ? substring : String.valueOf(substring) + ".srt";
    }
}
