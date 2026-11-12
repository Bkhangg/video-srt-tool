package com.bkggds.videotool;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public final class SrtTrackInfo {
    private static final Map<String, String> LANGUAGES = new HashMap();

    static {
        for (String str : Locale.getISOLanguages()) {
            LANGUAGES.put(str, str);
            try {
                LANGUAGES.put(new Locale(str).getISO3Language(), str);
            } catch (Exception e) {
            }
        }
        String[] strArr = {"fre", "fr", "ger", "de", "chi", "zh", "dut", "nl", "cze", "cs", "gre", "el", "rum", "ro", "per", "fa", "may", "ms", "bur", "my", "ice", "is", "wel", "cy", "slo", "sk", "alb", "sq"};
        for (int i = 0; i < 28; i += 2) {
            LANGUAGES.put(strArr[i], strArr[i + 1]);
        }
    }

    public static String language(String str) {
        if (str == null) {
            return null;
        }
        String replace = str.trim().replace('_', '-');
        if (!replace.matches("[A-Za-z]{2,3}(?:-[A-Za-z]{2,4})?")) {
            return null;
        }
        String[] split = replace.split("-", 2);
        String str2 = LANGUAGES.get(split[0].toLowerCase(Locale.US));
        if (str2 == null) {
            return null;
        }
        return Locale.forLanguageTag(String.valueOf(str2) + (split.length == 2 ? "-" + split[1] : "")).toLanguageTag();
    }

    private static String clean(String str) {
        return str == null ? "" : str.replaceAll("[\\p{Cntrl}/\\\\:*?\"<>|]", "_").trim();
    }

    private static String limit(String str, int i) {
        if (str.length() <= i) {
            return str;
        }
        if (i > 0 && Character.isHighSurrogate(str.charAt(i - 1))) {
            i--;
        }
        return str.substring(0, i);
    }

    public static String filename(String str, String str2, String str3) {
        String language = language(str3);
        if (language == null) {
            throw new IllegalArgumentException("language");
        }
        String clean = clean(str2);
        if (clean.length() == 0) {
            throw new IllegalArgumentException("title");
        }
        String clean2 = clean(str);
        if (clean2.length() == 0) {
            clean2 = "video";
        }
        String base = limit(clean2, 60);
        return String.valueOf(base) + "." + limit(clean, Math.min(48, ((120 - base.length()) - language.length()) - 2)) + "." + language + ".srt";
    }
}
