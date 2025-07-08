package com.example.movieexplorer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // pentru încărcarea imaginilor
import com.example.movieexplorer.Activity.DetailActivity;
import com.example.movieexplorer.Activity.Movie;
import com.example.movieexplorer.R;

import java.util.List;
import java.util.Locale;

public class AdapterMovie extends RecyclerView.Adapter<AdapterMovie.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;

    public AdapterMovie(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.title.setText(movie.getTitle());

        float scoreFloat = Float.parseFloat(movie.getScore());
        String formattedScore = String.format(Locale.US, "%.1f", scoreFloat);
        holder.score.setText(formattedScore);



        // URL complet poster (TMDB poster base URL + posterPath)
        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getImg();

        Glide.with(context)
                .load(posterUrl)
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title, score;
        ImageView img;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.nameTxt);
            score = itemView.findViewById(R.id.scoreTxt);
            img = itemView.findViewById(R.id.imageView);
        }
    }
}
