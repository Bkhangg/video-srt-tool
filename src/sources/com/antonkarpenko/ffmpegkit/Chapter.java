package com.antonkarpenko.ffmpegkit;

import org.json.JSONObject;

/* loaded from: classes.dex */
public class Chapter {
    public static final String KEY_END = "end";
    public static final String KEY_END_TIME = "end_time";
    public static final String KEY_ID = "id";
    public static final String KEY_START = "start";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_TIME_BASE = "time_base";
    private final JSONObject jsonObject;

    public Chapter(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Long getId() {
        return getNumberProperty(KEY_ID);
    }

    public String getTimeBase() {
        return getStringProperty("time_base");
    }

    public Long getStart() {
        return getNumberProperty(KEY_START);
    }

    public String getStartTime() {
        return getStringProperty("start_time");
    }

    public Long getEnd() {
        return getNumberProperty(KEY_END);
    }

    public String getEndTime() {
        return getStringProperty(KEY_END_TIME);
    }

    public JSONObject getTags() {
        return getProperty("tags");
    }

    public String getStringProperty(String key) {
        JSONObject allProperties = getAllProperties();
        if (allProperties == null || !allProperties.has(key)) {
            return null;
        }
        return allProperties.optString(key);
    }

    public Long getNumberProperty(String key) {
        JSONObject allProperties = getAllProperties();
        if (allProperties == null || !allProperties.has(key)) {
            return null;
        }
        return Long.valueOf(allProperties.optLong(key));
    }

    public JSONObject getProperty(String key) {
        JSONObject allProperties = getAllProperties();
        if (allProperties == null) {
            return null;
        }
        return allProperties.optJSONObject(key);
    }

    public JSONObject getAllProperties() {
        return this.jsonObject;
    }
}
