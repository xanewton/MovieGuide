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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * FavoritesContract
 */
public final class FavoritesContract {

    public static final String AUTHORITY = "com.xengar.android.movieguide";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_MOVIE_ID = "movie/*";
    public static final String PATH_TV_SHOW = "tvshow";
    public static final String PATH_TV_SHOW_ID = "tvshow/*";
    public static final String PATH_PERSON = "person";
    public static final String PATH_PERSON_ID = "person/*";


    private FavoritesContract() {
    }

    /* Inner class that defines the table contents */
    public static final class FavoriteColumns implements BaseColumns {
        public static final Uri uriMovie = BASE_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final Uri uriTVShow = BASE_URI.buildUpon().appendPath(PATH_TV_SHOW).build();
        public static final Uri uriPerson = BASE_URI.buildUpon().appendPath(PATH_PERSON).build();

        public static final String FAVORITE_MOVIES_TBL = "FAVORITE_MOVIES_TBL";
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "movieplot";
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

        public static final String FAVORITE_TV_SHOWS_TBL = "FAVORITE_TV_SHOWS_TBL";
        public static final String COLUMN_TV_SHOW_ID = "tvshowid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_FIRST_AIR_DATE = "first_air_date";
        public static final String COLUMN_LAST_AIR_DATE = "last_air_date";
        public static final String COLUMN_NUM_EPISODES = "num_episodes";
        public static final String COLUMN_NUM_SEASONS = "num_seasons";

        public static final String FAVORITE_PERSON_TBL = "FAVORITE_PERSON_TBL";
        public static final String COLUMN_PERSON_ID = "personid";
        public static final String COLUMN_PROFILE_PATH = "profile_path";
        public static final String COLUMN_KNOWNFOR_POSTER_PATH = "knownfor_poster_path";

        public static Uri makeUriForMovie(String symbol) {
            return uriMovie.buildUpon().appendPath(symbol).build();
        }

        public static Uri makeUriForTVShow(String symbol) {
            return uriTVShow.buildUpon().appendPath(symbol).build();
        }

        public static Uri makeUriForPerson(String symbol) {
            return uriPerson.buildUpon().appendPath(symbol).build();
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
