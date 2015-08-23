package com.hassanabid.popularmoviesapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeActivityFragment extends Fragment {

    private static final String LOG_TAG = HomeActivityFragment.class.getSimpleName();
    private static final String API_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";
    private static final String HIGHEST_RATED = "highest_rated";
    private static final String MOST_POPULAR = "popular";

    private GridView moviesGrid;

    private View mLoadingView;
    private int mShortAnimationDuration;

    public HomeActivityFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        moviesGrid  = (GridView) view.findViewById(R.id.movieGrid);
        mLoadingView =  view.findViewById(R.id.loading_spinner);

        // Initially hide the content view.
        moviesGrid.setVisibility(View.GONE);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        if(isNetworkAvailable()) {
            new FetchMoviesTask().execute("popularity");
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Cannot fetch movies")
                    .setMessage("Please connect to wifi or enable cellular data!")
                    .create()
                    .show();
        }
        setHasOptionsMenu(true);
        return view;
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,String[][]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private String[][] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_LIST = "results";
            final String MOVIE_TITLE = "original_title";
            final String MOVIE_POSTER = "poster_path";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_VOTES = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

            String[][] resultStrs = new String[movieArray.length()][5];
            for(int i = 0; i < movieArray.length(); i++) {
               int k = 0;

                // Get the JSON object representing the movie
                JSONObject movieData = movieArray.getJSONObject(i);

                resultStrs[i][k] = movieData.getString(MOVIE_TITLE);
                resultStrs[i][k+1] = movieData.getString(MOVIE_POSTER);
                resultStrs[i][k+2] = movieData.getString(MOVIE_OVERVIEW);
                resultStrs[i][k+3] = movieData.getString(MOVIE_RELEASE_DATE);
                resultStrs[i][k+4] = movieData.getString(MOVIE_VOTES);

                Log.d(LOG_TAG, "Movie data :" + i + "\n" + resultStrs[i]);
            }

            return resultStrs;
        }


        @Override
        protected String[][] doInBackground(String... params) {

            if(params.length == 0)
                return  null;

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String movieJsonStr = null;

            String sort_order = "popularity.desc";

            try {
                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                if(params[0].equals("highest_rated")) {
                    sort_order = "vote_average.desc";
                }
                String api_key = getActivity().getResources().getString(R.string.api_key);
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_order)
                        .appendQueryParameter(API_PARAM, api_key)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.d(LOG_TAG,"movies Uri  : " + builtUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null) {
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while((line = bufferedReader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                if(buffer.length() == 0) {
                    return null;
                }

                movieJsonStr = buffer.toString();
//                Log.v(LOG_TAG,"Movies JSOn data : " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "IO Error " + e);
                return null;
            } finally {
                if(httpURLConnection != null)
                    httpURLConnection.disconnect();
                if(bufferedReader != null) {
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

            return null;
        }

        @Override
        protected void onPostExecute(final String[][] result) {
            crossfade();
            if(result != null) {
                final MoviesDataAdapter moviesDataAdapter = new MoviesDataAdapter(getActivity(),result);
                moviesGrid.setAdapter(moviesDataAdapter);
                moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d(LOG_TAG,"Clicked : " + i);
                        mCallback.onMovieSelected(i,result[i][0]
                                ,result[i][1],result[i][2],result[i][3],result[i][4]);
                    }
                });
            }

        }
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
                        .create()
                        .show();
            }
            return true;
        } else {
            if(isNetworkAvailable()) {
                mLoadingView.setVisibility(View.VISIBLE);
                new FetchMoviesTask().execute(HIGHEST_RATED);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cannot fetch movies")
                        .setMessage("Please connect to wifi or enable cellular data!")
                        .create()
                        .show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    OnMovieSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnMovieSelectedListener {
        public void onMovieSelected(int position, String title, String poster_path,
                                    String overview, String release_date, String votes);
    }

    private void crossfade() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        moviesGrid.setAlpha(0f);
        moviesGrid.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        moviesGrid.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
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
}
