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
import com.example.movieexplorer.Domain.MovieItem;
import com.example.movieexplorer.R;
import com.google.gson.Gson;

import java.util.Locale;

public class DetailFragment extends Fragment {

    private static final String ARG_FILM_ID = "id"; // C
    private RequestQueue mRequestQueue;
    private TextView title, overview, releaseDate, score, runtime;
    private int idFilm;
    private ImageView background, poster;
    private StringRequest mStringReguest;

    // Cheia API de la TMDB
    private static final String API_KEY = "0ae44dc58ea8023c0d00a94e0cb0a0c9";
    private static final String BASE_DETAIL_URL = "https://api.themoviedb.org/3/movie/";

    // Metodă statică pentru a crea o instanță a fragmentului cu argumente
    public static DetailFragment newInstance(int filmId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FILM_ID, filmId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Extrage ID-ul filmului din argumente
        if (getArguments() != null) {
            idFilm = getArguments().getInt(ARG_FILM_ID, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Aloca layout-ul pentru acest fragment
        View view = inflater.inflate(R.layout.info_page, container, false);
        initView(view); // Inițializăm view-urile
        sendRequest(); // Trimitem cererea API pentru detalii
        return view;
    }

    private void sendRequest() {
        mRequestQueue = Volley.newRequestQueue(requireContext());

        // Construiește URL-ul complet pentru detalii film
        String url = BASE_DETAIL_URL + idFilm + "?api_key=" + API_KEY;

        mStringReguest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                MovieItem item = gson.fromJson(response, MovieItem.class);

                // Baza URL pentru imagini și dimensiuni
                String baseUrlImage = "https://image.tmdb.org/t/p/";
                String posterSize = "w500"; // Dimensiune pentru poster
                String backdropSize = "w1280"; // Dimensiune pentru imaginea de fundal

                // Încărcă imaginile cu Glide, verificând nullability
                if (poster != null && item.getPosterPath() != null) {
                    Glide.with(requireContext())
                            .load(baseUrlImage + posterSize + item.getPosterPath())
                            .into(poster);
                } else {
                    poster.setImageResource(R.drawable.ic_launcher_background); // Fallback
                }

                if (background != null && item.getBackdropPath() != null) {
                    Glide.with(requireContext())
                            .load(baseUrlImage + backdropSize + item.getBackdropPath())
                            .into(background);
                } else {
                    background.setImageResource(R.drawable.ic_launcher_background); // Fallback
                }


                // Setează textul pentru TextView-uri, verificând nullability
                if (title != null) title.setText(item.getTitle());
                if (overview != null) overview.setText(item.getOverview());
                if (releaseDate != null) releaseDate.setText(item.getReleaseDate());
                if (score != null) score.setText(String.format(Locale.US, "%.1f", item.getVoteAverage()));
                if (runtime != null) runtime.setText(String.valueOf(item.getRuntime()));

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

    // Inițializează view-urile din layout-ul fragmentului
    private void initView(View view) {
        title = view.findViewById(R.id.titleDetail);
        overview = view.findViewById(R.id.overview);
        releaseDate = view.findViewById(R.id.release_date);
        score = view.findViewById(R.id.scoreDetail);
        background = view.findViewById(R.id.backgroundMovie);
        poster = view.findViewById(R.id.posterDetail);
        runtime = view.findViewById(R.id.runtime);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Anulează cererile Volley pentru a preveni memory leaks
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }
}