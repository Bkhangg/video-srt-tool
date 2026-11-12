package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public class MediaInformationSession extends AbstractSession implements Session {
    private final MediaInformationSessionCompleteCallback completeCallback;
    private MediaInformation mediaInformation;

    public static MediaInformationSession create(String[] arguments) {
        return new MediaInformationSession(arguments, null, null);
    }

    public static MediaInformationSession create(String[] arguments, MediaInformationSessionCompleteCallback completeCallback) {
        return new MediaInformationSession(arguments, completeCallback, null);
    }

    public static MediaInformationSession create(String[] arguments, MediaInformationSessionCompleteCallback completeCallback, LogCallback logCallback) {
        return new MediaInformationSession(arguments, completeCallback, logCallback);
    }

    private MediaInformationSession(String[] arguments, MediaInformationSessionCompleteCallback completeCallback, LogCallback logCallback) {
        super(arguments, logCallback, LogRedirectionStrategy.NEVER_PRINT_LOGS);
        this.completeCallback = completeCallback;
    }

    public MediaInformation getMediaInformation() {
        return this.mediaInformation;
    }

    public void setMediaInformation(MediaInformation mediaInformation) {
        this.mediaInformation = mediaInformation;
    }

    public MediaInformationSessionCompleteCallback getCompleteCallback() {
        return this.completeCallback;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public String getFailStackTrace() {
        return "";
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public boolean isFFmpeg() {
        return false;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public boolean isFFprobe() {
        return false;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public boolean isMediaInformation() {
        return true;
    }

    public String toString() {
        return "MediaInformationSession{sessionId=" + this.sessionId + ", createTime=" + this.createTime + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", arguments=" + FFmpegKitConfig.argumentsToString(this.arguments) + ", logs=" + getLogsAsString() + ", state=" + this.state + ", returnCode=" + this.returnCode + ", failStackTrace='}";
    }
}
