package com.bkggds.videotool;

import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
public final class SafeSrtNames {
    public static String unique(String str, Set<String> set) {
        String trim = str == null ? "" : str.replaceAll("[\\p{Cntrl}/\\\\:*?\"<>|]", "_").trim();
        if (trim.length() == 0) {
            trim = "subtitles.srt";
        }
        if (!trim.toLowerCase(Locale.US).endsWith(".srt")) {
            trim = String.valueOf(trim) + ".srt";
        }
        String substring = trim.substring(0, trim.length() - 4);
        if (substring.length() > 120) {
            substring = substring.substring(0, 120);
        }
        String str2 = String.valueOf(substring) + ".srt";
        int i = 2;
        while (!set.add(str2.toLowerCase(Locale.US))) {
            str2 = String.valueOf(substring) + "-" + i + ".srt";
            i++;
        }
        return str2;
    }
}
