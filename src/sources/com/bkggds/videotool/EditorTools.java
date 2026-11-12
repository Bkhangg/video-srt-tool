package com.bkggds.videotool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
public final class EditorTools {
    private static int dp(Context context, int i) {
        return Math.round(i * context.getResources().getDisplayMetrics().density);
    }

    private static TextView action(Context context, String str, View.OnClickListener onClickListener) {
        TextView textView = new TextView(context);
        textView.setText(Localization.tr(str));
        textView.setTextSize(12.0f);
        textView.setTextColor(-15788246);
        textView.setGravity(17);
        textView.setMinHeight(dp(context, 48));
        textView.setPadding(dp(context, 8), dp(context, 6), dp(context, 8), dp(context, 6));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(-920071);
        gradientDrawable.setCornerRadius(dp(context, 8));
        textView.setBackground(new RippleDrawable(ColorStateList.valueOf(537859882), gradientDrawable, null));
        textView.setOnClickListener(onClickListener);
        textView.setFocusable(true);
        return textView;
    }

    public static void add(LinearLayout linearLayout, final EditText editText, ViewGroup.LayoutParams layoutParams) {
        Context context = editText.getContext();
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        addAction(linearLayout2, action(context, "Top", new View.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditorTools.jump(editText, false);
            }
        }));
        addAction(linearLayout2, action(context, "End", new View.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditorTools.jump(editText, true);
            }
        }));
        addAction(linearLayout2, action(context, "Full screen", new View.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditorTools.expand(editText);
            }
        }));
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams2.bottomMargin = dp(context, 8);
        linearLayout.addView(linearLayout2, layoutParams2);
        linearLayout.addView(editText, layoutParams);
    }

    private static void addAction(LinearLayout linearLayout, TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.setMargins(0, 0, dp(linearLayout.getContext(), 4), 0);
        linearLayout.addView(textView, layoutParams);
    }

    public static void jump(final EditText editText, final boolean z) {
        editText.post(new Runnable() { // from class: com.bkggds.videotool.EditorTools.4
            @Override // java.lang.Runnable
            public void run() {
                editText.setSelection(z ? editText.length() : 0);
                Layout layout = editText.getLayout();
                if (layout != null) {
                    editText.scrollTo(0, z ? Math.max(0, layout.getHeight() - ((editText.getHeight() - editText.getCompoundPaddingTop()) - editText.getCompoundPaddingBottom())) : 0);
                }
                editText.announceForAccessibility(Localization.tr(z ? "End of subtitles" : "Start of subtitles"));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void expand(final EditText editText) {
        if (editText.getContext() instanceof Activity) {
            Activity activity = (Activity) editText.getContext();
            final Dialog dialog = new Dialog(activity, AppTheme.dialogTheme());
            final SubtitleEditor subtitleEditor = new SubtitleEditor(activity);
            subtitleEditor.setTag("fullscreen");
            subtitleEditor.setInputType(655361);
            subtitleEditor.setGravity(8388659);
            subtitleEditor.setTypeface(Typeface.MONOSPACE);
            subtitleEditor.setTextSize(14.0f);
            subtitleEditor.setTextColor(-15788246);
            subtitleEditor.setBackgroundColor(-1);
            subtitleEditor.setPadding(dp(activity, 16), dp(activity, 12), dp(activity, 16), dp(activity, 32));
            subtitleEditor.setText(editText.getText());
            subtitleEditor.setSelection(EditorScrollPolicy.clampSelection(editText.getSelectionStart(), subtitleEditor.length()));
            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(1);
            linearLayout.setBackgroundColor(-1);
            linearLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.bkggds.videotool.EditorTools.5
                @Override // android.view.View.OnApplyWindowInsetsListener
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return InsetsUX.apply(view, windowInsets);
                }
            });
            LinearLayout linearLayout2 = new LinearLayout(activity);
            addAction(linearLayout2, action(activity, "Top", new View.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.6
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    EditorTools.jump(subtitleEditor, false);
                }
            }));
            addAction(linearLayout2, action(activity, "End", new View.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.7
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    EditorTools.jump(subtitleEditor, true);
                }
            }));
            addAction(linearLayout2, action(activity, "Done", new View.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.8
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    dialog.dismiss();
                }
            }));
            linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, -2));
            linearLayout.addView(subtitleEditor, new LinearLayout.LayoutParams(-1, 0, 1.0f));
            linearLayout.setFocusableInTouchMode(true);
            linearLayout.requestFocus();
            AppTheme.apply(linearLayout);
            dialog.setContentView(linearLayout);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.bkggds.videotool.EditorTools.9
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    editText.setText(subtitleEditor.getText());
                    editText.setSelection(EditorScrollPolicy.clampSelection(subtitleEditor.getSelectionStart(), editText.length()));
                    EditText editText2 = editText;
                    final EditText editText3 = editText;
                    final SubtitleEditor subtitleEditor2 = subtitleEditor;
                    editText2.post(new Runnable() { // from class: com.bkggds.videotool.EditorTools.9.1
                        @Override // java.lang.Runnable
                        public void run() {
                            editText3.scrollTo(0, subtitleEditor2.getScrollY());
                        }
                    });
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(-1, -1);
                window.setSoftInputMode(19);
                window.setStatusBarColor(-1);
                window.setNavigationBarColor(-1);
                window.getDecorView().setSystemUiVisibility(10000);
                if (Build.VERSION.SDK_INT >= 29) {
                    window.setNavigationBarContrastEnforced(false);
                    window.getDecorView().setForceDarkAllowed(false);
                }
                AppTheme.window(window);
                linearLayout.requestApplyInsets();
            }
        }
    }

    public static void configureButton(TextView textView) {
        textView.setMinHeight(dp(textView.getContext(), 48));
        textView.setFocusable(true);
        textView.setPadding(textView.getPaddingLeft(), dp(textView.getContext(), 8), textView.getPaddingRight(), dp(textView.getContext(), 8));
        ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = -2;
            textView.setLayoutParams(layoutParams);
        }
    }

    public static View.OnClickListener confirmClearListener(final EditText editText, final View.OnClickListener onClickListener) {
        return new View.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.10
            @Override // android.view.View.OnClickListener
            public void onClick(final View view) {
                if (editText.length() == 0) {
                    onClickListener.onClick(view);
                    return;
                }
                AlertDialog.Builder negativeButton = new AlertDialog.Builder(AppTheme.context(editText.getContext())).setTitle(Localization.tr("Clear subtitles?")).setMessage(Localization.tr("This will remove the text in this editor.")).setNegativeButton(Localization.tr("Cancel"), (DialogInterface.OnClickListener) null);
                String tr = Localization.tr("Clear");
                final View.OnClickListener onClickListener2 = onClickListener;
                AppFonts.decorate(negativeButton.setPositiveButton(tr, new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.10.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onClickListener2.onClick(view);
                    }
                }).show());
            }
        };
    }

    /* renamed from: com.bkggds.videotool.EditorTools$12, reason: invalid class name */
    class AnonymousClass12 implements View.OnClickListener {
        private final /* synthetic */ View.OnClickListener val$clear;
        private final /* synthetic */ EditText val$editor;

        AnonymousClass12(EditText editText, View.OnClickListener onClickListener) {
            this.val$editor = editText;
            this.val$clear = onClickListener;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(final View view) {
            if (this.val$editor.length() == 0) {
                this.val$clear.onClick(view);
                return;
            }
            AlertDialog.Builder negativeButton = new AlertDialog.Builder(AppTheme.context(this.val$editor.getContext())).setTitle(Localization.tr("Clear subtitles?")).setMessage(Localization.tr("This will remove the text in this editor.")).setNegativeButton(Localization.tr("Cancel"), (DialogInterface.OnClickListener) null);
            String tr = Localization.tr("Clear");
            final View.OnClickListener onClickListener = this.val$clear;
            negativeButton.setPositiveButton(tr, new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.EditorTools.12.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    onClickListener.onClick(view);
                }
            }).show();
        }
    }

    public static void configureFilename(final EditText editText) {
        editText.setImeOptions(6);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.bkggds.videotool.EditorTools.11

            /* renamed from: com.bkggds.videotool.EditorTools$11$1, reason: invalid class name */
            class AnonymousClass1 implements Runnable {
                private final /* synthetic */ SubtitleEditor val$editor;
                private final /* synthetic */ EditText val$original;

                AnonymousClass1(EditText editText, SubtitleEditor subtitleEditor) {
                    this.val$original = editText;
                    this.val$editor = subtitleEditor;
                }

                @Override // java.lang.Runnable
                public void run() {
                    this.val$original.scrollTo(0, this.val$editor.getScrollY());
                }
            }

            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService("input_method");
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                editText.clearFocus();
                return true;
            }
        });
    }

    /* renamed from: com.bkggds.videotool.EditorTools$13, reason: invalid class name */
    class AnonymousClass13 implements TextView.OnEditorActionListener {
        private final /* synthetic */ EditText val$v;

        AnonymousClass13(EditText editText) {
            this.val$v = editText;
        }

        @Override // android.widget.TextView.OnEditorActionListener
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            InputMethodManager inputMethodManager = (InputMethodManager) this.val$v.getContext().getSystemService("input_method");
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(this.val$v.getWindowToken(), 0);
            }
            this.val$v.clearFocus();
            return true;
        }
    }
}
