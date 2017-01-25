package com.dannyxmichaud.portfolio.popularmovies.tmdbapi;

@SuppressWarnings({"WeakerAccess", "unused"})

/**
 * Immutable object returned by MovieAPI class that contains details about a review.
 */
public final class ReviewItem
{
    private final String author;
    private final String content;
    private final String id;

    public ReviewItem(String id, String author, String content)
    {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getAuthor()
    {
        return this.author;
    }

    public String getContent()
    {
        return this.content;
    }

    public String getId()
    {
        return this.id;
    }
}
