package com.bkggds.videotool;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
public final class SidebarStyle {
    private static int dp(View view, int i) {
        return Math.round(i * view.getResources().getDisplayMetrics().density);
    }

    public static void prepare(View view) {
        view.setTag("sidebarItem");
        view.setMinimumHeight(dp(view, 52));
        view.setPadding(dp(view, 14), dp(view, 12), dp(view, 14), dp(view, 12));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(view, 10);
        view.setLayoutParams(layoutParams);
    }

    public static void select(View view, int i, int i2) {
        if (i2 == 3) {
            i2 = 2;
        }
        view.setActivated(i == i2);
    }

    public static void apply(View view) {
        int i;
        boolean dark = AppTheme.dark();
        boolean isActivated = view.isActivated();
        int i2 = dark ? isActivated ? -15511990 : -14800581 : isActivated ? -3343375 : -460036;
        int i3 = -1906448;
        if (dark) {
            i = isActivated ? -13773633 : -13418155;
        } else {
            i = isActivated ? -15887224 : -1906448;
        }
        if (!dark) {
            i3 = isActivated ? -15638951 : -15788246;
        } else if (isActivated) {
            i3 = -6687004;
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(i2);
        gradientDrawable.setCornerRadius(dp(view, 12));
        gradientDrawable.setStroke(dp(view, 1), i);
        view.setBackground(new RippleDrawable(ColorStateList.valueOf(dark ? 811526868 : 403642154), gradientDrawable, null));
        text(view, i3);
        view.invalidate();
    }

    private static void text(View view, int i) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextColor(i);
            AppFonts.apply(textView);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                text(viewGroup.getChildAt(i2), i);
            }
        }
    }
}
