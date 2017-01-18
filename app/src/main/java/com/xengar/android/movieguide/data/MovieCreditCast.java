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

import org.apache.commons.lang3.StringUtils;

/**
 * MovieCreditCast
 */
public class MovieCreditCast {

    private final String character;
    private final String movieTitle;
    private final String posterPath;
    private final String releaseDate;
    private final int id;
    private final int releaseYear;

    // Constructor
    public MovieCreditCast(int id, String character, String movieTitle, String posterPath,
                    String releaseDate) {
        this.id = id;
        this.character = character;
        this.movieTitle = movieTitle;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.releaseYear = (releaseDate != null && !releaseDate.equals("null"))?
                Integer.parseInt(StringUtils.substringBefore(releaseDate, "-")) : 0;
    }

    // Getters
    public int getId() { return id; }
    public String getCharacter() { return character; }
    public String getMovieTitle() { return movieTitle; }
    public String getPosterPath() { return posterPath; }
    public String getReleaseDate() { return releaseDate; }
    public int getReleaseYear() { return releaseYear; }
}
