package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.Account;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxFactoryMock;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivityInterface;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.BaseResponseHandler;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.RestClient;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.Response;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.FDepotApplication;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.FDepotGoogleApiClient;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.openbox.OpenBoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.settings.SettingsActivity;


public class BoxesActivity extends AppCompatActivity implements LocationListener , BoxActivityInterface {
    private final static String TAG = "BoxesActivity";

    private FDepotGoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;
    private String mCurrentSearchString = "";

    private BoxesFragmentInterface mCurrentBoxesView = null;
    private BoxesFragmentInterface mSecondBoxesView = null;
    private AppBarLayout mAppBarLayout = null;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationViewDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mIsMapMode = false; // TODO: persist on pause, stop, ...
    private static final String IS_MAP_MODE = "IsMapMode";
    private static final String LIST_FRAGMENT = "ListFragment";
    private static final String MAP_FRAGMENT = "MapFragment";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current state
        savedInstanceState.putBoolean(IS_MAP_MODE, mIsMapMode);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        mIsMapMode = savedInstanceState.getBoolean(IS_MAP_MODE);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mIsMapMode = savedInstanceState.getBoolean(IS_MAP_MODE);
        }

        Log.d(TAG, "=========== onCreate called ===========");
        int orientation = getResources().getConfiguration().orientation;
        Log.d(TAG, "=========== IS LANDSCAPE: " + (orientation == Configuration.ORIENTATION_LANDSCAPE) );
        Log.d(TAG, "=========== IS MAP MODE: " + (mIsMapMode) );
        Log.d(TAG, "=========== savedInstanceState: " + savedInstanceState);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_boxes);

        //finding and setting up a toolbar to replace actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        mAppBarLayout = (AppBarLayout)findViewById(R.id.app_bar_layout) ;

        //finding drawer view and binding events to actionbartoggle
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //finding and setting up side menu and its items
        mNavigationViewDrawer = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(mNavigationViewDrawer);
        if (mIsMapMode){
            mNavigationViewDrawer.getMenu().getItem(1).setTitle(R.string.currently_map_mode_set_to_list);   //getTitle() == getString(R.string.currently_list_mode_set_to_map)
        } else {
            mNavigationViewDrawer.getMenu().getItem(1).setTitle(R.string.currently_list_mode_set_to_map);
        }
        setupToolbarLayout();

        // dont setup another fragment if restored from previous state or else you get
        // overlapping fragments.
        // fragment could already be in the list after being recreated by the FragmentManager
        // after allocating memory. But when it is null, create new. onStart() makes it visible,
        // onResume() returns it to foreground

        String fragmentTag = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            try {
                if (mIsMapMode){
                    mCurrentBoxesView = recreateFragment(fragmentManager.findFragmentByTag(MAP_FRAGMENT), fragmentManager);
                    if (mCurrentBoxesView == null) {
                        mCurrentBoxesView = new BoxesAsMapFragment();
                    }
                    fragmentTag = MAP_FRAGMENT;
                } else {
                    mCurrentBoxesView = recreateFragment(fragmentManager.findFragmentByTag(LIST_FRAGMENT), fragmentManager);
                    if (mCurrentBoxesView == null) {
                        mCurrentBoxesView = new BoxesAsListFragment();
                    }
                    fragmentTag = LIST_FRAGMENT;
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            //stop showing menu title as toolbar title
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_boxes_container, (Fragment) mCurrentBoxesView, fragmentTag)
                    .addToBackStack(null)
                    .commit();

            if (orientation == Configuration.ORIENTATION_LANDSCAPE && findViewById(R.id.fragment_boxes_container_2) != null) {
                if (!mIsMapMode){
                    mSecondBoxesView = recreateFragment(fragmentManager.findFragmentByTag(MAP_FRAGMENT), fragmentManager);
                    if(mSecondBoxesView == null) {
                        mSecondBoxesView = new BoxesAsMapFragment();
                    }
                    fragmentTag = MAP_FRAGMENT;
                } else {
                    mSecondBoxesView = recreateFragment(fragmentManager.findFragmentByTag(LIST_FRAGMENT), fragmentManager);
                    if(mSecondBoxesView == null) {
                        mSecondBoxesView = new BoxesAsListFragment();
                    }
                    fragmentTag = LIST_FRAGMENT;
                }
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_boxes_container_2, (Fragment) mSecondBoxesView, fragmentTag)
                        .addToBackStack(null)
                        .commit();
            }
        }

        mGoogleApiClient = new FDepotGoogleApiClient(this, this);

        Account account = FDepotApplication.getApplication().getCurrentAccount();
        if (account != null) {
            TextView emailTextView = (TextView) findViewById(R.id.emailTextView);
            TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
            if (emailTextView != null && usernameTextView != null) {
                emailTextView.setText(account.getEmail());
                usernameTextView.setText(account.getFirstName() + " " + account.getLastName());
            }
        }
    }

    private void setupToolbarLayout(){
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT && mIsMapMode){
            findViewById(R.id.tabLayoutSortList).setVisibility(View.GONE);
            Log.d(TAG, "=========== setting toolbar to NO-tab mode ===========");
        } else {
            findViewById(R.id.tabLayoutSortList).setVisibility(View.VISIBLE);
            Log.d(TAG, "=========== setting toolbar to tab mode ===========");
        }
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)mAppBarLayout.getLayoutParams();
        params.setMargins( 0, 0, 0, 0);
        mAppBarLayout.setLayoutParams(params);
    }

    private void switchFragments(MenuItem menuItem){
        Log.d(TAG, "=========== switchFragments called ===========");
        String fragmentTag = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        try {
            if (menuItem.getTitle() == getString(R.string.currently_list_mode_set_to_map)){
                mCurrentBoxesView = recreateFragment(fragmentManager.findFragmentByTag(MAP_FRAGMENT), fragmentManager);
                if(mCurrentBoxesView == null) {
                    mCurrentBoxesView = new BoxesAsMapFragment();
                }
                fragmentTag = MAP_FRAGMENT;
                menuItem.setTitle(R.string.currently_map_mode_set_to_list);
                mIsMapMode = true;
                setupToolbarLayout();
            } else if (menuItem.getTitle() == getString(R.string.currently_map_mode_set_to_list)) {
                mCurrentBoxesView = recreateFragment(fragmentManager.findFragmentByTag(LIST_FRAGMENT), fragmentManager);
                if(mCurrentBoxesView == null) {
                    mCurrentBoxesView = new BoxesAsListFragment();
                }
                fragmentTag = LIST_FRAGMENT;
                menuItem.setTitle(R.string.currently_list_mode_set_to_map);
                mIsMapMode = false;
                setupToolbarLayout();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        //stop showing menu title as toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_boxes_container, (Fragment) mCurrentBoxesView, fragmentTag)
                .addToBackStack(null)
                .commit();

        fragmentTag = (mIsMapMode? LIST_FRAGMENT : MAP_FRAGMENT);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && findViewById(R.id.fragment_boxes_container_2) != null) {
            if (menuItem.getTitle() == getString(R.string.currently_list_mode_set_to_map)){
                mSecondBoxesView = recreateFragment(fragmentManager.findFragmentByTag(MAP_FRAGMENT), fragmentManager);
                if(mSecondBoxesView == null) {
                    mSecondBoxesView = new BoxesAsMapFragment();
                }
            } else if (menuItem.getTitle() == getString(R.string.currently_map_mode_set_to_list)) {
                mSecondBoxesView = recreateFragment(fragmentManager.findFragmentByTag(LIST_FRAGMENT), fragmentManager);
                if(mSecondBoxesView == null) {
                    mSecondBoxesView = new BoxesAsListFragment();
                }
            }
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_boxes_container_2, (Fragment) mSecondBoxesView, fragmentTag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private BoxesFragmentInterface recreateFragment(Fragment fragment, FragmentManager fragmentManager){
        if (fragment != null) {
            try {
                Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(fragment);
                Fragment newInstance = fragment.getClass().newInstance();
                newInstance.setInitialSavedState(savedState);
                return (BoxesFragmentInterface) newInstance;
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return null;
    }

    private void setupDrawerContent (NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem){
                    //what to do when an item in the side menu is clicked
                    selectDrawerItem(menuItem);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            }
        );
    }

    private void selectDrawerItem(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.nav_switch_map_list:
                switchFragments(menuItem);
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
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        //sync toggle state after onrestoreinstancestate
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        //pass configuration change to drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
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

        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        if (searchView != null) {
            View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                searchPlate.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
            } else {
                searchPlate.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            searchView.setIconifiedByDefault(true);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                mCurrentSearchString = newText;
                updateBoxList();
                return true;
            }

            public boolean onQueryTextSubmit(String searchString) {
                mCurrentSearchString = searchString;
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                BoxFactoryMock boxesFactory = BoxFactoryMock.get(BoxesActivity.this);

                searchString = searchString.length() > 11? searchString.substring(0, 9)+"..." : searchString;

                getSupportActionBar().setTitle(boxesFactory.getBoxes().size() + " \"" + searchString + "\" found");
                //show tooltip load more by scrolling to bottom or zooming out (mIsMapMode)
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
                Response<List<Box>> boxResponse = gson.fromJson(responseAsString, collectionType);

                updateBoxFragment(boxResponse.data);

            } else {
                Log.e(TAG, "search box success but response body null");
            }
            }
        });
    }

    private void updateBoxFragment(List<Box> boxList) {
        if(boxList != null
        && mCurrentBoxesView != null){
            mCurrentBoxesView.updateBoxList(boxList);
        }
    }

    @Override
    public void requestBoxListUpdate() {
        updateBoxList();
    }
}