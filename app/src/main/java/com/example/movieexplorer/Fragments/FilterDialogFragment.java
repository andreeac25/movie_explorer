package com.example.movieexplorer.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.movieexplorer.R;

import java.util.ArrayList;
import java.util.List;


public class FilterDialogFragment extends DialogFragment {


    private FilterDialogListener listener;
    private LinearLayout genreCheckboxesContainer;
    private Button applyButton;
    private Button resetButton;
    private List<String> availableGenres;

    public FilterDialogFragment(List<String> availableGenreNames, List<String> currentSelectedGenres) {
        // Required empty public constructor
    }

    public interface FilterDialogListener {
        void onApplyGenreFilter(List<String> selectedGenres);
        void onResetFilter();
    }

    public FilterDialogFragment(List<String> selectedGenres) {
        this.availableGenres = availableGenres;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FilterDialogListener) getTargetFragment();
            if (listener == null) {
                listener = (FilterDialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FilterDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_filter_dialog, null);

        genreCheckboxesContainer = view.findViewById(R.id.genre_checkboxes);
        applyButton = view.findViewById(R.id.apply_filter_btn);
        resetButton = view.findViewById(R.id.reset_filter_btn);

        // Adaugă dinamic checkbox-uri pentru fiecare gen disponibil
        for (String genre : availableGenres) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(genre);
            genreCheckboxesContainer.addView(checkBox);
        }

        applyButton.setOnClickListener(v -> {
            List<String> selectedGenres = new ArrayList<>();
            for (int i = 0; i < genreCheckboxesContainer.getChildCount(); i++) {
                View child = genreCheckboxesContainer.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child;
                    if (checkBox.isChecked()) {
                        selectedGenres.add(checkBox.getText().toString());
                    }
                }
            }
            listener.onApplyGenreFilter(selectedGenres); // Apelăm metoda simplificată
            dismiss(); // Închide dialogul
        });

        resetButton.setOnClickListener(v -> {
            listener.onResetFilter();
            dismiss(); // Închide dialogul
        });

        builder.setView(view);
        return builder.create();
    }
}