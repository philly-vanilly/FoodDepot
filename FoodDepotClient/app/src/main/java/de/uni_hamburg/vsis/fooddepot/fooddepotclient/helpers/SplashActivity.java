package de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.main.FDepotApplication;

/**
 * Created by Phil on 30.06.2016.
 */
public class SplashActivity extends AppCompatActivity {
    private FDepotApplication mThisApp;

    @Override
    protected void onResume() {
        super.onResume();
        mThisApp.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = mThisApp.getCurrentActivity();
        if (this.equals(currActivity))
            mThisApp.setCurrentActivity(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisApp = (FDepotApplication) this.getApplicationContext();

        Intent intent = new Intent(this, BoxesActivity.class);
        startActivity(intent);
        finish();
    }
}
