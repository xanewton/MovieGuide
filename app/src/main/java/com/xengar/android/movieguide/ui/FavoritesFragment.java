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

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.adapters.HomeMovieAdapter;
import com.xengar.android.movieguide.adapters.HomeTVAdapter;
import com.xengar.android.movieguide.data.FavoritesContract;
import com.xengar.android.movieguide.model.Movie;
import com.xengar.android.movieguide.model.TV;
import com.xengar.android.movieguide.utils.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_MOVIE_ID;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_NAME;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_POSTER_PATH;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_TITLE;
import static com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns.COLUMN_TV_SHOW_ID;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;

/**
 * FavoritesFragment
 */
public class FavoritesFragment extends Fragment implements View.OnClickListener{

    private LinearLayout moreMovies;
    private RecyclerView mRecyclerViewMovies;
    private CircularProgressBar progressBarMovies;
    private HomeMovieAdapter mAdapterMovies;
    private List<Movie> mMovies;
    private TextView moviesTitle;

    private LinearLayout moreTV;
    private RecyclerView mRecyclerViewTV;
    private CircularProgressBar progressBarTV;
    private HomeTVAdapter mAdapterTV;
    private List<TV> mTVList;
    private TextView tvTitle;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        moreMovies = (LinearLayout) view.findViewById(R.id.home_in_theaters);
        moreMovies.setOnClickListener(this);
        mRecyclerViewMovies = (RecyclerView) view.findViewById(R.id.recycler_view_movies);
        progressBarMovies = (CircularProgressBar) view.findViewById(R.id.progress_bar_movies);
        moviesTitle = (TextView) view.findViewById(R.id.movies_title);
        moviesTitle.setText(getText(R.string.menu_title_movies));
        mMovies = new ArrayList<>();
        mAdapterMovies = new HomeMovieAdapter(mMovies);
        fillMoviesSection();

        moreTV = (LinearLayout) view.findViewById(R.id.home_on_tv);
        moreTV.setOnClickListener(this);
        mRecyclerViewTV = (RecyclerView) view.findViewById(R.id.recycler_view_tv);
        progressBarTV = (CircularProgressBar) view.findViewById(R.id.progress_bar_tv);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(getText(R.string.menu_title_tv_shows));
        mTVList = new ArrayList<>();
        mAdapterTV = new HomeTVAdapter(mTVList);
        fillTVSection();

        return view;
    }

    /**
     * Fills the Movies section.
     */
    private void fillMoviesSection(){
        mRecyclerViewMovies.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewMovies.setAdapter(mAdapterMovies);
        FragmentUtils.updateProgressBar(progressBarMovies, true);

        FetchFavoriteMovies fetch =
                new FetchFavoriteMovies(mAdapterMovies, getActivity().getContentResolver());
        fetch.execute();
    }

    /**
     * Fills the TV section.
     */
    private void fillTVSection(){
        mRecyclerViewTV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewTV.setAdapter(mAdapterTV);

        FragmentUtils.updateProgressBar(progressBarTV, true);
        FetchFavoriteTV fetch =
                new FetchFavoriteTV(mAdapterTV, getActivity().getContentResolver());
        fetch.execute();
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


    /**
     * FetchFavoriteMovies from the database.
     */
    public class FetchFavoriteMovies extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private final String TAG = FetchFavoriteMovies.class.getSimpleName();
        private ContentResolver contentResolver;
        private HomeMovieAdapter adapter;

        // Constructor
        public FetchFavoriteMovies(HomeMovieAdapter adapter, ContentResolver contentResolver) {
            this.adapter = adapter;
            this.contentResolver = contentResolver;
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            ArrayList<Movie> posters = new ArrayList<>();
            String[] columns = new String[]{ COLUMN_POSTER_PATH, COLUMN_MOVIE_ID, COLUMN_TITLE};

            final Cursor cursor = contentResolver.query(FavoritesContract.FavoriteColumns.uriMovie,
                    columns, null, null, null);

            if (cursor != null && cursor.getCount() != 0) {
                Movie data;
                while (cursor.moveToNext()) {
                    data = new Movie();
                    data.setPosterPath(cursor.getString(0));
                    data.setId(String.format("%s", cursor.getInt(1)));
                    data.setTitle(cursor.getString(2));
                    posters.add(data);
                }
            } else {
                Log.d(TAG, "Cursor is empty");
            }
            if (cursor != null)
                cursor.close();
            return posters;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> posters) {
            super.onPostExecute(posters);
            if (posters != null) {
                mMovies.addAll(posters);
                adapter.notifyDataSetChanged();
            }
            FragmentUtils.updateProgressBar(progressBarMovies, false);
        }
    }

    /**
     * FetchFavoriteTVShows from the database.
     */
    public class FetchFavoriteTV extends AsyncTask<Void, Void, ArrayList<TV>> {

        private final String TAG = FetchFavoriteTV.class.getSimpleName();
        private ContentResolver contentResolver;
        private HomeTVAdapter adapter;

        // Constructor
        public FetchFavoriteTV(HomeTVAdapter adapter, ContentResolver contentResolver) {
            this.adapter = adapter;
            this.contentResolver = contentResolver;
        }

        @Override
        protected ArrayList<TV> doInBackground(Void... voids) {
            ArrayList<TV> posters = new ArrayList<>();
            String[] columns = new String[]{ COLUMN_POSTER_PATH, COLUMN_TV_SHOW_ID, COLUMN_NAME};

            final Cursor cursor = contentResolver.query(FavoritesContract.FavoriteColumns.uriTVShow,
                    columns, null, null, null);

            if (cursor != null && cursor.getCount() != 0) {
                TV data;
                while (cursor.moveToNext()) {
                    data = new TV();
                    data.setPosterPath(cursor.getString(0));
                    data.setId(String.format("%s", cursor.getInt(1)));
                    data.setName(cursor.getString(2));
                    posters.add(data);
                }
            } else {
                Log.d(TAG, "Cursor is empty");
            }
            if (cursor != null)
                cursor.close();
            return posters;
        }

        @Override
        protected void onPostExecute(ArrayList<TV> posters) {
            super.onPostExecute(posters);
            if (posters != null) {
                mTVList.addAll(posters);
                adapter.notifyDataSetChanged();
            }
            FragmentUtils.updateProgressBar(progressBarTV, false);
        }
    }

}
