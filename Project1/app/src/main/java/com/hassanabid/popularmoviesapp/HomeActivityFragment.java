package com.hassanabid.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public HomeActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

/*        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr;
        // Will contain the raw JSON response as a string.

        try {
        final String FORECAST_BASE_URL =
                "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String SORT_PARAM = "sort_by";
        final String API_PARAM = "api_key";
        String sort_order_default = "popularity.desc";
        String highest_rated = " vote_average.desc"
        String api_key = getActivity().getResources().getString(R.string.api_key);
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_PARAM, sort_order_default)
                .appendQueryParameter(API_PARAM, api_key)
                .build();

        URL url = new URL(builtUri.toString());

        // Create the request to OpenWeatherMap, and open the connection
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
//            return;
        }

        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // But it does make debugging a *lot* easier if you print out the completed
            // buffer for debugging.
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
//            return;
        }
            moviesJsonStr = buffer.toString();
    } catch (IOException e) {
        Log.e(LOG_TAG, "Error ", e);
        // If the code didn't successfully get the weather data, there's no point in attemping
        // to parse it.
//        return;
    } finally {
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e(LOG_TAG, "Error closing stream", e);
            }
        }
    }*/
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();



        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DATETIME = "dt";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < numDays; i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime = dayForecast.getLong(OWM_DATETIME);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

            }

            return resultStrs;
        }


        @Override
        protected String[] doInBackground(String... params) {

            if(params.length == 0)
                return  null;

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            String foreCastJsonStr = null;

            try {
//                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Seoul&mode=xml&units=metric&cnt=7");
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,format)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG,"built Uri  : " + builtUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

//              InputStream's are used for reading byte based data, one byte at a time.
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null) {
//                    foreCastJsonStr = null;
                    return null;
                }

//              Reads text from a character-input stream, buffering characters so as to provide for the efficient reading of
//              characters, arrays, and lines.
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while((line = bufferedReader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                if(buffer.length() == 0) {
                    foreCastJsonStr = null;
                    return null;
                }

                foreCastJsonStr = buffer.toString();
                Log.v(LOG_TAG,"Forcast JSOn data : " + foreCastJsonStr);

            } catch (IOException e) {
                Log.e("PlaceHolderFragment", "Error " + e);
                foreCastJsonStr = null;
            } finally {
                if(httpURLConnection != null)
                    httpURLConnection.disconnect();
                if(bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }

            }
            try {
                return getWeatherDataFromJson(foreCastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            if(result != null) {
//                mForecastAdapter.clear();
                // honeycomb and above
//                mForecastAdapter.addAll(result);
            }

        }
    }


}
