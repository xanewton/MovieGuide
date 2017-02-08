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

/**
 * Person
 */
public class Person {

    @SerializedName("adult")
    @Expose
    private boolean mAdult;
    @SerializedName("biography")
    @Expose
    private String mBiography;
    @SerializedName("birthday")
    @Expose
    private String mBirthday;
    @SerializedName("deathday")
    @Expose
    private String mDeathday;
    @SerializedName("gender")
    @Expose
    private int mGender;
    @SerializedName("homepage")
    @Expose
    private String mHomepage;
    @SerializedName("id")
    @Expose
    private String mId;
    @SerializedName("imdb_id")
    @Expose
    private String mImdbId;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("place_of_birth")
    @Expose
    private String mPlaceOfBirth;
    @SerializedName("popularity")
    @Expose
    private double mPopularity;
    @SerializedName("profile_path")
    @Expose
    private String mProfilePath;
    @SerializedName("images")
    @Expose
    private Images mImages;

    public boolean isAdult() {
        return mAdult;
    }

    public void setAdult(boolean adult) {
        mAdult = adult;
    }

    public String getBiography() {
        return mBiography;
    }

    public void setBiography(String biography) {
        mBiography = biography;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(String birthday) {
        mBirthday = birthday;
    }

    public String getDeathday() {
        return mDeathday;
    }

    public void setDeathday(String deathday) {
        mDeathday = deathday;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public String getHomepage() {
        return mHomepage;
    }

    public void setHomepage(String homepage) {
        mHomepage = homepage;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getImdbId() {
        return mImdbId;
    }

    public void setImdbId(String imdbId) {
        mImdbId = imdbId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPlaceOfBirth() {
        return mPlaceOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        mPlaceOfBirth = placeOfBirth;
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

    public Images getImages() {
        return mImages;
    }

    public void setImages(Images images) { mImages = images;
    }
}
