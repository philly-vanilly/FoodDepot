package de.uni_hamburg.vsis.fooddepot.fooddepotclient.widget;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.FoodDepotPermissions;

public class PermissionRequester {
    private static final String TAG = "PermissionRequester";

    public static <T extends Context & OnRequestPermissionsResultCallback> void requestPermissions(final T context, String[] permissions, int requestCode, String notificationTitle, String notificationText, int notificationIcon) {
        ResultReceiver resultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())) {
            @Override
            protected void onReceiveResult (int resultCode, Bundle resultData) {
                String[] resultPermissions = resultData.getStringArray(FoodDepotPermissions.PERMISSIONS);
                int[] grantedResults = resultData.getIntArray(FoodDepotPermissions.GRANTED_RESULTS);
                context.onRequestPermissionsResult(resultCode, resultPermissions, grantedResults);
            }
        };

        Intent permIntent = new Intent(context, PermissionRequestActivity.class);
        permIntent.putExtra(FoodDepotPermissions.RESULT_RECEIVER, resultReceiver);
        permIntent.putExtra(FoodDepotPermissions.PERMISSIONS, permissions);
        permIntent.putExtra(FoodDepotPermissions.REQUEST_CODE, requestCode);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(permIntent);

        PendingIntent permPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(notificationIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(0)
                .setContentIntent(permPendingIntent)
                .setStyle(null);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestCode, builder.build());
    }


    public static class PermissionRequestActivity extends AppCompatActivity {
        ResultReceiver resultReceiver;
        String[] permissions;
        int requestCode;

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantedResults) {
            Bundle resultData = new Bundle();
            resultData.putStringArray(FoodDepotPermissions.PERMISSIONS, permissions);
            resultData.putIntArray(FoodDepotPermissions.GRANTED_RESULTS, grantedResults);
            resultReceiver.send(requestCode, resultData);
            finish();
        }

        @Override
        protected void onStart() {
            super.onStart();

            resultReceiver = this.getIntent().getParcelableExtra(FoodDepotPermissions.RESULT_RECEIVER);
            permissions = this.getIntent().getStringArrayExtra(FoodDepotPermissions.PERMISSIONS);
            requestCode = this.getIntent().getIntExtra(FoodDepotPermissions.REQUEST_CODE, 0);

            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }
}