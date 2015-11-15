package it.jaschke.alexandria;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by hassanabid on 9/28/15.
 */
public class Utility {

    public static boolean isNetworkAvailable(Context c) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return  info != null && info.isConnectedOrConnecting();
    }
}
