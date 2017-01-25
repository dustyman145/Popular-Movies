package com.dannyxmichaud.portfolio.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieAPI;
import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieItem;
import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieList;
import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener{

    public final static String BUNDLE_KEYWORDS_ID = "mKeywords";

    // The mKeywords to search for.
    private String mKeywords;

    // The number of span in the grid recycler view.
    private static final int GRID_SPAN = 3;
    // The maximum number of pages the RecyclerView will show.
    // The number of items per page depends on TMDB API.
    private static final int PAGE_LIMIT = 20;

    // AsyncTask object to keep track of if it's already working.
    FetchMovieListAsyncTask mAsyncTask = null;
    // Custom RecyclerView mMovieListAdapter object.
    MovieListAdapter mMovieListAdapter;

    // Layout references.
    RecyclerView mRecyclerView;
    LinearLayout mStatusLayout;
    ProgressBar mStatusProgressBar;
    TextView mStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Get layout elements references.
        mRecyclerView = (RecyclerView) findViewById(R.id.search_results_recycler_view);
        mStatusLayout = (LinearLayout) findViewById(R.id.status_view);
        mStatusText = (TextView) findViewById(R.id.status_text);
        mStatusProgressBar = (ProgressBar) findViewById(R.id.status_progress_bar);

        // Set toolbar.
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.search_results_activity_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mKeywords = getIntent().getExtras().getString(BUNDLE_KEYWORDS_ID);

        mRecyclerView = (RecyclerView) findViewById(R.id.search_results_recycler_view);
        // Setup RecyclerView mMovieListAdapter and listeners.
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, GRID_SPAN));
        mMovieListAdapter = new MovieListAdapter(new ArrayList<MovieItem>(), (GridLayoutManager) mRecyclerView.getLayoutManager());
        mMovieListAdapter.SetItemOnClickListener(this);
        mRecyclerView.setAdapter(mMovieListAdapter);

        // Set EndLessScrollListener to load more items when the end of the list has been reached.
        mRecyclerView.addOnScrollListener(new PagedEndlessScrollListener(5) {
            @Override
            public boolean onLoadMore(int page) {
                if (page > PAGE_LIMIT) {
                    // Reached the last page and no more item to load.
                    return false;
                } else {
                    // Load the next page.
                    fetchMovieList(page);
                    return true;
                }
            }
        });
    }

    /**
     * Called when the activity is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        // If the RecyclerView doesn't contain any items, try to load the first page
        // from the TMDB API.
        if(mRecyclerView.getLayoutManager().getItemCount() == 0) {
            fetchMovieList(1);
        }
    }

    /**
     * Called when a new item in the RecyclerView has been clicked.
     *
     * @param v The view that has been clicked.
     */
    @Override
    public void onClick(View v) {

        // Get the MovieItem from the clicked view.
        int itemPosition = mRecyclerView.getChildAdapterPosition(v);
        MovieItem movieItem = mMovieListAdapter.GetMovieItem(itemPosition);

        // Start the MovieDetailsActivity and pass the movie id.
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MovieDetailsActivity.BUNDLE_MOVIE_ID, movieItem.getMovieID());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Load the specified page of movies from the TMDB API.
     *
     * @param page The page number to load.
     */
    void fetchMovieList(int page) {
        // Start FetchMovieListAsyncTask if it's not already running.
        if (mAsyncTask == null) {
            mAsyncTask = new FetchMovieListAsyncTask();
            mAsyncTask.execute(page);
        }
    }

    private class FetchMovieListAsyncTask extends AsyncTask<Integer, Void, Boolean> {

        // MovieAPI object to automatically handles TMDB API calls.
        MovieAPI mMovieAPI = new MovieAPI(SearchResultsActivity.this);

        // The mMovieList will be null or empty if an error occurred or no item was fetched.
        MovieList mMovieList = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // If the RecyclerView doesn't contains any item, show the loading view.
            if(mRecyclerView.getLayoutManager().getItemCount() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mStatusLayout.setVisibility(View.VISIBLE);
                mStatusProgressBar.setVisibility(View.VISIBLE);
                mStatusText.setVisibility(View.VISIBLE);

                mStatusText.setText(R.string.fetch_status_loading);
            }
        }

        @Override
        protected Boolean doInBackground(Integer... pageList) {
            boolean success = true;

            // Fetch all specified pages from the TMDB API.
            try {
                for(int page : pageList) {
                    mMovieList = mMovieAPI.fetchSearchMovies(mKeywords, page);
                    mMovieListAdapter.addCollectionItem(Arrays.asList(mMovieList.getAllMovies()));
                }
            } catch (ResponseStatusException e) {
                Log.e("TMDB API", e.getMessage());
                success = false;
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            // Notify the mMovieListAdapter if the fetched mMovieList is not empty.
            if (mMovieList != null) {
                mMovieListAdapter.notifyDataSetChanged();
            }

            // If the RecyclerView is empty and an error occurred show an error message.
            // Else hide the status layout.
            if (!success && mRecyclerView.getLayoutManager().getItemCount() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mStatusLayout.setVisibility(View.VISIBLE);
                mStatusProgressBar.setVisibility(View.GONE);
                mStatusText.setVisibility(View.VISIBLE);
                mStatusText.setText(R.string.fetch_status_failed);
            } else if (success && mRecyclerView.getLayoutManager().getItemCount() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mStatusLayout.setVisibility(View.VISIBLE);
                mStatusProgressBar.setVisibility(View.GONE);
                mStatusText.setVisibility(View.VISIBLE);
                mStatusText.setText(R.string.fetch_status_no_result_search);
            } else {
                mStatusLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            // Update AsyncTask object for tracking purpose.
            mAsyncTask = null;
        }

        @Override
        protected void onCancelled(Boolean success) {
            super.onCancelled(success);

            // Notify the mMovieListAdapter if the fetched mMovieList is not empty.
            if (mMovieList != null) {
                mMovieListAdapter.notifyDataSetChanged();
            }

            // If the RecyclerView is empty and an error occurred show an error message.
            // Else hide the status layout.
            if (!success && mRecyclerView.getLayoutManager().getItemCount() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mStatusLayout.setVisibility(View.VISIBLE);
                mStatusProgressBar.setVisibility(View.GONE);
                mStatusText.setVisibility(View.VISIBLE);
                mStatusText.setText(R.string.fetch_status_failed);
            } else if (success && mRecyclerView.getLayoutManager().getItemCount() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mStatusLayout.setVisibility(View.VISIBLE);
                mStatusProgressBar.setVisibility(View.GONE);
                mStatusText.setVisibility(View.VISIBLE);
                mStatusText.setText(R.string.fetch_status_no_result_search);
            } else {
                mStatusLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            // Update AsyncTask object for tracking purpose.
            mAsyncTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            // If the RecyclerView is empty and an error occurred show an error message.
            // Else hide the status layout.
            if (mRecyclerView.getLayoutManager().getItemCount() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mStatusLayout.setVisibility(View.VISIBLE);
                mStatusProgressBar.setVisibility(View.GONE);
                mStatusText.setVisibility(View.VISIBLE);
                mStatusText.setText(R.string.fetch_status_failed);
            } else {
                mStatusLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            // Update AsyncTask object for tracking purpose.
            mAsyncTask = null;
        }
    }
}
