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

    /* Preview text for the language selected in the dropdown, not the applied UI language. */
    private static String previewSample(String str) {
        if ("vi".equals(str)) {
            return "Xem trước phông chữ tiếng Việt. Con cáo nâu nhanh nhẹn nhảy qua con chó lười biếng. 0123456789";
        }
        if ("zh".equals(str)) {
            return "中文字体预览：敏捷的棕色狐狸跳过了懒狗。0123456789";
        }
        if ("ja".equals(str)) {
            return "日本語のフォントプレビュー。素早い茶色の狐が怠惰な犬を飛び越えます。0123456789";
        }
        if ("ko".equals(str)) {
            return "한국어 글꼴 미리보기. 빠른 갈색 여우가 게으른 개를 뛰어넘습니다. 0123456789";
        }
        return "The quick brown fox jumps over the lazy dog. 0123456789";
    }

    private static void updatePreview(Context context, TextView textView, String str, String str2) {
        textView.setText(previewSample(str2));
        textView.setTypeface(AppFonts.previewTypeface(context, str, str2));
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
        final String appliedLanguage = Localization.current();
        final String[] selectedLanguage = {appliedLanguage};
        final Spinner spinner = new Spinner(context);
        spinner.setAdapter((SpinnerAdapter) new AppFonts.LabelAdapter(context, Localization.languageOptions()));
        spinner.setSelection(Localization.index());
        spinner.setContentDescription(Localization.tr("Language"));
        linearLayout.addView(spinner, new LinearLayout.LayoutParams(-1, dp(context, 52)));
        linearLayout.addView(label(context, "Appearance"));
        final Spinner spinner2 = new Spinner(context);
        spinner2.setAdapter((SpinnerAdapter) new AppFonts.LabelAdapter(context, new String[]{Localization.tr("Light"), Localization.tr("Dark")}));
        spinner2.setSelection(sharedPreferences.getBoolean("dark", false) ? 1 : 0);
        spinner2.setContentDescription(Localization.tr("Appearance"));
        linearLayout.addView(spinner2, new LinearLayout.LayoutParams(-1, dp(context, 52)));
        linearLayout.addView(label(context, "Font"));
        final Spinner spinner3 = new Spinner(context);
        String[] strArr2 = (String[]) FontCatalog.NAMES.clone();
        strArr2[0] = Localization.tr("Default font");
        spinner3.setAdapter((SpinnerAdapter) new AppFonts.FontAdapter(context, strArr2));
        spinner3.setSelection(FontCatalog.index(sharedPreferences.getString("font_family", "system")));
        spinner3.setContentDescription(Localization.tr("Font"));
        linearLayout.addView(spinner3, new LinearLayout.LayoutParams(-1, dp(context, 52)));
        linearLayout.addView(label(context, "Font preview"));
        final TextView preview = label(context, "");
        preview.setTag("fontPreview");
        preview.setTextSize(16.0f);
        preview.setMinHeight(dp(context, 96));
        linearLayout.addView(preview, new LinearLayout.LayoutParams(-1, -2));
        updatePreview(context, preview, FontCatalog.IDS[spinner3.getSelectedItemPosition()], selectedLanguage[0]);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.bkggds.videotool.AppSettings.2
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                selectedLanguage[0] = Localization.CODES[i];
                updatePreview(context, preview, FontCatalog.IDS[spinner3.getSelectedItemPosition()], selectedLanguage[0]);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.bkggds.videotool.AppSettings.3
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                updatePreview(context, preview, FontCatalog.IDS[i], selectedLanguage[0]);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        AlertDialog create = new AlertDialog.Builder(context).setTitle(Localization.tr("Settings")).setView(scrollView).setNeutralButton(Localization.tr("Help"), new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.AppSettings.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ((MainActivity) activity).navigateUi(5);
            }
        }).setNegativeButton(Localization.tr("Cancel"), new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.AppSettings.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedLanguage[0] = appliedLanguage;
            }
        }).setPositiveButton(Localization.tr("Apply"), new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.AppSettings.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Localization.setLanguage(activity, selectedLanguage[0]);
                sharedPreferences.edit().putBoolean("dark", spinner2.getSelectedItemPosition() == 1).putString("font_family", FontCatalog.IDS[spinner3.getSelectedItemPosition()]).apply();
                ((MainActivity) activity).refreshPreferences();
            }
        }).create();
        AppTheme.apply(linearLayout);
        create.show();
        AppFonts.tree(create.getWindow().getDecorView());
    }
}
