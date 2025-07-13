package com.example.movieexplorer.Interface;

import java.util.List;

public interface FilterDialogListener {
    void onApplyGenreFilter(List<String> selectedGenres);
    void onResetFilter();
}
