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
package com.xengar.android.movieguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.xengar.android.movieguide.service.BaseResults;

import java.util.List;

/**
 * MultiSearch
 */
public class MultiSearch extends BaseResults {

    @SerializedName("results")
    private List<MultiSearchItem> mMultiSearchItems;

    public List<MultiSearchItem> getMultiSearchItems() {
        return mMultiSearchItems;
    }

    public void setMultiSearchItems(List<MultiSearchItem> multiSearchItems) {
        mMultiSearchItems = multiSearchItems;
    }

    public class MultiSearchItem {
        @SerializedName("media_type")
        @Expose
        private String mMediaType;
        @SerializedName("id")
        @Expose
        private String mId;
        @SerializedName("title")
        @Expose
        private String mTitle;
        @SerializedName("original_title")
        @Expose
        private String mOriginalTitle;
        @SerializedName("release_date")
        @Expose
        private String mReleaseDate;
        @SerializedName("name")
        @Expose
        private String mName;
        @SerializedName("original_name")
        @Expose
        private String mOriginalName;
        @SerializedName("poster_path")
        @Expose
        private String mPosterPath;
        @SerializedName("profile_path")
        @Expose
        private String mProfilePath;
        @SerializedName("first_air_date")
        @Expose
        private String mFirstAirDate;
        @SerializedName("vote_average")
        @Expose
        private double mVoteAverage;
        @SerializedName("vote_count")
        @Expose
        private int mVoteCount;

        @SerializedName("known_for")
        @Expose
        private List<KnownFor> mKnownFor;

        public String getFirstAirDate() {
            return mFirstAirDate;
        }

        public void setFirstAirDate(String firstAirDate) {
            mFirstAirDate = firstAirDate;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getMediaType() {
            return mMediaType;
        }

        public void setMediaType(String mediaType) {
            mMediaType = mediaType;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getOriginalName() {
            return mOriginalName;
        }

        public void setOriginalName(String originalName) {
            mOriginalName = originalName;
        }

        public String getOriginalTitle() {
            return mOriginalTitle;
        }

        public void setOriginalTitle(String originalTitle) {
            mOriginalTitle = originalTitle;
        }

        public String getPosterPath() {
            return mPosterPath;
        }

        public void setPosterPath(String posterPath) {
            mPosterPath = posterPath;
        }

        public String getProfilePath() {
            return mProfilePath;
        }

        public void setProfilePath(String profilePath) {
            mProfilePath = profilePath;
        }

        public String getReleaseDate() {
            return mReleaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            mReleaseDate = releaseDate;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public double getVoteAverage() {
            return mVoteAverage;
        }

        public void setVoteAverage(double voteAverage) {
            mVoteAverage = voteAverage;
        }

        public int getVoteCount() {
            return mVoteCount;
        }

        public void setVoteCount(int voteCount) {
            mVoteCount = voteCount;
        }

        public List<KnownFor> getKnownFor() {
            return mKnownFor;
        }

        public String getKnownForString(){
            String known = null;
            String name, title, line;
            for (KnownFor knowFor: mKnownFor) {
                name = knowFor.getName();
                title = knowFor.getTitle();
                line = (name != null)? name: "";
                line = (title != null)? line.concat(" " + title) : line;
                if (line != ""){
                    known = (known == null)? line : known.concat(", " + line);
                }
            }
            return known;
        }

        public void setKnownFor(List<KnownFor> knownFor) {
            mKnownFor = knownFor;
        }

        public class KnownFor {
            @SerializedName("title")
            private String mTitle;
            @SerializedName("name")
            private String mName;

            public String getName() {
                return mName;
            }

            public void setName(String name) {
                mName = name;
            }

            public String getTitle() {
                return mTitle;
            }

            public void setTitle(String title) {
                mTitle = title;
            }
        }
    }
}
