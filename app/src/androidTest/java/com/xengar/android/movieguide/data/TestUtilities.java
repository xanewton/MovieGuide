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
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.xengar.android.movieguide.utils.PollingCheck;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/*
    These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your Contract class to exactly match the one
    in our solution to use these as-given.
 */
@RunWith(AndroidJUnit4.class)
public class TestUtilities {

    // Sample id for The Wall movie
    static final int TEST_MOVIE_ID = 311324;
    // Sample id for The Simpsons tv show
    static final int TEXT_TV_SHOW_ID = 456;
    // Sample id for Margot Robbie
    static final int TEXT_PERSON_ID = 234352;


    @Test
    public void testAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Assert.assertEquals("com.xengar.android.movieguide", appContext.getPackageName());
    }


    // Sample ContentValues
    static ContentValues createTheWallValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_TITLE, "The Great Wall");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_PLOT,
                "The story of an elite force making a last stand for humanity on the world’s most iconic structure.");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_POSTER_PATH, "/hm0Z5tpRlSzPO97U5e2Q32Y0Xrb.jpg");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_YEAR, 2016);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_DURATION, 104);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_VOTE_AVERAGE, 6.3);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_VOTE_COUNT, 93);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_BACKGROUND_PATH, "/yESCAoZkaxZ2AMiHojl9jYYd9zD.jpg");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_ORIGINAL_LANGUAGE, "en");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_STATUS, "Post Production");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_IMDB_ID, "tt2034800");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_BUDGET, 135000000);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_REVENUE, 0);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_HOMEPAGE, "");

        return testValues;
    }

    // Sample ContentValues
    static ContentValues createTheSimpsonsValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_TV_SHOW_ID, TEXT_TV_SHOW_ID);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_NAME, "The Simpsons");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_OVERVIEW,
                "Set in Springfield, the average American town, the show focuses on the antics"
                + " and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and"
                + " Maggie, as well as a virtual cast of thousands. Since the beginning, the series"
                + " has been a pop culture icon, attracting hundreds of celebrities to guest star. "
                + "The show has also made name for itself in its fearless satirical take on politics,"
                + " media and American life in general.");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_POSTER_PATH, "/yTZQkSsxUFJZJe67IenRM0AEklc.jpg");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_BACKGROUND_PATH, "/f5uNbUC76oowt5mt5J9QlqrIYQ6.jpg");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_VOTE_AVERAGE, 6.9);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_VOTE_COUNT, 451);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_ORIGINAL_LANGUAGE, "en");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_ORIGINAL_COUNTRIES, "US");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_GENRES, "Animation, Comedy, Family");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_STATUS, "Returning Series");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_PROD_COMPANIES, "Gracie Films, 20th Century Fox Television, The Curiosity Company");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_HOMEPAGE, "http://www.thesimpsons.com/");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_FIRST_AIR_DATE, "1989-12-16");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_LAST_AIR_DATE, "2017-01-15");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_NUM_SEASONS, 29);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_NUM_EPISODES, 609);

        return testValues;
    }

    // Sample ContentValues
    static ContentValues createMargotRobbieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_PERSON_ID, TEXT_PERSON_ID);
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_NAME, "Margot Robbie");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_PROFILE_PATH,
                "/wHMKWqIkvJYFntCPHgQlcWYKav9.jpg");
        testValues.put(FavoritesContract.FavoriteColumns.COLUMN_KNOWNFOR_POSTER_PATH,
                "/e1mjopzAS2KNsvpbpahQ1a6SkSn.jpg");

        return testValues;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    // Compares valueCursor with expectedValues.
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long insertTheWallValues(Context context) {
        // insert our test records into the database
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTheWallValues();

        long locationRowId = db.insert(FavoritesContract.FavoriteColumns.FAVORITE_MOVIES_TBL, null,
                testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert The Wall values", locationRowId != -1);
        return locationRowId;
    }

    static long insertTheSimpsonsValues(Context context) {
        // insert our test records into the database
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTheSimpsonsValues();

        long locationRowId = db.insert(FavoritesContract.FavoriteColumns.FAVORITE_TV_SHOWS_TBL, null,
                testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert The Simpsons values", locationRowId != -1);
        return locationRowId;
    }

    static long insertMargotRobbieValues(Context context) {
        // insert our test records into the database
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMargotRobbieValues();

        long locationRowId = db.insert(FavoritesContract.FavoriteColumns.FAVORITE_PERSON_TBL, null,
                testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert Margot Robbie values", locationRowId != -1);
        return locationRowId;
    }



    /*
     * The functions inside of TestProvider use this utility class to test the ContentObserver
     * callbacks using the PollingCheck class that we grabbed from the Android CTS tests.
     * Note that this only tests that the onChange function is called; it does not test that the
     * correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {

        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}
