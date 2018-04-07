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
import android.content.SharedPreferences;
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

import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.SIZE_W342;
import static com.xengar.android.movieguide.utils.Constants.TMDB_IMAGE_URL;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * HomePersonAdapter
 */
public class HomePersonAdapter extends RecyclerView.Adapter<HomePersonAdapter.PersonHolder> {

    private final List<PersonPopular> people;
    private SharedPreferences mPrefs;
    private InterstitialAd mInterstitialAd;
    private Context mContext;

    public HomePersonAdapter(List<PersonPopular> people, final Context context) {
        this.people = people;
        mPrefs = context.getSharedPreferences(SHARED_PREF_NAME, 0);
        mContext = context;
        setupInterstitialAd();
    }

    private void setupInterstitialAd() {
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
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
            showDetails();

            int gridCounter = mPrefs.getInt("gridCounter", 0);
            if (gridCounter % 2 == 0) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            SharedPreferences.Editor editor = mPrefs.edit();
            gridCounter++;
            editor.putInt("gridCounter", gridCounter);
            editor.commit();
        }

        private void showDetails(){
            int position = getAdapterPosition(); // gets item position
            // Check if an item was deleted, but the user clicked it before the UI removed it
            if (position != RecyclerView.NO_POSITION) {
                ActivityUtils.launchPersonActivity(mContext, Integer.valueOf(person.getId()),
                        person.getFirstKnownFor());
            }
        }

    }
}
