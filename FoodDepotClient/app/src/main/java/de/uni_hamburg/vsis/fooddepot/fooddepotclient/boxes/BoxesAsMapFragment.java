package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.DisplayService;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.FDepotGoogleApiClient;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;

/**
 * Created by paul on 05.06.16.
 */
public class BoxesAsMapFragment extends SupportMapFragment implements OnMapReadyCallback, BoxesFragmentInterface {

    private final String TAG = "BoxesAsMapFragment";
    private GoogleMap mMap;
    private List<Marker> mMapMarkers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "============= ON MAP CREATE CALLED ================");
        super.onCreate(savedInstanceState);
        // calls onMapReady
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "============= ON MAP READY CALLED ================");
        mMap = googleMap;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d(TAG, "need to show rationale");
            } else {
                Log.d(TAG, " no need to show rationale");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FDepotGoogleApiClient.LOCATION_PERMISSION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
        try {
            Log.d(TAG, "============= SETTING LOCATION ================");
            // Enable MyLocation Layer of Google Map
            mMap.setMyLocationEnabled(true);
            // Get LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            // Get the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
            // Get Current Location
            Location myLocation = locationManager.getLastKnownLocation(provider);
            // set map type
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Get latitude of the current location
            double latitude = myLocation.getLatitude();
            // Get longitude of the current location
            double longitude = myLocation.getLongitude();
            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);
            // Show the current location in Google Map
            updateBoxList();
            // Zoom in the Google Map
            // mMap.animateCamera(CameraUpdateFactory.zoomTo(14)); //TODO: zoom as far as you need to see a number of boxes (displayable in the list for example)
            zoomToBoxSelection(latLng);
        } catch (SecurityException e) {
            Log.e(TAG, "no permission?");
        }
    }

    private void zoomToBoxSelection(LatLng userLatLng) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userLatLng);
        for (Marker marker : mMapMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        //converting dip to px:
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
        int padding = (int) px; // offset from edges of the map in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void updateBoxList() {
        List<Box> boxList = BoxFactory.getFactory().getBoxes();
        mMapMarkers = new ArrayList<>();
        Log.d(TAG, "============= UPDATE BOX LIST CALLED ================");
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (final Box box : boxList) {
            //TODO: Custom Marker
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(box.getLatitude(), box.getLongitude()))
                    .title(box.getName())
                    .snippet(box.getContent())
                    .icon(BitmapDescriptorFactory.fromResource(DisplayService.getImageIdForBox(box, getView())))
            );

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = BoxActivity.makeIntent(getContext(), box.getId());
                    startActivity(intent);
                }
            });

            mMapMarkers.add(marker);
            builder.include(new LatLng(box.getLatitude(), box.getLongitude()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }
}
