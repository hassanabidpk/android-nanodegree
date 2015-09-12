package com.hassanabid.popularmoviesapp2.retrofit;

import com.squareup.okhttp.Call;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by hassanabid on 9/11/15.
 */
public interface MovieTrailersService {

    public String API_KEY="api_key";

//    @GET("/movie/{movie_id}?api_key="+API_KEY)
//    Call<List<String>> listRepos(@Path("movie_id") int id);
}
