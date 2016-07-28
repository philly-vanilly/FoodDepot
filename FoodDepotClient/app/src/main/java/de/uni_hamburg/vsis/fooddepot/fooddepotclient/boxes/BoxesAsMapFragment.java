package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.DisplayService;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;

/**
 * Created by paul on 05.06.16.
 */
public class BoxesAsMapFragment extends SupportMapFragment implements OnMapReadyCallback, BoxesFragmentInterface {

    public final static String TAG = "BoxesAsMapFragment";
    private GoogleMap mMap;
    private HashMap<String, Marker> mMapMarkers;

    @Override
    public void centerOnSelectedBox(String boxUUID) {
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
        updateBoxList();
    }

    private void zoomToBoxSelection() {
        LatLng userLatLng = null;
        try {
            // Enable MyLocation Layer of Google Map
            mMap.setMyLocationEnabled(true);

            Location myLocation = ((BoxesActivity) getActivity()).getGoogleApiClient().getLastLocation();

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

        if (userLatLng != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(userLatLng);
            for (Marker marker : mMapMarkers.values()) {
                builder.include(marker.getPosition());
            }
            final LatLngBounds bounds = builder.build();

            // =========== set padding so you don't zoom in too much: ======================
            int dip = 40; //when boxes on map
            if (mMapMarkers.size() < 1){
                dip = 0;
                mMap.setPadding(1000, 1000, 1000, 1000);
            }

            //converting dip to px:
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
            final int padding = (int) px; // offset from edges of the map in pixels

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
        List<Box> boxList = BoxFactory.getFactory(getContext()).getBoxes();
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
                    BoxesActivity boxesActivity = (BoxesActivity) getContext();
                    boxesActivity.onBoxSelected(getUUID(marker), TAG);
                    return false; //returning false = default onClick-behavior (center and open InfoWindow)
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    @SuppressWarnings("ConstantConditions") Intent intent = BoxActivity.makeIntent(getContext(), getUUID(marker));
                    startActivity(intent);
                }
            });

            mMapMarkers.put(box.getId(), marker);
        }
        zoomToBoxSelection();
    }

    private String getUUID(Marker marker){
        for (Map.Entry<String, Marker> entry : mMapMarkers.entrySet()) {
            if (Objects.equals(entry.getValue().getPosition(), marker.getPosition())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
