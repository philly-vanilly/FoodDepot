package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


public class MainActivity   extends
                            AppCompatActivity
                            implements
                            NavigationView.OnNavigationItemSelectedListener,
                            OnMapReadyCallback,
                            GoogleApiClient.ConnectionCallbacks,
                            GoogleApiClient.OnConnectionFailedListener,
                            ActivityCompat.OnRequestPermissionsResultCallback,
                            LocationListener
{
    private final static String TAG = "MainActivity";
    private static final int REQUEST_CHECK_SETTINGS = 123;


    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Marker userLocationMarker = null;

    LocationRequest mLocationRequest = null;

    private boolean mapMode = true;


    private final int LOCATION_PERMISSION = 34;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapMode = true;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(LocationServices.API)
                .build();


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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        createLocationRequest();
        updateBoxList(new LatLng(1.0, 1.0));

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Here, thisActivity is the current activity

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Log.d(TAG, "need to show rationale");

                } else {
                    Log.d(TAG, " no need to show rationale");
                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }

        }




        // Add a marker in Sydney and move the camera
        LatLng hamburg = new LatLng(53.4, 9.9);

//        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.box_location_marker);
//        if(bitmap != null) {
//            userLocationMarker = mMap.addMarker(new MarkerOptions().position(hamburg).title("Apfel Box").snippet("contains 500g apples")
//
//                    .icon(bitmap)
//            );
//        } else
        {
            Log.e(TAG, "box bitmapdescriptor is null");
            userLocationMarker = mMap.addMarker(new MarkerOptions().position(hamburg).title("Apfel Box").snippet("contains 500g apples")

            );
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hamburg));
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "no permission?");
        }
    }

    protected void startLocationUpdates() {
        try {
            if (mLocationRequest != null) {
                Log.d(TAG, "requesting location updates");
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } else {
                Log.d(TAG, "can't request location updates, request == null");
            }
        } catch (SecurityException e){
            Log.e(TAG, "can't request location updates no permission?");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.i(TAG, "permission request received");
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "we have location permission");
                    MainActivity.this.startLocationUpdates();


                } else {
                    Log.d(TAG, "we don't have location permission");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }


                }
                break;
            default:
                Log.d(TAG, "we have unknown permission");
                break;


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...

        Log.e(TAG, "couldn't connect to googleApiClient");
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
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "connected to googleApiClient");
        createLocationRequest();
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "temporally disconnected from googleApiClient");
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
        return true;
    }


    private void showSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openBox(){
        Intent intent = new Intent(this, OpenBoxActivity.class);
        startActivity(intent);
    }

    private void showProfile(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            //@Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.d(TAG, "location settings satisfied");


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        Log.d(TAG, "location not settings satisfied");
                        try {

                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "new location received");
        setUserLocationMarker(location);
        updateBoxList(new LatLng(location.getLatitude(), location.getLongitude()));
    }



    private void setUserLocationMarker(Location location){
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if(userLocationMarker != null){
            userLocationMarker.setPosition(userLocation);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.box_location_marker);
            userLocationMarker.setIcon(bitmap);

        }


    }

    private void updateBoxList(LatLng position){
        rest.RestClient.search("", position.latitude, position.longitude, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if(responseBody != null) {
                    Log.d(TAG, "search box success:" + new String(responseBody));
                } else {
                    Log.e(TAG, "search box success but resopnse body null" );
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(responseBody != null) {
                    Log.e(TAG, "search box failed:" + new String(responseBody));
                } else {
                    Log.e(TAG, "search box failed" );
                }

            }
        });

    }



}
