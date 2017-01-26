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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * {@link ContentProvider} for MovieGuide app.
 */
public class FavoritesProvider extends ContentProvider {

    private static final String TAG = FavoritesProvider.class.getSimpleName();
    public static final String AUTHORITY = "com.xengar.android.movieguide";
    // URI matcher code for the content URI for the FAVORITE_MOVIES_TBL table
    private static final int MOVIES = 100;
    // URI matcher code for the content URI for a single movie in the FAVORITE_MOVIES_TBL table
    private static final int MOVIE_ID = 101;

    private FavoritesDbHelper helper;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.xengar.android.movieguide/movie" will map to
        // the integer code {@link #MOVIES}. This URI is used to provide access to MULTIPLE rows
        // of the verbs table.
        sUriMatcher.addURI(AUTHORITY, FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL, MOVIES);

        // The content URI of the form "content://com.xengar.android.movieguide/movie/#" will map
        // to the integer code {@link #MOVIE_ID}. This URI is used to provide access to ONE single
        // row of the verbs table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.xengar.android.movieguide/movie/3" matches, but
        // "content://com.xengar.android.movieguide/movie" (without a number at the end) doesn't.
        sUriMatcher.addURI(AUTHORITY, FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL + "/#", MOVIE_ID);
    }


    // Constructor
    public FavoritesProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL,
                selection, selectionArgs);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                return "vnd.android.cursor.dir/movie";
            case MOVIE_ID:
                return "vnd.android.cursor.item/movie";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long rowId = db.insert(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL, null, values);

        Log.v(TAG, "rowId = " + rowId);
        return ContentUris.withAppendedId(uri,
                values.getAsInteger(FavoritesContract.FavoriteColumns.COLUMN_NAME_MOVIE_ID));
    }

    @Override
    public boolean onCreate() {
        helper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                builder.setTables(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL);
                break;
            case MOVIE_ID:
                builder.setTables(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL);
                builder.appendWhere(FavoritesContract.FavoriteColumns.COLUMN_NAME_MOVIE_ID
                        + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL, values, selection,
                selectionArgs);
    }
}