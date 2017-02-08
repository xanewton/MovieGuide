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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test Database tables
 */
@RunWith(AndroidJUnit4.class)
public class TestDB {

    public static final String LOG_TAG = TestDB.class.getSimpleName();

    // Since we want each test to start with a clean slate
    private void deleteTheDatabase() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase(FavoritesDbHelper.DATABASE_NAME);
    }

    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    /**
     * Tests that the database exists and the quotes table has the correct columns.
     */
    @Test
    public void testCreateDb() throws Exception {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL);
        tableNameHashSet.add(FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL);
        tableNameHashSet.add(FavoritesContract.FavoriteColumns.FAVORITE_PERSON_TBL);

        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase(FavoritesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FavoritesDbHelper(appContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain the tables
        assertTrue("Error: Your database was created without the tables", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns._ID);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_MOVIE_ID);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_TITLE);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_PLOT);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_POSTER_PATH);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_YEAR);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_DURATION);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_VOTE_AVERAGE);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_VOTE_COUNT);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_BACKGROUND_PATH);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_ORIGINAL_LANGUAGE);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_STATUS);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_IMDB_ID);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_BUDGET);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_REVENUE);
        locationColumnHashSet.add(FavoritesContract.FavoriteColumns.COLUMN_HOMEPAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required columns
        assertTrue("Error: The database doesn't contain all of the required columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /**
     * Tests that we can insert and query the database.
     */
    @Test
    public void testFavoriteMoviesTable() {
        // Step 1: Get reference to writable database
        // If there's an error in the SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        Context appContext = InstrumentationRegistry.getTargetContext();
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Step 2: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createTheWallValues();

        // Step 3: Insert ContentValues into database and get a row ID back
        long locationRowId = db.insert(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL, null,
                testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Step 4: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Step 5: Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Step 6: Close Cursor and Database
        cursor.close();
        db.close();
    }

    /**
     * Tests that we can insert and query the database.
     */
    @Test
    public void testFavoriteTVShowsTable() {
        // Step 1: Get reference to writable database
        // If there's an error in the SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        Context appContext = InstrumentationRegistry.getTargetContext();
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Step 2: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createTheSimpsonsValues();

        // Step 3: Insert ContentValues into database and get a row ID back
        long locationRowId = db.insert(FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL, null,
                testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Step 4: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Step 5: Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Step 6: Close Cursor and Database
        cursor.close();
        db.close();
    }

    /**
     * Tests that we can insert and query the database.
     */
    @Test
    public void testFavoritePersonTable() {
        // Step 1: Get reference to writable database
        // If there's an error in the SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        Context appContext = InstrumentationRegistry.getTargetContext();
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Step 2: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createMargotRobbieValues();

        // Step 3: Insert ContentValues into database and get a row ID back
        long locationRowId = db.insert(FavoritesContract.FavoriteColumns.FAVORITE_PERSON_TBL, null,
                testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Step 4: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                FavoritesContract.FavoriteColumns.FAVORITE_PERSON_TBL,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Step 5: Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Step 6: Close Cursor and Database
        cursor.close();
        db.close();
    }
}
