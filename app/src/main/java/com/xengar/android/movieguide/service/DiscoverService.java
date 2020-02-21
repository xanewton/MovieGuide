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
package com.xengar.android.movieguide.service;

import com.xengar.android.movieguide.model.MovieResults;
import com.xengar.android.movieguide.model.TVResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * DiscoverService
 */
public interface DiscoverService {

    @GET("discover/movie")
    Call<MovieResults> inTheaters(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("sort_by") String sortBy,
            @Query("primary_release_date.lte") String date_lte,
            @Query("primary_release_date.gte") String date_gte
    );

    @GET("discover/tv")
    Call<TVResults> onTv(
            @Query("air_date.lte") String air_date_lte,
            @Query("air_date.gte") String air_date_gte,
            @Query("sort_by") String sortBy,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("api_key") String api_key
    );

    @GET("discover/movie")
    Call<MovieResults> discoverMovie(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("sort_by") String sortBy,
            @Query("vote_average.gte") String vote_average_gte,
            @Query("with_genres") String genres
    );

    @GET("discover/tv")
    Call<TVResults> discoverTv(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("sort_by") String sortBy,
            @Query("vote_average.gte") String vote_average_gte,
            @Query("with_genres") String genres
    );
}
