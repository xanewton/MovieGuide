package com.xengar.android.movieguide.data;

/**
 * Components of a movie item
 */
public class MovieData {

    private final String title;
    private final int id;
    private final String poster;

    // Constructor
    public MovieData(String poster, int id, String title) {
        this.title = title;
        this.id = id;
        this.poster = poster;
    }

    // Getters
    public String getMovieTitle() {
        return title;
    }

    public int getMovieId() {
        return id;
    }

    public String getMoviePoster() {
        return poster;
    }
}
