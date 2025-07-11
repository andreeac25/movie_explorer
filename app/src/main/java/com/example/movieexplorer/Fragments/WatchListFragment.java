package com.example.movieexplorer.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.movieexplorer.R;

public class WatchListFragment extends Fragment {
    public static final String TAG = "WatchListFragment";

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.watch_list, container, false);
        return view;
    }


}