package com.bkggds.videotool;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class AppTheme {
    private static boolean dark;
    private static final WeakHashMap<View, Style> originals = new WeakHashMap<>();

    private static final class Style {
        Drawable base;
        boolean hasText;
        int hint;
        Drawable last;
        int lastHint;
        int lastText;
        int text;

        private Style() {
        }

        /* synthetic */ Style(Style style) {
            this();
        }
    }

    public static void init(Context context) {
        dark = context.getSharedPreferences("bkggds_ui", 0).getBoolean("dark", false);
        AppFonts.init(context);
    }

    public static boolean dark() {
        return dark;
    }

    public static int iconColor(int i) {
        return palette(i, true);
    }

    public static void refresh(Activity activity) {
        window(activity);
        apply(activity.getWindow().getDecorView());
    }

    public static int dialogTheme() {
        return dark ? android.R.style.Theme_DeviceDefault_NoActionBar : android.R.style.Theme_DeviceDefault_Light_NoActionBar;
    }

    public static Context context(Context context) {
        return new ContextThemeWrapper(context, dark ? android.R.style.Theme_DeviceDefault : android.R.style.Theme_DeviceDefault_Light);
    }

    public static int palette(int i, boolean z) {
        return ThemePalette.map(dark, i, z);
    }

    private static Drawable copy(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable.ConstantState constantState = drawable.getConstantState();
        return constantState == null ? drawable : constantState.newDrawable().mutate();
    }

    private static void tint(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        if (!(drawable instanceof ColorDrawable)) {
            if (drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                ColorStateList color = gradientDrawable.getColor();
                if (color != null) {
                    gradientDrawable.setColor(palette(color.getDefaultColor(), false));
                }
                if (dark) {
                    gradientDrawable.setStroke(1, -13418155);
                    return;
                }
                return;
            }
            if (drawable instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                for (int i = 0; i < layerDrawable.getNumberOfLayers(); i++) {
                    tint(layerDrawable.getDrawable(i));
                }
                return;
            }
            return;
        }
        ColorDrawable colorDrawable = (ColorDrawable) drawable;
        colorDrawable.setColor(palette(colorDrawable.getColor(), false));
    }

    public static void apply(View view) {
        if (view == null || "previewControls".equals(view.getTag())) {
            return;
        }
        if ("sidebarItem".equals(view.getTag())) {
            SidebarStyle.apply(view);
            return;
        }
        if (view instanceof Spinner) {
            AppFonts.tree(view);
            return;
        }
        Style style = originals.get(view);
        if (style == null) {
            style = new Style(null);
            originals.put(view, style);
        }
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (!"fontPreview".equals(textView.getTag())) {
                AppFonts.apply(textView);
            }
            int currentTextColor = textView.getCurrentTextColor();
            int currentHintTextColor = textView.getCurrentHintTextColor();
            if (!style.hasText || currentTextColor != style.lastText) {
                style.text = currentTextColor;
            }
            if (!style.hasText || currentHintTextColor != style.lastHint) {
                style.hint = currentHintTextColor;
            }
            style.hasText = true;
            style.lastText = palette(style.text, true);
            style.lastHint = palette(style.hint, true);
            textView.setTextColor(style.lastText);
            textView.setHintTextColor(style.lastHint);
        }
        Drawable background = view.getBackground();
        if (background != style.last) {
            style.base = copy(background);
        }
        Drawable copy = copy(style.base);
        tint(copy);
        if (copy != background) {
            view.setBackground(copy);
        }
        style.last = view.getBackground();
        view.invalidate();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                apply(viewGroup.getChildAt(i));
            }
        }
    }

    public static void window(Activity activity) {
        init(activity);
        window(activity.getWindow());
    }

    public static void window(Window window) {
        if (window == null) {
            return;
        }
        int i = -15788246;
        window.setBackgroundDrawable(new ColorDrawable(dark ? -15788246 : -1));
        window.setStatusBarColor(dark ? -15788246 : -1);
        if (!dark && Build.VERSION.SDK_INT >= 26) {
            i = -1;
        }
        window.setNavigationBarColor(i);
        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        int i2 = dark ? systemUiVisibility & (-8209) : systemUiVisibility | 8208;
        if (Build.VERSION.SDK_INT < 26) {
            i2 &= -17;
        }
        window.getDecorView().setSystemUiVisibility(i2);
        if (Build.VERSION.SDK_INT >= 29) {
            window.setNavigationBarContrastEnforced(false);
            window.getDecorView().setForceDarkAllowed(false);
        }
    }
}
