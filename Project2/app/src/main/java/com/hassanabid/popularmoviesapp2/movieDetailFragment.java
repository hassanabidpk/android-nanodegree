package com.hassanabid.popularmoviesapp2;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hassanabid.popularmoviesapp2.dummy.DummyContent;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String POSTER_KEY = "poster_path";
    public static final String OVERVIEW_KEY = "overview";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String VOTES_KEY = "votes";

    int movie_id;
    String title;
    String poster ;
    String overview;
    String release_date;
    String votes;
    private ViewGroup trailersLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
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

        if (poster.equals("null") || poster.equals(null) || poster.equals("")) {
            posterView.setImageResource(R.drawable.empty_photo);
        } else {
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w500/" + poster)
                    .into(posterView);
        }

        titleView.setText(title);
        dateView.setText(release_date);
        voteView.setText(votes);
        overViewTextView.setText(overview);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Drawable drawable = getResources().getDrawable(R.drawable.ic_star_white_48dp);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.RED);
                favoriteButton.setImageDrawable(drawable);
                favoriteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().
                        getColor(android.R.color.white)));
            }
        });

        Log.d(LOG_TAG, "Movie id : " + movie_id + " Title : " + title);
        setmovieTrailers(new String[3]);
        return rootView;
    }

    private void setmovieTrailers(String[] trailers) {

        if(trailers == null )
            return;

        final ViewGroup speakersGroup = trailersLayout;

        // Remove all existing trailers (everything but first child, which is the header)
        for (int i = speakersGroup.getChildCount() - 1; i >= 1; i--) {
            speakersGroup.removeViewAt(i);
        }

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        boolean hasTrailers = true;

        for (String trailer : trailers) {

            final View trailerView = inflater
                    .inflate(R.layout.trailer_single_item, speakersGroup, false);
            final TextView trailerTitle = (TextView) trailerView
                    .findViewById(R.id.trailerTitle);
            final ImageButton trailerPlay = (ImageButton) trailerView
                    .findViewById(R.id.trailerPlay);
            hasTrailers = true;
            speakersGroup.addView(trailerView);
        }

        speakersGroup.setVisibility(hasTrailers ? View.VISIBLE : View.GONE);

    }
}
