package com.dannyxmichaud.portfolio.popularmovies.tmdbapi;

public class ResponseStatusException extends Exception
{
    private int statusCode;
    private String statusMessage;


    public ResponseStatusException(int statusCode, String statusMessage)
    {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getLocalizedMessage()
    {
        return getMessage();
    }

    public String getMessage()
    {
        return this.statusMessage;
    }

    public int getStatusCode()
    {
        return this.statusCode;
    }
}
