package com.bkggds.videotool;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class AddedSrt {
    private static final int SAVE_ADDED = 105;
    private static final WeakHashMap<MainActivity, State> states = new WeakHashMap<>();

    private static final class State {
        String language;
        String pendingName;
        String pendingText;
        TaskProgress task;
        String title;
        String video;

        private State() {
            this.title = "";
            this.language = "";
        }

        /* synthetic */ State(State state) {
            this();
        }
    }

    private static State state(MainActivity mainActivity) {
        String valueOf = String.valueOf(mainActivity.videoUi());
        State state = states.get(mainActivity);
        if (state != null && valueOf.equals(state.video)) {
            return state;
        }
        if (state != null && state.task != null) {
            state.task.cancel();
        }
        State state2 = new State(null);
        state2.video = valueOf;
        states.put(mainActivity, state2);
        return state2;
    }

    public static void release(MainActivity mainActivity) {
        State remove = states.remove(mainActivity);
        if (remove == null || remove.task == null) {
            return;
        }
        remove.task.cancel();
    }

    private static int dp(Context context, int i) {
        return Math.round(i * context.getResources().getDisplayMetrics().density);
    }

    private static TextView label(Context context, String str) {
        TextView textView = new TextView(context);
        textView.setText(Localization.tr(str));
        textView.setTextSize(14.0f);
        textView.setTextColor(-15788246);
        textView.setPadding(0, dp(context, 8), 0, dp(context, 6));
        return textView;
    }

    private static TextView button(Context context, String str, View.OnClickListener onClickListener) {
        TextView label = label(context, str);
        label.setMinHeight(dp(context, 48));
        label.setGravity(17);
        label.setPadding(dp(context, 10), dp(context, 8), dp(context, 10), dp(context, 8));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(-920071);
        gradientDrawable.setCornerRadius(dp(context, 10));
        label.setBackground(gradientDrawable);
        label.setOnClickListener(onClickListener);
        label.setFocusable(true);
        return label;
    }

    private static void add(LinearLayout linearLayout, View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(linearLayout.getContext(), 8);
        linearLayout.addView(view, layoutParams);
    }

    private static EditText field(Context context, String str, String str2) {
        EditText editText = new EditText(context);
        editText.setSingleLine(true);
        editText.setTextColor(-15788246);
        editText.setHintTextColor(-10193781);
        editText.setTextSize(15.0f);
        editText.setHint(Localization.tr(str));
        editText.setText(str2);
        editText.setMinHeight(dp(context, 48));
        return editText;
    }

    public static View screen(final MainActivity mainActivity) {
        final State state = state(mainActivity);
        final String videoBaseUi = mainActivity.videoBaseUi();
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(1);
        linearLayout.addView(label(mainActivity, "Add SRT"));
        linearLayout.addView(label(mainActivity, "Track name"));
        final EditText field = field(mainActivity, "Your SRT Language dialogue", state.title);
        add(linearLayout, field);
        linearLayout.addView(label(mainActivity, "Subtitle language code"));
        if (state.language.length() == 0) {
            state.language = Localization.current();
        }
        final Spinner spinner = new Spinner(mainActivity);
        spinner.setAdapter((SpinnerAdapter) new AppFonts.LabelAdapter(mainActivity, Localization.subtitleLabels()));
        spinner.setSelection(Localization.subtitleIndex(state.language));
        add(linearLayout, spinner);
        linearLayout.addView(label(mainActivity, "Output filename"));
        final TextView label = label(mainActivity, "");
        add(linearLayout, label);
        TextView label2 = label(mainActivity, "Video export embeds the track name and language. SRT-only export stores them in the filename. The original video is kept.");
        label2.setTextSize(12.0f);
        add(linearLayout, label2);
        final Runnable runnable = new Runnable() { // from class: com.bkggds.videotool.AddedSrt.1
            @Override // java.lang.Runnable
            public void run() {
                state.title = field.getText().toString();
                state.language = Localization.SUBTITLE_CODES[spinner.getSelectedItemPosition()];
                try {
                    label.setText(SrtTrackInfo.filename(videoBaseUi, state.title, state.language));
                } catch (IllegalArgumentException e) {
                    label.setText(Localization.tr("Enter a track name and a valid language code."));
                }
            }
        };
        TextWatcher textWatcher = new TextWatcher() { // from class: com.bkggds.videotool.AddedSrt.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                runnable.run();
            }
        };
        field.addTextChangedListener(textWatcher);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.bkggds.videotool.AddedSrt.9
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                runnable.run();
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        runnable.run();
        add(linearLayout, button(mainActivity, "Select SRT file", new View.OnClickListener() { // from class: com.bkggds.videotool.AddedSrt.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ImportGuard.require(mainActivity)) {
                    mainActivity.pickAddedUi();
                }
            }
        }));
        final EditText externalUi = mainActivity.externalUi();
        if (externalUi.getParent() instanceof ViewGroup) {
            ((ViewGroup) externalUi.getParent()).removeView(externalUi);
        }
        EditorTools.add(linearLayout, externalUi, new LinearLayout.LayoutParams(-1, dp(mainActivity, 280)));
        add(linearLayout, button(mainActivity, "Clear", EditorTools.confirmClearListener(externalUi, new View.OnClickListener() { // from class: com.bkggds.videotool.AddedSrt.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                externalUi.setText("");
            }
        })));
        add(linearLayout, button(mainActivity, "Embed SRT in MKV / MP4", new View.OnClickListener() { // from class: com.bkggds.videotool.AddedSrt.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AddedSrt.validate(mainActivity, field, AddedSrt.code(spinner), true) == null) {
                    return;
                }
                VideoMux.prepare(mainActivity, externalUi.getText().toString(), field.getText().toString(), AddedSrt.code(spinner));
            }
        }));
        add(linearLayout, button(mainActivity, "Save this added SRT", new View.OnClickListener() { // from class: com.bkggds.videotool.AddedSrt.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String validate = AddedSrt.validate(mainActivity, field, AddedSrt.code(spinner), true);
                if (validate == null) {
                    return;
                }
                state.pendingText = SrtText.normalize(externalUi.getText().toString());
                state.pendingName = validate;
                Intent intent = new Intent("android.intent.action.CREATE_DOCUMENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("application/x-subrip");
                intent.putExtra("android.intent.extra.TITLE", validate);
                intent.addFlags(2);
                try {
                    mainActivity.startActivityForResult(intent, AddedSrt.SAVE_ADDED);
                } catch (Exception e) {
                    state.pendingText = null;
                    state.pendingName = null;
                    Toast.makeText(mainActivity, Localization.tr("Could not open the file picker."), 1).show();
                }
            }
        }));
        add(linearLayout, button(mainActivity, "Use with extracted subtitles", new View.OnClickListener() { // from class: com.bkggds.videotool.AddedSrt.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String validate = AddedSrt.validate(mainActivity, field, AddedSrt.code(spinner), false);
                if (validate == null) {
                    return;
                }
                if (mainActivity.editorUi().getText().toString().trim().length() == 0) {
                    Toast.makeText(mainActivity, Localization.tr("Extract subtitles first, or save this SRT separately."), 1).show();
                } else {
                    mainActivity.fileNameUi().setText(validate);
                    mainActivity.navigateUi(2);
                }
            }

            /* renamed from: com.bkggds.videotool.AddedSrt$7$1, reason: invalid class name */
            class AnonymousClass1 implements Runnable {
                private final /* synthetic */ MainActivity val$a;
                private final /* synthetic */ String val$name;
                private final /* synthetic */ TaskProgress val$progress;
                private final /* synthetic */ State val$s;
                private final /* synthetic */ boolean val$saved;

                AnonymousClass1(TaskProgress taskProgress, MainActivity mainActivity, State state, boolean z, String str) {
                    this.val$progress = taskProgress;
                    this.val$a = mainActivity;
                    this.val$s = state;
                    this.val$saved = z;
                    this.val$name = str;
                }

                @Override // java.lang.Runnable
                public void run() {
                    this.val$progress.dismiss();
                    if (this.val$a.isDestroyed() || this.val$a.isFinishing() || AddedSrt.states.get(this.val$a) != this.val$s) {
                        return;
                    }
                    this.val$s.task = null;
                    this.val$a.statusUi(String.valueOf(Localization.tr(this.val$saved ? "Saved SRT" : "Save failed.")) + (this.val$saved ? ": " + this.val$name : ""), !this.val$saved);
                }
            }
        }));
        return linearLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    private static String code(Spinner spinner) {
        return Localization.SUBTITLE_CODES[spinner.getSelectedItemPosition()];
    }

    public static String validate(MainActivity mainActivity, EditText editText, String str, boolean z) {
        boolean z2;
        if (!ImportGuard.require(mainActivity)) {
            return null;
        }
        if (editText.getText().toString().trim().length() == 0) {
            editText.setError(Localization.tr("Enter a track name."));
            editText.requestFocus();
            return null;
        }
        if (SrtTrackInfo.language(str) == null) {
            Toast.makeText(mainActivity, Localization.tr("Use a language code such as vi/vie or en/eng."), 1).show();
            return null;
        }
        try {
            SrtText.normalize(mainActivity.externalUi().getText().toString());
            z2 = true;
        } catch (IllegalArgumentException e) {
            z2 = false;
        }
        if (!z2) {
            Toast.makeText(mainActivity, Localization.tr("Paste or load valid SRT text first."), 1).show();
            return null;
        }
        return SrtTrackInfo.filename(mainActivity.videoBaseUi(), editText.getText().toString(), str);
    }

    public static void loaded(MainActivity mainActivity, String str, String str2) {
        boolean z;
        String str3;
        try {
            str = SrtText.normalize(str);
            str3 = SrtText.convertedName(str2);
            z = str3 != null && !str3.equals(str2);
        } catch (IllegalArgumentException e) {
            str3 = str2;
            z = false;
        }
        mainActivity.externalUi().setText(str);
        if (z) {
            State state = state(mainActivity);
            if (state.title.trim().length() == 0) {
                state.title = str3.substring(0, str3.length() - 4);
            }
            Toast.makeText(mainActivity, Localization.tr("TXT recognized as SRT. Save a new .srt file or embed it in the video."), 1).show();
        }
        mainActivity.statusUi(String.valueOf(Localization.tr("Loaded: ")) + str3, false);
        mainActivity.navigateUi(3);
    }

    public static boolean onActivityResult(final MainActivity mainActivity, int i, int i2, Intent intent) {
        if (i != SAVE_ADDED) {
            return false;
        }
        final State state = state(mainActivity);
        final String str = state.pendingText;
        final String str2 = state.pendingName;
        state.pendingText = null;
        state.pendingName = null;
        if (i2 != -1 || intent == null || intent.getData() == null || str == null) {
            return true;
        }
        final Uri data = intent.getData();
        final TaskProgress taskProgress = new TaskProgress(mainActivity, "Saving SRT", 1);
        state.task = taskProgress;
        new Thread(new Runnable() { // from class: com.bkggds.videotool.AddedSrt.8
            @Override // java.lang.Runnable
            public void run() {
                boolean z = false;
                OutputStream openOutputStream = null;
                try {
                    openOutputStream = mainActivity.getContentResolver().openOutputStream(data, "wt");
                } catch (Exception e) {
                    openOutputStream = null;
                }
                if (openOutputStream == null) {
                    z = false;
                } else {
                try {
                    openOutputStream.write(str.getBytes("UTF-8"));
                    openOutputStream.close();
                    z = true;
                } catch (Throwable th) {
                    try {
                        openOutputStream.close();
                    } catch (Exception e2) {
                    }
                    z = false;
                }
                }
                final boolean ok = z;
                final TaskProgress progress = taskProgress;
                mainActivity.runOnUiThread(new Runnable() {
                    @Override // java.lang.Runnable
                    public void run() {
                        progress.dismiss();
                        if (mainActivity.isDestroyed() || mainActivity.isFinishing() || AddedSrt.states.get(mainActivity) != state) {
                            return;
                        }
                        state.task = null;
                        mainActivity.statusUi(String.valueOf(Localization.tr(ok ? "Saved SRT" : "Save failed.")) + (ok ? ": " + str2 : ""), !ok);
                    }
                });
            }
        }, "save-added-srt").start();
        return true;
    }
}
