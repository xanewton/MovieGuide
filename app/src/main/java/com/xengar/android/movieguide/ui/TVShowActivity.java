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
import com.xengar.android.movieguide.data.FavoritesContract;
import com.xengar.android.movieguide.data.ImageItem;
import com.xengar.android.movieguide.data.TVShowData;
import com.xengar.android.movieguide.data.TVShowDetails;
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

import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_BACKGROUND_PATH;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_FIRST_AIR_DATE;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_GENRES;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_HOMEPAGE;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_LAST_AIR_DATE;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_NAME;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_NUM_EPISODES;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_NUM_SEASONS;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_ORIGINAL_COUNTRIES;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_ORIGINAL_LANGUAGE;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_OVERVIEW;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_POSTER_PATH;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_PROD_COMPANIES;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_STATUS;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_TV_SHOW_ID;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_VOTE_AVERAGE;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_VOTE_COUNT;
import static com.xengar.android.movieguide.utils.Constants.BACKGROUND_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.KNOWN_FOR_BACKGROUND_POSTER;
import static com.xengar.android.movieguide.utils.Constants.LAST_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.SIZE_W342;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOW_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOW_ID;
import static com.xengar.android.movieguide.utils.JSONUtils.getArrayValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getDoubleValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getIntValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getListValue;
import static com.xengar.android.movieguide.utils.JSONUtils.getStringValue;

public class TVShowActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = TVShowActivity.class.getSimpleName();
    private static final Uri URI = FavoritesContract.FavoriteColumns.uriTVShow;
    private int tvShowId;
    private CollapsingToolbarLayout collapsingToolbar;
    private final String[] tvShowTitle = {" "};

    private TVShowDetails data = null;
    private TVShowData detailsData;
    private List<TrailerData> trailerData;

    // Details components
    private TextView title;
    private LinearLayout rating;
    private TextView textRating;
    private ImageView starRating;
    private ImageView backgroundPoster;
    private ImageView tvShowPoster;
    private TextView textGenres;
    private TextView textCountries;
    private TextView textProdCompanies;
    private TextView homepage;
    private TextView textStatus;
    private TextView overview;
    private TextView textLanguage;
    private TextView textFirstAirDate;
    private TextView textLastAirDate;
    private TextView textNumEpisodes;
    private TextView textNumSeasons;

    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayer youTubePlayer;
    private LinearLayout trailerList;

    private List<CastData> castData;
    private GridView gridview; // Cast list
    private final boolean[] gridViewResized = {false}; // boolean for resize gridview hack

    private FloatingActionButton fabAdd, fabDel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvshow);

        // Save name of activity, in case of calling SettingsActivity
        ActivityUtils.saveStringToPreferences(getApplicationContext(), LAST_ACTIVITY,
                TV_SHOW_ACTIVITY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        tvShowId = prefs.getInt(TV_SHOW_ID, -1);
        tvShowTitle[0] = "Sample TV Show";

        title = (TextView) findViewById(R.id.title);
        rating = (LinearLayout) findViewById(R.id.rating);
        textRating = (TextView) findViewById(R.id.text_rating);
        starRating = (ImageView) findViewById(R.id.star_rating);
        backgroundPoster = (ImageView) findViewById(R.id.background_poster);
        tvShowPoster = (ImageView) findViewById(R.id.tvshow_poster);
        textGenres = (TextView) findViewById(R.id.genre);
        textCountries = (TextView) findViewById(R.id.countries);
        textProdCompanies = (TextView) findViewById(R.id.prod_companies);
        homepage = (TextView) findViewById(R.id.homepage);
        textStatus = (TextView) findViewById(R.id.status);
        textLanguage = (TextView) findViewById(R.id.text_language);
        overview = (TextView) findViewById(R.id.overview);
        textFirstAirDate = (TextView) findViewById(R.id.first_air_date);
        textLastAirDate = (TextView) findViewById(R.id.last_air_date);
        textNumEpisodes = (TextView) findViewById(R.id.num_episodes);
        textNumSeasons = (TextView) findViewById(R.id.num_seasons);
        gridview = (GridView) findViewById(R.id.cast_data);
        trailerList = (LinearLayout) findViewById(R.id.tvshow_trailers);

        youTubePlayerFragment = YouTubePlayerFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.youtube_fragment, youTubePlayerFragment).commit();

        // Get TV Show Details data
        fetchTVShowData();

        ActivityUtils.loadNoBackgroundPoster(getApplicationContext(),
                (ImageView) findViewById(R.id.background_poster));
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ActivityUtils.changeCollapsingToolbarLayoutBehaviour(collapsingToolbar,
                (AppBarLayout) findViewById(R.id.appbar), tvShowTitle);
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
        if (id == R.id.action_search) {
            ActivityUtils.launchSearchActivity(getApplicationContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        data = new TVShowDetails(detailsData, trailerData, castData);
        if (youTubePlayer != null) {
            youTubePlayer.release();
        }
    }

    /**
     * Defines if add or remove from Favorites should be initially visible for this movieId.
     */
    private void showFavoriteButtons() {
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabDel = (FloatingActionButton) findViewById(R.id.fab_minus);

        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(URI, tvShowId),
                new String[]{COLUMN_TV_SHOW_ID}, null, null, null);
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
     * @param container TVShowData
     */
    private void defineClickFavoriteButtons(final TVShowData container) {
        final int DURATION = 1000;

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.favorite_tvshow_add_message), DURATION)
                        .setAction("Action", null).show();
                ContentValues values = new ContentValues();
                values.put(COLUMN_TV_SHOW_ID, tvShowId);
                values.put(COLUMN_NAME, container.getName());
                values.put(COLUMN_OVERVIEW, container.getOverview());
                values.put(COLUMN_POSTER_PATH, container.getPosterPath());
                values.put(COLUMN_BACKGROUND_PATH, container.getBackgroundPath());
                values.put(COLUMN_VOTE_AVERAGE, container.getVoteAverage());
                values.put(COLUMN_VOTE_COUNT, container.getVoteCount());
                values.put(COLUMN_ORIGINAL_LANGUAGE, container.getOriginalLanguage());
                values.put(COLUMN_ORIGINAL_COUNTRIES, container.getOriginalCountries());
                values.put(COLUMN_GENRES, container.getGenres().toString());
                values.put(COLUMN_STATUS, container.getStatus());
                values.put(COLUMN_PROD_COMPANIES, container.getProductionCompanies().toString());
                values.put(COLUMN_HOMEPAGE, container.getHomepage());
                values.put(COLUMN_FIRST_AIR_DATE, container.getFirstAirDate());
                values.put(COLUMN_LAST_AIR_DATE, container.getLastAirDate());
                values.put(COLUMN_NUM_SEASONS, container.getNumSeasons());
                values.put(COLUMN_NUM_EPISODES, container.getNumEpisodes());
                getContentResolver().insert(URI, values);

                fabAdd.setVisibility(View.INVISIBLE);
                fabDel.setVisibility(View.VISIBLE);
            }
        });

        fabDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.favorite_tvshow_del_message), DURATION)
                        .setAction("Action", null).show();
                getContentResolver().delete(URI, COLUMN_TV_SHOW_ID + " = ?",
                        new String[]{Integer.toString(tvShowId)} );

                fabAdd.setVisibility(View.VISIBLE);
                fabDel.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Get the TV Show data.
     */
    private void fetchTVShowData() {
        if (trailerList != null)
            trailerList.removeAllViews();

        if (data == null) {
            FetchTVShowTask detailsTask = new FetchTVShowTask(FetchTVShowTask.TV_SHOW_DETAILS);
            detailsTask.execute(tvShowId);
            FetchTVShowTask trailersTask = new FetchTVShowTask(FetchTVShowTask.TV_SHOW_TRAILERS);
            trailersTask.execute(tvShowId);
            FetchTVShowTask castTask = new FetchTVShowTask(FetchTVShowTask.TV_SHOW_CAST);
            castTask.execute(tvShowId);
        } else {
            Log.v(TAG, "data = " + data.getDetailsData());
            populateDetails(detailsData = data.getDetailsData());
            populateTrailerList(trailerData = data.getTrailersData());
            populateCastList(castData = data.getCastData());
        }
    }

    /**
     * Fills the TV Show Details in screen.
     * @param container TVShowData
     */
    private void populateDetails(final TVShowData container) {
        if (container == null) {
            return;
        }

        final Palette.PaletteAsyncListener paletteAsyncListener =
                ActivityUtils.definePaletteAsyncListener(this, title, textRating, rating, starRating);

        Callback callback =
                ActivityUtils.defineCallback(paletteAsyncListener, backgroundPoster, tvShowPoster);

        PopulateDetailsTitle(container);
        PopulateDetailsPoster(container, callback);
        PopulateDetailsGenresCountries(container);
        PopulateDetailsProdCompanies(container);
        PopulateDetailsStatus(container);
        PopulateDetailsDates(container);
        defineClickFavoriteButtons(container);
    }

    /**
     * Populates Title in screen.
     * @param container TVShowData
     */
    private void PopulateDetailsTitle(final TVShowData container) {
        tvShowTitle[0] = container.getName();
        collapsingToolbar.setTitle(tvShowTitle[0]);
        title.setText(tvShowTitle[0]);

        if (container.getVoteAverage() != null) {
            textRating.setText(container.getVoteAverage()
                    + getString(R.string.details_view_text_vote_average_divider)
                    + " of " + container.getVoteCount());
        }
    }

    /**
     * Populates poster in screen.
     * @param container TVShowData
     * @param callback callback
     */
    private void PopulateDetailsPoster(final TVShowData container, Callback callback) {

        ActivityUtils.loadImage(this, TMDB_IMAGE_URL + SIZE_W342 + container.getPosterPath(), true,
                R.drawable.disk_reel, tvShowPoster, null);

        String backgroundPosterPath = container.getBackgroundPath();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityUtils.loadImage(this, BACKGROUND_BASE_URI + backgroundPosterPath, true,
                    R.drawable.no_background_poster, backgroundPoster, callback);
        } else {
            ActivityUtils.loadImage(this, BACKGROUND_BASE_URI + backgroundPosterPath, false,
                    R.drawable.no_background_poster, backgroundPoster, callback);
        }

        // Save background movie image poster to use in PersonProfile page.
        ActivityUtils.saveStringToPreferences(this, KNOWN_FOR_BACKGROUND_POSTER,
                container.getBackgroundPath());
    }

    /**
     * Populates genres and countries in screen.
     * @param container TVShowData
     */
    private void PopulateDetailsGenresCountries(final TVShowData container) {
        if(!container.getGenres().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for(String genre: container.getGenres() ) {
                builder.append(genre);
                builder.append(" | ");
            }
            builder.delete(builder.length()-3, builder.length());
            textGenres.setText(builder.toString());
            textGenres.setVisibility(View.VISIBLE);
        } else{
            textGenres.setVisibility(View.GONE);
        }

        if(!container.getOriginalCountries().isEmpty()) {
            textCountries.setText(container.getOriginalCountries());
        } else {
            textCountries.setVisibility(View.GONE);
        }
    }

    /**
     * Populates Production Companies in screen.
     * @param container TVShowData
     */
    private void PopulateDetailsProdCompanies(final TVShowData container) {
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
     * Populates status, homepage, overview and languages in screen.
     * @param container TVShowData
     */
    private void PopulateDetailsStatus(final TVShowData container) {
        if (container.getStatus() != null) {
            textStatus.setText(container.getStatus());
            textStatus.setVisibility(View.VISIBLE);
        } else {
            textStatus.setVisibility(View.GONE);
        }

        if (container.getHomepage() != null) {
            homepage.setText(container.getHomepage());
        } else {
            homepage.setVisibility(View.GONE);
        }

        if (StringUtils.isBlank(container.getOverview())) {
            overview.setText(R.string.details_view_no_description);
        } else {
            overview.setText(container.getOverview());
        }

        if (container.getOriginalLanguage() != null){
            textLanguage.setText(container.getOriginalLanguage());
        } else {
            textLanguage.setVisibility(View.GONE);
        }
    }

    /**
     * Populates first air date, last air date, episodes and seasons in screen.
     * @param container TVShowData
     */
    private void PopulateDetailsDates(final TVShowData container) {
        if (container.getFirstAirDate() != null) {
            textFirstAirDate.setText(container.getFirstAirDate());
        } else {
            textFirstAirDate.setVisibility(View.GONE);
        }

        if (container.getLastAirDate() != null) {
            textLastAirDate.setText(container.getLastAirDate());
        } else {
            textLastAirDate.setVisibility(View.GONE);
        }

        if (container.getNumEpisodes() != 0) {
            textNumEpisodes.setText(getString(R.string.details_episodes, container.getNumEpisodes()));
        } else {
            textNumEpisodes.setVisibility(View.GONE);
        }
        if (container.getNumSeasons() != 0) {
            textNumSeasons.setText(getString(R.string.details_seasons, container.getNumSeasons()));
        } else {
            textNumSeasons.setVisibility(View.GONE);
        }
    }

    /**
     * Populates the trailers list in screen.
     * @param data list of trailer data
     */
    private void populateTrailerList(List<TrailerData> data) {
        Log.v(TAG, "populateTrailerList - data = " + data);

        for (final TrailerData trailer : data) {
            View view = getLayoutInflater().inflate(R.layout.trailer_list_item, null);
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

        if (!data.isEmpty()) {
            youTubePlayerFragment.initialize(getString(R.string.YOUTUBE_DATA_API_V3), this);
        } else {
            findViewById(R.id.youtube_fragment).setVisibility(View.GONE);
            findViewById(R.id.tvshow_trailers).setVisibility(View.GONE);
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

    /**
     * Populates the Cast list in screen.
     * @param data list of cast data
     */
    private void populateCastList(final List<CastData> data) {
        if (data == null || data.isEmpty()) {
            gridview.setVisibility(View.GONE);
            return;
        }

        // Add cast
        int index = 0, adds = 0;
        int maxCast = ActivityUtils.getPreferenceMaxCastItems(getApplicationContext());
        ImageAdapter adapter = new ImageAdapter(getApplicationContext(), ImageAdapter.CAST_IMAGE);
        // Assume the cast will always come sorted
        for (final CastData cast : data) {
            if (index == maxCast) {
                break;
            }
            index++;
            adds += adapter.add(new ImageItem(cast.getCastImagePath(), cast.getPersonId(),
                        cast.getCastName(), cast.getCharacter(), null));
        }
        if (adds != 0)
            adapter.notifyDataSetChanged();
        gridview.setAdapter(adapter);
        gridview.setVisibility(View.VISIBLE);

        ActivityUtils.changeGridViewHeight(gridview, adapter.getCount(), gridViewResized);
    }




    /**
     * Fetch task to get data
     */
    private class FetchTVShowTask extends AsyncTask<Integer, Void, JSONObject> {

        public static final String TV_SHOW_DETAILS = "TVShowDetails";
        public static final String TV_SHOW_TRAILERS = "TVShowTrailers";
        public static final String TV_SHOW_CAST = "TVShowCast";
        private static final String TRAILER_BASE_URI = "http://www.youtube.com/watch?v=";
        private String requestType = null;

        // Constructor
        public FetchTVShowTask(String requestType){
            this.requestType = requestType;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            String request = null;
            switch(requestType){
                case TV_SHOW_DETAILS:
                    request = "/tv/" + params[0];
                    break;
                case TV_SHOW_TRAILERS:
                    request = "/tv/" + params[0] + "/videos";
                    break;
                case TV_SHOW_CAST:
                    request = "/tv/" + params[0] + "/credits";
                    break;
            }

            return JSONLoader.load(request, getString(R.string.THE_MOVIE_DB_API_TOKEN));
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            super.onPostExecute(jObj);

            switch(requestType){
                case TV_SHOW_DETAILS:
                    processTVShowDetails(jObj);
                    break;
                case TV_SHOW_TRAILERS:
                    processTVShowTrailers(jObj);
                    break;
                case TV_SHOW_CAST:
                    processTVShowCast(jObj);
                    break;
            }
        }

        /**
         * Process the Movie Details data.
         * @param jObj object
         */
        private void processTVShowDetails(JSONObject jObj) {
            if (jObj != null) {
                try {
                    detailsData = new TVShowData(tvShowId, getStringValue(jObj, "name"),
                            getStringValue(jObj, "overview"), getStringValue(jObj, "poster_path"),
                            getStringValue(jObj, "backdrop_path"),
                            getDoubleValue(jObj, "vote_average", 0.0),
                            getIntValue(jObj, "vote_count", 0),
                            getStringValue(jObj, "original_language"),
                            getArrayValue(jObj, "origin_country"),
                            getListValue(jObj, "genres", "name"), getStringValue(jObj, "status"),
                            getListValue(jObj, "production_companies", "name"),
                            getStringValue(jObj, "homepage"), getStringValue(jObj, "first_air_date"),
                            getStringValue(jObj, "last_air_date"),
                            getIntValue(jObj, "number_of_episodes", 0),
                            getIntValue(jObj, "number_of_seasons", 0));
                    populateDetails(detailsData);
                } catch (JSONException e) {
                    Log.e(TAG, "", e);
                }

            } else {
                final Cursor cursor = getContentResolver().query(
                        ContentUris.withAppendedId(URI, tvShowId),
                        new String[]{ COLUMN_NAME, COLUMN_OVERVIEW, COLUMN_POSTER_PATH,
                                COLUMN_BACKGROUND_PATH, COLUMN_VOTE_AVERAGE, COLUMN_VOTE_COUNT,
                                COLUMN_ORIGINAL_LANGUAGE, COLUMN_ORIGINAL_COUNTRIES, COLUMN_GENRES,
                                COLUMN_STATUS, COLUMN_PROD_COMPANIES, COLUMN_HOMEPAGE,
                                COLUMN_FIRST_AIR_DATE, COLUMN_LAST_AIR_DATE, COLUMN_NUM_EPISODES,
                                COLUMN_NUM_SEASONS},
                        null, null, null);

                if (cursor.getCount() != 0) {
                    Log.d(TAG, "Cursor = " + cursor.getCount());
                    cursor.moveToFirst();
                    // Note: Better if it matches the query order
                    detailsData = new TVShowData(tvShowId, cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), cursor.getString(3), cursor.getDouble(4),
                            cursor.getInt(5), cursor.getString(6), cursor.getString(7),
                            Collections.<String>emptyList(), cursor.getString(9), Collections.<String>emptyList(),
                            cursor.getString(11), cursor.getString(12), cursor.getString(13),
                            cursor.getInt(14), cursor.getInt(15));
                    populateDetails(detailsData);
                }
                cursor.close();
            }
        }

        /**
         * Process the TVShow Trailers data.
         * @param jObj object
         */
        private void processTVShowTrailers(JSONObject jObj) {
            if (jObj != null) {
                trailerData = new ArrayList<>();
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
         * Process the TVShow cast data.
         * @param jObj object
         */
        private void processTVShowCast(JSONObject jObj) {
            if (jObj != null) {
                castData = new ArrayList<>();
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
