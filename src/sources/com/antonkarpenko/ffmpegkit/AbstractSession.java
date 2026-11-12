package com.antonkarpenko.ffmpegkit;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
public abstract class AbstractSession implements Session {
    public static final int DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT = 5000;
    protected static final AtomicLong sessionIdGenerator = new AtomicLong(1);
    protected final String[] arguments;
    protected final LogCallback logCallback;
    protected final LogRedirectionStrategy logRedirectionStrategy;
    protected final long sessionId = sessionIdGenerator.getAndIncrement();
    protected final Date createTime = new Date();
    protected Date startTime = null;
    protected Date endTime = null;
    protected final List<Log> logs = new LinkedList();
    protected final Object logsLock = new Object();
    protected Future<?> future = null;
    protected SessionState state = SessionState.CREATED;
    protected ReturnCode returnCode = null;

    protected AbstractSession(String[] arguments, LogCallback logCallback, LogRedirectionStrategy logRedirectionStrategy) {
        this.logCallback = logCallback;
        this.arguments = arguments;
        this.logRedirectionStrategy = logRedirectionStrategy;
        FFmpegKitConfig.addSession(this);
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public LogCallback getLogCallback() {
        return this.logCallback;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public long getSessionId() {
        return this.sessionId;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public Date getCreateTime() {
        return this.createTime;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public Date getStartTime() {
        return this.startTime;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public Date getEndTime() {
        return this.endTime;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public long getDuration() {
        Date startTime = this.startTime;
        Date endTime = this.endTime;
        if (startTime != null && endTime != null) {
            return endTime.getTime() - startTime.getTime();
        }
        return 0L;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public String[] getArguments() {
        return this.arguments;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public String getCommand() {
        return FFmpegKitConfig.argumentsToString(this.arguments);
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public List<Log> getAllLogs(int waitTimeout) {
        waitForAsynchronousMessagesInTransmit(waitTimeout);
        if (thereAreAsynchronousMessagesInTransmit()) {
            android.util.Log.i("ffmpeg-kit", String.format("getAllLogs was called to return all logs but there are still logs being transmitted for session id %d.", Long.valueOf(this.sessionId)));
        }
        return getLogs();
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public List<Log> getAllLogs() {
        return getAllLogs(DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT);
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public List<Log> getLogs() {
        LinkedList linkedList;
        synchronized (this.logsLock) {
            linkedList = new LinkedList(this.logs);
        }
        return linkedList;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public String getAllLogsAsString(int waitTimeout) {
        waitForAsynchronousMessagesInTransmit(waitTimeout);
        if (thereAreAsynchronousMessagesInTransmit()) {
            android.util.Log.i("ffmpeg-kit", String.format("getAllLogsAsString was called to return all logs but there are still logs being transmitted for session id %d.", Long.valueOf(this.sessionId)));
        }
        return getLogsAsString();
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public String getAllLogsAsString() {
        return getAllLogsAsString(DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT);
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public String getLogsAsString() {
        StringBuilder concatenatedString = new StringBuilder();
        synchronized (this.logsLock) {
            for (Log log : this.logs) {
                concatenatedString.append(log.getMessage());
            }
        }
        return concatenatedString.toString();
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public String getOutput() {
        return getAllLogsAsString();
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public SessionState getState() {
        return this.state;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public ReturnCode getReturnCode() {
        return this.returnCode;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public LogRedirectionStrategy getLogRedirectionStrategy() {
        return this.logRedirectionStrategy;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public boolean thereAreAsynchronousMessagesInTransmit() {
        return FFmpegKitConfig.messagesInTransmit(this.sessionId) != 0;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public void addLog(Log log) {
        synchronized (this.logsLock) {
            this.logs.add(log);
        }
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public Future<?> getFuture() {
        return this.future;
    }

    @Override // com.antonkarpenko.ffmpegkit.Session
    public void cancel() {
        if (this.state == SessionState.RUNNING) {
            FFmpegKit.cancel(this.sessionId);
        }
    }

    protected void waitForAsynchronousMessagesInTransmit(int timeout) {
        long start = System.currentTimeMillis();
        while (thereAreAsynchronousMessagesInTransmit() && System.currentTimeMillis() < timeout + start) {
            synchronized (this) {
                try {
                    wait(100L);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    void setFuture(Future<?> future) {
        this.future = future;
    }

    void startRunning() {
        this.state = SessionState.RUNNING;
        this.startTime = new Date();
    }

    void complete(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.state = SessionState.COMPLETED;
        this.endTime = new Date();
    }

    void fail(Exception exception) {
        this.state = SessionState.FAILED;
        this.endTime = new Date();
    }
}
