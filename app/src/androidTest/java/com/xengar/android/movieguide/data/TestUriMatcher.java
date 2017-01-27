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

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Test your UriMatcher.  Note that this class utilizes constants that are declared with package
 * protection inside of the UriMatcher, which is why the test must be in the same data package
 * as the Android app code.  Doing the test this way is a compromise between data hiding and testability.
 */
@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {

    private static final String TEST_MOVIE_ID = "311324";
    private static final String TEST_TV_SHOW_ID = "456";

    // content://com.xengar.android.movieguide/movie"
    private static final Uri TEST_MOVIES_DIR = FavoritesContract.FavoriteColumns.uriMovie;
    private static final Uri TEST_MOVIE_ID_DIR = FavoritesContract.FavoriteColumns.makeUriForMovie(TEST_MOVIE_ID);
    // content://com.xengar.android.movieguide/tvshow"
    private static final Uri TEST_TV_SHOW_DIR = FavoritesContract.FavoriteColumns.uriTVShow;
    private static final Uri TEST_TV_SHOW_ID_DIR = FavoritesContract.FavoriteColumns.makeUriForTVShow(TEST_TV_SHOW_ID);


    /**
     * Tests that UriMatcher returns the correct integer value for each of the Uri types that
     *  our ContentProvider can handle.
     */
    @Test
    public void testUriMatcher() {
        UriMatcher testMatcher = FavoritesProvider.buildUriMatcher();

        assertEquals("Error: The movie was matched incorrectly.",
                testMatcher.match(TEST_MOVIES_DIR), FavoritesProvider.MOVIES);
        assertEquals("Error: The movie was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_ID_DIR), FavoritesProvider.MOVIE_ID);
        assertEquals("Error: The TV Show was matched incorrectly.",
                testMatcher.match(TEST_TV_SHOW_DIR), FavoritesProvider.TV_SHOWS);
        assertEquals("Error: The TV Show was matched incorrectly.",
                testMatcher.match(TEST_TV_SHOW_ID_DIR), FavoritesProvider.TV_SHOW_ID);
    }

    /**
     * Tests that the movie can be retrieved from the uri.
     */
    @Test
    public void testMovieFromUri(){
        String movie = FavoritesContract.FavoriteColumns.getIdFromUri(TEST_MOVIE_ID_DIR);
        assertEquals("Error: The movie was matched incorrectly.", movie, TEST_MOVIE_ID);

        String tvshow = FavoritesContract.FavoriteColumns.getIdFromUri(TEST_TV_SHOW_ID_DIR);
        assertEquals("Error: The TV Show was matched incorrectly.", tvshow, TEST_TV_SHOW_ID);
    }

}
