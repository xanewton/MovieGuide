/*
 * Copyright (C) 2017 Angel Newton
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
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.model.PersonPopular;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.List;

import static com.xengar.android.movieguide.utils.Constants.SIZE_W342;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;

/**
 * HomePersonAdapter
 */
public class HomePersonAdapter extends RecyclerView.Adapter<HomePersonAdapter.PersonHolder> {

    private final List<PersonPopular> people;

    public HomePersonAdapter(List<PersonPopular> people) {
        this.people = people;
    }

    @Override
    public PersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_home, parent, false);
        return new PersonHolder(v);
    }

    @Override
    public void onBindViewHolder(PersonHolder holder, int position) {
        PersonPopular person = people.get(position);
        holder.bindMovie(person);
    }

    @Override
    public int getItemCount() {
        return (people != null) ? people.size() : 0;
    }


    /**
     * PersonHolder
     */
    public class PersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final Context mContext;
        private PersonPopular person;
        final ImageView personPoster;
        final TextView name;

        public PersonHolder(View itemView) {
            super(itemView);
            personPoster = (ImageView) itemView.findViewById(R.id.item_home_poster);
            name = (TextView) itemView.findViewById(R.id.item_home_title);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        void bindMovie(PersonPopular person) {
            this.person = person;
            name.setText(person.getName());
            Drawable placeholder =
                    ResourcesCompat.getDrawable(
                            mContext.getResources(), R.drawable.disk_reel, null);
            Picasso.with(mContext)
                    .load(TMDB_IMAGE_URL + SIZE_W342 + person.getProfilePath())
                    .placeholder(placeholder)
                    .fit().centerCrop()
                    .noFade()
                    .into(personPoster);
        }

        // Handles the item click.
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            // Check if an item was deleted, but the user clicked it before the UI removed it
            if (position != RecyclerView.NO_POSITION) {
                ActivityUtils.launchPersonActivity(mContext, Integer.valueOf(person.getId()),
                        person.getFirstKnownFor());
            }
        }

    }
}
