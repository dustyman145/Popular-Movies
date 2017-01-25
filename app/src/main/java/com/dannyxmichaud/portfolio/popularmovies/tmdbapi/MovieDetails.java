package com.dannyxmichaud.portfolio.popularmovies.tmdbapi;

import org.joda.time.LocalDate;

@SuppressWarnings({"WeakerAccess", "unused"})

/**
 * Immutable object returned by MovieAPI class that contains all details about a specific movie.
 */
public final class MovieDetails
{
    private final boolean adult;
    private final int budget;
    private final String[] genres;
    private final int id;
    private final String imdbId;
    private final String originalLanguage;
    private final String originalTitle;
    private final String overview;
    private final double popularity;
    private final String posterUrl;
    private final String backdropUrl;
    private final LocalDate releaseDate;
    private final String status;
    private final String tagline;
    private final String title;
    private final double voteAverage;
    private final int voteCount;
    private final int runtime;

    public MovieDetails(boolean adult, int budget, String[] genres, int id, String imdbId,
                        String originalLanguage, String originalTitle, String overview,
                        double popularity, String posterUrl, String backdropUrl, LocalDate releaseDate,
                        String tagline, String title, double voteAverage,
                        int voteCount, String status, int runtime)
    {
        this.adult = adult;
        this.budget = budget;
        this.genres = genres;
        this.id = id;
        this.imdbId = imdbId;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.popularity = popularity;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.releaseDate = releaseDate;
        this.tagline = tagline;
        this.title = title;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.status = status;
        this.runtime = runtime;
    }

    public int getBudget()
    {
        return this.budget;
    }

    public String[] getGenres()
    {
        return this.genres;
    }

    public int getId()
    {
        return this.id;
    }

    public String getImdbId()
    {
        return this.imdbId;
    }

    public String getOriginalLanguage()
    {
        return this.originalLanguage;
    }

    public String getOriginalTitle()
    {
        return this.originalTitle;
    }

    public String getOverview()
    {
        return this.overview;
    }

    public double getPopularity()
    {
        return this.popularity;
    }

    public String getPosterUrl()
    {
        return this.posterUrl;
    }

    public String getBackdropUrl()
    {
        return this.backdropUrl;
    }

    public LocalDate getReleaseDate()
    {
        return this.releaseDate;
    }

    public String getStatus()
    {
        return this.status;
    }

    public String getTagline()
    {
        return this.tagline;
    }

    public String getTitle()
    {
        return this.title;
    }

    public double getVoteAverage()
    {
        return this.voteAverage;
    }

    public int getVoteCount()
    {
        return this.voteCount;
    }

    public boolean isAdult()
    {
        return this.adult;
    }

    public int getRuntime() {
        return this.runtime;
    }
}
