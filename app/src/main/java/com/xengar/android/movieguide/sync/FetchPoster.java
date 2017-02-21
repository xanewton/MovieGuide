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
package com.xengar.android.movieguide.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.xengar.android.movieguide.adapters.ImageAdapter;
import com.xengar.android.movieguide.data.ImageItem;
import com.xengar.android.movieguide.utils.JSONLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.xengar.android.movieguide.utils.Constants.LOG;
import static com.xengar.android.movieguide.utils.Constants.NOW_PLAYING_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.ON_THE_AIR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.UPCOMING_MOVIES;

/**
 * FetchPoster items
 */
public class FetchPoster extends AsyncTask<Integer, Void, ArrayList<ImageItem>> {

    private static final String TAG = FetchPoster.class.getSimpleName();
    private static final String MOVIE_TOP_RATED = "/movie/top_rated";
    private static final String MOVIE_NOW_PLAYING = "/movie/now_playing";
    private static final String MOVIE_UPCOMING = "/movie/upcoming";
    private static final String DISCOVER_MOVIE = "/discover/movie";
    private static final String TV_POPULAR = "/tv/popular";
    private static final String TV_TOP_RATED = "/tv/top_rated";
    private static final String TV_ON_THE_AIR = "/tv/on_the_air";

    private final FetchItemListener fetchItemListener;
    private final String posterBaseUri;
    private final ImageAdapter adapter;
    private final String apiKey;
    private final String itemType;
    private String requestType;
    private String mLang;

    // Constructor
    public FetchPoster(String itemType, ImageAdapter adapter,
                       FetchItemListener fetchItemListener, String apiKey,
                       String posterBaseUri, String sortOrder, String language) {
        this.itemType = itemType;
        this.fetchItemListener = fetchItemListener;
        this.posterBaseUri = posterBaseUri;
        this.adapter = adapter;
        this.apiKey = apiKey;
        this.mLang =language;

        // assign the category to query
        switch (itemType){
            case TOP_RATED_MOVIES:
                this.requestType = MOVIE_TOP_RATED + "?page=";
                break;
            case NOW_PLAYING_MOVIES:
                this.requestType = MOVIE_NOW_PLAYING + "?page=";
                break;
            case UPCOMING_MOVIES:
                this.requestType = MOVIE_UPCOMING + "?page=";
                break;
            case POPULAR_MOVIES:
                this.requestType = DISCOVER_MOVIE + "?sort_by=" + sortOrder + "&page=";
                break;
            case POPULAR_TV_SHOWS:
                this.requestType = TV_POPULAR + "?page=";
                break;
            case TOP_RATED_TV_SHOWS:
                this.requestType = TV_TOP_RATED + "?page=";
                break;
            case ON_THE_AIR_TV_SHOWS:
                this.requestType = TV_ON_THE_AIR + "?page=";
                break;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<ImageItem> posters) {
        super.onPostExecute(posters);
        if (posters != null) {
            int adds = 0;
            for (ImageItem poster : posters) {
                adds += adapter.add(poster);
            }
            if (adds != 0)
                adapter.notifyDataSetChanged();
            fetchItemListener.onFetchCompleted();
        } else {
            fetchItemListener.onFetchFailed();
        }
    }

    @Override
    protected ArrayList<ImageItem> doInBackground(Integer... params) {
        ArrayList<ImageItem> posters = new ArrayList<>();
        try {
            JSONObject jObj = JSONLoader.load(requestType + params[0], apiKey, mLang);
            if (jObj == null) {
                if (LOG) {
                    Log.w(TAG, "Can not load the data from remote service");
                }
                return null;
            }

            String itemTitle = (itemType.equals(POPULAR_TV_SHOWS) || itemType.equals(TOP_RATED_TV_SHOWS)
                    || itemType.equals(ON_THE_AIR_TV_SHOWS))? "name" : "title";
            JSONArray itemsArray = jObj.getJSONArray("results");
            JSONObject item;
            for (int i = 0; i < itemsArray.length(); i++) {
                item = itemsArray.optJSONObject(i);
                posters.add(new ImageItem(posterBaseUri + item.getString("poster_path"),
                        item.getInt("id"), item.getString(itemTitle), null, null));
            }
        } catch (JSONException e) {
            if (LOG) {
                Log.e(TAG, "Error ", e);
            }
        }
        return posters;
    }
}
