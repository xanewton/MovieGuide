/*
 * Copyright (C) 2017 Angel Newton
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

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.ImageAdapter;
import com.xengar.android.movieguide.data.FavoritesContract;
import com.xengar.android.movieguide.data.ImageItem;
import com.xengar.android.movieguide.data.MovieCreditCast;
import com.xengar.android.movieguide.data.MovieCreditCrew;
import com.xengar.android.movieguide.data.PersonData;
import com.xengar.android.movieguide.utils.ActivityUtils;
import com.xengar.android.movieguide.utils.FragmentUtils;
import com.xengar.android.movieguide.utils.JSONLoader;
import com.xengar.android.movieguide.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_KNOWNFOR_POSTER_PATH;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_NAME;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_PERSON_ID;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_PROFILE_PATH;
import static com.xengar.android.movieguide.utils.Constants.BACKGROUND_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.KNOWN_FOR_BACKGROUND_POSTER;
import static com.xengar.android.movieguide.utils.Constants.LAST_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.LOG;
import static com.xengar.android.movieguide.utils.Constants.PAGE_PERSON_DETAILS;
import static com.xengar.android.movieguide.utils.Constants.PEOPLE;
import static com.xengar.android.movieguide.utils.Constants.PERSON_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.PERSON_ID;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.SIZE_W342;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;
import static com.xengar.android.movieguide.utils.Constants.TYPE_ADD_FAV;
import static com.xengar.android.movieguide.utils.Constants.TYPE_DEL_FAV;
import static com.xengar.android.movieguide.utils.Constants.TYPE_PAGE;

/**
 * Represents the Person Profile page.
 */
public class PersonActivity extends AppCompatActivity {

    private static final String TAG = PersonActivity.class.getSimpleName();
    private static final Uri URI = FavoritesContract.FavoriteColumns.uriPerson;
    private String mLang;
    private int personId;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView biography;
    private ImageView personalProfImage;
    private LinearLayout rating;
    private TextView textPopularity;
    private ImageView starRating;
    private ImageView backgroundPoster;
    private TextView personName;
    private TextView imdbId;
    private TextView placeOfBirth;
    private TextView birthday;
    private TextView deathday;
    private TextView homepage;
    private GridView gridview; // Movie credits list
    private final boolean[] gridViewResized = {false}; // boolean for resize gridview hack
    private final String[] personTitle = {" "};
    private LinearLayout creditCastList;
    private LinearLayout creditCrewList;

    private FloatingActionButton fabAdd, fabDel;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        personId = prefs.getInt(PERSON_ID, -1);

        // Save name of activity, in case of calling SettingsActivity
        ActivityUtils.saveStringToPreferences(getApplicationContext(), LAST_ACTIVITY,
                PERSON_ACTIVITY);

        personName = (TextView) findViewById(R.id.person);
        rating = (LinearLayout) findViewById(R.id.rating);
        textPopularity = (TextView) findViewById(R.id.text_popularity);
        starRating = (ImageView) findViewById(R.id.star_rating);
        backgroundPoster = (ImageView) findViewById(R.id.background_poster);
        biography = (TextView) findViewById(R.id.biography);
        personalProfImage = (ImageView) findViewById(R.id.personal_image);
        imdbId = (TextView) findViewById(R.id.imdb_id);
        placeOfBirth = (TextView) findViewById(R.id.placeOfBirth);
        birthday = (TextView) findViewById(R.id.birthday);
        deathday = (TextView) findViewById(R.id.deathday);
        homepage = (TextView) findViewById(R.id.homepage);
        gridview = (GridView) findViewById(R.id.credit_movies_data);
        creditCastList = (LinearLayout) findViewById(R.id.credit_cast_data);
        creditCrewList = (LinearLayout) findViewById(R.id.credit_crew_data);

        mLang = FragmentUtils.getFormatLocale(getApplicationContext());
        fetchPersonData();
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ActivityUtils.changeCollapsingToolbarLayoutBehaviour(collapsingToolbar,
                (AppBarLayout) findViewById(R.id.appbar), personTitle);
        showFavoriteButtons();

        // Initialize AdMob
        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id_3));
        ActivityUtils.showAdMobBanner(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ActivityUtils.firebaseAnalyticsLogEventSelectContent(
                mFirebaseAnalytics, PAGE_PERSON_DETAILS, PAGE_PERSON_DETAILS, TYPE_PAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (itemId == R.id.action_search) {
            ActivityUtils.launchSearchActivity(getApplicationContext());
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

        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(URI, personId),
                new String[]{COLUMN_PERSON_ID}, null, null, null);
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
     * @param personData person data
     */
    private void defineClickFavoriteButtons(final PersonData personData) {
        final int DURATION = 1000;

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.favorites_add_message), DURATION)
                        .setAction("Action", null).show();
                ContentValues values = new ContentValues();
                values.put(COLUMN_PERSON_ID, personId);
                values.put(COLUMN_NAME, personData.getActorName());
                values.put(COLUMN_PROFILE_PATH, personData.getProfileImagePath());
                values.put(COLUMN_KNOWNFOR_POSTER_PATH, personData.getBackgroundPath());
                getContentResolver().insert(URI, values);

                fabAdd.setVisibility(View.INVISIBLE);
                fabDel.setVisibility(View.VISIBLE);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics,
                        PERSON_ID + " " + personId, personData.getActorName(), TYPE_ADD_FAV);
            }
        });

        fabDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.favorites_del_message), DURATION)
                        .setAction("Action", null).show();
                getContentResolver().delete(URI, COLUMN_PERSON_ID + " = ?",
                        new String[]{Integer.toString(personId)} );

                fabAdd.setVisibility(View.VISIBLE);
                fabDel.setVisibility(View.INVISIBLE);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics,
                        PERSON_ID + " " + personId, personData.getActorName(), TYPE_DEL_FAV);
            }
        });
    }

    private void fetchPersonData() {
        FetchPersonTask detailsTask = new FetchPersonTask(FetchPersonTask.PERSON_PROFILE, false);
        detailsTask.execute(personId);
    }

    /**
     * Populates the Person data in screen.
     * @param personData PersonData
     */
    private void populateProfile(PersonData personData) {

        final Palette.PaletteAsyncListener paletteAsyncListener =
                ActivityUtils.definePaletteAsyncListener(this, personName, textPopularity, rating,
                        starRating);

        Callback callback =
                ActivityUtils.defineCallback(paletteAsyncListener, backgroundPoster, null);

        if (personData == null)
            return;

        personTitle[0] = personData.getActorName();
        collapsingToolbar.setTitle(personTitle[0]);
        personName.setText(personTitle[0]);
        biography.setText(personData.getBiography());
        textPopularity.setText(String.format(Locale.ENGLISH, "%d", personData.getPopularity()));
        imdbId.setText(personData.getImdbId());
        placeOfBirth.setText(personData.getPlaceOfBirth());
        birthday.setText(personData.getBirthday());
        if (personData.getDeathday() == null) {
            deathday.setVisibility(View.GONE);
        } else {
            deathday.setText(personData.getDeathday());
        }
        if (personData.getHomepage() == null) {
            homepage.setVisibility(View.GONE);
        } else {
            homepage.setText(personData.getHomepage());
        }

        /**
         * Read movie poster from preferences saved from MovieDetails page.
         */
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        String backgroundPoster = prefs.getString(KNOWN_FOR_BACKGROUND_POSTER, "null");
        personData.setBackgroundPath(backgroundPoster);
        PopulateBackgroundPoster(personData.getProfileImagePath(), backgroundPoster, callback);
        PopulateCreditCast(personData.getMovieCreditCastList());
        PopulateCreditCrew(personData.getMovieCreditCrewList());
        PopulateMovieList(personData);
        defineClickFavoriteButtons(personData);
        ActivityUtils.firebaseAnalyticsLogEventViewItem(
                mFirebaseAnalytics, "" + personId, personTitle[0], PEOPLE);
    }

    /**
     * Populates poster in screen.
     * @param profilePosterPath image path
     * @param backgroundPosterPath image path
     * @param callback callback
     */
    private void PopulateBackgroundPoster(final String profilePosterPath,
                                          final String backgroundPosterPath,
                                          Callback callback) {

        ActivityUtils.loadImage(this, TMDB_IMAGE_URL + SIZE_W342 + profilePosterPath, true,
                R.drawable.disk_reel, personalProfImage, null);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityUtils.loadImage(this, BACKGROUND_BASE_URI + backgroundPosterPath, true,
                    R.drawable.no_background_poster, backgroundPoster, callback);
        } else {
            ActivityUtils.loadImage(this, BACKGROUND_BASE_URI + backgroundPosterPath, false,
                    R.drawable.no_background_poster, backgroundPoster, callback);
        }
    }

    /**
     * Populates the Movie list in screen.
     * @param personData PersonData
     */
    private void PopulateMovieList(PersonData personData) {

        List<ImageItem> data = new ArrayList<>();
        int index = 0;
        int maxMovies = ActivityUtils.getPreferenceMaxMovieItems(getApplicationContext());
        // Build the ImageItem
        for (final MovieCreditCast creditCast: personData.getMovieCreditCastList()) {
            if ( index == maxMovies)
                break;
            index++;
            ImageItem imageItem = new ImageItem(creditCast.getPosterPath(), creditCast.getId(),
                    creditCast.getMovieTitle(), null, null);
            data.add(imageItem);
        }

        // Add movie
        int adds = 0;
        ImageAdapter adapter = new ImageAdapter(getApplicationContext(), ImageAdapter.MOVIE_IMAGE);
        for (final ImageItem movie : data) {
            adds += adapter.add(movie);
        }
        if (adds != 0)
            adapter.notifyDataSetChanged();
        gridview.setAdapter(adapter);
        gridview.setVisibility(View.VISIBLE);

        ActivityUtils.changeGridViewHeight(gridview, adapter.getCount(), gridViewResized);
    }

    /**
     * Populates the movie credit cast list on screen.
     * @param movieCreditCast list of movie credit cast
     */
    private void PopulateCreditCast(List<MovieCreditCast> movieCreditCast) {
        if (movieCreditCast == null || movieCreditCast.isEmpty()) {
            TextView credit = (TextView) findViewById(R.id.credit_cast_title);
            credit.setVisibility(View.GONE);
            return;
        }

        TextView date, title, character;
        View view;
        for (MovieCreditCast creditCast: movieCreditCast) {
            view = getLayoutInflater().inflate(R.layout.credit_list_item, null);
            date = (TextView) view.findViewById(R.id.movie_date);
            title = (TextView) view.findViewById(R.id.poster_title);
            character = (TextView) view.findViewById(R.id.movie_character);

            date.setText(String.format(Locale.ENGLISH, "%d", creditCast.getReleaseYear()));
            title.setText(creditCast.getMovieTitle());
            character.setText(creditCast.getCharacter());
            creditCastList.addView(view);
        }
    }

    /**
     * Populates the movie credit crew list on screen.
     * @param movieCreditCrew list of movie credit crew
     */
    private void PopulateCreditCrew(List<MovieCreditCrew> movieCreditCrew) {
        if (movieCreditCrew == null || movieCreditCrew.isEmpty()) {
            TextView credit = (TextView) findViewById(R.id.credit_crew_title);
            credit.setVisibility(View.GONE);
            return;
        }

        TextView date, title, character;
        View view;
        for (MovieCreditCrew creditCrew: movieCreditCrew) {
            view = getLayoutInflater().inflate(R.layout.credit_list_item, null);
            date = (TextView) view.findViewById(R.id.movie_date);
            title = (TextView) view.findViewById(R.id.poster_title);
            character = (TextView) view.findViewById(R.id.movie_character);

            date.setText(String.format(Locale.ENGLISH, "%d", creditCrew.getReleaseYear()));
            title.setText(creditCrew.getMovieTitle());
            character.setText(creditCrew.getJob());
            creditCrewList.addView(view);
        }
    }


    /**
     * Fetch task to get person profile data
     */
    private class FetchPersonTask extends AsyncTask<Integer, Void, JSONObject> {

        public static final String PERSON_PROFILE = "PersonProfile";
        private String requestType = null;
        private boolean queryProfileBiography = false;

        // Constructor
        public FetchPersonTask(String requestType, boolean queryProfileBiography){
            this.requestType = requestType;
            this.queryProfileBiography = queryProfileBiography;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            String request = null;
            String appendToResponse = "movie_credits";
            String language = mLang;
            switch (requestType) {
                case PERSON_PROFILE:
                    request = "/person/";
                    // Query in english and not movie_credits the second time
                    if (queryProfileBiography) {
                        appendToResponse = null;
                        language = "en-US";
                    }
                    break;
            }
            return JSONLoader.load(request + params[0],
                    getString(R.string.THE_MOVIE_DB_API_TOKEN), language, appendToResponse);
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            super.onPostExecute(jObj);
            switch (requestType) {
                case PERSON_PROFILE:
                    if (queryProfileBiography) {
                        processPersonDataForBiography(jObj);
                    } else {
                        processPersonData(jObj);
                    }
                    break;
            }
        }

        /**
         * Process the Person Details data.
         * @param jObj object
         */
        private void processPersonData(JSONObject jObj) {
            if (jObj != null) {
                try {
                    if (LOG) {
                        Log.v(TAG, "jObj = " + jObj);
                    }
                    PersonData personData = new PersonData(JSONUtils.getStringValue(jObj, "name"),
                            JSONUtils.getStringValue(jObj, "profile_path"),
                            JSONUtils.getStringValue(jObj, "place_of_birth"),
                            JSONUtils.getStringValue(jObj, "birthday"),
                            JSONUtils.getStringValue(jObj, "deathday"),
                            JSONUtils.getStringValue(jObj, "biography"), personId,
                            JSONUtils.getIntValue(jObj, "popularity", 0),
                            JSONUtils.getStringValue(jObj, "imdb_id"),
                            JSONUtils.getStringValue(jObj, "homepage"),
                            JSONUtils.getMovieCreditCastList(jObj, "movie_credits", "cast"),
                            JSONUtils.getMovieCreditCrewList(jObj, "movie_credits", "crew"), null);

                    populateProfile(personData);

                    /**
                     * THIS IS A HACK!
                     * Problem: The request doesn't return biography in english when the query is in
                     * other language and there is no translation. So, we do will query again using
                     * english.
                     */
                    if (personData.getBiography() == null && !mLang.contentEquals("en")
                            && requestType.contentEquals(PERSON_PROFILE)){
                        //Create a new request for the biography only
                        FetchPersonTask biographyTask =
                                new FetchPersonTask(FetchPersonTask.PERSON_PROFILE, true);
                        biographyTask.execute(personId);
                    }

                } catch (JSONException e) {
                    if (LOG) {
                        Log.e(TAG, "", e);
                    }
                }
            }
        }

        /**
         * Process the Person Details biography data.
         * @param jObj object
         */
        private void processPersonDataForBiography(JSONObject jObj) {
            if (jObj != null) {
                try {
                    if (LOG) {
                        Log.v(TAG, "jObj = " + jObj);
                    }
                    biography.setText(JSONUtils.getStringValue(jObj, "biography"));
                } catch (JSONException e) {
                    if (LOG) {
                        Log.e(TAG, "", e);
                    }
                }
            }
        }
    }
}
