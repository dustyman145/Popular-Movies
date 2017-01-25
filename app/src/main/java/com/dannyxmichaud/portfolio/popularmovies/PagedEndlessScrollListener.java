package com.dannyxmichaud.portfolio.popularmovies;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

@SuppressWarnings({"WeakerAccess", "unused"})

/**
 * Custom OnScrollListener that automatically handles mLoading more items when certain criteria
 * are met. Can be attached to any RecyclerView.
 *
 * Automatically handles pages if necessary.
 */
public abstract class PagedEndlessScrollListener extends RecyclerView.OnScrollListener {
    // The minimum number of items to have below your current scroll position
    // before mLoading more.
    private int mVisibleThreshold;
    // The current offset index of data you have loaded
    private int mCurrentPage = 1;
    // The total number of items in the dataset after the last load
    private int mPreviousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean mLoading = true;
    // True if all item has been loaded.
    private boolean mAllItemLoaded = false;

    /**
     * Public constructor.
     *
     * @param mVisibleThreshold The minimum number of items to have below your current
     *                         scroll position before mLoading more.
     */
    public PagedEndlessScrollListener(int mVisibleThreshold) {
        this.mVisibleThreshold = mVisibleThreshold;
    }

    /**
     * Callback method to be invoked when the RecyclerView has been scrolled.
     * This will be called after the scroll has completed.
     * This callback will also be called if visible item range changes after a layout calculation.
     * In that case, dx and dy will be 0.
     *
     * @param recyclerView The RecyclerView which scrolled.
     * @param dx The amount of horizontal scroll.
     * @param dy The amount of vertical scroll.
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // Get the LayoutManager's item information.
        // Also works on GridLayoutManager since it is a subclass of LineaLayoutManager.
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        int visibleItemCount = layoutManager.getChildCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        // Updates the local variables if an ongoing mLoading is finished.
        if (mLoading) {
            if (totalItemCount > mPreviousTotalItemCount) {
                mLoading = false;
                mPreviousTotalItemCount = totalItemCount;
                mCurrentPage++;
            }
        }

        // Trigger onLoadMore if more page is available, if no mLoading is currently ongoing and
        // if the mVisibleThreshold reaches its limit.
        if (!mAllItemLoaded && !mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
            mAllItemLoaded = !onLoadMore(mCurrentPage + 1);
            mLoading = true;
        }
    }

    /**
     * Defines the process for actually mLoading more data based on page.
     *
     * @param page The page number to load.
     * @return True if more data is being loaded; Returns false if there is no more data to load.
     */
    public abstract boolean onLoadMore(int page);
}
