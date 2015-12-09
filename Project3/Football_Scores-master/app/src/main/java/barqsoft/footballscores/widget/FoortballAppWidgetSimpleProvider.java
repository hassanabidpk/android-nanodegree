package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by hassanabid on 12/7/15.
 */
public class FoortballAppWidgetSimpleProvider extends AppWidgetProvider {

    private static final String  LOG_TAG = FoortballAppWidgetSimpleProvider.class.getSimpleName();


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent scoreIntent = new Intent(context,FootballAppWidgetIntentService.class);
        context.startService(scoreIntent);
        Log.d(LOG_TAG, "update Football simple widget");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Intent scoreIntent = new Intent(context,FootballAppWidgetIntentService.class);
        context.startService(scoreIntent);
        Log.d(LOG_TAG, "simple Football widget options changed");
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(LOG_TAG, "enabled");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}
