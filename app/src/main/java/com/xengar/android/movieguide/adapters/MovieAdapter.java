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
package com.xengar.android.movieguide.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.MovieData;
import com.xengar.android.movieguide.ui.MovieDetailsActivity;

import java.util.ArrayList;
import java.util.HashSet;

import static com.xengar.android.movieguide.utils.Constants.MOVIE_ID;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;

/**
 * Movie adapter used to fill the movie list in the Person Profile page.
 */
public class MovieAdapter extends BaseAdapter {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private final ArrayList<MovieData> movies = new ArrayList<>();
    private final HashSet<Integer> moviesIdSet = new HashSet<>();
    private final Context mContext;

    // Constructor
    public MovieAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movies.get(position).getMovieId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.movie_list_item, parent, false);
        } else {
            view = convertView;
        }

        MovieData movie = movies.get(position);
        view.setTag(movie);
        TextView title = (TextView) view.findViewById(R.id.name);
        Log.v(TAG, "movie.getMovieTitle() " + movie.getMovieTitle());
        ImageView poster = (ImageView) view.findViewById(R.id.image);
        if (movie.getMovieTitle() == null) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(movie.getMovieTitle());
        }
        if (movie.getMoviePoster() == null) {
            poster.setVisibility(View.GONE);
        } else {
            Picasso pic = Picasso.with(mContext);
            pic.load(movie.getMoviePoster())
                    .fit().centerCrop()
                    .error(R.drawable.no_movie_poster)
                    .into(poster);
        }

        // Now set the onClickListener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieData data = (MovieData) v.getTag();

                // Save movieId to Preferences
                SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_NAME, 0);
                SharedPreferences.Editor e = prefs.edit();
                e.putInt(MOVIE_ID, data.getMovieId());
                e.commit();

                // Launch a Movie Details Activity
                Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    public void add(MovieData movie) {
        if (moviesIdSet.contains(movie.getMovieId())) {
            Log.w(TAG, "Movie duplicate found, movieID = " + movie.getMovieId());
            return;
        }
        movies.add(movie);
        moviesIdSet.add(movie.getMovieId());
    }
}
