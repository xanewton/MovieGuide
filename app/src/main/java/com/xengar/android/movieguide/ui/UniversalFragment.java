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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.PosterAdapter;
import com.xengar.android.movieguide.data.ImageItem;
import com.xengar.android.movieguide.model.Movie;
import com.xengar.android.movieguide.model.MovieResults;
import com.xengar.android.movieguide.model.TV;
import com.xengar.android.movieguide.model.TVResults;
import com.xengar.android.movieguide.service.DiscoverService;
import com.xengar.android.movieguide.service.MovieService;
import com.xengar.android.movieguide.service.ServiceGenerator;
import com.xengar.android.movieguide.service.TVService;
import com.xengar.android.movieguide.sync.FetchItemListener;
import com.xengar.android.movieguide.utils.CustomErrorView;
import com.xengar.android.movieguide.utils.FragmentUtils;
import com.xengar.android.movieguide.utils.StringUtils;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.xengar.android.movieguide.utils.Constants.ITEM_CATEGORY;
import static com.xengar.android.movieguide.utils.Constants.LOG;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.NOW_PLAYING_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.ON_THE_AIR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.SIZE_W185;
import static com.xengar.android.movieguide.utils.Constants.SIZE_W342;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.UPCOMING_MOVIES;

/**
 * A placeholder fragment containing a simple view.
 * UniversalFragment for processing movies and TV shows
 */
public class UniversalFragment extends Fragment {

    private static final String TAG = UniversalFragment.class.getSimpleName();

    private PosterAdapter adapter;
    private String itemType = UPCOMING_MOVIES;

    private CircularProgressBar progressBar;
    private CustomErrorView mCustomErrorView;
    private String mLang;
    private RecyclerView recycler;
    private int mPage = 1;
    private int mTotalPages = 1;


    public UniversalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null)
            itemType = getArguments().getString(ITEM_CATEGORY, UPCOMING_MOVIES);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_universal, container, false);
        progressBar = (CircularProgressBar) view.findViewById(R.id.progressBar);
        mCustomErrorView = (CustomErrorView) view.findViewById(R.id.error);

        mLang = FragmentUtils.getFormatLocale(getActivity());
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        int columns = (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) ? 2 : 3;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
        recycler.setLayoutManager(layoutManager);

        String posterType = (itemType.contentEquals(POPULAR_MOVIES)
                || itemType.contentEquals(UPCOMING_MOVIES)
                || itemType.contentEquals(NOW_PLAYING_MOVIES)
                || itemType.contentEquals(TOP_RATED_MOVIES))? MOVIES : TV_SHOWS;
        adapter = new PosterAdapter(getContext(), posterType);
        recycler.setAdapter(adapter);
        FragmentUtils.updateProgressBar(progressBar, true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!FragmentUtils.checkInternetConnection(getActivity())) {
            onLoadFailed(new Throwable(getString(R.string.network_not_available_message)));
            return;
        }

        if (!itemType.contentEquals(POPULAR_TV_SHOWS) && !itemType.contentEquals(TOP_RATED_TV_SHOWS)
                && !itemType.contentEquals(ON_THE_AIR_TV_SHOWS) && !itemType.contentEquals(NOW_PLAYING_MOVIES)
                && !itemType.contentEquals(TOP_RATED_MOVIES) && !itemType.contentEquals(UPCOMING_MOVIES)
                && !itemType.contentEquals(POPULAR_MOVIES))
            return;

        mTotalPages = 1;
        LastItemListener listener = new LastItemListener(itemType);
        recycler.addOnScrollListener(listener);
        listener.loadPage();
    }

    private void onLoadFailed(Throwable t) {
        mCustomErrorView.setError(t);
        mCustomErrorView.setVisibility(View.VISIBLE);
        FragmentUtils.updateProgressBar(progressBar, false);
    }

    /**
     * Query the movies page.
     * @param listener listener
     * @param itemType type of item
     */
    private void loadMovies(final FetchItemListener listener, String itemType) {
        DiscoverService discoverService;
        MovieService movieService;
        Call<MovieResults> call = null;
        switch(itemType) {
            case NOW_PLAYING_MOVIES:
                discoverService = ServiceGenerator.createService(DiscoverService.class);
                call = discoverService.inTheaters(getString(R.string.THE_MOVIE_DB_API_TOKEN),
                                    mLang, mPage, "popularity.desc", StringUtils.inTheatersLte(),
                                    StringUtils.inTheatersGte());
                break;

            case TOP_RATED_MOVIES:
                movieService = ServiceGenerator.createService(MovieService.class);
                call = movieService.topRated(getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage);
                break;

            case UPCOMING_MOVIES:
                movieService = ServiceGenerator.createService(MovieService.class);
                call = movieService.upcoming(getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage);
                break;

            case POPULAR_MOVIES:
                movieService = ServiceGenerator.createService(MovieService.class);
                call = movieService.popular(getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage);
                break;
        }
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                if (response.isSuccessful()) {
                    List<Movie> movies = response.body().getMovies();
                    int adds = 0;
                    for (Movie movie : movies) {
                        adds += adapter.add(
                                new ImageItem(TMDB_IMAGE_URL + SIZE_W342 + movie.getPosterPath(),
                                        Integer.parseInt(movie.getId()), movie.getTitle(), null, null));
                    }
                    if (adds != 0) {
                        adapter.notifyDataSetChanged();
                        FragmentUtils.updateProgressBar(progressBar, false);
                        mTotalPages = response.body().getTotalPages();
                        if (mPage < mTotalPages)
                            mPage++;
                        else
                            listener.lastPageReached();
                    }
                    listener.onFetchCompleted();
                } else {
                    if (LOG) {
                        Log.i("TAG", "Res: " + response.code());
                    }
                    listener.onFetchFailed();
                }
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                onLoadFailed(t);
            }
        });
    }

    /**
     * Query the TV Show page.
     * @param listener listener
     * @param itemType type of item
     */
    private void loadTVShows(final FetchItemListener listener, String itemType) {
        DiscoverService discoverService;
        TVService tvService;
        Call<TVResults> call = null;
        switch(itemType) {
            case POPULAR_TV_SHOWS:
                tvService = ServiceGenerator.createService(TVService.class);
                call = tvService.popular(getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage);
                break;

            case TOP_RATED_TV_SHOWS:
                tvService = ServiceGenerator.createService(TVService.class);
                call = tvService.topRated(getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage);
                break;

            case ON_THE_AIR_TV_SHOWS:
                discoverService = ServiceGenerator.createService(DiscoverService.class);
                call = discoverService.onTv(StringUtils.getDateOnTheAir(), StringUtils.getDateToday(),
                        "popularity.desc", mLang, mPage, getString(R.string.THE_MOVIE_DB_API_TOKEN));
                break;

        }
        call.enqueue(new Callback<TVResults>() {
            @Override
            public void onResponse(Call<TVResults> call, Response<TVResults> response) {
                if (response.isSuccessful()) {
                    List<TV> tvs = response.body().getTVs();
                    int adds = 0;
                    for (TV tv : tvs) {
                        adds += adapter.add(
                                    new ImageItem(TMDB_IMAGE_URL + SIZE_W185 + tv.getPosterPath(),
                                            Integer.parseInt(tv.getId()), tv.getName(), null, null));
                    }
                    if (adds != 0) {
                        adapter.notifyDataSetChanged();
                        FragmentUtils.updateProgressBar(progressBar, false);
                        mTotalPages = response.body().getTotalPages();
                        if (mPage < mTotalPages)
                            mPage++;
                        else
                            listener.lastPageReached();
                    }
                    listener.onFetchCompleted();
                } else {
                    if (LOG) {
                        Log.i("TAG", "Res: " + response.code());
                    }
                    listener.onFetchFailed();
                }
            }

            @Override
            public void onFailure(Call<TVResults> call, Throwable t) {
                onLoadFailed(t);
            }
        });
    }



    /**
     * Listener to callback when the last item of the adapter is visible to the user.
     * It should then be the time to load more items.
     **/
    private class LastItemListener extends RecyclerView.OnScrollListener implements FetchItemListener {

        private boolean loadingState = false;
        private boolean lastPageReached = false;
        private String itemType = null;

        //Constructor
        public LastItemListener(String itemType){
            this.itemType = itemType;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            // init
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            RecyclerView.Adapter adapter = recyclerView.getAdapter();

            if (layoutManager.getChildCount() > 0) {
                // Calculate
                int indexOfLastItemViewVisible = layoutManager.getChildCount() -1;
                View lastItemViewVisible = layoutManager.getChildAt(indexOfLastItemViewVisible);
                int adapterPosition = layoutManager.getPosition(lastItemViewVisible);
                boolean isLastItemVisible = (adapterPosition == adapter.getItemCount() -1);

                /**
                 * Here you should load more items because user is seeing the last item of the list.
                 * Advice: you should add a boolean value to the class
                 * so that the method {@link #loadPage()} will be triggered only once
                 * and not every time the user touch the screen ;)
                 **/
                if (isLastItemVisible)
                    loadPage();
            }
        }

        /**
         * Load page of posters.
         */
        public void loadPage() {
            if (!loadingState && !lastPageReached) {
                switch (itemType) {
                    case POPULAR_TV_SHOWS:
                        loadTVShows(this, POPULAR_TV_SHOWS);
                        break;
                    case TOP_RATED_TV_SHOWS:
                        loadTVShows(this, TOP_RATED_TV_SHOWS);
                        break;
                    case ON_THE_AIR_TV_SHOWS:
                        loadTVShows(this, ON_THE_AIR_TV_SHOWS);
                        break;

                    case NOW_PLAYING_MOVIES:
                        loadMovies(this, NOW_PLAYING_MOVIES);
                        break;
                    case TOP_RATED_MOVIES:
                        loadMovies(this, TOP_RATED_MOVIES);
                        break;
                    case UPCOMING_MOVIES:
                        loadMovies(this, UPCOMING_MOVIES);
                        break;
                    case POPULAR_MOVIES:
                        loadMovies(this, POPULAR_MOVIES);
                        break;
                }
                loadingState = true;
            }
        }

        @Override
        public void onFetchCompleted() {
            loadingState = false;
        }

        @Override
        public void onFetchFailed() {
            loadingState = false;
            onLoadFailed(new Throwable(getString(R.string.remote_service_connection_error)));
        }

        @Override
        public void lastPageReached() {
            lastPageReached = true;
        }
    }

}
