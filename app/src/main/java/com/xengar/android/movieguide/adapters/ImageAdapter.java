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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.ImageItem;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.xengar.android.movieguide.utils.Constants.SIZE_W342;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;

/**
 * Image adapter used to fill the list of Movie/TVShow images.
 */
public class ImageAdapter extends BaseAdapter {

    public static final String CAST_IMAGE = "cast";
    public static final String MOVIE_IMAGE = "movie";

    private static final String TAG = ImageAdapter.class.getSimpleName();
    private final ArrayList<ImageItem> images = new ArrayList<>();
    private final HashSet<Integer> idSet = new HashSet<>();
    private final float density;
    private final Context mContext;
    private final String type;

    // Constructor
    public ImageAdapter(Context c, String type) {
        mContext = c;
        this.type = type;
        density = mContext.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return images.get(position).getImageId();
    }

    // Creates a new ImageView for each item referenced by the Adapter.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.movie_list_item, parent, false);
        } else {
            view = convertView;
        }
        ImageItem data = images.get(position);

        switch (type){
            case CAST_IMAGE:
                fillCastView(view, data);
                defineOnclickAction(view);
                break;
            case MOVIE_IMAGE:
                fillMovieView(view, data);
                defineOnclickAction(view);
                break;
        }

        return view;
    }

    /**
     * Defines OnClick action.
     * @param view
     */
    private void defineOnclickAction(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageItem item = (ImageItem) v.getTag();
                if (type.contentEquals(MOVIE_IMAGE)) {
                    ActivityUtils.launchMovieActivity(mContext, item.getImageId());
                } else if (type.contentEquals(CAST_IMAGE)) {
                    ActivityUtils.launchPersonActivity(mContext, item.getImageId());
                }
            }
        });
    }

    /**
     * Fills a cast image view.
     * @param view
     * @param image
     */
    private void fillCastView(View view, ImageItem image){
        view.setTag(image);
        TextView castName = (TextView) view.findViewById(R.id.name);
        ImageView castImage = (ImageView) view.findViewById(R.id.image);
        TextView castCharacter = (TextView) view.findViewById(R.id.character);
        castCharacter.setVisibility(View.VISIBLE);

        if (image.getImageTitle() == null) {
            castName.setVisibility(View.GONE);
        } else {
            castName.setText(image.getImageTitle());
        }
        if (image.getImageSubtitle() == null) {
            castCharacter.setVisibility(View.GONE);
        } else {
            castCharacter.setText(image.getImageSubtitle());
        }
        if (image.getImagePath() == null) {
            castImage.setVisibility(View.GONE);
        } else {
            ActivityUtils.loadImage(mContext, TMDB_IMAGE_URL + SIZE_W342 + image.getImagePath(),
                    true, R.drawable.disk_reel, castImage, null);
        }
    }

    /**
     * Fills a movie image view.
     * @param view
     * @param image
     */
    private void fillMovieView(View view, ImageItem image) {
        view.setTag(image);
        TextView title = (TextView) view.findViewById(R.id.name);
        ImageView poster = (ImageView) view.findViewById(R.id.image);
        if (image.getImageTitle() == null) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(image.getImageTitle());
        }
        if (image.getImagePath() == null) {
            poster.setVisibility(View.GONE);
        } else {
            ActivityUtils.loadImage(mContext, TMDB_IMAGE_URL + SIZE_W342 + image.getImagePath(),
                    true, R.drawable.disk_reel, poster, null);
        }
    }

    /**
     * Adds an image item.
     * @param item
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

    public void addAll(List<ImageItem> list) {
        images.addAll(list);
    }

    public void clearData() {
        images.clear();
        idSet.clear();
        notifyDataSetChanged();
    }

    public List<ImageItem> getImages() {
        return images;
    }
}
