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
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.model.Movie;
import com.xengar.android.movieguide.utils.ActivityUtils;
import com.xengar.android.movieguide.utils.Constants;

import java.util.List;

/**
 * HomeMovieAdapter
 */
public class HomeMovieAdapter extends RecyclerView.Adapter<HomeMovieAdapter.MovieHolder> {

    private List<Movie> mMovies;

    public HomeMovieAdapter(List<Movie> movies) {
        mMovies = movies;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_home, parent, false);
        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie movie = mMovies.get(position);
        holder.bindMovie(movie);
    }

    @Override
    public int getItemCount() {
        return (mMovies != null) ? mMovies.size() : 0;
    }


    /**
     * MovieHolder
     */
    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Context mContext;
        private Movie mMovie;
        ImageView moviePoster;
        TextView movieTitle;

        public MovieHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.item_home_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.item_home_title);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        void bindMovie(Movie movie) {
            mMovie = movie;
            movieTitle.setText(movie.getTitle());
            Drawable placeholder =
                    ResourcesCompat.getDrawable(
                            mContext.getResources(), R.drawable.disk_reel, null);
            Picasso.with(mContext)
                    .load(Constants.TMDB_IMAGE_URL + Constants.POSTER_SIZE_W342
                            + movie.getPosterPath())
                    .placeholder(placeholder)
                    .fit().centerCrop()
                    .noFade()
                    .into(moviePoster);
        }

        // Handles the item click.
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            // Check if an item was deleted, but the user clicked it before the UI removed it
            if (position != RecyclerView.NO_POSITION) {
                ActivityUtils.launchMovieActivity(mContext, Integer.valueOf(mMovie.getId()));
            }
        }

    }
}
