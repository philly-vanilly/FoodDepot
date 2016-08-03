package de.uni_hamburg.vsis.fooddepot.fooddepotclient.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;

/**
 * Created by Phil on 31.07.2016.
 */
public class FoodDepotWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "FoodDepotWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e(TAG, "======== onUpdate ========");

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, FoodDepotWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);
    }
}
