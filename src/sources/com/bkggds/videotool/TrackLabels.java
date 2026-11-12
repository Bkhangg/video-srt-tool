package com.bkggds.videotool;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public final class TrackLabels {
    private static final Map<String, String> ISO = new HashMap();

    static {
        for (String str : Locale.getISOLanguages()) {
            ISO.put(str, str);
            try {
                ISO.put(new Locale(str).getISO3Language(), str);
            } catch (Exception e) {
            }
        }
        String[] strArr = {"fre", "fr", "ger", "de", "chi", "zh", "dut", "nl", "cze", "cs", "gre", "el", "rum", "ro", "per", "fa", "may", "ms", "bur", "my", "ice", "is", "wel", "cy", "slo", "sk", "alb", "sq", "arm", "hy", "geo", "ka", "baq", "eu", "tib", "bo"};
        for (int i = 0; i < 36; i += 2) {
            ISO.put(strArr[i], strArr[i + 1]);
        }
    }

    public static String language(String str) {
        String replace = str == null ? "" : str.trim().replace('_', '-');
        if (replace.length() == 0 || replace.equalsIgnoreCase("und") || replace.equalsIgnoreCase("unk")) {
            return Localization.tr("Unknown language");
        }
        String[] split = replace.split("-", 2);
        String str2 = ISO.get(split[0].toLowerCase(Locale.US));
        if (str2 != null) {
            replace = String.valueOf(str2) + (split.length > 1 ? "-" + split[1] : "");
        }
        String displayName = Locale.forLanguageTag(replace).getDisplayName(Localization.locale());
        if (displayName.length() == 0 || displayName.equalsIgnoreCase(replace)) {
            return str;
        }
        return String.valueOf(displayName) + " (" + str + ")";
    }

    public static String title(long j, int i, String str, String str2, boolean z) {
        StringBuilder sb = new StringBuilder(String.valueOf(Localization.tr("Track ")));
        if (j <= 0) {
            j = i + 1;
        }
        String sb2 = sb.append(j).append(" · ").append(language(str)).toString();
        if (str2 != null && str2.trim().length() > 0) {
            sb2 = String.valueOf(sb2) + " · " + str2.trim();
        }
        return z ? String.valueOf(sb2) + Localization.tr(" (image)") : sb2;
    }
}
