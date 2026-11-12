package com.bkggds.videotool;

/* loaded from: classes.dex */
public final class EditorScrollPolicy {
    public static boolean keepGesture(float f, float f2, int i, boolean z, boolean z2) {
        if (Math.abs(f2) <= i || Math.abs(f) > Math.abs(f2)) {
            return true;
        }
        return f2 < 0.0f ? z2 : z;
    }

    public static int clampSelection(int i, int i2) {
        return Math.max(0, Math.min(i, i2));
    }
}
