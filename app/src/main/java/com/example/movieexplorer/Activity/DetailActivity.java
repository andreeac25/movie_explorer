package com.example.movieexplorer.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieexplorer.R;

public class DetailActivity extends AppCompatActivity {

    ImageView posterDetail, backgroundMovie;
    TextView titleDetail, scoreDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_page); // sau ce nume ai pus xml-ului

        // Legături cu XML
        posterDetail = findViewById(R.id.posterDetail);
        backgroundMovie = findViewById(R.id.backgroundMovie);
        titleDetail = findViewById(R.id.titleDetail);
        scoreDetail = findViewById(R.id.scoreDetail);

        // Preluăm datele trimise din intent
        String title = getIntent().getStringExtra("title");
        String score = getIntent().getStringExtra("score");
        String posterPath = getIntent().getStringExtra("poster");

        String posterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;

        titleDetail.setText(title);
        scoreDetail.setText(score);

        Glide.with(this)
                .load(posterUrl)
                .into(posterDetail);

        Glide.with(this)
                .load(posterUrl)
                .into(backgroundMovie);
    }
}


