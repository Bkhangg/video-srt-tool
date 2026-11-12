package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public enum Signal {
    SIGINT(2),
    SIGQUIT(3),
    SIGPIPE(13),
    SIGTERM(15),
    SIGXCPU(24);

    private final int value;

    Signal(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
