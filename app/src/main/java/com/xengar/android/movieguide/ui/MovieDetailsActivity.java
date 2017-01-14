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

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.FavoriteMoviesProvider;
import com.xengar.android.movieguide.data.MovieDetails;
import com.xengar.android.movieguide.data.MovieDetailsData;
import com.xengar.android.movieguide.utils.JSONLoader;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Locale;

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
 * MovieDetails Activity
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

    // Details components
    private TextView title;
    private LinearLayout rating;
    private TextView textRating;
    private ImageView starRating;
    private NestedScrollView scrollView;
    private ImageView backgroundPoster;
    private ImageView moviePoster;
    private TextView movieDate;
    private TextView movieDuration;
    private TextView movieVoteAverage;
    private TextView textLanguage;
    private TextView textStatus;
    private TextView textGenres;
    private TextView textCountries;
    private TextView textProdCompanies;
    private TextView textIMDbId;
    private TextView moviePlot;

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

        loadBackgroundPoster();

        title = (TextView) findViewById(R.id.title);
        rating = (LinearLayout) findViewById(R.id.rating);
        textRating = (TextView) findViewById(R.id.text_rating);
        starRating = (ImageView) findViewById(R.id.star_rating);
        scrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        backgroundPoster = (ImageView) findViewById(R.id.background_poster);
        moviePoster = (ImageView) findViewById(R.id.movie_poster);
        movieDate = (TextView) findViewById(R.id.movie_date);
        movieDuration = (TextView) findViewById(R.id.movie_duration);
        textLanguage = (TextView) findViewById(R.id.text_language);
        textStatus = (TextView) findViewById(R.id.status);
        textProdCompanies = (TextView) findViewById(R.id.prod_companies);
        textGenres = (TextView) findViewById(R.id.genre);
        textCountries = (TextView) findViewById(R.id.countries);
        textIMDbId = (TextView) findViewById(R.id.imdb_id);
        moviePlot = (TextView) findViewById(R.id.movie_plot);
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

    private void loadBackgroundPoster() {
        final ImageView imageView = (ImageView) findViewById(R.id.background_poster);
        Glide.with(this).load(R.drawable.no_background_poster).centerCrop().into(imageView);
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
     * @param container
     */
    private void populateDetails(final MovieDetailsData container) {

        final Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGenerated(Palette palette) {
                if (this == null)
                    return;
                Log.v(TAG, "textSwatch.PaletteAsyncListener");

                Palette.Swatch textSwatch = palette.getMutedSwatch();
                Palette.Swatch bgSwatch = palette.getDarkVibrantSwatch();

                if (textSwatch != null && bgSwatch != null) {
                    title.setTextColor(textSwatch.getTitleTextColor());
                    title.setBackgroundColor(textSwatch.getRgb());
                    textRating.setTextColor(bgSwatch.getTitleTextColor());
                    rating.setBackgroundColor(bgSwatch.getRgb());
                    starRating.setBackgroundColor(bgSwatch.getTitleTextColor());
                } else if (bgSwatch != null) {
                    title.setBackgroundColor(bgSwatch.getRgb());
                    title.setTextColor(bgSwatch.getBodyTextColor());
                    rating.setBackgroundColor(bgSwatch.getBodyTextColor());
                    textRating.setTextColor(bgSwatch.getRgb());
                    starRating.setBackgroundColor(bgSwatch.getRgb());
                } else if (textSwatch != null) {
                    title.setBackgroundColor(textSwatch.getRgb());
                    title.setTextColor(textSwatch.getBodyTextColor());
                    rating.setBackgroundColor(textSwatch.getBodyTextColor());
                    textRating.setTextColor(textSwatch.getRgb());
                    starRating.setBackgroundColor(textSwatch.getRgb());
                } else {
                    title.setTextColor(getResources().getColor(R.color.textcolorPrimary, null));
                    title.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    textRating.setTextColor(getResources().getColor(R.color.textcolorSec, null));
                    rating.setBackgroundColor(getResources().getColor(R.color.colorBackground, null));
                }
            }
        };

        Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                if (backgroundPoster != null) {
                    Bitmap bitmapBg = ((BitmapDrawable) backgroundPoster.getDrawable()).getBitmap();
                    Palette.from(bitmapBg).generate(paletteAsyncListener);
                } else if (moviePoster != null) {
                    Bitmap bitmapBg = ((BitmapDrawable) moviePoster.getDrawable()).getBitmap();
                    Palette.from(bitmapBg).generate(paletteAsyncListener);
                }
            }
            @Override
            public void onError() {
                Log.v(TAG, "Callback error");
                Bitmap bitmapBg = ((BitmapDrawable) backgroundPoster.getDrawable()).getBitmap();
                Palette.from(bitmapBg).generate(paletteAsyncListener);
            }
        };

        if (container == null) {
            return;
        }
        PopulateDetailsTitle(container);
        PopulateDetailsPoster(container, callback);
        PopulateDetailsDateDurationRating(container);
        PopulateDetailsLanguage(container);
        PopulateDetailsGenres(container);
        PopulateDetailsCountries(container);
        PopulateDetailsProdCompanies(container);
        PopulateDetailsStatus(container);
        PopulateDetailsPlot(container);
    }

    /**
     * Populates Title in screen.
     * @param container
     */
    private void PopulateDetailsTitle(final MovieDetailsData container) {
        movieTitle = container.getMovieTitle();
        collapsingToolbar.setTitle(movieTitle);
        title.setText(movieTitle);
    }

    /**
     * Populates poster in screen.
     * @param container
     * @param callback
     */
    private void PopulateDetailsPoster(final MovieDetailsData container, Callback callback) {
        Picasso pic = Picasso.with(this);

        if (container.getMoviePoster() == null) {
            pic.load(R.drawable.no_movie_poster)
                    .fit().centerCrop()
                    .into(moviePoster);
        } else {
            pic.load(POSTER_BASE_URI + container.getMoviePoster())
                    .fit().centerCrop()
                    .error(R.drawable.no_movie_poster)
                    .into(moviePoster);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (container.getBackgroundPath() == null) {
                pic.load(R.drawable.no_background_poster)
                        .fit().centerCrop()
                        .into(backgroundPoster, callback);
            } else {
                pic.load(BACKGROUND_BASE_URI + container.getBackgroundPath())
                        .fit().centerCrop()
                        .error(R.drawable.no_background_poster)
                        .into(backgroundPoster, callback);
            }
        } else {
            if (container.getBackgroundPath() == null) {
                pic.load(R.drawable.no_background_poster)
                        .fit()
                        .into(backgroundPoster, callback);
            } else {
                pic.load(BACKGROUND_BASE_URI + container.getBackgroundPath())
                        .fit()
                        .error(R.drawable.no_background_poster)
                        .into(backgroundPoster, callback);
            }
        }
    }

    /**
     * Populates date, duration and rating in screen.
     * @param container
     */
    private void PopulateDetailsDateDurationRating(final MovieDetailsData container) {
        if (StringUtils.isNotBlank(container.getYear())) {
            movieDate.setText(container.getYear());
        } else {
            movieDate.setText(R.string.details_view_no_release_date);
        }

        if (container.getDuration() != null) {
            movieDuration.setText(container.getDuration()
                    + getString(R.string.details_view_text_minutes));
        } else {
            movieDuration.setVisibility(View.GONE);
        }

        if (container.getVoteAverage() != null) {
            textRating.setText(container.getVoteAverage()
                    + getString(R.string.details_view_text_vote_average_divider));
        } else {
            movieVoteAverage.setVisibility(View.GONE);
        }
    }

    /**
     * Populates language in screen.
     * @param container
     */
    private void PopulateDetailsLanguage(final MovieDetailsData container) {
        if (container.getOriginalLanguage() != null) {
            String name = "";
            Locale[] locales = Locale.getAvailableLocales();
            for (Locale l : locales) {
                if (l.getLanguage().equals(container.getOriginalLanguage())) {
                    name = l.getDisplayLanguage();
                }
            }
            if(name.isEmpty()) {
                textLanguage.setText(container.getOriginalLanguage());
            }
            else {
                textLanguage.setText(name);
            }
            textLanguage.setVisibility(View.VISIBLE);
        }
        else {
            textLanguage.setVisibility(View.GONE);
        }
    }

    /**
     * Populates genres in screen.
     * @param container
     */
    private void PopulateDetailsGenres(final MovieDetailsData container) {
        if(!container.getGenres().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for(String genre: container.getGenres() ) {
                builder.append(genre);
                builder.append(" | ");
            }
            builder.delete(builder.length()-3, builder.length());
            textGenres.setText(builder.toString());
            textGenres.setVisibility(View.VISIBLE);
        }
        else{
            textGenres.setVisibility(View.GONE);
        }
    }

    /**
     * Populates countries in screen.
     * @param container
     */
    private void PopulateDetailsCountries(final MovieDetailsData container) {
        if(!container.getOriginalCountries().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for(String country: container.getOriginalCountries() ) {
                builder.append(country);
                builder.append(" | ");
            }
            builder.delete(builder.length()-3, builder.length());
            textCountries.setText(builder.toString());
            textCountries.setVisibility(View.VISIBLE);
        }
        else{
            textCountries.setVisibility(View.GONE);
        }
    }

    /**
     * Populates Production Companies in screen.
     * @param container
     */
    private void PopulateDetailsProdCompanies(final MovieDetailsData container) {
        if(!container.getProductionCompanies().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for(String podCompany: container.getProductionCompanies() ) {
                builder.append(podCompany);
                builder.append(" | ");
            }
            builder.delete(builder.length()-3, builder.length());
            textProdCompanies.setText(builder.toString());
            textProdCompanies.setVisibility(View.VISIBLE);
        }
        else{
            textProdCompanies.setVisibility(View.GONE);
        }
    }

    /**
     * Populates status and IMDbId in screen.
     * @param container
     */
    private void PopulateDetailsStatus(final MovieDetailsData container) {
        if (container.getStatus() != null) {
            textStatus.setText(container.getStatus());
            textStatus.setVisibility(View.VISIBLE);
        }
        else {
            textStatus.setVisibility(View.GONE);
        }

        if(container.getImdbUri() != null  && container.getImdbUri().isEmpty()) {
            String builder = IMDB_URI + "/" +  container.getImdbUri();
            textIMDbId.setText(builder);
            textIMDbId.setVisibility(View.VISIBLE);
        }
        else{
            textIMDbId.setVisibility(View.GONE);
        }
    }

    /**
     * Populates Movie plot in screen.
     * @param container
     */
    private void PopulateDetailsPlot(final MovieDetailsData container) {
        if (StringUtils.isBlank(container.getPlot())) {
            moviePlot.setText(R.string.details_view_no_description);
        } else {
            moviePlot.setText(container.getPlot());
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
