package com.example.movieexplorer.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieexplorer.Adapter.AdapterMovie;
import com.example.movieexplorer.Domain.MovieItem;
import com.example.movieexplorer.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterMovie.OnItemClickListener {

    private RecyclerView recyclerViewMovies;
    private AdapterMovie adapterMovie;
    private List<MovieItem> movieList;
    private RequestQueue mRequestQueue;

    // Cheia ta API de la TMDB
    private static final String API_KEY = "0ae44dc58ea8023c0d00a94e0cb0a0c9";
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key=";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage, container, false);

        recyclerViewMovies = view.findViewById(R.id.recyclerView);
        recyclerViewMovies.setLayoutManager(new GridLayoutManager(getContext(), 3));

        movieList = new ArrayList<>();
        adapterMovie = new AdapterMovie(movieList, this); // "this" deoarece HomeFragment implementează OnItemClickListener
        recyclerViewMovies.setAdapter(adapterMovie);

        mRequestQueue = Volley.newRequestQueue(requireContext());

        fetchPopularMovies(); // Inițializează cererea API

        return view;
    }

    private void fetchPopularMovies() {
        String url = BASE_URL + API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            // TMDB returnează filmele într-un array JSON numit "results"
                            String resultsArray = jsonResponse.getJSONArray("results").toString();

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<MovieItem>>() {}.getType();
                            List<MovieItem> fetchedMovies = gson.fromJson(resultsArray, listType);

                            // Adaugă filmele la lista existentă și notifică adaptorul
                            movieList.clear(); // Golește lista veche
                            movieList.addAll(fetchedMovies);
                            adapterMovie.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e("HomeFragment", "JSON Parsing error: " + e.getMessage());
                            Toast.makeText(getContext(), "Eroare la parsarea datelor filmelor.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HomeFragment", "Volley Error: " + error.getMessage());
                Toast.makeText(getContext(), "Eroare la încărcarea filmelor.", Toast.LENGTH_SHORT).show();
            }
        });

        mRequestQueue.add(stringRequest);
    }

    @Override
    public void onItemClick(MovieItem film) {
        // Când un film este apăsat, vrem să deschidem DetailFragment
        if (getFragmentManager() != null) {
            // Trimitem ID-ul filmului către DetailFragment
            DetailFragment detailFragment = DetailFragment.newInstance(film.getId());

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentLayout, detailFragment) // "fragment_container" este ID-ul FrameLayout-ului din MainActivity
                    .addToBackStack(null) // Permite navigarea înapoi cu butonul "back"
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Anulează cererile Volley pentru a preveni memory leaks
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this); // Poți folosi un tag pentru cereri, dar this e suficient aici
        }
    }
}