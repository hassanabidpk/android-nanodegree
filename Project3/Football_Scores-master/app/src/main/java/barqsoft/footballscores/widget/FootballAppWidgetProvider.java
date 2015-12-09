package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by csd on 11/23/15.
 */
public class FootballAppWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;

        for (int i = 0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.football_listview);
            views.setOnClickPendingIntent(R.id.w_container, pendingIntent);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                setRemoteAdapterforIceCream(views, context);
            }else{
                setRemoteAdapterforHoneCombBelow(views, context);
            }

            /*
            * Add more code ?
             */
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }

    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapterforIceCream(RemoteViews views, Context context) {

        views.setRemoteAdapter(R.id.football_scores_list, new Intent(context,
                FootballWidgetRemoteService.class));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setRemoteAdapterforHoneCombBelow(RemoteViews views, Context context) {

        views.setRemoteAdapter(0, R.id.football_scores_list, new Intent(context,
                FootballWidgetRemoteService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        int[] appWidgetIds=appWidgetManager.getAppWidgetIds(new ComponentName(context,getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.football_scores_list);
    }
}
