package com.dannyxmichaud.portfolio.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieDetailsFragment extends Fragment {

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

    // The movie id to get details.
    private int mMovieId;

    //
    // private FetchMovieDetailsAsyncTask mAsyncTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }

    public void updateMovieContent(int movieId) {

    }
}
