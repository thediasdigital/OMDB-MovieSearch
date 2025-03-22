package com.example.omdb_moviesearch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.omdb_moviesearch.BuildConfig;
import com.example.omdb_moviesearch.model.Movie;
import com.example.omdb_moviesearch.utils.ApiUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MovieViewModel extends ViewModel {
    private final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();
    public LiveData<List<Movie>> getMovies() { return movies; }

    public void Search(String query) {
        query = query.trim().replace(" ", "+");
        String url = "https://omdbapi.com/?apikey=" + BuildConfig.apiKey + "&type=movie&s=" + query;

        ApiUtility.get(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
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
                                movie.setImbdID(movieObj.getString("imdbID"));
                                movie.setPosterUrl(movieObj.getString("Poster"));
                                listMovies.add(movie);
                            }
                            movies.postValue(listMovies);
                        }
                        else {
                            movies.postValue(new ArrayList<>());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
