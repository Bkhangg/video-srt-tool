package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public enum Level {
    AV_LOG_STDERR(-16),
    AV_LOG_QUIET(-8),
    AV_LOG_PANIC(0),
    AV_LOG_FATAL(8),
    AV_LOG_ERROR(16),
    AV_LOG_WARNING(24),
    AV_LOG_INFO(32),
    AV_LOG_VERBOSE(40),
    AV_LOG_DEBUG(48),
    AV_LOG_TRACE(56);

    private final int value;

    public static Level from(int value) {
        if (value == AV_LOG_STDERR.getValue()) {
            return AV_LOG_STDERR;
        }
        if (value == AV_LOG_QUIET.getValue()) {
            return AV_LOG_QUIET;
        }
        if (value == AV_LOG_PANIC.getValue()) {
            return AV_LOG_PANIC;
        }
        if (value == AV_LOG_FATAL.getValue()) {
            return AV_LOG_FATAL;
        }
        if (value == AV_LOG_ERROR.getValue()) {
            return AV_LOG_ERROR;
        }
        if (value == AV_LOG_WARNING.getValue()) {
            return AV_LOG_WARNING;
        }
        if (value == AV_LOG_INFO.getValue()) {
            return AV_LOG_INFO;
        }
        if (value == AV_LOG_VERBOSE.getValue()) {
            return AV_LOG_VERBOSE;
        }
        if (value == AV_LOG_DEBUG.getValue()) {
            return AV_LOG_DEBUG;
        }
        return AV_LOG_TRACE;
    }

    public int getValue() {
        return this.value;
    }

    Level(int value) {
        this.value = value;
    }
}
