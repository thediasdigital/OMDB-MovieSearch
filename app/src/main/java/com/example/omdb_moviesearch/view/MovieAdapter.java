package com.example.omdb_moviesearch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omdb_moviesearch.R;
import com.example.omdb_moviesearch.model.Movie;
import com.example.omdb_moviesearch.utils.MovieClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    List<Movie> movies;
    Context context;
    public MovieClickListener clickListener;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    public void setClickListener(MovieClickListener clickListener) { this.clickListener = clickListener; }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View movieView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_layout, parent, false);
        return new MovieViewHolder(movieView, this.clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.titleTxt.setText(movie.getTitle());
        holder.descriptionTxt.setText(movie.getYear());
        // Set the imageView using picasso
        Picasso.get()
               .load(movie.getPosterUrl())
               .placeholder(R.drawable.placeholder)
               .error(R.drawable.placeholder)
               .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return (movies != null) ? movies.size() : 0;
    }

    public void updateMovies(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTxt, descriptionTxt;
        Button showDetailsBtn;
        MovieClickListener clickListener;
        public MovieViewHolder(@NonNull View itemView, MovieClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;

            imageView = itemView.findViewById(R.id.imageView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            descriptionTxt = itemView.findViewById(R.id.descriptionTxt);
            showDetailsBtn = itemView.findViewById(R.id.showDetailsBtn);

            showDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(view, getAdapterPosition());
                }
            });
        }
    }
}
