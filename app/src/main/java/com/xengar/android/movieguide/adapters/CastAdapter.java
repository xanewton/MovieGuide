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
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.CastData;
import com.xengar.android.movieguide.ui.PersonProfileActivity;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.xengar.android.movieguide.utils.Constants.PERSON_ID;
import static com.xengar.android.movieguide.utils.Constants.POSTER_PERSON_BASE_URI;

/**
 * Cast adapter used to fill the cast list in the Movie Details.
 */
public class CastAdapter extends BaseAdapter {

    private static final String TAG = CastAdapter.class.getSimpleName();
    private final ArrayList<CastData> movieCast = new ArrayList<>();
    private final HashSet<Integer> castIdSet = new HashSet<>();
    private final Context mContext;

    // Constructor
    public CastAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return movieCast.size();
    }

    @Override
    public Object getItem(int position) {
        return movieCast.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movieCast.get(position).getPersonId();
    }

    /**
     * Creates a new ImageView for each item referenced by the Adapter.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.movie_list_item, parent, false);
        } else {
            view = convertView;
        }

        CastData cast = movieCast.get(position);
        view.setTag(cast);
        TextView castName = (TextView) view.findViewById(R.id.name);
        Log.v(TAG, "cast.getCastName() " + cast.getCastName());

        ImageView castImage = (ImageView) view.findViewById(R.id.image);
        TextView castCharacter = (TextView) view.findViewById(R.id.character);
        castCharacter.setVisibility(View.VISIBLE);

        if (cast.getCastName() == null) {
            castName.setVisibility(View.GONE);
        } else {
            castName.setText(cast.getCastName());
        }
        if (cast.getCharacter() == null) {
            castCharacter.setVisibility(View.GONE);
        } else {
            castCharacter.setText(cast.getCharacter());
        }
        if (cast.getCastImagePath() == null) {
            castImage.setVisibility(View.GONE);
        } else {
            ActivityUtils.loadImage(mContext, POSTER_PERSON_BASE_URI + cast.getCastImagePath(),
                    true, R.drawable.no_movie_poster, castImage, null);
        }

        // Now set the onClickListener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CastData data = (CastData) v.getTag();

                // Set PersonID to preferences
                ActivityUtils.saveIntToPreferences(mContext, PERSON_ID, data.getPersonId());

                // Launch a Person Profile Activity
                Intent intent = new Intent(mContext, PersonProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    public void add(CastData cast) {
        if (castIdSet.contains(cast.getPersonId())) {
            Log.w(TAG, "Cast duplicate found, personID = " + cast.getPersonId());
            return;
        }
        movieCast.add(cast);
        castIdSet.add(cast.getPersonId());
    }

    public void addAll(List<CastData> res) {
        movieCast.addAll(res);
    }

    public void clearData() {
        movieCast.clear();
        castIdSet.clear();
        notifyDataSetChanged();
    }

    public List<CastData> getCastData() {
        return movieCast;
    }
}
