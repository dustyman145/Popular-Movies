package com.dannyxmichaud.portfolio.popularmovies;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dannyxmichaud.portfolio.popularmovies.tmdbapi.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("WeakerAccess")

/**
 * Adapters provide a binding from an app-specific data set to views that are displayed
 * within a RecyclerView.
 */
public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // OnClickListener object to handle RecyclerView item click.
    private View.OnClickListener mItemOnClickListener;

    // A list of MovieItem used by the Adapter to populate its view.
    private List<MovieItem> mMovieList = new ArrayList<>();

    // The number of column the GridLayoutManager contains.
    // By default, one.
    private int mSpanCount = 1;

    /**
     * Public constructor.
     *
     * @param movieList MovieItem collection.
     */
    public MovieListAdapter(Collection<MovieItem> movieList, GridLayoutManager layoutManager) {
        mMovieList.addAll(movieList);
        mSpanCount = layoutManager.getSpanCount();

        // Set SpanSizeLookup to handle both view type.
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (getItemViewType(position)) {
                    case R.layout.movie_list_item_full_width:
                        return mSpanCount;
                    case R.layout.movie_list_item_normal:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
    }

    /**
     * Set the RecyclerView's ItemOnClickListener.
     *
     * @param clickListener OnClickListenerObject.
     */
    public void SetItemOnClickListener(View.OnClickListener clickListener) {
        mItemOnClickListener = clickListener;
    }

    /**
     * Add a collection of MovieItem to the Adapter. The collection
     * will be added at the end of the Adapter's collection item.
     *
     * @param collection MovieItem collection.
     */
    public void addCollectionItem(Collection<MovieItem> collection) {
        mMovieList.addAll(collection);
    }

    /**
     * Get the MovieItem object at the specified position.
     *
     * @param position The item position in the mMovieListAdapter.
     * @return A MovieItem object.
     */
    public MovieItem GetMovieItem(int position) {
        return mMovieList.get(position);
    }

    /**
     * This method calls onCreateViewHolder(ViewGroup, int) to create a new RecyclerView.ViewHolder
     * and initializes some private fields to be used by RecyclerView.
     *
     * @param parent The ViewGroup parent.
     * @param viewType The corresponding ViewType depending on the ViewHolder position.
     * @return The created ViewHolder.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        // Inflate the correct layout depending on the item ViewType.
        switch(viewType) {
            case R.layout.movie_list_item_full_width:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item_full_width, parent, false);
                view.setOnClickListener(mItemOnClickListener);
                return new MovieFullWidthViewHolder(view);
            case R.layout.movie_list_item_normal:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item_normal, parent, false);
                view.setOnClickListener(mItemOnClickListener);
                return new MovieNormalViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item_normal, parent, false);
                view.setOnClickListener(mItemOnClickListener);
                return new MovieNormalViewHolder(view);
        }
    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position and also sets up
     * some private fields to be used by RecyclerView.
     *
     * @param holder The ViewHolder object.
     * @param position Item position in the ViewRecycler.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Bind the data to the view depending on its view type.
        switch(getItemViewType(position)) {
            case R.layout.movie_list_item_full_width:
                ((MovieFullWidthViewHolder)holder).bindMovieItem(mMovieList.get(position));
                break;
            case R.layout.movie_list_item_normal:
                ((MovieNormalViewHolder)holder).bindMovieItem(mMovieList.get(position));
                break;
        }
    }

    /**
     * Return the view type of the item at position for the purposes of view recycling.
     * The default implementation of this method returns 0, making the assumption of a
     * single view type for the mMovieListAdapter. Unlike ListView adapters, types need not be contiguous.
     * Consider using id resources to uniquely identify item view types.
     *
     * @param position Position to query.
     * @return Integer value identifying the type of the view needed to represent the item at position.
     */
    @Override
    public int getItemViewType(int position) {
        // 1 row out of two will be full width.
        if (position % (mSpanCount + 1) == 0) return R.layout.movie_list_item_full_width;
        else return R.layout.movie_list_item_normal;
    }

    /**
     * Returns the total number of items in the data set held by the mMovieListAdapter.
     *
     * @return The total number of items in this mMovieListAdapter.
     */
    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    /**
     * This view holder is designed to take the width of a single item in a RecyclerView.
     * This ViewHolder has a different layout than the NormalViewHolder designed to fit in a single
     * column.
     *
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * RecyclerView.Adapter implementations should subclass ViewHolder and add fields for caching
     * potentially expensive findViewById(int) results.
     */
    private class MovieNormalViewHolder extends RecyclerView.ViewHolder {

        // Store view references in a local object
        // for better performance.
        private ImageView mPosterView;
        private TextView mTitleView;
        private TextView mVoteAverageView;

        // Current context.
        private Context mContext;

        /**
         * Public constructor.
         *
         * @param itemView Inflated view object.
         */
        private MovieNormalViewHolder(View itemView) {
            super(itemView);

            // Store current context.
            mContext = itemView.getContext();

            // Since calling findViewById is heavy we store the references in
            // a local variable for later use.
            mPosterView = (ImageView) itemView.findViewById(R.id.poster_view);
            mTitleView = (TextView) itemView.findViewById(R.id.title_view);
            mVoteAverageView = (TextView) itemView.findViewById(R.id.vote_average_view);
        }

        /**
         * Populate the view data using its associated MovieItem.
         *
         * @param movieItem he MovieItem object associated with this view.
         */
        private void bindMovieItem(MovieItem movieItem) {
            // We first need to cancel any Picasso request. This is required since
            // we are using a RecyclerView object. As its name suggests, a RecyclerView
            // object reuse views to load new content. If the view had an unfinished Picasso
            // request on it the old image previously used in the view will be loaded instead
            // of the new one.
            Picasso.with(mContext).cancelRequest(mPosterView);

            // Load a new Picasso request if a poster URL is provided.
            if (!movieItem.getPosterUrl().isEmpty()) {
                Picasso.with(mContext).load(movieItem.getPosterUrl())
                        .placeholder(R.drawable.no_poster)
                        .error(R.drawable.no_poster)
                        .into(mPosterView);
            } else {
                // Use a default poster since no poster URL is provided.
                mPosterView.setImageResource(R.drawable.no_poster);
            }

            // Set the movie title.
            mTitleView.setText(movieItem.getTitle());
            mVoteAverageView.setText(String.valueOf(movieItem.getVoteAverage()));
        }
    }

    /**
     * A full width view holder designed to take the full width of a RecyclerView. This ViewHolder
     * has a different layout than the NormalViewHolder.
     *
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * RecyclerView.Adapter implementations should subclass ViewHolder and add fields for caching
     * potentially expensive findViewById(int) results.
     */
    private class MovieFullWidthViewHolder extends RecyclerView.ViewHolder {

        // Store view references in a local object
        // for better performance.
        private ImageView mPosterView;
        private TextView mTitleView;
        private TextView mOverviewView;
        private TextView mVoteAverageView;

        // Current context.
        private Context mContext;

        /**
         * Public constructor.
         *
         * @param itemView Inflated view object.
         */
        private MovieFullWidthViewHolder(View itemView) {
            super(itemView);

            // Store current context.
            mContext = itemView.getContext();

            // Since calling findViewById is heavy we store the references in
            // a local variable for later use.
            mPosterView = (ImageView) itemView.findViewById(R.id.poster_view);
            mTitleView = (TextView) itemView.findViewById(R.id.title_view);
            mOverviewView = (TextView) itemView.findViewById(R.id.overview_view);
            mVoteAverageView = (TextView) itemView.findViewById(R.id.vote_average_view);
        }

        /**
         * Populate the view data using its associated MovieItem.
         *
         * @param movieItem The MovieItem object associated with this view.
         */
        private void bindMovieItem(MovieItem movieItem) {
            // We first need to cancel any Picasso request. This is required since
            // we are using a RecyclerView object. As its name suggests, a RecyclerView
            // object reuse views to load new content. If the view had an unfinished Picasso
            // request on it the old image previously used in the view will be loaded instead
            // of the new one.
            Picasso.with(mContext).cancelRequest(mPosterView);

            // Load a new Picasso request if a poster URL is provided.
            if (!movieItem.getPosterUrl().isEmpty()) {
                Picasso.with(mContext).load(movieItem.getPosterUrl())
                        .placeholder(R.drawable.no_poster)
                        .error(R.drawable.no_poster)
                        .into(mPosterView);
            } else {
                // Use a default poster since no poster URL is provided.
                mPosterView.setImageResource(R.drawable.no_poster);
            }

            // Set movie title and overview.
            mTitleView.setText(movieItem.getTitle());
            mOverviewView.setText(movieItem.getOverview());
            mVoteAverageView.setText(String.valueOf(movieItem.getVoteAverage()));
        }
    }
}
