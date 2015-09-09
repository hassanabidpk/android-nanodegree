package com.hassanabid.popularmoviesapp2;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * An activity representing a single movie detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link MovieDetailFragment}.
 */
public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(MovieDetailFragment.ID_KEY,
                    getIntent().getStringExtra(MovieDetailFragment.ID_KEY));
            arguments.putString(MovieDetailFragment.TITLE_KEY,
                    getIntent().getStringExtra(MovieDetailFragment.TITLE_KEY));
            arguments.putString(MovieDetailFragment.POSTER_KEY,
                    getIntent().getStringExtra(MovieDetailFragment.POSTER_KEY));
            arguments.putString(MovieDetailFragment.OVERVIEW_KEY,
                    getIntent().getStringExtra(MovieDetailFragment.OVERVIEW_KEY));
            arguments.putString(MovieDetailFragment.RELEASE_DATE_KEY,
                    getIntent().getStringExtra(MovieDetailFragment.RELEASE_DATE_KEY));
            arguments.putString(MovieDetailFragment.VOTES_KEY,
                    getIntent().getStringExtra(MovieDetailFragment.VOTES_KEY));

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
