package com.example.movieexplorer.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieexplorer.Adapter.AdapterMovie;
import com.example.movieexplorer.Domain.Movie;
import com.example.movieexplorer.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment implements AdapterMovie.OnItemClickListener {

    private static final String MOVIE_LIST_JSON = "movie_list";
    private static final String SEARCH_QUERY = "search_query";

    private RecyclerView recyclerViewSearchResults;
    private AdapterMovie adapterSearchResults;
    private List<Movie> searchResultsList;
    private String searchQuery;
    private TextView searchResultsMainTitle;
    private TextView searchQueryDisplay;


    public static SearchResultFragment newInstance(List<Movie> movies, String query) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String jsonMovies = gson.toJson(movies);
        args.putString(MOVIE_LIST_JSON, jsonMovies);
        args.putString(SEARCH_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }


    //Initializes the list for search results and retrieves the movie list and search query from the fragment's arguments.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResultsList = new ArrayList<>();
        if (getArguments() != null) {
            String jsonMovies = getArguments().getString(MOVIE_LIST_JSON);
            searchQuery = getArguments().getString(SEARCH_QUERY);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Movie>>() {}.getType();
            List<Movie> fetchedMovies = gson.fromJson(jsonMovies, listType);
            if (fetchedMovies != null) {
                searchResultsList.addAll(fetchedMovies);
            }
        }
    }


    //Inflates the layout, sets up the search results display including the RecyclerView, and shows a message if no movies are found.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_result, container, false);
        searchResultsMainTitle = view.findViewById(R.id.search_results_main_title);
        searchQueryDisplay = view.findViewById(R.id.tv_search_query_display);

        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchQueryDisplay.setText("Showing results for '" + searchQuery + "'");
        } else {
            searchQueryDisplay.setText("No results");
        }
        recyclerViewSearchResults = view.findViewById(R.id.recyclerViewResult);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterSearchResults = new AdapterMovie(searchResultsList, this, R.layout.search_item);
        recyclerViewSearchResults.setAdapter(adapterSearchResults);
        if (searchResultsList.isEmpty()) {
            Toast.makeText(getContext(), "No movies found for '" + searchQuery + "'.", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    //The method that shows what happens when I tap on the item in the list.
    @Override
    public void onItemClick(Movie film) {
        if (getFragmentManager() != null) {
            DetailFragment detailFragment = DetailFragment.newInstance(film.getId());

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentLayout, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}