package com.antonkarpenko.ffmpegkit;

import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MediaInformation {
    public static final String KEY_BIT_RATE = "bit_rate";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_FORMAT = "format_name";
    public static final String KEY_FORMAT_LONG = "format_long_name";
    public static final String KEY_FORMAT_PROPERTIES = "format";
    public static final String KEY_SIZE = "size";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_TAGS = "tags";
    private final List<Chapter> chapters;
    private final JSONObject jsonObject;
    private final List<StreamInformation> streams;

    public MediaInformation(JSONObject jsonObject, List<StreamInformation> streams, List<Chapter> chapters) {
        this.jsonObject = jsonObject;
        this.streams = streams;
        this.chapters = chapters;
    }

    public String getFilename() {
        return getStringFormatProperty(KEY_FILENAME);
    }

    public String getFormat() {
        return getStringFormatProperty(KEY_FORMAT);
    }

    public String getLongFormat() {
        return getStringFormatProperty(KEY_FORMAT_LONG);
    }

    public String getDuration() {
        return getStringFormatProperty(KEY_DURATION);
    }

    public String getStartTime() {
        return getStringFormatProperty("start_time");
    }

    public String getSize() {
        return getStringFormatProperty(KEY_SIZE);
    }

    public String getBitrate() {
        return getStringFormatProperty("bit_rate");
    }

    public JSONObject getTags() {
        return getFormatProperty("tags");
    }

    public List<StreamInformation> getStreams() {
        return this.streams;
    }

    public List<Chapter> getChapters() {
        return this.chapters;
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

    public String getStringFormatProperty(String key) {
        JSONObject formatProperties = getFormatProperties();
        if (formatProperties == null || !formatProperties.has(key)) {
            return null;
        }
        return formatProperties.optString(key);
    }

    public Long getNumberFormatProperty(String key) {
        JSONObject formatProperties = getFormatProperties();
        if (formatProperties == null || !formatProperties.has(key)) {
            return null;
        }
        return Long.valueOf(formatProperties.optLong(key));
    }

    public JSONObject getFormatProperty(String key) {
        JSONObject formatProperties = getFormatProperties();
        if (formatProperties == null) {
            return null;
        }
        return formatProperties.optJSONObject(key);
    }

    public JSONObject getFormatProperties() {
        return this.jsonObject.optJSONObject(KEY_FORMAT_PROPERTIES);
    }

    public JSONObject getAllProperties() {
        return this.jsonObject;
    }
}
