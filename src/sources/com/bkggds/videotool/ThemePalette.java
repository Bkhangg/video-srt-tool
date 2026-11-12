package com.bkggds.videotool;

/* loaded from: classes.dex */
public final class ThemePalette {
    public static int map(boolean z, int i, boolean z2) {
        if (!z) {
            return i;
        }
        if (z2) {
            if (i == -1) {
                return -16437717;
            }
            if (i == -15788246 || i == -16777216 || i == -13418155) {
                return -1906448;
            }
            if (i == -10193781 || i == -7035976 || i == -12102295) {
                return -7035976;
            }
            if (i == -14856488 || i == -15887224) {
                return -10556716;
            }
            if (i == -4645860) {
                return -219739;
            }
        } else {
            if (i == -1) {
                return -15788246;
            }
            if (i == -15788246) {
                return -10556716;
            }
            if (i == -920071 || i == -460036) {
                return -14800581;
            }
            if (i == -1906448 || i == -3418655) {
                return -13418155;
            }
            if (i == -1050881) {
                return -15258043;
            }
            if (i == -4203522) {
                return -13543565;
            }
            if (i == -68878) {
                return -12248540;
            }
            if (i == -79158) {
                return -8446691;
            }
        }
        return i;
    }
}
