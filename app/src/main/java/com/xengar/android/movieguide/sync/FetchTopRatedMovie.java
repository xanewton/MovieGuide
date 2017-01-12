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
 * FetchTopRatedMovie
 */
public class FetchTopRatedMovie extends AsyncTask<Integer, Void, ArrayList<MovieData>> {

    private static final String TAG = FetchTopRatedMovie.class.getSimpleName();

    private final FetchItemListener fetchMovieListener;
    private  final String posterBaseUri;
    private final ImageAdapter adapter;
    private final String apiKey;

    // Constructor
    public FetchTopRatedMovie(ImageAdapter adapter, FetchItemListener fetchMovieListener, String apiKey, String posterBaseUri) {
        this.fetchMovieListener = fetchMovieListener;
        this.posterBaseUri = posterBaseUri;
        this.adapter = adapter;
        this.apiKey = apiKey;
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
            JSONObject jObj = JSONLoader.load("/movie/top_rated" + "?page=" + params[0], apiKey);
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
