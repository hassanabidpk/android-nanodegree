package com.hassanabid.popularmoviesapp2;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.hassanabid.popularmoviesapp2.adapters.MoviesDataAdapter;
import com.hassanabid.popularmoviesapp2.dummy.DummyContent;
import com.hassanabid.popularmoviesapp2.parcels.MovieReviewsParcel;
import com.hassanabid.popularmoviesapp2.utility.Utility;
import com.squareup.picasso.Picasso;

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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String POSTER_KEY = "poster_path";
    public static final String OVERVIEW_KEY = "overview";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String VOTES_KEY = "votes";
    private static final String MOVIE_TRAILERS_KEY = "movietrailers";
    private static final String MOVIE_REVIEWS_KEY = "moviereviews";


//    http://api.themoviedb.org/3/movie/211672/trailers?api_key=11004c5dda64d0bae607c7af2636e983

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    int movie_id;
    String title;
    String poster ;
    String overview;
    String release_date;
    String votes;
    private ViewGroup trailersLayout;
    private ViewGroup reviewsLayout;
    private String[] movieTrailers;
    private boolean isSaveInstance;
    private ArrayList<MovieReviewsParcel> movieReviewList;
    MovieReviewsParcel[] movieReviewsParcels;
    private android.support.v7.widget.ShareActionProvider mShareActionProvider;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ID_KEY)) {

            movie_id = getArguments().getInt(ID_KEY);
            title = getArguments().getString(TITLE_KEY);
            poster = getArguments().getString(POSTER_KEY);
            overview =getArguments().getString(OVERVIEW_KEY);
            release_date = getArguments().getString(RELEASE_DATE_KEY);
            votes = getArguments().getString(VOTES_KEY);

        }
        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_REVIEWS_KEY) ||
                !savedInstanceState.containsKey(MOVIE_TRAILERS_KEY)) {
            Log.d(LOG_TAG,"MovieList not available in instancestate");
            isSaveInstance = false;
            if(Utility.isNetworkAvailable(getActivity())) {
                new FetchMovieTrailersTask().execute(movie_id);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cannot fetch movies")
                        .setMessage("Please connect to wifi or enable cellular data!")
                        .create()
                        .show();
            }
        }
        else {
            movieReviewList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS_KEY);
            movieTrailers = savedInstanceState.getStringArray(MOVIE_TRAILERS_KEY);
            if(movieTrailers != null && movieReviewList != null) {
                Log.d(LOG_TAG, "movieTrailers retrieved: " + movieTrailers.length + " and reviews : " +
                        movieReviewList.size());
            }
            isSaveInstance = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView dateView = (TextView) rootView.findViewById(R.id.releaseDate);
        TextView voteView = (TextView) rootView.findViewById(R.id.votes);
        ImageView posterView = (ImageView) rootView.findViewById(R.id.posterDetail);
        TextView overViewTextView = (TextView) rootView.findViewById(R.id.overviewText);
        final FloatingActionButton favoriteButton = (FloatingActionButton) rootView.findViewById(R.id.favoriteBtn);
        trailersLayout = (ViewGroup) rootView.findViewById(R.id.movie_trailers);
        reviewsLayout = (ViewGroup) rootView.findViewById(R.id.movie_reviews);
        Button showReviews = (Button) rootView.findViewById(R.id.show_reviews);
        final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.movieScrolLView);

        if (poster.equals("null") || poster.equals(null) || poster.equals("")) {
            posterView.setImageResource(R.drawable.empty_photo);
        } else {
            String url = "http://image.tmdb.org/t/p/w185/" + poster;
            Log.d(LOG_TAG,"image url " + url);
            Picasso.with(getActivity())
                    .load(url)
                    .into(posterView);
        }

        titleView.setText(title);
        dateView.setText(release_date);
        voteView.setText(votes);
        overViewTextView.setText(overview);
        checkIfFavorite(movie_id, favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFavorite(movie_id, favoriteButton);

            }
        });

        Log.d(LOG_TAG, "Movie id : " + movie_id + " Title : " + title);
        if(isSaveInstance) {
            setmovieTrailers(movieTrailers);
            setmovieReviews(movieReviewList);
        }
        showReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsLayout.setVisibility(View.VISIBLE);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);

//                        scrollView.scrollTo(0, scrollView.getBottom());
                    }
                });
            }
        });

        setHasOptionsMenu(true);
        if (movieTrailers != null && movieTrailers.length != 0 && mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent(movieTrailers[0]));
        }
        return rootView;
    }

    public class FetchMovieTrailersTask extends AsyncTask<Integer,Void,String[]> {

        private final String LOG_TAG = FetchMovieTrailersTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_TRAILERS = "trailers";
            final String MOVIE_TRAILER_LIST = "youtube";
            final String MOVIE_REVIEWS = "reviews";
            final String MOVIE_REVIEWS_RESULT = "results";
            final String MOVIE_TRAILER_SOURCE = "source";
            final String MOVIE_REVIEW_AUTHOR = "author";
            final String MOVIE_REVIEWS_CONTENT = "content";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONObject movieTrailerObj = movieJson.getJSONObject(MOVIE_TRAILERS);
            JSONArray movieYoutubeArray = movieTrailerObj.getJSONArray(MOVIE_TRAILER_LIST);

            JSONObject movieReviewsObj = movieJson.getJSONObject(MOVIE_REVIEWS);
            JSONArray movieReviewArray = movieReviewsObj.getJSONArray(MOVIE_REVIEWS_RESULT);

            String[] resultStrs = new String[movieYoutubeArray.length()];
            for(int i = 0; i < movieYoutubeArray.length(); i++) {
                int k = 0;

                JSONObject movieData = movieYoutubeArray.getJSONObject(i);
                resultStrs[i] = movieData.getString(MOVIE_TRAILER_SOURCE);

                Log.d(LOG_TAG, "Movie data :" + i + "\n" + resultStrs[i]);
            }
            if(movieReviewArray!= null && movieReviewArray.length() > 0) {
                movieReviewsParcels = new MovieReviewsParcel[movieReviewArray.length()];
                Log.d(LOG_TAG,"movie reviews : " + movieReviewArray.length());
                for(int j = 0; j < movieReviewArray.length(); j++) {
                    JSONObject movieReviewData = movieReviewArray.getJSONObject(j);
                    movieReviewsParcels[j] = new MovieReviewsParcel(
                            movieReviewData.getString(MOVIE_REVIEW_AUTHOR),
                            movieReviewData.getString(MOVIE_REVIEWS_CONTENT)
                            );

                }
                movieReviewList = new ArrayList<MovieReviewsParcel>(Arrays.asList(movieReviewsParcels));
            }
            return resultStrs;
        }


        @Override
        protected String[] doInBackground(Integer... params) {

            if(params.length == 0)
                return  null;

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String movieJsonStr = null;


            try {
                final String MOVIES_BASE_URL = BASE_URL + params[0] + "?";
                final String API_PARAM = "api_key";
                final String APPEND_PARAMS = "append_to_response";
                final String APPEND_TRAILERS_REVIEWS = "trailers,reviews";
                String api_key = getActivity().getResources().getString(R.string.api_key);

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, api_key)
                        .appendQueryParameter(APPEND_PARAMS,APPEND_TRAILERS_REVIEWS)
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
        protected void onPostExecute(final String[] result) {
           if(result != null) {
               movieTrailers = result;
               setmovieTrailers(result);
               setmovieReviews(movieReviewList);
           }

        }
    }

    private void watchYoutubeVideo(String id){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            getActivity().startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+id));
            getActivity().startActivity(intent);
        }
    }

    private void setmovieTrailers(final String[] trailers) {

        if(trailers == null )
            return;
        if (trailers != null && trailers.length != 0 && mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent(trailers[0]));
        }
        final ViewGroup viewGroup = trailersLayout;

        // Remove all existing trailers (everything but first child, which is the header)
        for (int i = viewGroup.getChildCount() - 1; i >= 1; i--) {
            viewGroup.removeViewAt(i);
        }

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        boolean hasTrailers = true;

        for (int i= 0; i < trailers.length; i++) {

            final View trailerView = inflater
                    .inflate(R.layout.trailer_single_item, viewGroup, false);
            final TextView trailerTitle = (TextView) trailerView
                    .findViewById(R.id.trailerTitle);
            final ImageButton trailerPlay = (ImageButton) trailerView
                    .findViewById(R.id.trailerPlay);
            hasTrailers = true;
            trailerTitle.setText(String.format(Locale.US, "Trailer %d", i));
            final String source = trailers[i];
            trailerPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchYoutubeVideo(source);
                    Log.d(LOG_TAG, "clicked on source :" + source);
                }
            });
            viewGroup.addView(trailerView);
        }

        viewGroup.setVisibility(hasTrailers ? View.VISIBLE : View.GONE);

    }

    private void setmovieReviews(ArrayList<MovieReviewsParcel> reviews) {

        if(reviews == null || reviews.size() == 0 )
            return;

        final ViewGroup viewGroup = reviewsLayout;

        // Remove all existing trailers (everything but first child, which is the header)
        for (int i = viewGroup.getChildCount(); i > 0; i--) {
            viewGroup.removeViewAt(i);
        }

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        for (int i= 0; i < reviews.size(); i++) {
            MovieReviewsParcel review = reviews.get(i);
            final View reviewView = inflater
                    .inflate(R.layout.review_single_item, viewGroup, false);
            final TextView authorView = (TextView) reviewView
                    .findViewById(R.id.reviewAuthor);
            final TextView contentView = (TextView) reviewView
                    .findViewById(R.id.reviewContent);
            final String author =review.author;
            final String content =review.content;
            authorView.setText(author);
            contentView.setText(content);

            viewGroup.addView(reviewView);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(MOVIE_TRAILERS_KEY, movieTrailers);
        outState.putParcelableArrayList(MOVIE_REVIEWS_KEY, movieReviewList);
    }

    private void handleFavorite(int movie_id, FloatingActionButton favoriteButton) {

        SharedPreferences prefs = getActivity().getSharedPreferences(Utility.SHARED_PREFS_MOVIE_APP,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> movieIdSet = getFavorites(prefs);
        Log.d(LOG_TAG,"favs from prefs with size : " + movieIdSet.size());
        String movieId = String.valueOf(movie_id);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_star_white_48dp);
        drawable = DrawableCompat.wrap(drawable);
        if(movieIdSet.contains(movieId)) {

            if(movieIdSet.remove(movieId)) {
                Log.d(LOG_TAG, "remove movie ID from favs");
            }
            DrawableCompat.setTint(drawable,getResources().getColor(R.color.white));
            favoriteButton.setImageDrawable(drawable);
            favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                    getColor(R.color.accent_color)));

        } else {
           if(movieIdSet.add(movieId)) {
                Log.d(LOG_TAG, "ad movie ID to favs");
            }
            DrawableCompat.setTint(drawable, Color.RED);
            favoriteButton.setImageDrawable(drawable);
            favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                    getColor(android.R.color.white)));

        }
        editor.putStringSet(Utility.MOVIE_FAV_KEY, movieIdSet);
        Log.d(LOG_TAG, "current  favs " + movieIdSet.size());
        editor.commit();

    }

    private Set<String> getFavorites(SharedPreferences prefs) {
        Set<String> movieIdSet = prefs.getStringSet(Utility.MOVIE_FAV_KEY, null);
        if(movieIdSet == null) {
            movieIdSet = new HashSet<String>();
        }
        return movieIdSet;
    }

    private void checkIfFavorite(Integer movie_id,FloatingActionButton favoriteButton) {
            String id = String.valueOf(movie_id);
        SharedPreferences prefs = getActivity().getSharedPreferences(Utility.SHARED_PREFS_MOVIE_APP,
                Context.MODE_PRIVATE);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_star_white_48dp);
        drawable = DrawableCompat.wrap(drawable);
            if(getFavorites(prefs).contains(id)) {
                DrawableCompat.setTint(drawable, Color.RED);
                favoriteButton.setImageDrawable(drawable);
                favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                        getColor(android.R.color.white)));

            } else {

                DrawableCompat.setTint(drawable, getResources().getColor(R.color.white));
                favoriteButton.setImageDrawable(drawable);
                favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                        getColor(R.color.accent_color)));
            }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider)
                MenuItemCompat.getActionProvider(menuItem);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent createShareForecastIntent(String source) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + source);
        return shareIntent;
    }
}
