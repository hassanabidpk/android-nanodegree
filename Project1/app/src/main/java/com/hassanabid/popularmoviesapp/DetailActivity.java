package com.hassanabid.popularmoviesapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


public class DetailActivity extends ActionBarActivity {


    public static final String TITLE_KEY = "title";
    public static final String POSTER_KEY = "poster_path";
    public static final String OVERVIEW_KEY = "overview";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String VOTES_KEY = "votes";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String title = getIntent().getExtras().getString(TITLE_KEY);
        String poster = getIntent().getExtras().getString(POSTER_KEY);
        String overview = getIntent().getExtras().getString(OVERVIEW_KEY);
        String release_date = getIntent().getExtras().getString(RELEASE_DATE_KEY);
        String votes = getIntent().getExtras().getString(VOTES_KEY);

        TextView titleView = (TextView) findViewById(R.id.title);
        TextView dateView = (TextView) findViewById(R.id.releaseDate);
        TextView voteView = (TextView) findViewById(R.id.votes);
        ImageView posterView = (ImageView) findViewById(R.id.posterDetail);
        TextView overViewTextView = (TextView) findViewById(R.id.overviewText);

        if (poster.equals("null") || poster.equals(null) || poster.equals("")) {
            posterView.setImageResource(R.drawable.empty_photo);
        } else {
            Picasso.with(this)
                    .load("http://image.tmdb.org/t/p/w500/" + poster)
                    .into(posterView);
        }

        titleView.setText(title);
        dateView.setText(release_date);
        voteView.setText(votes);
        overViewTextView.setText(overview);

    }

}
