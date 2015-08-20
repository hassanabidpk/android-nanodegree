package com.hassanabid.popularmoviesapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeActivityFragment extends Fragment {

    private static final String API_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";
    public HomeActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String full_api_url = API_URL + getActivity().getResources().getString(R.string.api_key);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
