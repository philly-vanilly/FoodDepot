package de.uni_hamburg.vsis.fooddepot.fooddepotclient.widget;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Random;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxDao;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.FoodDepotPermissions;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;

/**
 * Created by Phil on 02.08.2016.
 */
public class UpdateWidgetService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private BoxFactory mBoxFactory;

    private static final String TAG = "UpdateWidgetService";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private int[] allWidgetIds;
    private List<Box> mBoxes;


    @Override
    public void onCreate() {
        Log.i(TAG, "======== onCreate ========");

        mBoxFactory = BoxFactory.getFactory();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "======== onDestroy ========");

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "======== onStartCommand ========");
        allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        //alternative way:
//        ComponentName thisWidget = new ComponentName(getApplicationContext(), FoodDepotWidgetProvider.class);
//        int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);

        mBoxes = mBoxFactory.getBoxes();
        if (!mBoxes.isEmpty()) {
            updateUi(mBoxes);
        }
        //stopSelf();

        super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY;
    }

    private void updateUi(final List<Box> boxes){
        BoxDao boxDao = mBoxFactory.getBoxDao();
        for (int widgetId : allWidgetIds) {
            Box randomBox = boxes.get(new Random().nextInt(boxes.size()));

            RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.textViewPriceWidget, boxDao.getRoundedPriceForBox(randomBox));
            remoteViews.setTextViewText(R.id.textViewContentWidget, "(" + randomBox.getContent() + ")");
            remoteViews.setTextViewText(R.id.textViewOwnerNameWidget, randomBox.getOwnerName());
            remoteViews.setTextViewText(R.id.textViewNameWidget, randomBox.getName());
            remoteViews.setTextViewText(R.id.textViewDistanceWidget, boxDao.getFormattedDistanceForBox(randomBox));
            remoteViews.setTextViewText(R.id.ratingCountWidget, String.valueOf(randomBox.getUserRatingCount()));

            String stars = "";
            int numberStars = Math.round(boxDao.getRoundedOverallRatingForBox(randomBox));
            for (int i = 0; i < numberStars; i++) {
                stars += "*";
            }
            remoteViews.setTextViewText(R.id.ratingBarWidget, stars);

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(), FoodDepotWidgetProvider.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "======== onBind ========");

        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "======== onConnected ========");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "======== no Permission ========");
            PermissionRequester.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    FoodDepotPermissions.PERM_REQUEST_LOCATION_NOTIFICATION,
                    "FoodDepot Permission Request",
                    "Blease gib Permission!",
                    R.drawable.ic_carrot
            );
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "======== onConnectionSuspended ========");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "======== onLocationChanged ========");
        mLastLocation = location;

        if (mLastLocation != null) {
            DownloadBoxesTask task = new DownloadBoxesTask();
            task.execute();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "======== onConnectionFailed ========");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isGranted = false;
        for (int i = 0; i < grantResults.length; i++) {
            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && (grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                isGranted = true;
            }
        }
        if (isGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                onLocationChanged(mLastLocation);
            }
        }
        else
            Log.w("PasvLocListenerService", "ACCESS_FINE_LOCATION permission not granted. Location notifications will not be available.");
    }

    private class DownloadBoxesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... empty) {
            BoxDao boxDao = mBoxFactory.getBoxDao();
            boxDao.getNumberOfBoxesMatchingString("", 0, 20, "", mLastLocation.getLatitude(), mLastLocation.getLongitude());
            boxDao.updateDistanceForAllBoxes(mLastLocation);
            if (!mBoxes.isEmpty()) {
                updateUi(mBoxFactory.getBoxes());
            }
            return null;
        }
    }
}
