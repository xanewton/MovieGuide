/*
 * Copyright (C) 2017 Angel Newton
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
package com.xengar.android.movieguide.utils;

/**
 * Constants
 */
public final class  Constants {

    public static final String DISCOVER_TYPE = "discover_type";
    public static final String DISCOVER_GENRES = "discover_genres";
    public static final String DISCOVER_SORT_TYPE = "discover_sort_type";
    public static final String DISCOVER_MIN_RATING = "discover_min_rating";
    public static final String DISCOVER_DEFAULT_MIN_RATING = "0";
    public static final String DISCOVER_DEFAULT_SORT_TYPE = "popularity.desc";

    public static final String SHARED_PREF_NAME = "com.xengar.android.movieguide";
    public static final String ITEM_CATEGORY = "item_category";
    public static final String MOVIES = "Movies";
    public static final String TV_SHOWS = "TV Shows";
    public static final String PEOPLE = "People";
    public static final String FAVORITES = "Favorites";
    public static final String HOME = "Home";
    public static final String DISCOVER = "Discover";
    public static final String DISCOVER_RESULT = "Discover Result";

    public static final String UPCOMING_MOVIES = "UpcomingMovies";
    public static final String POPULAR_MOVIES = "PopularMovies";
    public static final String NOW_PLAYING_MOVIES = "NowPlayingMovies";
    public static final String TOP_RATED_MOVIES = "TopRatedMovies";
    public static final String PERSON_ID = "person_id";
    public static final String TV_SHOW_ID = "movie_id";
    public static final String MOVIE_ID = "movie_id";
    public static final String KNOWN_FOR_BACKGROUND_POSTER = "known_for_background_poster";

    public static final String IMDB_URI = "http://www.imdb.com/title";
    public static final String LAST_ACTIVITY = "last_activity";
    public static final String MAIN_ACTIVITY = "main_activity";
    public static final String MOVIE_ACTIVITY = "movie_activity";
    public static final String PERSON_ACTIVITY = "person_activity";
    public static final String TV_SHOW_ACTIVITY = "tv_show_activity";

    public static final String POPULAR_TV_SHOWS = "PopularTVShows";
    public static final String TOP_RATED_TV_SHOWS = "TopRatedTVShows";
    public static final String ON_THE_AIR_TV_SHOWS = "OnTheAirTVShows";

    public static final String TMDB_IMAGE_URL = "https://image.tmdb.org/t/p/";
    public static final String SIZE_W154 = "w154";
    public static final String SIZE_W342 = "w342";
    public static final String SIZE_W185 = "w185";
    public static final String SIZE_W500 = "w500";
    public static final String BACKGROUND_BASE_URI = TMDB_IMAGE_URL + SIZE_W500;

    public static final String PREF_QUERY_LANGUAGE = "pref_query_language";

    // Firebase strings
    public static final String PAGE_MOVIE_DETAILS = "Movie Details";
    public static final String PAGE_TV_SHOW_DETAILS = "TV Show Details";
    public static final String PAGE_PERSON_DETAILS = "Person Details";
    public static final String PAGE_SETTINGS = "Settings";
    public static final String PAGE_MOVIES = "Movies";
    public static final String PAGE_TV_SHOWS = "TV Shows";
    public static final String PAGE_PEOPLE = "People";
    public static final String PAGE_FAVORITES = "Favorites";
    public static final String PAGE_HOME = "Home";
    public static final String PAGE_DISCOVER = "Discover";
    public static final String PAGE_DISCOVER_RESULTS = "Discover Results";
    public static final String PAGE_SEARCH = "Search";
    public static final String TYPE_ADD_FAV = "add to Favorites";
    public static final String TYPE_DEL_FAV = "remove from Favorites";
    public static final String TYPE_PAGE = "page";

    /**
     * Boolean used to log or not lines
     * Usage:
     *      if (LOG) {
     *           if (condition) Log.i(...);
     *      }
     *  When you set LOG to false, the compiler will strip out all code inside such checks
     * (since it is a static final, it knows at compile time that code is not used.)
     * http://stackoverflow.com/questions/2446248/remove-all-debug-logging-calls-before-publishing-are-there-tools-to-do-this
     */
    public static final boolean LOG = false;

}
