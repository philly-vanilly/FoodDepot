package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;
import java.util.Objects;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.configuration.ProfileActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.configuration.SettingsActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.CustomBeaconManager;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.FoodDepotConstants;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.login.LoginActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.main.FDepotApplication;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Account;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.speech.SpeechActivationService;

/**
 * Created by Phil on 12.08.2016.
 */
public class DrawerMenuActions extends BoxesActivity {
    private final static String TAG = "DrawerMenuActions";

    BoxesActivity mBoxesActivity;

    public DrawerMenuActions(BoxesActivity boxesActivity) {
        mBoxesActivity = boxesActivity;
    }

    public void mapListSwitch(MenuItem menuItem){
        Log.d(TAG, "=========== switchFragments called ===========");
        String fragmentTag = null;
        FragmentManager fragmentManager = mBoxesActivity.getSupportFragmentManager();

        if (menuItem.getTitle() == mBoxesActivity.getString(R.string.currently_list_mode_set_to_map)){
            mCurrentBoxesView = mBoxesActivity.recreateFragment(fragmentManager.findFragmentByTag(BoxesAsMapFragment.TAG), fragmentManager);
            if(mCurrentBoxesView == null) {
                mCurrentBoxesView = new BoxesAsMapFragment();
                mBoxesAsMapFragment = (BoxesAsMapFragment) mCurrentBoxesView;
            }
            fragmentTag = BoxesAsMapFragment.TAG;
            menuItem.setTitle(R.string.currently_map_mode_set_to_list);
            mIsMapMode = true;
        } else if (menuItem.getTitle() == mBoxesActivity.getString(R.string.currently_map_mode_set_to_list)) {
            mCurrentBoxesView = mBoxesActivity.recreateFragment(fragmentManager.findFragmentByTag(BoxesAsListFragment.TAG), fragmentManager);
            if(mCurrentBoxesView == null) {
                mCurrentBoxesView = new BoxesAsListFragment();
                mBoxesAsListFragment = (BoxesAsListFragment) mCurrentBoxesView;
            }
            fragmentTag = BoxesAsListFragment.TAG;
            menuItem.setTitle(R.string.currently_list_mode_set_to_map);
            mIsMapMode = false;
        }
        mBoxesActivity.setupAppBarLayout();

        //stop showing menu title as toolbar title
        mBoxesActivity.setActionBarTitleBasedOnQuery();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_boxes_container, (Fragment) mCurrentBoxesView, fragmentTag)
                .addToBackStack(null)
                .commit();

        fragmentTag = (mIsMapMode? BoxesAsListFragment.TAG : BoxesAsMapFragment.TAG);

        int orientation = mBoxesActivity.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && mBoxesActivity.findViewById(R.id.fragment_boxes_container_2) != null) {
            if (menuItem.getTitle() == mBoxesActivity.getString(R.string.currently_list_mode_set_to_map)){
                mSecondBoxesView = recreateFragment(fragmentManager.findFragmentByTag(BoxesAsMapFragment.TAG), fragmentManager);
                if(mSecondBoxesView == null) {
                    mSecondBoxesView = new BoxesAsMapFragment();
                    mBoxesAsMapFragment = (BoxesAsMapFragment) mSecondBoxesView;
                }
            } else if (menuItem.getTitle() == mBoxesActivity.getString(R.string.currently_map_mode_set_to_list)) {
                mSecondBoxesView = recreateFragment(fragmentManager.findFragmentByTag(BoxesAsListFragment.TAG), fragmentManager);
                if(mSecondBoxesView == null) {
                    mSecondBoxesView = new BoxesAsListFragment();
                    mBoxesAsListFragment = (BoxesAsListFragment) mSecondBoxesView;
                }
            }
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_boxes_container_2, (Fragment) mSecondBoxesView, fragmentTag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void beaconActiveSwitch(MenuItem menuItem) {
        mIsBeaconScanActivated = !mIsBeaconScanActivated;
        if (mIsBeaconScanActivated) {
            menuItem.setTitle(R.string.action_deactivate_beacon);
            if (ContextCompat.checkSelfPermission(mBoxesActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                //noinspection StatementWithEmptyBody
                if (ActivityCompat.shouldShowRequestPermissionRationale(mBoxesActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //TODO: Explain why permission needed
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(mBoxesActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, FoodDepotConstants.ACCESS_COARSE_LOCATION);
                }
            }
            mBeaconManager = new CustomBeaconManager(mBoxesActivity);
        } else {
            menuItem.setTitle(R.string.action_activate_beacon);
            if (mBeaconManager != null) {
                mBeaconManager.disconnect();
            }
        }
    }

    public void openProfile() {
        FDepotApplication fDepotApplication = FDepotApplication.getApplication();
        fDepotApplication.loadUser();
        Account account = fDepotApplication.getCurrentAccount();
        if (    account.getUsername().equals("") ||
                account.getFirstName().equals("") ||
                account.getLastName().equals("") ||
                account.getEmail().equals("") ||
                account.getPassword().equals("") ) {
            mBoxesActivity.startActivity(new Intent(mBoxesActivity, LoginActivity.class));
        } else {
            mBoxesActivity.startActivity(new Intent(mBoxesActivity, ProfileActivity.class));
        }
    }

    public void openSettings() {
        mBoxesActivity.startActivity(new Intent(mBoxesActivity, SettingsActivity.class));
    }


    public void speechActiveSwitch(MenuItem menuItem) {
        if(Objects.equals(menuItem.getTitle().toString(), mBoxesActivity.getString(R.string.action_activate_speech))){
            if (ActivityCompat.checkSelfPermission(mBoxesActivity, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(mBoxesActivity, new String[]{android.Manifest.permission.RECORD_AUDIO}, FoodDepotConstants.RECORD_AUDIO);
            }
            if (ActivityCompat.checkSelfPermission(mBoxesActivity, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                menuItem.setTitle(mBoxesActivity.getString(R.string.action_deactivate_speech));
                Intent i = SpeechActivationService.makeStartServiceIntent(mBoxesActivity);

                if (isSpeechRecognitionActivityPresented(mBoxesActivity)) {
                    mBoxesActivity.startService(i);
                } else {
                    installGoogleVoiceSearch(mBoxesActivity);
                }
            } else {
                //display no permission
            }
        } else {
            menuItem.setTitle(mBoxesActivity.getString(R.string.action_activate_speech));
            if(SpeechActivationService.isServiceRunning(mBoxesActivity)) {
                SpeechActivationService.getInstance().activated(false);

                //alternatively:
                //stopService(new Intent(this, SpeechActivationService.class));
            }
        }
    }


    /**
     * source: https://software.intel.com/en-us/articles/developing-android-applications-with-voice-recognition-features
     * Checks availability of speech recognizing Activity
     *
     * @param callerActivity – Activity that called the checking
     * @return true – if Activity there available, false – if Activity is absent
     */
    private static boolean isSpeechRecognitionActivityPresented(Activity callerActivity) {
        try {
            // getting an instance of package manager
            PackageManager pm = callerActivity.getPackageManager();
            // a list of activities, which can process speech recognition Intent
            List activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

            if (activities.size() != 0) {    // if list not empty
                return true;                // then we can recognize the speech
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return false; // we have no activities to recognize the speech
    }

    /**
     * source: https://software.intel.com/en-us/articles/developing-android-applications-with-voice-recognition-features
     * Asking the permission for installing Google Voice Search.
     * If permission granted – sent user to Google Play
     * @param callerActivity – Activity, that initialized installing
     */
    private static void installGoogleVoiceSearch(final Activity callerActivity) {

        // creating a dialog asking user if he want
        // to install the Voice Search
        Dialog dialog = new AlertDialog.Builder(callerActivity)
                .setMessage("For recognition it’s necessary to install 'Google Voice Search'")    // dialog message
                .setTitle("Install Voice Search from Google Play?")    // dialog header
                .setPositiveButton("Install", new DialogInterface.OnClickListener() {    // confirm button

                    // Install Button click handler
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // creating an Intent for opening applications page in Google Play
                            // Voice Search package name: com.google.android.voicesearch
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.voicesearch"));
                            // setting flags to avoid going in application history (Activity call stack)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            // sending an Intent
                            callerActivity.startActivity(intent);
                        } catch (Exception ex) {
                            // if something going wrong
                            // doing nothing
                        }
                    }})

                .setNegativeButton("Cancel", null)    // cancel button
                .create();

        dialog.show();    // showing dialog
    }
}
