package de.uni_hamburg.vsis.fooddepot.fooddepotclient.speech;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Objects;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.main.FDepotApplication;

/**
 * Persistently run a speech activator in the background.
 * Use {@link Intent}s to start and stop it
 * @author Greg Milette &#60;<a href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 */
public class SpeechActivationService extends Service {

    private static final String TAG = "SpeechActivationService";
    private static final String className = SpeechActivationService.class.getName();

    public static final String ACTIVATION_RESULT_INTENT_KEY = "ACTIVATION_RESULT_INTENT_KEY";
    public static final String ACTIVATION_RESULT_BROADCAST_NAME = "root.gast.playground.speech.ACTIVATION";

    /**
     * send this when external code wants the Service to stop
     */
    public static final String ACTIVATION_STOP_INTENT_KEY = "ACTIVATION_STOP_INTENT_KEY";

    public static final int NOTIFICATION_ID = 10298;
    private static SpeechActivationService mInstance;

    private boolean isStarted;

    private SpeechActivator activator;

    public static SpeechActivationService getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        isStarted = false;
    }

    public static Intent makeStartServiceIntent(Context context) {
        Intent i = new Intent(context, SpeechActivationService.class);
        return i;
    }

    public static Intent makeServiceStopIntent(Context context) {
        Intent i = new Intent(context, SpeechActivationService.class);
        i.putExtra(ACTIVATION_STOP_INTENT_KEY, true);
        return i;
    }

    /**
     * stop or start an activator based on the activator type and if an
     * activator is currently running
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra(ACTIVATION_STOP_INTENT_KEY)) {
                Log.d(TAG, "stop service intent");
                activated(false);
            }
            else {
                if (!isStarted) {
                    startDetecting();
                }
            }
        }

        // restart in case the Service gets canceled
        return START_REDELIVER_INTENT;
    }

    private void startDetecting() {
        activator = new SpeechActivator(this);
        Log.d(TAG, "started: " + activator.getClass().getSimpleName());
        isStarted = true;
        activator.detectActivation();
        startForeground(NOTIFICATION_ID, getNotification());
    }

    public void activated(boolean success) {
        // make sure the activator is stopped before doing anything else
        stopActivator();

        // always stop after receive an activation
        stopSelf();

        //update view //TODO: use  broadcastreceiver
        Activity currentActivity = FDepotApplication.getApplication().getCurrentActivity();
        if (currentActivity != null && currentActivity instanceof BoxesActivity){
            ((BoxesActivity) currentActivity).setupDrawerContent();
        }

        // broadcast result
        Intent intent = new Intent(ACTIVATION_RESULT_BROADCAST_NAME);
        intent.putExtra(ACTIVATION_RESULT_INTENT_KEY, success);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "On destroy");
        super.onDestroy();
        stopActivator();
        stopForeground(true);
    }

    private void stopActivator() {
        if (activator != null) {
            Log.d(TAG, "stopped: " + activator.getClass().getSimpleName());
            activator.stop();
            isStarted = false;
        }
    }

    private Notification getNotification() {
        Log.d(TAG, "Notification made");

        Activity currentActivity = FDepotApplication.getApplication().getCurrentActivity();
        Intent i = new Intent(currentActivity, SpeechActivationService.class);
        i.putExtra(ACTIVATION_STOP_INTENT_KEY, true);
        PendingIntent resultPendingIntent = PendingIntent.getService(currentActivity, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_mic_off, "Deactivate", resultPendingIntent);

        Notification notification = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_mic)  // the status icon
            .setTicker("TickerSample")  // the status text
            .setWhen(System.currentTimeMillis())  // the time stamp
            .setContentTitle("Food Depot Voice Control")  // the label of the entry
            .setContentText("Voice Control is Activated")  // the contents of the entry
            .addAction(action)
            .build();

        return notification;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Objects.equals(className, runningServiceInfo.service.getClassName())) {
                return true; // Package name matches, our service is running
            }
        }
        return false; // No matching package name found => Our service is not running
    }
}
