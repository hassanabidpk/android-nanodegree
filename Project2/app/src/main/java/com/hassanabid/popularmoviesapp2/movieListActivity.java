package com.hassanabid.popularmoviesapp2;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;


public class MovieListActivity extends AppCompatActivity
        implements MovieListFragment.OnMovieSelectedListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            // In two-pane mode
            // Create new fragment and transaction
            Fragment movieListFragment = new MovieListFragment();
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            Bundle arguments = new Bundle();
            arguments.putInt(MovieDetailFragment.ID_KEY, 1);
            transaction.replace(R.id.movie_list, movieListFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }


    @Override
    public void onMovieSelected(int position,int id, String title, String poster_path, String overview, String release_date, String votes) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(MovieDetailFragment.ID_KEY, id);
            arguments.putString(MovieDetailFragment.TITLE_KEY, title);
            arguments.putString(MovieDetailFragment.POSTER_KEY, poster_path);
            arguments.putString(MovieDetailFragment.OVERVIEW_KEY, overview);
            arguments.putString(MovieDetailFragment.RELEASE_DATE_KEY, release_date);
            arguments.putString(MovieDetailFragment.VOTES_KEY, votes);


            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode,

            Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
            intent.putExtra(MovieDetailFragment.ID_KEY,id);
            intent.putExtra(MovieDetailFragment.TITLE_KEY, title);
            intent.putExtra(MovieDetailFragment.POSTER_KEY, poster_path);
            intent.putExtra(MovieDetailFragment.OVERVIEW_KEY, overview);
            intent.putExtra(MovieDetailFragment.RELEASE_DATE_KEY, release_date);
            intent.putExtra(MovieDetailFragment.VOTES_KEY, votes);
            startActivity(intent);
        }
    }
}
