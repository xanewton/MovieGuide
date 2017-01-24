/*
 * Copyright (C) 2017 Angel Garcia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xengar.android.movieguide.utils;

import com.xengar.android.movieguide.data.MovieCreditCast;
import com.xengar.android.movieguide.data.MovieCreditCrew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    /**
     * Return a list of strings with data from the field.
     * @param jobj
     * @param name
     * @param field
     * @return
     * @throws JSONException
     */
    public static List<String> getListValue(JSONObject jobj, String name, String field)
            throws JSONException {

        if (jobj.isNull(name)) {
            return Collections.emptyList();
        } else {
            JSONArray jArray = jobj.getJSONArray(name);
            List<String> results = new ArrayList<>(jArray.length());
            JSONObject Jobject;
            for (int i = 0; i < jArray.length(); i++) {
                Jobject = jArray.getJSONObject(i);
                results.add(Jobject.getString(field));
            }
            return results;
        }
    }

    public static String getArrayValue(JSONObject jobj, String name)
            throws JSONException {

        if (jobj.isNull(name)) {
            return null;
        } else {
            JSONArray jArray = jobj.getJSONArray(name);
            String result = jArray.getString(0);
            for (int i = 1; i < jArray.length(); i++) {
                result += "," + jArray.getString(i);
            }
            return result;
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

    /**
     * Gets a List of credit cast.
     * @param jobj
     * @param
     * @return
     * @throws JSONException
     */
    public static List<MovieCreditCast> getMovieCreditCastList(JSONObject jobj, String object,
                                                               String name)
            throws JSONException {
        if (jobj.isNull(object)) {
            return Collections.emptyList();
        } else {
            JSONObject Jobject = jobj.getJSONObject(object);
            if (Jobject.isNull(name)){
                return Collections.emptyList();
            } else {
                List<MovieCreditCast> list = new ArrayList<>();
                JSONArray jArray = Jobject.getJSONArray(name);
                JSONObject jsonObject;
                for (int i = 0; i < jArray.length(); i++) {
                    jsonObject = jArray.getJSONObject(i);
                    MovieCreditCast creditCast = new MovieCreditCast(
                            jsonObject.getInt("id"),
                            jsonObject.getString("character"),
                            jsonObject.getString("title"),
                            jsonObject.getString("poster_path"),
                            jsonObject.getString("release_date")
                    );
                    list.add(i, creditCast);
                }
                Collections.sort(list, new Comparator<MovieCreditCast>() {
                    @Override public int compare(MovieCreditCast p1, MovieCreditCast p2) {
                        return p2.getReleaseYear() - p1.getReleaseYear(); // Descending
                    }
                });
                return list;
            }
        }
    }

    /**
     * Gets a List of crew cast.
     * @param jobj
     * @param
     * @return
     * @throws JSONException
     */
    public static List<MovieCreditCrew> getMovieCreditCrewList(JSONObject jobj, String object,
                                                               String name)
            throws JSONException {
        if (jobj.isNull(object)) {
            return Collections.emptyList();
        } else {
            JSONObject Jobject = jobj.getJSONObject(object);
            if (Jobject.isNull(name)){
                return Collections.emptyList();
            } else {
                List<MovieCreditCrew> list = new ArrayList<>();
                JSONArray jArray = Jobject.getJSONArray(name);
                JSONObject jsonObject;
                for (int i = 0; i < jArray.length(); i++) {
                    jsonObject = jArray.getJSONObject(i);
                    MovieCreditCrew creditCrew = new MovieCreditCrew(
                            jsonObject.getInt("id"),
                            jsonObject.getString("job"),
                            jsonObject.getString("title"),
                            jsonObject.getString("poster_path"),
                            jsonObject.getString("release_date")
                    );
                    list.add(i, creditCrew);
                }
                Collections.sort(list, new Comparator<MovieCreditCrew>() {
                    @Override public int compare(MovieCreditCrew p1, MovieCreditCrew p2) {
                        return p2.getReleaseYear() - p1.getReleaseYear(); // Descending
                    }
                });
                return list;
            }
        }
    }

}
