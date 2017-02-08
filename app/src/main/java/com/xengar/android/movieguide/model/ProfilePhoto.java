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
 * ProfilePhoto
 */
public class ProfilePhoto {
    @SerializedName("aspect_ratio")
    @Expose
    private double mAspectRatio;
    @SerializedName("file_path")
    @Expose
    private String mFilePath;
    @SerializedName("height")
    @Expose
    private int mHeight;
    @SerializedName("iso_639_1")
    @Expose
    private Object mIso6391;
    @SerializedName("vote_average")
    @Expose
    private double mVoteAverage;
    @SerializedName("vote_count")
    @Expose
    private int mVoteCount;
    @SerializedName("width")
    @Expose
    private int mWidth;

    public double getAspectRatio() {
        return mAspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        mAspectRatio = aspectRatio;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public Object getIso6391() {
        return mIso6391;
    }

    public void setIso6391(Object iso6391) {
        mIso6391 = iso6391;
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

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }
}
