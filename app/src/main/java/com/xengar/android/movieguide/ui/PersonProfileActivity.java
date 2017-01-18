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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.MovieCreditCast;
import com.xengar.android.movieguide.data.MovieCreditCrew;
import com.xengar.android.movieguide.data.PersonalProfileData;
import com.xengar.android.movieguide.utils.JSONLoader;
import com.xengar.android.movieguide.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.R.attr.id;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_ID;
import static com.xengar.android.movieguide.utils.Constants.PERSON_ID;
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
    private TextView personName;
    private TextView popularity;
    private TextView imdbId;
    private TextView placeOfBith;
    private TextView birthday;
    private TextView deathday;
    private TextView homepage;
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

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        personName = (TextView) findViewById(R.id.person);
        biography = (TextView) findViewById(R.id.biography);
        personalProfImage = (ImageView) findViewById(R.id.personal_image);
        popularity = (TextView) findViewById(R.id.popularity);
        imdbId = (TextView) findViewById(R.id.imdb_id);
        placeOfBith = (TextView) findViewById(R.id.placeOfBirth);
        birthday = (TextView) findViewById(R.id.birthday);
        deathday = (TextView) findViewById(R.id.deathday);
        homepage = (TextView) findViewById(R.id.homepage);
        creditCastList = (LinearLayout) findViewById(R.id.credit_cast_data);
        creditCrewList = (LinearLayout) findViewById(R.id.credit_crew_data);

        fetchPersonData();
        loadBackgroundPoster();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        if (personalData == null)
            return;

        collapsingToolbar.setTitle(personalData.getActorName());
        personName.setText(personalData.getActorName());
        biography.setText(personalData.getBiography());
        popularity.setText("" + personalData.getPopularity());
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

        Picasso pic = Picasso.with(this);
        Log.v(TAG, "path" + personalData.getProfileImagePath());
        if (personalData.getProfileImagePath() == null) {
            pic.load(R.drawable.no_movie_poster)
                    .fit().centerCrop()
                    .into(personalProfImage);
        } else {
            pic.load(personalData.getProfileImagePath())
                    .fit().centerCrop()
                    .error(R.drawable.no_movie_poster)
                    .into(personalProfImage);
        }

        PopulateCreditCast(personalData.getMovieCreditCastList());
        PopulateCreditCrew(personalData.getMovieCreditCrewList());
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
        private static final String POSTER_PERSONAL_IMAGE_BASE_URI = "http://image.tmdb.org/t/p/w92";
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
                            POSTER_PERSONAL_IMAGE_BASE_URI + JSONUtils.getStringValue(jObj, "profile_path"),
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
