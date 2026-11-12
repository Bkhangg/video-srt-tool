package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public class FFprobeSession extends AbstractSession implements Session {
    private final FFprobeSessionCompleteCallback completeCallback;

    public static FFprobeSession create(String[] arguments) {
        return new FFprobeSession(arguments, null, null, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFprobeSession create(String[] arguments, FFprobeSessionCompleteCallback completeCallback) {
        return new FFprobeSession(arguments, completeCallback, null, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFprobeSession create(String[] arguments, FFprobeSessionCompleteCallback completeCallback, LogCallback logCallback) {
        return new FFprobeSession(arguments, completeCallback, logCallback, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFprobeSession create(String[] arguments, FFprobeSessionCompleteCallback completeCallback, LogCallback logCallback, LogRedirectionStrategy logRedirectionStrategy) {
        return new FFprobeSession(arguments, completeCallback, logCallback, logRedirectionStrategy);
    }

    private FFprobeSession(String[] arguments, FFprobeSessionCompleteCallback completeCallback, LogCallback logCallback, LogRedirectionStrategy logRedirectionStrategy) {
        super(arguments, logCallback, logRedirectionStrategy);
        this.completeCallback = completeCallback;
    }

    public FFprobeSessionCompleteCallback getCompleteCallback() {
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
        return true;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public boolean isMediaInformation() {
        return false;
    }

    public String toString() {
        return "FFprobeSession{sessionId=" + this.sessionId + ", createTime=" + this.createTime + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", arguments=" + FFmpegKitConfig.argumentsToString(this.arguments) + ", logs=" + getLogsAsString() + ", state=" + this.state + ", returnCode=" + this.returnCode + ", failStackTrace='}";
    }
}
