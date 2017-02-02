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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.HomeMovieAdapter;
import com.xengar.android.movieguide.data.Movie;
import com.xengar.android.movieguide.data.MovieResults;
import com.xengar.android.movieguide.service.DiscoverService;
import com.xengar.android.movieguide.service.ServiceGenerator;
import com.xengar.android.movieguide.utils.FragmentUtils;
import com.xengar.android.movieguide.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HomeFragment
 */
public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CircularProgressBar progressBar;
    private HomeMovieAdapter mAdapter;
    private List<Movie> mMovies;
    private int mPage = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        fillMoviesSection(view);
        return view;
    }

    /**
     * Fills the Movies section
     * @param view
     */
    private void fillMoviesSection(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_movies);
        progressBar = (CircularProgressBar) view.findViewById(R.id.progressBarMovies);
        mMovies = new ArrayList<>();
        mAdapter = new HomeMovieAdapter(mMovies);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);
        updateProgressBar(true);

        DiscoverService discover = ServiceGenerator.createService(DiscoverService.class);
        String lang = FragmentUtils.getFormatLocale(getActivity());
        Call<MovieResults> call = discover.inTheaters(getString(R.string.THE_MOVIE_DB_API_TOKEN),
                lang, mPage, "popularity.desc", StringUtils.inTheatersLte(),
                StringUtils.inTheatersGte());
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                if (response.isSuccessful()) {
                    List<Movie> movies = response.body().getMovies();
                    mMovies.clear();
                    if (movies != null) {
                        if (movies.size() < 10) {
                            mMovies.addAll(movies);
                        } else {
                            for (int i = 0; i < 10; i++) {
                                Movie movie = movies.get(i);
                                mMovies.add(movie);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    updateProgressBar(false);
                } else {
                    Log.i("TAG", "Res: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                Log.i("TAG", "Error: " + t.getMessage());
                updateProgressBar(false);
            }
        });
    }

    private void updateProgressBar(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

}
