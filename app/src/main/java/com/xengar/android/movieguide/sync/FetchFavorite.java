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
import com.xengar.android.movieguide.data.FavoritesContract;
import com.xengar.android.movieguide.data.ImageItem;

import java.util.ArrayList;

import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_MOVIE_ID;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_NAME;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_POSTER_PATH;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_TITLE;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_TV_SHOW_ID;
import static com.xengar.android.movieguide.utils.Constants.FAVORITE_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.FAVORITE_TV_SHOWS;

/**
 * FetchFavorite. Gets favorite items.
 */
public class FetchFavorite extends AsyncTask<Void, Void, ArrayList<ImageItem>> {

    private static final String TAG = FetchFavorite.class.getSimpleName();
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
        switch (requestType){
            case FAVORITE_TV_SHOWS:
                this.uri = FavoritesContract.FavoriteColumns.uriTVShow;
                break;
            case FAVORITE_MOVIES:
            default:
                this.uri = FavoritesContract.FavoriteColumns.uriMovie;
                break;
        }
    }

    @Override
    protected ArrayList<ImageItem> doInBackground(Void... voids) {
        ArrayList<ImageItem> posters = new ArrayList<>();
        String[] columns = new String[]{""};
        switch (requestType){
            case FAVORITE_MOVIES:
                columns = new String[]{ COLUMN_POSTER_PATH, COLUMN_MOVIE_ID, COLUMN_TITLE};
                break;
            case FAVORITE_TV_SHOWS:
                columns = new String[]{ COLUMN_POSTER_PATH, COLUMN_TV_SHOW_ID, COLUMN_NAME};
                break;
        }

        final Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            ImageItem data;
            while (cursor.moveToNext()) {
                data = new ImageItem(posterBaseUri + cursor.getString(0), cursor.getInt(1),
                        cursor.getString(2), null);
                posters.add(data);
            }
        } else {
            Log.d(TAG, "Cursor is empty");
        }
        if (cursor != null)
            cursor.close();
        return posters;
    }

    @Override
    protected void onPostExecute(ArrayList<ImageItem> posters) {
        super.onPostExecute(posters);
        if (posters != null) {
            for (ImageItem poster : posters) {
                adapter.add(poster);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
