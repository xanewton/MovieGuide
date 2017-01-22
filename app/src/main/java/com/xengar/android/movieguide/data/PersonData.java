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

    private int personId;
    private int popularity;
    private String actorName;
    private String profilePath;
    private String placeOfBirth;
    private String birthday;
    private String deathday;
    private String biography;
    private String imdbId;
    private String homepage;
    private List<MovieCreditCast> movieCreditCastList;
    private List<MovieCreditCrew> movieCreditCrewList;

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
