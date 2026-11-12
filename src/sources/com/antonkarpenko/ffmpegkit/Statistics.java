package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public class Statistics {
    private double bitrate;
    private long sessionId;
    private long size;
    private double speed;
    private double time;
    private float videoFps;
    private int videoFrameNumber;
    private float videoQuality;

    public Statistics(long sessionId, int videoFrameNumber, float videoFps, float videoQuality, long size, double time, double bitrate, double speed) {
        this.sessionId = sessionId;
        this.videoFrameNumber = videoFrameNumber;
        this.videoFps = videoFps;
        this.videoQuality = videoQuality;
        this.size = size;
        this.time = time;
        this.bitrate = bitrate;
        this.speed = speed;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public int getVideoFrameNumber() {
        return this.videoFrameNumber;
    }

    public void setVideoFrameNumber(int videoFrameNumber) {
        this.videoFrameNumber = videoFrameNumber;
    }

    public float getVideoFps() {
        return this.videoFps;
    }

    public void setVideoFps(float videoFps) {
        this.videoFps = videoFps;
    }

    public float getVideoQuality() {
        return this.videoQuality;
    }

    public void setVideoQuality(float videoQuality) {
        this.videoQuality = videoQuality;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getBitrate() {
        return this.bitrate;
    }

    public void setBitrate(double bitrate) {
        this.bitrate = bitrate;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String toString() {
        return "Statistics{sessionId=" + this.sessionId + ", videoFrameNumber=" + this.videoFrameNumber + ", videoFps=" + this.videoFps + ", videoQuality=" + this.videoQuality + ", size=" + this.size + ", time=" + this.time + ", bitrate=" + this.bitrate + ", speed=" + this.speed + '}';
    }
}
