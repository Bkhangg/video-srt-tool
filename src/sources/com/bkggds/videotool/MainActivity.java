package com.bkggds.videotool;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bkggds.videotool.MainActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.zip.Inflater;

/* loaded from: classes.dex */
public class MainActivity extends Activity {
    private static final String ALL_TRACKS_NAME = "all_tracks.srt";
    private static final int BG = -1;
    private static final int BORDER = -1906448;
    private static final int ERR_BG = -68878;
    private static final int ERR_BORDER = -79158;
    private static final int ERR_FG = -4645860;
    private static final int FG = -15788246;
    private static final long ID_BLOCK = 161;
    private static final long ID_BLOCK_DURATION = 155;
    private static final long ID_BLOCK_GROUP = 160;
    private static final long ID_CLUSTER = 524531317;
    private static final long ID_CLUSTER_TS = 231;
    private static final long ID_CODEC_ID = 134;
    private static final long ID_COMP_ALGO = 16980;
    private static final long ID_COMP_SETTINGS = 16981;
    private static final long ID_CONTENT_COMP = 20532;
    private static final long ID_EBML = 440786851;
    private static final long ID_INFO = 357149030;
    private static final long ID_LANGUAGE = 2274716;
    private static final long ID_SEGMENT = 408125543;
    private static final long ID_SIMPLE_BLOCK = 163;
    private static final long ID_TRACKS = 374648427;
    private static final long ID_TRACK_ENTRY = 174;
    private static final long ID_TRACK_NAME = 21358;
    private static final long ID_TRACK_NUMBER = 215;
    private static final long ID_TRACK_TYPE = 131;
    private static final long ID_TS_SCALE = 2807729;
    private static final int INFO_BG = -1050881;
    private static final int INFO_BORDER = -4203522;
    private static final int INFO_FG = -14856488;
    private static final int MUTED = -10193781;
    private static final int MUTED_BG = -920071;
    private static final int PRIMARY = -15788246;
    private static final int PRIMARY_FG = -1;
    private static final int REQ_SAVE_AS = 102;
    private static final int REQ_SRT2 = 101;
    private static final int REQ_VIDEO = 100;
    private int cueCount;
    private int currentScreen;
    private LinearLayout drawer;
    private int drawerWidth;
    private EditText editExternal;
    private EditText editExtracted;
    private EditText editFileName;
    private boolean extracting;
    private LinearLayout fileNameBox;
    private PageScrollView pageScroll;
    private boolean saveAllTracks;
    private boolean pendingPlay;
    private boolean scanningTracks;
    private LinearLayout screens;
    private View scrim;
    private int selectedTrack;
    private TextView tvBanner;
    private TextView tvTopTitle;
    private Uri videoUri;
    private static final String[] NAV_TITLES = {"Import video", "Extract subtitles", "Edit subtitles", "Add a new SRT to the video", "Save file", "Help"};
    private static final String[] NAV_DESCS = {"Pick an MKV or MP4", "Read embedded tracks", "Review the SRT text", "Paste or load an SRT", "Write the result to Downloads", "Formats and notes"};
    private String trackInfo = "";
    private String savedInfo = "";
    private final List<LinearLayout> navItems = new ArrayList();
    private final List<TrackOption> tracks = new ArrayList();

    private String readUriText(Uri uri) throws Exception {
        return SrtText.read(getContentResolver().openInputStream(uri));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanTracks() {
        Workflow.scan(this);
    }

    private View screenEdit() {
        return Workflow.editScreen(this);
    }

    private View screenExtract() {
        return Workflow.extractScreen(this);
    }

    private View screenSecond() {
        return AddedSrt.screen(this);
    }

    private View trackRow(final int i) {
        TrackOption trackOption = this.tracks.get(i);
        LinearLayout column = column();
        int dp = dp(12);
        column.setPadding(dp, dp, dp, dp);
        column.setBackground(ripple(bg(i == this.selectedTrack ? MUTED_BG : -1, 10, BORDER, 1)));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(8);
        column.setLayoutParams(layoutParams);
        String title = trackOption.title();
        if (i == this.selectedTrack) {
            title = "✓ ".concat(title);
        }
        column.addView(text(title, 14, -15788246, true));
        column.addView(muted(trackOption.subtitle()));
        column.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda40
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.m32lambda$trackRow$8$combuddyappsvideotoolMainActivity(i, view);
            }
        });
        return column;
    }

    private int vintLen(long j) {
        if (j < 128) {
            return 1;
        }
        if (j < 16384) {
            return 2;
        }
        return j < 2097152 ? 3 : 4;
    }

    public void applyResultUi(String str, String str2) {
        EditText editText = this.editExtracted;
        editText.setText(str);
        editText.setSelection(0);
        editText.scrollTo(0, 0);
        this.cueCount = countCues(str);
        this.editFileName.setText(str2);
    }

    public int cueCountUi(String str) {
        return countCues(str);
    }

    public int currentPageUi() {
        return this.currentScreen;
    }

    public EditText editorUi() {
        return this.editExtracted;
    }

    public EditText externalUi() {
        return this.editExternal;
    }

    public Extraction extractUi(Uri uri, TrackOption trackOption) throws Exception {
        return extractSrt(uri, trackOption);
    }

    public boolean extractingUi() {
        return this.extracting;
    }

    public EditText fileNameUi() {
        return this.editFileName;
    }

    /* renamed from: lambda$navItem$3$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m14lambda$navItem$3$combuddyappsvideotoolMainActivity(int i, View view) {
        closeDrawer();
        if (i == 1) {
            Workflow.continueFromImport(this);
        } else {
            showScreen(i);
        }
    }

    /* renamed from: lambda$onActivityResult$24$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m15lambda$onActivityResult$24$combuddyappsvideotoolMainActivity(String str, Uri uri) {
        AddedSrt.loaded(this, str, queryDisplayName(uri));
    }

    /* renamed from: lambda$screenImport$5$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m30lambda$screenImport$5$combuddyappsvideotoolMainActivity() {
        Workflow.continueFromImport(this);
    }

    public List listTracksUi(Uri uri) throws Exception {
        return listTracks(uri);
    }

    public String mergeUi(String str, String str2) {
        return mergeSrt(str, str2);
    }

    public void navigateUi(int i) {
        showScreen(i);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        View view = getWindow().getDecorView().findViewWithTag("previewPlayer");
        if (view instanceof VideoPreview) {
            ((VideoPreview) view).pausePlayback();
        }
    }

    protected void onDestroy() {
        Workflow.release(this);
        Navigation.release(this);
        AddedSrt.release(this);
        VideoMux.release(this);
        super.onDestroy();
    }

    public void openSettings() {
        closeDrawer();
        AppSettings.show(this);
    }

    public void pickAddedUi() {
        pickExternalSrt();
    }

    public void refreshLanguage() {
        this.editExtracted.setHint(Localization.tr("Nothing extracted yet. Run Extract subtitles..."));
        this.editExternal.setHint(Localization.tr("Paste SRT text to merge with the extracted subtitles..."));
        View findViewWithTag = getWindow().getDecorView().findViewWithTag("navVersion");
        if (findViewWithTag instanceof TextView) {
            ((TextView) findViewWithTag).setText("v" + appVersion() + Localization.tr("  ·  MKV / MP4  ·  SRT output"));
        }
        showScreen(this.currentScreen);
        setStatus(Localization.tr("Preferences updated."), false);
    }

    public void refreshPreferences() {
        Localization.init(this);
        AppTheme.window(this);
        Localization.refreshLabels(getWindow().getDecorView());
        refreshLanguage();
    }

    public void refreshUi() {
        showScreen(this.currentScreen);
    }

    public void saveCurrentUi(boolean z) {
        if (ImportGuard.require(this)) {
            this.saveAllTracks = false;
            if (z) {
                doSaveAs();
            } else {
                doSave();
            }
        }
    }

    public void scanUi() {
        scanTracks();
    }

    public boolean scanningUi() {
        return this.scanningTracks;
    }

    public void setExtractingUi(boolean z) {
        this.extracting = z;
    }

    public void setScanningUi(boolean z) {
        this.scanningTracks = z;
    }

    public void statusUi(String str, boolean z) {
        setStatus(str, z);
    }

    public String trackFileNameUi(TrackOption trackOption) {
        return uniqueSrtName(videoBaseName(), trackOption, new ArrayList());
    }

    public List tracksUi() {
        return this.tracks;
    }

    public String videoBaseUi() {
        return videoBaseName();
    }

    public Uri videoUi() {
        return this.videoUri;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        final Thread.UncaughtExceptionHandler previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable th) {
                try {
                    getSharedPreferences("bkggds_ui", 0).edit().putString("crash_log", android.util.Log.getStackTraceString(th)).commit();
                } catch (Throwable ignored) {
                }
                if (previousHandler != null) {
                    previousHandler.uncaughtException(thread, th);
                }
            }
        });
        Localization.init(this);
        AppTheme.init(this);
        Window window = getWindow();
        window.setStatusBarColor(-1);
        window.setNavigationBarColor(-1);
        window.getDecorView().setSystemUiVisibility(10000);
        this.editExtracted = monoField(Localization.tr("Nothing extracted yet. Run Extract subtitles..."), 300);
        this.editExternal = monoField(Localization.tr("Paste SRT text to merge with the extracted subtitles..."), 260);
        EditText editText = new EditText(this);
        this.editFileName = editText;
        editText.setText("video_subtitle.srt");
        this.editFileName.setTextSize(14.0f);
        this.editFileName.setTextColor(-15788246);
        this.editFileName.setHintTextColor(MUTED);
        this.editFileName.setSingleLine(true);
        this.editFileName.setBackground(ripple(bg(-1, 10, BORDER, 1)));
        this.editFileName.setPadding(dp(12), dp(10), dp(12), dp(10));
        EditorTools.configureFilename(this.editFileName);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(-1);
        frameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnApplyWindowInsetsListener
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                WindowInsets apply;
                apply = InsetsUX.apply(view, windowInsets);
                return apply;
            }
        });
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(-1);
        linearLayout.addView(buildTopBar());
        linearLayout.addView(buildBanner());
        PageScrollView pageScrollView = new PageScrollView(this);
        this.pageScroll = pageScrollView;
        pageScrollView.setFillViewport(true);
        LinearLayout linearLayout2 = new LinearLayout(this);
        this.screens = linearLayout2;
        linearLayout2.setOrientation(1);
        this.screens.setPadding(dp(16), dp(12), dp(16), dp(40));
        pageScrollView.addView(this.screens, new ViewGroup.LayoutParams(-1, -2));
        linearLayout.addView(pageScrollView, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        Localization.footer(this, linearLayout);
        frameLayout.addView(linearLayout, new ViewGroup.LayoutParams(-1, -1));
        View view = new View(this);
        this.scrim = view;
        view.setBackgroundColor(-15788246);
        this.scrim.setAlpha(0.0f);
        this.scrim.setVisibility(8);
        this.scrim.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda15
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MainActivity.this.m20lambda$onCreate$1$combuddyappsvideotoolMainActivity(view2);
            }
        });
        frameLayout.addView(this.scrim, new ViewGroup.LayoutParams(-1, -1));
        this.drawer = buildDrawer();
        this.drawerWidth = (int) Math.min(dp(300), getResources().getDisplayMetrics().widthPixels * 0.84f);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(this.drawerWidth, -1);
        layoutParams.gravity = 8388611;
        this.drawer.setTranslationX(-this.drawerWidth);
        this.drawer.setElevation(dp(10));
        frameLayout.addView(this.drawer, layoutParams);
        setContentView(frameLayout);
        showScreen(0);
        setStatus(Localization.tr("Pick a video from this device to begin."), false);
        Navigation.install(this);
        handleViewIntent(getIntent());
        String crashLog = getSharedPreferences("bkggds_ui", 0).getString("crash_log", null);
        if (crashLog != null) {
            getSharedPreferences("bkggds_ui", 0).edit().remove("crash_log").apply();
            new android.app.AlertDialog.Builder(this).setTitle("Crash log").setMessage(crashLog).setPositiveButton("OK", (android.content.DialogInterface.OnClickListener) null).show();
        }
    }

    /* renamed from: lambda$onCreate$1$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m20lambda$onCreate$1$combuddyappsvideotoolMainActivity(View view) {
        closeDrawer();
    }

    private View buildTopBar() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(-1);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        linearLayout2.setPadding(dp(8), dp(6), dp(16), dp(6));
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        frameLayout.setBackground(ripple(bg(0, 10, 0, 0)));
        frameLayout.addView(new MenuIcon(this, -15788246), new FrameLayout.LayoutParams(dp(20), dp(20), 17));
        frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.m5lambda$buildTopBar$2$combuddyappsvideotoolMainActivity(view);
            }
        });
        linearLayout2.addView(frameLayout);
        LinearLayout linearLayout3 = new LinearLayout(this);
        linearLayout3.setOrientation(1);
        this.tvTopTitle = text(Localization.tr("Import video"), 15, -15788246, true);
        TextView text = text(Localization.tr("Video Subtitle Tool"), 12, MUTED, false);
        linearLayout3.addView(this.tvTopTitle);
        linearLayout3.addView(text);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.leftMargin = dp(6);
        linearLayout2.addView(linearLayout3, layoutParams);
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, -2));
        linearLayout.addView(divider());
        return linearLayout;
    }

    /* renamed from: lambda$buildTopBar$2$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m5lambda$buildTopBar$2$combuddyappsvideotoolMainActivity(View view) {
        openDrawer();
    }

    private View buildBanner() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(16), dp(10), dp(16), 0);
        linearLayout.setBackgroundColor(-1);
        TextView text = text("", 13, INFO_FG, false);
        this.tvBanner = text;
        text.setPadding(dp(12), dp(10), dp(12), dp(10));
        this.tvBanner.setLineSpacing(dp(2), 1.0f);
        this.tvBanner.setVisibility(8);
        linearLayout.addView(this.tvBanner);
        return linearLayout;
    }

    private LinearLayout buildDrawer() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(-1);
        linearLayout.setPadding(dp(12), dp(20), dp(12), dp(14));
        TextView text = text(Localization.tr("Video Subtitle Tool"), 16, -15788246, true);
        TextView text2 = text(Localization.tr("Extract, merge and save subtitles"), 12, MUTED, false);
        linearLayout.addView(text);
        linearLayout.addView(text2);
        space(linearLayout, 16);
        linearLayout.addView(divider());
        space(linearLayout, 8);
        int i = 0;
        while (true) {
            int length = NAV_TITLES.length;
            if (i < 3) {
                LinearLayout navItem = navItem(i);
                this.navItems.add(navItem);
                linearLayout.addView(navItem);
                i++;
            } else {
                AppSettings.sidebar(this, linearLayout);
                linearLayout.addView(new View(this), new LinearLayout.LayoutParams(1, 0, 1.0f));
                linearLayout.addView(divider());
                TextView text3 = text("v" + appVersion() + Localization.tr("  ·  MKV / MP4  ·  SRT output"), 11, MUTED, false);
                text3.setTag("navVersion");
                text3.setPadding(dp(12), dp(12), dp(12), dp(2));
                linearLayout.addView(text3);
                AppSettings.makeScrollable(linearLayout);
                return linearLayout;
            }
        }
    }

    private LinearLayout navItem(final int i) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(12), dp(10), dp(12), dp(10));
        TextView text = text(NAV_TITLES[i], 14, -15788246, true);
        TextView text2 = text(NAV_DESCS[i], 11, MUTED, false);
        text2.setPadding(0, dp(2), 0, 0);
        linearLayout.addView(text);
        linearLayout.addView(text2);
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda37
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.m14lambda$navItem$3$combuddyappsvideotoolMainActivity(i, view);
            }
        });
        AppSettings.compactNav(linearLayout);
        return linearLayout;
    }

    private void openDrawer() {
        this.scrim.setVisibility(0);
        this.scrim.animate().alpha(0.45f).setDuration(180L).start();
        this.drawer.animate().translationX(0.0f).setDuration(220L).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void closeDrawer() {
        hideKeyboard();
        this.drawer.animate().translationX(-this.drawerWidth).setDuration(200L).setInterpolator(new DecelerateInterpolator()).start();
        this.scrim.animate().alpha(0.0f).setDuration(180L).withEndAction(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m6lambda$closeDrawer$4$combuddyappsvideotoolMainActivity();
            }
        }).start();
    }

    /* renamed from: lambda$closeDrawer$4$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m6lambda$closeDrawer$4$combuddyappsvideotoolMainActivity() {
        if (this.scrim.getAlpha() == 0.0f) {
            this.scrim.setVisibility(8);
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (VideoPreview.fullscreenPreview != null) {
            VideoPreview.fullscreenPreview.exitFullscreen();
            return;
        }
        if (this.drawer.getTranslationX() > (-this.drawerWidth) + 1) {
            closeDrawer();
        } else {
            Navigation.back(this);
        }
    }

    private void showScreen(int i) {
        View screenExtract;
        if (i > 0 && i <= 4 && !ImportGuard.require(this)) {
            i = 0;
        }
        if (i == 4) {
            i = 2;
        }
        Navigation.record(this, i);
        PageScrollView pageScrollView = this.pageScroll;
        if (pageScrollView != null) {
            pageScrollView.showPage(i);
        }
        this.currentScreen = i;
        TextView textView = this.tvTopTitle;
        if (textView != null) {
            textView.setText(Localization.tr(NAV_TITLES[i]));
        }
        for (int i2 = 0; i2 < this.navItems.size(); i2++) {
            SidebarStyle.select(this.navItems.get(i2), i2, i);
        }
        hideKeyboard();
        this.screens.removeAllViews();
        if (i == 1) {
            screenExtract = screenExtract();
        } else if (i == 2) {
            screenExtract = screenEdit();
        } else if (i == 3) {
            screenExtract = screenSecond();
        } else if (i == 4) {
            screenExtract = screenSave();
        } else if (i == 5) {
            screenExtract = screenHelp();
        } else {
            screenExtract = screenImport();
        }
        this.screens.addView(screenExtract);
        LinearLayout linearLayout = this.screens;
        AppTheme.refresh(this);
    }

    private View screenImport() {
        LinearLayout column = column();
        LinearLayout card = card(Localization.tr("Video file"), Localization.tr("Choose an MKV or MP4 file from this device."));
        card.addView(fieldLabel(Localization.tr("Selected file")));
        Uri uri = this.videoUri;
        card.addView(valueText(uri == null ? Localization.tr("No file selected") : queryDisplayName(uri)));
        space(card, 14);
        card.addView(buttonPrimary(Localization.tr(this.videoUri != null ? "Choose another video" : "Select video from device"), new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.importVideo();
            }
        }));
        space(card, 8);
        TextView buttonOutline = buttonOutline(Localization.tr("Continue to extract subtitles"), new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m30lambda$screenImport$5$combuddyappsvideotoolMainActivity();
            }
        });
        setButtonEnabled(buttonOutline, this.videoUri != null, false);
        card.addView(buttonOutline);
        column.addView(card);
        if (this.videoUri != null) {
            LinearLayout card2 = card(Localization.tr("Preview"), null);
            VideoPreview videoPreview = new VideoPreview(this, this.videoUri);
            videoPreview.setVideoTitle(queryDisplayName(this.videoUri));
            if (this.pendingPlay) {
                this.pendingPlay = false;
                videoPreview.startPlayback();
                videoPreview.startFullscreen();
            }
            card2.addView(videoPreview);
            column.addView(card2);
        }
        column.addView(note(Localization.tr("Text subtitles only: SRT, ASS/SSA, WebVTT and mov_text. Image based subtitles such as PGS or VobSub cannot be converted to text.")));
        return column;
    }

    /* renamed from: lambda$screenExtract$6$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m28lambda$screenExtract$6$combuddyappsvideotoolMainActivity() {
        doExtractTrack(this.selectedTrack);
    }

    /* renamed from: lambda$screenExtract$7$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m29lambda$screenExtract$7$combuddyappsvideotoolMainActivity() {
        showScreen(2);
    }

    /* renamed from: lambda$trackRow$8$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m32lambda$trackRow$8$combuddyappsvideotoolMainActivity(int i, View view) {
        this.selectedTrack = i;
        showScreen(1);
    }

    /* renamed from: lambda$screenEdit$9$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m27lambda$screenEdit$9$combuddyappsvideotoolMainActivity() {
        showScreen(4);
    }

    /* renamed from: lambda$screenEdit$10$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m26lambda$screenEdit$10$combuddyappsvideotoolMainActivity() {
        showScreen(3);
    }

    private View otherLangRow(final TrackOption trackOption) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(dp(10), dp(10), dp(10), dp(10));
        linearLayout.setBackground(ripple(bg(-1, 10, BORDER, 1)));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(6);
        linearLayout.setLayoutParams(layoutParams);
        LinearLayout column = column();
        column.addView(text(trackOption.title(), 14, -15788246, true));
        column.addView(muted(trackOption.subtitle()));
        linearLayout.addView(column, new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout.addView(text(Localization.tr("Extract"), 13, INFO_FG, true));
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda35
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.m21lambda$otherLangRow$11$combuddyappsvideotoolMainActivity(trackOption, view);
            }
        });
        return linearLayout;
    }

    /* renamed from: lambda$otherLangRow$11$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m21lambda$otherLangRow$11$combuddyappsvideotoolMainActivity(TrackOption trackOption, View view) {
        doExtractTrack(this.tracks.indexOf(trackOption));
    }

    /* renamed from: lambda$scanTracks$13$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m25lambda$scanTracks$13$combuddyappsvideotoolMainActivity(Uri uri) {
        List<TrackOption> found;
        String message;
        try {
            found = listTracks(uri);
            message = null;
        } catch (Exception e) {
            found = new ArrayList<>();
            message = e.getMessage();
        }
        final List<TrackOption> arrayList = found;
        final String errorMessage = message;
        runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m24lambda$scanTracks$12$combuddyappsvideotoolMainActivity(arrayList, errorMessage);
            }
        });
    }

    /* renamed from: lambda$scanTracks$12$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m24lambda$scanTracks$12$combuddyappsvideotoolMainActivity(List list, String str) {
        String str2;
        this.scanningTracks = false;
        this.tracks.clear();
        this.tracks.addAll(list);
        this.selectedTrack = 0;
        int i = 0;
        while (true) {
            if (i >= this.tracks.size()) {
                break;
            }
            if (!this.tracks.get(i).bitmap) {
                this.selectedTrack = i;
                break;
            }
            i++;
        }
        str2 = "";
        if (this.tracks.isEmpty()) {
            StringBuilder sb = new StringBuilder(Localization.tr("No subtitle tracks found in this video."));
            if (str != null) {
                str2 = " (" + str + ")";
            }
            sb.append(str2);
            setStatus(sb.toString(), true);
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.tracks.size());
            sb2.append(Localization.tr(" subtitle track"));
            sb2.append(this.tracks.size() != 1 ? Localization.tr("s") : "");
            sb2.append(Localization.tr(" found."));
            setStatus(sb2.toString(), false);
        }
        if (this.currentScreen == 1) {
            showScreen(1);
        }
    }

    private void doExtractTrack(int i) {
        if (this.videoUri == null) {
            setStatus(Localization.tr("Select a video first."), true);
            return;
        }
        if (i < 0 || i >= this.tracks.size()) {
            return;
        }
        final TrackOption trackOption = this.tracks.get(i);
        this.selectedTrack = i;
        if (trackOption.bitmap) {
            setStatus(Localization.tr("That track is image based (PGS / VobSub) and cannot become text."), true);
            return;
        }
        this.extracting = true;
        showScreen(1);
        setStatus(Localization.tr("Extracting ") + trackOption.title() + "...", false);
        new Thread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m11lambda$doExtractTrack$15$combuddyappsvideotoolMainActivity(trackOption);
            }
        }).start();
    }

    /* renamed from: lambda$doExtractTrack$15$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m11lambda$doExtractTrack$15$combuddyappsvideotoolMainActivity(final TrackOption trackOption) {
        String srt = null;
        String info;
        try {
            Extraction extractSrt = extractSrt(this.videoUri, trackOption);
            srt = extractSrt.srt;
            info = extractSrt.info;
        } catch (Exception e) {
            info = Localization.tr("Error: ") + e.getMessage();
        }
        final String str2 = srt;
        final String str = info == null ? "" : info;
        runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m10lambda$doExtractTrack$14$combuddyappsvideotoolMainActivity(str2, str, trackOption);
            }
        });
    }

    /* renamed from: lambda$doExtractTrack$14$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m10lambda$doExtractTrack$14$combuddyappsvideotoolMainActivity(String str, String str2, TrackOption trackOption) {
        this.extracting = false;
        String str3 = "";
        if (str == null || str.trim().isEmpty()) {
            this.cueCount = 0;
            this.editExtracted.setText("");
            this.trackInfo = str2;
            setStatus(Localization.tr("Could not read text from that track."), true);
            showScreen(1);
            return;
        }
        this.editExtracted.setText(str);
        this.cueCount = countCues(str);
        this.trackInfo = str2;
        StringBuilder sb = new StringBuilder(Localization.tr("Extracted "));
        sb.append(this.cueCount);
        sb.append(Localization.tr(" cues from "));
        sb.append(trackOption.title());
        sb.append(".");
        if (this.tracks.size() > 1) {
            str3 = Localization.tr(" To keep other languages, use Extract all ") + textTracks().size() + Localization.tr(" tracks.");
        }
        sb.append(str3);
        setStatus(sb.toString(), false);
        showScreen(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doExtractAll() {
        if (this.videoUri == null) {
            setStatus(Localization.tr("Select a video first."), true);
            return;
        }
        final List<TrackOption> textTracks = textTracks();
        if (textTracks.isEmpty()) {
            setStatus(Localization.tr("No text subtitle track to export."), true);
            return;
        }
        this.extracting = true;
        showScreen(1);
        setStatus(Localization.tr("Extracting ") + textTracks.size() + Localization.tr(" tracks..."), false);
        final Uri uri = this.videoUri;
        final String videoBaseName = videoBaseName();
        new Thread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m9lambda$doExtractAll$18$combuddyappsvideotoolMainActivity(textTracks, uri, videoBaseName);
            }
        }).start();
    }

    /* renamed from: lambda$doExtractAll$18$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m9lambda$doExtractAll$18$combuddyappsvideotoolMainActivity(List list, Uri uri, String str) {
        String str2;
        final ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int i = 0;
        String str3 = null;
        int i2 = 0;
        while (i < list.size()) {
            final TrackOption trackOption = (TrackOption) list.get(i);
            i++;
            final int size = list.size();
            final int progressIndex = i;
            runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.this.m7lambda$doExtractAll$16$combuddyappsvideotoolMainActivity(progressIndex, size, trackOption);
                }
            });
            try {
                str2 = extractSrt(uri, trackOption).srt;
            } catch (Exception unused) {
                str2 = null;
            }
            if (str2 != null && !str2.trim().isEmpty()) {
                String uniqueSrtName = uniqueSrtName(str, trackOption, arrayList2);
                if (saveSrt(str2, uniqueSrtName) != null) {
                    arrayList2.add(uniqueSrtName);
                    arrayList.add(trackOption.title() + " → " + uniqueSrtName);
                    if (str3 == null) {
                        str3 = str2;
                    }
                }
            }
            i2++;
        }
        final String firstSrt = str3;
        final int savedCount = i2;
        runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m8lambda$doExtractAll$17$combuddyappsvideotoolMainActivity(arrayList, firstSrt, savedCount);
            }
        });
    }

    /* renamed from: lambda$doExtractAll$16$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m7lambda$doExtractAll$16$combuddyappsvideotoolMainActivity(int i, int i2, TrackOption trackOption) {
        setStatus(Localization.tr("Extracting track ") + i + Localization.tr(" of ") + i2 + " (" + trackOption.title() + ")...", false);
    }

    /* renamed from: lambda$doExtractAll$17$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m8lambda$doExtractAll$17$combuddyappsvideotoolMainActivity(List list, String str, int i) {
        this.extracting = false;
        if (list.isEmpty()) {
            setStatus(Localization.tr("Could not extract any track. Try one track at a time."), true);
            showScreen(1);
            return;
        }
        if (str != null) {
            this.editExtracted.setText(str);
            this.cueCount = countCues(str);
        }
        String tr = Localization.tr("Saved ");
        StringBuilder sb = new StringBuilder(tr);
        sb.append(list.size());
        sb.append(Localization.tr(" file"));
        int size = list.size();
        String tr2 = Localization.tr("s");
        sb.append(size == 1 ? "" : tr2);
        sb.append(Localization.tr(" to Downloads:"));
        StringBuilder sb2 = new StringBuilder(sb.toString());
        for (int i2 = 0; i2 < list.size() && i2 < 4; i2++) {
            sb2.append("\n• ");
            sb2.append((String) list.get(i2));
        }
        if (list.size() > 4) {
            sb2.append("\n• …");
        }
        if (i > 0) {
            sb2.append("\n");
            sb2.append(i);
            StringBuilder sb3 = new StringBuilder(Localization.tr(" track"));
            sb3.append(i == 1 ? "" : tr2);
            sb3.append(Localization.tr(" could not be read."));
            sb2.append(sb3.toString());
        }
        this.savedInfo = sb2.toString();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(list.size());
        sb4.append(Localization.tr(" SRT file"));
        sb4.append(list.size() != 1 ? tr2 : "");
        sb4.append(Localization.tr(" saved to Downloads."));
        setStatus(sb4.toString(), false);
        showScreen(4);
        Toast.makeText(this, tr + list.size() + Localization.tr(" SRT files"), 0).show();
    }

    private List<TrackOption> textTracks() {
        ArrayList arrayList = new ArrayList();
        for (TrackOption trackOption : this.tracks) {
            if (!trackOption.bitmap) {
                arrayList.add(trackOption);
            }
        }
        return arrayList;
    }

    private String videoBaseName() {
        Uri uri = this.videoUri;
        String str = "video";
        String queryDisplayName = uri == null ? "video" : queryDisplayName(uri);
        if (queryDisplayName != null && !queryDisplayName.isEmpty()) {
            str = queryDisplayName;
        }
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf > 0) {
            str = str.substring(0, lastIndexOf);
        }
        return str.length() > 64 ? str.substring(0, 64) : str;
    }

    private String uniqueSrtName(String str, TrackOption trackOption, List<String> list) {
        String fileSuffix = trackOption.fileSuffix();
        String str2 = str + "." + fileSuffix + ".srt";
        int i = 2;
        while (list.contains(str2)) {
            str2 = str + "." + fileSuffix + "-" + i + ".srt";
            i++;
        }
        return str2;
    }

    /* renamed from: lambda$screenSecond$19$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m31lambda$screenSecond$19$combuddyappsvideotoolMainActivity(View view) {
        this.editExternal.setText("");
        setStatus(Localization.tr("Second subtitle cleared."), false);
    }

    private View screenSave() {
        String tr;
        LinearLayout column = column();
        int size = textTracks().size();
        LinearLayout card = card(Localization.tr("Save subtitle file"), Localization.tr("The result is written to Downloads as an SRT file."));
        card.addView(fieldLabel(Localization.tr("File name")));
        detach(this.fileNameBox);
        LinearLayout linearLayout = new LinearLayout(this);
        this.fileNameBox = linearLayout;
        linearLayout.setOrientation(1);
        detach(this.editFileName);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, dp(46));
        layoutParams.topMargin = dp(6);
        this.fileNameBox.addView(this.editFileName, layoutParams);
        card.addView(this.fileNameBox);
        space(card, 14);
        if (size > 1) {
            card.addView(buttonPrimary(Localization.tr("Save all ") + size + Localization.tr(" language tracks"), new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.this.saveAllFromEdit();
                }
            }));
            space(card, 8);
        }
        card.addView(buttonOutline(size > 1 ? Localization.tr("Save only the subtitle above") : Localization.tr("Save to Downloads"), new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.doSave();
            }
        }));
        space(card, 8);
        card.addView(buttonOutline(size > 1 ? Localization.tr("Save all tracks, choose a folder...") : Localization.tr("Save as... (choose a folder)"), new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.doSaveAs();
            }
        }));
        column.addView(card);
        int countCues = countCues(this.editExtracted.getText().toString());
        if (size > 1) {
            tr = size + Localization.tr(" text tracks in this video. Each language is saved as its own file.");
        } else if (countCues > 0) {
            tr = countCues + Localization.tr(" cues ready to save.");
        } else {
            tr = Localization.tr("Nothing to save yet. Extract subtitles first.");
        }
        column.addView(note(tr));
        String str = this.savedInfo;
        if (str != null && !str.isEmpty()) {
            column.addView(note(this.savedInfo));
        }
        return column;
    }

    private int textTrackCount() {
        return textTracks().size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveAllFromEdit() {
        if (textTrackCount() < 2) {
            this.saveAllTracks = false;
            doSave();
        } else if (this.editExtracted.getText().toString().trim().isEmpty() && this.videoUri == null) {
            setStatus(Localization.tr("Nothing to save yet. Extract subtitles first."), true);
        } else {
            this.saveAllTracks = true;
            doSaveAs();
        }
    }

    private void saveTracksToFolder(final Uri uri, final List<TrackOption> list) {
        setStatus(Localization.tr("Saving ") + list.size() + Localization.tr(" tracks..."), false);
        final Uri uri2 = this.videoUri;
        final String videoBaseName = videoBaseName();
        new Thread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m23xb0b0ceef(list, uri2, videoBaseName, uri);
            }
        }).start();
    }

    /* renamed from: lambda$saveTracksToFolder$21$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m23xb0b0ceef(List list, Uri uri, String str, Uri uri2) {
        String str2;
        final ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Iterator it = list.iterator();
        String str3 = null;
        while (it.hasNext()) {
            TrackOption trackOption = (TrackOption) it.next();
            try {
                str2 = extractSrt(uri, trackOption).srt;
            } catch (Exception unused) {
                str2 = null;
            }
            if (str2 != null && !str2.trim().isEmpty()) {
                String uniqueSrtName = uniqueSrtName(str, trackOption, arrayList2);
                if (writeTextToFolder(uri2, uniqueSrtName, str2)) {
                    arrayList2.add(uniqueSrtName);
                    arrayList.add(trackOption.title() + " → " + uniqueSrtName);
                    if (str3 == null) {
                        str3 = str2;
                    }
                }
            }
        }
        final String firstSaved = str3;
        runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m22x4f5e3250(arrayList, firstSaved);
            }
        });
    }

    /* renamed from: lambda$saveTracksToFolder$20$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m22x4f5e3250(List list, String str) {
        this.saveAllTracks = false;
        if (list.isEmpty()) {
            setStatus(Localization.tr("Could not extract any track. Try one track at a time."), true);
            return;
        }
        if (str != null && this.cueCount == 0) {
            this.editExtracted.setText(str);
            this.cueCount = countCues(str);
        }
        StringBuilder sb = new StringBuilder(Localization.tr("Saved "));
        sb.append(list.size());
        sb.append(Localization.tr(" file"));
        int size = list.size();
        String tr = Localization.tr("s");
        sb.append(size == 1 ? "" : tr);
        sb.append(Localization.tr(" to the folder you picked:"));
        StringBuilder sb2 = new StringBuilder(sb.toString());
        for (int i = 0; i < list.size() && i < 6; i++) {
            sb2.append("\n• ");
            sb2.append((String) list.get(i));
        }
        if (list.size() > 6) {
            sb2.append("\n• …");
        }
        this.savedInfo = sb2.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(list.size());
        sb3.append(Localization.tr(" SRT file"));
        sb3.append(list.size() != 1 ? tr : "");
        sb3.append(Localization.tr(" saved."));
        setStatus(sb3.toString(), false);
        showScreen(4);
    }

    private boolean writeTextToFolder(Uri uri, String str, String str2) {
        Uri uri2;
        try {
            if (DocumentsContract.isTreeUri(uri)) {
                Uri buildChildDocumentsUriUsingTree = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
                Cursor query = getContentResolver().query(buildChildDocumentsUriUsingTree, new String[]{"document_id", "_display_name"}, null, null, null);
                while (query != null) {
                    try {
                        if (!query.moveToNext()) {
                            break;
                        }
                        if (str.equals(query.getString(1))) {
                            uri2 = DocumentsContract.buildDocumentUriUsingTree(uri, query.getString(0));
                            break;
                        }
                    } finally {
                    }
                }
                uri2 = null;
                if (query != null) {
                    query.close();
                }
                if (uri2 == null && (uri2 = DocumentsContract.createDocument(getContentResolver(), buildChildDocumentsUriUsingTree, "application/x-subrip", str)) == null) {
                    return false;
                }
                return writeTextToUri(uri2, str2);
            }
        } catch (Exception unused) {
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSave() {
        if (ImportGuard.require(this)) {
            final String obj = this.editExtracted.getText().toString();
            final String obj2 = this.editExternal.getText().toString();
            if (obj.trim().isEmpty()) {
                setStatus(Localization.tr("Nothing to save yet. Extract subtitles first."), true);
            } else {
                final String fileName = fileName();
                new Thread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        MainActivity.this.m13lambda$doSave$23$combuddyappsvideotoolMainActivity(obj, obj2, fileName);
                    }
                }).start();
            }
        }
    }

    /* renamed from: lambda$doSave$23$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m13lambda$doSave$23$combuddyappsvideotoolMainActivity(String str, String str2, final String str3) {
        final String mergeSrt = mergeSrt(str, str2);
        final Uri saveSrt = saveSrt(mergeSrt, str3);
        runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m12lambda$doSave$22$combuddyappsvideotoolMainActivity(saveSrt, str3, mergeSrt);
            }
        });
    }

    /* renamed from: lambda$doSave$22$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m12lambda$doSave$22$combuddyappsvideotoolMainActivity(Uri uri, String str, String str2) {
        if (uri == null) {
            setStatus(Localization.tr("Save failed."), true);
            return;
        }
        this.savedInfo = Localization.tr("Saved: Downloads/") + str + "  (" + countCues(str2) + Localization.tr(" cues)");
        StringBuilder sb = new StringBuilder(Localization.tr("Saved to Downloads/"));
        sb.append(str);
        setStatus(sb.toString(), false);
        showScreen(4);
        Toast.makeText(this, Localization.tr("Saved to Downloads"), 0).show();
    }

    private View screenHelp() {
        LinearLayout column = column();
        LinearLayout card = card(Localization.tr("Formats"), null);
        card.addView(bullet(Localization.tr("Video: MKV (Matroska) and MP4.")));
        card.addView(bullet(Localization.tr("Subtitles read: SRT, ASS/SSA, WebVTT, mov_text.")));
        card.addView(bullet(Localization.tr("Not supported: PGS and VobSub, they are images.")));
        column.addView(card);
        LinearLayout card2 = card(Localization.tr("How it works"), null);
        card2.addView(bullet(Localization.tr("1. Import a video.")));
        card2.addView(bullet(Localization.tr("2. Extract the embedded subtitle track.")));
        card2.addView(bullet(Localization.tr("3. Add a new SRT to the video.")));
        card2.addView(bullet(Localization.tr("4. Save that subtitle, or the whole file, with Extract all tracks.")));
        card2.addView(bullet(Localization.tr("Edit subtitles has Save this subtitle and Save as... buttons.")));
        column.addView(card2);
        LinearLayout card3 = card(Localization.tr("Notes"), null);
        card3.addView(bullet(Localization.tr("Everything runs on the phone, nothing is uploaded.")));
        card3.addView(bullet(Localization.tr("Saving uses the system Downloads folder, no storage permission needed.")));
        column.addView(card3);
        return column;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void importVideo() {
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("*/*");
        intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"video/*", "video/x-matroska", "video/mp4", "video/mpeg", "application/octet-stream"});
        intent.addFlags(65);
        startActivityForResult(intent, REQ_VIDEO);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pickExternalSrt() {
        if (ImportGuard.require(this)) {
            Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"text/*", "application/x-subrip", "*/*"});
            intent.addFlags(1);
            startActivityForResult(intent, REQ_SRT2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSaveAs() {
        Intent intent;
        if (ImportGuard.require(this)) {
            if (this.saveAllTracks) {
                intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                intent.addFlags(67);
                setStatus(Localization.tr("Pick the folder where the subtitle files will be saved."), false);
            } else {
                intent = new Intent("android.intent.action.CREATE_DOCUMENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TITLE", fileName());
                intent.addFlags(2);
            }
            startActivityForResult(intent, REQ_SAVE_AS);
        }
    }

    private String fileName() {
        String trim = this.editFileName.getText().toString().trim();
        if (trim.isEmpty()) {
            trim = "video_subtitle.srt";
        }
        if (trim.toLowerCase(Locale.US).endsWith(".srt")) {
            return trim;
        }
        return trim + ".srt";
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleViewIntent(intent);
    }

    /* A video opened from a file manager ("Open with"): load it and play it right away. */
    private void handleViewIntent(Intent intent) {
        if (intent == null || !Intent.ACTION_VIEW.equals(intent.getAction()) || intent.getData() == null) {
            return;
        }
        Uri data = intent.getData();
        try {
            getContentResolver().takePersistableUriPermission(data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ignored) {
        }
        String displayName = queryDisplayName(data);
        if (displayName == null || displayName.length() == 0) {
            displayName = data.getLastPathSegment();
        }
        if (displayName == null || displayName.length() == 0) {
            displayName = "video";
        }
        this.videoUri = data;
        Workflow.reset(this);
        this.tracks.clear();
        this.cueCount = 0;
        this.trackInfo = "";
        this.editExtracted.setText("");
        String base = displayName;
        int dot = base.lastIndexOf(46);
        if (dot > 0) {
            base = base.substring(0, dot);
        }
        if (base.length() > 64) {
            base = base.substring(0, 64);
        }
        this.editFileName.setText(base + ".srt");
        this.pendingPlay = true;
        showScreen(0);
        setStatus(Localization.tr("Selected: ") + displayName, false);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (VideoMux.onActivityResult(this, i, i2, intent) || AddedSrt.onActivityResult(this, i, i2, intent) || Workflow.onActivityResult(this, i, i2, intent)) {
            return;
        }
        if (i2 != -1 || intent == null || intent.getData() == null) {
            if (i == REQ_SAVE_AS) {
                this.saveAllTracks = false;
                return;
            }
            return;
        }
        final Uri data = intent.getData();
        if (i != REQ_VIDEO) {
            if (i == REQ_SRT2) {
                new Thread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        MainActivity.this.m17lambda$onActivityResult$26$combuddyappsvideotoolMainActivity(data);
                    }
                }).start();
                return;
            }
            if (i == REQ_SAVE_AS) {
                if (this.saveAllTracks) {
                    ArrayList arrayList = new ArrayList();
                    for (TrackOption trackOption : this.tracks) {
                        if (!trackOption.bitmap) {
                            arrayList.add(trackOption);
                        }
                    }
                    saveTracksToFolder(data, arrayList);
                    return;
                }
                final String mergeSrt = mergeSrt(this.editExtracted.getText().toString(), this.editExternal.getText().toString());
                new Thread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        MainActivity.this.m19lambda$onActivityResult$28$combuddyappsvideotoolMainActivity(data, mergeSrt);
                    }
                }).start();
                return;
            }
            return;
        }
        try {
            getContentResolver().takePersistableUriPermission(data, 1);
        } catch (Exception unused) {
        }
        this.videoUri = data;
        Workflow.reset(this);
        this.tracks.clear();
        this.cueCount = 0;
        this.trackInfo = "";
        this.editExtracted.setText("");
        String queryDisplayName = queryDisplayName(data);
        int lastIndexOf = queryDisplayName.lastIndexOf(46);
        if (lastIndexOf > 0) {
            queryDisplayName = queryDisplayName.substring(0, lastIndexOf);
        }
        if (queryDisplayName.length() > 64) {
            queryDisplayName = queryDisplayName.substring(0, 64);
        }
        this.editFileName.setText(queryDisplayName + ".srt");
        StringBuilder sb = new StringBuilder(Localization.tr("Selected: "));
        sb.append(queryDisplayName(data));
        setStatus(sb.toString(), false);
        showScreen(0);
    }

    /* renamed from: lambda$onActivityResult$26$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m17lambda$onActivityResult$26$combuddyappsvideotoolMainActivity(final Uri uri) {
        try {
            final String readUriText = readUriText(uri);
            runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.this.m15lambda$onActivityResult$24$combuddyappsvideotoolMainActivity(readUriText, uri);
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.this.m16lambda$onActivityResult$25$combuddyappsvideotoolMainActivity(e);
                }
            });
        }
    }

    /* renamed from: lambda$onActivityResult$25$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m16lambda$onActivityResult$25$combuddyappsvideotoolMainActivity(Exception exc) {
        setStatus(Localization.tr("Could not read that file: ") + exc.getMessage(), true);
    }

    /* renamed from: lambda$onActivityResult$28$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m19lambda$onActivityResult$28$combuddyappsvideotoolMainActivity(Uri uri, String str) {
        final boolean writeTextToUri = writeTextToUri(uri, str);
        runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m18lambda$onActivityResult$27$combuddyappsvideotoolMainActivity(writeTextToUri);
            }
        });
    }

    /* renamed from: lambda$onActivityResult$27$com-buddyapps-videotool-MainActivity, reason: not valid java name */
    /* synthetic */ void m18lambda$onActivityResult$27$combuddyappsvideotoolMainActivity(boolean z) {
        if (!z) {
            setStatus(Localization.tr("Could not write the file. Try again."), true);
            return;
        }
        this.savedInfo = Localization.tr("Saved to the location you picked.");
        setStatus(Localization.tr("File saved."), false);
        showScreen(4);
    }

    private void setStatus(String str, boolean z) {
        if (this.tvBanner == null) {
            AppTheme.refresh(this);
            return;
        }
        if (str == null || str.isEmpty()) {
            this.tvBanner.setVisibility(8);
            AppTheme.refresh(this);
            return;
        }
        this.tvBanner.setVisibility(0);
        this.tvBanner.setText(str);
        this.tvBanner.setTextColor(z ? ERR_FG : INFO_FG);
        this.tvBanner.setBackground(bg(z ? ERR_BG : INFO_BG, 10, z ? ERR_BORDER : INFO_BORDER, 1));
        AppTheme.refresh(this);
    }

    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        currentFocus.clearFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class TrackOption {
        boolean bitmap;
        boolean fromMkv = false;
        int index;
        String lang;
        String mime;
        String name;
        long sampleCount;
        long trackNumber;

        String title() {
            return TrackLabels.title(this.trackNumber, this.index, this.lang, this.name, this.bitmap);
        }

        TrackOption(int i, String str, String str2, String str3, boolean z, long j) {
            this.index = i;
            this.trackNumber = i + 1;
            this.lang = str == null ? "" : str;
            this.name = str2 == null ? "" : str2;
            this.mime = str3 == null ? "" : str3;
            this.bitmap = z;
            this.sampleCount = j;
        }

        String subtitle() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append(this.mime);
            if (this.sampleCount > 0) {
                str = "  ·  " + this.sampleCount + Localization.tr(" cues");
            } else {
                str = "";
            }
            sb.append(str);
            return sb.toString();
        }

        String fileSuffix() {
            String str;
            String replaceAll = this.lang.isEmpty() ? "und" : this.lang.replaceAll("[^A-Za-z0-9]", "_");
            if (this.name.isEmpty()) {
                str = "";
            } else {
                str = "." + this.name.replaceAll("[^A-Za-z0-9]+", "_");
            }
            long j = this.trackNumber;
            if (j <= 0) {
                j = this.index + 1;
            }
            return replaceAll + ".track" + j + str;
        }
    }

    private List<TrackOption> listTracks(Uri uri) throws Exception {
        String str;
        String str2;
        ArrayList arrayList = new ArrayList();
        if (isMatroska(uri)) {
            try {
                List<TrackOption> listTracksMkv = listTracksMkv(uri);
                if (!listTracksMkv.isEmpty()) {
                    return listTracksMkv;
                }
            } catch (Exception unused) {
            }
        }
        ParcelFileDescriptor openFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        MediaExtractor mediaExtractor = new MediaExtractor();
        if (openFileDescriptor != null) {
            try {
                mediaExtractor.setDataSource(openFileDescriptor.getFileDescriptor());
            } catch (Throwable th) {
                mediaExtractor.release();
                if (openFileDescriptor != null) {
                    try {
                        openFileDescriptor.close();
                    } catch (Exception unused2) {
                    }
                }
                throw th;
            }
        }
        for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
            String string = trackFormat.getString("mime");
            if (string != null && (string.startsWith("text/") || string.startsWith("application/"))) {
                try {
                    str = trackFormat.getString("language");
                } catch (Exception unused3) {
                    str = "";
                }
                try {
                    str2 = trackFormat.getString("title");
                } catch (Exception unused4) {
                    str2 = "";
                }
                TrackOption trackOption = new TrackOption(i, str, str2, string, !isTextMime(string), 0L);
                trackOption.fromMkv = false;
                arrayList.add(trackOption);
            }
        }
        mediaExtractor.release();
        if (openFileDescriptor != null) {
            try {
                openFileDescriptor.close();
            } catch (Exception unused5) {
            }
        }
        return arrayList;
    }

    private List<TrackOption> listTracksMkv(Uri uri) throws Exception {
        long[] readHeader;
        long j;
        long min;
        MainActivity mainActivity = this;
        ArrayList arrayList = new ArrayList();
        ByteSource openSource = openSource(uri);
        if (openSource != null && (readHeader = mainActivity.readHeader(openSource)) != null) {
            StringBuilder sb = new StringBuilder();
            TreeMap treeMap = new TreeMap();
            openSource.seek(openSource.position() + readHeader[1]);
            long[] readHeader2 = mainActivity.readHeader(openSource);
            if (readHeader2 != null && readHeader2[0] == ID_SEGMENT) {
                long position = openSource.position();
                long j2 = readHeader2[1];
                if (j2 < 0) {
                    min = openSource.size();
                    j = 0;
                } else {
                    j = 0;
                    min = Math.min(j2 + position, openSource.size());
                }
                while (position < min) {
                    openSource.seek(position);
                    long[] readHeader3 = mainActivity.readHeader(openSource);
                    if (readHeader3 == null || readHeader3[1] < j) {
                        break;
                    }
                    long position2 = openSource.position();
                    if (readHeader3[0] == ID_TRACKS) {
                        mainActivity.parseTracks(openSource, position2, readHeader3[1] + position2, treeMap, sb);
                    }
                    position = position2 + readHeader3[1];
                    mainActivity = this;
                }
                Iterator<Map.Entry<Long, TrackSub>> it = treeMap.entrySet().iterator();
                while (it.hasNext()) {
                    TrackSub value = it.next().getValue();
                    if (value.type == 17) {
                        String str = value.codecId == null ? "" : value.codecId;
                        TrackOption trackOption = new TrackOption(arrayList.size(), value.language == null ? "" : value.language, value.name != null ? value.name : "", str, value.bitmap || !(str.toUpperCase(Locale.US).contains("SRT") || str.toUpperCase(Locale.US).contains("ASS") || str.toUpperCase(Locale.US).contains("WEBVTT") || str.toUpperCase(Locale.US).contains("TEXT")), 0L);
                        trackOption.trackNumber = value.number;
                        trackOption.fromMkv = true;
                        arrayList.add(trackOption);
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class Extraction {
        String info;
        String srt;

        Extraction(String str, String str2) {
            this.srt = str;
            this.info = str2;
        }
    }

    private Extraction extractSrt(Uri uri) throws Exception {
        return extractSrt(uri, null);
    }

    private Extraction extractSrt(Uri uri, TrackOption trackOption) throws Exception {
        String extractWithMediaExtractor;
        String extractWithMkvParser;
        StringBuilder sb = new StringBuilder();
        if (!isMatroska(uri) ? ((extractWithMediaExtractor = extractWithMediaExtractor(uri, trackOption, sb)) == null || extractWithMediaExtractor.trim().isEmpty()) && (extractWithMkvParser = extractWithMkvParser(uri, trackOption, sb)) != null && !extractWithMkvParser.trim().isEmpty() : ((extractWithMediaExtractor = extractWithMkvParser(uri, trackOption, sb)) == null || extractWithMediaExtractor.trim().isEmpty()) && (extractWithMkvParser = extractWithMediaExtractor(uri, trackOption, sb)) != null && !extractWithMkvParser.trim().isEmpty()) {
            extractWithMediaExtractor = extractWithMkvParser;
        }
        return new Extraction(extractWithMediaExtractor, sb.toString().trim());
    }

    private boolean isMatroska(Uri uri) {
        try {
            InputStream openInputStream = getContentResolver().openInputStream(uri);
            try {
                if (openInputStream == null) {
                    return false;
                }
                byte[] bArr = new byte[4];
                if (openInputStream.read(bArr) != 4) {
                    return false;
                }
                return (bArr[0] & 255) == 26 && (bArr[1] & 255) == 69 && (bArr[2] & 255) == 223 && (bArr[3] & 255) == 163;
            } finally {
                if (openInputStream != null) {
                    openInputStream.close();
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private String extractWithMediaExtractor(Uri uri, TrackOption trackOption, StringBuilder sb) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        ParcelFileDescriptor openFileDescriptor = null;
        try {
            openFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            if (openFileDescriptor == null) {
                return null;
            }
            mediaExtractor.setDataSource(openFileDescriptor.getFileDescriptor());
            StringBuilder sb2 = new StringBuilder();
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
                String string = mediaExtractor.getTrackFormat(i).getString("mime");
                sb2.append("track ").append(i).append(": ").append(string).append("; ");
                if (isTextMime(string)) {
                    arrayList.add(Integer.valueOf(i));
                }
            }
            sb.append("MediaExtractor: ").append((CharSequence) sb2).append("\n");
            if (trackOption != null && !trackOption.mime.isEmpty()) {
                if (!trackOption.fromMkv && arrayList.contains(Integer.valueOf(trackOption.index))) {
                    ArrayList<Integer> arrayList2 = new ArrayList<>();
                    arrayList2.add(Integer.valueOf(trackOption.index));
                    arrayList = arrayList2;
                } else {
                    ArrayList<Integer> arrayList3 = new ArrayList<>();
                    ArrayList<Integer> arrayList4 = new ArrayList<>();
                    ArrayList<Integer> arrayList5 = new ArrayList<>();
                    for (Integer num : arrayList) {
                        MediaFormat trackFormat = mediaExtractor.getTrackFormat(num.intValue());
                        String string2 = trackFormat.getString("mime");
                        if (string2 != null && string2.equals(trackOption.mime)) {
                            arrayList5.add(num);
                            String str = "";
                            try {
                                str = trackFormat.getString("language");
                            } catch (Exception unused) {
                                str = "";
                            }
                            String str2 = "";
                            try {
                                str2 = trackFormat.getString("title");
                            } catch (Exception unused2) {
                                str2 = "";
                            }
                            if (str == null) {
                                str = "";
                            }
                            if (str2 == null) {
                                str2 = "";
                            }
                            boolean isEmpty = trackOption.lang.isEmpty() || trackOption.lang.equals(str);
                            boolean isEmpty2 = trackOption.name.isEmpty() || trackOption.name.equals(str2);
                            if (isEmpty && isEmpty2) {
                                arrayList3.add(num);
                            }
                            if (isEmpty) {
                                arrayList4.add(num);
                            }
                        }
                    }
                    if (!arrayList3.isEmpty()) {
                        arrayList = arrayList3;
                    } else if (!arrayList4.isEmpty()) {
                        arrayList = arrayList4;
                    } else if (!arrayList5.isEmpty()) {
                        arrayList = arrayList5;
                    }
                }
            }
            for (Integer num2 : arrayList) {
                int intValue = num2.intValue();
                mediaExtractor.selectTrack(intValue);
                mediaExtractor.seekTo(0L, 0);
                ArrayList<Cue> arrayList6 = new ArrayList<>();
                ArrayList<long[]> arrayList7 = new ArrayList<>();
                ArrayList<String> arrayList8 = new ArrayList<>();
                ByteBuffer allocate = ByteBuffer.allocate(1048576);
                while (true) {
                    int readSampleData = mediaExtractor.readSampleData(allocate, 0);
                    if (readSampleData < 0) {
                        break;
                    }
                    long sampleTime = mediaExtractor.getSampleTime();
                    byte[] bArr = new byte[readSampleData];
                    allocate.rewind();
                    allocate.get(bArr);
                    String decodeSample = decodeSample(bArr);
                    if (decodeSample != null && !decodeSample.isEmpty()) {
                        arrayList7.add(new long[]{sampleTime / 1000});
                        arrayList8.add(decodeSample);
                    }
                    if (!mediaExtractor.advance()) {
                        break;
                    }
                }
                mediaExtractor.unselectTrack(intValue);
                if (arrayList8.isEmpty()) {
                    continue;
                }
                if (!looksLikeText(arrayList8)) {
                    sb.append("track ").append(intValue).append(": bitmap subtitles (PGS/VobSub), skipped\n");
                    continue;
                }
                for (int i2 = 0; i2 < arrayList8.size(); i2++) {
                    String str3 = arrayList8.get(i2);
                    if (str3.contains("Dialogue:")) {
                        for (String str4 : str3.split("\\r?\\n")) {
                            Cue parseAssLine = parseAssLine(str4.trim());
                            if (parseAssLine != null) {
                                arrayList6.add(parseAssLine);
                            }
                        }
                    } else if (str3.contains("-->")) {
                        for (String str5 : str3.split("\\r?\\n\\s*\\r?\\n")) {
                            Cue parseSrtBlock = parseSrtBlock(str5.trim());
                            if (parseSrtBlock != null) {
                                arrayList6.add(parseSrtBlock);
                            }
                        }
                    } else {
                        long j = arrayList7.get(i2)[0];
                        long j2 = i2 + 1 < arrayList7.size() ? arrayList7.get(i2 + 1)[0] : j + 3000;
                        if (j2 <= j) {
                            j2 = j + 2000;
                        }
                        if (j2 - j > 8000) {
                            j2 = j + 8000;
                        }
                        arrayList6.add(new Cue(j, j2, str3));
                    }
                }
                if (arrayList6.isEmpty()) {
                    continue;
                }
                sb.append("MediaExtractor: used track ").append(intValue).append("\n");
                Collections.sort(arrayList6, new Comparator<Cue>() { // from class: com.bkggds.videotool.MainActivity.34
                    @Override // java.util.Comparator
                    public int compare(Cue cue, Cue cue2) {
                        return Long.compare(cue.start, cue2.start);
                    }
                });
                return buildSrt(dedupe(arrayList6));
            }
            sb.append("MediaExtractor: no readable text subtitle track\n");
            return null;
        } catch (Exception e) {
            sb.append("MediaExtractor failed: ").append(e.getMessage()).append("\n");
            return null;
        } finally {
            try {
                mediaExtractor.release();
            } catch (Exception unused3) {
            }
            if (openFileDescriptor != null) {
                try {
                    openFileDescriptor.close();
                } catch (Exception unused4) {
                }
            }
        }
    }

    private boolean isTextMime(String str) {
        if (str == null) {
            return false;
        }
        String lowerCase = str.toLowerCase(Locale.US);
        return lowerCase.startsWith("text/") || lowerCase.contains("subrip") || lowerCase.contains("subtitle") || lowerCase.contains("ssa") || lowerCase.contains("ass") || lowerCase.contains("vtt") || lowerCase.contains("tx3g") || lowerCase.contains("mov_text") || lowerCase.contains("sami");
    }

    private String decodeSample(byte[] bArr) {
        int i;
        int i2 = 0;
        if (bArr.length > 2 && (i = ((bArr[0] & 255) << 8) | (bArr[1] & 255)) > 0 && i == bArr.length - 2) {
            i2 = 2;
        }
        return new String(bArr, i2, bArr.length - i2, StandardCharsets.UTF_8).replace("\u0000", "").trim();
    }

    private boolean looksLikeText(List<String> list) {
        int i = 0;
        int i2 = 0;
        for (String str : list) {
            i += str.length();
            for (int i3 = 0; i3 < str.length(); i3++) {
                char charAt = str.charAt(i3);
                if (charAt == '\n' || charAt == '\r' || charAt == '\t' || (charAt >= ' ' && charAt != 127)) {
                    i2++;
                }
            }
        }
        return i == 0 || ((double) i2) / ((double) i) > 0.9d;
    }

    private String extractWithMkvParser(Uri uri, StringBuilder sb) {
        return extractWithMkvParser(uri, null, sb);
    }

    private String extractWithMkvParser(Uri uri, TrackOption trackOption, StringBuilder sb) {
        try {
            ParcelFileDescriptor openFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            try {
                if (openFileDescriptor == null) {
                    return null;
                }
                long statSize = openFileDescriptor.getStatSize();
                FileInputStream fileInputStream = new FileInputStream(openFileDescriptor.getFileDescriptor());
                try {
                    FileChannel channel = fileInputStream.getChannel();
                    long size = statSize > 0 ? statSize : channel.size();
                    ByteSource byteSource = new ByteSource(channel, size);
                    long[] readHeader = readHeader(byteSource);
                    if (readHeader == null || readHeader[0] != ID_EBML) {
                        sb.append("MKV: not an EBML/Matroska file\n");
                        return null;
                    }
                    if (readHeader[1] > 0) {
                        byteSource.seek(byteSource.position() + readHeader[1]);
                    }
                    long segmentStart = -1;
                    long segmentEnd = size;
                    while (byteSource.position() < size) {
                        long[] readHeader2 = readHeader(byteSource);
                        if (readHeader2 == null) {
                            break;
                        }
                        if (readHeader2[0] == ID_SEGMENT) {
                            long position = byteSource.position();
                            segmentStart = position;
                            segmentEnd = readHeader2[1] < 0 ? size : position + readHeader2[1];
                            break;
                        }
                        if (readHeader2[1] < 0) {
                            break;
                        }
                        byteSource.seek(byteSource.position() + readHeader2[1]);
                    }
                    if (segmentStart < 0) {
                        sb.append("MKV: no Segment element\n");
                        return null;
                    }
                    long min = Math.min(segmentEnd, size);
                    TreeMap<Long, TrackSub> treeMap = new TreeMap<>();
                    StringBuilder sb2 = new StringBuilder();
                    long j = 1000000;
                    long j2 = segmentStart;
                    while (j2 < min) {
                        byteSource.seek(j2);
                        long[] readHeader3 = readHeader(byteSource);
                        if (readHeader3 == null) {
                            break;
                        }
                        long position2 = byteSource.position();
                        long j3 = readHeader3[1];
                        if (readHeader3[0] == ID_INFO && j3 >= 0) {
                            j = parseInfoTsScale(byteSource, position2, j3, j);
                        } else if (readHeader3[0] == ID_TRACKS && j3 >= 0) {
                            parseTracks(byteSource, position2, j3, treeMap, sb2);
                        }
                        if (j3 < 0) {
                            break;
                        }
                        j2 = position2 + j3;
                    }
                    sb.append("MKV tracks: ").append(sb2.length() == 0 ? "none" : sb2.toString()).append("\n");
                    if (treeMap.isEmpty()) {
                        sb.append("MKV: no subtitle track in Tracks element\n");
                        return null;
                    }
                    ArrayList<RawCue> arrayList = new ArrayList<>();
                    long j4 = segmentStart;
                    while (j4 < min) {
                        byteSource.seek(j4);
                        long[] readHeader4 = readHeader(byteSource);
                        if (readHeader4 == null) {
                            break;
                        }
                        long position3 = byteSource.position();
                        long j5 = readHeader4[1];
                        if (readHeader4[0] == ID_CLUSTER) {
                            parseCluster(byteSource, position3, j5 < 0 ? min : Math.min(position3 + j5, min), j, treeMap, arrayList);
                            if (j5 < 0) {
                                break;
                            }
                        }
                        if (j5 < 0) {
                            break;
                        }
                        j4 = position3 + j5;
                    }
                    if (arrayList.isEmpty()) {
                        sb.append("MKV: subtitle track(s) found but no subtitle blocks could be read\n");
                        return null;
                    }
                    sb.append("MKV: read ").append(arrayList.size()).append(" subtitle blocks\n");
                    return buildSrtFromRaw(arrayList, treeMap, trackOption);
                } finally {
                    try {
                        fileInputStream.close();
                    } catch (Exception unused) {
                    }
                }
            } finally {
                if (openFileDescriptor != null) {
                    try {
                        openFileDescriptor.close();
                    } catch (Exception unused2) {
                    }
                }
            }
        } catch (Exception e) {
            sb.append("MKV parser failed: ").append(e.getMessage()).append("\n");
            return null;
        }
    }

    private static class TrackSub {
        boolean bitmap;
        String codecId;
        int compAlgo;
        byte[] compSettings;
        String language;
        String languageIetf;
        String name;
        long number;
        long type;

        private TrackSub() {
            this.codecId = "";
            this.name = "";
            this.language = "";
            this.compAlgo = -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class RawCue {
        long endMs;
        long startMs;
        String text;
        long track;

        private RawCue() {
        }
    }

    private long parseInfoTsScale(ByteSource byteSource, long j, long j2, long j3) throws IOException {
        long j4 = j2 + j;
        while (j < j4) {
            byteSource.seek(j);
            long[] readHeader = readHeader(byteSource);
            if (readHeader == null || readHeader[1] < 0) {
                break;
            }
            long position = byteSource.position();
            if (readHeader[0] == ID_TS_SCALE) {
                j3 = toLong(readBytes(byteSource, (int) readHeader[1]));
            }
            j = position + readHeader[1];
        }
        if (j3 <= 0) {
            return 1000000L;
        }
        return j3;
    }

    private void parseTracks(ByteSource byteSource, long j, long j2, Map<Long, TrackSub> map, StringBuilder sb) throws IOException {
        ByteSource byteSource2;
        long j3 = j2 + j;
        while (j < j3) {
            byteSource.seek(j);
            long[] readHeader = readHeader(byteSource);
            if (readHeader == null || readHeader[1] < 0) {
                return;
            }
            long position = byteSource.position();
            if (readHeader[0] == ID_TRACK_ENTRY) {
                byteSource2 = byteSource;
                TrackSub parseTrackEntry = parseTrackEntry(byteSource2, position, position + readHeader[1]);
                if (parseTrackEntry != null) {
                    sb.append("#");
                    sb.append(parseTrackEntry.number);
                    sb.append(" type=");
                    sb.append(parseTrackEntry.type);
                    sb.append(" codec=");
                    sb.append(parseTrackEntry.codecId);
                    sb.append("; ");
                    if (parseTrackEntry.codecId.contains("PGS") || parseTrackEntry.codecId.contains("VOBSUB") || parseTrackEntry.codecId.contains("TEXTST")) {
                        parseTrackEntry.bitmap = true;
                    }
                    map.put(Long.valueOf(parseTrackEntry.number), parseTrackEntry);
                }
            } else {
                byteSource2 = byteSource;
            }
            j = position + readHeader[1];
            byteSource = byteSource2;
        }
    }

    private TrackSub parseTrackEntry(ByteSource byteSource, long j, long j2) throws IOException {
        TrackSub trackSub = new TrackSub();
        long j3 = j;
        while (j3 < j2) {
            byteSource.seek(j3);
            long[] readHeader = readHeader(byteSource);
            if (readHeader == null || readHeader[1] < 0) {
                break;
            }
            long position = byteSource.position();
            long j4 = readHeader[1];
            long j5 = position + j4;
            long j6 = readHeader[0];
            if (j6 == ID_TRACK_NUMBER) {
                trackSub.number = toLong(readBytes(byteSource, (int) j4));
            } else if (j6 == ID_TRACK_TYPE) {
                trackSub.type = toLong(readBytes(byteSource, (int) j4));
            } else if (j6 == ID_CODEC_ID) {
                trackSub.codecId = new String(readBytes(byteSource, (int) readHeader[1]), StandardCharsets.UTF_8).trim();
            } else if (j6 == ID_TRACK_NAME) {
                trackSub.name = new String(readBytes(byteSource, (int) readHeader[1]), StandardCharsets.UTF_8).trim();
            } else if (j6 == 2274717) {
                trackSub.languageIetf = new String(readBytes(byteSource, (int) readHeader[1]), StandardCharsets.UTF_8).trim();
            } else if (j6 == ID_LANGUAGE) {
                trackSub.language = new String(readBytes(byteSource, (int) readHeader[1]), StandardCharsets.UTF_8).trim();
            } else if (j6 == ID_CONTENT_COMP) {
                while (position < j5) {
                    byteSource.seek(position);
                    long[] readHeader2 = readHeader(byteSource);
                    if (readHeader2 != null && readHeader2[1] >= 0) {
                        long position2 = byteSource.position();
                        long j7 = readHeader2[0];
                        if (j7 == ID_COMP_ALGO) {
                            trackSub.compAlgo = (int) toLong(readBytes(byteSource, (int) readHeader2[1]));
                        } else if (j7 == ID_COMP_SETTINGS) {
                            trackSub.compSettings = readBytes(byteSource, (int) readHeader2[1]);
                        }
                        position = position2 + readHeader2[1];
                    }
                }
            }
            j3 = j5;
        }
        if (trackSub.number <= 0) {
            return null;
        }
        String str = trackSub.languageIetf;
        if (str != null && !str.isEmpty()) {
            trackSub.language = str;
        }
        return trackSub;
    }

    private void parseCluster(ByteSource byteSource, long j, long j2, long j3, Map<Long, TrackSub> map, List<RawCue> list) throws IOException {
        long j4 = -1L;
        MainActivity mainActivity = this;
        ByteSource byteSource2 = byteSource;
        long j5 = j;
        long j6 = -1;
        while (j5 < j2) {
            byteSource2.seek(j5);
            long[] readHeader = readHeader(byteSource);
            if (readHeader == null || readHeader[1] < 0) {
                return;
            }
            long position = byteSource2.position();
            long min = Math.min(readHeader[1] + position, j2);
            long j7 = readHeader[0];
            if (j7 == ID_CLUSTER_TS) {
                j6 = mainActivity.toLong(mainActivity.readBytes(byteSource2, (int) readHeader[1]));
            } else {
                if (j7 != ID_SIMPLE_BLOCK) {
                    j4 = min;
                    if (j7 == ID_BLOCK_GROUP) {
                        long j8 = -1;
                        long j9 = -1;
                        long j10 = -1;
                        while (position < j4) {
                            byteSource2.seek(position);
                            long[] readHeader2 = readHeader(byteSource);
                            if (readHeader2 == null || readHeader2[1] < 0) {
                                break;
                            }
                            long position2 = byteSource2.position();
                            long j11 = readHeader2[0];
                            if (j11 == ID_BLOCK) {
                                j8 = position2 + readHeader2[1];
                                j9 = position2;
                            } else if (j11 == ID_BLOCK_DURATION) {
                                j10 = mainActivity.toLong(mainActivity.readBytes(byteSource2, (int) readHeader2[1]));
                            }
                            position = position2 + readHeader2[1];
                        }
                        if (j9 >= 0 && j6 >= 0) {
                            mainActivity.parseBlock(byteSource2, j9, j8, j6, j3, map, list, j10);
                        }
                    }
                } else if (j6 >= 0) {
                    mainActivity.parseBlock(byteSource2, position, min, j6, j3, map, list, -1L);
                    j4 = min;
                }
                mainActivity = this;
                byteSource2 = byteSource;
                j5 = j4;
            }
            j4 = min;
            mainActivity = this;
            byteSource2 = byteSource;
            j5 = j4;
        }
    }

    private void parseBlock(ByteSource byteSource, long j, long j2, long j3, long j4, Map<Long, TrackSub> map, List<RawCue> list, long j5) throws IOException {
        TrackSub trackSub;
        byte[] readBytes;
        byteSource.seek(j);
        long readVintValue = readVintValue(byteSource);
        if (readVintValue >= 0 && (trackSub = map.get(Long.valueOf(readVintValue))) != null && trackSub.type == 17 && !trackSub.bitmap) {
            byteSource.seek(j + vintLen(readVintValue));
            int read = byteSource.read();
            int read2 = byteSource.read();
            if (read < 0 || read2 < 0) {
                return;
            }
            short s = (short) (((read & 255) << 8) | (read2 & 255));
            int read3 = byteSource.read();
            if (read3 < 0) {
                return;
            }
            if (((read3 & 6) >> 1) == 0) {
                int position = (int) (j2 - byteSource.position());
                if (position <= 0) {
                    return;
                } else {
                    readBytes = readBytes(byteSource, position);
                }
            } else {
                int position2 = (int) (j2 - byteSource.position());
                if (position2 <= 0) {
                    return;
                } else {
                    readBytes = readBytes(byteSource, position2);
                }
            }
            if (readBytes.length == 0) {
                return;
            }
            try {
                String decodeSubPayload = decodeSubPayload(readBytes, trackSub);
                if (decodeSubPayload == null) {
                    return;
                }
                String trim = decodeSubPayload.trim();
                if (trim.isEmpty()) {
                    return;
                }
                if (trim.contains("Dialogue:")) {
                    for (String str : trim.split("\\r?\\n")) {
                        Cue parseAssLine = parseAssLine(str.trim());
                        if (parseAssLine != null && !parseAssLine.text.isEmpty()) {
                            RawCue rawCue = new RawCue();
                            rawCue.track = readVintValue;
                            rawCue.startMs = parseAssLine.start;
                            rawCue.endMs = parseAssLine.end;
                            rawCue.text = parseAssLine.text;
                            list.add(rawCue);
                        }
                    }
                    return;
                }
                RawCue rawCue2 = new RawCue();
                rawCue2.track = readVintValue;
                rawCue2.startMs = j3 + s;
                if (j5 > 0) {
                    rawCue2.endMs = rawCue2.startMs + ((j5 * j4) / 1000000);
                } else {
                    rawCue2.endMs = -1L;
                }
                rawCue2.text = cleanText(trim);
                if (rawCue2.text.isEmpty()) {
                    return;
                }
                list.add(rawCue2);
            } catch (Exception unused) {
            }
        }
    }

    private String decodeSubPayload(byte[] bArr, TrackSub trackSub) throws Exception {
        if (trackSub.compAlgo == 0) {
            Inflater inflater = new Inflater();
            try {
                inflater.setInput(bArr);
                byte[] bArr2 = new byte[Math.max(bArr.length * 8, 8192)];
                int inflate = inflater.inflate(bArr2);
                if (inflate > 0) {
                    byte[] bArr3 = new byte[inflate];
                    System.arraycopy(bArr2, 0, bArr3, 0, inflate);
                    inflater.end();
                    bArr = bArr3;
                } else {
                    inflater.end();
                    return null;
                }
            } catch (Throwable th) {
                inflater.end();
                throw th;
            }
        } else if (trackSub.compAlgo == 3 && trackSub.compSettings != null) {
            byte[] bArr4 = new byte[trackSub.compSettings.length + bArr.length];
            System.arraycopy(trackSub.compSettings, 0, bArr4, 0, trackSub.compSettings.length);
            System.arraycopy(bArr, 0, bArr4, trackSub.compSettings.length, bArr.length);
            bArr = bArr4;
        }
        String str = new String(bArr, StandardCharsets.UTF_8);
        return str.startsWith("\ufeff") ? str.substring(1) : str;
    }

    private String buildSrtFromRaw(List<RawCue> list, Map<Long, TrackSub> map, TrackOption trackOption) {
        List list2;
        HashMap hashMap = new HashMap();
        for (RawCue rawCue : list) {
            ((List) hashMap.computeIfAbsent(Long.valueOf(rawCue.track), new Function() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda22
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return MainActivity.lambda$buildSrtFromRaw$30((Long) obj);
                }
            })).add(rawCue);
        }
        if (trackOption != null) {
            list2 = trackOption.trackNumber > 0 ? (List) hashMap.get(Long.valueOf(trackOption.trackNumber)) : null;
            if (list2 == null) {
                Iterator it = hashMap.entrySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Map.Entry entry = (Map.Entry) it.next();
                    TrackSub trackSub = map.get(entry.getKey());
                    if (trackSub != null) {
                        String str = trackSub.language == null ? "" : trackSub.language;
                        String str2 = trackSub.name == null ? "" : trackSub.name;
                        if (str.equals(trackOption.lang) && str2.equals(trackOption.name)) {
                            list2 = (List) entry.getValue();
                            break;
                        }
                    }
                }
            }
            if (list2 == null) {
                Iterator it2 = hashMap.entrySet().iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    Map.Entry entry2 = (Map.Entry) it2.next();
                    TrackSub trackSub2 = map.get(entry2.getKey());
                    if (trackSub2 != null) {
                        String str3 = trackSub2.language == null ? "" : trackSub2.language;
                        if (!trackOption.lang.isEmpty() && str3.equals(trackOption.lang)) {
                            list2 = (List) entry2.getValue();
                            break;
                        }
                    }
                }
            }
        } else {
            list2 = null;
        }
        if (list2 == null) {
            for (Object item3 : hashMap.entrySet()) {
                Map.Entry entry3 = (Map.Entry) item3;
                if (list2 == null || ((List) entry3.getValue()).size() > list2.size()) {
                    list2 = (List) entry3.getValue();
                }
            }
        }
        if (list2 == null) {
            return null;
        }
        Collections.sort(list2, Comparator.comparingLong(new ToLongFunction() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda23
            @Override // java.util.function.ToLongFunction
            public final long applyAsLong(Object obj) {
                long j;
                j = ((MainActivity.RawCue) obj).startMs;
                return j;
            }
        }));
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list2.size(); i++) {
            RawCue rawCue2 = (RawCue) list2.get(i);
            long j = rawCue2.endMs;
            if (j <= rawCue2.startMs) {
                int i2 = i + 1;
                j = i2 < list2.size() ? ((RawCue) list2.get(i2)).startMs : rawCue2.startMs + 3000;
            }
            if (j <= rawCue2.startMs) {
                j = rawCue2.startMs + 1000;
            }
            if (j - rawCue2.startMs > 10000) {
                j = rawCue2.startMs + 10000;
            }
            arrayList.add(new Cue(rawCue2.startMs, j, rawCue2.text));
        }
        return buildSrt(dedupe(arrayList));
    }

    static /* synthetic */ List lambda$buildSrtFromRaw$30(Long l) {
        return new ArrayList();
    }

    private List<Cue> dedupe(List<Cue> list) {
        ArrayList arrayList = new ArrayList();
        String str = null;
        for (Cue cue : list) {
            if (cue.text != null) {
                String trim = cue.text.trim();
                if (!trim.isEmpty() && !trim.equals(str)) {
                    arrayList.add(new Cue(cue.start, cue.end, trim));
                    str = trim;
                }
            }
        }
        return arrayList;
    }

    private String cleanText(String str) {
        return SrtText.cleanAssPayload(str).replaceAll("\\{[^}]*\\}", "").replace("\\N", "\n").replace("\\n", "\n").replace("\\h", " ").replaceAll("<[^>]{1,40}>", "").trim();
    }

    private ByteSource openSource(Uri uri) throws IOException {
        ParcelFileDescriptor openFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        if (openFileDescriptor == null) {
            return null;
        }
        long statSize = openFileDescriptor.getStatSize();
        FileChannel channel = new FileInputStream(openFileDescriptor.getFileDescriptor()).getChannel();
        if (statSize <= 0) {
            statSize = channel.size();
        }
        return new ByteSource(channel, statSize);
    }

    private static class ByteSource {
        private final FileChannel ch;
        private final long size;
        private final byte[] buf = new byte[65536];
        private long bufStart = 0;
        private int bufLen = 0;
        private int bufOff = 0;
        private long pos = 0;

        ByteSource(FileChannel fileChannel, long j) {
            this.ch = fileChannel;
            this.size = j;
        }

        long position() {
            return this.pos;
        }

        long size() {
            return this.size;
        }

        void seek(long j) {
            if (j == this.pos) {
                return;
            }
            long j2 = this.bufStart;
            if (j >= j2 && j <= this.bufLen + j2) {
                this.bufOff = (int) (j - j2);
                this.pos = j;
            } else {
                this.pos = j;
                this.bufStart = j;
                this.bufLen = 0;
                this.bufOff = 0;
            }
        }

        int read() throws IOException {
            if (this.bufOff >= this.bufLen) {
                this.bufStart = this.pos;
                this.bufOff = 0;
                int read = this.ch.read(ByteBuffer.wrap(this.buf), this.pos);
                if (read <= 0) {
                    this.bufLen = 0;
                    return -1;
                }
                this.bufLen = read;
            }
            this.pos++;
            byte[] bArr = this.buf;
            int i = this.bufOff;
            this.bufOff = i + 1;
            return bArr[i] & 255;
        }

        void readFully(byte[] bArr, int i, int i2) throws IOException {
            int i3 = 0;
            while (i3 < i2) {
                if (this.bufOff >= this.bufLen) {
                    this.bufStart = this.pos;
                    this.bufOff = 0;
                    int read = this.ch.read(ByteBuffer.wrap(this.buf), this.pos);
                    if (read <= 0) {
                        throw new IOException("unexpected end of file");
                    }
                    this.bufLen = read;
                }
                int min = Math.min(this.bufLen - this.bufOff, i2 - i3);
                System.arraycopy(this.buf, this.bufOff, bArr, i + i3, min);
                this.bufOff += min;
                this.pos += min;
                i3 += min;
            }
        }
    }

    private long[] readHeader(ByteSource byteSource) throws IOException {
        int read = byteSource.read();
        if (read < 0) {
            return null;
        }
        int i = 128;
        int i2 = 128;
        int i3 = 1;
        while (i3 <= 8 && (read & i2) == 0) {
            i2 >>= 1;
            i3++;
        }
        if (i3 > 8) {
            return null;
        }
        long j = read;
        for (int i4 = 1; i4 < i3; i4++) {
            int read2 = byteSource.read();
            if (read2 < 0) {
                return null;
            }
            j = (j << 8) | read2;
        }
        int read3 = byteSource.read();
        if (read3 < 0) {
            return null;
        }
        int i5 = 1;
        while (i5 <= 8 && (read3 & i) == 0) {
            i >>= 1;
            i5++;
        }
        if (i5 > 8) {
            return null;
        }
        long j2 = read3 & (i - 1);
        for (int i6 = 1; i6 < i5; i6++) {
            int read4 = byteSource.read();
            if (read4 < 0) {
                return null;
            }
            j2 = (j2 << 8) | read4;
        }
        if (j2 == (1 << (i5 * 7)) - 1) {
            j2 = -1;
        }
        return new long[]{j, j2};
    }

    private long readVintValue(ByteSource byteSource) throws IOException {
        int read = byteSource.read();
        if (read < 0) {
            return -1L;
        }
        int i = 128;
        int i2 = 1;
        while (i2 <= 8 && (read & i) == 0) {
            i >>= 1;
            i2++;
        }
        if (i2 > 8) {
            return -1L;
        }
        long j = read & (i - 1);
        for (int i3 = 1; i3 < i2; i3++) {
            int read2 = byteSource.read();
            if (read2 < 0) {
                return -1L;
            }
            j = (j << 8) | read2;
        }
        return j;
    }

    private byte[] readBytes(ByteSource byteSource, int i) throws IOException {
        if (i < 0 || i > 67108864) {
            throw new IOException("bad length " + i);
        }
        byte[] bArr = new byte[i];
        byteSource.readFully(bArr, 0, i);
        return bArr;
    }

    private long toLong(byte[] bArr) {
        long j = 0;
        for (byte b : bArr) {
            j = (j << 8) | (b & 255);
        }
        return j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class Cue {
        long end;
        long start;
        String text;

        Cue(long j, long j2, String str) {
            this.start = j;
            this.end = j2;
            this.text = str;
        }
    }

    private Cue parseAssLine(String str) {
        if (!str.startsWith("Dialogue:")) {
            return null;
        }
        String[] split = str.substring(9).trim().split(",", 10);
        if (split.length < 10) {
            return null;
        }
        long parseAssTime = parseAssTime(split[1].trim());
        long parseAssTime2 = parseAssTime(split[2].trim());
        String cleanText = cleanText(split[9]);
        if (cleanText.isEmpty()) {
            return null;
        }
        return new Cue(parseAssTime, parseAssTime2, cleanText);
    }

    private Cue parseSrtBlock(String str) {
        if (!str.contains("-->")) {
            return null;
        }
        String[] split = str.split("\\r?\\n");
        int i = 0;
        while (true) {
            if (i >= split.length) {
                i = -1;
                break;
            }
            if (split[i].contains("-->")) {
                break;
            }
            i++;
        }
        if (i < 0) {
            return null;
        }
        String[] split2 = split[i].split("-->");
        long parseSrtTime = parseSrtTime(split2[0].trim());
        long parseSrtTime2 = parseSrtTime(split2[1].trim().split("\\s")[0]);
        StringBuilder sb = new StringBuilder();
        for (int i2 = i + 1; i2 < split.length; i2++) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(split[i2]);
        }
        String trim = sb.toString().trim();
        if (trim.isEmpty()) {
            return null;
        }
        return new Cue(parseSrtTime, parseSrtTime2, trim);
    }

    private List<Cue> parseSrtList(String str) {
        Cue parseSrtBlock;
        ArrayList arrayList = new ArrayList();
        for (String str2 : str.replace("\r\n", "\n").replace((char) 65279, ' ').split("\\n\\s*\\n")) {
            String trim = str2.trim();
            if (!trim.isEmpty() && (parseSrtBlock = parseSrtBlock(trim)) != null) {
                arrayList.add(parseSrtBlock);
            }
        }
        return arrayList;
    }

    private String buildSrt(List<Cue> list) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < list.size()) {
            Cue cue = list.get(i);
            i++;
            sb.append(i);
            sb.append("\n");
            sb.append(fmt(cue.start));
            sb.append(" --> ");
            sb.append(fmt(cue.end));
            sb.append("\n");
            sb.append(cue.text);
            sb.append("\n\n");
        }
        return sb.toString();
    }

    private int countCues(String str) {
        return parseSrtList(str).size();
    }

    private long parseAssTime(String str) {
        long parseLong;
        long j = 0;
        try {
            String[] split = str.split(":");
            if (split.length < 3) {
                return 0L;
            }
            long parseLong2 = Long.parseLong(split[0].trim());
            long parseLong3 = Long.parseLong(split[1].trim());
            String trim = split[2].trim();
            if (trim.contains(".")) {
                String[] split2 = trim.split("\\.");
                parseLong = Long.parseLong(split2[0]);
                String str2 = split2[1];
                if (str2.length() == 1) {
                    str2 = str2 + "0";
                }
                if (str2.length() > 2) {
                    str2 = str2.substring(0, 2);
                }
                j = Long.parseLong(str2);
            } else {
                parseLong = Long.parseLong(trim);
            }
            return (((parseLong2 * 3600) + (parseLong3 * 60) + parseLong) * 1000) + (j * 10);
        } catch (Exception unused) {
            return 0L;
        }
    }

    private long parseSrtTime(String str) {
        long parseLong;
        long j = 0;
        try {
            String[] split = str.replace(".", ",").split(":");
            if (split.length < 3) {
                return 0L;
            }
            long parseLong2 = Long.parseLong(split[0].trim());
            long parseLong3 = Long.parseLong(split[1].trim());
            String trim = split[2].trim();
            if (trim.contains(",")) {
                String[] split2 = trim.split(",");
                parseLong = Long.parseLong(split2[0]);
                String str2 = split2[1];
                while (str2.length() < 3) {
                    str2 = str2 + "0";
                }
                if (str2.length() > 3) {
                    str2 = str2.substring(0, 3);
                }
                j = Long.parseLong(str2);
            } else {
                parseLong = Long.parseLong(trim);
            }
            return (((parseLong2 * 3600) + (parseLong3 * 60) + parseLong) * 1000) + j;
        } catch (Exception unused) {
            return 0L;
        }
    }

    private String fmt(long j) {
        long j2 = j / 1000;
        return String.format(Locale.US, "%02d:%02d:%02d,%03d", Long.valueOf(j2 / 3600), Long.valueOf((j2 % 3600) / 60), Long.valueOf(j2 % 60), Long.valueOf(j % 1000));
    }

    private String mergeSrt(String str, String str2) {
        if (str2 == null || str2.trim().isEmpty()) {
            return str;
        }
        if (!str2.contains("-->")) {
            List<Cue> parseSrtList = parseSrtList(str);
            ArrayList arrayList = new ArrayList();
            for (Cue cue : parseSrtList) {
                arrayList.add(new Cue(cue.start, cue.end, (cue.text + "\n" + str2.trim()).trim()));
            }
            if (!arrayList.isEmpty()) {
                parseSrtList = arrayList;
            }
            return buildSrt(parseSrtList);
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.addAll(parseSrtList(str));
        arrayList2.addAll(parseSrtList(str2));
        Collections.sort(arrayList2, Comparator.comparingLong(new ToLongFunction() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda41
            @Override // java.util.function.ToLongFunction
            public final long applyAsLong(Object obj) {
                long j;
                j = ((MainActivity.Cue) obj).start;
                return j;
            }
        }));
        return buildSrt(arrayList2);
    }

    private Uri saveSrt(String str, String str2) {
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                ContentResolver contentResolver = getContentResolver();
                Uri findDownloadsFile = findDownloadsFile(str2);
                if (findDownloadsFile != null) {
                    OutputStream openOutputStream = contentResolver.openOutputStream(findDownloadsFile, "wt");
                    if (openOutputStream != null) {
                        try {
                            openOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
                        } finally {
                        }
                    }
                    if (openOutputStream != null) {
                        openOutputStream.close();
                    }
                    return findDownloadsFile;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str2);
                contentValues.put("mime_type", "application/x-subrip");
                contentValues.put("relative_path", Environment.DIRECTORY_DOWNLOADS);
                contentValues.put("is_pending", (Integer) 1);
                Uri insert = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (insert == null) {
                    return null;
                }
                OutputStream openOutputStream2 = contentResolver.openOutputStream(insert);
                if (openOutputStream2 != null) {
                    try {
                        openOutputStream2.write(str.getBytes(StandardCharsets.UTF_8));
                    } finally {
                    }
                }
                if (openOutputStream2 != null) {
                    openOutputStream2.close();
                }
                contentValues.clear();
                contentValues.put("is_pending", (Integer) 0);
                contentResolver.update(insert, contentValues, null, null);
                return insert;
            }
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            externalStoragePublicDirectory.mkdirs();
            File file = new File(externalStoragePublicDirectory, str2);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                fileOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.close();
                return Uri.fromFile(file);
            } finally {
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri findDownloadsFile(String str) {
        try {
            Cursor query = getContentResolver().query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_display_name=? AND relative_path=?", new String[]{str, Environment.DIRECTORY_DOWNLOADS + "/"}, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        Uri withAppendedId = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, query.getLong(0));
                        if (query != null) {
                            query.close();
                        }
                        return withAppendedId;
                    }
                } finally {
                }
            }
            if (query == null) {
                return null;
            }
            query.close();
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    private boolean writeTextToUri(Uri uri, String str) {
        try {
            OutputStream openOutputStream = getContentResolver().openOutputStream(uri);
            if (openOutputStream == null) {
                if (openOutputStream != null) {
                    openOutputStream.close();
                }
                return false;
            }
            try {
                openOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
                if (openOutputStream != null) {
                    openOutputStream.close();
                }
                return true;
            } finally {
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String queryDisplayName(Uri uri) {
        Uri uri2;
        int columnIndex;
        try {
            uri2 = uri;
            try {
                Cursor query = getContentResolver().query(uri2, null, null, null, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst() && (columnIndex = query.getColumnIndex("_display_name")) >= 0) {
                            String string = query.getString(columnIndex);
                            if (query != null) {
                                query.close();
                            }
                            return string;
                        }
                    } finally {
                    }
                }
                if (query != null) {
                    query.close();
                }
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            uri2 = uri;
        }
        return uri2.getLastPathSegment();
    }

    private int dp(int i) {
        return (int) (i * getResources().getDisplayMetrics().density);
    }

    private String appVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception unused) {
            return "2.1";
        }
    }

    private GradientDrawable bg(int i, int i2, int i3, int i4) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setColor(i);
        gradientDrawable.setCornerRadius(dp(i2));
        if (i4 > 0) {
            gradientDrawable.setStroke(dp(i4), i3);
        }
        return gradientDrawable;
    }

    private Drawable ripple(Drawable drawable) {
        return new RippleDrawable(ColorStateList.valueOf(437196586), drawable, null);
    }

    private TextView text(String str, int i, int i2, boolean z) {
        String tr = Localization.tr(str);
        TextView textView = new TextView(this);
        textView.setText(tr);
        textView.setTextSize(i);
        textView.setTextColor(i2);
        if (z) {
            textView.setTypeface(Typeface.create("sans-serif-medium", 0));
        }
        return textView;
    }

    private TextView muted(String str) {
        TextView text = text(str, 12, MUTED, false);
        text.setLineSpacing(dp(2), 1.0f);
        return text;
    }

    private TextView valueText(String str) {
        TextView text = text(str, 14, -15788246, false);
        text.setLineSpacing(dp(2), 1.0f);
        return text;
    }

    private TextView fieldLabel(String str) {
        TextView text = text(str.toUpperCase(Locale.US), 10, MUTED, true);
        text.setLetterSpacing(0.08f);
        return text;
    }

    private LinearLayout column() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        return linearLayout;
    }

    private LinearLayout card(String str, String str2) {
        LinearLayout column = column();
        column.setBackground(bg(-1, 14, BORDER, 1));
        column.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(12);
        column.setLayoutParams(layoutParams);
        column.addView(text(str, 15, -15788246, true));
        if (str2 != null && !str2.isEmpty()) {
            View muted = muted(str2);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
            layoutParams2.topMargin = dp(4);
            layoutParams2.bottomMargin = dp(12);
            column.addView(muted, layoutParams2);
            return column;
        }
        space(column, 10);
        return column;
    }

    private TextView buttonPrimary(String str, final Runnable runnable) {
        TextView text = text(str, 14, -1, true);
        text.setGravity(17);
        text.setBackground(ripple(bg(-15788246, 10, 0, 0)));
        text.setPadding(dp(16), 0, dp(16), 0);
        text.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(46)));
        text.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                runnable.run();
            }
        });
        EditorTools.configureButton(text);
        return text;
    }

    private TextView buttonOutline(String str, final Runnable runnable) {
        TextView text = text(str, 14, -15788246, true);
        text.setGravity(17);
        text.setBackground(ripple(bg(-1, 10, BORDER, 1)));
        text.setPadding(dp(16), 0, dp(16), 0);
        text.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(46)));
        text.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.MainActivity$$ExternalSyntheticLambda26
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                runnable.run();
            }
        });
        EditorTools.configureButton(text);
        return text;
    }

    private TextView buttonGhost(String str) {
        TextView text = text(str, 14, MUTED, true);
        text.setGravity(17);
        text.setBackground(ripple(bg(MUTED_BG, 10, 0, 0)));
        text.setLayoutParams(new LinearLayout.LayoutParams(-2, dp(46)));
        EditorTools.configureButton(text);
        return text;
    }

    private void setButtonEnabled(TextView textView, boolean z, boolean z2) {
        textView.setEnabled(z);
        if (!z) {
            textView.setBackground(bg(MUTED_BG, 10, 0, 0));
            textView.setTextColor(-7035976);
            textView.setOnClickListener(null);
        } else {
            textView.setBackground(ripple(z2 ? bg(-15788246, 10, 0, 0) : bg(-1, 10, BORDER, 1)));
            textView.setTextColor(z2 ? -1 : -15788246);
        }
    }

    private EditText monoField(String str, int i) {
        SubtitleEditor subtitleEditor = new SubtitleEditor(this);
        subtitleEditor.setHint(str);
        subtitleEditor.setTextSize(14.0f);
        subtitleEditor.setTypeface(Typeface.MONOSPACE);
        subtitleEditor.setTextColor(-15788246);
        subtitleEditor.setHintTextColor(MUTED);
        subtitleEditor.setGravity(8388659);
        subtitleEditor.setInputType(655361);
        subtitleEditor.setBackground(ripple(bg(-1, 10, BORDER, 1)));
        subtitleEditor.setPadding(dp(12), dp(10), dp(12), dp(10));
        subtitleEditor.setLineSpacing(dp(2), 1.0f);
        if (i > 120) {
            i = 120;
        }
        subtitleEditor.setMinHeight(dp(i));
        return subtitleEditor;
    }

    private View note(String str) {
        TextView text = text(str, 12, MUTED, false);
        text.setLineSpacing(dp(2), 1.0f);
        text.setPadding(dp(12), dp(10), dp(12), dp(10));
        text.setBackground(bg(MUTED_BG, 10, BORDER, 1));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = dp(4);
        layoutParams.bottomMargin = dp(12);
        text.setLayoutParams(layoutParams);
        return text;
    }

    private TextView bullet(String str) {
        TextView text = text("•  " + str, 13, -13418155, false);
        text.setLineSpacing((float) dp(3), 1.0f);
        text.setPadding(0, dp(3), 0, dp(3));
        return text;
    }

    private void space(LinearLayout linearLayout, int i) {
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(i)));
        linearLayout.addView(view);
    }

    private View divider() {
        View view = new View(this);
        view.setBackgroundColor(BORDER);
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, 1));
        return view;
    }

    private void detach(View view) {
        ViewGroup viewGroup;
        if (view == null || (viewGroup = (ViewGroup) view.getParent()) == null) {
            return;
        }
        viewGroup.removeView(view);
    }

    private static class MenuIcon extends View {
        private int baseColor;
        private final Paint paint;

        MenuIcon(Context context, int i) {
            super(context);
            Paint paint = new Paint(1);
            this.paint = paint;
            float f = context.getResources().getDisplayMetrics().density;
            this.baseColor = i;
            paint.setColor(i);
            paint.setStrokeWidth(f * 1.7f);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.paint.setColor(AppTheme.iconColor(this.baseColor));
            float width = getWidth();
            float height = getHeight();
            float f = width * 0.08f;
            float f2 = width * 0.92f;
            float f3 = height * 0.28f;
            canvas.drawLine(f, f3, f2, f3, this.paint);
            float f4 = height * 0.52f;
            canvas.drawLine(f, f4, f2, f4, this.paint);
            float f5 = height * 0.76f;
            canvas.drawLine(f, f5, f2, f5, this.paint);
        }
    }
}
