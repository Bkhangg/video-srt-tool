package com.bkggds.videotool;

/* loaded from: classes.dex */
public final class FontCatalog {
    public static final String[] IDS = {"system", "plusjakartasans", "manrope", "sora", "outfit", "dmsans", "lora", "playfairdisplay", "literata", "spacegrotesk", "jetbrainsmono", "nunito"};
    public static final String[] NAMES = {"Default font", "Plus Jakarta Sans", "Manrope", "Sora", "Outfit", "DM Sans", "Lora", "Playfair Display", "Literata", "Space Grotesk", "JetBrains Mono", "Nunito"};

    public static int index(String str) {
        for (int i = 0; i < IDS.length; i++) {
            if (IDS[i].equals(str)) {
                return i;
            }
        }
        return 0;
    }

    public static String valid(String str) {
        return IDS[index(str)];
    }
}
