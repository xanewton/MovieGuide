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
import android.widget.LinearLayout;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.HomeMovieAdapter;
import com.xengar.android.movieguide.adapters.HomeTVAdapter;
import com.xengar.android.movieguide.data.Movie;
import com.xengar.android.movieguide.data.MovieResults;
import com.xengar.android.movieguide.data.TV;
import com.xengar.android.movieguide.data.TVResults;
import com.xengar.android.movieguide.service.DiscoverService;
import com.xengar.android.movieguide.service.ServiceGenerator;
import com.xengar.android.movieguide.utils.CustomErrorView;
import com.xengar.android.movieguide.utils.FragmentUtils;
import com.xengar.android.movieguide.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;

/**
 * HomeFragment
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    private static final int MAX_ITEMS = 25;

    private LinearLayout moreMovies;
    private RecyclerView mRecyclerViewMovies;
    private CircularProgressBar progressBarMovies;
    private HomeMovieAdapter mAdapterMovies;
    private List<Movie> mMovies;
    private int mPage = 1;
    private String mLang;

    private LinearLayout moreTV;
    private RecyclerView mRecyclerViewTV;
    private CircularProgressBar progressBarTV;
    private HomeTVAdapter mAdapterTV;
    private List<TV> mTVList;
    private CustomErrorView mCustomErrorView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mLang = FragmentUtils.getFormatLocale(getActivity());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        mCustomErrorView = (CustomErrorView) view.findViewById(R.id.error);

        moreMovies = (LinearLayout) view.findViewById(R.id.home_in_theaters);
        moreMovies.setOnClickListener(this);
        mRecyclerViewMovies = (RecyclerView) view.findViewById(R.id.recycler_view_movies);
        progressBarMovies = (CircularProgressBar) view.findViewById(R.id.progress_bar_movies);
        mMovies = new ArrayList<>();
        mAdapterMovies = new HomeMovieAdapter(mMovies);

        moreTV = (LinearLayout) view.findViewById(R.id.home_on_tv);
        moreTV.setOnClickListener(this);
        mRecyclerViewTV = (RecyclerView) view.findViewById(R.id.recycler_view_tv);
        progressBarTV = (CircularProgressBar) view.findViewById(R.id.progress_bar_tv);
        mTVList = new ArrayList<>();
        mAdapterTV = new HomeTVAdapter(mTVList);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!FragmentUtils.checkInternetConnection(getActivity())) {
            Log.e(TAG, "Network is not available");
            onLoadFailed(new Throwable(getString(R.string.network_not_available_message)));
            return;
        }

        fillMoviesSection();
        fillTVSection();
    }

    private void onLoadFailed(Throwable t) {
        mCustomErrorView.setError(t);
        mCustomErrorView.setVisibility(View.VISIBLE);
        FragmentUtils.updateProgressBar(progressBarMovies, false);
        FragmentUtils.updateProgressBar(progressBarTV, false);
    }


    /**
     * Fills the Movies section.
     */
    private void fillMoviesSection(){
        mRecyclerViewMovies.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewMovies.setAdapter(mAdapterMovies);
        FragmentUtils.updateProgressBar(progressBarMovies, true);

        DiscoverService discover = ServiceGenerator.createService(DiscoverService.class);
        Call<MovieResults> call = discover.inTheaters(getString(R.string.THE_MOVIE_DB_API_TOKEN),
                mLang, mPage, "popularity.desc", StringUtils.inTheatersLte(),
                StringUtils.inTheatersGte());
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                if (response.isSuccessful()) {
                    List<Movie> movies = response.body().getMovies();
                    mMovies.clear();
                    if (movies != null) {
                        if (movies.size() < MAX_ITEMS) {
                            mMovies.addAll(movies);
                        } else {
                            for (int i = 0; i < MAX_ITEMS && i < movies.size(); i++) {
                                Movie movie = movies.get(i);
                                mMovies.add(movie);
                            }
                        }
                        mAdapterMovies.notifyDataSetChanged();
                    }
                    FragmentUtils.updateProgressBar(progressBarMovies, false);
                } else {
                    Log.i("TAG", "Res: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                Log.i("TAG", "Error: " + t.getMessage());
                FragmentUtils.updateProgressBar(progressBarMovies, false);
            }
        });
    }

    /**
     * Fills the TV section.
     */
    private void fillTVSection(){
        mRecyclerViewTV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewTV.setAdapter(mAdapterTV);

        FragmentUtils.updateProgressBar(progressBarTV, true);
        DiscoverService service = ServiceGenerator.createService(DiscoverService.class);
        Call<TVResults> call = service.onTv(StringUtils.getDateOnTheAir(),
                StringUtils.getDateToday(), "popularity.desc", mLang, mPage,
                getString(R.string.THE_MOVIE_DB_API_TOKEN));
        call.enqueue(new Callback<TVResults>() {
            @Override
            public void onResponse(Call<TVResults> call, Response<TVResults> response) {
                if (response.isSuccessful()) {
                    List<TV> tvs = response.body().getTVs();
                    mTVList.clear();
                    if (tvs != null) {
                        for (int i = 0; i < MAX_ITEMS && i < tvs.size() ; i++) {
                            TV tv = tvs.get(i);
                            mTVList.add(tv);
                        }
                        mAdapterTV.notifyDataSetChanged();
                    }
                    FragmentUtils.updateProgressBar(progressBarTV, false);
                } else {
                    Log.i("TAG", "Res: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TVResults> call, Throwable t) {
                Log.i("TAG", "Error: " + t.getMessage());
                FragmentUtils.updateProgressBar(progressBarTV, false);
            }
        });
    }

    /**
     * Changes activity to the correct page.
     * @param view
     */
    @Override
    public void onClick(View view) {
        MainActivity activity = (MainActivity) getActivity();
        switch(view.getId()){
            case R.id.home_in_theaters:
                activity.switchPagerAdapter(MOVIES);
                activity.showPage(MOVIES);
                activity.assignCheckedItem(MOVIES);
                break;

            case R.id.home_on_tv:
                activity.switchPagerAdapter(TV_SHOWS);
                activity.showPage(TV_SHOWS);
                activity.assignCheckedItem(TV_SHOWS);
                break;
        }
    }
}
