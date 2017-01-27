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

import com.xengar.android.movieguide.R;

/**
 * {@link ContentProvider} for MovieGuide app.
 */
public class FavoritesProvider extends ContentProvider {

    private static final String TAG = FavoritesProvider.class.getSimpleName();
    // URI matcher code for the content URI for the FAVORITE_MOVIES_TBL table
    public static final int MOVIES = 100;
    // URI matcher code for the content URI for a single movie in the FAVORITE_MOVIES_TBL table
    public static final int MOVIE_ID = 101;
    // URI matcher code for the content URI for the FAVORITE_TV_SHOWS_TBL table
    public static final int TV_SHOWS = 200;
    // URI matcher code for the content URI for a single movie in the FAVORITE_TV_SHOWS_TBL table
    public static final int TV_SHOW_ID = 201;

    private FavoritesDbHelper helper;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    static final UriMatcher sUriMatcher = buildUriMatcher();


    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.xengar.android.movieguide/movie" will map to
        // the integer code {@link #MOVIES}. This URI is used to provide access to MULTIPLE rows
        // of the verbs table.
        matcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_MOVIE, MOVIES);

        // The content URI of the form "content://com.xengar.android.movieguide/movie/#" will map
        // to the integer code {@link #MOVIE_ID}. This URI is used to provide access to ONE single
        // row of the verbs table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.xengar.android.movieguide/movie/3" matches, but
        // "content://com.xengar.android.movieguide/movie" (without a number at the end) doesn't.
        matcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_MOVIE_ID, MOVIE_ID);

        matcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_TV_SHOW, TV_SHOWS);
        matcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_TV_SHOW_ID, TV_SHOW_ID);

        return matcher;
    }

    // Constructor
    public FavoritesProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String table = null;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
            case MOVIE_ID:
                table = FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL;
                break;
            case TV_SHOWS:
            case TV_SHOW_ID:
                table = FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL;
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.error_unknown_uri) + uri);
        }
        int rowsDeleted = db.delete(table, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                return "vnd.android.cursor.dir/movie";
            case MOVIE_ID:
                return "vnd.android.cursor.item/movie";
            case TV_SHOWS:
                return "vnd.android.cursor.dir/tvshow";
            case TV_SHOW_ID:
                return "vnd.android.cursor.item/tvshow";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String table = null;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
            case MOVIE_ID:
                table = FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL;
                break;
            case TV_SHOWS:
            case TV_SHOW_ID:
                table = FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL;
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.error_unknown_uri) + uri);
        }

        long rowId = db.insert(table, null, values);
        Log.v(TAG, "rowId = " + rowId);
        Uri returnUri = null;
        if (rowId > 0)
            returnUri = ContentUris.withAppendedId(uri, rowId);
        else
            throw new android.database.SQLException(
                    getContext().getString(R.string.error_failed_insert_row) + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
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
                builder.appendWhere(FavoritesContract.FavoriteColumns.COLUMN_MOVIE_ID
                        + " = " + uri.getLastPathSegment());
                break;
            case TV_SHOWS:
                builder.setTables(FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL);
                break;
            case TV_SHOW_ID:
                builder.setTables(FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL);
                builder.appendWhere(FavoritesContract.FavoriteColumns.COLUMN_TV_SHOW_ID
                        + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor returnCursor =
                builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String table = null;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
            case MOVIE_ID:
                table = FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL;
                break;
            case TV_SHOWS:
            case TV_SHOW_ID:
                table = FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        int rowsUpdated = db.update(table, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
