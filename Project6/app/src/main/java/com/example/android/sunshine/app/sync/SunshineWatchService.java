package com.example.android.sunshine.app.sync;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.ExecutionException;

/**
 * Created by hassanabid on 12/17/15.
 *
 * Get some help from here http://catinean.com/2015/03/28/creating-a-watch-face-with-android-wear-api-part-2/
 */
public class SunshineWatchService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = SunshineWatchService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    public static final String UPDATE_WATCHFACE = "watch_face_update";

    private static final String WEATHER_KEY_PATH = "/sunshineweather";
    private static final String WEATHER_KEY_ID = "weatherkeyid";
    private static final String MAX_TEMP_KEY = "maxTemp";
    private static final String MIN_TEMP_KEY = "minTemp";
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;

    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[] {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    public SunshineWatchService() {
        super("SunshineWatchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");

        if (intent != null
                && intent.getAction() != null
                && intent.getAction().equals(UPDATE_WATCHFACE)) {

            mGoogleApiClient = new GoogleApiClient.Builder(SunshineWatchService.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApiIfAvailable(Wearable.API)
                    .build();
            Log.d(LOG_TAG, "updateWatchFace - attempt connection");
//            Log.d(LOG_TAG, "updateWatchFace - Start has Wearable API : " +
//                    mGoogleApiClient.hasConnectedApi(Wearable.API));

            mGoogleApiClient.connect();
        }

    }


    @Override
    public void onConnected(Bundle bundle) {

        Log.d(LOG_TAG,"onConnected");
        String locationQuery = Utility.getPreferredLocation(this);

        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery,
                System.currentTimeMillis());

        // we'll query our contentProvider, as always
        Cursor cursor = getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION,
                null, null, null);
        Log.d(LOG_TAG,"cursor : " + cursor);

        if (cursor.moveToFirst()) {
            int weatherId = cursor.getInt(INDEX_WEATHER_ID);
            double high = cursor.getDouble(INDEX_MAX_TEMP);
            double low = cursor.getDouble(INDEX_MIN_TEMP);
            String minTemp = Utility.formatTemperature(this, low);
            String maxTemp = Utility.formatTemperature(this, high);

            final PutDataMapRequest mapRequest = PutDataMapRequest.create(WEATHER_KEY_PATH);
            mapRequest.getDataMap().putInt(WEATHER_KEY_ID, weatherId);
            mapRequest.getDataMap().putString(MAX_TEMP_KEY, maxTemp);
            mapRequest.getDataMap().putString(MIN_TEMP_KEY, minTemp);

            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, mapRequest.asPutDataRequest());

            Log.d(LOG_TAG,"maxTemp : " + maxTemp + " minTemp : " + minTemp + " pendingResult :" +
            pendingResult);
        }

        cursor.close();


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended :" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed :" + connectionResult);
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // The Wearable API is unavailable
            Log.d(LOG_TAG,"onConnectionFailed : The Wearable API is unavailable |" +  connectionResult);

        }

    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG,"onDestroy");
        super.onDestroy();
//        mGoogleApiClient.disconnect();
    }


}
