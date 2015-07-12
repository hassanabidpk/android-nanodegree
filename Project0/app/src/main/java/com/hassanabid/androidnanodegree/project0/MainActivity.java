package com.hassanabid.androidnanodegree.project0;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String LOG = MainActivity.class.getSimpleName();

    Button spotifyApp;
    Button scoreApp;
    Button libraryApp;
    Button buildApp;
    Button xyzApp;
    Button capstoneApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spotifyApp = (Button) findViewById(R.id.button_spotify);
        scoreApp = (Button) findViewById(R.id.button_scores);
        libraryApp = (Button) findViewById(R.id.button_library);
        buildApp = (Button) findViewById(R.id.button_build);
        xyzApp = (Button) findViewById(R.id.button_xyz);
        capstoneApp = (Button) findViewById(R.id.button_capstone);

        spotifyApp.setOnClickListener(onButtonClickListener);
        scoreApp.setOnClickListener(onButtonClickListener);
        libraryApp.setOnClickListener(onButtonClickListener);
        buildApp.setOnClickListener(onButtonClickListener);
        xyzApp.setOnClickListener(onButtonClickListener);
        capstoneApp.setOnClickListener(onButtonClickListener);

    }

    private View.OnClickListener onButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.button_spotify:
                    Toast.makeText(MainActivity.this, String.format(Locale.US,
                                    getResources().getString(R.string.toast_message),
                                    getResources().getString(R.string.spotify_streamer)),
                                    Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_library:
                    Toast.makeText(MainActivity.this, String.format(Locale.US,
                                    getResources().getString(R.string.toast_message),
                                    getResources().getString(R.string.library_app)),
                                    Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_scores:
                    Toast.makeText(MainActivity.this, String.format(Locale.US,
                                    getResources().getString(R.string.toast_message),
                                    getResources().getString(R.string.scores_app)),
                                    Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_build:
                    Toast.makeText(MainActivity.this, String.format(Locale.US,
                                    getResources().getString(R.string.toast_message),
                                    getResources().getString(R.string.built_it_bigger)),
                                    Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_xyz:
                    Toast.makeText(MainActivity.this, String.format(Locale.US,
                                    getResources().getString(R.string.toast_message),
                                    getResources().getString(R.string.xyz_reader)),
                                    Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_capstone:
                    Toast.makeText(MainActivity.this, String.format(Locale.US,
                                    getResources().getString(R.string.toast_message),
                                    getResources().getString(R.string.captsone_app)),
                                    Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.invalid_option), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
