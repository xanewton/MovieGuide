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

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.MovieAdapter;
import com.xengar.android.movieguide.data.MovieCreditCast;
import com.xengar.android.movieguide.data.MovieCreditCrew;
import com.xengar.android.movieguide.data.MovieData;
import com.xengar.android.movieguide.data.PersonalProfileData;
import com.xengar.android.movieguide.utils.ActivityUtils;
import com.xengar.android.movieguide.utils.JSONLoader;
import com.xengar.android.movieguide.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static com.xengar.android.movieguide.utils.Constants.BACKGROUND_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_BACKGROUND_POSTER;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_ID;
import static com.xengar.android.movieguide.utils.Constants.PERSON_ID;
import static com.xengar.android.movieguide.utils.Constants.POSTER_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.POSTER_PERSON_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.JSONUtils.getStringValue;

public class PersonProfileActivity extends AppCompatActivity {

    private static final String TAG = PersonProfileActivity.class.getSimpleName();
    private int personId;
    private int movieID;
    private CollapsingToolbarLayout collapsingToolbar;
    private PersonalProfileData personalProfileData;
    private TextView biography;
    private ImageView personalProfImage;
    private LinearLayout rating;
    private TextView textPopularity;
    private ImageView starRating;
    private ImageView backgroundPoster;
    private TextView personName;
    private TextView imdbId;
    private TextView placeOfBith;
    private TextView birthday;
    private TextView deathday;
    private TextView homepage;
    private GridView gridview; // Movie credits list
    private boolean gridViewResized[] = {false}; // boolean for resize gridview hack
    private String personTitle[] = {" "};
    private LinearLayout creditCastList;
    private LinearLayout creditCrewList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        personId = prefs.getInt(PERSON_ID, -1);
        movieID = prefs.getInt(MOVIE_ID, -1);

        personName = (TextView) findViewById(R.id.person);
        rating = (LinearLayout) findViewById(R.id.rating);
        textPopularity = (TextView) findViewById(R.id.text_popularity);
        starRating = (ImageView) findViewById(R.id.star_rating);
        backgroundPoster = (ImageView) findViewById(R.id.background_poster);
        biography = (TextView) findViewById(R.id.biography);
        personalProfImage = (ImageView) findViewById(R.id.personal_image);
        imdbId = (TextView) findViewById(R.id.imdb_id);
        placeOfBith = (TextView) findViewById(R.id.placeOfBirth);
        birthday = (TextView) findViewById(R.id.birthday);
        deathday = (TextView) findViewById(R.id.deathday);
        homepage = (TextView) findViewById(R.id.homepage);
        gridview = (GridView) findViewById(R.id.credit_movies_data);
        creditCastList = (LinearLayout) findViewById(R.id.credit_cast_data);
        creditCrewList = (LinearLayout) findViewById(R.id.credit_crew_data);

        fetchPersonData();
        loadBackgroundPoster();
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ActivityUtils.changeCollapsingToolbarLayoutBehaviour(collapsingToolbar,
                (AppBarLayout) findViewById(R.id.appbar), personTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    private void loadBackgroundPoster() {
        final ImageView imageView = (ImageView) findViewById(R.id.background_poster);
        Glide.with(this).load(R.drawable.no_background_poster).centerCrop().into(imageView);
    }

    private void fetchPersonData() {
        FetchPersonTask detailsTask = new FetchPersonTask(FetchPersonTask.PERSON_PROFILE);
        detailsTask.execute(personId);
    }

    /**
     * Populates the Person data in screen.
     * @param personalData
     */
    private void populatePersonalData(PersonalProfileData personalData) {

        final Palette.PaletteAsyncListener paletteAsyncListener =
                ActivityUtils.definePaletteAsyncListener(this, personName, textPopularity, rating,
                        starRating);

        Callback callback =
                ActivityUtils.defineCallback(paletteAsyncListener, backgroundPoster, null);

        if (personalData == null)
            return;

        personTitle[0] = personalData.getActorName();
        collapsingToolbar.setTitle(personTitle[0]);
        personName.setText(personTitle[0]);
        biography.setText(personalData.getBiography());
        textPopularity.setText("" + personalData.getPopularity());
        imdbId.setText(personalData.getImdbId());
        placeOfBith.setText(personalData.getPlaceOfBirth());
        birthday.setText(personalData.getBirthday());
        if (personalData.getDeathday() == null) {
            deathday.setVisibility(View.GONE);
        } else {
            deathday.setText(personalData.getDeathday());
        }
        if (personalData.getHomepage() == null) {
            homepage.setVisibility(View.GONE);
        } else {
            homepage.setText(personalData.getHomepage());
        }

        /**
         * Read movie poster from preferences saved from MovieDetails page.
         * TODO: Use last movie poster.
         */
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        String backgroundPoster = prefs.getString(MOVIE_BACKGROUND_POSTER, "null");
        PopulateBackgroundPoster(personalData.getProfileImagePath(), backgroundPoster, callback);
        PopulateCreditCast(personalData.getMovieCreditCastList());
        PopulateCreditCrew(personalData.getMovieCreditCrewList());
        PopulateMovieList(personalData);
    }

    /**
     * Populates poster in screen.
     * @param profilePosterPath
     * @param backgroundPosterPath
     * @param callback
     */
    private void PopulateBackgroundPoster(final String profilePosterPath,
                                          final String backgroundPosterPath,
                                          Callback callback) {

        ActivityUtils.loadImage(this, profilePosterPath, true, R.drawable.no_movie_poster,
                personalProfImage, null);

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
     * @param profileData
     */
    private void PopulateMovieList(PersonalProfileData profileData) {

        List<MovieData> data = new ArrayList<>();
        //Build the MovieData
        for (final MovieCreditCast creditCast: profileData.getMovieCreditCastList()) {
            MovieData movieData = new MovieData(
                    POSTER_BASE_URI + creditCast.getPosterPath(), creditCast.getId(),
                    creditCast.getMovieTitle());
            data.add(movieData);
        }

        // Add movie
        MovieAdapter adapter = new MovieAdapter(this);
        for (final MovieData movie : data) {
            adapter.add(movie);
        }
        adapter.notifyDataSetChanged();
        gridview.setAdapter(adapter);
        gridview.setVisibility(View.VISIBLE);

        ActivityUtils.changeGridViewHeight(gridview, adapter.getCount(), gridViewResized);
    }

    /**
     * Populates the movie credit cast list on screen.
     * @param movieCreditCasts
     */
    private void PopulateCreditCast(List<MovieCreditCast> movieCreditCasts) {
        if (movieCreditCasts == null || movieCreditCasts.isEmpty()) {
            return;
        }

        TextView date, title, character;
        View view;
        for (MovieCreditCast creditcast: movieCreditCasts) {
            view = getLayoutInflater().inflate(R.layout.credit_list_item, null);
            date = (TextView) view.findViewById(R.id.movie_date);
            title = (TextView) view.findViewById(R.id.movie_title);
            character = (TextView) view.findViewById(R.id.movie_character);

            date.setText("" + creditcast.getReleaseYear());
            title.setText(creditcast.getMovieTitle());
            character.setText(creditcast.getCharacter());
            creditCastList.addView(view);
        }
    }

    /**
     * Populates the movie credit crew list on screen.
     * @param movieCreditCrew
     */
    private void PopulateCreditCrew(List<MovieCreditCrew> movieCreditCrew) {
        if (movieCreditCrew == null || movieCreditCrew.isEmpty()) {
            return;
        }

        TextView date, title, character;
        View view;
        for (MovieCreditCrew creditcrew: movieCreditCrew) {
            view = getLayoutInflater().inflate(R.layout.credit_list_item, null);
            date = (TextView) view.findViewById(R.id.movie_date);
            title = (TextView) view.findViewById(R.id.movie_title);
            character = (TextView) view.findViewById(R.id.movie_character);

            date.setText("" + creditcrew.getReleaseYear());
            title.setText(creditcrew.getMovieTitle());
            character.setText(creditcrew.getJob());
            creditCrewList.addView(view);
        }
    }


    /**
     * Fetch task to get person profile data
     */
    private class FetchPersonTask extends AsyncTask<Integer, Void, JSONObject> {

        public static final String PERSON_PROFILE = "PersonProfile";
        private String requestType = null;

        // Constructor
        public FetchPersonTask(String requestType){
            this.requestType = requestType;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            String request = null;
            switch (requestType) {
                case PERSON_PROFILE:
                    request = "/person/";
                    break;
            }
            JSONObject jObj = JSONLoader.load(request + params[0],
                    getString(R.string.THE_MOVIE_DB_API_TOKEN), "movie_credits");
            return jObj;
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            super.onPostExecute(jObj);
            switch (requestType) {
                case PERSON_PROFILE:
                    processPersonDetails(jObj);
                    break;
            }
        }

        /**
         * Process the Person Details data.
         * @param jObj
         */
        private void processPersonDetails(JSONObject jObj) {
            if (jObj != null) {
                try {
                    Log.v(TAG, "jObj = " + jObj);
                    personalProfileData = new PersonalProfileData(getStringValue(jObj, "name"),
                            POSTER_PERSON_BASE_URI + JSONUtils.getStringValue(jObj, "profile_path"),
                            JSONUtils.getStringValue(jObj, "place_of_birth"),
                            JSONUtils.getStringValue(jObj, "birthday"),
                            JSONUtils.getStringValue(jObj, "deathday"),
                            JSONUtils.getStringValue(jObj, "biography"), id,
                            JSONUtils.getIntValue(jObj, "popularity", 0),
                            JSONUtils.getStringValue(jObj, "imdb_id"),
                            JSONUtils.getStringValue(jObj, "homepage"),
                            JSONUtils.getMovieCreditCastList(jObj, "movie_credits", "cast"),
                            JSONUtils.getMovieCreditCrewList(jObj, "movie_credits", "crew"));
                    populatePersonalData(personalProfileData);

                } catch (JSONException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
    }
}
