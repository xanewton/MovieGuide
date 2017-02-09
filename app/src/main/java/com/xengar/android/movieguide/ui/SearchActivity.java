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

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.model.MultiSearch;
import com.xengar.android.movieguide.service.SearchService;
import com.xengar.android.movieguide.service.ServiceGenerator;
import com.xengar.android.movieguide.utils.ActivityUtils;
import com.xengar.android.movieguide.utils.FragmentUtils;
import com.xengar.android.movieguide.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xengar.android.movieguide.utils.Constants.SIZE_W154;
import static com.xengar.android.movieguide.utils.Constants.SIZE_W185;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;

/**
 * SearchActivity
 */
public class SearchActivity extends AppCompatActivity {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private Drawable placeholderImage;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private SearchAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        placeholderImage = ContextCompat.getDrawable(getApplicationContext(), R.drawable.disk_reel);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        mSearchView = (SearchView) findViewById(R.id.search_view);

        setupActionBar();
        setupSearchView();

        List<MultiSearch.MultiSearchItem> searchItems = new ArrayList<>();
        mAdapter = new SearchAdapter(searchItems);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        mSearchView.setQueryHint(getString(R.string.action_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 2) {
                    mAdapter.getFilter().filter(newText);
                    return true;
                }

                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return true;
            }
        });
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * SearchAdapter
     */
    class SearchAdapter extends RecyclerView.Adapter<SearchHolder> implements Filterable {

        private final List<MultiSearch.MultiSearchItem> mMultiSearchItems;

        public SearchAdapter(List<MultiSearch.MultiSearchItem> multiMultiSearchItems) {
            mMultiSearchItems = multiMultiSearchItems;
        }

        @Override
        public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_search, parent, false);
            return new SearchHolder(v);
        }

        @Override
        public void onBindViewHolder(SearchHolder holder, int position) {
            MultiSearch.MultiSearchItem item = mMultiSearchItems.get(position);
            holder.bindItem(item);
        }

        @Override
        public int getItemCount() {
            return (mMultiSearchItems != null) ? mMultiSearchItems.size() : 0;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    final FilterResults results = new FilterResults();
                    String lang = FragmentUtils.getFormatLocale(SearchActivity.this);
                    SearchService service = ServiceGenerator.createService(SearchService.class);
                    Call<MultiSearch> call = service.multiSearch(charSequence.toString(),
                            getString(R.string.THE_MOVIE_DB_API_TOKEN), lang, "1");
                    call.enqueue(new Callback<MultiSearch>() {
                        @Override
                        public void onResponse(Call<MultiSearch> call,
                                               Response<MultiSearch> response) {
                            if (response.isSuccessful()) {
                                List<MultiSearch.MultiSearchItem> movies
                                        = response.body().getMultiSearchItems();
                                results.values = movies;
                                results.count = movies != null ? movies.size() : 0;
                                mMultiSearchItems.clear();
                                if (movies != null) {
                                    mMultiSearchItems.addAll(movies);
                                    notifyDataSetChanged();
                                }
                            } else {
                                Log.i(TAG, "Error: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<MultiSearch> call, Throwable t) {
                            Log.i(TAG, "Error: " + t.getMessage());
                        }
                    });

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence,
                                              FilterResults filterResults) {
                    if (filterResults.values != null) {
                        mMultiSearchItems.addAll(
                                (Collection<? extends MultiSearch.MultiSearchItem>)
                                        filterResults.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }

    /**
     * SearchHolder
     */
    public class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final String TYPE_MOVIE = "movie";
        private final String TYPE_TV = "tv";
        private final String TYPE_PERSON = "person";

        private final Context mContext;
        private MultiSearch.MultiSearchItem mItem;

        private final ImageView poster;
        private final ImageView type;
        private final TextView name;
        private final TextView originalName;
        private final TextView voteAverage;
        private final TextView voteCount;
        private final TextView year;
        private final LinearLayout layout;

        public SearchHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            poster = (ImageView) itemView.findViewById(R.id.search_poster);
            type = (ImageView) itemView.findViewById(R.id.search_type);
            name = (TextView) itemView.findViewById(R.id.search_name);
            year = (TextView) itemView.findViewById(R.id.search_year);
            originalName = (TextView) itemView.findViewById(R.id.search_original_name);
            voteAverage = (TextView) itemView.findViewById(R.id.search_vote_average);
            voteCount = (TextView) itemView.findViewById(R.id.search_vote_count);
            layout = (LinearLayout) itemView.findViewById(R.id.search_root);
            layout.setOnClickListener(this);
        }

        void bindItem(MultiSearch.MultiSearchItem item) {
            mItem = item;
            switch (item.getMediaType()) {
                case TYPE_MOVIE:
                    bindItemValues(TMDB_IMAGE_URL + SIZE_W154 + item.getPosterPath(),
                            item.getTitle(), item.getOriginalName(),
                            String.valueOf(item.getVoteAverage()),
                            String.valueOf(item.getVoteCount()),
                            StringUtils.getYear(item.getReleaseDate()),
                            R.drawable.ic_local_movies_black_24dp);
                    break;

                case TYPE_TV:
                    bindItemValues(TMDB_IMAGE_URL + SIZE_W154 + item.getPosterPath(),
                            item.getName(), item.getOriginalName(),
                            String.valueOf(item.getVoteAverage()),
                            String.valueOf(item.getVoteCount()),
                            StringUtils.getYear(item.getFirstAirDate()),
                            R.drawable.ic_tv_black_24dp);
                    break;

                case TYPE_PERSON:
                    bindItemValues(TMDB_IMAGE_URL + SIZE_W185 + item.getProfilePath(),
                            item.getName(), item.getKnownForString(),
                            String.valueOf(item.getVoteAverage()),
                            String.valueOf(item.getVoteCount()),
                            "",
                            R.drawable.ic_person_black_24dp);
                    break;
            }
        }

        private void bindItemValues(final String imagePath, final String name,
                                    final String originalName,
                                    final String voteAverage, final String voteCount,
                                    final String year, final int imageId) {
            Picasso.with(mContext)
                    .load(imagePath)
                    .placeholder(placeholderImage)
                    .fit().centerCrop()
                    .noFade()
                    .error(placeholderImage)
                    .into(poster);
            this.name.setText(name);
            this.originalName.setText(originalName);
            this.voteAverage.setText(voteAverage);
            this.voteCount.setText(voteCount);
            this.year.setText(year);
            ActivityUtils.setImage(getApplicationContext(), type, imageId);
        }

        void startDetailActivity() {
            switch (mItem.getMediaType()) {
                case TYPE_MOVIE:
                    ActivityUtils.launchMovieActivity(getApplicationContext(),
                            Integer.parseInt(mItem.getId()));
                    break;

                case TYPE_TV:
                    ActivityUtils.launchTVShowActivity(getApplicationContext(),
                            Integer.parseInt(mItem.getId()));
                    break;

                case TYPE_PERSON:
                    ActivityUtils.launchPersonActivity(getApplicationContext(),
                            Integer.parseInt(mItem.getId()));
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            startDetailActivity();
        }
    }

}
