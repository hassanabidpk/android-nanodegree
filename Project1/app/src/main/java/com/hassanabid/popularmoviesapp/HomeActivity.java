package com.hassanabid.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class HomeActivity extends ActionBarActivity implements HomeActivityFragment.OnMovieSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }

    @Override
    public void onMovieSelected(int position, String title, String poster_path,
                                String overview, String release_date, String votes) {

        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.TITLE_KEY, title);
        intent.putExtra(DetailActivity.POSTER_KEY, poster_path);
        intent.putExtra(DetailActivity.OVERVIEW_KEY, overview);
        intent.putExtra(DetailActivity.RELEASE_DATE_KEY, release_date);
        intent.putExtra(DetailActivity.VOTES_KEY, votes);
        startActivity(intent);
    }
}
