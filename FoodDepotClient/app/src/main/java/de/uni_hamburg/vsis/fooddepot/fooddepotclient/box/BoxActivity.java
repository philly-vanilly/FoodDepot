package de.uni_hamburg.vsis.fooddepot.fooddepotclient.box;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;

/**
 * Details View for one Box and its content
 */
public class BoxActivity extends AppCompatActivity {
    private static final String TAG = "BoxActivity";

    //Serializable unique id to reference in other classes:
    public static final String EXTRA_BOX_ACTIVITY_ID = "de.uni_hamburg.vsis.fooddepot.fooddepotclient.BoxActivity_ID";

    private Toolbar mToolbar;
    private BoxFragmentInterface currentBoxView = null;


    /**
     * Custom intent maker based on UUID which is the value Boxes are checked for uniqueness with, inside equals()
     * NOTE: an intent extra passes values from a calling to a called activity
     */
    public static Intent makeIntent(Context context, UUID boxID) {
        Intent intent = new Intent(context, BoxActivity.class);
        intent.putExtra(EXTRA_BOX_ACTIVITY_ID, boxID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        //finding and setting up a toolbar to replace actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_box);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //navigating "Up" in the hierarchy

        if(findViewById(R.id.fragment_box_container) != null && savedInstanceState == null && currentBoxView == null) {
            try{
                if(true) {
                    currentBoxView = new BoxFragmentFilled();
                } else {
                    currentBoxView = new BoxFragmentEmpty();
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            ((Fragment) currentBoxView).setArguments(getIntent().getExtras());

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_box_container, (Fragment) currentBoxView)
                    .addToBackStack(null) //to save and restore state of fragment when going back/forth with fragments
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //Log.d("MENU_ITEM", item.getTitle().toString());
        int id = item.getItemId();
        switch(id){
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        UUID boxId = null;
        if (currentBoxView != null){
            Box box = currentBoxView.getBox();
            boxId = box.getId();
        }
        Intent boxesActivityIntent = BoxesActivity.makeIntent(this, boxId);
        startActivity(boxesActivityIntent);
    }
}
