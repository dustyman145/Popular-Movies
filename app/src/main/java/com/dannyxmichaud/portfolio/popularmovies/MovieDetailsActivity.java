package com.dannyxmichaud.portfolio.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieAPI;
import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieDetails;
import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.ResponseStatusException;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    public final static String BUNDLE_MOVIE_ID = "movie_id";

    // Layout references.
    private TextView mTitleView;
    private TextView mTaglineView;
    private TextView mDateView;
    private TextView mRuntimeView;
    private TextView mOverviewView;
    private TextView mVotesView;
    private TextView mRatingView;
    private TextView mGenreView;
    private TextView mPopularityView;
    private TextView mLanguageView;
    private ImageView mPosterView;
    private ImageView mBackdropView;
    private View mStatusView;
    private TextView mStatusTextView;
    private ProgressBar mStatusProgressBar;
    private View mMovieDetailsView;

    // The movie id to get details.
    private int mMovieId;

    // AsyncTask object to keep track of if it's already working.
    private FetchMovieDetailsAsyncTask mAsyncTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Get layout references.
        mTitleView = (TextView) findViewById(R.id.title_view);
        mTaglineView = (TextView) findViewById(R.id.tagline_view);
        mDateView = (TextView) findViewById(R.id.date_view);
        mRuntimeView = (TextView) findViewById(R.id.runtime_view);
        mOverviewView = (TextView) findViewById(R.id.overview_view);
        mVotesView = (TextView) findViewById(R.id.votes_view);
        mRatingView = (TextView) findViewById(R.id.rating_view);
        mGenreView = (TextView) findViewById(R.id.genre_view);
        mPopularityView = (TextView) findViewById(R.id.popularity_view);
        mLanguageView = (TextView) findViewById(R.id.language_view);
        mPosterView = (ImageView) findViewById(R.id.poster_view);
        mBackdropView = (ImageView) findViewById(R.id.backdrop_view);
        mStatusProgressBar = (ProgressBar) findViewById(R.id.status_progress_bar);
        mStatusTextView = (TextView) findViewById(R.id.status_text);
        mStatusView = findViewById(R.id.status_view);
        mMovieDetailsView = findViewById(R.id.movie_details_view);

        // Get the movie id from the bundle.
        mMovieId = getIntent().getExtras().getInt(BUNDLE_MOVIE_ID);

        // Set toolbar.
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.movie_details_activity_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Fetch the movie details from the TMDB API.
        mAsyncTask = new FetchMovieDetailsAsyncTask();
        mAsyncTask.execute(mMovieId);
    }

    /**
     * Populate the view with the movie details fetched from th TMDB API.
     *
     * @param movieDetails MovieDetails object.
     */
    private void populateDataView(MovieDetails movieDetails) {
        String tagline = "\"" + movieDetails.getTagline() + "\"";
        String dateRelease = String.format("%1s (%2s)", movieDetails.getReleaseDate(), movieDetails.getStatus());
        String runtime = String.format(getString(R.string.runtime_min), movieDetails.getRuntime());

        String voteCount;
        if (movieDetails.getVoteCount() <= 1) voteCount = String.format(getString(R.string.single_vote), movieDetails.getVoteCount());
        else voteCount = String.format(getString(R.string.multiple_votes), movieDetails.getVoteCount());

        StringBuilder genres = new StringBuilder();
        boolean firstLine = true;
        for (String item : movieDetails.getGenres()) {
            if (!firstLine) genres.append(System.getProperty("line.separator"));
            genres.append(item);
            firstLine = false;
        }

        // Set the maximum length of the popular value to 4 chars.
        String popular = String.valueOf(movieDetails.getPopularity());
        if (popular.length() > 4) {
            popular = popular.substring(0, 3);
            if (popular.endsWith(".")) popular = popular.replace(".", "");
        }

        // Populate TextViews.
        mTitleView.setText(movieDetails.getTitle());
        mTaglineView.setText(tagline);
        mDateView.setText(dateRelease);
        mRuntimeView.setText(runtime);
        mOverviewView.setText(movieDetails.getOverview());
        mVotesView.setText(voteCount);
        mRatingView.setText(String.valueOf(movieDetails.getVoteAverage()));
        mGenreView.setText(genres.toString());
        mPopularityView.setText(popular);
        mLanguageView.setText(movieDetails.getOriginalLanguage());

        // Load a new Picasso request if a poster URL is provided.
        if (!movieDetails.getPosterUrl().isEmpty()) {
            Picasso.with(this).load(movieDetails.getPosterUrl())
                    .placeholder(R.drawable.no_poster)
                    .error(R.drawable.no_poster)
                    .into(mPosterView);
        } else {
            // Use a default poster since no poster URL is provided.
            mPosterView.setImageResource(R.drawable.no_poster);
        }

        // Load a new Picasso request if a backdrop URL is provided.
        if (!movieDetails.getBackdropUrl().isEmpty()) {
            Picasso.with(this).load(movieDetails.getBackdropUrl()).into(mBackdropView);
        } else {
            // No backdrop URL is provided. Remove the drawable.
            mBackdropView.setImageDrawable(null);
        }
    }

    private class FetchMovieDetailsAsyncTask extends AsyncTask<Integer, Void, Void> {

        // MovieAPI object to automatically handles TMDB API calls.
        MovieAPI mMovieAPI = new MovieAPI(MovieDetailsActivity.this);

        // The mMovieList will be null or empty if an error occurred or no item was fetched.
        MovieDetails mMovieDetails = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mMovieDetailsView.setVisibility(View.GONE);
            mStatusView.setVisibility(View.VISIBLE);
            mStatusTextView.setVisibility(View.VISIBLE);
            mStatusProgressBar.setVisibility(View.VISIBLE);
            mStatusTextView.setText(R.string.fetch_status_loading);
        }

        @Override
        protected Void doInBackground(Integer... pageList) {
            // Fetch movie details from the TMDB API.
            try {
                int movieId = pageList[0];
                mMovieDetails = mMovieAPI.fetchMovieDetails(movieId);
            } catch (ResponseStatusException e) {
                Log.e("TMDB API", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);

            if (mMovieDetails != null) {
                populateDataView(mMovieDetails);
                mMovieDetailsView.setVisibility(View.VISIBLE);
                mStatusView.setVisibility(View.GONE);
                mStatusTextView.setVisibility(View.GONE);
                mStatusProgressBar.setVisibility(View.GONE);
            } else {
                mMovieDetailsView.setVisibility(View.GONE);
                mStatusView.setVisibility(View.VISIBLE);
                mStatusTextView.setVisibility(View.VISIBLE);
                mStatusProgressBar.setVisibility(View.GONE);
                mStatusTextView.setText(R.string.fetch_status_failed);
            }

            // Update AsyncTask object for tracking purpose.
            mAsyncTask = null;
        }

        @Override
        protected void onCancelled(Void param) {
            super.onCancelled(param);

            if (mMovieDetails != null) {
                populateDataView(mMovieDetails);
                mMovieDetailsView.setVisibility(View.VISIBLE);
                mStatusView.setVisibility(View.GONE);
                mStatusTextView.setVisibility(View.GONE);
                mStatusProgressBar.setVisibility(View.GONE);
            } else {
                mMovieDetailsView.setVisibility(View.GONE);
                mStatusView.setVisibility(View.VISIBLE);
                mStatusTextView.setVisibility(View.VISIBLE);
                mStatusProgressBar.setVisibility(View.GONE);
                mStatusTextView.setText(R.string.fetch_status_failed);
            }

            // Update AsyncTask object for tracking purpose.
            mAsyncTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            // TODO: Show error layout.

            // Update AsyncTask object for tracking purpose.
            mAsyncTask = null;
        }
    }
}
