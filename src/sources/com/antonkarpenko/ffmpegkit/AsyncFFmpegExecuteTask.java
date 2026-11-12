package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public class AsyncFFmpegExecuteTask implements Runnable {
    private final FFmpegSessionCompleteCallback completeCallback;
    private final FFmpegSession ffmpegSession;

    public AsyncFFmpegExecuteTask(FFmpegSession ffmpegSession) {
        this.ffmpegSession = ffmpegSession;
        this.completeCallback = ffmpegSession.getCompleteCallback();
    }

    @Override // java.lang.Runnable
    public void run() {
        FFmpegKitConfig.ffmpegExecute(this.ffmpegSession);
        if (this.completeCallback != null) {
            try {
                this.completeCallback.apply(this.ffmpegSession);
            } catch (Exception e) {
            }
        }
        FFmpegSessionCompleteCallback globalFFmpegSessionCompleteCallback = FFmpegKitConfig.getFFmpegSessionCompleteCallback();
        if (globalFFmpegSessionCompleteCallback != null) {
            try {
                globalFFmpegSessionCompleteCallback.apply(this.ffmpegSession);
            } catch (Exception e2) {
            }
        }
    }
}
