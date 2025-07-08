package com.example.movieexplorer.Activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieexplorer.Adapter.AdapterMovie;
import com.example.movieexplorer.Fragments.HomeFragment;
import com.example.movieexplorer.R;
import com.example.movieexplorer.Fragments.WatchListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements Data.DataListener{

    private RecyclerView recyclerView;
    private List<Movie> movieList = new ArrayList<>();
    private AdapterMovie adapterMovie;
    private final String API_KEY = "0ae44dc58ea8023c0d00a94e0cb0a0c9";
    private final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;

    ImageButton homePageBtn, watchListPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapterMovie = new AdapterMovie(this, movieList);
        recyclerView.setAdapter(adapterMovie);

        fetchMovies();

        // Set up buttons
        homePageBtn = findViewById(R.id.button_homepage);
        watchListPageBtn = findViewById(R.id.button_watchlist);

        // Home button click
        homePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePageBtn.setSelected(true);
                watchListPageBtn.setSelected(false);

                updateButtonColor(homePageBtn, true);
                updateButtonColor(watchListPageBtn, false);

                // Show HomeFragment
                replaceFragment(new HomeFragment());
            }
        });

        // Watchlist button click
        watchListPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchListPageBtn.setSelected(true);
                homePageBtn.setSelected(false);

                updateButtonColor(watchListPageBtn, true);
                updateButtonColor(homePageBtn, false);

                // Show WatchListFragment
                replaceFragment(new WatchListFragment());
            }
        });
    }

    // Change button color if selected
    private void updateButtonColor(ImageButton button, boolean selected) {
        if (selected) {
            button.setColorFilter(getResources().getColor(R.color.lightBlue), PorterDuff.Mode.SRC_IN);
        } else {
            button.clearColorFilter();
        }
    }



    // Replace current fragment with a new one
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onDataLoaded(List<Movie> movies) {
        movieList.clear();
        movieList.addAll(movies);
        adapterMovie.notifyDataSetChanged();
    }


    private void fetchMovies() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {  // apel asincron
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TMDB", "Request Failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String jsonData = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray results = jsonObject.getJSONArray("results");

                        movieList.clear();

                        for(int i=0; i<results.length(); i++){
                            JSONObject movieObj = results.getJSONObject(i);
                            String title = movieObj.getString("title");
                            String score = movieObj.getString("vote_average");
                            String posterPath = movieObj.getString("poster_path");

                            movieList.add(new Movie(title, score, posterPath));
                        }

                        // Actualizare RecyclerView pe thread-ul UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapterMovie.notifyDataSetChanged();
                            }
                        });

                    } catch (Exception e){
                        Log.e("TMDB", "JSON parsing error", e);
                    }
                }
            }
        });
    }


}