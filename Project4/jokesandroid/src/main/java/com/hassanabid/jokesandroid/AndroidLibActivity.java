package com.hassanabid.jokesandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AndroidLibActivity extends AppCompatActivity {

    private static final String LOG_TAG = AndroidLibActivity.class.getSimpleName();
    public static final String JOKE_KEY = "joke";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "androidLibrary MainActivity");
        String joke = getIntent().getStringExtra(JOKE_KEY);
        if(joke != null)
            Toast.makeText(this,joke,Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"Something went wrong!",Toast.LENGTH_LONG).show();
    }
}
