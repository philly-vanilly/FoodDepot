package de.uni_hamburg.vsis.fooddepot.fooddepotclient.model;

/**
 * Created by Phil on 27.07.2016.
 */
public class DepotBeacon {
    private String mUUID;
    private int mMajor;
    private int mMinor;

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        mUUID = UUID;
    }

    public int getMajor() {
        return mMajor;
    }

    public void setMajor(int major) {
        mMajor = major;
    }

    public int getMinor() {
        return mMinor;
    }

    public void setMinor(int minor) {
        mMinor = minor;
    }
}
