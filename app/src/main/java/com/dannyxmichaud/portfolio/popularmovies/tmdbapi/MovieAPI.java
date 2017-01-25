package com.dannyxmichaud.portfolio.popularmovies.tmdbapi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

@SuppressWarnings({"WeakerAccess", "unused"})

/**
 * A class wrapper to simplify the calls to the TMDB API.
 */
public class MovieAPI {
    private static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String PATH_MOVIE = "movie";
    private static final String PATH_SEARCH = "search";
    private static final String PATH_POPULAR = "popular";
    private static final String PATH_REVIEWS = "reviews";
    private static final String PATH_TOP_RATED = "top_rated";
    private static final String PATH_NOW_PLAYING = "now_playing";
    private static final String PATH_VIDEOS = "videos";
    private static final String QUERY_API_KEY = "api_key";
    private static final String QUERY_APPEND_TO_RESPONSE = "append_to_response";
    private static final String QUERY_LANGUAGE = "language";
    private static final String QUERY_KEYWORDS = "query";
    private static final String QUERY_PAGE = "page";

    // TMDB api key.
    private final String mApiKey;
    // Current context.
    private Context mContext;

    /**
     * Public constructor.
     *
     * @param paramContext The application context.
     */
    public MovieAPI(@NonNull Context paramContext) {
        mContext = paramContext;
        mApiKey = getApiKey();
    }

    /**
     * Deserialize a movie details JSONObject returned by TMDB API.
     *
     * @param localJSONObject The result JSONObject returned by TMDB API.
     * @return MovieDetails object.
     * @throws JSONException
     */
    private MovieDetails deserializeMovieDetails(@NonNull JSONObject localJSONObject) throws JSONException {
        // Deserialize the movie genres in an String Array.
        JSONArray genresJSONArray = localJSONObject.getJSONArray("genres");
        String[] genres = new String[genresJSONArray.length()];
        for (int i = 0; i < genresJSONArray.length(); i++) {
            genres[i] = genresJSONArray.getJSONObject(i).getString("name");
        }

        // Deserialize the movie details.
        boolean isAdult = localJSONObject.getBoolean("adult");
        int budget = localJSONObject.getInt("budget");
        int id = localJSONObject.getInt("id");
        String imdbId = localJSONObject.getString("imdb_id");
        String originalLanguage = localJSONObject.getString("original_language");
        String originalTitle = localJSONObject.getString("original_title");
        String overview = localJSONObject.getString("overview");
        double popularity = localJSONObject.getDouble("popularity");
        String tagline = localJSONObject.getString("tagline");
        String title = localJSONObject.getString("title");
        double voteAverage = localJSONObject.getDouble("vote_average");
        int voteCount = localJSONObject.getInt("vote_count");
        String status = localJSONObject.getString("status");
        int runtime = localJSONObject.getInt("runtime");
        LocalDate localLocalDate = LocalDate.parse(localJSONObject.getString("release_date"), DateTimeFormat.forPattern("yyyy-MM-dd"));

        // Get the poster Url.
        String posterUrl = "";
        if (!localJSONObject.isNull("poster_path")) {
            // Build the poster Url.
            posterUrl = Uri.parse(IMAGES_BASE_URL).buildUpon()
                    .appendPath(getPosterResolution())
                    .appendPath(localJSONObject.getString("poster_path").replace("/", "")).build().toString();
        }

        // Get the backdrop Url.
        String backdropUrl = "";
        if (!localJSONObject.isNull("backdrop_path")) {
            // Build backdrop Url.
            backdropUrl = Uri.parse(IMAGES_BASE_URL).buildUpon()
                    .appendPath(getBackdropResolution())
                    .appendPath(localJSONObject.getString("backdrop_path").replace("/", "")).build().toString();
        }

        // Return the MovieDetails object.
        return new MovieDetails(isAdult, budget, genres, id, imdbId, originalLanguage,
                originalTitle, overview, popularity, posterUrl, backdropUrl, localLocalDate,
                tagline, title, voteAverage, voteCount, status, runtime);
    }

    /**
     * Deserialize a movie list JSONObject returned by TMDB API.
     *
     * @param localJSONObject The JSONObject returned by TMDB api.
     * @return MovieList object.
     * @throws JSONException
     */
    private MovieList deserializeMovieList(@NonNull JSONObject localJSONObject) throws JSONException {
        JSONArray resultsJSONArray = localJSONObject.getJSONArray("results");

        // Deserialize the movie list result.
        MovieItem[] arrayOfMovieItem = new MovieItem[resultsJSONArray.length()];
        for (int i =0; i < resultsJSONArray.length(); i++) {
            // Get the movie poster url.
            String posterUrl = "";
            if (!resultsJSONArray.getJSONObject(i).isNull("poster_path")) {
                // Create the movie poster url.
                posterUrl = Uri.parse(IMAGES_BASE_URL).buildUpon()
                        .appendPath(getPosterResolution())
                        .appendPath(resultsJSONArray.getJSONObject(i).getString("poster_path").replace("/", ""))
                        .build().toString();
            }

            // Populate the MovieItem array.
            arrayOfMovieItem[i] = new MovieItem(resultsJSONArray.getJSONObject(i).getInt("id"), resultsJSONArray.getJSONObject(i).getString("original_language"), resultsJSONArray.getJSONObject(i).getString("original_title"), resultsJSONArray.getJSONObject(i).getString("overview"), posterUrl, resultsJSONArray.getJSONObject(i).getString("title"), resultsJSONArray.getJSONObject(i).getDouble("vote_average"));
        }

        // Return a MovieList object.
        return new MovieList(localJSONObject.getInt("page"), localJSONObject.getInt("total_pages"), localJSONObject.getInt("total_results"), arrayOfMovieItem);
    }

    /**
     * Deserialize a movie review list JSONObject returned by TMDB API.
     *
     * @param localJSONObject The JSONObject returned by TMDB api.
     * @return ReviewList object.
     * @throws JSONException
     */
    private ReviewList deserializeMovieReviews(@NonNull JSONObject localJSONObject) throws JSONException {
        JSONArray resultsJSONArray = localJSONObject.getJSONArray("results");

        // Deserialize the reviews result.
        ReviewItem[] arrayOfReviewItem = new ReviewItem[resultsJSONArray.length()];
        for (int i =0; i < resultsJSONArray.length(); i++) {
            arrayOfReviewItem[i] = new ReviewItem(resultsJSONArray.getJSONObject(i).getString("id"), resultsJSONArray.getJSONObject(i).getString("author"), resultsJSONArray.getJSONObject(i).getString("content"));
        }

        // Return a ReviewList object.
        return new ReviewList(localJSONObject.getInt("id"), localJSONObject.getInt("page"), arrayOfReviewItem, localJSONObject.getInt("total_pages"), localJSONObject.getInt("total_results"));
    }

    /**
     * Get the device local code.
     *
     * @return The language code formatted in local code.
     */
    private static String getLocaleCode() {
        // The Android API version 24 and above introduced a new way to fetch the device's
        // default language. If the version is below 24 we simply get the default language
        // of the running java instance.
        if (Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList = LocaleList.getDefault();
            if (!localeList.isEmpty()) {
                return localeList.get(0).getLanguage() + "_" + localeList.get(0).getCountry();
            }
        }
        return Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
    }

    /**
     * Return the optimized poster resolution for the current device.
     *
     * @return The width poster resolution.
     */
    private String getPosterResolution() {
        return "w342";
    }

    /**
     * Return the optimized backdrop resolution for the current device.
     *
     * @return The width backdrop resolution.
     */
    private String getBackdropResolution() {
        return "w780";
    }

    /**
     * Fetch the movie details from the TMDB API.
     *
     * @param movieId The movie ID to query.
     * @return MovieList object.
     * @throws ResponseStatusException
     */
    public MovieDetails fetchMovieDetails(int movieId) throws ResponseStatusException {
        // Build the request Url.
        Uri requestUrl = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(QUERY_API_KEY, mApiKey)
                .appendQueryParameter(QUERY_LANGUAGE, getLocaleCode())
                .appendQueryParameter(QUERY_APPEND_TO_RESPONSE, PATH_VIDEOS).build();

        // Build the HttpClient request.
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url((requestUrl).toString()).build();
        try {
            // Execute the request and parse the JSONObject.
            Response response = httpClient.newCall(request).execute();
            JSONObject responseJSONObject = new JSONObject(response.body().string());

            // Throw an exception if the API return an error.
            if ((responseJSONObject.has("status_code")) && (responseJSONObject.has("status_message"))) {
                throw new ResponseStatusException(responseJSONObject.getInt("status_code"), responseJSONObject.getString("status_message"));
            }

            // Deserialize the JSONObject.
            return deserializeMovieDetails(responseJSONObject);
        } catch (JSONException|IOException e) {
            // Throw an exception if the request fails.
            throw new ResponseStatusException(504, "Your request to the backend server timed out. Try again.");
        }
    }

    /**
     * Fetch the movie reviews from the TMDB API.
     *
     * @param movieId The movie ID to query.
     * @param pageId The page number to query.
     * @return MovieList object.
     * @throws ResponseStatusException
     */
    public ReviewList fetchMovieReviews(int movieId, int pageId) throws ResponseStatusException {
        // Build the request Url.
        Uri requestUrl = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(String.valueOf(movieId))
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(QUERY_API_KEY, mApiKey)
                .appendQueryParameter(QUERY_PAGE, String.valueOf(pageId))
                .appendQueryParameter(QUERY_LANGUAGE, getLocaleCode()).build();

        // Build the HttpClient request.
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url((requestUrl).toString()).build();
        try {
            // Execute the request and parse the JSONObject.
            Response response = httpClient.newCall(request).execute();
            JSONObject responseJSONObject = new JSONObject(response.body().string());

            // Throw an exception if the API return an error.
            if ((responseJSONObject.has("status_code")) && (responseJSONObject.has("status_message"))) {
                throw new ResponseStatusException(responseJSONObject.getInt("status_code"), responseJSONObject.getString("status_message"));
            }

            // Deserialize the JSONObject.
            return deserializeMovieReviews(responseJSONObject);
        } catch (JSONException|IOException e) {
            // Throw an exception if the request fails.
            throw new ResponseStatusException(504, "Your request to the backend server timed out. Try again.");
        }
    }

    /**
     * Fetch the popular movies from TMDB API.
     *
     * @param pageId The page number to query.
     * @return MovieList object.
     * @throws ResponseStatusException
     */
    public MovieList fetchPopularMovies(int pageId) throws ResponseStatusException {
        // Build the request Url.
        Uri requestUrl = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(PATH_POPULAR)
                .appendQueryParameter(QUERY_API_KEY, mApiKey)
                .appendQueryParameter(QUERY_PAGE, String.valueOf(pageId))
                .appendQueryParameter(QUERY_LANGUAGE, getLocaleCode()).build();

        // Build the HttpClient request.
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url((requestUrl).toString()).build();
        try {
            // Execute the request and parse the JSONObject.
            Response response = httpClient.newCall(request).execute();
            JSONObject responseJSONObject = new JSONObject(response.body().string());

            // Throw an exception if the API return an error.
            if ((responseJSONObject.has("status_code")) && (responseJSONObject.has("status_message"))) {
                throw new ResponseStatusException(responseJSONObject.getInt("status_code"), responseJSONObject.getString("status_message"));
            }

            // Deserialize the JSONObject.
            return deserializeMovieList(responseJSONObject);
        } catch (JSONException|IOException e) {
            // Throw an exception if the request fails.
            throw new ResponseStatusException(504, "Your request to the backend server timed out. Try again.");
        }
    }

    /**
     * Fetch the top rated movies from TMDB API.
     *
     * @param pageId The page number to query.
     * @return MovieList object.
     * @throws ResponseStatusException
     */
    public MovieList fetchTopRatedMovies(int pageId) throws ResponseStatusException {
        // Build the request Url.
        Uri requestUrl = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(PATH_TOP_RATED)
                .appendQueryParameter(QUERY_API_KEY, mApiKey)
                .appendQueryParameter(QUERY_PAGE, String.valueOf(pageId))
                .appendQueryParameter(QUERY_LANGUAGE, getLocaleCode()).build();

        // Build the HttpClient request.
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url((requestUrl).toString()).build();
        try {
            // Execute the request and parse the JSONObject.
            Response response = httpClient.newCall(request).execute();
            JSONObject responseJSONObject = new JSONObject(response.body().string());

            // Throw an exception if the API return an error.
            if ((responseJSONObject.has("status_code")) && (responseJSONObject.has("status_message"))) {
                throw new ResponseStatusException(responseJSONObject.getInt("status_code"), responseJSONObject.getString("status_message"));
            }

            // Deserialize the JSONObject.
            return deserializeMovieList(responseJSONObject);
        } catch (JSONException|IOException e) {
            // Throw an exception if the request fails.
            throw new ResponseStatusException(504, "Your request to the backend server timed out. Try again.");
        }
    }

    /**
     * Fetch the currently playing movies from TMDB API.
     *
     * @param pageId The page number to query.
     * @return MovieList object.
     * @throws ResponseStatusException
     */
    public MovieList fetchNowPlayingMovies(int pageId) throws ResponseStatusException {
        // Build the request Url.
        Uri requestUrl = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(PATH_NOW_PLAYING)
                .appendQueryParameter(QUERY_API_KEY, mApiKey)
                .appendQueryParameter(QUERY_PAGE, String.valueOf(pageId))
                .appendQueryParameter(QUERY_LANGUAGE, getLocaleCode()).build();

        // Build the HttpClient request.
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url((requestUrl).toString()).build();
        try {
            // Execute the request and parse the JSONObject.
            Response response = httpClient.newCall(request).execute();
            JSONObject responseJSONObject = new JSONObject(response.body().string());

            // Throw an exception if the API return an error.
            if ((responseJSONObject.has("status_code")) && (responseJSONObject.has("status_message"))) {
                throw new ResponseStatusException(responseJSONObject.getInt("status_code"), responseJSONObject.getString("status_message"));
            }

            // Deserialize the JSONObject.
            return deserializeMovieList(responseJSONObject);
        } catch (JSONException|IOException e) {
            // Throw an exception if the request fails.
            throw new ResponseStatusException(504, "Your request to the backend server timed out. Try again.");
        }
    }

    /**
     * Fetch a list of movies corresponding to the provided keywords from TMDB API.
     *
     * @param keywords The keywords to query.
     * @param pageId The page number to query.
     * @return MovieList object.
     * @throws ResponseStatusException
     */
    public MovieList fetchSearchMovies(@NonNull String keywords, int pageId) throws ResponseStatusException {
        // Build the request Url.
        Uri requestUrl = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(PATH_SEARCH)
                .appendPath(PATH_MOVIE)
                .appendQueryParameter(QUERY_API_KEY, mApiKey)
                .appendQueryParameter(QUERY_KEYWORDS, keywords)
                .appendQueryParameter(QUERY_PAGE, String.valueOf(pageId))
                .appendQueryParameter(QUERY_LANGUAGE, getLocaleCode()).build();

        // Build the HttpClient request.
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url((requestUrl).toString()).build();
        try {
            // Execute the request and parse the JSONObject.
            Response response = httpClient.newCall(request).execute();
            JSONObject responseJSONObject = new JSONObject(response.body().string());

            // Throw an exception if the API return an error.
            if ((responseJSONObject.has("status_code")) && (responseJSONObject.has("status_message"))) {
                throw new ResponseStatusException(responseJSONObject.getInt("status_code"), responseJSONObject.getString("status_message"));
            }

            // Deserialize the JSONObject.
            return deserializeMovieList(responseJSONObject);
        } catch (JSONException|IOException e) {
            // Throw an exception if the request fails.
            throw new ResponseStatusException(504, "Your request to the backend server timed out. Try again.");
        }
    }

    /**
     * Return the TMDB API key located in the manifest file.
     *
     * @return TMDB API key.
     */
    private String getApiKey() {
        String apiKey = "";

        try {
            ApplicationInfo ai = this.mContext.getPackageManager()
                    .getApplicationInfo(this.mContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            apiKey = bundle.getString("com.dannyxmichaud.portfolio.popularmovies.tmdbapi.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        return apiKey;
    }
}

