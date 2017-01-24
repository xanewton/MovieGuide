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
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.squareup.picasso.Callback;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.ImageAdapter;
import com.xengar.android.movieguide.data.CastData;
import com.xengar.android.movieguide.data.FavoriteMoviesProvider;
import com.xengar.android.movieguide.data.ImageItem;
import com.xengar.android.movieguide.data.MovieData;
import com.xengar.android.movieguide.data.MovieDetails;
import com.xengar.android.movieguide.data.ReviewData;
import com.xengar.android.movieguide.data.TrailerData;
import com.xengar.android.movieguide.utils.ActivityUtils;
import com.xengar.android.movieguide.utils.JSONLoader;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_BACKGROUND_PATH;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_BUDGET;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_DURATION;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_HOMEPAGE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_IMDB_ID;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_MOVIE_PLOT;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_NAME_MOVIE_ID;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_NAME_TITLE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_ORIGINAL_LANGUAGE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_POSTER_PATH;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_REVENUE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_STATUS;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_VOTE_AVERAGE;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_VOTE_COUNT;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_YEAR;
import static com.xengar.android.movieguide.data.FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME;
import static com.xengar.android.movieguide.utils.Constants.BACKGROUND_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.IMDB_URI;
import static com.xengar.android.movieguide.utils.Constants.LAST_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_BACKGROUND_POSTER;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_ID;
import static com.xengar.android.movieguide.utils.Constants.POSTER_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.JSONUtils.getDoubleValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getIntValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getListValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getStringValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getUriValue;

/**
 * MovieDetails Activity
 */
public class MovieActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = MovieActivity.class.getSimpleName();
    private static final Uri URI =
            Uri.parse("content://" + FavoriteMoviesProvider.AUTHORITY + "/" + TABLE_NAME);
    private static final String SHORT_TEXT_PREVIEW = " \n <font color=#FF8A80>... show more</font>";
    private static final String LONG_TEXT_PREVIEW = " \n<font color=#FF8A80>... show less</font>";
    private static final String END_TEXT_PREVIEW = "\n<font color=#FF8A80> the end!</font>";

    private MovieData detailsData;
    private MovieDetails data = null;
    private List<TrailerData> trailerData;
    private int movieID;
    private final String movieTitle[] = {" "};
    private CollapsingToolbarLayout collapsingToolbar;

    // Details components
    private TextView title;
    private LinearLayout rating;
    private TextView textRating;
    private ImageView starRating;
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
    private TextView budget;
    private TextView revenue;
    private TextView homepage;

    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayer youTubePlayer;
    private LinearLayout trailerList;

    private List<ReviewData> reviewData;
    private LinearLayout reviewList;
    private boolean isReviewShown;

    private List<CastData> castData;
    private GridView gridview; // Cast list
    private final boolean[] gridViewResized = {false}; // boolean for resize gridview hack

    private FloatingActionButton fabAdd, fabDel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        movieID = prefs.getInt(MOVIE_ID, -1);

        // Save name of activity, in case of calling SettingsActivity
        ActivityUtils.saveStringToPreferences(getApplicationContext(), LAST_ACTIVITY,
                MOVIE_ACTIVITY);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) findViewById(R.id.title);
        rating = (LinearLayout) findViewById(R.id.rating);
        textRating = (TextView) findViewById(R.id.text_rating);
        starRating = (ImageView) findViewById(R.id.star_rating);
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
        trailerList = (LinearLayout) findViewById(R.id.movie_trailers);
        reviewList = (LinearLayout) findViewById(R.id.movie_reviews);
        gridview = (GridView) findViewById(R.id.cast_data);
        budget = (TextView) findViewById(R.id.budget);
        revenue = (TextView) findViewById(R.id.revenue);
        homepage = (TextView) findViewById(R.id.homepage);

        youTubePlayerFragment = YouTubePlayerFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.youtube_fragment, youTubePlayerFragment).commit();

        // Get Movie Details data
        fetchMovieData();
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ActivityUtils.changeCollapsingToolbarLayoutBehaviour(collapsingToolbar,
                (AppBarLayout) findViewById(R.id.appbar), movieTitle);
        ActivityUtils.loadNoBackgroundPoster(getApplicationContext(),
                (ImageView) findViewById(R.id.background_poster));
        showFavoriteButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ActivityUtils.launchSettingsActivity(getApplicationContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Defines if add or remove from Favorites should be initially visible for this movieId.
     */
    private void showFavoriteButtons() {
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabDel = (FloatingActionButton) findViewById(R.id.fab_minus);

        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(URI, movieID),
                new String[]{COLUMN_NAME_MOVIE_ID}, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            fabDel.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.VISIBLE);
        }
        if (cursor != null)
            cursor.close();
    }

    /**
     * Defines what to do when click on add/remove from Favorites buttons.
     * @param container
     */
    private void defineClickFavoriteButtons(final MovieData container) {
        final int DURATION = 1000;

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.favorite_movie_add_message), DURATION)
                        .setAction("Action", null).show();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_MOVIE_ID, movieID);
                values.put(COLUMN_NAME_TITLE, container.getTitle());
                values.put(COLUMN_MOVIE_PLOT, container.getPlot());
                values.put(COLUMN_POSTER_PATH, container.getPosterPath());
                values.put(COLUMN_YEAR, container.getYear());
                values.put(COLUMN_DURATION, container.getDuration());
                values.put(COLUMN_VOTE_AVERAGE, container.getVoteAverage());
                values.put(COLUMN_BACKGROUND_PATH, container.getBackgroundPath());
                values.put(COLUMN_ORIGINAL_LANGUAGE, container.getOriginalLanguage());
                values.put(COLUMN_STATUS, container.getStatus());
                values.put(COLUMN_IMDB_ID, container.getImdbUri());
                values.put(COLUMN_BUDGET, container.getBudget());
                values.put(COLUMN_REVENUE, container.getRevenue());
                values.put(COLUMN_HOMEPAGE, container.getHomepage());
                getContentResolver().insert(URI, values);

                fabAdd.setVisibility(View.INVISIBLE);
                fabDel.setVisibility(View.VISIBLE);
            }
        });

        fabDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.favorite_movie_del_message), DURATION)
                        .setAction("Action", null).show();
                getContentResolver().delete(URI, COLUMN_NAME_MOVIE_ID + " = ?",
                        new String[]{Integer.toString(movieID)} );

                fabAdd.setVisibility(View.VISIBLE);
                fabDel.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Gets the movie data.
     */
    private void fetchMovieData() {
        if (trailerList != null)
            trailerList.removeAllViews();
        if (reviewList != null)
            reviewList.removeAllViews();

        if (data == null) {
            FetchMovieTask detailsTask = new FetchMovieTask(FetchMovieTask.MOVIE_DETAILS);
            detailsTask.execute(movieID);
            FetchMovieTask trailersTask = new FetchMovieTask(FetchMovieTask.MOVIE_TRAILERS);
            trailersTask.execute(movieID);
            FetchMovieTask castTask = new FetchMovieTask(FetchMovieTask.MOVIE_CAST);
            castTask.execute(movieID);
            FetchMovieTask reviewsTask = new FetchMovieTask(FetchMovieTask.MOVIE_REVIEWS);
            reviewsTask.execute(movieID);
        } else {
            Log.v(TAG, "data = " + data.getDetailsData());
            populateDetails(detailsData = data.getDetailsData());
            populateTrailerList(trailerData = data.getTrailersData());
            populateCastList(castData = data.getCastData());
            populateReviewList(reviewData = data.getReviewsData(), isReviewShown);
        }
    }

    /**
     * Fills the Movie Details in screen.
     * @param container
     */
    private void populateDetails(final MovieData container) {

        if (container == null) {
            return;
        }

        final Palette.PaletteAsyncListener paletteAsyncListener =
                ActivityUtils.definePaletteAsyncListener(this, title, textRating, rating, starRating);

        Callback callback =
                ActivityUtils.defineCallback(paletteAsyncListener, backgroundPoster, moviePoster);

        PopulateDetailsTitle(container);
        PopulateDetailsPoster(container, callback);
        PopulateDetailsDateDurationRating(container);
        PopulateDetailsLanguage(container);
        PopulateDetailsGenres(container);
        PopulateDetailsCountries(container);
        PopulateDetailsProdCompanies(container);
        PopulateDetailsStatus(container);
        PopulateDetailsPlot(container);
        defineClickFavoriteButtons(container);
    }

    /**
     * Populates Title in screen.
     * @param container
     */
    private void PopulateDetailsTitle(final MovieData container) {
        movieTitle[0] = container.getTitle();
        collapsingToolbar.setTitle(movieTitle[0]);
        title.setText(movieTitle[0]);
    }

    /**
     * Populates poster in screen.
     * @param container
     * @param callback
     */
    private void PopulateDetailsPoster(final MovieData container, Callback callback) {

        ActivityUtils.loadImage(this, POSTER_BASE_URI + container.getPosterPath(), true,
                R.drawable.no_movie_poster, moviePoster, null);

        String backgroundPosterPath = container.getBackgroundPath();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityUtils.loadImage(this, BACKGROUND_BASE_URI + backgroundPosterPath, true,
                    R.drawable.no_background_poster, backgroundPoster, callback);
        } else {
            ActivityUtils.loadImage(this, BACKGROUND_BASE_URI + backgroundPosterPath, false,
                    R.drawable.no_background_poster, backgroundPoster, callback);
        }

        // Save background movie image poster to use in PersonProfile page.
        ActivityUtils.saveStringToPreferences(this, MOVIE_BACKGROUND_POSTER,
                container.getBackgroundPath());
    }

    /**
     * Populates date, duration and rating in screen.
     * @param container
     */
    private void PopulateDetailsDateDurationRating(final MovieData container) {
        if (StringUtils.isNotBlank(container.getYear())) {
            movieDate.setText(container.getYear());
        } else {
            movieDate.setText(R.string.details_view_no_release_date);
        }

        if (container.getDuration() != null) {
            movieDuration.setText(
                    String.format(Locale.ENGLISH, "%d %s", container.getDuration(),
                            getString(R.string.details_view_text_minutes)));
        } else {
            movieDuration.setVisibility(View.GONE);
        }

        if (container.getVoteAverage() != null) {
            textRating.setText(container.getVoteAverage()
                    + getString(R.string.details_view_text_vote_average_divider)
                    + " of " + container.getVoteCount());
        } else {
            movieVoteAverage.setVisibility(View.GONE);
        }
    }

    /**
     * Populates language in screen.
     * @param container
     */
    private void PopulateDetailsLanguage(final MovieData container) {
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
    private void PopulateDetailsGenres(final MovieData container) {
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
    private void PopulateDetailsCountries(final MovieData container) {
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
    private void PopulateDetailsProdCompanies(final MovieData container) {
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
     * Populates status, IMDbId, budget, revenue and homepage in screen.
     * @param container
     */
    private void PopulateDetailsStatus(final MovieData container) {
        if (container.getStatus() != null) {
            textStatus.setText(container.getStatus());
            textStatus.setVisibility(View.VISIBLE);
        }
        else {
            textStatus.setVisibility(View.GONE);
        }

        if (container.getBudget() != null) {
            budget.setText(container.getBudget());
        } else {
            budget.setVisibility(View.GONE);
        }
        if (container.getRevenue() != null) {
            revenue.setText(container.getRevenue());
        } else {
            revenue.setVisibility(View.GONE);
        }
        if (container.getHomepage() != null) {
            homepage.setText(container.getHomepage());
        } else {
            homepage.setVisibility(View.GONE);
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
    private void PopulateDetailsPlot(final MovieData container) {
        if (StringUtils.isBlank(container.getPlot())) {
            moviePlot.setText(R.string.details_view_no_description);
        } else {
            moviePlot.setText(container.getPlot());
        }
    }

    /**
     * Populates the trailers list in screen.
     * @param data
     */
    private void populateTrailerList(List<TrailerData> data) {
        Log.v(TAG, "populateTrailerList - data = " + data);

        for (final TrailerData trailer : data) {
            View view = getLayoutInflater().inflate(R.layout.movie_trailer_list_item, null);
            view.setTag(trailer);
            TextView trailerName = (TextView) view.findViewById(R.id.trailer_name);
            trailerName.setText(trailer.getTrailerName());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrailerData data = (TrailerData) v.getTag();
                    youTubePlayer.loadVideo(data.getTrailerUri().getQueryParameter("v"));
                }
            });
            trailerList.addView(view);
        }
        boolean isTrailerLoaded = true;

        if (!data.isEmpty()) {
            youTubePlayerFragment.initialize(getString(R.string.YOUTUBE_DATA_API_V3), this);
        } else {
            findViewById(R.id.youtube_fragment).setVisibility(View.GONE);
            findViewById(R.id.movie_trailers).setVisibility(View.GONE);
        }
    }

    public List<TrailerData> getTrailerData() {
        return trailerData;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult youTubeInitializationResult) {
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            Log.v(TAG, "trailerData " + trailerData);
            if (trailerData != null && !trailerData.isEmpty()) {
                Uri uri = trailerData.get(0).getTrailerUri();

                Log.v(TAG, "trailerData " + trailerData.size());
                String trailerCode = uri.getQueryParameter("v");
                if (trailerCode != null) {
                    this.youTubePlayer = youTubePlayer;
                    youTubePlayer.cueVideo(trailerCode);
                }
            } else {
                getFragmentManager().beginTransaction().remove(youTubePlayerFragment).commit();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        data = new MovieDetails(detailsData, trailerData, reviewData, castData);
        if (youTubePlayer != null) {
            youTubePlayer.release();
        }
    }

    /**
     * Populates the list of reviews in screen.
     * @param data
     * @param showReview
     */
    private void populateReviewList(final List<ReviewData> data, boolean showReview) {
        if (data == null || data.isEmpty()) {
            reviewList.setVisibility(View.GONE);
        }

        for (final ReviewData review : data) {
            View view = getLayoutInflater().inflate(R.layout.movie_review_list_item, null);
            TextView reviewAuthor = (TextView) view.findViewById(R.id.review_author);
            reviewAuthor.setText(review.getReviewAuthor());
            TextView reviewContentStart = (TextView) view.findViewById(R.id.review_content_start);
            TextView reviewContentEnd = (TextView) view.findViewById(R.id.review_content_end);
            StringBuilder buildStart = new StringBuilder();
            buildStart.append(review.getReviewContent());

            if (buildStart.length() > 72) {
                reviewContentStart.setText(
                        ActivityUtils.fromHtml(buildStart.substring(0, 72) + SHORT_TEXT_PREVIEW));
                reviewContentEnd.setText(
                        ActivityUtils.fromHtml(buildStart.substring(0, buildStart.length())
                                + LONG_TEXT_PREVIEW));
                Log.v(TAG, "reviewContentstart" + reviewContentStart.getText());
                Log.v(TAG, "reviewContentEnd" + reviewContentEnd.getText());

            } else {
                reviewContentStart.setText(buildStart);
                reviewContentEnd.setText(ActivityUtils.fromHtml(buildStart + END_TEXT_PREVIEW));
                Log.v(TAG, "reviewContentstart" + reviewContentStart.getText());
            }
            Log.v(TAG, "reviewContentEnd" + reviewContentEnd.getText());
            reviewContentStart.setVisibility(View.VISIBLE);
            reviewContentEnd.setVisibility(View.GONE);
            reviewList.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView contentStart = (TextView) v.findViewById(R.id.review_content_start);
                    TextView contentEnd = (TextView) v.findViewById(R.id.review_content_end);
                    Log.v(TAG, "reviewContentstart ON" + contentStart.getText());
                    Log.v(TAG, "reviewContentEnd ON" + contentEnd.getText());
                    if (!isReviewShown) {
                        contentEnd.setVisibility(View.VISIBLE);
                        contentStart.setVisibility(View.GONE);
                    } else {
                        contentStart.setVisibility(View.VISIBLE);
                        contentEnd.setVisibility(View.GONE);
                    }
                    isReviewShown = !isReviewShown;
                }
            });
        }
        reviewList.setVisibility(View.VISIBLE);
    }

    /**
     * Populates the Cast list in screen.
     * @param data
     */
    private void populateCastList(final List<CastData> data) {
        if (data == null || data.isEmpty()) {
            gridview.setVisibility(View.GONE);
            return;
        }

        // Add cast
        int index = 0;
        int maxCast = ActivityUtils.getPreferenceMaxCastItems(getApplicationContext());
        ImageAdapter adapter = new ImageAdapter(getApplicationContext(), ImageAdapter.CAST_IMAGE);
        // Assume the cast will always come sorted
        for (final CastData cast : data) {
            if (index == maxCast) {
                break;
            }
            index++;
            adapter.add(new ImageItem(cast.getCastImagePath(), cast.getPersonId(),
                                        cast.getCastName(), cast.getCharacter()));
        }
        adapter.notifyDataSetChanged();
        gridview.setAdapter(adapter);
        gridview.setVisibility(View.VISIBLE);

        ActivityUtils.changeGridViewHeight(gridview, adapter.getCount(), gridViewResized);
    }


    /**
     * Fetch task to get data
     */
    private class FetchMovieTask extends AsyncTask<Integer, Void, JSONObject> {

        public static final String MOVIE_DETAILS = "MovieDetails";
        public static final String MOVIE_TRAILERS = "MovieTrailers";
        public static final String MOVIE_REVIEWS = "MovieReviews";
        public static final String MOVIE_CAST = "MovieCast";
        private static final String TRAILER_BASE_URI = "http://www.youtube.com/watch?v=";
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
                case MOVIE_TRAILERS:
                    request = "/movie/" + params[0] + "/videos";
                    break;
                case MOVIE_REVIEWS:
                    request = "/movie/" + params[0] + "/reviews";
                    break;
                case MOVIE_CAST:
                    request = "/movie/" + params[0] + "/credits";
                    break;
            }

            return JSONLoader.load(request, getString(R.string.THE_MOVIE_DB_API_TOKEN));
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            super.onPostExecute(jObj);

            switch(requestType){
                case MOVIE_DETAILS:
                    processMovieDetails(jObj);
                    break;
                case MOVIE_TRAILERS:
                    processMovieTrailers(jObj);
                    break;
                case MOVIE_REVIEWS:
                    processMovieReviews(jObj);
                    break;
                case MOVIE_CAST:
                    processMovieCast(jObj);
                    break;
            }
        }

        /**
         * Process the Movie Details data.
         * @param jObj
         */
        private void processMovieDetails(JSONObject jObj) {
            if (jObj != null) {
                try {
                    detailsData = new MovieData(getStringValue(jObj, "poster_path"),
                            movieID, getStringValue(jObj, "title"),
                            getStringValue(jObj, "overview"),
                            getStringValue(jObj, "release_date"),
                            getIntValue(jObj, "runtime", 0),
                            getDoubleValue(jObj, "vote_average", 0.0),
                            getIntValue(jObj, "vote_count", 0),
                            getStringValue(jObj, "backdrop_path"),
                            getStringValue(jObj, "original_language"),
                            getListValue(jObj, "production_countries", "name"),
                            getListValue(jObj, "genres", "name"),
                            getStringValue(jObj, "status"),
                            getUriValue(jObj, "imdb_uri" + IMDB_URI),
                            getListValue(jObj, "production_companies", "name"),
                            getStringValue(jObj, "budget"),
                            getStringValue(jObj, "revenue"),
                            getStringValue(jObj, "homepage"));
                    populateDetails(detailsData);
                } catch (JSONException e) {
                    Log.e(TAG, "", e);
                }

            } else {
                final Cursor cursor = getContentResolver().query(
                        ContentUris.withAppendedId(URI, movieID),
                        new String[]{ COLUMN_POSTER_PATH, COLUMN_NAME_TITLE, COLUMN_MOVIE_PLOT,
                                COLUMN_YEAR, COLUMN_DURATION, COLUMN_VOTE_AVERAGE, COLUMN_VOTE_COUNT,
                                COLUMN_BACKGROUND_PATH, COLUMN_ORIGINAL_LANGUAGE,
                                /*COLUMN_ORIGINAL_COUNTRIES, COLUMN_GENRES,*/ COLUMN_STATUS,
                                COLUMN_IMDB_ID/*, COLUMN_PROD_COMPANIES */, COLUMN_BUDGET,
                                COLUMN_REVENUE, COLUMN_HOMEPAGE},
                        null, null, null);

                if (cursor.getCount() != 0) {
                    Log.d(TAG, "Cursor = " + cursor.getCount());
                    cursor.moveToFirst();
                    // Note: Better if it matches the query order
                    detailsData = new MovieData(cursor.getString(0), movieID,
                            cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getInt(4), cursor.getDouble(5), cursor.getInt(6),
                            cursor.getString(7), cursor.getString(8), Collections.<String>emptyList(),
                            Collections.<String>emptyList(), cursor.getString(9),
                            cursor.getString(10), Collections.<String>emptyList(), cursor.getString(11),
                            cursor.getString(12), cursor.getString(13));
                    populateDetails(detailsData);
                }
                cursor.close();
            }
        }

        /**
         * Process the Movie Trailers data.
         * @param jObj
         */
        private void processMovieTrailers(JSONObject jObj) {
            if (jObj != null) {
                trailerData = new ArrayList<TrailerData>();
                try {
                    JSONArray array = jObj.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        trailerData.add(new TrailerData(
                                            Uri.parse(TRAILER_BASE_URI + object.getString("key")),
                                            object.getString("name")));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "", e);
                }
                populateTrailerList(trailerData);
            }
        }

        /**
         * Process the Movie Reviews data.
         * @param jObj
         */
        private void processMovieReviews(JSONObject jObj) {
            if (jObj != null) {
                reviewData = new ArrayList<ReviewData>();
                try {
                    JSONArray array = jObj.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        reviewData.add(new ReviewData(object.getString("author"),
                                object.getString("content")));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "", e);
                }
                populateReviewList(reviewData, isReviewShown);
            }
        }

        /**
         * Process the Movie cast data.
         * @param jObj
         */
        private void processMovieCast(JSONObject jObj) {
            if (jObj != null) {
                castData = new ArrayList<CastData>();
                try {
                    JSONArray array = jObj.getJSONArray("cast");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        castData.add(
                                new CastData(object.getString("name"),
                                        object.getString("profile_path"),
                                        object.getString("character"),
                                        object.getInt("id"),
                                        object.getInt("order")));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "", e);
                }
                populateCastList(castData);
            }
        }
    }
}
