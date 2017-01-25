package com.dannyxmichaud.portfolio.popularmovies;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class MainActivity extends AppCompatActivity implements FloatingSearchView.OnSearchListener {

    // Layout reference.
    private FloatingSearchView mFloatingSearchView;

    // This object will be null if the layout is in
    // single-pane layout.
    private MovieDetailsFragment mMovieDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get layout references.
        mFloatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        // Setup the tab layout layout to display the appropriate fragment when clicked.
        PagerAdapter pagerAdapter = new FixedTabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Register the persistent floating search view listener to handles search query.
        mFloatingSearchView.setOnSearchListener(this);

        // Get the movie details fragment. This object will be null if the layout is in
        // single-pane layout.
        mMovieDetailsFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.movie_details_fragment);
    }

    /**
     * Called when a search suggestion has been clicked from the persistent floating search layout.
     *
     * @param searchSuggestion Search suggestion information.
     */
    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        // Clear the floating search view text.
        mFloatingSearchView.clearQuery();

        // Open the activity to display the search results.
        Intent intent = new Intent(this, SearchResultsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SearchResultsActivity.BUNDLE_KEYWORDS_ID, searchSuggestion.getBody());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Called when a search action is requested from the persistent floating search layout.
     *
     * @param currentQuery The keywords to search for.
     */
    @Override
    public void onSearchAction(String currentQuery) {
        // Clear the floating search view text.
        mFloatingSearchView.clearQuery();

        // Open the activity to display the search results.
        Intent intent = new Intent(this, SearchResultsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SearchResultsActivity.BUNDLE_KEYWORDS_ID, currentQuery);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Display the movie details depending on the layout display.
     * Cane be singles-pane layout or multiple-pane layout depending
     * on the screen size.
     *
     * @param movieId The movie id to display.
     */
    public void displayMovieDetails(int movieId) {
        if (mMovieDetailsFragment != null) {
            // Open an activity since the layout is in single-pane layout.
            mMovieDetailsFragment.updateMovieContent(movieId);
        } else {
            // Double-pane layout. Send data to fragment.
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(MovieDetailsActivity.BUNDLE_MOVIE_ID, movieId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    /**
     * Fragment tabs mMovieListAdapter class.
     */
    class FixedTabsPagerAdapter extends FragmentStatePagerAdapter {

        FixedTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PopularFragment();
                case 1:
                    return new NowPlayingFragment();
                case 2:
                    return new TopRatedFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    if (mMovieDetailsFragment == null) return getString(R.string.popular_tab_title_short);
                    else return getString(R.string.popular_tab_title_full);
                case 1:
                    if (mMovieDetailsFragment == null) return getString(R.string.now_playing_tab_title_short);
                    else return getString(R.string.now_playing_tab_title_full);
                case 2:
                    if (mMovieDetailsFragment == null) return getString(R.string.top_rated_tab_title_short);
                    else return getString(R.string.top_rated_tab_title_full);
                default:
                    return null;
            }
        }
    }
}
