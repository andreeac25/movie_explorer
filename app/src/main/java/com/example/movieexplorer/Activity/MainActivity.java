package com.example.movieexplorer.Activity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.movieexplorer.Fragments.HomeFragment;
import com.example.movieexplorer.Fragments.WatchListFragment;
import com.example.movieexplorer.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageButton homePageBtn, watchListPageBtn, searchBtn, backBtn;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private WatchListFragment watchListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();  // This line initializes the fragment manager.
        homePageBtn = findViewById(R.id.button_homepage); // This line finds the homepage button by its ID.
        watchListPageBtn = findViewById(R.id.button_watchlist); // This line finds the watchlist button by its ID.
        searchBtn = findViewById(R.id.button_search); // This line finds the search button by its ID.

        // This block initializes and displays the HomeFragment when the activity starts for the first time.
        if (savedInstanceState == null) {
            homeFragment = (HomeFragment) fragmentManager.findFragmentByTag(HomeFragment.TAG);
            homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentLayout, homeFragment, HomeFragment.TAG)
                    .commit();

            showFragment(homeFragment);
            updateButtonColor(homePageBtn, true);
            updateButtonColor(watchListPageBtn, false);
            updateButtonColor(searchBtn, false);
        }

        // This method handles the click event for the search button.
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonColor(searchBtn, true);
                updateButtonColor(homePageBtn, false);
                updateButtonColor(watchListPageBtn, false);
                    replaceFragment(homeFragment);
                    fragmentManager.executePendingTransactions();
                    homeFragment.activateSearchInput();
            }
        });

        // This method handles the click event for the home button.
        homePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonColor(homePageBtn, true);
                updateButtonColor(watchListPageBtn, false);
                updateButtonColor(searchBtn, false);
                    homeFragment = new HomeFragment();
                    fragmentManager.beginTransaction()
                            .add(R.id.fragmentLayout, homeFragment)
                            .commit();
                    fragmentManager.executePendingTransactions();
                    showFragment(homeFragment);
                }
        });

        // This method handles the click event for the watchlist button.
        watchListPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonColor(watchListPageBtn, true);
                updateButtonColor(homePageBtn, false);
                updateButtonColor(searchBtn, false);
                    watchListFragment = new WatchListFragment();
                    fragmentManager.beginTransaction()
                            .add(R.id.fragmentLayout, watchListFragment)
                            .commit();
                    fragmentManager.executePendingTransactions();
            }
        });
    }

    // This method shows the specified fragment and hides others.
    private void showFragment(Fragment fragmentToShow) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isAdded()) {
                if (fragment == fragmentToShow) {
                    transaction.show(fragment);
                } else {
                    if (fragment.isVisible()) {
                        transaction.hide(fragment);
                    }
                }
            }
        }
        transaction.commit();
    }


    // This method updates the color filter of an ImageButton.
    private void updateButtonColor(ImageButton button, boolean selected) {
        if (selected) {
            button.setColorFilter(getResources().getColor(R.color.lightBlue), PorterDuff.Mode.SRC_IN);
        } else {
            button.clearColorFilter();
        }
    }

    // This method handles the back button press behavior.
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, fragment);
        fragmentTransaction.commit();
    }
}