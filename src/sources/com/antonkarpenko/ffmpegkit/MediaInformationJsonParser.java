package com.antonkarpenko.ffmpegkit;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MediaInformationJsonParser {
    public static final String KEY_CHAPTERS = "chapters";
    public static final String KEY_STREAMS = "streams";

    public static MediaInformation from(String ffprobeJsonOutput) {
        try {
            return fromWithError(ffprobeJsonOutput);
        } catch (JSONException e) {
            return null;
        }
    }

    public static MediaInformation fromWithError(String ffprobeJsonOutput) throws JSONException {
        JSONObject jsonObject = new JSONObject(ffprobeJsonOutput);
        JSONArray streamArray = jsonObject.optJSONArray(KEY_STREAMS);
        JSONArray chapterArray = jsonObject.optJSONArray(KEY_CHAPTERS);
        ArrayList<StreamInformation> streamList = new ArrayList<>();
        for (int i = 0; streamArray != null && i < streamArray.length(); i++) {
            JSONObject streamObject = streamArray.optJSONObject(i);
            if (streamObject != null) {
                streamList.add(new StreamInformation(streamObject));
            }
        }
        ArrayList<Chapter> chapterList = new ArrayList<>();
        for (int i2 = 0; chapterArray != null && i2 < chapterArray.length(); i2++) {
            JSONObject chapterObject = chapterArray.optJSONObject(i2);
            if (chapterObject != null) {
                chapterList.add(new Chapter(chapterObject));
            }
        }
        return new MediaInformation(jsonObject, streamList, chapterList);
    }
}
