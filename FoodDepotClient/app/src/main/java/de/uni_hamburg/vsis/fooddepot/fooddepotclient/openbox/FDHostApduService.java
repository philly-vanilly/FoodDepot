package de.uni_hamburg.vsis.fooddepot.fooddepotclient.openbox;

import android.app.Service;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.os.IBinder;

public class FDHostApduService extends HostApduService {
    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        byte [] id = {(byte)0xff};
        return id ;
    }
    @Override
    public void onDeactivated(int reason) {

    }
}
