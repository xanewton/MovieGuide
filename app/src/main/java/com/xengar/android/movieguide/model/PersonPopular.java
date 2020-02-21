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
package com.xengar.android.movieguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * PersonPopular
 */
public class PersonPopular {

    @SerializedName("adult")
    @Expose
    private boolean mAdult;
    @SerializedName("id")
    @Expose
    private String mId;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("profile_path")
    @Expose
    private String mProfilePath;
    @SerializedName("popularity")
    @Expose
    private double mPopularity;
    @SerializedName("known_for")
    @Expose
    private List<KnownFor> mKnownFor;
    private String mFirstKnownFor;

    public boolean isAdult() {
        return mAdult;
    }

    public void setAdult(boolean adult) {
        mAdult = adult;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(double popularity) {
        mPopularity = popularity;
    }

    public String getProfilePath() {
        return mProfilePath;
    }

    public void setProfilePath(String profilePath) {
        mProfilePath = profilePath;
    }

    public List<KnownFor> getKnownFor() {
        return mKnownFor;
    }

    public void setKnownFor(List<KnownFor> knownFor) {
        mKnownFor = knownFor;
    }

    public String getFirstKnownFor() {
        String backgroundPath = null;
        if (mKnownFor!= null && mKnownFor.size() > 0){
            KnownFor knownFor = mKnownFor.get(0);
            backgroundPath = knownFor.getBackgroundPath();
        } else if (mFirstKnownFor != null) {
            backgroundPath = mFirstKnownFor;
        }
        return backgroundPath;
    }

    public void setFirstKnownFor(String firstKnownForPath) {
        this.mFirstKnownFor = firstKnownForPath;
    }


    public class KnownFor {
        @SerializedName("title")
        private String mTitle;
        @SerializedName("poster_path")
        private String mPosterPath;
        @SerializedName("backdrop_path")
        private String mBackgroundPath;
        @SerializedName("id")
        private String mId;

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getPosterPath() {
            return mPosterPath;
        }

        public void setPosterPath(String posterPath) {
            mPosterPath = posterPath;
        }

        public String getBackgroundPath(){
            return mBackgroundPath;
        }

        public void setBackgroundPath(String posterPath) {
            mBackgroundPath = posterPath;
        }
    }
}
