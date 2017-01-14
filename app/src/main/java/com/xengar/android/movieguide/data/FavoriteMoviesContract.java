package com.xengar.android.movieguide.data;

import android.provider.BaseColumns;

/**
 * Created by xengar on 1/13/2017.
 */
public final class FavoriteMoviesContract {
    private FavoriteMoviesContract() {
    }

    /* Inner class that defines the table contents */
    public static final class FavoriteMovieColumn implements BaseColumns {
        public static final String TABLE_NAME = "FAVORITE_MOVIES_TBL";
        public static final String COLUMN_NAME_MOVIE_ID = "movieid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_MOVIE_PLOT = "movieplot";
        public static final String COLUMN_POSTER_PATH = "path";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_BACKGROUND_PATH = "background_path";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_COUNTRIES = "original_countries";
        public static final String COLUMN_PROD_COMPANIES = "production_companies";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_IMDB_ID = "imdb_id";
    }
}
