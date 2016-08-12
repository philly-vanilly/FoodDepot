package de.uni_hamburg.vsis.fooddepot.fooddepotclient.openbox;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

public class FDHostApduService extends HostApduService { //HostApduService is a convenience Service class that can be extended to emulate an NFC card inside an Android service component

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        byte [] id = {(byte)0xff};
        return id ;
    }
    @Override
    public void onDeactivated(int reason) {

    }
}
