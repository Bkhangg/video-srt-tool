package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public class AsyncFFprobeExecuteTask implements Runnable {
    private final FFprobeSessionCompleteCallback completeCallback;
    private final FFprobeSession ffprobeSession;

    public AsyncFFprobeExecuteTask(FFprobeSession ffprobeSession) {
        this.ffprobeSession = ffprobeSession;
        this.completeCallback = ffprobeSession.getCompleteCallback();
    }

    @Override // java.lang.Runnable
    public void run() {
        FFmpegKitConfig.ffprobeExecute(this.ffprobeSession);
        if (this.completeCallback != null) {
            try {
                this.completeCallback.apply(this.ffprobeSession);
            } catch (Exception e) {
            }
        }
        FFprobeSessionCompleteCallback globalFFprobeSessionCompleteCallback = FFmpegKitConfig.getFFprobeSessionCompleteCallback();
        if (globalFFprobeSessionCompleteCallback != null) {
            try {
                globalFFprobeSessionCompleteCallback.apply(this.ffprobeSession);
            } catch (Exception e2) {
            }
        }
    }
}
