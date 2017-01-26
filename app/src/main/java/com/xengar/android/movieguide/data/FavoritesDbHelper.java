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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xengar.android.movieguide.data.FavoritesContract.FavoriteColumns;

/**
 * Database helper for Favorite Movies and TV Shows. Manages database creation and version management.
 */
public class FavoritesDbHelper extends SQLiteOpenHelper {

    // Name of the database file
    private static final String DATABASE_NAME = "FavoriteMovies.db";

    // Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    // Constructor
    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + FavoriteColumns.FAVORITE_MOVIES_TBL + " ("
            + "_id INTEGER PRIMARY KEY, "
            + FavoriteColumns.COLUMN_NAME_MOVIE_ID + " INTEGER, "
            + FavoriteColumns.COLUMN_NAME_TITLE + " TEXT, "
            + FavoriteColumns.COLUMN_MOVIE_PLOT + " TEXT, "
            + FavoriteColumns.COLUMN_POSTER_PATH + " TEXT, "
            + FavoriteColumns.COLUMN_YEAR + " TEXT, "
            + FavoriteColumns.COLUMN_DURATION + " INTEGER, "
            + FavoriteColumns.COLUMN_VOTE_AVERAGE + " REAL, "
            + FavoriteColumns.COLUMN_BACKGROUND_PATH + " TEXT, "
            + FavoriteColumns.COLUMN_ORIGINAL_LANGUAGE + " TEXT, "
            + FavoriteColumns.COLUMN_STATUS + " TEXT, "
            + FavoriteColumns.COLUMN_IMDB_ID + " TEXT, "
            + FavoriteColumns.COLUMN_BUDGET + " TEXT, "
            + FavoriteColumns.COLUMN_REVENUE + " TEXT, "
            + FavoriteColumns.COLUMN_HOMEPAGE + " TEXT )";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + FavoriteColumns.FAVORITE_MOVIES_TBL;

        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}