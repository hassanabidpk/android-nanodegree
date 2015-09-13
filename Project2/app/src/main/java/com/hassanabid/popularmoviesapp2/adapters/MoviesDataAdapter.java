package com.hassanabid.popularmoviesapp2.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hassanabid.popularmoviesapp2.MovieParcel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import  com.hassanabid.popularmoviesapp2.R;

/**
 * Created by hassanabid on 8/23/15.
 */
public class MoviesDataAdapter extends BaseAdapter {

    private static final String LOG_TAG = MoviesDataAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<MovieParcel> movieList;

    public MoviesDataAdapter(Context c, ArrayList<MovieParcel> movies) {
        this.mContext = c;
        this.movieList = movies;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return movieList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = mInflater.inflate(R.layout.movie_poster_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.poster);
            if ((mContext.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_LARGE ||
                    (mContext.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                viewHolder.imageView.setLayoutParams(new LinearLayout
                        .LayoutParams(210  ,300 ));
            } else {

                viewHolder.imageView.setLayoutParams(new LinearLayout
                    .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            }

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ImageView imageView = viewHolder.imageView;
        MovieParcel movie = movieList.get(position);
        String posterPath = movie.poster;
        if(posterPath != null) {
            String posterUrl = "http://image.tmdb.org/t/p/w500/" + posterPath;
            Picasso.with(mContext).load(posterUrl)
                    .into(imageView);
        } else {

            imageView.setImageResource(R.drawable.empty_photo);
        }
        return view;
    }

    public class ViewHolder {
        ImageView imageView;
    }
}
