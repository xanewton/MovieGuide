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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.PosterData;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Movie adapter used to fill the movie list in the Person Profile page.
 */
public class MovieAdapter extends BaseAdapter {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private final ArrayList<PosterData> movies = new ArrayList<>();
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
        return movies.get(position).getPosterId();
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

        PosterData movie = movies.get(position);
        view.setTag(movie);
        TextView title = (TextView) view.findViewById(R.id.name);
        Log.v(TAG, "movie.getPosterTitle() " + movie.getPosterTitle());
        ImageView poster = (ImageView) view.findViewById(R.id.image);
        if (movie.getPosterTitle() == null) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(movie.getPosterTitle());
        }
        if (movie.getPosterPath() == null) {
            poster.setVisibility(View.GONE);
        } else {
            ActivityUtils.loadImage(mContext, movie.getPosterPath(), true,
                    R.drawable.no_movie_poster, poster, null);
        }

        // Now set the onClickListener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PosterData data = (PosterData) v.getTag();
                ActivityUtils.launchMovieDetailsActivity(mContext, data.getPosterId());
            }
        });

        return view;
    }

    public void add(PosterData movie) {
        if (moviesIdSet.contains(movie.getPosterId())) {
            Log.w(TAG, "Movie duplicate found, movieID = " + movie.getPosterId());
            return;
        }
        movies.add(movie);
        moviesIdSet.add(movie.getPosterId());
    }
}
