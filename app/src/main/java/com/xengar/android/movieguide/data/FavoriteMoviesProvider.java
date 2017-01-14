package com.xengar.android.movieguide.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * {@link ContentProvider} for MovieGuide app.
 */
public class FavoriteMoviesProvider extends ContentProvider {

    private static final String TAG = FavoriteMoviesProvider.class.getSimpleName();
    public static final String AUTHORITY = "com.xengar.android.movieguide";
    // URI matcher code for the content URI for the FAVORITE_MOVIES_TBL table
    private static final int MOVIES = 100;
    // URI matcher code for the content URI for a single movie in the FAVORITE_MOVIES_TBL table
    private static final int MOVIE_ID = 101;

    private FavoriteMoviesDbHelper helper;

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
        sUriMatcher.addURI(AUTHORITY, FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME, MOVIES);

        // The content URI of the form "content://com.xengar.android.movieguide/movie/#" will map
        // to the integer code {@link #MOVIE_ID}. This URI is used to provide access to ONE single
        // row of the verbs table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.xengar.android.movieguide/movie/3" matches, but
        // "content://com.xengar.android.movieguide/movie" (without a number at the end) doesn't.
        sUriMatcher.addURI(AUTHORITY, FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME + "/#", MOVIE_ID);
    }


    // Constructor
    public FavoriteMoviesProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME,
                selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
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
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long rowId = db.insert(FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME, null, values);

        Log.v(TAG, "rowId = " + rowId);
        return ContentUris.withAppendedId(uri,
                values.getAsInteger(FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_NAME_MOVIE_ID));
    }

    @Override
    public boolean onCreate() {
        helper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                builder.setTables(FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME);
                break;
            case MOVIE_ID:
                builder.setTables(FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME);
                builder.appendWhere(FavoriteMoviesContract.FavoriteMovieColumn.COLUMN_NAME_MOVIE_ID
                        + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = builder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(FavoriteMoviesContract.FavoriteMovieColumn.TABLE_NAME, values, selection,
                selectionArgs);
    }
}
