package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.Box;

/**
 * Created by paul on 05.06.16.
 */
public class BoxesAsMapFragment
        extends SupportMapFragment
        implements OnMapReadyCallback,
        BoxesFragmentInterface {

    private final String TAG = "BoxesAsMapFragment";

    private GoogleMap mMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            });
//            fab.setVisibility(View.INVISIBLE);

        getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d(TAG, "need to show rationale");

            } else {
                Log.d(TAG, " no need to show rationale");
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        34);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }



        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "no permission?");
        }
    }



    @Override
    public void updateBoxList(List<Box> boxList) {


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Box box : boxList) {


            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.box_location_marker);



            {
                mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(box.getLatitude(), box.getLongitude()))
                                .title(box.getName())
                                .snippet(box.getContent())
//                        .icon(bitmap)

                );
                builder.include(new LatLng(box.getLatitude(), box.getLongitude()));
            }

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }
}
