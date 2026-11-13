package com.bkggds.videotool;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class AppFonts {
    private static String selected = "system";
    private static final Map<String, Typeface> loaded = new HashMap();
    private static final WeakHashMap<TextView, Style> originals = new WeakHashMap<>();

    private static final class Style {
        Typeface last;
        Typeface original;

        private Style() {
        }

        /* synthetic */ Style(Style style) {
            this();
        }
    }

    public static void init(Context context) {
        selected = FontCatalog.valid(context.getSharedPreferences("bkggds_ui", 0).getString("font_family", "system"));
    }

    public static Typeface typeface(Context context, String str, int i) {
        if (FontCatalog.valid(str).equals("system") || Localization.isCjk()) {
            return Typeface.create(Typeface.DEFAULT, i);
        }
        return load(context, str, i);
    }

    /* Preview follows the language picked in the settings dropdown, so a CJK sample still gets
       glyphs: the bundled Latin fonts have none, the system fallback (Noto Sans CJK) does. */
    public static Typeface previewTypeface(Context context, String str, String str2) {
        Typeface load = load(context, str, 0);
        String probe = "zh".equals(str2) ? "中" : ("ja".equals(str2) ? "あ" : ("ko".equals(str2) ? "한" : null));
        if (probe != null && !supports(load, probe)) {
            return Typeface.create(Typeface.DEFAULT, 0);
        }
        return load;
    }

    private static boolean supports(Typeface typeface, String str) {
        Paint paint = new Paint();
        paint.setTypeface(typeface);
        return paint.hasGlyph(str);
    }

    private static Typeface load(Context context, String str, int i) {
        Typeface createFromAsset;
        String valid = FontCatalog.valid(str);
        if (valid.equals("system")) {
            return Typeface.create(Typeface.DEFAULT, i);
        }
        String str2 = String.valueOf(valid) + ":" + i;
        Typeface typeface = loaded.get(str2);
        if (typeface != null) {
            return typeface;
        }
        int i2 = (i & 1) != 0 ? 700 : 400;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                createFromAsset = new Typeface.Builder(context.getAssets(), "fonts/" + valid + ".ttf").setWeight(i2).setFontVariationSettings("'wght' " + i2).setFallback("sans-serif").build();
                if ((i & 2) != 0) {
                    createFromAsset = Typeface.create(createFromAsset, 2);
                }
            } else {
                createFromAsset = Typeface.createFromAsset(context.getAssets(), "fonts-compat/" + valid + (i2 >= 700 ? "-bold.ttf" : "-regular.ttf"));
                if ((i & 2) != 0) {
                    createFromAsset = Typeface.create(createFromAsset, i);
                }
            }
            loaded.put(str2, createFromAsset);
            return createFromAsset;
        } catch (RuntimeException e) {
            return Typeface.create(Typeface.DEFAULT, i);
        }
    }

    public static void apply(TextView textView) {
        if ("fontPreview".equals(textView.getTag()) || "fontOption".equals(textView.getTag())) {
            return;
        }
        Style style = originals.get(textView);
        Typeface typeface = textView.getTypeface();
        if (style == null) {
            style = new Style(null);
            style.original = typeface;
            originals.put(textView, style);
        } else if (typeface != style.last) {
            style.original = typeface;
        }
        int style2 = style.original == null ? 0 : style.original.getStyle();
        if (style.original != null && style.original.equals(Typeface.create("sans-serif-medium", 0))) {
            style2 |= 1;
        }
        if (Build.VERSION.SDK_INT >= 28 && style.original != null && style.original.getWeight() >= 600) {
            style2 |= 1;
        }
        Typeface typeface2 = selected.equals("system") ? style.original : typeface(textView.getContext(), selected, style2);
        if (typeface != typeface2) {
            textView.setTypeface(typeface2);
        }
        style.last = textView.getTypeface();
    }

    public static void tree(View view) {
        if (view instanceof TextView) {
            apply((TextView) view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                tree(viewGroup.getChildAt(i));
            }
        }
    }

    public static AlertDialog decorate(AlertDialog alertDialog) {
        if (alertDialog.getWindow() != null) {
            tree(alertDialog.getWindow().getDecorView());
        }
        return alertDialog;
    }

    public static class LabelAdapter extends ArrayAdapter<String> {
        public LabelAdapter(Context context, String[] strArr) {
            super(context, android.R.layout.simple_spinner_dropdown_item, strArr);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            AppFonts.tree(view2);
            return view2;
        }

        @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            View dropDownView = super.getDropDownView(i, view, viewGroup);
            AppFonts.tree(dropDownView);
            return dropDownView;
        }
    }

    public static final class FontAdapter extends ArrayAdapter<String> {
        public FontAdapter(Context context, String[] strArr) {
            super(context, android.R.layout.simple_spinner_dropdown_item, strArr);
        }

        private View decorate(View view, int i) {
            if (view instanceof TextView) {
                view.setTag("fontOption");
                ((TextView) view).setTypeface(AppFonts.typeface(getContext(), FontCatalog.IDS[i], 0));
            }
            return view;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            return decorate(super.getView(i, view, viewGroup), i);
        }

        @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            return decorate(super.getDropDownView(i, view, viewGroup), i);
        }
    }
}
