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
package com.xengar.android.movieguide.service;

import com.xengar.android.movieguide.data.TV;
import com.xengar.android.movieguide.data.TVResults;
import com.xengar.android.movieguide.data.VideoResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * TVService
 */
public interface TVService {

    @GET("tv/{tv_id}")
    Call<TV> tvDetails(
            @Path("tv_id") String id,
            @Query("api_key") String api_key,
            @Query("language") String lang
    );

    @GET("tv/{tv_id}/videos")
    Call<VideoResults> tvVideos(
            @Path("tv_id") String id,
            @Query("api_key") String api_key,
            @Query("language") String lang
    );

    @GET("tv/airing_today")
    Call<TVResults> airingToday(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page
    );

    @GET("tv/on_the_air")
    Call<TVResults> onTheAir(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page
    );

    @GET("tv/popular")
    Call<TVResults> popular(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page
    );

    @GET("tv/top_rated")
    Call<TVResults> topRated(
            @Query("api_key") String api_key,
            @Query("language") String lang,
            @Query("page") int page
    );
}
