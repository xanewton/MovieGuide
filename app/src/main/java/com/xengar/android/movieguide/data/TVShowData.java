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
package com.xengar.android.movieguide.data;

import java.util.List;

/**
 * TVShowData
 */
public class TVShowData {
    private final int tvShowId;
    private final String posterPath;
    private final String name;
    private final String overview;
    private final Double voteAverage;
    private final Integer voteCount;
    private final String backgroundPath;
    private final String originalLanguage;
    private final String originalCountries;
    private final List<String> genres;
    private final String status;
    private final List<String> productionCompanies;
    private final String homepage;
    private final String firstAirDate;
    private final String lastAirDate;
    private final int numEpisodes;
    private final int numSeasons;

    // Constructor
    public TVShowData(int tvShowId, String name, String overview, String posterPath,
                      String backgroundPath, Double voteAverage, Integer voteCount,
                      String originalLanguage, String originalCountries, List<String> genres,
                      String status, List<String> productionCompanies, String homepage,
                      String firstAirDate, String lastAirDate, int numEpisodes, int numSeasons) {
        this.tvShowId = tvShowId;
        this.posterPath = posterPath;
        this.name = name;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.backgroundPath = backgroundPath;
        this.originalLanguage = originalLanguage;
        this.originalCountries = originalCountries;
        this.genres = genres;
        this.status = status;
        this.productionCompanies = productionCompanies;
        this.homepage = homepage;
        this.firstAirDate =firstAirDate;
        this.lastAirDate = lastAirDate;
        this.numEpisodes = numEpisodes;
        this.numSeasons = numSeasons;
    }

    // Getters
    public int getTVShowId() {
        return tvShowId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalCountries()  {
        return originalCountries;
    }

    public List<String> getGenres()  {
        return genres;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getProductionCompanies()  {
        return productionCompanies;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public int getNumEpisodes() {
        return numEpisodes;
    }

    public int getNumSeasons() {
        return numSeasons;
    }
}
