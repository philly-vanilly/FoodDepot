package de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers;

import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.DepotBeacon;

/**
 * Created by Phil on 26.07.2016.
 */
public class CustomBeaconManager extends BeaconManager {
    private static final UUID ESTIMOTE_PROXIMITY_UUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("rid", ESTIMOTE_PROXIMITY_UUID, null, null);

    public CustomBeaconManager(final BoxesActivity boxesActivity) {
        super(boxesActivity);

        connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
//                startMonitoring(new Region("monitored region", ESTIMOTE_PROXIMITY_UUID, 23774, 21333));
//                startMonitoring(new Region("monitored region", ESTIMOTE_PROXIMITY_UUID, 32018, 41230));
                startMonitoring(ALL_ESTIMOTE_BEACONS);
            }
        });

        setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beaconList) {
//                Intent intent = BoxActivity.makeIntent(boxesActivity, BoxFactory.getFactory(boxesActivity).getBoxes().get(0).getId());
//                boxesActivity.startActivity(intent);
//                boxesActivity.onBoxSelected(BoxFactory.getFactory(boxesActivity).getBoxes().get(0).getId(), null);
                List<Box> boxes = BoxFactory.getFactory(boxesActivity).getBoxes();
                for (Beacon beacon : beaconList){
                    String beaconUUID = beacon.getProximityUUID().toString().toUpperCase();
                    int beaconMinor = beacon.getMinor();
                    int beaconMajor = beacon.getMajor();
                    for (Box box : boxes) {
                        DepotBeacon depotBeacon = box.getDepotBeacon();
                        if (depotBeacon != null) {
                            String depotBeaconUUID = depotBeacon.getUUID();
                            int depotBeaconMajor = depotBeacon.getMajor();
                            int depotBeaconMinor = depotBeacon.getMinor();
                            if (Objects.equals(beaconUUID, depotBeaconUUID) && Objects.equals(beaconMinor, depotBeaconMinor) && Objects.equals(beaconMajor, depotBeaconMajor) ) {
                                boxesActivity.onBoxSelected(box.getId(), null);
                                break; //when found stop iterating
                            }
                        }
                    }
                }
            }

            @Override
            public void onExitedRegion(Region region) {
            }
        });

    }
}
