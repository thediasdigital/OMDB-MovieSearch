package com.example.omdb_moviesearch.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.omdb_moviesearch.R;
import com.example.omdb_moviesearch.databinding.ActivityDetailsBinding;
import com.example.omdb_moviesearch.model.Movie;
import com.example.omdb_moviesearch.viewmodel.MovieViewModel;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    ActivityDetailsBinding binding;
    MovieViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize view binding
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the intent and movie ID
        Intent intent = getIntent();
        String id = intent.getStringExtra("IMDB_ID");

        // View Model
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        viewModel.getMovieDetailsById(id);
        viewModel.getMovie().observe(this, movie -> {
            // Set the details via live movie data
            binding.detailsTitle.setText(movie.getTitle());
            binding.detailsGenre.setText(movie.getGenre());
            binding.detailsRating.setText(movie.getRating());
            binding.detailsImdb.setText(movie.getImdbRating());
            binding.detailsPlot.setText(movie.getPlot());
            binding.detailsRuntime.setText(movie.getRuntime());
            Picasso.get()
                    .load(movie.getPosterUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.detailsPoster);
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
