package com.xengar.android.movieguide.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JSONUtils
 */
public final class JSONUtils {
    private JSONUtils() {}

    public static String getStringValue(JSONObject jobj, String name)
            throws JSONException {

        if (jobj.isNull(name)) {
            return null;
        } else {
            return jobj.getString(name);
        }
    }

    public static double getDoubleValue(JSONObject jobj, String name, double defaultValue)
            throws JSONException {
        if (jobj.isNull(name)) {
            return defaultValue;
        } else {
            return jobj.getDouble(name);
        }
    }

    public static int getIntValue(JSONObject jobj, String name, int defaultValue)
            throws JSONException {

        if (jobj.isNull(name)) {
            return defaultValue;
        } else {
            return jobj.getInt(name);
        }
    }

    public static List<String> getListValue(JSONObject jobj, String name)
            throws JSONException {

        if (jobj.isNull(name)) {
            return Collections.emptyList();
        } else {
            JSONArray jArray = jobj.getJSONArray(name);
            List<String> results = new ArrayList<>(jArray.length());
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject Jobject = jArray.getJSONObject(i);
                results.add(Jobject.getString("name"));
            }
            return results;
        }
    }

    public static String getUriValue(JSONObject jobj, String name)
            throws JSONException {

        if (jobj.isNull(name)) {
            return null;
        } else {
            return jobj.getString("imdb_id");
        }
    }
}
