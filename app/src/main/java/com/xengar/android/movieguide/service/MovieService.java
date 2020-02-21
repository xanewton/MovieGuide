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

import com.xengar.android.movieguide.model.Movie;
import com.xengar.android.movieguide.model.MovieResults;
import com.xengar.android.movieguide.model.VideoResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * MovieService
 */
public interface MovieService {

    @GET("movie/{movie_id}")
    Call<Movie> movieDetails(
            @Path("movie_id") String id,
            @Query("api_key") String api_key,
            @Query("language") String lang
    );

    @GET("movie/{movie_id}/videos")
    Call<VideoResults> movieVideos(
            @Path("movie_id") String id,
            @Query("api_key") String api_key,
            @Query("language") String lang
    );

    @GET("movie/now_playing")
    Call<MovieResults> nowPlaying(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page
    );

    @GET("movie/popular")
    Call<MovieResults> popular(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page
    );

    @GET("movie/top_rated")
    Call<MovieResults> topRated(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page
    );

    @GET("movie/upcoming")
    Call<MovieResults> upcoming(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page

    );
}
