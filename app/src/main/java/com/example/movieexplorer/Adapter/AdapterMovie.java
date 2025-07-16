package com.example.movieexplorer.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieexplorer.Domain.Movie;
import com.example.movieexplorer.Domain.Genre;
import com.example.movieexplorer.R;

import java.util.List;
import java.util.Locale;

public class AdapterMovie extends RecyclerView.Adapter<AdapterMovie.MovieViewHolder> {

    private List<Movie> movieList;
    private OnItemClickListener listener;
    private int itemLayoutResId;

    public interface OnItemClickListener {
        void onItemClick(Movie film);
    }


    public AdapterMovie(List<Movie> movieList, OnItemClickListener listener) {
        this(movieList, listener, R.layout.movie_item);
    }


    public AdapterMovie(List<Movie> movieList, OnItemClickListener listener, int itemLayoutResId) {
        this.movieList = movieList;
        this.listener = listener;
        this.itemLayoutResId = itemLayoutResId;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single movie item view
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResId, parent, false);
        // Create and return a new ViewHolder instance holding the inflated view
        return new MovieViewHolder(view, itemLayoutResId);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Get the movie object at the given position in the list
        Movie film = movieList.get(position);
        // Set the movie title in the title TextView
        holder.titleTextView.setText(film.getTitle());
        // Set the release date in the corresponding TextView
        holder.dateResult.setText(film.getReleaseDate());
        // If the score TextView exists, set the movie's average vote formatted to 1 decimal place
        if (holder.scoreTextView != null) {
            holder.scoreTextView.setText(String.format(Locale.US, "%.1f", film.getVoteAverage()));
        }
        // Construct the full URL for the movie poster image
        String imageUrl = "https://image.tmdb.org/t/p/w500" + film.getPosterPath();
        // Load the movie poster image into the ImageView using Glide
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.unavailable) // Show placeholder image while loading
                .error(R.drawable.unavailable) //// Show this image if loading fails
                .into(holder.posterImageView);
        // Set a click listener on the entire item view to notify the listener when clicked
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(film); // Pass the clicked movie back through the listener
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of movies in the list to tell RecyclerView how many items to display
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;
        TextView scoreTextView;
        TextView dateResult;
        TextView genreTextView;

        public MovieViewHolder(@NonNull View itemView, int layoutResId) {
            super(itemView);
            // Initialize view references depending on which layout resource is used
            if (layoutResId == R.layout.movie_item) {
                // For the regular movie item layout
                posterImageView = itemView.findViewById(R.id.imageView);
                titleTextView = itemView.findViewById(R.id.nameTxt);
                scoreTextView = itemView.findViewById(R.id.scoreTxt);
                dateResult = itemView.findViewById(R.id.year);
                genreTextView = itemView.findViewById(R.id.genreMovie);

            } else if (layoutResId == R.layout.search_item) {
                // For the search results item layout
                posterImageView = itemView.findViewById(R.id.search_item_poster);
                titleTextView = itemView.findViewById(R.id.search_item_title);
                scoreTextView = itemView.findViewById(R.id.scoreResult);
                dateResult = itemView.findViewById(R.id.search_date);
                genreTextView = itemView.findViewById(R.id.genreSearch);
            } else {
                // Log an error if the layout resource ID is unknown or unsupported
                Log.e("AdapterMovie", "Unknown layout ID: " + layoutResId);

            }
        }


    }
}