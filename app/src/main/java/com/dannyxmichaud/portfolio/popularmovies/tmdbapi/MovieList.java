package com.dannyxmichaud.portfolio.popularmovies.tmdbapi;

@SuppressWarnings({"WeakerAccess", "unused"})

/**
 * Immutable object returned by MovieAPI class that contains a list of MovieDetails object.
 */
public final class MovieList
{
    private final int currentPage;
    private final MovieItem[] movieList;
    private final int totalMovies;
    private final int totalPages;

    public MovieList(int currentPage, int totalPages, int totalMovies, MovieItem[] movieList)
    {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalMovies = totalMovies;
        this.movieList = movieList;
    }

    public MovieItem[] getAllMovies()
    {
        return this.movieList;
    }

    public int getCurrentPage()
    {
        return this.currentPage;
    }

    public int getTotalMovies()
    {
        return this.totalMovies;
    }

    public int getTotalPages()
    {
        return this.totalPages;
    }
}
