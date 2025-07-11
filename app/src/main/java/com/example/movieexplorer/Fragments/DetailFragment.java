package com.example.movieexplorer.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.movieexplorer.Domain.Genre;
import com.example.movieexplorer.Domain.Movie;
import com.example.movieexplorer.R;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

public class DetailFragment extends Fragment {

    private static final String FILM_ID = "id";
    private RequestQueue mRequestQueue;
    private TextView title, overview, releaseDate, score, runtime, categories;
    private int idFilm;
    private ImageView background, poster;
    private StringRequest mStringReguest;
    private static final String API_KEY = "0ae44dc58ea8023c0d00a94e0cb0a0c9";
    private static final String BASE_DETAIL_URL = "https://api.themoviedb.org/3/movie/";


    // This method creates a new DetailFragment instance with a specific film ID.
    public static DetailFragment newInstance(int filmId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(FILM_ID, filmId);
        fragment.setArguments(args);
        return fragment;
    }

    // This method initializes the fragment and retrieves the film ID from arguments.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            idFilm = getArguments().getInt(FILM_ID, 0);
        }
    }

    // This method inflates the fragment's UI and initializes its components.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_page, container, false);
        initView(view);
        sendRequest();
        return view;
    }

    // This method sends a network request to fetch movie details.
    private void sendRequest() {
        mRequestQueue = Volley.newRequestQueue(requireContext()); // Initializes the Volley request queue.
        String url = BASE_DETAIL_URL + idFilm + "?api_key=" + API_KEY; // Constructs the API URL for movie details.
        mStringReguest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            // This block handles the successful response from the API.
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Movie item = gson.fromJson(response, Movie.class);


                String baseUrlImage = "https://image.tmdb.org/t/p/";
                String posterSize = "w500";
                String backdropSize = "w1280";


                if (poster != null && item.getPosterPath() != null) {
                    // Loads the movie poster image using Glide.
                    Glide.with(requireContext())
                            .load(baseUrlImage + posterSize + item.getPosterPath())
                            .into(poster);
                } else {
                    poster.setImageResource(R.drawable.unavailable); //Sets a fallback image if no poster is available.
                }

                if (background != null && item.getBackdropPath() != null) {
                    // Loads the movie backdrop image using Glide
                    Glide.with(requireContext())
                            .load(baseUrlImage + backdropSize + item.getBackdropPath())
                            .into(background);
                } else {
                    background.setImageResource(R.drawable.unavailable); // Sets a fallback image if no backdrop is available.
                }

                if (title != null) title.setText(item.getTitle());
                if (overview != null) overview.setText(item.getOverview());
                if (releaseDate != null) releaseDate.setText(item.getReleaseDate());
                if (score != null) score.setText(String.format(Locale.US, "%.1f", item.getVoteAverage()));
                if (runtime != null) runtime.setText(String.valueOf(item.getRuntime()));
                if (categories != null) {

                    List<Genre> genres = item.getGenres();
                    if (genres != null && !genres.isEmpty()) {
                        StringBuilder genreText = new StringBuilder();
                        for (int i = 0; i < genres.size(); i++) {
                            genreText.append(genres.get(i).getName());
                            if (i < genres.size() - 1) {
                                genreText.append(", ");
                            }
                        }
                        categories.setText(genreText.toString());
                    } else {
                        categories.setText("N/A");
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("DetailFragment", "Volley Error: " + volleyError.getMessage());
                Toast.makeText(requireContext(), "Eroare la încărcarea detaliilor filmului.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(mStringReguest);
    }


    private void initView(View view) {
        title = view.findViewById(R.id.titleDetail);
        overview = view.findViewById(R.id.overview);
        releaseDate = view.findViewById(R.id.release_date);
        score = view.findViewById(R.id.scoreDetail);
        background = view.findViewById(R.id.backgroundMovie);
        poster = view.findViewById(R.id.posterDetail);
        runtime = view.findViewById(R.id.runtime);
        categories = view.findViewById(R.id.categories);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }
}