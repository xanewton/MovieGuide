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
 * Components of a Detail Data View for a Movie
 */
public class MovieDetails {
    private final MovieData detailsData;
    private final List<TrailerData> trailersData;
    private final List<ReviewData> reviewsData;
    private final List<CastData> castData;

    // Constructor
    public MovieDetails(MovieData detailsData, List<TrailerData> trailersData,
                        List<ReviewData> reviewsData, List<CastData> castData) {
        this.detailsData = detailsData;
        this.trailersData = trailersData;
        this.reviewsData = reviewsData;
        this.castData = castData;
    }

    // Getters
    public MovieData getDetailsData() {
        return detailsData;
    }

    public List<TrailerData> getTrailersData() {
        return trailersData;
    }

    public List<ReviewData> getReviewsData() {
        return reviewsData;
    }

    public List<CastData> getCastData() {
        return castData;
    }
}
