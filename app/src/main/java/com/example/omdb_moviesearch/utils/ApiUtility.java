package com.example.omdb_moviesearch.utils;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiUtility {
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void get(String url, Callback callback) {
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(callback);
    }
}
