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

/**
 * Components of a movie item
 */
public class MovieData {

    private final String title;
    private final int id;
    private final String poster;

    // Constructor
    public MovieData(String poster, int id, String title) {
        this.title = title;
        this.id = id;
        this.poster = poster;
    }

    // Getters
    public String getMovieTitle() {
        return title;
    }

    public int getMovieId() {
        return id;
    }

    public String getMoviePoster() {
        return poster;
    }
}
