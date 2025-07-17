package com.example.movieexplorer.Activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.movieexplorer.Fragments.DetailFragment;
import com.example.movieexplorer.Fragments.HomeFragment;
import com.example.movieexplorer.Fragments.WatchListFragment;
import com.example.movieexplorer.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageButton homePageBtn, watchListPageBtn, searchBtn;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private WatchListFragment watchListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity to activity_main.xml
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
                // Replace the current fragment with the HomeFragment
                replaceFragment(homeFragment);
                // Ensure the fragment transaction is completed before proceeding
                fragmentManager.executePendingTransactions();
                // Activate the search input field in the HomeFragment
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
                // Begin a fragment transaction to add the HomeFragment to the layout
                fragmentManager.beginTransaction()
                            .add(R.id.fragmentLayout, homeFragment)
                            .commit();
                // Ensure the transaction is completed before proceeding
                fragmentManager.executePendingTransactions();
                // Show the HomeFragment (might handle visibility logic if multiple fragments are active)
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
                // Create a new instance of the WatchListFragment
                watchListFragment = new WatchListFragment();
                // Begin a fragment transaction to add the WatchListFragment to the layout
                fragmentManager.beginTransaction()
                            .add(R.id.fragmentLayout, watchListFragment)
                            .commit();
                // Ensure the transaction is executed immediately before continuing
                fragmentManager.executePendingTransactions();
            }
        });
    }

    // This method shows the specified fragment and hides others.
    private void showFragment(Fragment fragmentToShow) {
        // Begin a new fragment transaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Get the list of all fragments managed by the FragmentManager
        List<Fragment> fragments = fragmentManager.getFragments();
        // Loop through each fragment
        for (Fragment fragment : fragments) {
            // Check if the fragment is not null and has been added to the fragment manager
            if (fragment != null && fragment.isAdded()) {
                if (fragment == fragmentToShow) {
                    // Show the fragment we want to display
                    transaction.show(fragment);
                } else {
                    // Hide all other visible fragments
                    if (fragment.isVisible()) {
                        transaction.hide(fragment);
                    }
                }
            }
        }
        // Commit the transaction to apply the changes
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

    // This method replaces the current fragment in the layout with a new one
    private void replaceFragment(Fragment fragment) {
        // Get the FragmentManager to interact with fragments associated with this activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Begin a new fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace the current fragment in the container (R.id.fragmentLayout) with the new fragment
        fragmentTransaction.replace(R.id.fragmentLayout, fragment);
        // Commit the transaction to apply the changes
        fragmentTransaction.commit();
    }
}