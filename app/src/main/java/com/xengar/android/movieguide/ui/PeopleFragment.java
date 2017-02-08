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
import com.xengar.android.movieguide.model.PersonPopular;
import com.xengar.android.movieguide.model.PersonResults;
import com.xengar.android.movieguide.service.PersonService;
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
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.POSTER_SIZE_W342;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {

    private static final String TAG = PeopleFragment.class.getSimpleName();

    private CircularProgressBar progressBar;
    private CustomErrorView mCustomErrorView;

    private String mLang;

    private RecyclerView recycler;
    private PosterAdapter adapter;
    private int mPage = 1;
    private int mTotalPages = 1;


    public PeopleFragment() {
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

        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        int columns = (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) ? 3 : 4;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
        recycler.setLayoutManager(layoutManager);

        mLang = FragmentUtils.getFormatLocale(getActivity());
        adapter = new PosterAdapter(getContext(), MOVIES);
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

        mTotalPages = 1;
        LastItemListener listener = new LastItemListener();
        recycler.addOnScrollListener(listener);
        listener.loadPage();
    }

    private void onLoadFailed(Throwable t) {
        mCustomErrorView.setError(t);
        mCustomErrorView.setVisibility(View.VISIBLE);
        FragmentUtils.updateProgressBar(progressBar, false);
    }

    /**
     * Query the Person popular page.
     * @param listener
     */
    private void loadPeople(final FetchItemListener listener){
        PersonService service = ServiceGenerator.createService(PersonService.class);
        Call<PersonResults> call =
                service.popular(getString(R.string.THE_MOVIE_DB_API_TOKEN), mLang, mPage);
        call.enqueue(new Callback<PersonResults>() {
            @Override
            public void onResponse(Call<PersonResults> call, Response<PersonResults> response) {
                if (response.isSuccessful()) {
                    List<PersonPopular> people = response.body().getPeople();
                    int adds = 0;
                    for (PersonPopular person : people) {
                        adds += adapter.add(
                                new ImageItem(TMDB_IMAGE_URL + POSTER_SIZE_W342 + person.getProfilePath(),
                                        Integer.parseInt(person.getId()), person.getName(), null));
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
            public void onFailure(Call<PersonResults> call, Throwable t) {
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

        //Constructor
        public LastItemListener(){
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
                 * Advice: you should add a bollean value to the class
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
                loadPeople(this);
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
