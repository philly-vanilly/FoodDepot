package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import rest.BaseResponseHandler;
import rest.RestClient;
import rest.beans.Response;
import rest.beans.Box;
import rest.beans.User;


public class BoxesActivity extends AppCompatActivity implements LocationListener {
    private final static String TAG = "BoxesActivity";

    private FDepotGoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;
    private String mCurrentSearchString = "";

    private BoxesFragmentInterface currentBoxesView = null;


    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private boolean isMapMode = true; // TODO: persist on pause, stop, ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //finding and setting up a toolbar to replace actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //finding drawer view and binding events to actionbartoggle
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        // toggle.syncState(); //needed????


        //finding and setting up side menu and its items
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        // nvDrawer.setNavigationItemSelectedListener(this); //needed????
        setupDrawerContent(nvDrawer);

        // manages fragments (adding them to activities) and also manages the backstack of
        // saved fragment transactions
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentBoxesView = fragmentManager.findFragmentById(R.id.fragment_container);

        // fragment could already be in the list after being recreated by the FragmentManager
        // after allocating memory. But when it is null, create new. onStart() makes it visible,
        // onResume() returns it to foreground
        if (currentBoxesView == null) {
            if (isMapMode) {
                currentBoxesView = new BoxesAsMapFragment();
            } else {
                currentBoxesView = new BoxesAsListFragment();
            }
            fragmentManager.beginTransaction().add(R.id.fragment_container, currentBoxesView).commit();
        }

        mGoogleApiClient = new FDepotGoogleApiClient(this, this);

        User user = FDepotApplication.getApplication().getCurrentUser();
        if (user != null) {
            TextView emailTextView = (TextView) findViewById(R.id.emailTextView);
            TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
            if (emailTextView != null && usernameTextView != null) {
                emailTextView.setText(user.email);
                usernameTextView.setText(user.firstName + " " + user.lastName);
            }//end if email and text view not null
        }//end if user not null
    }

    private void setupDrawerContent (NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem){
                    //what to do when an item in the side menu is clicked
                    selectDrawerItem(menuItem);
                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            }
        );
    }

    public void selectDrawerItem(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.nav_switch_map_list:
                isMapMode = !isMapMode;
                Class fragmentClass = null;
                if (isMapMode){
                    fragmentClass = BoxesAsMapFragment.class;
                } else {
                    fragmentClass = BoxesAsListFragment.class;
                }
                Fragment fragment = null;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case R.id.nav_open_box:
                startActivity(new Intent(this, OpenBoxActivity.class));
                break;
            case R.id.nav_toggle_sell:
                //TODO: implement
                break;
            case R.id.nav_profile:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_manage:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                Log.e(TAG, "Selected drawer item is not implemented.");
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //version from internet:
        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        //pauls version:
        // if (item.getItemId() == R.id.action_toggle_list_view_mode) return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        //sync toggle state after onrestoreinstancestate
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        //pass configuration change to drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
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
        RestClient.search(keys, latitude, longitude, new BaseResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (responseBody != null) {
                    String responseAsString = new String(responseBody);
                    Log.d(TAG, "search box success:" + responseAsString);


                    Type collectionType = new TypeToken<Response<List<Box>>>() {}.getType();
                    Response<List<Box>> boxResponse = gson.fromJson(responseAsString,collectionType );


                    updateBoxFragment(boxResponse.data);

                } else {
                    Log.e(TAG, "search box success but resopnse body null");
                }
            }
        });
    }

    private void updateBoxFragment(List<Box> boxList) {
        if(boxList != null){
            currentBoxesView.updateBoxList(boxList);
        }
    }
}