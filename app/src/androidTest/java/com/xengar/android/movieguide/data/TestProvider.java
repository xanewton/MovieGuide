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

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This is not a complete set of tests of the FavoritesProvider, but it does test
 * that at least the basic functionality has been implemented correctly.
 */
@RunWith(AndroidJUnit4.class)
public class TestProvider {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
     * Helper function to delete all records from the database table using the ContentProvider.
     * Queries the ContentProvider to make sure that the database has been successfully deleted.
     */
    public void deleteAllRecordsFromProvider() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.getContentResolver().delete(FavoritesContract.FavoriteColumns.uriMovie, null, null);
        Cursor cursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriMovie, null, null, null, null);
        assert cursor != null;
        assertEquals("Error: Records not deleted from Favorite Movies table during delete", 0, cursor.getCount());
        cursor.close();

        appContext.getContentResolver().delete(FavoritesContract.FavoriteColumns.uriTVShow, null, null);
        Cursor cursor2 = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriTVShow, null, null, null, null);
        assert cursor2 != null;
        assertEquals("Error: Records not deleted from Favorite TV Shows table during delete", 0, cursor.getCount());
        cursor2.close();

        appContext.getContentResolver().delete(FavoritesContract.FavoriteColumns.uriPerson, null, null);
        Cursor cursor3 = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriPerson, null, null, null, null);
        assert cursor3 != null;
        assertEquals("Error: Records not deleted from Favorite Person table during delete", 0, cursor.getCount());
        cursor3.close();
    }

    /*
     * Helper function to delete all records from the database table using the database functions only.
     */
    public void deleteAllRecordsFromDB() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL, null, null);
        db.delete(FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL, null, null);
        db.delete(FavoritesContract.FavoriteColumns.FAVORITE_PERSON_TBL, null, null);
        db.close();
    }


    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Before
    public void setUp(){
        deleteAllRecordsFromProvider();
    }

    /*
     * Checks to make sure that the content provider is registered correctly.
     */
    @Test
    public void testProviderRegistry() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        PackageManager pm = appContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // Provider class.
        ComponentName componentName = new ComponentName(appContext.getPackageName(),
                FavoritesProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: FavoritesProvider registered with authority: " + providerInfo.authority
                    + " instead of authority: " + FavoritesContract.AUTHORITY,
                    providerInfo.authority, FavoritesContract.AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: FavoritesProvider not registered at " + appContext.getPackageName(),
                    false);
        }
    }

    /*
     * Uses the database directly to insert and then uses the ContentProvider to read out the data.
     */
    @Test
    public void testBasicFavoriteMoviesQueries() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // insert our test records into the database
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createTheWallValues();
        long rowId = TestUtilities.insertTheWallValues(appContext);

        // Test the basic content provider query
        Cursor quoteCursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriMovie, null, null, null, null);

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicFavoriteMoviesQueries, quote query", quoteCursor,
                testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Quote Query did not properly set NotificationUri",
                    quoteCursor != null ? quoteCursor.getNotificationUri() : null,
                    FavoritesContract.FavoriteColumns.uriMovie);
        }
    }

    /*
     * Uses the database directly to insert and then uses the ContentProvider to read out the data.
     */
    @Test
    public void testBasicFavoriteTVShowsQueries() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // insert our test records into the database
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createTheSimpsonsValues();
        long rowId = TestUtilities.insertTheSimpsonsValues(appContext);

        // Test the basic content provider query
        Cursor quoteCursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriTVShow, null, null, null, null);

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicFavoriteTVShowsQueries, quote query", quoteCursor,
                testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Quote Query did not properly set NotificationUri",
                    quoteCursor.getNotificationUri(), FavoritesContract.FavoriteColumns.uriTVShow);
        }
    }

    /*
     * Uses the database directly to insert and then uses the ContentProvider to read out the data.
     */
    @Test
    public void testBasicFavoritePersonQueries() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // insert our test records into the database
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMargotRobbieValues();
        long rowId = TestUtilities.insertMargotRobbieValues(appContext);

        // Test the basic content provider query
        Cursor quoteCursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriPerson, null, null, null, null);

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicFavoritePersonQueries, quote query", quoteCursor,
                testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Quote Query did not properly set NotificationUri",
                    quoteCursor.getNotificationUri(), FavoritesContract.FavoriteColumns.uriPerson);
        }
    }

    /*
     * This test uses the provider to insert and then update the data.
     */
    @Test
    public void testUpdateFavoriteMovie() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createTheWallValues();

        Uri quoteUri = appContext.getContentResolver().
                insert(FavoritesContract.FavoriteColumns.uriMovie, values);
        long rowId = ContentUris.parseId(quoteUri);

        // Verify we got a row back.
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "New row id: " + rowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(FavoritesContract.FavoriteColumns._ID, rowId);
        updatedValues.put(FavoritesContract.FavoriteColumns.COLUMN_VOTE_COUNT, 250);

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriMovie, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = appContext.getContentResolver().update(
                FavoritesContract.FavoriteColumns.uriMovie, updatedValues,
                FavoritesContract.FavoriteColumns._ID + "= ?", new String[] { Long.toString(rowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriMovie,
                null,   // projection
                FavoritesContract.FavoriteColumns._ID + " = " + rowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateFavoriteMovie.  Error validating quote entry update.",
                cursor, updatedValues);
        cursor.close();
    }

    /*
     * This test uses the provider to insert and then update the data.
     */
    @Test
    public void testUpdateFavoriteTVShow() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createTheSimpsonsValues();

        Uri quoteUri = appContext.getContentResolver().
                insert(FavoritesContract.FavoriteColumns.uriTVShow, values);
        long rowId = ContentUris.parseId(quoteUri);

        // Verify we got a row back.
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "New row id: " + rowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(FavoritesContract.FavoriteColumns._ID, rowId);
        updatedValues.put(FavoritesContract.FavoriteColumns.COLUMN_VOTE_COUNT, 500);

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriTVShow, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = appContext.getContentResolver().update(
                FavoritesContract.FavoriteColumns.uriTVShow, updatedValues,
                FavoritesContract.FavoriteColumns._ID + "= ?", new String[] { Long.toString(rowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriTVShow,
                null,   // projection
                FavoritesContract.FavoriteColumns._ID + " = " + rowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateFavoriteTVShow.  Error validating quote entry update.",
                cursor, updatedValues);
        cursor.close();
    }

    /*
     * This test uses the provider to insert and then update the data.
     */
    @Test
    public void testUpdateFavoritePerson() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMargotRobbieValues();

        Uri quoteUri = appContext.getContentResolver().
                insert(FavoritesContract.FavoriteColumns.uriPerson, values);
        long rowId = ContentUris.parseId(quoteUri);

        // Verify we got a row back.
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "New row id: " + rowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(FavoritesContract.FavoriteColumns._ID, rowId);
        updatedValues.put(FavoritesContract.FavoriteColumns.COLUMN_KNOWNFOR_POSTER_PATH,
                "/rP36Rx5RQh0rmH2ynEIaG8DxbV2.jpg");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriPerson, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = appContext.getContentResolver().update(
                FavoritesContract.FavoriteColumns.uriPerson, updatedValues,
                FavoritesContract.FavoriteColumns._ID + "= ?", new String[] { Long.toString(rowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = appContext.getContentResolver().query(
                FavoritesContract.FavoriteColumns.uriPerson,
                null,   // projection
                FavoritesContract.FavoriteColumns._ID + " = " + rowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateFavoritePerson.  Error validating quote entry update.",
                cursor, updatedValues);
        cursor.close();
    }

    // Make sure we can still delete after adding/updating stuff
    @Test
    public void testInsertReadFavoriteMoviesProvider() {
        ContentValues testValues = TestUtilities.createTheWallValues();
        testInsertReadProvider(FavoritesContract.FavoriteColumns.uriMovie, testValues,
                "testInsertReadProvider - FavoriteMovies");
    }

    // Make sure we can still delete after adding/updating stuff
    @Test
    public void testInsertReadFavoriteTVShowsProvider() {
        ContentValues testValues = TestUtilities.createTheSimpsonsValues();
        testInsertReadProvider(FavoritesContract.FavoriteColumns.uriTVShow, testValues,
                "testInsertReadProvider - FavoriteTVShows");
    }

    // Make sure we can still delete after adding/updating stuff
    @Test
    public void testInsertReadFavoritePersonProvider() {
        ContentValues testValues = TestUtilities.createMargotRobbieValues();
        testInsertReadProvider(FavoritesContract.FavoriteColumns.uriPerson, testValues,
                "testInsertReadProvider - FavoritePerson");
    }

    private void testInsertReadProvider(Uri uri, ContentValues testValues, String action) {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(uri, true, tco);
        Uri locationUri = appContext.getContentResolver().insert(uri, testValues);

        // Did our content observer get called?  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        long quoteRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(quoteRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = appContext.getContentResolver().query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor(action + ". Error validating Quote entry.",
                cursor, testValues);
    }

    // Make sure we can still delete after adding/updating stuff
    @Test
    public void testDeleteRecords() {
        ContentValues testValues = TestUtilities.createTheWallValues();
        testInsertReadProvider(FavoritesContract.FavoriteColumns.uriMovie, testValues,
                "testInsertReadProvider - FavoriteMovies");

        testValues = TestUtilities.createTheSimpsonsValues();
        testInsertReadProvider(FavoritesContract.FavoriteColumns.uriTVShow, testValues,
                "testInsertReadProvider - FavoriteTVShows");

        testValues = TestUtilities.createMargotRobbieValues();
        testInsertReadProvider(FavoritesContract.FavoriteColumns.uriPerson, testValues,
                "testInsertReadProvider - FavoritePerson");

        Context appContext = InstrumentationRegistry.getTargetContext();
        // Register a content observer for our quote delete.
        TestUtilities.TestContentObserver quoteObserver = TestUtilities.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(
                FavoritesContract.FavoriteColumns.uriMovie, true, quoteObserver);

        appContext.getContentResolver().registerContentObserver(
                FavoritesContract.FavoriteColumns.uriTVShow, true, quoteObserver);

        appContext.getContentResolver().registerContentObserver(
                FavoritesContract.FavoriteColumns.uriPerson, true, quoteObserver);

        deleteAllRecordsFromProvider();

        // If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        quoteObserver.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(quoteObserver);
    }
}
