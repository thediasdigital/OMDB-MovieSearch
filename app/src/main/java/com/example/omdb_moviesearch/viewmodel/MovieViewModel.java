package com.example.omdb_moviesearch.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.omdb_moviesearch.BuildConfig;
import com.example.omdb_moviesearch.model.Movie;
import com.example.omdb_moviesearch.utils.ApiUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MovieViewModel extends ViewModel {
    private final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();
    private final MutableLiveData<Movie> movie = new MutableLiveData<>();
    public LiveData<List<Movie>> getMovies() { return movies; }
    public LiveData<Movie> getMovie() { return movie; }

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void Search(String query) {
        query = query.trim().replace(" ", "+");
        String url = "https://omdbapi.com/?apikey=" + BuildConfig.apiKey + "&type=movie&s=" + query;

        ApiUtility.get(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful() && response.body() != null) {
                    try {
                        String res = response.body().string();
                        JSONObject jsonObj = new JSONObject(res);
                        
                        if(jsonObj.has("Search")) {
                            
                            JSONArray jsonArr = jsonObj.getJSONArray("Search");
                            List<Movie> listMovies = new ArrayList<>();

                            for(int i = 0; i < jsonArr.length(); i++) {
                                JSONObject movieObj = jsonArr.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(movieObj.getString("Title"));
                                movie.setYear(movieObj.getString("Year"));
                                movie.setImdbID(movieObj.getString("imdbID"));
                                movie.setPosterUrl(movieObj.getString("Poster"));
                                listMovies.add(movie);
                            }
                            movies.postValue(listMovies);
                        }
                        else {
                            movies.postValue(new ArrayList<>());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    public void getMovieDetailsById(String id) {
        String url = "https://omdbapi.com/?apikey=" + BuildConfig.apiKey + "&i=" + id;

        ApiUtility.get(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful() && response.body() != null) {
                    try {
                        String res = response.body().string();
                        JSONObject jsonObj = new JSONObject(res);
                        Movie detailedMovie = new Movie();

                        detailedMovie.setImdbID(id);
                        detailedMovie.setTitle(jsonObj.getString("Title"));
                        detailedMovie.setYear(jsonObj.getString("Year"));
                        detailedMovie.setRating(jsonObj.getString("Rated"));
                        detailedMovie.setRuntime(jsonObj.getString("Runtime"));
                        detailedMovie.setGenre(jsonObj.getString("Genre"));
                        detailedMovie.setPlot(jsonObj.getString("Plot"));
                        detailedMovie.setPosterUrl(jsonObj.getString("Poster"));
                        detailedMovie.setImdbRating(jsonObj.getString("imdbRating"));

                        movie.postValue(detailedMovie);

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }
    public void addToFavourites() {
        // Get the current uid and detailed movie
        String uid = auth.getUid();
        Movie movie = this.movie.getValue();

        if(uid != null && movie != null) {
            Map<String, Object> movieData = new HashMap<>();
            movieData.put("Title", movie.getTitle());
            movieData.put("Year", movie.getYear());
            movieData.put("Poster", movie.getPosterUrl());

            // Add movie to favourites collection
            db.collection("Users")
              .document(uid)
              .collection("Favourites")
              .document(movie.getImdbID())
              .set(movieData)
              .addOnSuccessListener(v -> { Log.d("xyz", "Success"); })
              .addOnFailureListener(e -> { Log.e("xyz", "Fail", e); });
        }
        else {
            Log.d("xyz", "No UID or Movie");
        }
    }
    public void getUserFavourites() {
        // Get the current uid
        String uid = auth.getUid();

        if(uid != null) {
            db.collection("Users")
              .document(uid)
              .collection("Favourites")
              .get()
              .addOnSuccessListener(queryDocumentSnapshots -> {
                  // Create a list to post later
                  List<Movie> favMovies = new ArrayList<>();
                  // Loop through documents
                  for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {
                      Movie movie = new Movie();

                      movie.setImdbID(doc.getId());
                      movie.setTitle(doc.getString("Title"));
                      movie.setYear(doc.getString("Year"));
                      movie.setPosterUrl(doc.getString("Poster"));
                      // Add each favourite movie
                      favMovies.add(movie);
                  }
                  // Post favourite movies
                  movies.postValue(favMovies);
              })
              .addOnFailureListener(e -> { Log.e("xyz", "Failed to load favourites", e); });
        }
    }
}
