package com.example.movieexplorer.Activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.movieexplorer.Fragments.HomeFragment;
import com.example.movieexplorer.Fragments.WatchListFragment;
import com.example.movieexplorer.R;

public class MainActivity extends AppCompatActivity {

    ImageButton homePageBtn, watchListPageBtn, searchBtn;
    EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }


        homePageBtn = findViewById(R.id.button_homepage);
        watchListPageBtn = findViewById(R.id.button_watchlist);
        searchInput = findViewById(R.id.search_input);
        searchBtn = findViewById(R.id.button_search);

        // Home button click
        homePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePageBtn.setSelected(true);
                watchListPageBtn.setSelected(false);

                updateButtonColor(homePageBtn, true);
                updateButtonColor(watchListPageBtn, false);
                updateButtonColor(searchBtn, false);

                // Show HomeFragment
                loadFragment(new HomeFragment());
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
                updateButtonColor(searchBtn, false);

                // Show WatchListFragment
                loadFragment(new WatchListFragment());
            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setVisibility(View.VISIBLE);
                searchInput.requestFocus();
            }
        });

    }

    // Metodă helper pentru a încărca un fragment în container
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updateButtonColor(ImageButton button, boolean selected) {
        if (selected) {
            button.setColorFilter(getResources().getColor(R.color.lightBlue), PorterDuff.Mode.SRC_IN);
        } else {
            button.clearColorFilter();
        }
    }
}

