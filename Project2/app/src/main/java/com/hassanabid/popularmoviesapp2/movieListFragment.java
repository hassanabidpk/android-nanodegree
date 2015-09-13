package com.hassanabid.popularmoviesapp2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hassanabid.popularmoviesapp2.adapters.MoviesDataAdapter;
import com.hassanabid.popularmoviesapp2.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MovieListFragment extends android.support.v4.app.Fragment {


    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private static final String API_URL = "http://api.themoviedb.org/3/discover/movie/";
    private static final String HIGHEST_RATED = "highest_rated";
    private static final String FAV_MOVIES = "fav_list";
    private static final String MOST_POPULAR = "popularity.desc";
    private static final String MOVIE_DB_KEY = "moviedb";

    private GridView moviesGrid;
    private ArrayList<MovieParcel> movieList;

    MovieParcel[] movies;
    ArrayList<String> favList;
    String[][] favResultStrs;
    private View mLoadingView;
    private int mShortAnimationDuration;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private OnMovieSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnMovieSelectedListener {
        void onMovieSelected(int position, int id, String title, String poster_path,
                                    String overview, String release_date, String votes);
        void onFetchFirstMovie(int position, int id, String title, String poster_path,
                                      String overview, String release_date, String votes);
    }

    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");

        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_DB_KEY)) {
            Log.d(LOG_TAG,"MovieList not available in instancestate");
            if(isNetworkAvailable()) {
                new FetchMoviesTask().execute("popularity");
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cannot fetch movies")
                        .setMessage("Please connect to wifi or enable cellular data!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_DB_KEY);
            Log.d(LOG_TAG, "MovieList retrieved with size : " + movieList.size());
        }
        getFavList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        moviesGrid  = (GridView) view.findViewById(R.id.movieGrid);
        mLoadingView =  view.findViewById(R.id.loading_spinner);
        moviesGrid.setVisibility(View.GONE);

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        if(movieList != null) {
            crossfade();
            final MoviesDataAdapter moviesDataAdapter = new MoviesDataAdapter(getActivity(),movieList);
            moviesGrid.setAdapter(moviesDataAdapter);
            if(movieList.size() != 0) {
                MovieParcel movie = movieList.get(0);
                mCallback.onFetchFirstMovie(0, movie.id, movie.title
                        , movie.poster, movie.overview, movie.release_date, movie.vote);
            }
            moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(LOG_TAG, "Clicked : " + i);
                    MovieParcel movie = movieList.get(i);
                    mCallback.onMovieSelected(i,movie.id,movie.title
                            ,movie.poster,movie.overview,movie.release_date,movie.vote);
                }
            });

        }

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_DB_KEY, movieList);
    }


    public class FetchMoviesTask extends AsyncTask<String,Void,String[][]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        // These are the names of the JSON objects that need to be extracted.
        final String MOVIE_ID = "id";
        final String MOVIE_LIST = "results";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_POSTER = "poster_path";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_VOTES = "vote_average";
        boolean isFavListReq = false;

        private String[][] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

            String[][] resultStrs = new String[movieArray.length()][6];
            movies = new MovieParcel[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {
                int k = 0;

                // Get the JSON object representing the movie
                JSONObject movieData = movieArray.getJSONObject(i);
                movies[i] = new MovieParcel(
                        movieData.getInt(MOVIE_ID),
                        movieData.getString(MOVIE_TITLE),
                        movieData.getString(MOVIE_POSTER),
                        movieData.getString(MOVIE_OVERVIEW),
                        movieData.getString(MOVIE_RELEASE_DATE),
                        movieData.getString(MOVIE_VOTES));
                resultStrs[i][k] = movieData.getString(MOVIE_TITLE);
                resultStrs[i][k+1] = movieData.getString(MOVIE_POSTER);
                resultStrs[i][k+2] = movieData.getString(MOVIE_OVERVIEW);
                resultStrs[i][k+3] = movieData.getString(MOVIE_RELEASE_DATE);
                resultStrs[i][k+4] = movieData.getString(MOVIE_VOTES);
                resultStrs[i][k+5] = Integer.toString(movieData.getInt(MOVIE_ID));


                Log.d(LOG_TAG, "Movie data :" + i + " | "+ resultStrs[i][k+5]);
            }
            movieList = new ArrayList<MovieParcel>(Arrays.asList(movies));
            return resultStrs;
        }

        private void getFavMovieDataFromJson (String singleMovieJson, int i) throws JSONException {

            JSONObject movieJson = new JSONObject(singleMovieJson);

                int k = 0;

                // Get the JSON object representing the movie
                movies[i] = new MovieParcel(
                        movieJson.getInt(MOVIE_ID),
                        movieJson.getString(MOVIE_TITLE),
                        movieJson.getString(MOVIE_POSTER),
                        movieJson.getString(MOVIE_OVERVIEW),
                        movieJson.getString(MOVIE_RELEASE_DATE),
                        movieJson.getString(MOVIE_VOTES));
                favResultStrs[i][k] = movieJson.getString(MOVIE_TITLE);
                favResultStrs[i][k+1] = movieJson.getString(MOVIE_POSTER);
                favResultStrs[i][k+2] = movieJson.getString(MOVIE_OVERVIEW);
                favResultStrs[i][k+3] = movieJson.getString(MOVIE_RELEASE_DATE);
                favResultStrs[i][k+4] = movieJson.getString(MOVIE_VOTES);
                favResultStrs[i][k+5] = Integer.toString(movieJson.getInt(MOVIE_ID));


                Log.d(LOG_TAG, "Movie data :" + i + " | " + favResultStrs[i][k + 5]);
        }


        @Override
        protected String[][] doInBackground(String... params) {

            if(params.length == 0)
                return  null;
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String movieJsonStr = null;
            String favJsonJsonStr = null;

            String sort_order = MOST_POPULAR;
            if(params[0].equals(HIGHEST_RATED)) {
                sort_order = "vote_average.desc";
            } else if(params[0].equals(FAV_MOVIES)) {
                isFavListReq = true;

            }
            if(!isFavListReq) {
                try {
                    final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                    final String SORT_PARAM = "sort_by";
                    final String API_PARAM = "api_key";

                    String api_key = getActivity().getResources().getString(R.string.api_key);
                    Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_PARAM, sort_order)
                            .appendQueryParameter(API_PARAM, api_key)
                            .build();
                    URL url = new URL(builtUri.toString());

                    Log.d(LOG_TAG, "movies Uri  : " + builtUri.toString());

                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if (inputStream == null) {
                        return null;
                    }

                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {

                        buffer.append(line + "\n");

                    }

                    if (buffer.length() == 0) {
                        return null;
                    }

                    movieJsonStr = buffer.toString();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "IO Error " + e);
                    return null;
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }

                }
                try {
                    return getMovieDataFromJson(movieJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            } else {

                    if(favList == null || favList.size() == 0)
                        return null;
                favResultStrs = new String[favList.size()][6];
                movies = new MovieParcel[favList.size()];
                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";

                final String API_PARAM = "api_key";

                String api_key = getActivity().getResources().getString(R.string.api_key);
                for(int i = 0; i < favList.size(); i++) {
                    try {
                        final String MOVIE_FINAL_URL = MOVIES_BASE_URL + favList.get(i) + "?";
                        Uri builtUri = Uri.parse(MOVIE_FINAL_URL).buildUpon()
                                .appendQueryParameter(API_PARAM, api_key)
                                .build();
                        URL url = new URL(builtUri.toString());

                        Log.d(LOG_TAG, "movies Uri (fav) : " + builtUri.toString());

                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.connect();

                        InputStream inputStream = httpURLConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();

                        if (inputStream == null) {
                            return null;
                        }

                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;

                        while ((line = bufferedReader.readLine()) != null) {

                            buffer.append(line + "\n");

                        }

                        if (buffer.length() == 0) {
                            return null;
                        }

                        favJsonJsonStr = buffer.toString();

                    } catch (IOException e) {
                        Log.e(LOG_TAG, "IO Error " + e);
                        return null;
                    } finally {
                        if (httpURLConnection != null)
                            httpURLConnection.disconnect();
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (final IOException e) {
                                Log.e(LOG_TAG, "Error closing stream", e);
                            }
                        }

                    }
                    try {
                        getFavMovieDataFromJson(favJsonJsonStr,i);
                        if(i == (favList.size() - 1)) {
                            movieList = new ArrayList<MovieParcel>(Arrays.asList(movies));
                            return favResultStrs;
                        }
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(final String[][] result) {
            crossfade();
            if(result != null && !isFavListReq) {
                final MoviesDataAdapter moviesDataAdapter = new MoviesDataAdapter(getActivity(),movieList);
                moviesGrid.setAdapter(moviesDataAdapter);
                if(movieList.size() != 0) {
                    MovieParcel movie = movieList.get(0);
                    mCallback.onFetchFirstMovie(0, movie.id, movie.title
                            , movie.poster, movie.overview, movie.release_date, movie.vote);
                }
                moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d(LOG_TAG,"Clicked : " + i);
                        mCallback.onMovieSelected(i,Integer.valueOf(result[i][5]),result[i][0]
                                ,result[i][1],result[i][2],result[i][3],result[i][4]);
                    }
                });
            } else if(result != null && isFavListReq){
                Log.d(LOG_TAG,"movie str result from favorites");
                final MoviesDataAdapter moviesDataAdapter = new MoviesDataAdapter(getActivity(),movieList);
                moviesGrid.setAdapter(moviesDataAdapter);
                if(movieList.size() != 0) {
                    MovieParcel movie = movieList.get(0);
                    mCallback.onFetchFirstMovie(0, movie.id, movie.title
                            , movie.poster, movie.overview, movie.release_date, movie.vote);
                }
                moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d(LOG_TAG, "Clicked : " + i);
                        mCallback.onMovieSelected(i, Integer.valueOf(result[i][5]), result[i][0]
                                , result[i][1], result[i][2], result[i][3], result[i][4]);
                    }
                });
            }

        }
    }

    private void crossfade() {

        moviesGrid.setAlpha(0f);
        moviesGrid.setVisibility(View.VISIBLE);

        moviesGrid.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular_movies) {
            if(isNetworkAvailable()) {
                mLoadingView.setVisibility(View.VISIBLE);
                new FetchMoviesTask().execute(MOST_POPULAR);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cannot fetch movies")
                        .setMessage("Please connect to wifi or enable cellular data!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
            return true;
        } else if(id == R.id.action_rated_movies){
            if(isNetworkAvailable()) {
                mLoadingView.setVisibility(View.VISIBLE);
                new FetchMoviesTask().execute(HIGHEST_RATED);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cannot fetch movies")
                        .setMessage("Please connect to wifi or enable cellular data!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        } else if(id==R.id.action_fav_movies) {
            mLoadingView.setVisibility(View.VISIBLE);
            getFavList();
            new FetchMoviesTask().execute(FAV_MOVIES);

        }

        return super.onOptionsItemSelected(item);
    }

    private void getFavList() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Utility.SHARED_PREFS_MOVIE_APP,
                Context.MODE_PRIVATE);
        Set<String> movieIdSet = prefs.getStringSet(Utility.MOVIE_FAV_KEY, null);
        if(movieIdSet != null) {
            favList = new ArrayList<String>(movieIdSet);
        }else {
            favList = new ArrayList<String>();
        }
        Log.d(LOG_TAG,"favs : " + favList.size());
        if(favList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No Favorites!")
                    .setMessage("Press the star button to add movies to your favorite list!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        }

    }
}
