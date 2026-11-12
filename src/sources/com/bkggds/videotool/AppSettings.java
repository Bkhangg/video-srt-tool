package com.bkggds.videotool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.bkggds.videotool.AppFonts;

/* loaded from: classes.dex */
public final class AppSettings {
    private static int dp(Context context, int i) {
        return Math.round(i * context.getResources().getDisplayMetrics().density);
    }

    private static TextView label(Context context, String str) {
        TextView textView = new TextView(context);
        textView.setText(Localization.tr(str));
        textView.setTextSize(14.0f);
        textView.setTextColor(-15788246);
        textView.setPadding(0, dp(context, 14), 0, dp(context, 8));
        return textView;
    }

    public static void sidebar(final Activity activity, LinearLayout linearLayout) {
        TextView label = label(activity, "Settings");
        label.setTypeface(null, 1);
        label.setMinHeight(dp(activity, 56));
        label.setGravity(16);
        label.setPadding(dp(activity, 12), dp(activity, 8), dp(activity, 12), dp(activity, 8));
        label.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.AppSettings.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((MainActivity) activity).openSettings();
            }
        });
        label.setFocusable(true);
        SidebarStyle.prepare(label);
        linearLayout.addView(label);
    }

    public static void compactNav(LinearLayout linearLayout) {
        while (linearLayout.getChildCount() > 1) {
            linearLayout.removeViewAt(1);
        }
        linearLayout.setMinimumHeight(dp(linearLayout.getContext(), 48));
        linearLayout.setGravity(16);
        SidebarStyle.prepare(linearLayout);
    }

    public static void makeScrollable(LinearLayout linearLayout) {
        if (linearLayout.getChildCount() < 2) {
            return;
        }
        View childAt = linearLayout.getChildAt(linearLayout.getChildCount() - 1);
        View childAt2 = linearLayout.getChildAt(linearLayout.getChildCount() - 2);
        LinearLayout linearLayout2 = new LinearLayout(linearLayout.getContext());
        linearLayout2.setOrientation(1);
        while (linearLayout.getChildCount() > 2) {
            View childAt3 = linearLayout.getChildAt(0);
            linearLayout.removeViewAt(0);
            linearLayout2.addView(childAt3);
        }
        linearLayout.removeAllViews();
        ScrollView scrollView = new ScrollView(linearLayout.getContext());
        scrollView.setFillViewport(false);
        scrollView.addView(linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        linearLayout.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        linearLayout.addView(childAt2);
        linearLayout.addView(childAt);
    }

    private static String previewSample() {
        String str = Localization.current();
        if ("vi".equals(str)) {
            return "Trích xuất và chỉnh sửa phụ đề. 0123456789";
        }
        if ("zh".equals(str)) {
            return "提取、合并并保存字幕。0123456789";
        }
        if ("ja".equals(str)) {
            return "字幕の抽出・結合・保存。0123456789";
        }
        if ("ko".equals(str)) {
            return "자막 추출, 병합 및 저장. 0123456789";
        }
        return "The quick brown fox jumps over the lazy dog. 0123456789";
    }

    public static void footer(Activity activity, LinearLayout linearLayout) {
        TextView label = label(activity, "Do with love by BKggDs");
        label.setTag("appFooter");
        label.setGravity(17);
        label.setTextSize(12.0f);
        label.setTextColor(-10193781);
        label.setPadding(dp(activity, 8), dp(activity, 10), dp(activity, 8), dp(activity, 12));
        linearLayout.addView(label, new LinearLayout.LayoutParams(-1, -2));
    }

    public static void show(final Activity activity) {
        final Context context = AppTheme.context(activity);
        final SharedPreferences sharedPreferences = activity.getSharedPreferences("bkggds_ui", 0);
        ScrollView scrollView = new ScrollView(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(context, 20), dp(context, 4), dp(context, 20), dp(context, 20));
        scrollView.addView(linearLayout);
        linearLayout.addView(label(context, "Language"));
        final Spinner spinner = new Spinner(context);
        spinner.setAdapter((SpinnerAdapter) new AppFonts.LabelAdapter(context, Localization.languageNames()));
        spinner.setSelection(Localization.index());
        linearLayout.addView(spinner, new LinearLayout.LayoutParams(-1, dp(context, 52)));
        linearLayout.addView(label(context, "Appearance"));
        final Spinner spinner2 = new Spinner(context);
        spinner2.setAdapter((SpinnerAdapter) new AppFonts.LabelAdapter(context, new String[]{Localization.tr("Light"), Localization.tr("Dark")}));
        spinner2.setSelection(sharedPreferences.getBoolean("dark", false) ? 1 : 0);
        linearLayout.addView(spinner2, new LinearLayout.LayoutParams(-1, dp(context, 52)));
        linearLayout.addView(label(context, "Font"));
        final Spinner spinner3 = new Spinner(context);
        String[] strArr2 = (String[]) FontCatalog.NAMES.clone();
        strArr2[0] = Localization.tr("Default font");
        spinner3.setAdapter((SpinnerAdapter) new AppFonts.FontAdapter(context, strArr2));
        spinner3.setSelection(FontCatalog.index(sharedPreferences.getString("font_family", "system")));
        linearLayout.addView(spinner3, new LinearLayout.LayoutParams(-1, dp(context, 52)));
        linearLayout.addView(label(context, "Font preview"));
        final TextView label = label(context, previewSample());
        label.setTag("fontPreview");
        label.setTextSize(16.0f);
        label.setMinHeight(dp(context, 96));
        label.setTypeface(AppFonts.typeface(context, FontCatalog.IDS[spinner3.getSelectedItemPosition()], 0));
        linearLayout.addView(label, new LinearLayout.LayoutParams(-1, -2));
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.bkggds.videotool.AppSettings.2
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                label.setTypeface(AppFonts.typeface(context, FontCatalog.IDS[i], 0));
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        AlertDialog create = new AlertDialog.Builder(context).setTitle(Localization.tr("Settings")).setView(scrollView).setNeutralButton(Localization.tr("Help"), new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.AppSettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ((MainActivity) activity).navigateUi(5);
            }
        }).setNegativeButton(Localization.tr("Cancel"), (DialogInterface.OnClickListener) null).setPositiveButton(Localization.tr("Apply"), new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.AppSettings.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Localization.setLanguage(activity, Localization.CODES[spinner.getSelectedItemPosition()]);
                sharedPreferences.edit().putBoolean("dark", spinner2.getSelectedItemPosition() == 1).putString("font_family", FontCatalog.IDS[spinner3.getSelectedItemPosition()]).apply();
                ((MainActivity) activity).refreshPreferences();
            }
        }).create();
        AppTheme.apply(linearLayout);
        create.show();
        AppFonts.tree(create.getWindow().getDecorView());
    }
}
