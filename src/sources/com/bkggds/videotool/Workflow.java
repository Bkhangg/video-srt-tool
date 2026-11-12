package com.bkggds.videotool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.bkggds.videotool.AppFonts;
import com.bkggds.videotool.MainActivity;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class Workflow {
    private static final int EXPORT_FOLDER = 104;
    private static final WeakHashMap<MainActivity, State> states = new WeakHashMap<>();

    private static final class Result {
        String name;
        String srt;
        final MainActivity.TrackOption track;

        Result(MainActivity.TrackOption trackOption, String str, String str2) {
            this.track = trackOption;
            this.srt = str;
            this.name = str2;
        }
    }

    private static final class State {
        int active;
        List<Result> pending;
        List<Result> results;
        final TrackSelection selection;
        TaskProgress task;
        int trackScroll;
        String tracksSignature;
        String video;

        private State() {
            this.video = "";
            this.selection = new TrackSelection();
            this.results = new ArrayList();
            this.tracksSignature = "";
        }

        /* synthetic */ State(State state) {
            this();
        }
    }

    private static State state(MainActivity mainActivity) {
        String valueOf = String.valueOf(mainActivity.videoUi());
        State state = states.get(mainActivity);
        if (state == null || !state.video.equals(valueOf)) {
            if (state != null && state.task != null) {
                state.task.cancel();
            }
            State state2 = new State(null);
            state2.video = valueOf;
            states.put(mainActivity, state2);
            return state2;
        }
        return state;
    }

    public static void reset(MainActivity mainActivity) {
        State remove = states.remove(mainActivity);
        if (remove != null && remove.task != null) {
            remove.task.cancel();
        }
        mainActivity.setExtractingUi(false);
        mainActivity.setScanningUi(false);
    }

    public static void release(MainActivity mainActivity) {
        State remove = states.remove(mainActivity);
        if (remove == null || remove.task == null) {
            return;
        }
        remove.task.cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean alive(MainActivity mainActivity) {
        return (mainActivity.isFinishing() || mainActivity.isDestroyed()) ? false : true;
    }

    private static int dp(Context context, int i) {
        return Math.round(i * context.getResources().getDisplayMetrics().density);
    }

    private static LinearLayout column(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        return linearLayout;
    }

    private static TextView text(Context context, String str, int i, boolean z) {
        TextView textView = new TextView(context);
        textView.setText(Localization.tr(str));
        textView.setTextSize(i);
        textView.setTextColor(-15788246);
        if (z) {
            textView.setTypeface(null, 1);
        }
        textView.setPadding(0, dp(context, 5), 0, dp(context, 5));
        return textView;
    }

    private static TextView button(Context context, String str, View.OnClickListener onClickListener) {
        TextView text = text(context, str, 14, true);
        text.setGravity(17);
        text.setMinHeight(dp(context, 48));
        text.setPadding(dp(context, 12), dp(context, 8), dp(context, 12), dp(context, 8));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(-920071);
        gradientDrawable.setCornerRadius(dp(context, 10));
        text.setBackground(gradientDrawable);
        text.setOnClickListener(onClickListener);
        text.setFocusable(true);
        return text;
    }

    private static void addButton(LinearLayout linearLayout, TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = dp(linearLayout.getContext(), 8);
        linearLayout.addView(textView, layoutParams);
    }

    private static void detach(View view) {
        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static void scan(final MainActivity mainActivity) {
        if (!ImportGuard.require(mainActivity)) {
            mainActivity.navigateUi(0);
            return;
        }
        if (mainActivity.scanningUi()) {
            mainActivity.navigateUi(1);
            return;
        }
        final State state = state(mainActivity);
        final Uri videoUi = mainActivity.videoUi();
        mainActivity.setScanningUi(true);
        mainActivity.navigateUi(1);
        new Thread(new Runnable() { // from class: com.bkggds.videotool.Workflow.1
            @Override // java.lang.Runnable
            public void run() {
                List list;
                try {
                    list = mainActivity.listTracksUi(videoUi);
                } catch (Exception e) {
                    list = null;
                }
                final List list2 = list;
                MainActivity mainActivity2 = mainActivity;
                final MainActivity mainActivity3 = mainActivity;
                final State state2 = state;
                final Uri uri = videoUi;
                mainActivity2.runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.Workflow.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!Workflow.alive(mainActivity3) || Workflow.states.get(mainActivity3) != state2 || !uri.equals(mainActivity3.videoUi())) {
                            return;
                        }
                        mainActivity3.setScanningUi(false);
                        if (list2 == null) {
                            mainActivity3.statusUi(Localization.tr("Could not read subtitle tracks. Please try again."), true);
                        } else {
                            mainActivity3.tracksUi().clear();
                            mainActivity3.tracksUi().addAll(list2);
                            Workflow.sync(state2, mainActivity3.tracksUi());
                            mainActivity3.statusUi(String.valueOf(Localization.tr("Subtitle tracks found")) + ": " + list2.size(), false);
                        }
                        if (mainActivity3.currentPageUi() == 1) {
                            mainActivity3.refreshUi();
                        }
                    }
                });
            }
        }, "read-subtitle-tracks").start();
    }

    public static void continueFromImport(MainActivity mainActivity) {
        if (!ImportGuard.require(mainActivity)) {
            mainActivity.navigateUi(0);
        } else if (mainActivity.scanningUi()) {
            mainActivity.navigateUi(1);
        } else {
            mainActivity.scanUi();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sync(State state, List<MainActivity.TrackOption> list) {
        boolean[] zArr = new boolean[list.size()];
        StringBuilder sb = new StringBuilder(state.video);
        for (int i = 0; i < list.size(); i++) {
            MainActivity.TrackOption trackOption = list.get(i);
            zArr[i] = !trackOption.bitmap;
            sb.append('|').append(trackOption.trackNumber).append(':').append(trackOption.lang).append(':').append(trackOption.name).append(':').append(trackOption.bitmap);
        }
        String sb2 = sb.toString();
        if (!state.tracksSignature.equals(sb2)) {
            state.trackScroll = 0;
            state.tracksSignature = sb2;
        }
        state.selection.sync(sb2, zArr);
    }

    public static View extractScreen(final MainActivity mainActivity) {
        final State state = state(mainActivity);
        final List tracksUi = mainActivity.tracksUi();
        sync(state, tracksUi);
        LinearLayout column = column(mainActivity);
        column.addView(text(mainActivity, "Extract embedded subtitles", 18, true));
        boolean z = false;
        column.addView(text(mainActivity, "Check one or more text tracks to extract. Each track remains a separate SRT.", 13, false));
        if (mainActivity.videoUi() == null) {
            addButton(column, button(mainActivity, "Import video", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    mainActivity.navigateUi(0);
                }
            }));
            return column;
        }
        if (mainActivity.scanningUi()) {
            column.addView(new ProgressBar(mainActivity), new LinearLayout.LayoutParams(dp(mainActivity, 36), dp(mainActivity, 36)));
            column.addView(text(mainActivity, "Reading subtitle tracks...", 14, false));
            return column;
        }
        if (tracksUi.isEmpty()) {
            column.addView(text(mainActivity, "No subtitle tracks found in this video.", 14, false));
            addButton(column, button(mainActivity, "Read subtitle tracks", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    mainActivity.scanUi();
                }
            }));
            return column;
        }
        final TextView text = text(mainActivity, "", 13, false);
        column.addView(text);
        final TextView button = button(mainActivity, "Extract selected tracks", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Workflow.extract(mainActivity, state);
            }
        });
        final Runnable runnable = new Runnable() { // from class: com.bkggds.videotool.Workflow.5
            @Override // java.lang.Runnable
            public void run() {
                text.setText(String.valueOf(Localization.tr("Selected tracks")) + ": " + state.selection.size() + " / " + tracksUi.size());
                button.setText(String.valueOf(Localization.tr("Extract selected tracks")) + " (" + state.selection.size() + ")");
                button.setEnabled(state.selection.size() > 0 && !mainActivity.extractingUi());
                button.setAlpha(button.isEnabled() ? 1.0f : 0.45f);
            }
        };
        final ArrayList arrayList = new ArrayList();
        LinearLayout column2 = column(mainActivity);
        column2.setOrientation(0);
        TextView button2 = button(mainActivity, "Select all", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                state.selection.all();
                for (int i = 0; i < arrayList.size(); i++) {
                    ((CheckBox) arrayList.get(i)).setChecked(state.selection.checked(i));
                }
                runnable.run();
            }
        });
        TextView button3 = button(mainActivity, "Deselect all", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                state.selection.none();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ((CheckBox) it.next()).setChecked(false);
                }
                runnable.run();
            }
        });
        int i = -2;
        column2.addView(button2, new LinearLayout.LayoutParams(0, -2, 1.0f));
        column2.addView(button3, new LinearLayout.LayoutParams(0, -2, 1.0f));
        column.addView(column2);
        final TrackListScrollView trackListScrollView = new TrackListScrollView(mainActivity);
        LinearLayout column3 = column(mainActivity);
        trackListScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() { // from class: com.bkggds.videotool.Workflow.8
            @Override // android.view.View.OnScrollChangeListener
            public void onScrollChange(View view, int i2, int i3, int i4, int i5) {
                state.trackScroll = i3;
            }
        });
        int i2 = 0;
        while (i2 < tracksUi.size()) {
            MainActivity.TrackOption trackOption = (MainActivity.TrackOption) tracksUi.get(i2);
            CheckBox checkBox = new CheckBox(AppTheme.context(mainActivity));
            checkBox.setText(String.valueOf(trackOption.title()) + "\n" + trackOption.subtitle());
            checkBox.setTextSize(14.0f);
            checkBox.setTextColor(-15788246);
            checkBox.setPadding(dp(mainActivity, 6), dp(mainActivity, 8), dp(mainActivity, 6), dp(mainActivity, 8));
            checkBox.setChecked(state.selection.checked(i2));
            checkBox.setEnabled((trackOption.bitmap || mainActivity.extractingUi()) ? false : true);
            checkBox.setAlpha(trackOption.bitmap ? 0.5f : 1.0f);
            checkBox.setMinHeight(dp(mainActivity, 64));
            final int boxIndex = i2;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.bkggds.videotool.Workflow.9
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    state.selection.set(boxIndex, z2);
                    runnable.run();
                }
            });
            arrayList.add(checkBox);
            column3.addView(checkBox, new LinearLayout.LayoutParams(-1, -2));
            i2++;
            i = -2;
            z = false;
        }
        trackListScrollView.addView(column3, new FrameLayout.LayoutParams(-1, i));
        column.addView(trackListScrollView, new LinearLayout.LayoutParams(-1, i));
        final int i3 = state.trackScroll;
        trackListScrollView.post(new Runnable() { // from class: com.bkggds.videotool.Workflow.10
            @Override // java.lang.Runnable
            public void run() {
                trackListScrollView.scrollTo(0, i3);
            }

            /* renamed from: com.bkggds.videotool.Workflow$10$1, reason: invalid class name */
            class AnonymousClass1 implements Runnable {
                private final /* synthetic */ MainActivity val$a;
                private final /* synthetic */ List val$failed;
                private final /* synthetic */ List val$ready;
                private final /* synthetic */ State val$s;
                private final /* synthetic */ List val$selected;
                private final /* synthetic */ TaskProgress val$task;

                AnonymousClass1(TaskProgress taskProgress, MainActivity mainActivity, State state, List list, List list2, List list3) {
                    this.val$task = taskProgress;
                    this.val$a = mainActivity;
                    this.val$s = state;
                    this.val$ready = list;
                    this.val$selected = list2;
                    this.val$failed = list3;
                }

                @Override // java.lang.Runnable
                public void run() {
                    this.val$task.dismiss();
                    if (!Workflow.alive(this.val$a) || Workflow.states.get(this.val$a) != this.val$s) {
                        return;
                    }
                    this.val$s.task = null;
                    this.val$a.setExtractingUi(false);
                    if (this.val$task.cancelled()) {
                        this.val$a.refreshUi();
                        return;
                    }
                    if (this.val$ready.isEmpty()) {
                        this.val$a.statusUi(Localization.tr("Could not extract the selected tracks. Your existing subtitles were kept."), true);
                        this.val$a.refreshUi();
                        return;
                    }
                    this.val$s.results = this.val$ready;
                    this.val$s.active = 0;
                    this.val$a.applyResultUi(((Result) this.val$ready.get(0)).srt, ((Result) this.val$ready.get(0)).name);
                    this.val$a.navigateUi(2);
                    this.val$a.statusUi(String.valueOf(Localization.tr("Extracted tracks")) + ": " + this.val$ready.size() + " / " + this.val$selected.size(), false);
                    if (this.val$failed.isEmpty()) {
                        return;
                    }
                    new AlertDialog.Builder(AppTheme.context(this.val$a)).setTitle(Localization.tr("Some tracks could not be read")).setMessage(Workflow.join(this.val$failed)).setPositiveButton(Localization.tr("Done"), (DialogInterface.OnClickListener) null).show();
                }
            }
        });
        if (tracksUi.size() > 4) {
            column.addView(text(mainActivity, "Scroll the track list to see more.", 12, z));
        }
        addButton(column, button);
        runnable.run();
        if (!state.results.isEmpty()) {
            addButton(column, button(mainActivity, "Review subtitles", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.11
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    mainActivity.navigateUi(2);
                }
            }));
        }
        return column;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void extract(final MainActivity mainActivity, final State state) {
        if (!ImportGuard.require(mainActivity) || mainActivity.extractingUi() || state.selection.size() == 0) {
            return;
        }
        final Uri videoUi = mainActivity.videoUi();
        final ArrayList arrayList = new ArrayList();
        Iterator<Integer> it = state.selection.indices().iterator();
        while (it.hasNext()) {
            arrayList.add((MainActivity.TrackOption) mainActivity.tracksUi().get(it.next().intValue()));
        }
        final TaskProgress taskProgress = new TaskProgress(mainActivity, "Extracting subtitles", arrayList.size());
        state.task = taskProgress;
        mainActivity.setExtractingUi(true);
        mainActivity.refreshUi();
        new Thread(new Runnable() { // from class: com.bkggds.videotool.Workflow.12
            @Override // java.lang.Runnable
            public void run() {
                MainActivity.Extraction extractUi = null;
                final ArrayList arrayList2 = new ArrayList();
                final ArrayList arrayList3 = new ArrayList();
                HashSet hashSet = new HashSet();
                int i = 0;
                while (i < arrayList.size() && !taskProgress.cancelled()) {
                    MainActivity.TrackOption trackOption = (MainActivity.TrackOption) arrayList.get(i);
                    taskProgress.update(i, arrayList.size(), trackOption.title());
                    try {
                        extractUi = mainActivity.extractUi(videoUi, trackOption);
                    } catch (Exception e) {
                        extractUi = null;
                    }
                    if (extractUi == null || extractUi.srt == null || extractUi.srt.trim().length() == 0) {
                        arrayList3.add(trackOption.title());
                        i++;
                        taskProgress.update(i, arrayList.size(), trackOption.title());
                        continue;
                    }
                    arrayList2.add(new Result(trackOption, extractUi.srt, SafeSrtNames.unique(mainActivity.trackFileNameUi(trackOption), hashSet)));
                    i++;
                    taskProgress.update(i, arrayList.size(), trackOption.title());
                }
                MainActivity mainActivity2 = mainActivity;
                final TaskProgress taskProgress2 = taskProgress;
                final MainActivity mainActivity3 = mainActivity;
                final State state2 = state;
                final List list = arrayList;
                mainActivity2.runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.Workflow.12.1
                    @Override // java.lang.Runnable
                    public void run() {
                        taskProgress2.dismiss();
                        if (!Workflow.alive(mainActivity3) || Workflow.states.get(mainActivity3) != state2) {
                            return;
                        }
                        state2.task = null;
                        mainActivity3.setExtractingUi(false);
                        if (taskProgress2.cancelled()) {
                            mainActivity3.refreshUi();
                            return;
                        }
                        if (arrayList2.isEmpty()) {
                            mainActivity3.statusUi(Localization.tr("Could not extract the selected tracks. Your existing subtitles were kept."), true);
                            mainActivity3.refreshUi();
                            return;
                        }
                        state2.results = arrayList2;
                        state2.active = 0;
                        mainActivity3.applyResultUi(((Result) arrayList2.get(0)).srt, ((Result) arrayList2.get(0)).name);
                        mainActivity3.navigateUi(2);
                        mainActivity3.statusUi(String.valueOf(Localization.tr("Extracted tracks")) + ": " + arrayList2.size() + " / " + list.size(), false);
                        if (arrayList3.isEmpty()) {
                            return;
                        }
                        AppFonts.decorate(new AlertDialog.Builder(AppTheme.context(mainActivity3)).setTitle(Localization.tr("Some tracks could not be read")).setMessage(Workflow.join(arrayList3)).setPositiveButton(Localization.tr("Done"), (DialogInterface.OnClickListener) null).show());
                    }
                });
            }
        }, "extract-selected-subtitles").start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next()).append('\n');
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void commit(MainActivity mainActivity, State state) {
        if (state.results.isEmpty() || state.active < 0 || state.active >= state.results.size()) {
            return;
        }
        state.results.get(state.active).srt = mainActivity.editorUi().getText().toString();
        state.results.get(state.active).name = mainActivity.fileNameUi().getText().toString();
    }

    public static View editScreen(final MainActivity mainActivity) {
        final State state = state(mainActivity);
        commit(mainActivity, state);
        LinearLayout column = column(mainActivity);
        column.addView(text(mainActivity, "Edit subtitles", 18, true));
        if (state.results.size() == 1) {
            column.addView(text(mainActivity, state.results.get(0).track.title(), 13, false));
        }
        if (state.results.size() > 1) {
            column.addView(text(mainActivity, "Choose an extracted track to edit", 13, false));
            int size = state.results.size();
            String[] strArr = new String[size];
            for (int i = 0; i < size; i++) {
                strArr[i] = state.results.get(i).track.title();
            }
            Spinner spinner = new Spinner(AppTheme.context(mainActivity));
            spinner.setAdapter((SpinnerAdapter) new AppFonts.LabelAdapter(AppTheme.context(mainActivity), strArr));
            spinner.setSelection(state.active);
            column.addView(spinner, new LinearLayout.LayoutParams(-1, dp(mainActivity, 52)));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.bkggds.videotool.Workflow.13
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j) {
                    if (i2 == state.active) {
                        return;
                    }
                    Workflow.commit(mainActivity, state);
                    state.active = i2;
                    Result result = state.results.get(i2);
                    mainActivity.applyResultUi(result.srt, result.name);
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
        EditText editorUi = mainActivity.editorUi();
        detach(editorUi);
        EditorTools.add(column, editorUi, new LinearLayout.LayoutParams(-1, dp(mainActivity, 340)));
        column.addView(text(mainActivity, "File name", 13, true));
        EditText fileNameUi = mainActivity.fileNameUi();
        detach(fileNameUi);
        column.addView(fileNameUi, new LinearLayout.LayoutParams(-1, -2));
        addButton(column, button(mainActivity, "Save this subtitle as SRT", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (mainActivity.editorUi().getText().toString().trim().length() == 0) {
                    Toast.makeText(mainActivity, Localization.tr("Nothing to save yet. Extract subtitles first."), 1).show();
                } else {
                    Workflow.commit(mainActivity, state);
                    mainActivity.saveCurrentUi(true);
                }
            }
        }));
        if (state.results.size() > 1) {
            addButton(column, button(mainActivity, "Save extracted tracks to a folder", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.15
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Workflow.chooseExport(mainActivity, state);
                }

                /* renamed from: com.bkggds.videotool.Workflow$15$1, reason: invalid class name */
                class AnonymousClass1 implements Runnable {
                    private final /* synthetic */ MainActivity val$a;
                    private final /* synthetic */ int val$count;
                    private final /* synthetic */ List val$failures;
                    private final /* synthetic */ List val$files;
                    private final /* synthetic */ State val$s;
                    private final /* synthetic */ TaskProgress val$task;

                    AnonymousClass1(TaskProgress taskProgress, MainActivity mainActivity, State state, int i, List list, List list2) {
                        this.val$task = taskProgress;
                        this.val$a = mainActivity;
                        this.val$s = state;
                        this.val$count = i;
                        this.val$files = list;
                        this.val$failures = list2;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        this.val$task.dismiss();
                        if (Workflow.alive(this.val$a) && Workflow.states.get(this.val$a) == this.val$s) {
                            this.val$s.task = null;
                            this.val$a.setExtractingUi(false);
                            this.val$a.statusUi(String.valueOf(Localization.tr("Saved files")) + ": " + this.val$count + " / " + this.val$files.size(), !this.val$failures.isEmpty());
                            this.val$a.refreshUi();
                            if (this.val$failures.isEmpty()) {
                                return;
                            }
                            new AlertDialog.Builder(AppTheme.context(this.val$a)).setTitle(Localization.tr("Some files could not be saved")).setMessage(Workflow.join(this.val$failures)).setPositiveButton(Localization.tr("Done"), (DialogInterface.OnClickListener) null).show();
                        }
                    }
                }
            }));
        }
        addButton(column, button(mainActivity, "Add SRT", new View.OnClickListener() { // from class: com.bkggds.videotool.Workflow.16
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                mainActivity.navigateUi(3);
            }
        }));
        return column;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void chooseExport(MainActivity mainActivity, State state) {
        if (ImportGuard.require(mainActivity) && !mainActivity.extractingUi()) {
            commit(mainActivity, state);
            ArrayList arrayList = new ArrayList();
            HashSet hashSet = new HashSet();
            String obj = mainActivity.externalUi().getText().toString();
            for (Result result : state.results) {
                arrayList.add(new Result(result.track, mainActivity.mergeUi(result.srt, obj), SafeSrtNames.unique(result.name, hashSet)));
            }
            state.pending = arrayList;
            Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
            intent.addFlags(3);
            try {
                mainActivity.startActivityForResult(intent, EXPORT_FOLDER);
            } catch (Exception e) {
                state.pending = null;
                Toast.makeText(mainActivity, Localization.tr("Could not open the folder picker."), 1).show();
            }
        }
    }

    public static boolean onActivityResult(final MainActivity mainActivity, int i, int i2, Intent intent) {
        if (i != EXPORT_FOLDER) {
            return false;
        }
        final State state = state(mainActivity);
        final List<Result> list = state.pending;
        state.pending = null;
        if (i2 != -1 || intent == null || intent.getData() == null || list == null || list.isEmpty()) {
            return true;
        }
        final Uri data = intent.getData();
        final TaskProgress taskProgress = new TaskProgress(mainActivity, "Saving extracted tracks", list.size());
        state.task = taskProgress;
        mainActivity.setExtractingUi(true);
        new Thread(new Runnable() { // from class: com.bkggds.videotool.Workflow.17
            @Override // java.lang.Runnable
            public void run() {
                Uri uri = null;
                OutputStream openOutputStream = null;
                final ArrayList arrayList = new ArrayList();
                int i3 = 0;
                int i4 = 0;
                while (i3 < list.size() && !taskProgress.cancelled()) {
                    Result result = (Result) list.get(i3);
                    try {
                        uri = DocumentsContract.createDocument(mainActivity.getContentResolver(), DocumentsContract.buildDocumentUriUsingTree(data, DocumentsContract.getTreeDocumentId(data)), "application/x-subrip", result.name);
                    } catch (Exception e) {
                        uri = null;
                    }
                    if (uri == null) {
                        arrayList.add(result.name);
                        i3++;
                        taskProgress.update(i3, list.size(), result.name);
                        continue;
                    }
                    try {
                        openOutputStream = mainActivity.getContentResolver().openOutputStream(uri, "wt");
                    } catch (Exception e2) {
                        arrayList.add(result.name);
                        if (uri != null) {
                            try {
                                DocumentsContract.deleteDocument(mainActivity.getContentResolver(), uri);
                            } catch (Exception e3) {
                            }
                        }
                        i3++;
                        taskProgress.update(i3, list.size(), result.name);
                    }
                    if (openOutputStream == null) {
                        arrayList.add(result.name);
                        i3++;
                        taskProgress.update(i3, list.size(), result.name);
                        continue;
                    }
                    try {
                        openOutputStream.write(result.srt.getBytes("UTF-8"));
                        openOutputStream.close();
                        i4++;
                        i3++;
                        taskProgress.update(i3, list.size(), result.name);
                    } catch (Throwable th) {
                        try {
                            openOutputStream.close();
                        } catch (Exception e4) {
                        }
                        arrayList.add(result.name);
                        i3++;
                        taskProgress.update(i3, list.size(), result.name);
                    }
                }
                MainActivity mainActivity2 = mainActivity;
                final TaskProgress taskProgress2 = taskProgress;
                final MainActivity mainActivity3 = mainActivity;
                final State state2 = state;
                final List list2 = list;
                final int savedCount = i4;
                mainActivity2.runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.Workflow.17.1
                    @Override // java.lang.Runnable
                    public void run() {
                        taskProgress2.dismiss();
                        if (Workflow.alive(mainActivity3) && Workflow.states.get(mainActivity3) == state2) {
                            state2.task = null;
                            mainActivity3.setExtractingUi(false);
                            mainActivity3.statusUi(String.valueOf(Localization.tr("Saved files")) + ": " + savedCount + " / " + list2.size(), !arrayList.isEmpty());
                            mainActivity3.refreshUi();
                            if (arrayList.isEmpty()) {
                                return;
                            }
                            AppFonts.decorate(new AlertDialog.Builder(AppTheme.context(mainActivity3)).setTitle(Localization.tr("Some files could not be saved")).setMessage(Workflow.join(arrayList)).setPositiveButton(Localization.tr("Done"), (DialogInterface.OnClickListener) null).show());
                        }
                    }
                });
            }
        }, "save-selected-subtitles").start();
        return true;
    }
}
