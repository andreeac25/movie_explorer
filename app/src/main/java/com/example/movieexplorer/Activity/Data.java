package com.example.movieexplorer.Activity;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Data extends AsyncTask<String, String, List<Movie>> {

    public interface DataListener {
        void onDataLoaded(List<Movie> movies);
    }

    private static final String JSON_URL = "https://api.themoviedb.org/3/trending/movie/week?api_key=...";
    private DataListener listener;

    public Data(DataListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Movie> doInBackground(String... strings) {
        List<Movie> movieList = new ArrayList<>();

        try {
            URL url = new URL(JSON_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObj = jsonArray.getJSONObject(i);

                Movie movie = new Movie();
                movie.setTitle(movieObj.getString("title"));
                movie.setScore(movieObj.getString("vote_average"));
                movie.setImg(movieObj.getString("poster_path"));

                movieList.add(movie);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return movieList;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (listener != null) {
            listener.onDataLoaded(movies);
        }
    }
}

