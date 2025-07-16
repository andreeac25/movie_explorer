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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieexplorer.Adapter.AdapterMovie;
import com.example.movieexplorer.Domain.Movie;
import com.example.movieexplorer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class WatchListFragment extends Fragment implements AdapterMovie.OnItemClickListener{

    private RecyclerView recyclerView; // RecyclerView to display the list of movies in the watchlist
    private AdapterMovie movieAdapter; // Adapter for binding movie data to the RecyclerView
    private List<Movie> watchlistMovies; // List to hold the movies in the user's watchlist
    private TextView emptyWatchlistMessage; // TextView to show a message when the watchlist is empty
    private FirebaseFirestore db; // Reference to the Firestore database
    private FirebaseAuth mAuth; // Reference to Firebase Authentication for user management

    // Default constructor (required for fragments)
    public WatchListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firestore database instance
        db = FirebaseFirestore.getInstance();
        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();
        // Initialize the list that will hold the user's watchlist movies
        watchlistMovies = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.watch_list, container, false);
        // Initialize the RecyclerView and empty message TextView from the layout
        recyclerView = view.findViewById(R.id.recyclerViewWatchList);
        emptyWatchlistMessage = view.findViewById(R.id.emptyWatchlistMessage);
        // Set a vertical LinearLayoutManager for displaying list items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Initialize the adapter with the list of movies and the click listener (this fragment)
        // Make sure AdapterMovie is implemented to accept a List<Movie> and a click listener
        movieAdapter = new AdapterMovie(watchlistMovies, this, R.layout.search_item);
        // Attach the adapter to the RecyclerVie
        recyclerView.setAdapter(movieAdapter);
        // Return the fully configured view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Load the user's watchlist as soon as the view is created
        loadWatchlistMovies();
    }

    // Method to load the user's watchlist from Firestore
    private void loadWatchlistMovies() {
        // Get the currently authenticated user from Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Retrieve the user's unique ID
            String userId = currentUser.getUid();
            // Access the "watchlist" subcollection under the current user's document
            db.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Clear the existing list before adding new items
                            watchlistMovies.clear();
                            // Loop through each document retrieved from Firestore
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Movie movie = document.toObject(Movie.class);
                                // Add the movie to the watchlist
                                watchlistMovies.add(movie);
                            }
                            if (watchlistMovies.isEmpty()) {
                                // If the watchlist is empty, show the empty message and hide the RecyclerView
                                emptyWatchlistMessage.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                // Otherwise, hide the empty message and display the list
                                emptyWatchlistMessage.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                movieAdapter.notifyDataSetChanged();
                            }
                        } else {
                            // If the Firestore request failed, log the error and show a toast
                            Log.w("WatchlistFragment", "Error getting documents: ", task.getException());
                            Toast.makeText(getContext(), "Error loading watchlist.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // If the user is not logged in, notify them and show the appropriate message
            Toast.makeText(getContext(), "You are not logged in. Log in to view the watchlist.", Toast.LENGTH_LONG).show();
            emptyWatchlistMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyWatchlistMessage.setText("Log in to see your watchlist.");
        }
    }

    // Method to refresh the watchlist, useful when returning to this fragment
    @Override
    public void onResume() {
        super.onResume();
        // Reload the list of movies in the watchlist
        loadWatchlistMovies();
    }

    @Override
    public void onItemClick(Movie film) {
        // Check if the movie has a valid ID before proceeding
        if (film.getId() != null) {
            // Create a new instance of the DetailFragment with the movie ID
            DetailFragment detailsFragment = DetailFragment.newInstance(film.getId());
            // Ensure the parent fragment manager is available before performing the transaction
            if (getParentFragmentManager() != null) {
                // Replace the current fragment with the DetailFragment
                // and add the transaction to the back stack for navigation
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentLayout, detailsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            // Show a toast message if the movie ID is invalid or null
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
