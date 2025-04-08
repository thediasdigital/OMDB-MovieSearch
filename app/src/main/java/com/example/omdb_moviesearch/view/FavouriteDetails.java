package com.example.omdb_moviesearch.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.omdb_moviesearch.R;
import com.example.omdb_moviesearch.databinding.ActivityFavouriteDetailsBinding;
import com.example.omdb_moviesearch.viewmodel.MovieViewModel;
import com.squareup.picasso.Picasso;

public class FavouriteDetails extends AppCompatActivity {

    ActivityFavouriteDetailsBinding binding;
    MovieViewModel viewModel;
    String imdbID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFavouriteDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the intent and movie ID
        Intent intent = getIntent();
        String id = intent.getStringExtra("IMDB_ID");

        // View Model
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        viewModel.getMovieDetailsById(id);
        viewModel.getUserDesc(id, desc -> {
            binding.detailsPlot.setText(desc);
        });
        viewModel.getMovie().observe(this, movie -> {
            this.imdbID = movie.getImdbID();
            // Set the details via live movie data
            binding.detailsTitle.setText(movie.getTitle());
            binding.detailsGenre.setText(movie.getGenre());
            binding.detailsRating.setText(movie.getRating());
            binding.detailsImdb.setText(movie.getImdbRating());
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
                Intent intent = new Intent(FavouriteDetails.this, Favourites.class);
                startActivity(intent);
                finish();
            }
        });

        binding.removeFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeFavourite(imdbID);
                Intent intent = new Intent(FavouriteDetails.this, Favourites.class);
                startActivity(intent);
                finish();
            }
        });

        binding.editDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current description
                String desc = binding.detailsPlot.getText().toString();
                // Setup an edit text view for the popup
                EditText input = new EditText(FavouriteDetails.this);
                input.setText(desc);

                // Popup for editing text
                new AlertDialog.Builder(FavouriteDetails.this)
                        .setTitle("Edit description")
                        .setView(input)
                        .setPositiveButton("Save", (dialog, which) -> {
                            String newDesc = input.getText().toString().trim();
                            // Set the new description to the view and update it in the db
                            binding.detailsPlot.setText(newDesc);
                            viewModel.updateMovieDescription(newDesc);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }
}