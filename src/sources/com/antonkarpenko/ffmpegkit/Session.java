package com.antonkarpenko.ffmpegkit;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/* loaded from: classes.dex */
public interface Session {
    void addLog(Log log);

    void cancel();

    List<Log> getAllLogs();

    List<Log> getAllLogs(int i);

    String getAllLogsAsString();

    String getAllLogsAsString(int i);

    String[] getArguments();

    String getCommand();

    Date getCreateTime();

    long getDuration();

    Date getEndTime();

    String getFailStackTrace();

    Future<?> getFuture();

    LogCallback getLogCallback();

    LogRedirectionStrategy getLogRedirectionStrategy();

    List<Log> getLogs();

    String getLogsAsString();

    String getOutput();

    ReturnCode getReturnCode();

    long getSessionId();

    Date getStartTime();

    SessionState getState();

    boolean isFFmpeg();

    boolean isFFprobe();

    boolean isMediaInformation();

    boolean thereAreAsynchronousMessagesInTransmit();
}
