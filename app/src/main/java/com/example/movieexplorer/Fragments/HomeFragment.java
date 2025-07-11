package com.example.movieexplorer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.example.movieexplorer.Domain.Movie;
import com.example.movieexplorer.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterMovie.OnItemClickListener {

    private RecyclerView recyclerViewMovies;
    private AdapterMovie adapterMovie;
    private List<Movie> movieList;
    private RequestQueue mRequestQueue;
    private EditText searchInput;

    private static final String API_KEY = "0ae44dc58ea8023c0d00a94e0cb0a0c9";
    private static final String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular?api_key=";
    private static final String SEARCH_MOVIES_URL = "https://api.themoviedb.org/3/search/movie?api_key=";
    public static final String TAG = "HomeFragment";

    private boolean hasLoadedPopularMovies = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage, container, false);

        recyclerViewMovies = view.findViewById(R.id.recyclerView);
        recyclerViewMovies.setLayoutManager(new GridLayoutManager(getContext(), 3));

        movieList = new ArrayList<>();
        adapterMovie = new AdapterMovie(movieList, this, R.layout.movie_item);
        recyclerViewMovies.setAdapter(adapterMovie);

        mRequestQueue = Volley.newRequestQueue(requireContext());

        searchInput = view.findViewById(R.id.search_input);

        setupSearchInput(view);

        // fetchPopularMovies() este acum apelat în onResume()

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (searchInput != null && !searchInput.getText().toString().isEmpty()) {
            searchInput.setText("");

        } else if (searchInput != null && searchInput.getText().toString().isEmpty()) {

            fetchPopularMovies();
        }

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }


    private void setupSearchInput(View rootView) {
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                }

                performSearchAndNavigate(searchInput.getText().toString());
                return true;
            }
            return false;
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    fetchPopularMovies();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        View searchLayout = rootView.findViewById(R.id.search_layout);
        if (searchLayout != null) {
            searchLayout.setOnClickListener(v -> {
                activateSearchInput();
            });
        }
    }


    public void activateSearchInput() {
        if (searchInput != null) {
            searchInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }


    private void fetchPopularMovies() {
        String url = POPULAR_MOVIES_URL + API_KEY;
        makeMovieRequest(url);
    }

    private void performSearchAndNavigate(String query) {
        if (query.trim().isEmpty()) {
            Toast.makeText(getContext(), "Introduceți un termen de căutare.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String url = SEARCH_MOVIES_URL + API_KEY + "&query=" + encodedQuery;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String resultsArray = jsonResponse.getJSONArray("results").toString();

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Movie>>() {}.getType();
                                List<Movie> fetchedMovies = gson.fromJson(resultsArray, listType);

                                if (getParentFragmentManager() != null) {
                                    SearchResultFragment searchResultsFragment = SearchResultFragment.newInstance(fetchedMovies, query);
                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragmentLayout, searchResultsFragment)
                                            .addToBackStack(null)
                                            .commit();
                                }

                            } catch (JSONException e) {
                                Log.e("HomeFragment", "JSON Parsing error for search: " + e.getMessage());
                                Toast.makeText(getContext(), "Eroare la parsarea datelor căutării.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("HomeFragment", "Volley Error for search: " + error.getMessage());
                    Toast.makeText(getContext(), "Eroare de rețea sau la căutarea filmelor.", Toast.LENGTH_SHORT).show();
                }
            });

            stringRequest.setTag(this);
            mRequestQueue.add(stringRequest);

        } catch (Exception e) {
            Log.e("HomeFragment", "Error encoding URL: " + e.getMessage());
            Toast.makeText(getContext(), "Eroare la procesarea căutării.", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeMovieRequest(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String resultsArray = jsonResponse.getJSONArray("results").toString();

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Movie>>() {}.getType();
                            List<Movie> fetchedMovies = gson.fromJson(resultsArray, listType);

                            movieList.clear();
                            movieList.addAll(fetchedMovies);
                            adapterMovie.notifyDataSetChanged();

                            if (fetchedMovies.isEmpty() && searchInput.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Empty list", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Log.e("HomeFragment", "JSON Parsing error for popular movies: " + e.getMessage());
                            Toast.makeText(getContext(), "Error parsing popular data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HomeFragment", "Volley Error for popular movies: " + error.getMessage());
                Toast.makeText(getContext(), "Eroare de rețea sau la încărcarea filmelor populare.", Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setTag(this);
        mRequestQueue.add(stringRequest);
    }

    @Override
    public void onItemClick(Movie film) {
        if (getParentFragmentManager() != null) {
            DetailFragment detailFragment = DetailFragment.newInstance(film.getId());

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentLayout, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }
}