package com.example.omdb_moviesearch.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.omdb_moviesearch.R;
import com.example.omdb_moviesearch.databinding.ActivityFavouritesBinding;
import com.example.omdb_moviesearch.model.Movie;
import com.example.omdb_moviesearch.utils.MovieClickListener;
import com.example.omdb_moviesearch.viewmodel.MovieViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Favourites extends AppCompatActivity implements MovieClickListener {

    ActivityFavouritesBinding binding;
    MovieViewModel viewModel;
    FirebaseAuth auth;
    MovieAdapter adapter;
    List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movies = new ArrayList<>();
        // Binding and auth
        binding = ActivityFavouritesBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();

        setContentView(binding.getRoot());

        // Initialize recycler view
        adapter = new MovieAdapter(getApplicationContext(), new ArrayList<>());
        adapter.setClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Viewmodel and live data observation
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        viewModel.getUserFavourites();
        viewModel.getMovies().observe(this, movies -> {
            adapter.updateMovies(movies);
            this.movies.clear();
            this.movies.addAll(movies);
        });


        binding.toMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Favourites.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v, int pos) {
        Movie clickedMovie = movies.get(pos);
        Intent intent = new Intent(Favourites.this, FavouriteDetails.class);
        intent.putExtra("IMDB_ID", clickedMovie.getImdbID());
        startActivity(intent);
        finish();
    }
}