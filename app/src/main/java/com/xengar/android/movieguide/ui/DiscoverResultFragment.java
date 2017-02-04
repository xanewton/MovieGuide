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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.ImageAdapter;
import com.xengar.android.movieguide.data.FilterData;
import com.xengar.android.movieguide.data.ImageItem;
import com.xengar.android.movieguide.data.Movie;
import com.xengar.android.movieguide.data.MovieResults;
import com.xengar.android.movieguide.data.TV;
import com.xengar.android.movieguide.data.TVResults;
import com.xengar.android.movieguide.service.DiscoverService;
import com.xengar.android.movieguide.service.ServiceGenerator;
import com.xengar.android.movieguide.sync.FetchItemListener;
import com.xengar.android.movieguide.sync.OnItemClickListener;
import com.xengar.android.movieguide.utils.CustomErrorView;
import com.xengar.android.movieguide.utils.FragmentUtils;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xengar.android.movieguide.utils.Constants.ARG_FILTER_DATA;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.POSTER_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverResultFragment extends Fragment {

    private static final String TAG = DiscoverResultFragment.class.getSimpleName();

    public static final int TYPE_MOVIES = 0;
    public static final int TYPE_TV = 1;

    private CircularProgressBar progressBar;
    private CustomErrorView mCustomErrorView;

    private FilterData mFilterData;
    private String mGenres;
    private String mSortBy;
    private String mMinRating;
    private String mLang;

    private ImageAdapter adapter;
    private GridView gridview;
    private int mPage = 1;
    private int mTotalPages = 1;


    public DiscoverResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ImageAdapter(getActivity(), ImageAdapter.POSTER_IMAGE);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_universal, container, false);
        progressBar = (CircularProgressBar) view.findViewById(R.id.progressBar);
        mCustomErrorView = (CustomErrorView) view.findViewById(R.id.error);

        mFilterData = (FilterData) getArguments().getSerializable(ARG_FILTER_DATA);
        mLang = FragmentUtils.getFormatLocale(getActivity());
        final int type = mFilterData.getType();
        final String itemType = (type == TYPE_MOVIES)? MOVIES : TV_SHOWS;

        mGenres = mFilterData.getGenres();
        mGenres = (mGenres == null) ? "" : mGenres;
        mSortBy = mFilterData.getSortType();
        mMinRating = mFilterData.getMinRating();

        gridview = (GridView) view.findViewById(R.id.gridview);
        if (getActivity() instanceof OnItemClickListener) {
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    ((OnItemClickListener) getActivity()).onItemSelectionClick( itemType,
                            (int) adapter.getItemId(position));
                }
            });
        }
        gridview.setAdapter(adapter);
        updateProgressBar(true);
        return view;
    }

    private void updateProgressBar(boolean visibility) {
        if (progressBar != null) {
            progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Checks for internet connection.
     * @return true if connected or connecting
     */
    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkInternetConnection()) {
            Log.e(TAG, "Network is not available");
            onLoadFailed(new Throwable(getString(R.string.network_not_available_message)));
            return;
        }

        switch (mFilterData.getType()){
            case TYPE_MOVIES:
                gridview.setOnScrollListener(new ItemViewScrollListener(MOVIES));
                break;

            case TYPE_TV:
                gridview.setOnScrollListener(new ItemViewScrollListener(TV_SHOWS));
                break;
        }
    }

    private void onLoadFailed(Throwable t) {
        mCustomErrorView.setError(t);
        mCustomErrorView.setVisibility(View.VISIBLE);
        updateProgressBar(false);
    }

    /**
     * Look for the movies.
     * @param fetchItemListener
     */
    private void discoverMovies(final FetchItemListener fetchItemListener) {
        DiscoverService discoverMoviesService = ServiceGenerator.createService(DiscoverService.class);
        Call<MovieResults> discoverMoviesCall =
                discoverMoviesService.discoverMovie(
                        getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage, mSortBy, mMinRating, mGenres);
        discoverMoviesCall.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                if (response.isSuccessful()) {
                    List<Movie> movies = response.body().getMovies();
                    int adds = 0;
                    for (Movie movie:movies) {
                        adds += adapter.add(
                                new ImageItem(POSTER_BASE_URI + movie.getPosterPath(),
                                        Integer.parseInt(movie.getId()), movie.getTitle(), null));
                    }
                    if (adds != 0) {
                        adapter.notifyDataSetChanged();
                        updateProgressBar(false);
                        mTotalPages = response.body().getTotalPages();
                        if (mPage < mTotalPages)
                            mPage++;
                        else
                            fetchItemListener.lastPageReached();
                    }
                    fetchItemListener.onFetchCompleted();
                } else {
                    Log.i("TAG", "Res: " + response.code());
                    fetchItemListener.onFetchFailed();
                }
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                onLoadFailed(t);
            }
        });
    }

    /**
     * Look for the TV Shows.
     */
    private void discoverTV(final FetchItemListener fetchItemListener) {
        DiscoverService discoverTvService = ServiceGenerator.createService(DiscoverService.class);
        Call<TVResults> discoverTvCall = discoverTvService.discoverTv(
                getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage, mSortBy, mMinRating, mGenres);
        discoverTvCall.enqueue(new Callback<TVResults>() {
            @Override
            public void onResponse(Call<TVResults> call, Response<TVResults> response) {
                if (response.isSuccessful()) {
                    List<TV> tvs = response.body().getTVs();
                    int adds = 0;
                    for (TV tv:tvs) {
                        adds += adapter.add(
                                new ImageItem(POSTER_BASE_URI + tv.getPosterPath(),
                                        Integer.parseInt(tv.getId()), tv.getName(), null));
                    }
                    if (adds != 0) {
                        adapter.notifyDataSetChanged();
                        updateProgressBar(false);
                        mTotalPages = response.body().getTotalPages();
                        if (mPage < mTotalPages)
                            mPage++;
                        else
                            fetchItemListener.lastPageReached();
                    }
                    fetchItemListener.onFetchCompleted();
                } else {
                    Log.i("TAG", "Res: " + response.code());
                    fetchItemListener.onFetchFailed();
                }
            }

            @Override
            public void onFailure(Call<TVResults> call, Throwable t) {
                onLoadFailed(t);
            }
        });
    }



    /**
     * ItemViewScrollListener
     */
    private class ItemViewScrollListener implements AbsListView.OnScrollListener, FetchItemListener {

        private boolean loadingState = false;
        private boolean lastPageReached = false;
        private String itemType = null;

        //Constructor
        public ItemViewScrollListener(String itemType){
            this.itemType = itemType;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if (firstVisibleItem + visibleItemCount >= totalItemCount) {

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
        public void lastPageReached(){
            lastPageReached = true;
        }
    }

}
