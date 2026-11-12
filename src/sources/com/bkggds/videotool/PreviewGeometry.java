package com.bkggds.videotool;

/* loaded from: classes.dex */
public final class PreviewGeometry {
    public static int[] fit(int i, int i2, int i3, int i4) {
        int max = Math.max(1, i);
        int max2 = Math.max(1, i2);
        if (i3 <= 0 || i4 <= 0) {
            i3 = 16;
            i4 = 9;
        }
        double d = i3;
        double d2 = i4;
        double min = Math.min(max / d, max2 / d2);
        return new int[]{Math.max(1, Math.min(max, (int) Math.round(d * min))), Math.max(1, Math.min(max2, (int) Math.round(d2 * min)))};
    }
}
