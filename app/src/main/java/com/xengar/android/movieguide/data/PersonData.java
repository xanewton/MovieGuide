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
 * PersonData
 */
public class PersonData {

    private final int personId;
    private final int popularity;
    private final String actorName;
    private final String profilePath;
    private final String placeOfBirth;
    private final String birthday;
    private final String deathday;
    private final String biography;
    private final String imdbId;
    private final String homepage;
    private final List<MovieCreditCast> movieCreditCastList;
    private final List<MovieCreditCrew> movieCreditCrewList;

    // Constructor
    public PersonData(String actorName, String profilePath, String placeOfBirth,
                      String birthday, String deathday,
                      String biography, int personId, int popularity, String imdbId,
                      String homepage, List<MovieCreditCast> movieCreditCastList,
                      List<MovieCreditCrew> movieCreditCrewList) {
        this.personId = personId;
        this.popularity = popularity;
        this.actorName = actorName;
        this.profilePath = profilePath;
        this.biography = biography;
        this.placeOfBirth = placeOfBirth;
        this.birthday = birthday;
        this.deathday = deathday;
        this.imdbId = imdbId;
        this.homepage = homepage;
        this.movieCreditCastList = movieCreditCastList;
        this.movieCreditCrewList = movieCreditCrewList;
    }

    // Getters
    public String getActorName() {
        return actorName;
    }

    public String getProfileImagePath() {
        return profilePath;
    }

    public String getBiography() {
        return biography;
    }

    public int getPersonId()
    {
        return personId;
    }

    public String getPlaceOfBirth()
    {
        return placeOfBirth;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public String getDeathday()
    {
        return deathday;
    }

    public int getPopularity()
    {
        return popularity;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getHomepage() {
        return homepage;
    }

    public List<MovieCreditCast> getMovieCreditCastList() {
        return movieCreditCastList;
    }

    public List<MovieCreditCrew> getMovieCreditCrewList() {
        return movieCreditCrewList;
    }
}
