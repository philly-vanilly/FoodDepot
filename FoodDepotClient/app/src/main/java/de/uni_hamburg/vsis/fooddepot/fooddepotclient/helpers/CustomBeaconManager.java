package de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers;

import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;

/**
 * Created by Phil on 26.07.2016.
 */
public class CustomBeaconManager extends BeaconManager {
    private static final UUID ESTIMOTE_PROXIMITY_UUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("rid", ESTIMOTE_PROXIMITY_UUID, null, null);

    public CustomBeaconManager(final Context applicationContext) {
        super(applicationContext);

        connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                startMonitoring(new Region("monitored region", ESTIMOTE_PROXIMITY_UUID, 23774, 21333));
                startMonitoring(new Region("monitored region", ESTIMOTE_PROXIMITY_UUID, 32018, 41230));
            }
        });

        setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Intent intent = BoxActivity.makeIntent(applicationContext, BoxFactory.getFactory(applicationContext).getBoxes().get(0).getId());
                applicationContext.startActivity(intent);
            }

            @Override
            public void onExitedRegion(Region region) {
            }
        });

    }
}
