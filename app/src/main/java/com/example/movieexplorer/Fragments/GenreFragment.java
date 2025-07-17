package com.example.movieexplorer.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieexplorer.Adapter.AdapterMovie;
import com.example.movieexplorer.Domain.Movie;
import com.example.movieexplorer.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GenreFragment extends Fragment implements AdapterMovie.OnItemClickListener{


    private static final String GENRE_ID = "genre_id";
    private static final String GENRE_NAME = "genre_name";
    private static final String API_KEY = "0ae44dc58ea8023c0d00a94e0cb0a0c9";
    private static final String GENRES_URL = "https://api.themoviedb.org/3/discover/movie?api_key=";

    private String genreId;
    private String genreName;
    private RecyclerView recyclerViewGenreMovies;
    private AdapterMovie adapterGenreMovies;
    private List<Movie> movieListGenre;
    private RequestQueue mRequestQueue;
    private TextView genreTitleTextView;
    private ImageButton backButtonGenres;

    public GenreFragment() {}

    // Factory method to create a new instance of GenreFragment with genreId and genreName
    public static GenreFragment newInstance(String genreId, String genreName) {
        GenreFragment fragment = new GenreFragment();
        Bundle args = new Bundle();
        args.putString(GENRE_ID, genreId);
        args.putString(GENRE_NAME, genreName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve arguments passed via newInstance
        if (getArguments() != null) {
            genreId = getArguments().getString(GENRE_ID);
            genreName = getArguments().getString(GENRE_NAME);
        }
        // Initialize Volley request queue for network operations
        mRequestQueue = Volley.newRequestQueue(requireContext());
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        recyclerViewGenreMovies = view.findViewById(R.id.recyclerViewGenre);
        // Setup RecyclerView with GridLayoutManager to display movies in grid format
        recyclerViewGenreMovies.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // Initialize the list that will hold movies of this genre
        movieListGenre = new ArrayList<>();
        // Initialize the adapter and set it to RecyclerView
        adapterGenreMovies = new AdapterMovie(movieListGenre, this);
        recyclerViewGenreMovies.setAdapter(adapterGenreMovies);
        // Initialize the back button for this fragment
        genreTitleTextView = view.findViewById(R.id.genreTitleTextView);
        if (genreName != null) {
            genreTitleTextView.setText(genreName + " Movies");
        }
        backButtonGenres = view.findViewById(R.id.buttonBackGenres);
        // Fetch movies that belong to the selected genre
        fetchMoviesByGenre();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set click listener for back button to handle navigation
        if (backButtonGenres != null) {
            backButtonGenres.setOnClickListener(v -> {
                // If there are fragments in back stack, pop the last one
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {
                    // If no back stack, finish the activity
                    requireActivity().finish();
                }
            });
        } else {
            Log.e("DetailFragment", "Error");
        }
    }

    // Method to fetch movies of the selected genre using TMDB API
    private void fetchMoviesByGenre() {
        // Validate genreId before making the request
        if (genreId == null || genreId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Genre ID not provided", Toast.LENGTH_SHORT).show();
            return;
        }
        // Construct the URL for fetching movies by genre using API key and genreId
        String url = GENRES_URL + API_KEY + "&with_genres=" + genreId;
        // Create a StringRequest for the GET method
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the JSON response
                            JSONObject jsonResponse = new JSONObject(response);
                            // Extract the "results" JSON array as a string
                            String resultsArray = jsonResponse.getJSONArray("results").toString();
                            // Use Gson to convert JSON array string to List<Movie>
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Movie>>() {
                            }.getType();
                            List<Movie> fetchedMovies = gson.fromJson(resultsArray, listType);
                            // Clear current list and add new movies
                            movieListGenre.clear();
                            movieListGenre.addAll(fetchedMovies);
                            // Notify adapter that data set has changed to update UI
                            adapterGenreMovies.notifyDataSetChanged();
                            // If no movies found, notify user
                            if (fetchedMovies.isEmpty()) {
                                Toast.makeText(getContext(), "Not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("GenreFragment", "JSON Parsing error for genre movies: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log and show error toast if network request fails
                Log.e("GenreMovieFragment", "Volley Error for genre movies: " + error.getMessage());
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        // Tag the request with this fragment for easy cancellation later
        stringRequest.setTag(this);
        // Add the request to the Volley request queue
        mRequestQueue.add(stringRequest);
    }

    // Handle item click from AdapterMovie to open DetailFragment with the selected movie
    public void onItemClick(Movie film) {
        if (getParentFragmentManager() != null) {
            DetailFragment detailFragment = DetailFragment.newInstance(film.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentLayout, detailFragment)
                    .addToBackStack(null) // add to back stack so user can navigate back
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel any ongoing Volley requests tagged with this fragment to avoid memory leaks
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }
}