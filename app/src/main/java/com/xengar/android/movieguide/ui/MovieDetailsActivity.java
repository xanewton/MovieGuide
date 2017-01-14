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

package com.xengar.android.movieguide.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.FavoriteMoviesProvider;
import com.xengar.android.movieguide.data.MovieDetails;
import com.xengar.android.movieguide.data.MovieDetailsData;
import com.xengar.android.movieguide.utils.JSONLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

import static android.app.DownloadManager.COLUMN_STATUS;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_BACKGROUND_PATH;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_DURATION;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_IMDB_ID;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_MOVIE_PLOT;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_NAME_TITLE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_ORIGINAL_LANGUAGE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_POSTER_PATH;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_VOTE_AVERAGE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_YEAR;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME;
import static com.xengar.android.movieguide.utils.JSONUtils.getDoubleValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getIntValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getListValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getStringValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getUriValue;

/**
 * MovieDetails
 */
public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "MovieID";
    private static final Uri URI =
            Uri.parse("content://" + FavoriteMoviesProvider.AUTHORITY + "/" + TABLE_NAME);

    private static final String IMDB_URI = "http://www.imdb.com/title";
    private static final String POSTER_BASE_URI = "http://image.tmdb.org/t/p/w185";
    private static final String POSTER_CAST_BASE_URI = "http://image.tmdb.org/t/p/w92";
    private static final String BACKGROUND_BASE_URI = "http://image.tmdb.org/t/p/w500";

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private MovieDetailsData detailsData;
    private MovieDetails data = null;
    private int movieID;
    private String movieTitle = " ";
    private CollapsingToolbarLayout collapsingToolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        movieID = intent.getIntExtra(EXTRA_MOVIE_ID, -1);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Movie Details data
        fetchMovieData();
        defineCollapsingToolbarLayoutBehaviour();

        loadBackdrop();
    }

    /**
     * Changes the CollapsingToolbarLayout to hide the title when the image is visible.
     */
    private void defineCollapsingToolbarLayoutBehaviour() {
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(movieTitle);
                    isShow = true;
                } else if(isShow) {
                    // there should a space between double quote otherwise it wont work
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(R.drawable.cheese_2).centerCrop().into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Gets the movie data.
     */
    private void fetchMovieData() {
        if (data == null) {
            FetchMovieTask task = new FetchMovieTask(FetchMovieTask.MOVIE_DETAILS);
            task.execute(movieID);
        } else {
            Log.v(TAG, "data = " + data.getDetailsData());
            populateDetails(detailsData = data.getDetailsData());
        }
    }

    /**
     * Fills the Movie Details in screen.
     *
     * @param container
     */
    private void populateDetails(final MovieDetailsData container) {

        if (container != null) {
            movieTitle = container.getMovieTitle();
            collapsingToolbar.setTitle(movieTitle);
        }

    }


    /**
     * Fetch task to get data
     */
    private class FetchMovieTask extends AsyncTask<Integer, Void, JSONObject> {

        public static final String MOVIE_DETAILS = "MovieDetails";
        private String requestType = null;

        // Constructor
        public FetchMovieTask(String requestType){
            this.requestType = requestType;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            String request = null;
            switch(requestType){
                case MOVIE_DETAILS:
                    request = "/movie/" + params[0];
                    break;
            }

            JSONObject jObj = JSONLoader.load(request, getString(R.string.THE_MOVIE_DB_API_TOKEN));
            return jObj;
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            super.onPostExecute(jObj);

            switch(requestType){
                case MOVIE_DETAILS:
                    processMovieDetails(jObj);
                    break;
            }
        }

        /**
         * Process the Movie Details data.
         */
        private void processMovieDetails(JSONObject jObj) {
            if (jObj != null) {
                try {
                    detailsData = new MovieDetailsData(getStringValue(jObj, "poster_path"),
                            movieID, getStringValue(jObj, "title"),
                            getStringValue(jObj, "overview"),
                            getStringValue(jObj, "release_date"),
                            getIntValue(jObj, "runtime", 0),
                            getDoubleValue(jObj, "vote_average", 0.0),
                            getStringValue(jObj, "backdrop_path"),
                            getStringValue(jObj, "original_language"),
                            getListValue(jObj, "production_countries"),
                            getListValue(jObj, "genres"),
                            getStringValue(jObj, "status"),
                            getUriValue(jObj, "imdb_uri" + IMDB_URI),
                            getListValue(jObj, "production_companies"));
                    populateDetails(detailsData);
                } catch (JSONException e) {
                    Log.e(TAG, "", e);
                }

            } else {
                final Cursor cursor = getContentResolver().query(
                        ContentUris.withAppendedId(URI, movieID),
                        new String[]{COLUMN_DURATION, COLUMN_YEAR, COLUMN_MOVIE_PLOT,
                                COLUMN_NAME_TITLE, COLUMN_POSTER_PATH, COLUMN_VOTE_AVERAGE,
                                COLUMN_BACKGROUND_PATH, COLUMN_ORIGINAL_LANGUAGE,
                                /*COLUMN_ORIGINAL_COUNTRIES, COLUMN_GENRES,*/ COLUMN_STATUS,
                                COLUMN_IMDB_ID/*, COLUMN_PROD_COMPANIES */},
                        null, null, null);
                Log.d(TAG, "Cursor = " + cursor.getCount());

                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    detailsData = new MovieDetailsData(cursor.getString(4), movieID,
                            cursor.getString(3), cursor.getString(2), cursor.getString(1),
                            cursor.getInt(0), cursor.getDouble(5), cursor.getString(6),
                            cursor.getString(7), Collections.<String>emptyList(),
                            Collections.<String>emptyList(), cursor.getString(10),
                            cursor.getString(11), Collections.<String>emptyList());
                    populateDetails(detailsData);
                }
            }
        }
    }
}
