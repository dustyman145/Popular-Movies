package com.dannyxmichaud.portfolio.popularmovies.tmdbapi;

@SuppressWarnings({"WeakerAccess", "unused"})

/**
 * Immutable object returned by MovieAPI class that contains the basic information about a movie.
 * Usually used by MovieList object.
 */
public final class MovieItem
{
    private final int id;
    private final String originalLanguage;
    private final String originalTitle;
    private final String overview;
    private final String posterUrl;
    private final String title;
    private final double voteAverage;

    MovieItem(int id, String originalLanguage, String originalTitle, String overview, String posterUrl, String title, double voteAverage)
    {
        this.id = id;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.title = title;
        this.voteAverage = voteAverage;
    }

    public int getMovieID()
    {
        return this.id;
    }

    public String getOriginalLanguage()
    {
        return this.originalLanguage;
    }

    public String getOriginalTitle()
    {
        return this.originalTitle;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getPosterUrl()
    {
        return this.posterUrl;
    }

    public String getTitle()
    {
        return this.title;
    }

    public double getVoteAverage() {
        return this.voteAverage;
    }
}
