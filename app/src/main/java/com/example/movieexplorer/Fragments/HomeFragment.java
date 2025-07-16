package com.example.movieexplorer.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieexplorer.Fragments.AccountFragment;
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
    private RecyclerView recyclerViewNowPlaying, recyclerTrending, recyclerViewUpcoming, recyclerViewTopRated, recyclerViewPopular;
    private AdapterMovie adapterNowPlaying, adapterTrending, adapterUpcoming, adapterTopRated, adapterPopular;
    private List<Movie> movieListNowPlaying, movieListTrending, movieListUpcoming, movieListTopRated, movieListPopular;
    private RequestQueue mRequestQueue;
    private EditText searchInput;
    private ImageButton account;
    private static final String API_KEY = "0ae44dc58ea8023c0d00a94e0cb0a0c9";
    private static final String TRENDING_MOVIES_URL = "https://api.themoviedb.org/3/trending/movie/day?api_key=";
    private static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=";
    private static final String UPCOMING_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key=";
    private static final String TOP_RATED_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=";
    private static final String POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?api_key=";
    private static final String SEARCH_MOVIES_URL = "https://api.themoviedb.org/3/search/movie?api_key=";
    private static final String GENRE_ACTION_ID = "28";
    private static final String GENRE_ADVENTURE_ID = "12";
    private static final String GENRE_ANIMATION_ID = "16";
    private static final String GENRE_COMEDY_ID = "35";
    private static final String GENRE_CRIME_ID = "80";
    private static final String GENRE_DOCUMENTARY_ID = "99";
    private static final String GENRE_DRAMA_ID = "18";
    private static final String GENRE_FAMILY_ID = "10751";
    private static final String GENRE_FANTASY_ID = "14";
    private static final String GENRE_HISTORY_ID = "36";
    private static final String GENRE_HORROR_ID = "27";
    private static final String GENRE_MUSIC_ID = "10402";
    private static final String GENRE_MYSTERY_ID = "9648";
    private static final String GENRE_ROMANCE_ID = "10749";
    private static final String GENRE_SCIENCE_FICTION_ID = "878";
    private static final String GENRE_THRILLER_ID = "53";
    private static final String GENRE_WESTERN_ID = "37";
    public static final String TAG = "HomeFragment";

    //This method creates and returns the view for the fragment using the "homepage" layout
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage, container, false);
        //This part sets up multiple horizontal RecyclerViews for different movie categories,
        recyclerViewNowPlaying = view.findViewById(R.id.recyclerViewNowPlaying);
        recyclerViewNowPlaying.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        movieListNowPlaying = new ArrayList<>();
        adapterNowPlaying = new AdapterMovie(movieListNowPlaying, this, R.layout.movie_item);
        recyclerViewNowPlaying.setAdapter(adapterNowPlaying);
        recyclerTrending = view.findViewById(R.id.recyclerViewTrending);
        recyclerTrending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        movieListTrending = new ArrayList<>();
        adapterTrending = new AdapterMovie(movieListTrending, this, R.layout.movie_item);
        recyclerTrending.setAdapter(adapterTrending);
        recyclerViewUpcoming = view.findViewById(R.id.recyclerViewUpcoming);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        movieListUpcoming = new ArrayList<>();
        adapterUpcoming = new AdapterMovie(movieListUpcoming, this, R.layout.movie_item);
        recyclerViewUpcoming.setAdapter(adapterUpcoming);
        recyclerViewPopular = view.findViewById(R.id.recyclerViewPopular);
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        movieListPopular = new ArrayList<>();
        adapterPopular = new AdapterMovie(movieListPopular, this, R.layout.movie_item);
        recyclerViewPopular.setAdapter(adapterPopular);
        recyclerViewTopRated = view.findViewById(R.id.recyclerViewTopRated);
        recyclerViewTopRated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        movieListTopRated = new ArrayList<>();
        adapterTopRated = new AdapterMovie(movieListTopRated, this, R.layout.movie_item);
        recyclerViewTopRated.setAdapter(adapterTopRated);
        //This part prepares the search input and request queue
        mRequestQueue = Volley.newRequestQueue(requireContext());
        searchInput = view.findViewById(R.id.search_input);
        setupSearchInput(view);
        //This part sets up buttons for selecting movie genres.
        setupButton(view, R.id.action, GENRE_ACTION_ID, "Action");
        setupButton(view, R.id.adventure, GENRE_ADVENTURE_ID, "Adventure");
        setupButton(view, R.id.animation, GENRE_ANIMATION_ID, "Animation");
        setupButton(view, R.id.comedy, GENRE_COMEDY_ID, "Comedy");
        setupButton(view, R.id.crime, GENRE_CRIME_ID, "Crime");
        setupButton(view, R.id.documentary, GENRE_DOCUMENTARY_ID, "Documentary");
        setupButton(view, R.id.drama, GENRE_DRAMA_ID, "Drama");
        setupButton(view, R.id.family, GENRE_FAMILY_ID, "Family");
        setupButton(view, R.id.fantasy, GENRE_FANTASY_ID, "Fantasy");
        setupButton(view, R.id.history, GENRE_HISTORY_ID, "History");
        setupButton(view, R.id.horror, GENRE_HORROR_ID, "Horror");
        setupButton(view, R.id.music, GENRE_MUSIC_ID, "Musical");
        setupButton(view, R.id.mystery, GENRE_MYSTERY_ID, "Mystery");
        setupButton(view, R.id.romance, GENRE_ROMANCE_ID, "Romance");
        setupButton(view, R.id.science_fiction, GENRE_SCIENCE_FICTION_ID, "SF");
        setupButton(view, R.id.thriller, GENRE_THRILLER_ID, "Thriller");
        setupButton(view, R.id.western, GENRE_WESTERN_ID, "Western");
        //This part sets up button for account
        account = view.findViewById(R.id.account_btn);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountFragment accountFragment = new AccountFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLayout, accountFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });



        return view;
    }


    // This method runs when the fragment becomes visible again: it clears the search input if needed,
    // reloads movie data if the input is empty, and hides the keyboard to keep the UI clean.
    @Override
    public void onResume() {
        super.onResume();
        // Check if the search input exists and is not empty
        if (searchInput != null && !searchInput.getText().toString().isEmpty()) {
            searchInput.setText("");
            // If search input exists and is empty
        } else if (searchInput != null && searchInput.getText().toString().isEmpty()) {
            // Load movies from diverse categories
            fetchMovies(NOW_PLAYING_URL + API_KEY, adapterNowPlaying, movieListNowPlaying, "Now Playing");
            fetchMovies(TRENDING_MOVIES_URL + API_KEY, adapterTrending, movieListTrending, "Trending");
            fetchMovies(UPCOMING_URL + API_KEY, adapterUpcoming, movieListUpcoming, "Upcoming");
            fetchMovies(POPULAR_URL + API_KEY, adapterPopular, movieListPopular, "Popular");
            fetchMovies(TOP_RATED_URL + API_KEY, adapterTopRated, movieListTopRated, "Top Rated");
        }
        // Hide the keyboard if it's open
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    // This method sets up the search input behavior: it listens for search actions from the keyboard,
    // reloads movie data when the input is cleared, and activates the search input when the search area is clicked.
    private void setupSearchInput(View rootView) {
        // Listen for search action from keyboard (like pressing Enter or Search)
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                }
                // Start search with the text entered by the user
                performSearchAndNavigate(searchInput.getText().toString());
                return true;
            }
            return false;
        });
        // Listen for text changes in the search input
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If the text was deleted and is now empty
                if (s.length() == 0) {
                    // Reload all movie categories
                    fetchMovies(NOW_PLAYING_URL + API_KEY, adapterNowPlaying, movieListNowPlaying, "Now Playing");
                    fetchMovies(TRENDING_MOVIES_URL + API_KEY, adapterTrending, movieListTrending, "Trending");
                    fetchMovies(UPCOMING_URL + API_KEY, adapterUpcoming, movieListUpcoming, "Upcoming");
                    fetchMovies(POPULAR_URL + API_KEY, adapterPopular, movieListPopular, "Popular");
                    fetchMovies(TOP_RATED_URL + API_KEY, adapterTopRated, movieListTopRated, "Top Rated");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        // Make the whole search area clickable to activate the input
        View searchLayout = rootView.findViewById(R.id.search_layout);
        if (searchLayout != null) {
            searchLayout.setOnClickListener(v -> {
                activateSearchInput(); // Focus the search input
            });
        }
    }

    private void setupButton(View rootView, int buttonId, String genreId, String genreName) {
        Button button = rootView.findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> {
                if (getParentFragmentManager() != null) {
                    GenreFragment genreMovieFragment = GenreFragment.newInstance(genreId, genreName);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentLayout, genreMovieFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else {
            Log.e(TAG, "Error");
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


    private void performSearchAndNavigate(String query) {
        if (query.trim().isEmpty()) {
            Toast.makeText(getContext(), "Search", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), "Error parsing search data.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("HomeFragment", error.getMessage());
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });

            stringRequest.setTag(this);
            mRequestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("HomeFragment", "Error encoding URL: " + e.getMessage());
            Toast.makeText(getContext(), "Error processing search.", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchMovies(String url, final AdapterMovie adapter, final List<Movie> movieList, final String categoryName) {
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
                            adapter.notifyDataSetChanged();

                            if (fetchedMovies.isEmpty()) {
                                Log.d(TAG, "No movies found for " + categoryName);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error for " + categoryName + " movies: " + e.getMessage());
                            Toast.makeText(getContext(), "Error parsing " + categoryName + " data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error for " + categoryName + " movies: " + error.getMessage());
                Toast.makeText(getContext(), "Network or movie loading error" + categoryName + ".", Toast.LENGTH_SHORT).show();
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