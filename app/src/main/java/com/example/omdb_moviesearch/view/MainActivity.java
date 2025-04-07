package com.example.omdb_moviesearch.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.omdb_moviesearch.databinding.ActivityMainBinding;
import com.example.omdb_moviesearch.model.Movie;
import com.example.omdb_moviesearch.utils.MovieClickListener;
import com.example.omdb_moviesearch.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieClickListener {

    ActivityMainBinding binding;
    MovieViewModel viewModel;
    MovieAdapter adapter;
    List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movies = new ArrayList<>();

        // Initialize view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize recycler view
        adapter = new MovieAdapter(getApplicationContext(), new ArrayList<>());
        adapter.setClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Live data observer
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        viewModel.getMovies().observe(this, movies -> {
            Log.i("tag", "Observe trigger");
            adapter.updateMovies(movies);
            this.movies.clear();
            this.movies.addAll(movies);
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.Search(binding.searchBar.getText().toString());
            }
        });

        binding.toFavouritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Favourites.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onClick(View v, int pos) {
        Movie clickedMovie = movies.get(pos);
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("IMDB_ID", clickedMovie.getImdbID());
        startActivity(intent);
    }
}