package de.uni_hamburg.vsis.fooddepot.fooddepotclient.openbox;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

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
