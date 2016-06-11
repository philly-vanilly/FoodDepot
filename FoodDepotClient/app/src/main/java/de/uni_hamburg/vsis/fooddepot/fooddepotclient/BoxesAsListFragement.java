package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.app.Fragment;
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

import rest.beans.Box;

public class BoxesAsListFragement extends Fragment
        implements BoxesFragmentInterface {

    private final String TAG = "BoxesAsListFragement";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void updateBoxList(List<Box> boxList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Box box : boxList) {
        }

       //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }
}
