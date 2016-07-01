package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;

/**
 * Created by Phil on 01.07.2016.
 */
public abstract class BoxDao {
    private static final String TAG = "BoxDao";

    public abstract List<Box> getNumberOfBoxesMatchingString(String searchString, int fetchedBoxes, int numberOfBoxes, UUID queryId, double lat1, double lon1);
    public abstract List<Box> getNumberOfEmptyBoxes(String searchString, int fetchedBoxes, int numberOfBoxes, UUID queryId, double lat1, double lon1);
    public abstract Drawable getPhotoForBox(UUID boxId);
    public abstract Box getBoxById (UUID boxId);

//    public Box getBoxById(UUID boxId) {
//        //TODO:
//        for (Box Box : BoxFactory.getFactory().getBoxes()) {
//            if (Box.getId().equals(boxId)) { //equals returns true for String value, == returns true only for same object reference
//                return Box;
//            }
//        }
//        return null;
//    }

    //TODO: target date doesnt make any sense
    public long makeReservation(Box box, String targetDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
        Date targetDate = null;
        try {
            targetDate = dateFormat.parse(targetDateString);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        Date nowDate = new Date();
        if (targetDate != null) {
            //TODO: write to box
            return targetDate.getTime() - nowDate.getTime();
        } else {
            return -1;
        }
    }

    /**
     * Getter for [see return]
     * @return Rounded Price for a Box as a String with the currency appended
     */
    public String getRoundedPriceForBox(Box box) {
        double fullPrice = box.getPrice();

        BigDecimal bd = new BigDecimal(fullPrice);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return String.valueOf(bd + " FD");
    }

    /**
     * Rating for a box, between 0.0 and the number of stars, with 0.5, 1.5, ... being allowed.
     * @return rating in float with one decimal places
     */
    public float getRoundedOverallRatingForBox(Box box) {
        double fullRating = -1;
        fullRating = box.getOverallUserRating();

        BigDecimal bd = null;
        if (fullRating >= 0 && fullRating <= 5){ //allowed rating range
            bd = new BigDecimal(fullRating);
            bd = bd.multiply(new BigDecimal(2));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            bd = bd.divide(new BigDecimal(2.0f));
        } else {
            Log.e(TAG, "Box " + box.getId().toString() + " has invalid rating: " + fullRating);
            bd = new BigDecimal(0);
        }
        return bd.floatValue();
    }

    /**
     * Distance calculator from user- and box- latitude/longitude. It sets the distance based on
     * trigonometry with passed geolocation arguments and then returns the distance as a String.
     * @param lat1 Device-User latitude
     * @param lon1 Device-User longitude
     * @return distance in km or m
     */
    public String getTriangularDistanceForBox(Box box, double lat1, double lon1) {
        double lon2 = box.getLongitude();
        double lat2 = box.getLatitude();

        if (lat1 > 90 || lat1 < -90 || lon1 > 180 || lon1 < -180){
            Log.e(TAG, "Device-User " + box.getId().toString() + " has invalid coordinates: " + lat1 + " and " + lon1);
            return "###";
        } else if (lat2 > 90 || lat2 < -90 || lon2 > 180 || lon2 < -180){
            Log.e(TAG, "Box " + box.getId().toString() + " has invalid coordinates: " + lat2 + " and " + lon2);
            return "###";
        }

        Location locationA = new Location("User-Point");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("Box-Point");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        float distance = locationA.distanceTo(locationB); //distance in km

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
}
