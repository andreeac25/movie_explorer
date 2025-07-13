package com.example.movieexplorer.Adapter;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterMovie extends RecyclerView.Adapter<AdapterMovie.MovieViewHolder> {

    private List<Movie> movieList;
    private List<Movie> originalMovieList;
    private OnItemClickListener listener;
    private int itemLayoutResId;

    public interface OnItemClickListener {
        void onItemClick(Movie film);
    }


    public AdapterMovie(List<Movie> movieList, OnItemClickListener listener) {
        this(movieList, listener, R.layout.movie_item);
    }


    public AdapterMovie(List<Movie> movieList, OnItemClickListener listener, int itemLayoutResId) {
        this.originalMovieList = new ArrayList<>(movieList);
        this.movieList = movieList;
        this.listener = listener;
        this.itemLayoutResId = itemLayoutResId;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResId, parent, false);
        return new MovieViewHolder(view, itemLayoutResId);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie film = movieList.get(position);
        holder.titleTextView.setText(film.getTitle());
        holder.dateResult.setText(film.getReleaseDate().substring(0,4));

        if (holder.scoreTextView != null) {
            holder.scoreTextView.setText(String.format(Locale.US, "%.1f", film.getVoteAverage()));
        }


        String imageUrl = "https://image.tmdb.org/t/p/w500" + film.getPosterPath();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.unavailable)
                .into(holder.posterImageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(film);
            }
        });
    }

    @Override
    public int getItemCount() {
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

            if (layoutResId == R.layout.movie_item) {
                posterImageView = itemView.findViewById(R.id.imageView);
                titleTextView = itemView.findViewById(R.id.nameTxt);
                scoreTextView = itemView.findViewById(R.id.scoreTxt);
                dateResult = itemView.findViewById(R.id.year);
                genreTextView = itemView.findViewById(R.id.genreMovie);

            } else if (layoutResId == R.layout.search_item) {
                posterImageView = itemView.findViewById(R.id.search_item_poster);
                titleTextView = itemView.findViewById(R.id.search_item_title);
                scoreTextView = itemView.findViewById(R.id.scoreResult);
                dateResult = itemView.findViewById(R.id.search_date);
                genreTextView = itemView.findViewById(R.id.genreSearch);

            } else {

                Log.e("AdapterMovie", "Unknown layout ID: " + layoutResId);

            }
        }


    }

    public void updateMovies(List<Movie> newMovies) {
        this.originalMovieList.clear();
        this.originalMovieList.addAll(newMovies);

        this.movieList.clear(); // Golește lista afișată în prezent
        this.movieList.addAll(newMovies); // Adaugă toate filmele noi pentru a fi afișate inițial

        notifyDataSetChanged();
    }

    public void filterByGenre(List<String> selectedGenreNames) {
        movieList.clear(); // Golește lista afișată în prezent

        if (selectedGenreNames == null || selectedGenreNames.isEmpty()) {
            // Dacă nu sunt genuri selectate, afișează toate filmele din lista originală
            movieList.addAll(originalMovieList);
        } else {
            for (Movie movie : originalMovieList) {
                // Verifică dacă filmul are genuri și dacă vreunul dintre genurile sale
                // este prezent în lista de nume de genuri selectate.
                if (movie.getGenres() != null) {
                    for (Genre movieGenre : movie.getGenres()) {
                        if (selectedGenreNames.contains(movieGenre.getName())) {
                            movieList.add(movie);
                            break; // Adaugă filmul o singură dată dacă se potrivește cu un gen
                        }
                    }
                }
            }
        }
        notifyDataSetChanged(); // Notifică RecyclerView-ul că datele s-au schimbat
    }


    public void resetFilter() {
        movieList.clear();
        movieList.addAll(originalMovieList);
        notifyDataSetChanged();
    }
}