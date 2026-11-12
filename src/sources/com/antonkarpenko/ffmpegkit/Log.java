package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public class Log {
    private final Level level;
    private final String message;
    private final long sessionId;

    public Log(long sessionId, Level level, String message) {
        this.sessionId = sessionId;
        this.level = level;
        this.message = message;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    public Level getLevel() {
        return this.level;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "Log{sessionId=" + this.sessionId + ", level=" + this.level + ", message='" + this.message + "'}";
    }
}
