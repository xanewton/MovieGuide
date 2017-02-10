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
package com.xengar.android.movieguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.ImageItem;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.HashSet;

import static android.content.ContentValues.TAG;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.PEOPLE;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;

/**
 * PosterAdapter
 */
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder>{

    private final ArrayList<ImageItem> images = new ArrayList<>();
    private final HashSet<Integer> idSet = new HashSet<>();
    private final Context context;
    private final String type;


    public PosterAdapter(final Context context, String type) {
        this.type = type;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.poster_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.imageId = images.get(i).getImageId();
        viewHolder.backgroundPoster = images.get(i).getBackgroundImagePath();
        Picasso.with(context)
                .load(images.get(i).getImagePath())
                .error(R.drawable.disk_reel)
                .resize(342, 513)
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    /**
     * Adds an image item.
     * @param item image item
     */
    public int add(ImageItem item) {
        if (idSet.contains(item.getImageId())) {
            Log.w(TAG, "Poster item duplicate found, itemID = " + item.getImageId());
            return 0;
        }
        images.add(item);
        idSet.add(item.getImageId());
        return 1;
    }


    /*
     * ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView imageView;
        private int imageId;
        private String backgroundPoster;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.poster_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (type){
                case MOVIES:
                    ActivityUtils.launchMovieActivity(context, imageId);
                    break;

                case TV_SHOWS:
                    ActivityUtils.launchTVShowActivity(context, imageId);
                    break;

                case PEOPLE:
                    ActivityUtils.launchPersonActivity(context, imageId, backgroundPoster);
                    break;
            }
        }
    }
}
