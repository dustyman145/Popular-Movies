package com.dannyxmichaud.portfolio.popularmovies.tmdbapi;

@SuppressWarnings({"WeakerAccess", "unused"})

/**
 * Immutable object returned by MovieAPI class that contains a list of ReviewItem
 * about a specific movie.
 */
public class ReviewList
{
    private final int currentPage;
    private final int movieId;
    private final ReviewItem[] reviewList;
    private final int totalPages;
    private final int totalResults;

    public ReviewList(int movieId, int currentPage, ReviewItem[] reviewList, int totalPages, int totalResults)
    {
        this.movieId = movieId;
        this.currentPage = currentPage;
        this.reviewList = reviewList;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public int getCurrentPage()
    {
        return this.currentPage;
    }

    public int getMovieId()
    {
        return this.movieId;
    }

    public ReviewItem[] getReviewList()
    {
        return this.reviewList;
    }

    public int getTotalPages()
    {
        return this.totalPages;
    }

    public int getTotalResults()
    {
        return this.totalResults;
    }
}
