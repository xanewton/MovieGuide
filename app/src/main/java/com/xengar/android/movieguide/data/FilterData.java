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
package com.xengar.android.movieguide.data;

import java.io.Serializable;

/**
 * FilterData
 */
public class FilterData implements Serializable {
    private int mType;
    private String mGenres;
    private String mSortType;
    private String mMinRating;

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(String genres) {
        mGenres = genres;
    }

    public String getMinRating() {
        return mMinRating;
    }

    public void setMinRating(String minRating) {
        mMinRating = minRating;
    }

    public String getSortType() {
        return mSortType;
    }

    public void setSortType(String sortType) {
        mSortType = sortType;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
