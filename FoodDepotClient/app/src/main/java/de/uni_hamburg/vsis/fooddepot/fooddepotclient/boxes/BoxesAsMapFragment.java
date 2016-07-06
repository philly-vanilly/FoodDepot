package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.DisplayService;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.FDepotGoogleApiClient;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;

/**
 * Created by paul on 05.06.16.
 */
public class BoxesAsMapFragment extends SupportMapFragment implements OnMapReadyCallback, BoxesFragmentInterface {

    public final static String TAG = "BoxesAsMapFragment";
    private GoogleMap mMap;
    private HashMap<UUID, Marker> mMapMarkers;

    @Override
    public void centerOnSelectedBox(UUID boxUUID) {
        Marker marker = mMapMarkers.get(boxUUID);
        LatLng markerLatLng = marker.getPosition();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(markerLatLng)
                .zoom(14)
                .build();
        marker.showInfoWindow();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }

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
        updateBoxList();
    }

    private void zoomToBoxSelection() {
        LatLng userLatLng = null;
        try {
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
            userLatLng = new LatLng(latitude, longitude);
        } catch (SecurityException e) {
            Log.e(TAG, "no permission?");
        }

        if (userLatLng != null || mMapMarkers.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(userLatLng);
            for (Marker marker : mMapMarkers.values()) {
                builder.include(marker.getPosition());
            }
            final LatLngBounds bounds = builder.build();

            //converting dip to px:
            Resources r = getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
            final int padding = (int) px; // offset from edges of the map in pixels

//            mMap.animateCamera(cameraUpdate);

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cameraUpdate);
                }
            });
        }
    }

    @Override
    public void updateBoxList() {
        List<Box> boxList = BoxFactory.getFactory(getActivity()).getBoxes();
        mMapMarkers = new HashMap<>();
        Log.d(TAG, "============= UPDATE BOX LIST CALLED ================");
        for (final Box box : boxList) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(box.getLatitude(), box.getLongitude()))
                    .title(box.getName())
                    .snippet(box.getContent())
                    .icon(BitmapDescriptorFactory.fromResource(DisplayService.getImageIdForBox(box, getView())))
            );

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    BoxesActivity boxesActivity = (BoxesActivity) getActivity();
                    boxesActivity.onBoxSelected(getUUID(marker), TAG);
                    return false; //returning false = default onClick-behavior (center and open InfoWindow)
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = BoxActivity.makeIntent(getContext(), getUUID(marker));
                    startActivity(intent);
                }
            });

            mMapMarkers.put(box.getId(), marker);
        }
        zoomToBoxSelection();
    }

    private UUID getUUID(Marker marker){
        Iterator<Map.Entry<UUID,Marker>> iter = mMapMarkers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, Marker> entry = iter.next();
            if (entry.getValue().getPosition().equals(marker.getPosition())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
