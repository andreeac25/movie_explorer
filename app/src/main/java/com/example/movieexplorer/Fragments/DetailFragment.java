package com.example.movieexplorer.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

public class DetailFragment extends Fragment {

    private static final String FILM_ID = "id";
    private RequestQueue mRequestQueue;
    private TextView title, overview, releaseDate, score, runtime, categories;
    private int idFilm;
    private ImageView background, poster, backBtn;
    private StringRequest mStringReguest;
    private ImageButton saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Movie currentMovie;
    private boolean isMovieSaved = false;
    private static final String API_KEY = "0ae44dc58ea8023c0d00a94e0cb0a0c9";
    private static final String BASE_DETAIL_URL = "https://api.themoviedb.org/3/movie/";



    // This method creates a new DetailFragment instance with a specific film ID.
    public static DetailFragment newInstance(int filmId) {
        // Create a new instance of the fragment
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        // Put the movie ID into the bundle with a specific key (FILM_ID)
        args.putInt(FILM_ID, filmId);
        // Attach the arguments to the fragment
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
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            currentMovie = (Movie) getArguments().getSerializable("movie_object_key");
        }
    }

    // This method inflates the fragment's UI and initializes its components.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment from the XML file (info_page.xml)
        View view = inflater.inflate(R.layout.info_page, container, false);
        // Find the save button in the layout and assign it
        saveButton = view.findViewById(R.id.button_save_watchlist);
        // Set a click listener for the button to handle saving/removing the movie from the watchlist
        saveButton.setOnClickListener(v -> toggleMovieSavedStatus());
        // Initialize the rest of the view components (e.g., TextViews, ImageViews, etc.)
        initView(view);
        // Send a request to load movie details or data from the server or database
        sendRequest();
        // Return the fully configured view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Check if the back button view was properly initialized
        if (backBtn != null) {
            // Set a click listener for the back button
            backBtn.setOnClickListener(v -> {
                // If there are fragments in the back stack, pop the last one (go back)
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {
                    // If no fragments are in the back stack, close the activity
                    requireActivity().finish();
                }
            });
        } else {
            // Log an error if the back button was not found or is null
            Log.e("DetailFragment", "Error");
        }
    }

    // This method sends a network request to fetch movie details.
    private void sendRequest() {
        // Initialize the Volley request queue with the current context
        mRequestQueue = Volley.newRequestQueue(requireContext());
        // Construct the API request URL using the base URL, movie ID, and API key
        String url = BASE_DETAIL_URL + idFilm + "?api_key=" + API_KEY;
        // Create a StringRequest to fetch data from the API
        mStringReguest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            // This block handles the successful response from the API.
            @Override
            public void onResponse(String response) {
                // Parse the JSON response into a Movie object using Gson
                Gson gson = new Gson();
                Movie item = gson.fromJson(response, Movie.class);
                currentMovie = item;
                // Check if the movie is already saved in the user's watchlist
                checkMovieSavedStatus();
                // Base URL and sizes for images (poster and backdrop)
                String baseUrlImage = "https://image.tmdb.org/t/p/";
                String posterSize = "w500";
                String backdropSize = "w1280";
                // Load the movie poster image using Glide, or show fallback if unavailable
                if (poster != null && item.getPosterPath() != null) {
                    // Loads the movie poster image using Glide.
                    Glide.with(requireContext())
                            .load(baseUrlImage + posterSize + item.getPosterPath())
                            .into(poster);
                } else {
                    poster.setImageResource(R.drawable.unavailable); //Sets a fallback image if no poster is available.
                }
                // Load the backdrop image using Glide, or show fallback if unavailable
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
                    // Set the movie genres (categories), separated by commas
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
            // This block handles any errors that occur during the API request
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Log the error and show a toast message to the user
                Log.e("DetailFragment", "Volley Error: " + volleyError.getMessage());
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the Volley request queue to execute it
        mRequestQueue.add(mStringReguest);
    }


    private void initView(View view) {
        // Method to initialize all the UI components from the provided view
        title = view.findViewById(R.id.titleDetail);
        overview = view.findViewById(R.id.overview);
        releaseDate = view.findViewById(R.id.release_date);
        score = view.findViewById(R.id.scoreDetail);
        background = view.findViewById(R.id.backgroundMovie);
        poster = view.findViewById(R.id.posterDetail);
        runtime = view.findViewById(R.id.runtime);
        categories = view.findViewById(R.id.categories);
        backBtn = view.findViewById(R.id.buttonBack);
    }

    // Method to check whether the current movie is already saved in the user's watchlist
    private void checkMovieSavedStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // If the user is not logged in or the movie data is invalid, set to "not saved" state
        if (currentUser == null || currentMovie == null || currentMovie.getId() == null) {
            // If the user is not logged in or the movie data is invalid, set to "not saved" state
            saveButton.setImageResource(R.drawable.list);
            isMovieSaved = false;
            return;
        }
        // Get the current user's UID and the movie's ID (used as the document ID)
        String userId = currentUser.getUid();
        String movieIdDocumentId = String.valueOf(currentMovie.getId());
        // Reference to the specific document for the movie in the user's watchlist
        DocumentReference docRef = db.collection("users")
                .document(userId)
                .collection("watchlist")
                .document(movieIdDocumentId);
        // Attempt to retrieve the document to check if it exists (i.e., movie is saved)
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // The movie is already saved in the watchlist
                saveButton.setImageResource(R.drawable.bookmark);
                isMovieSaved = true;
            } else {
                // The movie is not saved
                saveButton.setImageResource(R.drawable.list);
                isMovieSaved = false;
            }
        }).addOnFailureListener(e -> {
            // Log the error if something goes wrong while checking
            Log.e("DetailsFragment", "Error" + e.getMessage());
            // Assume the movie is not saved in case of failure
            saveButton.setImageResource(R.drawable.list);
            isMovieSaved = false;
            // Show a toast to inform the user of the error
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        });
    }

    // Method to toggle the saved status of the current movie in the user's watchlist
    private void toggleMovieSavedStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check if the user is logged in; if not, show a message and exit
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to save movies!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the current movie or its ID is valid; if not, show a message and exit
        if (currentMovie == null || currentMovie.getId() == null) {
            Toast.makeText(getContext(), "You must be logged in to save movies!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the user ID and use the movie ID as the Firestore document ID
        String userId = currentUser.getUid();
        String movieIdDocumentId = String.valueOf(currentMovie.getId());

        if (isMovieSaved) {
            // If the movie is already saved, remove it from the watchlist
            db.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .document(movieIdDocumentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        isMovieSaved = false;
                        saveButton.setImageResource(R.drawable.list); // Setează iconița cu contur
                        Toast.makeText(getContext(), "Movie removed from watchlist!", Toast.LENGTH_SHORT).show();
                    })
                    // Update UI and state after successful removal
                    .addOnFailureListener(e -> {
                        Log.e("DetailsFragment", "Movie removed from watchlist!" + e.getMessage());
                        Toast.makeText(getContext(), "Error deleting movie.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Log error and show a failure message
            db.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .document(movieIdDocumentId)
                    .set(currentMovie, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        // Update UI and state after successful addition
                        isMovieSaved = true;
                        saveButton.setImageResource(R.drawable.bookmark);
                        Toast.makeText(getContext(), "Movie saved to watchlist!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Log error and show a failure message
                        Log.e("DetailsFragment", "Error saving movie:" + e.getMessage()); // Displays a message for the developer
                        Toast.makeText(getContext(), "Error saving movie:", Toast.LENGTH_SHORT).show(); // displays a message to the user
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel all pending requests in the Volley request queue associated with this fragment
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }
}