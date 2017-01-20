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

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.xengar.android.movieguide.adapters.ImageAdapter;
import com.xengar.android.movieguide.data.FavoriteMoviesProvider;
import com.xengar.android.movieguide.data.MovieData;

import java.util.ArrayList;

import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_NAME_MOVIE_ID;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_NAME_TITLE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_POSTER_PATH;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME;
import static com.xengar.android.movieguide.utils.Constants.FAVORITE_MOVIES;

/**
 * FetchFavorite. Gets favorite items.
 */
public class FetchFavorite extends AsyncTask<Void, Void, ArrayList<MovieData>> {

    private static final String TAG = FetchFavorite.class.getSimpleName();
    private static final Uri FAVORITE_MOVIES_URI =
            Uri.parse("content://" + FavoriteMoviesProvider.AUTHORITY + "/" + TABLE_NAME);
    private final String posterBaseUri;
    private final ImageAdapter adapter;
    private final ContentResolver contentResolver;
    private final String requestType;
    private final Uri uri;


    // Constructor
    public FetchFavorite(String itemType, ImageAdapter adapter, ContentResolver contentResolver,
                         String posterBaseUri) {
        this.requestType = itemType;
        this.adapter = adapter;
        this.contentResolver = contentResolver;
        this.posterBaseUri = posterBaseUri;
        switch (itemType){
            case FAVORITE_MOVIES:
                this.uri = FAVORITE_MOVIES_URI;
                break;
            default:
                this.uri = FAVORITE_MOVIES_URI;
        }
    }

    @Override
    protected ArrayList<MovieData> doInBackground(Void... voids) {
        ArrayList<MovieData> moviePosters = new ArrayList<>();
        final Cursor cursor = contentResolver.query(uri,
                new String[]{ COLUMN_POSTER_PATH, COLUMN_NAME_MOVIE_ID, COLUMN_NAME_TITLE},
                null, null, null);

        if (cursor != null && cursor.getCount() != 0) {
            MovieData data;
            while (cursor.moveToNext()) {
                data = new MovieData(posterBaseUri + cursor.getString(0), cursor.getInt(1),
                        cursor.getString(2));
                moviePosters.add(data);
            }
        } else {
            Log.d(TAG, "Cursor is empty");
        }
        return moviePosters;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieData> moviePosters) {
        super.onPostExecute(moviePosters);
        if (moviePosters != null) {
            for (MovieData movie : moviePosters) {
                adapter.add(movie);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
