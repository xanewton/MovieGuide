package com.xengar.android.movieguide.data;

/**
 * Components of a Detail Data View for a Movie
 */
public class MovieDetails {
    private final MovieDetailsData detailsData;

    // Constructor
    public MovieDetails(MovieDetailsData detailsData) {
        this.detailsData = detailsData;
    }

    // Getters
    public MovieDetailsData getDetailsData() {
        return detailsData;
    }
}
