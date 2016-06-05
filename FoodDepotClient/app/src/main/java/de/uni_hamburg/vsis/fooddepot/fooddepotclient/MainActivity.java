package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import rest.RestClient;
import rest.beans.Response;
import rest.beans.Box;
import rest.beans.User;


public class MainActivity extends
        AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,


        LocationListener {
    private final static String TAG = "MainActivity";
    private static Gson mGson = new Gson();




    private FDepotGoogleApiClient mGoogleApiClient = null;




    private boolean mapMode = true;
    private Location mLastLocation = null;
    private String mCurrentSearchString = "";

    private BoxListFragmentInterface currentBoxListView = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        currentBoxListView = (BoxListFragmentInterface) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapMode = true;


        mGoogleApiClient = new FDepotGoogleApiClient(this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        User user  = FDepotApplication.getApplication().getCurrentUser();
        if(user != null){
            TextView emailTextView = (TextView)findViewById(R.id.emailTextView);
            TextView usernameTextView = (TextView)findViewById(R.id.usernameTextView);
            if(emailTextView != null && usernameTextView != null){
                emailTextView.setText(user.email );
                usernameTextView.setText(user.firstName + " "  + user.lastName);
            }

        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }




    //for the items in the Navigation menu
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_open_box) {
            openBox();
        } else if (id == R.id.nav_toggle_sell) {

        } else if (id == R.id.nav_profile) {
            showProfile();
        } else if (id == R.id.nav_manage) {
            showSettings();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //for the items in the AppBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_toggle_list_view_mode) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();
        if (null != searchView) {
//            searchView.setSearchableInfo(searchManager
//                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                mCurrentSearchString = newText;
                updateBoxList();
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                mCurrentSearchString = query;
                updateBoxList();
                return true;

            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }


    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openBox() {
        Intent intent = new Intent(this, OpenBoxActivity.class);
        startActivity(intent);
    }

    private void showProfile() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "new location received");
        mLastLocation = location;
        updateBoxList();
    }




    private void updateBoxList(){
        if(mLastLocation != null) {
            updateBoxList(mLastLocation.getLatitude(), mLastLocation.getLongitude(), mCurrentSearchString);
        }
    }

    private void updateBoxList(double latitude, double longitude, String keys) {
        RestClient.search(keys, latitude, longitude, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (responseBody != null) {
                    String responseAsString = new String(responseBody);
                    Log.d(TAG, "search box success:" + responseAsString);


                    Type collectionType = new TypeToken<Response<List<Box>>>() {}.getType();
                    Response<List<Box>> boxResponse = mGson.fromJson(responseAsString,collectionType );


                    updateBoxFragment(boxResponse.data);

                } else {
                    Log.e(TAG, "search box success but resopnse body null");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    Log.e(TAG, "search box failed:" + new String(responseBody));
                } else {
                    Log.e(TAG, "search box failed");
                }
            }
        });
    }

    private void updateBoxFragment(List<Box> boxList) {
        if(boxList != null){
            currentBoxListView.updateBoxList(boxList);
        }
    }
}
