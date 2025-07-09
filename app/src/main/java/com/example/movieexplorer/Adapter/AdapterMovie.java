// app/src/main/java/com/example/movieexplorer/Adapter/MovieAdapter.java
package com.example.movieexplorer.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieexplorer.Domain.MovieItem;
import com.example.movieexplorer.R;

import java.util.List;
import java.util.Locale;

public class AdapterMovie extends RecyclerView.Adapter<AdapterMovie.MovieViewHolder> {

    private List<MovieItem> movieList;
    private OnItemClickListener listener; // Interfață pentru click-uri

    // Interfața pentru a comunica click-urile către fragment/activitate
    public interface OnItemClickListener {
        void onItemClick(MovieItem film);
    }

    public AdapterMovie(List<MovieItem> movieList, OnItemClickListener listener) {
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieItem film = movieList.get(position);
        holder.titleTextView.setText(film.getTitle());
        if (holder.scoreTextView != null) {
            holder.scoreTextView.setText(String.format(Locale.US, "%.1f", film.getVoteAverage()));
        }



        // Construiește URL-ul complet pentru imaginea posterului
        // The MovieDB folosește "w500" ca mărime standard pentru postere
        String imageUrl = "https://image.tmdb.org/t/p/w500" + film.getPosterPath();

        // Folosește Glide pentru a încărca imaginea
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background) // Un placeholder vizibil în timp ce se încarcă
                .error(R.drawable.ic_launcher_foreground) // O imagine de eroare dacă nu se încarcă
                .into(holder.posterImageView);

        // Setează listener-ul pentru click pe întregul element
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

    // ViewHolder-ul care deține referințele la view-urile din viewholder_movie.xml
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView, scoreTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.nameTxt);
            scoreTextView = itemView.findViewById(R.id.scoreTxt);
        }
    }
}
