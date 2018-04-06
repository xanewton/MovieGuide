package com.xengar.android.movieguide.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.MovieData;
import com.xengar.android.movieguide.data.MovieDetails;
import com.xengar.android.movieguide.data.TrailerData;
import com.xengar.android.movieguide.utils.ActivityUtils;
import com.xengar.android.movieguide.utils.FragmentUtils;
import com.xengar.android.movieguide.utils.JSONLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.xengar.android.movieguide.utils.Constants.LAST_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.LOG;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_ID;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;


public class YoutubeActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener {


    private final String movieTitle[] = {"test "};
    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayer youTubePlayer;
    private int movieID;
    private TextView title;
    private String mLang;
    private LinearLayout trailerList;
    private List<TrailerData> trailerData;
    private MovieDetails data = null;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView textLanguage;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        movieID = prefs.getInt(MOVIE_ID, -1);

        // Save name of activity, in case of calling SettingsActivity
        ActivityUtils.saveStringToPreferences(getApplicationContext(), LAST_ACTIVITY,
                MOVIE_ACTIVITY);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) findViewById(R.id.title);
        trailerList = (LinearLayout) findViewById(R.id.movie_trailers);
        textLanguage = (TextView) findViewById(R.id.text_language);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ActivityUtils.changeCollapsingToolbarLayoutBehaviour(collapsingToolbar,
                (AppBarLayout) findViewById(R.id.appbar), movieTitle);

        youTubePlayerFragment = YouTubePlayerFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.youtube_fragment, youTubePlayerFragment).commit();

        mLang = FragmentUtils.getFormatLocale(getApplicationContext());
        // Get Movie Details data
        fetchMovieData();

        // Initialize AdMob
        // https://firebase.google.com/docs/admob/android/quick-start
        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id_1));
        mContext = this;
    }


    private void fetchMovieData() {

        if (data == null) {
            FetchMovieTask trailersTask = new FetchMovieTask(FetchMovieTask.MOVIE_TRAILERS);
            trailersTask.execute(movieID);
        } else {
            populateTrailerList(trailerData = data.getTrailersData());
        }
    }

    private void populateTrailerList(List<TrailerData> data) {

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
            findViewById(R.id.movie_trailers).setVisibility(View.GONE);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    private class FetchMovieTask extends AsyncTask<Integer, Void, JSONObject> {

        public static final String MOVIE_DETAILS = "MovieDetails";
        public static final String MOVIE_TRAILERS = "MovieTrailers";
        public static final String MOVIE_REVIEWS = "MovieReviews";
        public static final String MOVIE_CAST = "MovieCast";
        private static final String TRAILER_BASE_URI = "http://www.youtube.com/watch?v=";
        private String requestType = null;

        // Constructor
        public FetchMovieTask(String requestType) {
            this.requestType = requestType;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            String request = null;
            switch (requestType) {
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

            return JSONLoader.load(request, getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang);
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            super.onPostExecute(jObj);

            switch (requestType) {
                case MOVIE_DETAILS:
                    //processMovieDetails(jObj);
                    break;
                case MOVIE_TRAILERS:
                    processMovieTrailers(jObj);
                    break;
            }
        }

        private void processMovieTrailers(JSONObject jObj) {
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

                }
                populateTrailerList(trailerData);
            }
        }

        private void populateTrailerList(List<TrailerData> data) {

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
                //youTubePlayerFragment.initialize(getString(R.string.YOUTUBE_DATA_API_V3), mContext);
            } else {
                findViewById(R.id.youtube_fragment).setVisibility(View.GONE);
                findViewById(R.id.movie_trailers).setVisibility(View.GONE);
            }
        }

        public List<TrailerData> getTrailerData() {
            return trailerData;
        }

        private void populateDetails(final MovieData container) {

            if (container == null) {
                return;
            }

            PopulateDetailsTitle(container);
            PopulateDetailsLanguage(container);
        }


        private void PopulateDetailsLanguage(final MovieData container) {
            if (container.getOriginalLanguage() != null) {
                String name = "";
                Locale[] locales = Locale.getAvailableLocales();
                for (Locale l : locales) {
                    if (l.getLanguage().equals(container.getOriginalLanguage())) {
                        name = l.getDisplayLanguage();
                    }
                }
                if (name.isEmpty()) {
                    textLanguage.setText(container.getOriginalLanguage());
                } else {
                    textLanguage.setText(name);
                }
                textLanguage.setVisibility(View.VISIBLE);
            } else {
                textLanguage.setVisibility(View.GONE);
            }
        }


        /**
         * Populates Title in screen.
         *
         * @param container MovieData
         */
        private void PopulateDetailsTitle(final MovieData container) {
            movieTitle[0] = container.getTitle();
            collapsingToolbar.setTitle(movieTitle[0]);
            title.setText(movieTitle[0]);
        }

    }

}