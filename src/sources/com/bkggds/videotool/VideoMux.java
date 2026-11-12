package com.bkggds.videotool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.widget.Toast;
import com.antonkarpenko.ffmpegkit.FFmpegKitConfig;
import com.antonkarpenko.ffmpegkit.FFmpegSession;
import com.antonkarpenko.ffmpegkit.FFprobeKit;
import com.antonkarpenko.ffmpegkit.MediaInformation;
import com.antonkarpenko.ffmpegkit.MediaInformationSession;
import com.antonkarpenko.ffmpegkit.ReturnCode;
import com.antonkarpenko.ffmpegkit.Statistics;
import com.antonkarpenko.ffmpegkit.StatisticsCallback;
import com.antonkarpenko.ffmpegkit.StreamInformation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CancellationException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class VideoMux {
    private static final int SAVE_VIDEO = 106;
    private static final WeakHashMap<MainActivity, Job> jobs = new WeakHashMap<>();

    public static void prepare(MainActivity mainActivity, String str, String str2, String str3) {
        if (ImportGuard.require(mainActivity)) {
            if (jobs.get(mainActivity) != null) {
                Toast.makeText(mainActivity, Localization.tr("A video export is already in progress."), 1).show();
                return;
            }
            Job job = new Job(mainActivity, str, str2, str3);
            jobs.put(mainActivity, job);
            job.prepare();
        }
    }

    public static void release(MainActivity mainActivity) {
        Job remove = jobs.remove(mainActivity);
        if (remove != null) {
            remove.cancel();
        }
    }

    public static boolean onActivityResult(MainActivity mainActivity, int i, int i2, Intent intent) {
        if (i != SAVE_VIDEO) {
            return false;
        }
        Job job = jobs.get(mainActivity);
        if (job == null) {
            return true;
        }
        if (i2 != -1 || intent == null || intent.getData() == null) {
            job.cancel();
            return true;
        }
        Uri data = intent.getData();
        if (same(mainActivity, job.source, data)) {
            job.fail("Choose a different output file. The original video cannot be overwritten.");
            return true;
        }
        job.remux(data);
        return true;
    }

    private static boolean same(MainActivity mainActivity, Uri uri, Uri uri2) {
        if (uri.equals(uri2)) {
            return true;
        }
        try {
            Uri canonicalize = mainActivity.getContentResolver().canonicalize(uri);
            Uri canonicalize2 = mainActivity.getContentResolver().canonicalize(uri2);
            if (canonicalize != null) {
                if (canonicalize.equals(canonicalize2)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        try {
            if (Objects.equals(uri.getAuthority(), uri2.getAuthority())) {
                if (DocumentsContract.getDocumentId(uri).equals(DocumentsContract.getDocumentId(uri2))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e2) {
            return false;
        }
    }

    private static final class Job {
        final String base;
        volatile boolean cancelled;
        boolean copiedInput;
        volatile boolean destinationTouched;
        File directory;
        double duration;
        volatile boolean finished;
        String format;
        String input;
        int inputAttachments;
        int inputAudio;
        File inputCopy;
        int inputVideo;
        final String language;
        File output;
        final MainActivity owner;
        volatile boolean picking;
        MuxProgress progress;
        volatile FFmpegSession session;
        final Uri source;
        long sourceSize = -1;
        File subtitle;
        int subtitles;
        final String text;
        final String title;

        Job(MainActivity mainActivity, String str, String str2, String str3) {
            this.owner = mainActivity;
            this.source = mainActivity.videoUi();
            this.text = SrtText.normalize(str);
            this.title = str2.trim();
            this.language = str3;
            this.base = mainActivity.videoBaseUi();
        }

        boolean alive() {
            return (this.owner.isFinishing() || this.owner.isDestroyed()) ? false : true;
        }

        void check() {
            if (this.cancelled || !alive()) {
                throw new CancellationException();
            }
        }

        void open(String str) {
            this.progress = new MuxProgress(this.owner, str, new Runnable() { // from class: com.bkggds.videotool.VideoMux.Job.1
                @Override // java.lang.Runnable
                public void run() {
                    Job.this.cancel();
                }
            });
        }

        void cancel() {
            this.cancelled = true;
            FFmpegSession fFmpegSession = this.session;
            if (fFmpegSession != null) {
                fFmpegSession.cancel();
            }
            if (this.progress != null) {
                this.progress.dismiss();
            }
            if (this.picking) {
                cleanup();
                if (VideoMux.jobs.get(this.owner) == this) {
                    VideoMux.jobs.remove(this.owner);
                }
            }
        }

        void prepare() {
            open("Preparing video export");
            new Thread(new Runnable() { // from class: com.bkggds.videotool.VideoMux.Job.2
                @Override // java.lang.Runnable
                public void run() {
                    Throwable th;
                    boolean z;
                    Throwable th2;
                    try {
                        Job.this.check();
                        Job.this.directory = new File(Job.this.owner.getCacheDir(), "mux-" + UUID.randomUUID().toString());
                        if (Job.this.directory.mkdirs()) {
                            try {
                                ParcelFileDescriptor openFileDescriptor = Job.this.owner.getContentResolver().openFileDescriptor(Job.this.source, "r");
                                try {
                                    if (openFileDescriptor != null) {
                                        Job.this.sourceSize = openFileDescriptor.getStatSize();
                                        try {
                                            Os.lseek(openFileDescriptor.getFileDescriptor(), 0L, OsConstants.SEEK_CUR);
                                            z = true;
                                        } catch (ErrnoException e) {
                                            z = false;
                                        }
                                        if (openFileDescriptor != null) {
                                            openFileDescriptor.close();
                                        }
                                        long j = Job.this.sourceSize > 0 ? Job.this.sourceSize + (Job.this.sourceSize / 10) : 33554432L;
                                        long availableBytes = new StatFs(Job.this.directory.getAbsolutePath()).getAvailableBytes();
                                        if (j > availableBytes - 16777216 || (!z && Job.this.sourceSize > 0 && Job.this.sourceSize > (availableBytes - j) - 16777216)) {
                                            throw new IOException("Not enough free storage to export this video.");
                                        }
                                        if (!z) {
                                            Job.this.inputCopy = new File(Job.this.directory, "source.video");
                                            Job.this.copySource(Job.this.inputCopy);
                                            Job.this.input = Job.this.inputCopy.getAbsolutePath();
                                            Job.this.copiedInput = true;
                                        }
                                        Job.this.progress.update(-1, "Reading video information...", true);
                                        MediaInformationSession mediaInformation = FFprobeKit.getMediaInformation(Job.this.copiedInput ? Job.this.input : FFmpegKitConfig.getSafParameterForRead(Job.this.owner, Job.this.source));
                                        Job.this.check();
                                        MediaInformation mediaInformation2 = mediaInformation.getMediaInformation();
                                        if (mediaInformation2 == null) {
                                            throw new IOException("Could not read this video. Try importing a local MKV or MP4 file.");
                                        }
                                        Job.this.format = MuxPlan.container(mediaInformation2.getFormat());
                                        try {
                                            Job.this.duration = Double.parseDouble(mediaInformation2.getDuration()) * 1000.0d;
                                        } catch (Exception e2) {
                                        }
                                        if (Job.this.sourceSize < 0) {
                                            try {
                                                Job.this.sourceSize = Long.parseLong(mediaInformation2.getSize());
                                            } catch (Exception e3) {
                                            }
                                        }
                                        if (Job.this.sourceSize > 0 && Job.this.sourceSize + (Job.this.sourceSize / 10) > new StatFs(Job.this.directory.getAbsolutePath()).getAvailableBytes() - 16777216) {
                                            throw new IOException("Not enough free storage to export this video.");
                                        }
                                        Iterator<StreamInformation> it = mediaInformation2.getStreams().iterator();
                                        while (it.hasNext()) {
                                            String type = it.next().getType();
                                            if ("subtitle".equals(type)) {
                                                Job.this.subtitles++;
                                            }
                                            if ("video".equals(type)) {
                                                Job.this.inputVideo++;
                                            }
                                            if ("audio".equals(type)) {
                                                Job.this.inputAudio++;
                                            }
                                            if ("attachment".equals(type)) {
                                                Job.this.inputAttachments++;
                                            }
                                        }
                                        if (Job.this.inputVideo != 0) {
                                            if (SrtText.hasCueWithin(Job.this.text, Job.this.duration)) {
                                                Job.this.subtitle = new File(Job.this.directory, "added.srt");
                                                try {
                                                    FileOutputStream fileOutputStream = new FileOutputStream(Job.this.subtitle);
                                                    try {
                                                        fileOutputStream.write(Job.this.text.getBytes("UTF-8"));
                                                        fileOutputStream.close();
                                                        Job.this.check();
                                                        Job.this.owner.runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.VideoMux.Job.2.1
                                                            @Override // java.lang.Runnable
                                                            public void run() {
                                                                if (Job.this.cancelled || !Job.this.alive()) {
                                                                    Job.this.done();
                                                                    return;
                                                                }
                                                                Job.this.progress.dismiss();
                                                                Job.this.picking = true;
                                                                Intent intent = new Intent("android.intent.action.CREATE_DOCUMENT");
                                                                intent.addCategory("android.intent.category.OPENABLE");
                                                                intent.setType(Job.this.format.equals("mkv") ? "video/x-matroska" : "video/mp4");
                                                                intent.putExtra("android.intent.extra.TITLE", String.valueOf(Job.this.base) + ".subtitled." + Job.this.format);
                                                                intent.addFlags(2);
                                                                try {
                                                                    Job.this.owner.startActivityForResult(intent, VideoMux.SAVE_VIDEO);
                                                                } catch (Exception e4) {
                                                                    Job.this.fail("Could not open the file picker.");
                                                                }
                                                            }
                                                        });
                                                    } catch (Throwable th3) {
                                                        try {
                                                            fileOutputStream.close();
                                                            throw th3;
                                                        } catch (Throwable th4) {
                                                            th = th4;
                                                            th2 = th3;
                                                            if (th2 == null) {
                                                                throw th;
                                                            }
                                                            if (th2 == th) {
                                                                throw th2;
                                                            }
                                                            th2.addSuppressed(th);
                                                        }
                                                    }
                                                } catch (Throwable th5) {
                                                    th = th5;
                                                    th2 = null;
                                                }
                                            } else {
                                                throw new IOException("The subtitle timing is outside the video. Adjust the SRT timestamps first.");
                                            }
                                        } else {
                                            throw new IOException("The imported file has no video track.");
                                        }
                                    } else {
                                        throw new IOException("Could not open the video.");
                                    }
                                } catch (Throwable th6) {
                                    th = th6;
                                    if (openFileDescriptor == null) {
                                        throw th;
                                    }
                                    try {
                                        openFileDescriptor.close();
                                        throw th;
                                    } catch (Throwable th7) {
                                        th = th7;
                                        if (th == null) {
                                            throw th;
                                        }
                                        if (th == th) {
                                            throw th;
                                        }
                                        th.addSuppressed(th);
                                    }
                                }
                            } catch (Throwable th8) {
                                th = th8;
                                th = null;
                            }
                        } else {
                            throw new IOException("Could not create temporary storage.");
                        }
                    } catch (CancellationException e4) {
                        Job.this.finishCancelled();
                    } catch (Throwable th9) {
                        Job.this.error(th9);
                    }
                }
            }, "prepare-video-mux").start();
        }

        void copySource(File file) throws Exception {
            long j = 0;
            this.progress.update(this.sourceSize > 0 ? 0 : -1, "Preparing a local video copy...", true);
            Throwable th = null;
            try {
                InputStream openInputStream = this.owner.getContentResolver().openInputStream(this.source);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    try {
                        if (openInputStream == null) {
                            throw new IOException("Could not open the video.");
                        }
                        byte[] bArr = new byte[1048576];
                        long j2 = 0;
                        while (true) {
                            int read = openInputStream.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            check();
                            fileOutputStream.write(bArr, 0, read);
                            j2 += read;
                            this.progress.update(this.sourceSize > j ? (int) Math.min(95L, (100 * j2) / this.sourceSize) : -1, "Preparing a local video copy...", false);
                            j = 0;
                        }
                        fileOutputStream.close();
                        if (openInputStream != null) {
                            openInputStream.close();
                        }
                    } catch (Throwable th2) {
                        fileOutputStream.close();
                        throw th2;
                    }
                } finally {
                }
            } catch (Throwable th3) {
                if (0 == 0) {
                    throw th3;
                }
                if (null != th3) {
                    th.addSuppressed(th3);
                }
            }
        }

        void remux(final Uri uri) {
            if (this.cancelled || this.finished) {
                return;
            }
            this.picking = false;
            open("Embedding subtitles");
            new Thread(new Runnable() { // from class: com.bkggds.videotool.VideoMux.Job.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Job.this.check();
                        Job.this.output = new File(Job.this.directory, "result." + Job.this.format);
                        String[] arguments = MuxPlan.arguments(Job.this.copiedInput ? Job.this.input : FFmpegKitConfig.getSafParameterForRead(Job.this.owner, Job.this.source), Job.this.subtitle.getAbsolutePath(), Job.this.output.getAbsolutePath(), Job.this.format, Job.this.subtitles, Job.this.title, Job.this.language);
                        Job.this.progress.update(Job.this.duration > 0.0d ? 0 : -1, "Embedding subtitle track...", true);
                        final Job job = Job.this;
                        job.session = FFmpegSession.create(arguments, null, null, new StatisticsCallback() { // from class: com.bkggds.videotool.VideoMux.Job.3.1
                            @Override // com.antonkarpenko.ffmpegkit.StatisticsCallback
                            public void apply(Statistics statistics) {
                                if (job.cancelled) {
                                    if (job.session != null) {
                                        job.session.cancel();
                                        return;
                                    }
                                    return;
                                }
                                job.progress.update(job.duration > 0.0d ? (int) Math.max(0.0d, Math.min(85.0d, (statistics.getTime() * 85.0d) / job.duration)) : -1, "Embedding subtitle track...", false);
                            }
                        });
                        Job.this.check();
                        FFmpegKitConfig.ffmpegExecute(Job.this.session);
                        Job.this.check();
                        if (!ReturnCode.isSuccess(Job.this.session.getReturnCode()) || !Job.this.output.isFile() || Job.this.output.length() == 0) {
                            throw new IOException("Could not embed subtitles. This video may contain tracks that cannot be copied into its container.");
                        }
                        Job.this.progress.update(88, "Checking the new video...", true);
                        MediaInformation mediaInformation = FFprobeKit.getMediaInformation(Job.this.output.getAbsolutePath()).getMediaInformation();
                        Job.this.check();
                        Job.this.verify(mediaInformation);
                        Job.this.verifyText();
                        Job.this.progress.update(90, "Saving the new video...", true);
                        FileInputStream fileInputStream = new FileInputStream(Job.this.output);
                        try {
                            OutputStream openOutputStream = Job.this.owner.getContentResolver().openOutputStream(uri, "wt");
                            if (openOutputStream == null) {
                                throw new IOException("Could not write the output video.");
                            }
                            try {
                                Job.this.destinationTouched = true;
                                byte[] bArr = new byte[1048576];
                                long j = 0;
                                while (true) {
                                    int read = fileInputStream.read(bArr);
                                    if (read == -1) {
                                        break;
                                    }
                                    Job.this.check();
                                    openOutputStream.write(bArr, 0, read);
                                    j += read;
                                    Job.this.progress.update(((int) Math.min(9L, (j * 9) / Job.this.output.length())) + 90, "Saving the new video...", false);
                                }
                            } finally {
                                openOutputStream.close();
                            }
                        } finally {
                            fileInputStream.close();
                        }
                        Job.this.progress.update(100, "Video saved", true);
                        Job.this.check();
                        Job.this.owner.runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.VideoMux.Job.3.2
                            @Override // java.lang.Runnable
                            public void run() {
                                if (!Job.this.cancelled && Job.this.alive()) {
                                    Job.this.owner.statusUi(Localization.tr("Video saved with the new subtitle track."), false);
                                    Toast.makeText(Job.this.owner, Localization.tr("Video saved with the new subtitle track."), 1).show();
                                }
                                Job.this.done();
                            }
                        });
                    } catch (CancellationException e) {
                        Job.this.deleteOutput(uri);
                        Job.this.finishCancelled();
                    } catch (Throwable th) {
                        Job.this.deleteOutput(uri);
                        Job.this.error(th);
                    }
                }
            }, "embed-srt-video").start();
        }

        void verifyText() throws Exception {
            File file = new File(this.directory, "verified.srt");
            this.session = FFmpegSession.create(new String[]{"-hide_banner", "-nostdin", "-y", "-copyts", "-i", this.output.getAbsolutePath(), "-map", "0:s:" + this.subtitles, "-c:s", "srt", "-f", "srt", file.getAbsolutePath()}, null, null, null);
            check();
            FFmpegKitConfig.ffmpegExecute(this.session);
            check();
            if (!ReturnCode.isSuccess(this.session.getReturnCode()) || !file.isFile()) {
                throw new IOException("The subtitle text could not be verified.");
            }
            if (!SrtText.verifies(this.text, SrtText.read(new FileInputStream(file)))) {
                throw new IOException("The subtitle text could not be verified.");
            }
        }

        void verify(MediaInformation mediaInformation) throws Exception {
            if (mediaInformation == null) {
                throw new IOException("The new video could not be verified.");
            }
            ArrayList arrayList = new ArrayList();
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            for (StreamInformation streamInformation : mediaInformation.getStreams()) {
                String type = streamInformation.getType();
                if ("video".equals(type)) {
                    i++;
                }
                if ("audio".equals(type)) {
                    i2++;
                }
                if ("attachment".equals(type)) {
                    i3++;
                }
                if ("subtitle".equals(type)) {
                    arrayList.add(streamInformation);
                }
            }
            if (i < this.inputVideo || i2 < this.inputAudio || i3 < this.inputAttachments || arrayList.size() != this.subtitles + 1) {
                throw new IOException("The new video could not be verified.");
            }
            StreamInformation streamInformation2 = (StreamInformation) arrayList.get(this.subtitles);
            JSONObject tags = streamInformation2.getTags();
            if (tags == null || !MuxPlan.iso3(this.language).equals(tags.optString("language")) || (!this.title.equals(tags.optString("title")) && !this.title.equals(tags.optString("handler_name")))) {
                throw new IOException("The subtitle metadata could not be verified.");
            }
            if (!(this.format.equals("mkv") ? "subrip" : "mov_text").equals(streamInformation2.getCodec())) {
                throw new IOException("The subtitle format could not be verified.");
            }
        }

        void deleteOutput(Uri uri) {
            boolean z = false;
            try {
                ParcelFileDescriptor openFileDescriptor = this.owner.getContentResolver().openFileDescriptor(uri, "r");
                if (openFileDescriptor != null) {
                    try {
                        if (openFileDescriptor.getStatSize() == 0) {
                            z = true;
                        }
                    } finally {
                        openFileDescriptor.close();
                    }
                }
            } catch (Exception e) {
            }
            if (this.destinationTouched || z) {
                try {
                    DocumentsContract.deleteDocument(this.owner.getContentResolver(), uri);
                } catch (Exception e2) {
                }
            }
        }

        void error(final Throwable th) {
            this.owner.runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.VideoMux.Job.4
                @Override // java.lang.Runnable
                public void run() {
                    if (Job.this.cancelled || !Job.this.alive()) {
                        Job.this.done();
                        return;
                    }
                    String message = th.getMessage();
                    if (th instanceof LinkageError) {
                        message = "The video engine is unavailable on this device.";
                    } else if ("Unsupported video format".equals(message)) {
                        message = "Only MKV and MP4 videos are supported.";
                    } else if (message != null && (message.contains("ENOSPC") || message.contains("No space left"))) {
                        message = "Not enough free storage to export this video.";
                    } else if (message == null || message.length() > 300 || (!message.startsWith("Could not ") && !message.startsWith("Not enough ") && !message.startsWith("The imported ") && !message.startsWith("The new video ") && !message.startsWith("The subtitle "))) {
                        message = "Video export failed. The original video was not changed.";
                    }
                    Job.this.fail(message);
                }
            });
        }

        void fail(String str) {
            if (alive()) {
                AppFonts.decorate(new AlertDialog.Builder(AppTheme.context(this.owner)).setTitle(Localization.tr("Video export failed")).setMessage(Localization.tr(str)).setPositiveButton(Localization.tr("Done"), (DialogInterface.OnClickListener) null).show());
            }
            done();
        }

        void finishCancelled() {
            this.owner.runOnUiThread(new Runnable() { // from class: com.bkggds.videotool.VideoMux.Job.5
                @Override // java.lang.Runnable
                public void run() {
                    Job.this.done();
                }
            });
        }

        void done() {
            this.finished = true;
            this.picking = false;
            if (this.progress != null) {
                this.progress.dismiss();
            }
            if (VideoMux.jobs.get(this.owner) == this) {
                VideoMux.jobs.remove(this.owner);
            }
            cleanup();
        }

        void cleanup() {
            if (this.directory != null) {
                File[] listFiles = this.directory.listFiles();
                if (listFiles != null) {
                    for (File file : listFiles) {
                        file.delete();
                    }
                }
                this.directory.delete();
            }
        }
    }
}
