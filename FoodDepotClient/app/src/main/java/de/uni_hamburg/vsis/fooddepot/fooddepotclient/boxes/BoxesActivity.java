package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.Objects;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.speech.SpeechActivationService;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.FoodDepotConstants;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Account;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.SortingSelector;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.main.FDepotApplication;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.FDepotGoogleApiClient;


public class BoxesActivity extends AppCompatActivity implements LocationListener {
    private final static String TAG = "BoxesActivity";

    private FDepotApplication mThisApp;
    private FDepotGoogleApiClient mGoogleApiClient;

    public FDepotGoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    private String mCurrentSearchString = "";

    protected BoxesFragmentInterface mCurrentBoxesView;
    protected BoxesFragmentInterface mSecondBoxesView;
    protected BoxesAsListFragment mBoxesAsListFragment;
    protected BoxesAsMapFragment mBoxesAsMapFragment;
    private AppBarLayout mAppBarLayout;
    TabLayout mTabLayoutSortList;
    private Menu mOptionsMenu;

    private BoxFactory mBoxFactory;
    protected BeaconManager mBeaconManager;

    private DrawerMenuActions mDrawerMenuActions;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationViewDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    protected boolean mIsMapMode = false; // TODO: persist on pause, stop, ...
    private String mIdOfCurrentlySelectedBox;
    protected boolean mIsBeaconScanActivated = false;

    private static final String IS_MAP_MODE = "IsMapMode";
    //Serializable unique id to reference in other classes:
    public static final String EXTRA_BOXES_ACTIVITY_ID = "de.uni_hamburg.vsis.fooddepot.fooddepotclient.BoxesActivity_ID";

    public static Intent makeIntent(Context context, String boxID) {
        Intent intent = new Intent(context, BoxesActivity.class);
        intent.putExtra(EXTRA_BOXES_ACTIVITY_ID, boxID);
        return intent;
    }

    public DrawerMenuActions getDrawerMenuActions() {
        return mDrawerMenuActions;
    }

    public void onBoxSelected(String boxUUID, String senderFragmentTag) {
        Log.d(TAG, "============== onBoxSelected called ===============");
        mIdOfCurrentlySelectedBox = boxUUID;
        if (Objects.equals(senderFragmentTag, BoxesAsListFragment.TAG) && mBoxesAsMapFragment != null) {
            mBoxesAsMapFragment.centerOnSelectedBox(boxUUID);
        } else if (Objects.equals(senderFragmentTag, BoxesAsMapFragment.TAG) && mBoxesAsListFragment != null) {
            mBoxesAsListFragment.centerOnSelectedBox(boxUUID);
        } else { // update both:
            if (mBoxesAsMapFragment != null ) {
                mBoxesAsMapFragment.centerOnSelectedBox(boxUUID);
            }
            if (mBoxesAsListFragment != null ) {
                mBoxesAsListFragment.centerOnSelectedBox(boxUUID);
            }
        }
    }

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisApp = (FDepotApplication) this.getApplicationContext();
        mDrawerMenuActions = new DrawerMenuActions(this);

        if (savedInstanceState != null) {
            mIsMapMode = savedInstanceState.getBoolean(IS_MAP_MODE);
        }

        setContentView(R.layout.activity_boxes);

        mGoogleApiClient = FDepotGoogleApiClient.getFDepotGoogleApiClient(this, this);
        mBoxFactory = BoxFactory.getFactory(); //this

        //finding and setting up a toolbar to replace actionbar
        mToolbar = (Toolbar) findViewById(R.id.boxesActivityToolbar);
        setSupportActionBar(mToolbar);
        setActionBarTitleBasedOnQuery();
        mAppBarLayout = (AppBarLayout)findViewById(R.id.boxesActivityAppBarLayout) ;

        //finding drawer view and binding events to actionbartoggle
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //finding and setting up side menu and its items
        mNavigationViewDrawer = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent();
        setupAppBarLayout();

        //========================================================
        String fragmentTag;
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment oldMapFragment = fragmentManager.findFragmentByTag(BoxesAsMapFragment.TAG);
        Fragment.SavedState oldMapFragmentState = null;
        if (oldMapFragment != null) {
            oldMapFragmentState = fragmentManager.saveFragmentInstanceState(oldMapFragment);
            fragmentManager.beginTransaction().remove(oldMapFragment).commit();
        }
        Fragment newMapFragment = new BoxesAsMapFragment();
        if (oldMapFragmentState != null){
            newMapFragment.setInitialSavedState(oldMapFragmentState);
        }

        Fragment oldListFragment = fragmentManager.findFragmentByTag(BoxesAsListFragment.TAG);
        Fragment.SavedState oldListFragmentState = null;
        if (oldListFragment != null) {
            oldListFragmentState = fragmentManager.saveFragmentInstanceState(oldListFragment);
            fragmentManager.beginTransaction().remove(oldListFragment).commit();
        }
        Fragment newListFragment = new BoxesAsListFragment();
        if (oldListFragmentState != null){
            newListFragment.setInitialSavedState(oldListFragmentState);
        }

        if (mIsMapMode){
            fragmentTag = BoxesAsMapFragment.TAG;
            mCurrentBoxesView = (BoxesFragmentInterface) newMapFragment;
            mBoxesAsMapFragment = (BoxesAsMapFragment) mCurrentBoxesView;
        } else {
            fragmentTag = BoxesAsListFragment.TAG;
            mCurrentBoxesView = (BoxesFragmentInterface) newListFragment;
            mBoxesAsListFragment = (BoxesAsListFragment) mCurrentBoxesView;
        }
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_boxes_container, (Fragment) mCurrentBoxesView, fragmentTag)
                .commit();

        if (findViewById(R.id.fragment_boxes_container_2) != null) {
            if (!mIsMapMode){
                fragmentTag = BoxesAsMapFragment.TAG;
                mSecondBoxesView = (BoxesAsMapFragment) newMapFragment;
                mBoxesAsMapFragment = (BoxesAsMapFragment) mSecondBoxesView;
            } else {
                fragmentTag = BoxesAsListFragment.TAG;
                mSecondBoxesView = (BoxesAsListFragment) newListFragment;
                mBoxesAsListFragment = (BoxesAsListFragment) mSecondBoxesView;
            }
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_boxes_container_2, (Fragment) mSecondBoxesView, fragmentTag)
                    .commit();
        }

        Account account = FDepotApplication.getApplication().getCurrentAccount();
        if (account != null) {
            TextView emailTextView = (TextView) findViewById(R.id.emailTextView);
            TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
            if (emailTextView != null && usernameTextView != null) {
                emailTextView.setText(account.getEmail());
                usernameTextView.setText(account.getFirstName() + " " + account.getLastName());
            }
        }

        mTabLayoutSortList = (TabLayout) findViewById(R.id.tabLayoutSortList);

        mTabLayoutSortList.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                sortBoxList(tab);
                if (mBoxesAsListFragment != null) {
                    mBoxesAsListFragment.getBoxesListAdapter().notifyDataSetChanged(); //update whole list
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                sortBoxList(tab);
                if (mBoxesAsListFragment != null) {
                    mBoxesAsListFragment.getBoxesListAdapter().notifyDataSetChanged(); //update whole list
                }
            }

            private void sortBoxList(TabLayout.Tab tab){
                switch (tab.getPosition()){
                    case 0:
                        mBoxFactory.getBoxDao().sortBySelection(SortingSelector.NAME);
                        break;
                    case 1:
                        mBoxFactory.getBoxDao().sortBySelection(SortingSelector.PRICE);
                        break;
                    case 2:
                        mBoxFactory.getBoxDao().sortBySelection(SortingSelector.DISTANCE);
                        break;
                    case 3:
                        mBoxFactory.getBoxDao().sortBySelection(SortingSelector.RATING);
                        break;
                    default:
                        Log.e(TAG, "No Handler For Tab Position!");
                }
            }
        });
    }

    protected void setupAppBarLayout(){
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT && !mIsMapMode){
            findViewById(R.id.tabLayoutSortList).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tabLayoutSortList).setVisibility(View.GONE);
        }
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)mAppBarLayout.getLayoutParams();
        params.setMargins( 0, 0, 0, 0);
        mAppBarLayout.setLayoutParams(params);

        //stop showing menu title as toolbar title //TODO: change to show search string
        setActionBarTitleBasedOnQuery();
    }

    protected BoxesFragmentInterface recreateFragment(Fragment fragment, FragmentManager fragmentManager){
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
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

    public void setupDrawerContent (){
        MenuItem navSpeechItem = mNavigationViewDrawer.getMenu().findItem(R.id.nav_speech);
        if (SpeechActivationService.isServiceRunning(this)){
            navSpeechItem.setTitle(getString(R.string.action_deactivate_speech));
        } else {
            navSpeechItem.setTitle(getString(R.string.action_activate_speech));
        }

        if (mIsMapMode){
            mNavigationViewDrawer.getMenu().getItem(0).setTitle(R.string.currently_map_mode_set_to_list);
        } else {
            mNavigationViewDrawer.getMenu().getItem(0).setTitle(R.string.currently_list_mode_set_to_map);
        }

        mNavigationViewDrawer.setNavigationItemSelectedListener(
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
                mDrawerMenuActions.mapListSwitch(menuItem);
                break;
            case R.id.nav_switch_beacon_active:
                mDrawerMenuActions.beaconActiveSwitch(menuItem);
                break;
            case R.id.nav_profile:
                mDrawerMenuActions.openProfile();
                break;
            case R.id.nav_settings:
                mDrawerMenuActions.openSettings();
                break;
            case R.id.nav_speech:
                mDrawerMenuActions.speechActiveSwitch(menuItem);
                break;
            default:
                Log.e(TAG, "Selected drawer item is not implemented.");
        }

        menuItem.setChecked(false); //doesnt work ....?

        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if it’s speech recognition results
        // and process finished ok
        if (requestCode == FoodDepotConstants.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            // receiving a result in string array
            // there can be some strings because sometimes speech recognizing inaccurate
            // more relevant results in the beginning of the list
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            // in “matches” array we holding a results... let’s show the most relevant
            if (matches.size() > 0){
                Toast.makeText(this, matches.get(0).toString(), Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            menu.setGroupVisible(R.id.boxesActivityBarButtons, false);
        } else {
            menu.setGroupVisible(R.id.boxesActivityBarButtons, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.barButtonName:
                    mBoxFactory.getBoxDao().sortBySelection(SortingSelector.NAME);
                    break;
                case R.id.barButtonPrice:
                    mBoxFactory.getBoxDao().sortBySelection(SortingSelector.PRICE);
                    break;
                case R.id.barButtonDistance:
                    mBoxFactory.getBoxDao().sortBySelection(SortingSelector.DISTANCE);
                    break;
                case R.id.barButtonRating:
                    mBoxFactory.getBoxDao().sortBySelection(SortingSelector.RATING);
                    break;
                default: Log.e(TAG, "No Handler For MenuItem Position!");
            }
            if (mBoxesAsListFragment != null) {
                mBoxesAsListFragment.getBoxesListAdapter().notifyDataSetChanged(); //update whole list in Adapter
            }
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
        setupDrawerContent(); //TODO: needed here???
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        mOptionsMenu = menu;
        final Context context = this;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        if (searchView != null) {
            View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                searchPlate.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
            } else {
                //noinspection deprecation
                searchPlate.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            searchView.setIconifiedByDefault(true);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                return false; //false = default behavior (showing autocomplete suggestions etc)
            }

            public boolean onQueryTextSubmit(String searchString) {
                mCurrentSearchString = searchString;
                SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }

                DownloadBoxesTask task = new DownloadBoxesTask(context);
                Integer fetchedBoxes = 0;
                Integer numberOfBoxes = 20;
                String authToken = "DUMMY_AUTH_TOKEN"; //TODO: replace dummy auth tokem
                Double latitude = mGoogleApiClient.getLastLocation().getLatitude();
                Double longitude = mGoogleApiClient.getLastLocation().getLongitude();

                task.execute(new DownloadBoxesParams(searchString, fetchedBoxes, numberOfBoxes, authToken, latitude, longitude));
                return true; //true = query was handled, false = query not handled; perform default action
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true; //true = menu is shown, false = menu is not shown anymore
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "=================== new location received =========================");
        mBoxFactory.getBoxDao().updateDistanceForAllBoxes(location);
        updateBoxesInFragments();
    }

    public void updateBoxesInFragments() {
        if( mCurrentBoxesView != null) {
            mCurrentBoxesView.updateBoxList();
        }
        if (mSecondBoxesView != null) {
            mSecondBoxesView.updateBoxList();
        }
    }

    protected void setActionBarTitleBasedOnQuery() {
        setSupportActionBar(mToolbar);
        if (mCurrentSearchString != null && !Objects.equals(mCurrentSearchString, "")) {
            getSupportActionBar().setTitle(mBoxFactory.getBoxes().size() + " \"" + mCurrentSearchString + "\" found");
        } else {
            getSupportActionBar().setTitle(null);
        }
    }

    @Override
    public void onBackPressed() {
        SearchView searchView = (SearchView) mOptionsMenu.findItem(R.id.menu_search).getActionView();
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    //TODO: move somewhere to be usable by other app components
    private class DownloadBoxesTask extends AsyncTask<DownloadBoxesParams, Void, Integer>{
        private final Context mContext;
        DownloadBoxesTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            String displayedString = mCurrentSearchString.length() > 11? mCurrentSearchString.substring(0, 9)+ "..." : mCurrentSearchString;
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Searching for: \"" + displayedString + "\"");

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutSortList);
            tabLayout.setVisibility(View.GONE);
            mOptionsMenu.setGroupVisible(R.id.boxesActivityBarButtons, false);

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.boxesActivityProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }

        @Override
        protected Integer doInBackground(DownloadBoxesParams... params) {
            DownloadBoxesParams paramsIter = params[0];
            Integer numberOfAddedBoxes = mBoxFactory.getBoxDao().getNumberOfBoxesMatchingString(
                paramsIter.getSearchString(),
                paramsIter.getFetchedBoxes(),
                paramsIter.getNumberOfBoxes(),
                paramsIter.getAuthToken(),
                paramsIter.getLatitude(),
                paramsIter.getLongitude()
            );
            return numberOfAddedBoxes;
        }

        @Override
        protected void onPostExecute(Integer isResultPulled) {
            super.onPostExecute(isResultPulled);

            setActionBarTitleBasedOnQuery();

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.boxesActivityProgressBar);
            progressBar.setVisibility(View.GONE);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mOptionsMenu.setGroupVisible(R.id.boxesActivityBarButtons, true);
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT && !mIsMapMode) {
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutSortList);
                tabLayout.setVisibility(View.VISIBLE);
            }

            mBoxFactory.getBoxDao().updateDistanceForAllBoxes(getGoogleApiClient().getLastLocation());
            updateBoxesInFragments();
        }
    }
}