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
import android.content.SharedPreferences;
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
import com.xengar.android.movieguide.sync.FetchFavorite;
import com.xengar.android.movieguide.sync.FetchItemListener;
import com.xengar.android.movieguide.sync.FetchPoster;
import com.xengar.android.movieguide.sync.OnItemClickListener;

import static com.xengar.android.movieguide.utils.Constants.FAVORITE_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.FAVORITE_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.ITEM_CATEGORY;
import static com.xengar.android.movieguide.utils.Constants.NOW_PLAYING_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.ON_THE_AIR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.POSTER_BASE_URI;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.UPCOMING_MOVIES;

/**
 * A placeholder fragment containing a simple view.
 * UniversalFragment for processing movies and TV shows
 */
public class UniversalFragment extends Fragment {

    private static final String TAG = UniversalFragment.class.getSimpleName();

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
        adapter = new ImageAdapter(getActivity(), ImageAdapter.POSTER_IMAGE);
        setRetainInstance(true);

        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREF_NAME, 0);
        itemType = prefs.getString(ITEM_CATEGORY, UPCOMING_MOVIES);
        if (itemType.equals(POPULAR_MOVIES)) {
            sortOrder = "popularity.desc";
        } else if (itemType.equals(NOW_PLAYING_MOVIES)) {
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
                    ((OnItemClickListener) getActivity()).onItemSelectionClick( itemType,
                            (int) adapter.getItemId(position));
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

        // Hide Floating Action Button
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

        switch (itemType) {
            case POPULAR_TV_SHOWS:
                gridview.setOnScrollListener(new ItemViewScrollListener(POPULAR_TV_SHOWS));
                break;
            case TOP_RATED_TV_SHOWS:
                gridview.setOnScrollListener(new ItemViewScrollListener(TOP_RATED_TV_SHOWS));
                break;
            case ON_THE_AIR_TV_SHOWS:
                gridview.setOnScrollListener(new ItemViewScrollListener(ON_THE_AIR_TV_SHOWS));
                break;
            case FAVORITE_TV_SHOWS:
                adapter.clearData();
                FetchFavorite taskTVShows = new FetchFavorite(FAVORITE_TV_SHOWS, adapter,
                        getActivity().getContentResolver(), posterBaseUri);
                taskTVShows.execute();
                break;
            case FAVORITE_MOVIES:
                adapter.clearData();
                FetchFavorite taskMovies = new FetchFavorite(FAVORITE_MOVIES, adapter,
                        getActivity().getContentResolver(), posterBaseUri);
                taskMovies.execute();
                break;
            case NOW_PLAYING_MOVIES:
                gridview.setOnScrollListener(new ItemViewScrollListener(NOW_PLAYING_MOVIES));
                break;
            case TOP_RATED_MOVIES:
                gridview.setOnScrollListener(new ItemViewScrollListener(TOP_RATED_MOVIES));
                break;
            case UPCOMING_MOVIES:
                gridview.setOnScrollListener(new ItemViewScrollListener(UPCOMING_MOVIES));
                break;
            default:
                gridview.setOnScrollListener(new ItemViewScrollListener(POPULAR_MOVIES));
                break;
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
                    FetchPoster fetchTopRated =
                            new FetchPoster(itemType, adapter, this, apiKey, posterBaseUri, sortOrder);
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
