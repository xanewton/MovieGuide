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

import java.util.List;

/**
 * Details for a Movie
 */
public class MovieData {

    private final int movieId;
    private final String posterPath;
    private final String title;
    private final String plot;
    private final String year;
    private final Integer duration;
    private final Double voteAverage;
    private final Integer voteCount;
    private final String backgroundPath;
    private final String originalLanguage;
    private final List<String> originalCountries;
    private final List<String> genres;
    private final String status;
    private final String imdbUri;
    private final List<String> productionCompanies;
    private final String budget;
    private final String revenue;
    private final String homepage;

    // Constructor
    public MovieData(String moviePoster, int movieId, String title, String plot, String year,
                     Integer duration, Double voteAverage, Integer voteCount, String backgroundPath,
                     String originalLanguage, List<String> originalCountries, List<String> genres,
                     String status, String imdbUri, List<String> productionCompanies, String budget,
                     String revenue, String homepage) {

        this.movieId = movieId;
        this.posterPath = moviePoster;
        this.title = title;
        this.plot = plot;
        this.year = year;
        this.duration = duration;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.backgroundPath = backgroundPath;
        this.originalLanguage = originalLanguage;
        this.originalCountries = originalCountries;
        this.genres = genres;
        this.status = status;
        this.imdbUri = imdbUri;
        this.productionCompanies = productionCompanies;
        this.budget = budget;
        this.revenue = revenue;
        this.homepage = homepage;
    }

    // Getter methods
    public String getPlot() {
        return plot;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getYear() {
        return year;
    }

    public Integer getDuration() {
        return duration;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public List<String> getOriginalCountries() {
        return originalCountries;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getStatus() {
        return status;
    }

    public String getImdbUri() {
        return imdbUri;
    }

    public List<String> getProductionCompanies() {
        return productionCompanies;
    }

    public String getBudget() {
        return budget;
    }

    public String getRevenue() {
        return revenue;
    }

    public String getHomepage() {
        return homepage;
    }

}
