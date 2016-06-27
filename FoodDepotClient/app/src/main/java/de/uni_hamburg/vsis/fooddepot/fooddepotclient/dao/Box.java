package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by paul on 05.06.16.
 */
public class Box {
    private static final String TAG = "Box";

    //DTO attributes for serialization and REST-communication:
    private UUID mId;
    private String mName;
    private String mContent;
    private double mWeight; //in Kg
    private double mLatitude;
    private double mLongitude;
    private Drawable mImage;
    private double mOverallUserRating;
    private double mPrice;
    private String mAddress;
    private String mOwnerName;
    private double mTemperature;
    private String mSmell;
    private int mUserRatingCount;

    //UI helper attributes:
    private boolean mIsClicked;
    private float mDistance;

    public Box(){}

    public long makeReservation(String targetDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
        Date targetDate = null;
        try {
            targetDate = dateFormat.parse(targetDateString);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        Date nowDate = new Date();
        if (targetDate != null) {
            return targetDate.getTime() - nowDate.getTime();
        } else {
            return -1;
        }
    }

    /**
     * Getter for [see return]
     * @return Rounded Price for a Box as a String with the currency appended
     */
    public String getPriceForBox() {
        double fullPrice = getPrice();

        BigDecimal bd = new BigDecimal(fullPrice);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return String.valueOf(bd + " FD");
    }

    /**
     * Rating for a box, between 0.0 and the number of stars, with 0.5, 1.5, ... being allowed.
     * @return rating in float with one decimal places
     */
    public float getRoundedOverallRatingForBox() {
        double fullRating = -1;
        fullRating = getOverallUserRating();

        BigDecimal bd = null;
        if (fullRating >= 0 && fullRating <= 5){ //allowed rating range
            bd = new BigDecimal(fullRating);
            bd = bd.multiply(new BigDecimal(2));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            bd = bd.divide(new BigDecimal(2.0f));
        } else {
            Log.e(TAG, "Box " + getId().toString() + " has invalid rating: " + fullRating);
            bd = new BigDecimal(0);
        }
        return bd.floatValue();
    }

    /**
     * Distance calculator from user- and box- latitude/longitude. It sets the distance based on the
     * passed geolocation arguments and then returns the distance as a String.
     * @param lat1 Device-User latitude
     * @param lon1 Device-User longitude
     * @return distance in km or m
     */
    public String makeDistanceForBox(double lat1, double lon1) {
        double lon2 = getLongitude();
        double lat2 = getLatitude();

        if (lat1 > 90 || lat1 < -90 || lon1 > 180 || lon1 < -180){
            Log.e(TAG, "Device-User " + getId().toString() + " has invalid coordinates: " + lat1 + " and " + lon1);
            return "###";
        } else if (lat2 > 90 || lat2 < -90 || lon2 > 180 || lon2 < -180){
            Log.e(TAG, "Box " + getId().toString() + " has invalid coordinates: " + lat2 + " and " + lon2);
            return "###";
        }

        Location locationA = new Location("User-Point");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("Box-Point");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        float distance = locationA.distanceTo(locationB); //distance in km
        setDistance(distance);

        String result = "";
        if(distance >= 1000){
            BigDecimal bd = new BigDecimal(distance);
            bd = bd.divide(new BigDecimal(1000));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            result = String.valueOf(bd) + " km";
        } else {
            BigDecimal bd = new BigDecimal(distance);
            bd = bd.setScale(0, RoundingMode.HALF_UP);
            result = String.valueOf(bd) + " m";
        }

        return result;
    }


    public UUID getId() {
        return mId;
    }
    public void setId(UUID id) {
        mId = id;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
    public String getContent() {
        return mContent;
    }
    public void setContent(String content) {
        mContent = content;
    }
    public double getWeight() { return mWeight; }
    public void setWeight(double weight) { mWeight = weight; }
    public double getLatitude() {
        return mLatitude;
    }
    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
    public Drawable getImage() { return mImage; }
    public void setImage(Drawable image) { mImage = image; }
    public double getPrice() { return mPrice; }
    public void setPrice(double price) { mPrice = price; }
    public String getAddress() {
        return mAddress;
    }
    public void setAddress(String address) {
        mAddress = address;
    }
    public boolean isClicked() { return mIsClicked; }
    public void setClicked(boolean isClicked){ mIsClicked = isClicked; }
    public void setDistance(float distance) {
        mDistance = distance;
    }
    public float getDistance() {
        return mDistance;
    }
    public String getOwnerName() {
        return mOwnerName;
    }
    public void setOwnerName(String ownerName){ mOwnerName = ownerName; }
    public double getOverallUserRating() {
        return mOverallUserRating;
    }
    public void setOverallUserRating(double overallUserRating) {
        mOverallUserRating = overallUserRating;
    }
    public int getUserRatingCount() {
        return mUserRatingCount;
    }
    public void setUserRatingCount(int userRatingCount) {
        mUserRatingCount = userRatingCount;
    }
    public double getTemperature() {
        return mTemperature;
    }
    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }
    public String getSmell() {
        return mSmell;
    }
    public void setSmell(String smell) {
        mSmell = smell;
    }

    @Override
    public int hashCode() {
        return getId().hashCode() * 31;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Box))
            return false;
        return this.getId().equals(((Box) obj).getId());
    }
}
