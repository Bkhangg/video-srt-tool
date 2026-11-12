package com.bkggds.videotool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.VideoSize;
import androidx.media3.common.text.Cue;
import androidx.media3.common.text.CueGroup;
import androidx.media3.exoplayer.ExoPlayer;
import java.util.ArrayList;
import java.util.Locale;

/* Video player built on ExoPlayer: fullscreen, gestures, subtitle and audio track menus. */
public final class VideoPreview extends FrameLayout {
    static VideoPreview fullscreenPreview;
    private final Activity activity;
    private final AudioManager audio;
    private final PlayerIcon audioBtn;
    private final TextView bubble;
    private final PlayerIcon ccBtn;
    private final LinearLayout controls;
    private final Uri uri;
    private final TextView fileTitle;
    private final PlayerIcon fullBtn;
    private final FitVideo video;
    private final FrameLayout frame;
    private final Handler handler;
    private final PlayerIcon play;
    private final ExoPlayer player;
    private final PlayerIcon ratioBtn;
    private final SeekBar seek;
    private final TextView speedBtn;
    private final TextView status;
    private final TextView subtitle;
    private final TextView time;
    private final LinearLayout topBar;
    private final View touch;
    private final Runnable hide;
    private final Runnable hideBubble;
    private final Runnable update;
    private final GestureDetector detector;
    private boolean detached;
    private boolean moving;
    private boolean fullscreenOn;
    private boolean ready;
    private boolean seeking;
    private long duration;
    private int ratio;
    private float speed;
    private int gesture;
    private float downX;
    private float downY;
    private float startBrightness;
    private int startVolume;
    private long startPos;
    private long pendingSeek;
    private ViewGroup overlay;
    private ViewGroup originalParent;
    private ViewGroup.LayoutParams originalParams;
    private int originalIndex;

    public VideoPreview(Context context, Uri uri) {
        super(context);
        this.uri = uri;
        this.handler = new Handler();
        this.speed = 1.0f;
        this.ratio = 0;
        this.activity = findActivity(context);
        this.audio = activity == null ? null : (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        this.hide = new Runnable() { // from class: com.bkggds.videotool.VideoPreview.1
            @Override // java.lang.Runnable
            public void run() {
                if (VideoPreview.this.ready && VideoPreview.this.player.isPlaying() && !VideoPreview.this.seeking) {
                    VideoPreview.this.controls.setVisibility(8);
                    VideoPreview.this.topBar.setVisibility(8);
                }
            }
        };
        this.hideBubble = new Runnable() { // from class: com.bkggds.videotool.VideoPreview.2
            @Override // java.lang.Runnable
            public void run() {
                VideoPreview.this.bubble.setVisibility(8);
            }
        };
        this.update = new Runnable() { // from class: com.bkggds.videotool.VideoPreview.3
            @Override // java.lang.Runnable
            public void run() {
                if (VideoPreview.this.detached) {
                    return;
                }
                if (VideoPreview.this.ready) {
                    long currentPosition = VideoPreview.this.player.getCurrentPosition();
                    long dur = VideoPreview.this.player.getDuration();
                    if (dur != C.TIME_UNSET && dur > 0) {
                        VideoPreview.this.duration = dur;
                    }
                    if (!VideoPreview.this.seeking) {
                        VideoPreview.this.seek.setProgress((int) Math.min(currentPosition, 2147483647L));
                    }
                    VideoPreview.this.time.setText(String.valueOf(VideoPreview.stamp(currentPosition)) + " / " + VideoPreview.stamp(VideoPreview.this.duration));
                    boolean isPlaying = VideoPreview.this.player.isPlaying();
                    VideoPreview.this.play.setKind(isPlaying ? PlayerIcon.PAUSE : PlayerIcon.PLAY);
                    VideoPreview.this.play.setContentDescription(Localization.tr(isPlaying ? "Pause" : "Play"));
                }
                VideoPreview.this.handler.postDelayed(this, 500L);
            }
        };
        setBackgroundColor(0);
        this.frame = new FrameLayout(context);
        this.frame.setClipChildren(true);
        addView(this.frame, new FrameLayout.LayoutParams(-1, -1, 17));

        this.video = new FitVideo(context);
        this.video.setBackgroundColor(0);
        this.frame.addView(this.video, new FrameLayout.LayoutParams(-1, -1, 17));

        this.subtitle = new TextView(context);
        this.subtitle.setGravity(17);
        this.subtitle.setTextSize(16.0f);
        this.subtitle.setTextColor(-1);
        this.subtitle.setShadowLayer(4.0f, 0.0f, 1.0f, -16777216);
        this.subtitle.setPadding(dp(10), dp(4), dp(10), dp(4));
        this.subtitle.setVisibility(8);
        FrameLayout.LayoutParams subtitleParams = new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        subtitleParams.bottomMargin = dp(64);
        this.frame.addView(this.subtitle, subtitleParams);

        this.touch = new View(context);
        this.frame.addView(this.touch, new FrameLayout.LayoutParams(-1, -1));

        this.bubble = new TextView(context);
        this.bubble.setGravity(17);
        this.bubble.setTextSize(13.0f);
        this.bubble.setTextColor(-1);
        this.bubble.setBackground(shape(-1442840576, dp(18)));
        this.bubble.setPadding(dp(16), dp(8), dp(16), dp(8));
        this.bubble.setVisibility(8);
        FrameLayout.LayoutParams bubbleParams = new FrameLayout.LayoutParams(-2, -2, 17);
        this.frame.addView(this.bubble, bubbleParams);

        this.status = new TextView(context);
        this.status.setGravity(17);
        this.status.setTextSize(12.0f);
        this.status.setTextColor(-13418155);
        this.status.setPadding(dp(8), dp(8), dp(8), dp(8));
        this.status.setText(Localization.tr("Loading preview..."));
        this.frame.addView(this.status, new FrameLayout.LayoutParams(-1, -1));

        this.topBar = new LinearLayout(context);
        this.topBar.setOrientation(0);
        this.topBar.setGravity(16);
        this.topBar.setBackgroundColor(-1442840576);
        this.topBar.setPadding(dp(4), dp(2), dp(8), dp(2));
        PlayerIcon close = icon(PlayerIcon.BACK, Localization.tr("Close"));
        close.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoPreview.this.exitFullscreen();
            }
        });
        this.topBar.addView(close, new LinearLayout.LayoutParams(dp(46), dp(46)));
        this.fileTitle = new TextView(context);
        this.fileTitle.setTextSize(12.0f);
        this.fileTitle.setTextColor(-1);
        this.fileTitle.setSingleLine(true);
        this.fileTitle.setEllipsize(TextUtils.TruncateAt.END);
        this.fileTitle.setPadding(dp(4), 0, 0, 0);
        this.topBar.addView(this.fileTitle, new LinearLayout.LayoutParams(0, -2, 1.0f));
        this.topBar.setVisibility(8);
        this.frame.addView(this.topBar, new FrameLayout.LayoutParams(-1, dp(48), 48));

        this.controls = new LinearLayout(context);
        this.controls.setTag("previewControls");
        this.controls.setOrientation(1);
        this.controls.setBackgroundColor(-1442840576);
        this.controls.setPadding(dp(4), dp(2), dp(4), dp(2));
        this.controls.setVisibility(8);

        LinearLayout row1 = new LinearLayout(context);
        row1.setOrientation(0);
        row1.setGravity(16);
        this.seek = new SeekBar(context);
        this.seek.setPadding(dp(4), 0, dp(4), 0);
        this.seek.setContentDescription(Localization.tr("Seek video"));
        row1.addView(this.seek, new LinearLayout.LayoutParams(0, dp(36), 1.0f));
        this.time = new TextView(context);
        this.time.setTextSize(10.0f);
        this.time.setTextColor(-1);
        this.time.setPadding(dp(6), 0, dp(6), 0);
        this.time.setText("00:00 / 00:00");
        row1.addView(this.time, new LinearLayout.LayoutParams(-2, -2));
        this.controls.addView(row1, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout row2 = new LinearLayout(context);
        row2.setOrientation(0);
        row2.setGravity(16);
        this.play = icon(PlayerIcon.PLAY, Localization.tr("Play"));
        row2.addView(this.play, new LinearLayout.LayoutParams(0, dp(44), 1.0f));
        TextView back = button("-10", Localization.tr("Back 10 seconds"));
        back.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoPreview.this.seekBy(-10000L);
            }
        });
        row2.addView(back, new LinearLayout.LayoutParams(0, dp(44), 1.0f));
        TextView forward = button("+10", Localization.tr("Forward 10 seconds"));
        forward.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoPreview.this.seekBy(10000L);
            }
        });
        row2.addView(forward, new LinearLayout.LayoutParams(0, dp(44), 1.0f));
        this.speedBtn = button("1×", Localization.tr("Playback speed"));
        this.speedBtn.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoPreview.this.showSpeedMenu();
            }
        });
        row2.addView(this.speedBtn, new LinearLayout.LayoutParams(0, dp(44), 1.0f));
        this.audioBtn = icon(PlayerIcon.AUDIO, Localization.tr("Audio track"));
        this.audioBtn.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoPreview.this.showTrackMenu(C.TRACK_TYPE_AUDIO, Localization.tr("Audio track"));
            }
        });
        row2.addView(this.audioBtn, new LinearLayout.LayoutParams(0, dp(44), 1.0f));
        this.ccBtn = icon(PlayerIcon.SUBTITLE, Localization.tr("Subtitle track"));
        this.ccBtn.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoPreview.this.showTrackMenu(C.TRACK_TYPE_TEXT, Localization.tr("Subtitle track"));
            }
        });
        row2.addView(this.ccBtn, new LinearLayout.LayoutParams(0, dp(44), 1.0f));
        this.ratioBtn = icon(PlayerIcon.RATIO, Localization.tr("Aspect ratio"));
        this.ratioBtn.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoPreview.this.cycleRatio();
            }
        });
        row2.addView(this.ratioBtn, new LinearLayout.LayoutParams(0, dp(44), 1.0f));
        this.fullBtn = icon(PlayerIcon.FULLSCREEN, Localization.tr("Fullscreen"));
        this.fullBtn.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoPreview.this.toggleFullscreen();
            }
        });
        row2.addView(this.fullBtn, new LinearLayout.LayoutParams(0, dp(44), 1.0f));
        this.controls.addView(row2, new LinearLayout.LayoutParams(-1, -2));
        this.frame.addView(this.controls, new FrameLayout.LayoutParams(-1, -2, 80));

        this.play.setOnClickListener(new View.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!VideoPreview.this.ready) {
                    return;
                }
                if (VideoPreview.this.player.isPlaying()) {
                    VideoPreview.this.player.pause();
                } else {
                    VideoPreview.this.player.play();
                }
                VideoPreview.this.showControls();
            }
        });

        this.seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.bkggds.videotool.VideoPreview.13
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                VideoPreview.this.seeking = true;
                VideoPreview.this.handler.removeCallbacks(VideoPreview.this.hide);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z) {
                    VideoPreview.this.time.setText(String.valueOf(VideoPreview.stamp(i)) + " / " + VideoPreview.stamp(VideoPreview.this.duration));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (VideoPreview.this.ready) {
                    VideoPreview.this.player.seekTo(seekBar.getProgress());
                }
                VideoPreview.this.seeking = false;
                VideoPreview.this.showControls();
            }
        });

        this.detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: com.bkggds.videotool.VideoPreview.14
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                VideoPreview.this.toggleControls();
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTap(MotionEvent motionEvent) {
                if (!VideoPreview.this.ready) {
                    return true;
                }
                float x = motionEvent.getX();
                if (x < VideoPreview.this.getWidth() / 3.0f) {
                    VideoPreview.this.seekBy(-10000L);
                } else if (x > (VideoPreview.this.getWidth() * 2) / 3.0f) {
                    VideoPreview.this.seekBy(10000L);
                } else if (VideoPreview.this.player.isPlaying()) {
                    VideoPreview.this.player.pause();
                } else {
                    VideoPreview.this.player.play();
                }
                VideoPreview.this.showControls();
                return true;
            }
        });
        this.touch.setOnTouchListener(new View.OnTouchListener() { // from class: com.bkggds.videotool.VideoPreview.15
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                VideoPreview.this.detector.onTouchEvent(motionEvent);
                switch (motionEvent.getActionMasked()) {
                    case 0:
                        VideoPreview.this.downX = motionEvent.getX();
                        VideoPreview.this.downY = motionEvent.getY();
                        VideoPreview.this.gesture = 0;
                        VideoPreview.this.startPos = VideoPreview.this.ready ? VideoPreview.this.player.getCurrentPosition() : 0L;
                        VideoPreview.this.pendingSeek = VideoPreview.this.startPos;
                        VideoPreview.this.startVolume = VideoPreview.this.volume();
                        VideoPreview.this.startBrightness = VideoPreview.this.brightness();
                        break;
                    case 1:
                        if (VideoPreview.this.gesture == 1 && VideoPreview.this.ready) {
                            VideoPreview.this.player.seekTo(VideoPreview.this.pendingSeek);
                            VideoPreview.this.showControls();
                        }
                        VideoPreview.this.gesture = 0;
                        VideoPreview.this.handler.removeCallbacks(VideoPreview.this.hideBubble);
                        VideoPreview.this.handler.postDelayed(VideoPreview.this.hideBubble, 600L);
                        break;
                    case 2:
                        float x = motionEvent.getX() - VideoPreview.this.downX;
                        float y = motionEvent.getY() - VideoPreview.this.downY;
                        if (VideoPreview.this.gesture == 0 && (Math.abs(x) > VideoPreview.this.dp(18) || Math.abs(y) > VideoPreview.this.dp(18))) {
                            if (Math.abs(x) > Math.abs(y)) {
                                VideoPreview.this.gesture = 1;
                            } else if (motionEvent.getX() < VideoPreview.this.getWidth() / 2.0f) {
                                VideoPreview.this.gesture = 2;
                            } else {
                                VideoPreview.this.gesture = 3;
                            }
                            VideoPreview.this.showControls();
                        }
                        if (VideoPreview.this.gesture == 1 && VideoPreview.this.duration > 0) {
                            long delta = (long) ((x / VideoPreview.this.getWidth()) * VideoPreview.this.duration);
                            long target = Math.max(0L, Math.min(VideoPreview.this.duration, VideoPreview.this.startPos + delta));
                            VideoPreview.this.pendingSeek = target;
                            VideoPreview.this.toast(VideoPreview.stamp(target) + " / " + VideoPreview.stamp(VideoPreview.this.duration));
                        } else if (VideoPreview.this.gesture == 2) {
                            float value = VideoPreview.this.startBrightness - (y / VideoPreview.this.getHeight());
                            VideoPreview.this.setBrightness(value);
                            VideoPreview.this.toast(Localization.tr("Brightness") + " " + Math.round(VideoPreview.this.brightness() * 100.0f) + "%");
                        } else if (VideoPreview.this.gesture == 3) {
                            int max = VideoPreview.this.maxVolume();
                            if (max > 0) {
                                int value2 = (int) Math.round(VideoPreview.this.startVolume + ((-y) / VideoPreview.this.getHeight()) * max);
                                VideoPreview.this.setVolume(Math.max(0, Math.min(max, value2)));
                                VideoPreview.this.toast(Localization.tr("Volume") + " " + Math.round((VideoPreview.this.volume() / (float) max) * 100.0f) + "%");
                            }
                        }
                        break;
                    case 3:
                        VideoPreview.this.gesture = 0;
                        break;
                }
                return true;
            }
        });

        this.player = new ExoPlayer.Builder(context).build();
        this.player.setVideoSurfaceView(this.video);
        this.player.addListener(new Player.Listener() { // from class: com.bkggds.videotool.VideoPreview.16
            @Override // androidx.media3.common.Player.Listener
            public void onPlaybackStateChanged(int i) {
                if (i == Player.STATE_READY) {
                    VideoPreview.this.onReady();
                } else if (i == Player.STATE_ENDED) {
                    VideoPreview.this.showControls();
                }
            }

            @Override // androidx.media3.common.Player.Listener
            public void onIsPlayingChanged(boolean z) {
                VideoPreview.this.setKeepScreenOn(z);
                if (!z) {
                    VideoPreview.this.showControls();
                }
            }

            @Override // androidx.media3.common.Player.Listener
            public void onPlayerError(PlaybackException playbackException) {
                VideoPreview.this.fail();
            }

            @Override // androidx.media3.common.Player.Listener
            public void onVideoSizeChanged(VideoSize videoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    VideoPreview.this.video.videoWidth = videoSize.width;
                    VideoPreview.this.video.videoHeight = videoSize.height;
                    VideoPreview.this.video.requestLayout();
                }
            }

            @Override // androidx.media3.common.Player.Listener
            public void onCues(CueGroup cueGroup) {
                VideoPreview.this.renderCues(cueGroup);
            }
        });
        try {
            this.player.setMediaItem(MediaItem.fromUri(uri));
            this.player.prepare();
        } catch (Exception e) {
            fail();
        }
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReady() {
        if (detached) {
            return;
        }
        this.ready = true;
        this.duration = Math.max(0L, this.player.getDuration());
        this.seek.setMax((int) Math.max(1L, Math.min(this.duration, 2147483647L)));
        this.status.setVisibility(8);
        showControls();
        this.handler.removeCallbacks(this.update);
        this.handler.post(this.update);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderCues(CueGroup cueGroup) {
        StringBuilder sb = new StringBuilder();
        for (Cue cue : cueGroup.cues) {
            if (cue.text == null) {
                continue;
            }
            String line = cue.text.toString().trim();
            if (line.length() == 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(line);
        }
        if (sb.length() == 0) {
            this.subtitle.setText("");
            this.subtitle.setVisibility(8);
            return;
        }
        this.subtitle.setText(sb.toString());
        this.subtitle.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showControls() {
        if (this.detached) {
            return;
        }
        this.controls.setVisibility(0);
        if (this.fullscreenOn) {
            this.topBar.setVisibility(0);
        }
        this.handler.removeCallbacks(this.hide);
        if (this.ready && this.player.isPlaying()) {
            this.handler.postDelayed(this.hide, 3500L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleControls() {
        if (this.controls.getVisibility() == 0) {
            this.controls.setVisibility(8);
            this.topBar.setVisibility(8);
            this.handler.removeCallbacks(this.hide);
            return;
        }
        showControls();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toast(String str) {
        this.bubble.setText(str);
        this.bubble.setVisibility(0);
        this.handler.removeCallbacks(this.hideBubble);
        this.handler.postDelayed(this.hideBubble, 1200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void seekBy(long j) {
        if (!this.ready) {
            return;
        }
        long target = Math.max(0L, Math.min(this.duration, this.player.getCurrentPosition() + j));
        this.player.seekTo(target);
        toast((j >= 0 ? "+" : "") + (j / 1000) + "s  " + stamp(target));
        showControls();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSpeedMenu() {
        final float[] speeds = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f};
        String[] labels = new String[speeds.length];
        int checked = 2;
        for (int i = 0; i < speeds.length; i++) {
            labels[i] = speedLabel(speeds[i]);
            if (Math.abs(speeds[i] - this.speed) < 0.01f) {
                checked = i;
            }
        }
        new AlertDialog.Builder(AppTheme.context(getContext())).setTitle(Localization.tr("Playback speed")).setSingleChoiceItems(labels, checked, new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.17
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                VideoPreview.this.speed = speeds[i];
                VideoPreview.this.player.setPlaybackSpeed(speeds[i]);
                VideoPreview.this.speedBtn.setText(VideoPreview.speedLabel(speeds[i]));
                dialogInterface.dismiss();
                VideoPreview.this.showControls();
            }
        }).setNegativeButton(Localization.tr("Cancel"), (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cycleRatio() {
        this.ratio = (this.ratio + 1) % 3;
        this.video.mode = this.ratio;
        this.video.requestLayout();
        requestLayout();
        toast(Localization.tr(this.ratio == 0 ? "Fit" : this.ratio == 1 ? "Fill" : "Stretch"));
        showControls();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTrackMenu(final int trackType, String title) {
        Tracks tracks = this.player.getCurrentTracks();
        final ArrayList<String> labels = new ArrayList<>();
        final ArrayList<Tracks.Group> groups = new ArrayList<>();
        final ArrayList<Integer> indexes = new ArrayList<>();
        boolean text = trackType == C.TRACK_TYPE_TEXT;
        int checked = 0;
        if (text) {
            labels.add(Localization.tr("No subtitles"));
            groups.add(null);
            indexes.add(-1);
        }
        int count = 0;
        for (Tracks.Group group : tracks.getGroups()) {
            if (group.getType() != trackType) {
                continue;
            }
            for (int i = 0; i < group.length; i++) {
                Format format = group.getTrackFormat(i);
                count++;
                StringBuilder sb = new StringBuilder();
                sb.append(Localization.tr("Track")).append(' ').append(count);
                if (format.language != null && format.language.length() > 0) {
                    sb.append(" · ").append(Localization.languageLabel(format.language));
                } else if (!TextUtils.isEmpty(format.label)) {
                    sb.append(" · ").append(format.label);
                }
                labels.add(sb.toString());
                groups.add(group);
                indexes.add(i);
                if (group.isTrackSelected(i)) {
                    checked = labels.size() - 1;
                }
            }
        }
        if (count == 0) {
            toast(Localization.tr(text ? "No subtitles" : "No audio tracks"));
            return;
        }
        final int type = trackType;
        new AlertDialog.Builder(AppTheme.context(getContext())).setTitle(title).setSingleChoiceItems(labels.toArray(new String[0]), checked, new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.VideoPreview.18
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                VideoPreview.this.applyTrack(type, i, groups, indexes);
                dialogInterface.dismiss();
            }
        }).setNegativeButton(Localization.tr("Cancel"), (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyTrack(int trackType, int i, ArrayList<Tracks.Group> groups, ArrayList<Integer> indexes) {
        TrackSelectionParameters.Builder buildUpon = this.player.getTrackSelectionParameters().buildUpon();
        buildUpon.clearOverridesOfType(trackType);
        if (i <= 0 || groups.get(i) == null) {
            buildUpon.setTrackTypeDisabled(trackType, true);
            if (trackType == C.TRACK_TYPE_TEXT) {
                this.subtitle.setText("");
                this.subtitle.setVisibility(8);
            }
        } else {
            buildUpon.setTrackTypeDisabled(trackType, false);
            buildUpon.addOverride(new TrackSelectionOverride(groups.get(i).getMediaTrackGroup(), indexes.get(i).intValue()));
        }
        this.player.setTrackSelectionParameters(buildUpon.build());
        showControls();
    }

    public void setVideoTitle(String str) {
        this.fileTitle.setText(str == null ? "" : str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleFullscreen() {
        if (this.fullscreenOn) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    private void enterFullscreen() {
        ViewGroup parent;
        if (this.fullscreenOn || this.activity == null || (parent = (ViewGroup) getParent()) == null) {
            return;
        }
        this.moving = true;
        this.originalParent = parent;
        this.originalIndex = parent.indexOfChild(this);
        this.originalParams = getLayoutParams();
        parent.removeView(this);
        this.overlay = new FrameLayout(this.activity);
        this.overlay.setBackgroundColor(-16777216);
        this.overlay.addView(this, new FrameLayout.LayoutParams(-1, -1));
        ((ViewGroup) this.activity.getWindow().getDecorView()).addView(this.overlay, new ViewGroup.LayoutParams(-1, -1));
        this.fullscreenOn = true;
        fullscreenPreview = this;
        this.moving = false;
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        immersive(true);
        this.fullBtn.setKind(PlayerIcon.EXIT_FULLSCREEN);
        this.fullBtn.setContentDescription(Localization.tr("Exit fullscreen"));
        requestLayout();
        showControls();
    }

    public void exitFullscreen() {
        if (!this.fullscreenOn) {
            return;
        }
        this.moving = true;
        if (this.overlay != null) {
            this.overlay.removeView(this);
            if (this.overlay.getParent() != null) {
                ((ViewGroup) this.overlay.getParent()).removeView(this.overlay);
            }
        }
        this.overlay = null;
        if (this.originalParent != null) {
            if (this.originalParams != null) {
                this.originalParent.addView(this, Math.min(this.originalIndex, this.originalParent.getChildCount()), this.originalParams);
            } else {
                this.originalParent.addView(this);
            }
        }
        this.fullscreenOn = false;
        fullscreenPreview = null;
        this.moving = false;
        if (this.activity != null) {
            this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            immersive(false);
        }
        this.fullBtn.setKind(PlayerIcon.FULLSCREEN);
        this.fullBtn.setContentDescription(Localization.tr("Fullscreen"));
        this.topBar.setVisibility(8);
        requestLayout();
        showControls();
    }

    private void immersive(boolean z) {
        Window window;
        if (this.activity == null || (window = this.activity.getWindow()) == null) {
            return;
        }
        View decor = window.getDecorView();
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController controller = decor.getWindowInsetsController();
            if (z) {
                window.setDecorFitsSystemWindows(false);
                if (controller != null) {
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                    controller.hide(WindowInsets.Type.systemBars());
                }
            } else {
                if (controller != null) {
                    controller.show(WindowInsets.Type.systemBars());
                }
                window.setDecorFitsSystemWindows(true);
            }
            return;
        }
        decor.setSystemUiVisibility(z ? 0x1706 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int volume() {
        if (this.audio == null) {
            return 0;
        }
        return this.audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int maxVolume() {
        if (this.audio == null) {
            return 0;
        }
        return this.audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVolume(int i) {
        if (this.audio != null) {
            this.audio.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float brightness() {
        Window window;
        if (this.activity == null || (window = this.activity.getWindow()) == null) {
            return 0.5f;
        }
        float f = window.getAttributes().screenBrightness;
        if (f >= 0.0f) {
            return f;
        }
        try {
            return Settings.System.getInt(getContext().getContentResolver(), "screen_brightness") / 255.0f;
        } catch (Exception e) {
            return 0.5f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBrightness(float f) {
        Window window;
        if (this.activity == null || (window = this.activity.getWindow()) == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = Math.max(0.02f, Math.min(1.0f, f));
        window.setAttributes(attributes);
    }

    private PlayerIcon icon(int kind, String str) {
        PlayerIcon playerIcon = new PlayerIcon(getContext(), kind);
        playerIcon.setContentDescription(str);
        return playerIcon;
    }

    private TextView button(String str, String str2) {
        TextView textView = new TextView(getContext());
        textView.setText(str);
        textView.setGravity(17);
        textView.setTextSize(14.0f);
        textView.setTextColor(-1);
        textView.setContentDescription(str2);
        textView.setFocusable(true);
        return textView;
    }

    private android.graphics.drawable.Drawable shape(int i, int i2) {
        android.graphics.drawable.GradientDrawable gradientDrawable = new android.graphics.drawable.GradientDrawable();
        gradientDrawable.setColor(i);
        gradientDrawable.setCornerRadius(i2);
        return gradientDrawable;
    }

    private static Activity findActivity(Context context) {
        Context c = context;
        while (c != null) {
            if (c instanceof Activity) {
                return (Activity) c;
            }
            c = c instanceof ContextWrapper ? ((ContextWrapper) c).getBaseContext() : null;
        }
        return null;
    }

    private static String speedLabel(float f) {
        if (f == Math.round(f)) {
            return ((int) f) + "×";
        }
        return String.valueOf(f).replace(".0", "") + "×";
    }

    private int dp(int i) {
        return Math.round(i * getResources().getDisplayMetrics().density);
    }

    static String stamp(long j) {
        long max = Math.max(0L, j / 1000);
        return max >= 3600 ? String.format(Locale.US, "%d:%02d:%02d", Long.valueOf(max / 3600), Long.valueOf((max / 60) % 60), Long.valueOf(max % 60)) : String.format(Locale.US, "%02d:%02d", Long.valueOf(max / 60), Long.valueOf(max % 60));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        if (size <= 0) {
            size = getResources().getDisplayMetrics().widthPixels;
        }
        int height;
        if (this.fullscreenOn) {
            height = Math.max(1, View.MeasureSpec.getSize(i2));
        } else {
            height = PreviewGeometry.fit(size, Math.max(1, Math.round(getResources().getDisplayMetrics().heightPixels * 0.62f)), this.video.videoWidth, this.video.videoHeight)[1];
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
    }

    private void fail() {
        this.ready = false;
        this.handler.removeCallbacks(this.update);
        this.handler.removeCallbacks(this.hide);
        this.controls.setVisibility(8);
        this.topBar.setVisibility(8);
        this.subtitle.setVisibility(8);
        this.status.setText(Localization.tr("Preview unavailable. This device may not support the video codec. Subtitle extraction is still available."));
        this.status.setVisibility(0);
        this.video.setVisibility(4);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.detached = false;
        this.handler.removeCallbacks(this.update);
        if (this.ready) {
            this.handler.post(this.update);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        if (this.moving) {
            super.onDetachedFromWindow();
            return;
        }
        if (this.fullscreenOn) {
            if (this.overlay != null && this.overlay.getParent() != null) {
                ((ViewGroup) this.overlay.getParent()).removeView(this.overlay);
            }
            this.overlay = null;
            this.fullscreenOn = false;
            fullscreenPreview = null;
            if (this.activity != null) {
                this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                immersive(false);
            }
        }
        this.detached = true;
        this.handler.removeCallbacksAndMessages(null);
        try {
            this.player.release();
        } catch (Exception e) {
        }
        super.onDetachedFromWindow();
    }

    private static final class FitVideo extends SurfaceView {
        int mode;
        int videoHeight;
        int videoWidth;

        FitVideo(Context context) {
            super(context);
            this.videoWidth = 16;
            this.videoHeight = 9;
        }

        @Override // android.view.SurfaceView, android.view.View
        protected void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            if (size <= 0 || size2 <= 0) {
                setMeasuredDimension(size, size2);
                return;
            }
            if (this.mode == 2) {
                setMeasuredDimension(size, size2);
                return;
            }
            int[] fit = PreviewGeometry.fit(size, size2, this.videoWidth, this.videoHeight);
            if (this.mode == 0) {
                setMeasuredDimension(fit[0], fit[1]);
            } else {
                double max = Math.max(size / (double) Math.max(1, this.videoWidth), size2 / (double) Math.max(1, this.videoHeight));
                setMeasuredDimension((int) Math.round(this.videoWidth * max), (int) Math.round(this.videoHeight * max));
            }
        }
    }
}
