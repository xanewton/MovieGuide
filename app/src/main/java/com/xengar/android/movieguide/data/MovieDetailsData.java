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
public class MovieDetailsData extends MovieData {

    public final String plot;
    public final String year;
    public final Integer duration;
    public final Double voteAverage;
    public final Integer voteCount;
    public final String backgroundPath;
    public final String originalLanguage;
    public final List<String> originalCountries;
    public final List<String> genres;
    public final String status;
    public final String imdbUri;
    public final List<String> productionCompanies;
    public final String budget;
    public final String revenue;
    public final String homepage;

    // Constructor
    public MovieDetailsData(String moviePoster, int movieId, String title, String plot, String year,
                            Integer duration, Double voteAverage, Integer voteCount,
                            String backgroundPath, String originalLanguage,
                            List<String> originalCountries, List<String> genres, String status,
                            String imdbUri, List<String> productionCompanies, String budget,
                            String revenue, String homepage) {
        super(moviePoster, movieId, title);
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
