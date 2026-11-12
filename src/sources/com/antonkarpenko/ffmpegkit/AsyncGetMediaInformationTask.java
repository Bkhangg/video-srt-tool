package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public class AsyncGetMediaInformationTask implements Runnable {
    private final MediaInformationSessionCompleteCallback completeCallback;
    private final MediaInformationSession mediaInformationSession;
    private final Integer waitTimeout;

    public AsyncGetMediaInformationTask(MediaInformationSession mediaInformationSession) {
        this(mediaInformationSession, Integer.valueOf(AbstractSession.DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT));
    }

    public AsyncGetMediaInformationTask(MediaInformationSession mediaInformationSession, Integer waitTimeout) {
        this.mediaInformationSession = mediaInformationSession;
        this.completeCallback = mediaInformationSession.getCompleteCallback();
        this.waitTimeout = waitTimeout;
    }

    @Override // java.lang.Runnable
    public void run() {
        FFmpegKitConfig.getMediaInformationExecute(this.mediaInformationSession, this.waitTimeout.intValue());
        if (this.completeCallback != null) {
            try {
                this.completeCallback.apply(this.mediaInformationSession);
            } catch (Exception e) {
            }
        }
        MediaInformationSessionCompleteCallback globalMediaInformationSessionCompleteCallback = FFmpegKitConfig.getMediaInformationSessionCompleteCallback();
        if (globalMediaInformationSessionCompleteCallback != null) {
            try {
                globalMediaInformationSessionCompleteCallback.apply(this.mediaInformationSession);
            } catch (Exception e2) {
            }
        }
    }
}
