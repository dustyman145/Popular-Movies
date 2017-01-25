package com.dannyxmichaud.portfolio.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieAPI;
import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieItem;
import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieList;
import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;

public class PopularFragment extends Fragment implements View.OnClickListener {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        // Get layout elements references.
        mRecyclerView = (RecyclerView) view.findViewById(R.id.popular_recycler_view);
        mStatusLayout = (LinearLayout) view.findViewById(R.id.status_view);
        mStatusText = (TextView) view.findViewById(R.id.status_text);
        mStatusProgressBar = (ProgressBar) view.findViewById(R.id.status_progress_bar);

        // Set an OnClickListener to fetch new items when the user tap the view.
        view.findViewById(R.id.main_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the RecyclerView and no loading is occurring is empty try to load the first page
                // from the TMDB API.
                if (mRecyclerView.getLayoutManager().getItemCount() == 0 && mAsyncTask == null) {
                    fetchMovieList(1);
                }
            }
        });

        // Setup RecyclerView mMovieListAdapter and listeners.
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), GRID_SPAN));
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

        // Returns the inflated view.
        return view;
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

        // Display the movie details, the MainActivity will automatically handle the layout mode.
        ((MainActivity)getActivity()).displayMovieDetails(movieItem.getMovieID());
    }

    private class FetchMovieListAsyncTask extends AsyncTask<Integer, Void, Boolean> {

        // MovieAPI object to automatically handles TMDB API calls.
        MovieAPI mMovieAPI = new MovieAPI(getContext());

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
                    mMovieList = mMovieAPI.fetchPopularMovies(page);
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
