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
import android.widget.Toast;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.ImageAdapter;
import com.xengar.android.movieguide.sync.FetchItemListener;
import com.xengar.android.movieguide.sync.FetchMovie;
import com.xengar.android.movieguide.sync.OnItemClickListener;


/**
 * A placeholder fragment containing a simple view.
 * UniversalFragment for processing movies and TV shows
 */
public class UniversalFragment extends Fragment {

    private static final String TAG = UniversalFragment.class.getSimpleName();
    private static final String SHARED_PREF_NAME = "com.xengar.android.movieguide";
    private static final String POSTER_BASE_URI = "http://image.tmdb.org/t/p/w185";

    private ImageAdapter adapter;
    private String sortOrder;
    private GridView gridview;
    private String apiKey;
    private String posterBaseUri;
    private String itemType;


    public UniversalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ImageAdapter(getActivity());
        setRetainInstance(true);

        itemType = getArguments().getString("itemType", "UpcomingMovies");
        if (itemType.equals("PopularMovies")) {
            sortOrder = "popularity.desc";
        } else if (itemType.equals("NowPlayingMovies")) {
            sortOrder = "current.desc";
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.universal_fragment, container, false);
        Log.v(TAG, "onCreateView");
        gridview = (GridView) view.findViewById(R.id.gridview);
        if (getActivity() instanceof OnItemClickListener) {
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    int movieId = (int) adapter.getItemId(position);
                    ((OnItemClickListener) getActivity()).onMovieClick(movieId);
                }
            });
        }
        gridview.setAdapter(adapter);
        posterBaseUri = POSTER_BASE_URI;
        apiKey = getString(R.string.THE_MOVIE_DB_API_TOKEN);
        return view;
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

        boolean isConnected = checkInternetConnection();
        if (!isConnected && !itemType.equals("favorites")) {
            Log.e(TAG, "Network is not available");
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    R.string.network_not_available_message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (itemType.equals("favorites")) {
            getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        } else {
            getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        }

        if (itemType.equals("favorites")) {
            /*adapter.clearData();
            FetchFavoriteMovieTask task = new FetchFavoriteMovieTask(adapter, getActivity().getContentResolver(), posterBaseUri);
            task.execute();*/
        } else if (itemType.equals("NowPlayingMovies")) {
            gridview.setOnScrollListener(new ItemViewScrollListener("NowPlayingMovies"));
        } else if (itemType.equals("TopRatedMovies")) {
            gridview.setOnScrollListener(new ItemViewScrollListener("TopRatedMovies"));
        } else if (itemType.equals("UpcomingMovies")) {
            gridview.setOnScrollListener(new ItemViewScrollListener("UpcomingMovies"));
        } else {
            gridview.setOnScrollListener(new ItemViewScrollListener("PopularMovies"));
        }
    }


    /**
     * ItemViewScrollListener
     */
    private class ItemViewScrollListener
            implements AbsListView.OnScrollListener, FetchItemListener {

        private static final int PAGE_SIZE = 20;
        private boolean loadingState = false;
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

                if (!loadingState) {
                    FetchMovie fetchTopRated =
                            new FetchMovie(itemType, adapter, this, apiKey, posterBaseUri, sortOrder);
                    fetchTopRated.execute(totalItemCount / PAGE_SIZE + 1);
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
            Toast.makeText(getActivity(), R.string.remote_service_connection_error,
                    Toast.LENGTH_LONG).show();
        }
    }

}