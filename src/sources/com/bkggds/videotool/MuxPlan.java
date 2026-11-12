package com.bkggds.videotool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: classes.dex */
public final class MuxPlan {
    public static String container(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Unsupported video format");
        }
        for (String str2 : str.split(",")) {
            if (str2.equals("matroska") || str2.equals("webm")) {
                return "mkv";
            }
            if (str2.equals("mp4") || str2.equals("mov")) {
                return "mp4";
            }
        }
        throw new IllegalArgumentException("Unsupported video format");
    }

    public static String iso3(String str) {
        String language = SrtTrackInfo.language(str);
        if (language != null) {
            return new Locale(language.split("-")[0]).getISO3Language();
        }
        throw new IllegalArgumentException("Invalid language");
    }

    public static String[] arguments(String str, String str2, String str3, String str4, int i, String str5, String str6) {
        if (!str4.equals("mkv") && !str4.equals("mp4")) {
            throw new IllegalArgumentException("Unsupported container");
        }
        if (i < 0 || str5 == null || str5.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid track");
        }
        String str7 = "s:" + i;
        String[] strArr = new String[31];
        strArr[0] = "-hide_banner";
        strArr[1] = "-nostdin";
        strArr[2] = "-y";
        strArr[3] = "-i";
        strArr[4] = str;
        strArr[5] = "-sub_charenc";
        strArr[6] = "UTF-8";
        strArr[7] = "-f";
        strArr[8] = "srt";
        strArr[9] = "-i";
        strArr[10] = str2;
        strArr[11] = "-map";
        strArr[12] = "0";
        strArr[13] = "-map";
        strArr[14] = "1:s:0";
        strArr[15] = "-map_metadata";
        strArr[16] = "0";
        strArr[17] = "-map_chapters";
        strArr[18] = "0";
        strArr[19] = "-c";
        strArr[20] = "copy";
        strArr[21] = "-c:" + str7;
        strArr[22] = str4.equals("mkv") ? "srt" : "mov_text";
        strArr[23] = "-metadata:s:" + str7;
        strArr[24] = "title=" + str5.trim();
        strArr[25] = "-metadata:s:" + str7;
        strArr[26] = "language=" + iso3(str6);
        strArr[27] = "-metadata:s:" + str7;
        strArr[28] = "handler_name=" + str5.trim();
        strArr[29] = "-disposition:" + str7;
        strArr[30] = "default";
        ArrayList arrayList = new ArrayList(Arrays.asList(strArr));
        for (int i2 = 0; i2 < i; i2++) {
            arrayList.add("-disposition:s:" + i2);
            arrayList.add("-default");
        }
        if (str4.equals("mp4")) {
            arrayList.add("-movflags");
            arrayList.add("+faststart");
        }
        arrayList.add("-f");
        arrayList.add(str4.equals("mkv") ? "matroska" : "mp4");
        arrayList.add(str3);
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
