package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.R;

/**
 * Created by hassanabid on 12/7/15.
 */
public class FootballAppWidgetIntentService extends IntentService implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] FOOTBALL_SCORE_COLUMNS = {
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.LEAGUE_COL
    };
    // these indices must match the projection
    public static final int INDEX_COL_DATE = 0;
    public static final int INDEX_COL_HOME = 1;
    public static final int INDEX_COL_AWAY = 2;
    public static final int INDEX_COL_HOME_GOALS = 3;
    public static final int INDEX_COL_AWAY_GOALS = 4;
    public static final int INDEX_MATCH_ID = 5;
    public static final int INDEX_COL_LEAGUE = 6;


    public FootballAppWidgetIntentService() {

        super("FootballAppWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Retrieve all of the widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                FoortballAppWidgetSimpleProvider.class));

        Uri footballDateUri = DatabaseContract.scores_table.buildScoreWithDate();

        Date requiredDate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

        String[] today = new String[1];
        today[0] = mformat.format(requiredDate);
        Cursor data = getContentResolver().query(footballDateUri, FOOTBALL_SCORE_COLUMNS,
                "date",today,null);

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String homeTeam = data.getString(INDEX_COL_HOME);
        String awayTeam = data.getString(INDEX_COL_AWAY);
        int awayScore = data.getInt(INDEX_COL_AWAY_GOALS);
        int homeScore = data.getInt(INDEX_COL_HOME_GOALS);
        int footballMatchId = data.getInt(INDEX_MATCH_ID);
        String leagueName = data.getString(INDEX_COL_LEAGUE);


        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.football_simple_widget;
            String gameScore = Utilies.getScores(homeScore,awayScore);

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.home_name, homeTeam);
            views.setTextViewText(R.id.away_name, awayTeam);
            views.setTextViewText(R.id.score_textview, gameScore);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
