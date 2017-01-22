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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.PosterData;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Image adapter used to fill the list of Movie/TVShow posters.
 */
public class ImageAdapter extends BaseAdapter {

    private static final String TAG = ImageAdapter.class.getSimpleName();
    private static final int IMAGE_WIDTH = 185;
    private static final int IMAGE_HEIGHT = 278;
    private final ArrayList<PosterData> posters = new ArrayList<>();
    private final HashSet<Integer> idSet = new HashSet<>();
    private final float density;
    private final Context mContext;

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
        density = mContext.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getCount() {
        return posters.size();
    }

    @Override
    public Object getItem(int position) {
        return posters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return posters.get(position).getPosterId();
    }

    // Creates a new ImageView for each item referenced by the Adapter.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.image_layout, parent, false);
        } else {
            view = convertView;
        }

        PosterData data = posters.get(position);
        ImageView imageView = (ImageView) view.findViewById(R.id.poster_view);
        view.setLayoutParams(new GridView.LayoutParams((int) (IMAGE_WIDTH * density),
                (int) (IMAGE_HEIGHT * density)));

        if (data.getPosterPath() == null) {
            TextView textView = (TextView) view.findViewById(R.id.poster_title);
            textView.setText(data.getPosterTitle());
        }

        ActivityUtils.loadImage(mContext, data.getPosterPath(), false, 0, imageView, null);
        return view;
    }


    public void add(PosterData res) {
        if (idSet.contains(res.getPosterId())) {
            Log.w(TAG, "Poster item duplicate found, itemID = " + res.getPosterId());
            return;
        }
        posters.add(res);
        idSet.add(res.getPosterId());
    }

    public void addAll(List<PosterData> res) {
        posters.addAll(res);
    }

    public void clearData() {
        posters.clear();
        idSet.clear();
        notifyDataSetChanged();
    }

    public List<PosterData> getPosters() {
        return posters;
    }
}
