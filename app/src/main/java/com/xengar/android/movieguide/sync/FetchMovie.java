package com.xengar.android.movieguide.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.xengar.android.movieguide.adapters.ImageAdapter;
import com.xengar.android.movieguide.data.MovieData;
import com.xengar.android.movieguide.utils.JSONLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * FetchMovie
 */
public class FetchMovie extends AsyncTask<Integer, Void, ArrayList<MovieData>> {

    private static final String TAG = FetchMovie.class.getSimpleName();
    private static final String MOVIE_TOP_RATED = "/movie/top_rated";
    private static final String MOVIE_NOW_PLAYING = "/movie/now_playing";
    private static final String MOVIE_UPCOMING = "/movie/upcoming";
    private static final String DISCOVER_MOVIE = "/discover/movie";

    private final FetchItemListener fetchMovieListener;
    private final String posterBaseUri;
    private final ImageAdapter adapter;
    private final String apiKey;
    private final String sortOrder;
    private final String category;
    private String loadCategory;

    // Constructor
    public FetchMovie(String category, ImageAdapter adapter,
                      FetchItemListener fetchMovieListener, String apiKey,
                      String posterBaseUri, String sortOrder) {
        this.category = category;
        this.fetchMovieListener = fetchMovieListener;
        this.posterBaseUri = posterBaseUri;
        this.adapter = adapter;
        this.apiKey = apiKey;
        this.sortOrder = sortOrder;

        // assign the category to query
        switch (category){
            case "TopRatedMovie":
                this.loadCategory = MOVIE_TOP_RATED + "?page=";
                break;
            case "NowPlayingMovie":
                this.loadCategory = MOVIE_NOW_PLAYING + "?page=";
                break;
            case "UpcomingMovie":
                this.loadCategory = MOVIE_UPCOMING + "?page=";
                break;
            case "PopularMovie":
                this.loadCategory = DISCOVER_MOVIE + "?sort_by=" + sortOrder + "&page=";
                break;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieData> moviePosters) {
        super.onPostExecute(moviePosters);
        if (moviePosters != null) {
            for (MovieData res : moviePosters) {
                adapter.add(res);
            }
            adapter.notifyDataSetChanged();
            fetchMovieListener.onFetchCompleted();
        } else {
            fetchMovieListener.onFetchFailed();
        }
    }

    @Override
    protected ArrayList<MovieData> doInBackground(Integer... params) {
        ArrayList<MovieData> moviePosters = new ArrayList<>();
        try {
            JSONObject jObj = JSONLoader.load(loadCategory + params[0], apiKey);
            if (jObj == null) {
                Log.w(TAG, "Can not load the data from remote service");
                return null;
            }

            JSONArray movieArray = jObj.getJSONArray("results");
            JSONObject movie = null;
            String moviePoster = null;
            int movieId = 0;
            String movieTitle = null;
            for (int i = 0; i < movieArray.length(); i++) {
                movie = movieArray.optJSONObject(i);
                moviePoster = movie.getString("poster_path");
                movieId = movie.getInt("id");
                movieTitle = movie.getString("title");
                MovieData data = new MovieData(posterBaseUri + moviePoster, movieId, movieTitle);
                moviePosters.add(data);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error ", e);
        }
        return moviePosters;
    }
}
