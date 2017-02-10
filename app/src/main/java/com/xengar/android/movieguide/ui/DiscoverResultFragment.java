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
import com.xengar.android.movieguide.service.ServiceGenerator;
import com.xengar.android.movieguide.sync.FetchItemListener;
import com.xengar.android.movieguide.utils.CustomErrorView;
import com.xengar.android.movieguide.utils.FragmentUtils;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_DEFAULT_MIN_RATING;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_DEFAULT_SORT_TYPE;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_GENRES;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_MIN_RATING;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_SORT_TYPE;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_TYPE;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.SIZE_W185;
import static com.xengar.android.movieguide.utils.Constants.SIZE_W342;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverResultFragment extends Fragment {

    private static final String TAG = DiscoverResultFragment.class.getSimpleName();

    private CircularProgressBar progressBar;
    private CustomErrorView mCustomErrorView;

    private String itemType = MOVIES;
    private String mGenres = null;
    private String mSortBy = DISCOVER_DEFAULT_SORT_TYPE;
    private String mMinRating = DISCOVER_DEFAULT_MIN_RATING;
    private String mLang = "";

    private RecyclerView recycler;
    private PosterAdapter adapter;
    private int mPage = 1;
    private int mTotalPages = 1;


    public DiscoverResultFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_universal, container, false);
        progressBar = (CircularProgressBar) view.findViewById(R.id.progressBar);
        mCustomErrorView = (CustomErrorView) view.findViewById(R.id.error);

        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREF_NAME, 0);
        mLang = FragmentUtils.getFormatLocale(getActivity());
        mGenres = prefs.getString(DISCOVER_GENRES, null);
        mGenres = (mGenres == null) ? "" : mGenres;
        mSortBy = prefs.getString(DISCOVER_SORT_TYPE, DISCOVER_DEFAULT_SORT_TYPE);
        mMinRating = prefs.getString(DISCOVER_MIN_RATING, DISCOVER_DEFAULT_MIN_RATING);

        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        int columns = (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) ? 2 : 3;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
        recycler.setLayoutManager(layoutManager);

        itemType = prefs.getString(DISCOVER_TYPE, MOVIES);
        adapter = new PosterAdapter(getContext(), itemType);
        recycler.setAdapter(adapter);
        FragmentUtils.updateProgressBar(progressBar, true);
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

        if (!itemType.contentEquals(MOVIES) && !itemType.contentEquals(TV_SHOWS))
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
     * Query the Movies page.
     * @param listener listener
     */
    private void discoverMovies(final FetchItemListener listener){
        DiscoverService service = ServiceGenerator.createService(DiscoverService.class);
        Call<MovieResults> call =
                service.discoverMovie(
                        getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage, mSortBy, mMinRating, mGenres);
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
                    Log.i("TAG", "Res: " + response.code());
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
     */
    private void discoverTV(final FetchItemListener listener) {
        DiscoverService service = ServiceGenerator.createService(DiscoverService.class);
        Call<TVResults> call = service.discoverTv(
                getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage, mSortBy, mMinRating, mGenres);
        call.enqueue(new Callback<TVResults>() {
            @Override
            public void onResponse(Call<TVResults> call, Response<TVResults> response) {
                if (response.isSuccessful()) {
                    List<TV> tvs = response.body().getTVs();
                    int adds = 0;
                    for (TV tv: tvs) {
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
                    Log.i("TAG", "Res: " + response.code());
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
     * Listener to callback when the last item of the adpater is visible to the user.
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
                    case MOVIES:
                        discoverMovies(this);
                        break;
                    case TV_SHOWS:
                        discoverTV(this);
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
