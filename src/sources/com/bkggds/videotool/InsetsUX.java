package com.bkggds.videotool;

import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

/* loaded from: classes.dex */
public final class InsetsUX {
    public static WindowInsets apply(View view, WindowInsets windowInsets) {
        boolean z;
        if (Build.VERSION.SDK_INT >= 30) {
            Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            z = windowInsets.isVisible(WindowInsets.Type.ime());
        } else {
            int systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
            view.setPadding(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), systemWindowInsetBottom);
            z = systemWindowInsetBottom > view.getResources().getDisplayMetrics().heightPixels / 4;
        }
        View findViewWithTag = view.findViewWithTag("appFooter");
        if (findViewWithTag != null) {
            findViewWithTag.setVisibility(z ? 8 : 0);
        }
        return Build.VERSION.SDK_INT >= 30 ? WindowInsets.CONSUMED : windowInsets.consumeSystemWindowInsets();
    }
}
