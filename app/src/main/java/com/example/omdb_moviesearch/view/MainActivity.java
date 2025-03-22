package com.example.omdb_moviesearch.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.omdb_moviesearch.databinding.ActivityMainBinding;
import com.example.omdb_moviesearch.model.Movie;
import com.example.omdb_moviesearch.viewmodel.MovieViewModel;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MovieViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Live data observer
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        viewModel.getMovies().observe(this, movies -> {
            Log.i("tag", "Observe trigger");
            for(int i = 0; i < movies.size(); i++) {
                Movie movie = movies.get(i);
                Log.i("Title", movie.getTitle());
            }
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.Search(binding.searchBar.getText().toString());
            }
        });
    }
}