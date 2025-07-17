package com.example.movieexplorer.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.movieexplorer.Activity.Login;
import com.example.movieexplorer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button logout_btn;
    private ImageButton backBtn;
    TextView emailSettings;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        // Inflate the account_page layout for this fragment
        View view = inflater.inflate(R.layout.account_page, container, false);
        // Initialize Firebase authentication instance
        auth = FirebaseAuth.getInstance();
        // Reference to the logout button in the layout
        logout_btn = view.findViewById(R.id.logout);
        // Get the currently logged-in user
        user = auth.getCurrentUser();
        // Reference to the TextView that shows the user's email
        emailSettings = view.findViewById(R.id.email_settings);
        // If no user is logged in, redirect to Login activity and finish current one
        if (user == null){
            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }else {
            // If user is logged in, display their email in the TextView
            emailSettings.setText(user.getEmail());
        }
        // Set up logout button click listener
        backBtn = view.findViewById(R.id.backAccount);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the current user from Firebase
                FirebaseAuth.getInstance().signOut();
                // Redirect to Login activity after logout and finish current activity
                Intent intent = new Intent(requireContext(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

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
            Log.e("Account Fragment", "Error");
        }
        // Return the inflated view to be displayed
        return view;
    }
}