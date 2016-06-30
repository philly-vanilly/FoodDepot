package de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;

/**
 * Created by Phil on 30.06.2016.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, BoxesActivity.class);
        startActivity(intent);
        finish();
    }
}
