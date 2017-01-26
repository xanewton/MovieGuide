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

import android.provider.BaseColumns;

/**
 * FavoritesContract
 */
public final class FavoritesContract {
    private FavoritesContract() {
    }

    /* Inner class that defines the table contents */
    public static final class FavoriteColumns implements BaseColumns {
        public static final String FAVORITE_MOVIES_TBL = "FAVORITE_MOVIES_TBL";
        public static final String FAVORITE_TV_SHOWS_TBL = "FAVORITE_TV_SHOWS_TBL";
        public static final String COLUMN_NAME_MOVIE_ID = "movieid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_MOVIE_PLOT = "movieplot";
        public static final String COLUMN_POSTER_PATH = "path";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_BACKGROUND_PATH = "background_path";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_COUNTRIES = "original_countries";
        public static final String COLUMN_PROD_COMPANIES = "production_companies";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_IMDB_ID = "imdb_id";
        public static final String COLUMN_BUDGET = "budget";
        public static final String COLUMN_REVENUE = "revenue";
        public static final String COLUMN_HOMEPAGE = "homepage";
    }
}
