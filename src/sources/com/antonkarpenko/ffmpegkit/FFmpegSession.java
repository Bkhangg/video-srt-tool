package com.antonkarpenko.ffmpegkit;

import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class FFmpegSession extends AbstractSession implements Session {
    private final FFmpegSessionCompleteCallback completeCallback;
    private final List<Statistics> statistics;
    private final StatisticsCallback statisticsCallback;
    private final Object statisticsLock;

    public static FFmpegSession create(String[] arguments) {
        return new FFmpegSession(arguments, null, null, null, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFmpegSession create(String[] arguments, FFmpegSessionCompleteCallback completeCallback) {
        return new FFmpegSession(arguments, completeCallback, null, null, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFmpegSession create(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback) {
        return new FFmpegSession(arguments, completeCallback, logCallback, statisticsCallback, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFmpegSession create(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback, LogRedirectionStrategy logRedirectionStrategy) {
        return new FFmpegSession(arguments, completeCallback, logCallback, statisticsCallback, logRedirectionStrategy);
    }

    private FFmpegSession(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback, LogRedirectionStrategy logRedirectionStrategy) {
        super(arguments, logCallback, logRedirectionStrategy);
        this.completeCallback = completeCallback;
        this.statisticsCallback = statisticsCallback;
        this.statistics = new LinkedList();
        this.statisticsLock = new Object();
    }

    public StatisticsCallback getStatisticsCallback() {
        return this.statisticsCallback;
    }

    public FFmpegSessionCompleteCallback getCompleteCallback() {
        return this.completeCallback;
    }

    public List<Statistics> getAllStatistics(int waitTimeout) {
        waitForAsynchronousMessagesInTransmit(waitTimeout);
        if (thereAreAsynchronousMessagesInTransmit()) {
            android.util.Log.i("ffmpeg-kit", String.format("getAllStatistics was called to return all statistics but there are still statistics being transmitted for session id %d.", Long.valueOf(this.sessionId)));
        }
        return getStatistics();
    }

    public List<Statistics> getAllStatistics() {
        return getAllStatistics(AbstractSession.DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT);
    }

    public List<Statistics> getStatistics() {
        List<Statistics> list;
        synchronized (this.statisticsLock) {
            list = this.statistics;
        }
        return list;
    }

    public Statistics getLastReceivedStatistics() {
        synchronized (this.statisticsLock) {
            if (this.statistics.size() <= 0) {
                return null;
            }
            return this.statistics.get(this.statistics.size() - 1);
        }
    }

    public void addStatistics(Statistics statistics) {
        synchronized (this.statisticsLock) {
            this.statistics.add(statistics);
        }
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public String getFailStackTrace() {
        return "";
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public boolean isFFmpeg() {
        return true;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public boolean isFFprobe() {
        return false;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public boolean isMediaInformation() {
        return false;
    }

    public String toString() {
        return "FFmpegSession{sessionId=" + this.sessionId + ", createTime=" + this.createTime + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", arguments=" + FFmpegKitConfig.argumentsToString(this.arguments) + ", logs=" + getLogsAsString() + ", state=" + this.state + ", returnCode=" + this.returnCode + ", failStackTrace='}";
    }
}
