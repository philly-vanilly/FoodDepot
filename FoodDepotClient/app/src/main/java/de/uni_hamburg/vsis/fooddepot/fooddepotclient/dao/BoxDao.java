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
import java.util.Objects;
import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.SortingSelector;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.SortingService;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;

/**
 * Created by Phil on 01.07.2016.
 */
public abstract class BoxDao {
    private static final String TAG = "BoxDao";
    protected BoxesActivity mContext;
    protected List<Box> mBoxes;

    public BoxDao(BoxesActivity context, List<Box> boxes) {
        mContext = context;
        mBoxes = boxes;
    }

    public abstract void getNumberOfBoxesMatchingString(String searchString, int fetchedBoxes, int numberOfBoxes, String authToken, double lat1, double lon1);
    public abstract List<Box> getNumberOfEmptyBoxes(String searchString, int fetchedBoxes, int numberOfBoxes, String queryId, double lat1, double lon1);
    public abstract Drawable getPhotoForBox(String boxId);
    public abstract Box getBoxById (String boxId);

    public void sortBySelection(SortingSelector selector) {
        SortingService.sortBySelection(selector, mBoxes);
    }

    public void addBoxes(List<Box> boxes){
        for (Box box : boxes) {
            Integer positionInList = getPosition(box.getId());
            if (positionInList == -1) {
                mBoxes.add(box);
            } else {
                mBoxes.set(positionInList, box);
            }
        }
        SortingService.sortBySelection(SortingSelector.NAME, mBoxes);
    }

    public Integer getPosition(String id) {
        Integer result = -1;
        Box box = getBox(id);
        if (box != null) {
            result = mBoxes.indexOf(box);
        }
        return result;
    }

    public Box getBox(String id) {
        for (Box boxIter : mBoxes) {
            if (Objects.equals(id, boxIter.getId())) {
                return boxIter;
            }
        }
        return null;
    }

    public void updateDistanceForAllBoxes(Location lastLocation){
        double lon1 = lastLocation.getLongitude();
        double lat1 = lastLocation.getLatitude();

        for (Box box : mBoxes) {
            double lon2 = box.getLongitude();
            double lat2 = box.getLatitude();

            if (lat1 > 90 || lat1 < -90 || lon1 > 180 || lon1 < -180){
                Log.e(TAG, "Device-User " + box.getId() + " has invalid coordinates: " + lat1 + " and " + lon1);
            } else if (lat2 > 90 || lat2 < -90 || lon2 > 180 || lon2 < -180){
                Log.e(TAG, "Box " + box.getId() + " has invalid coordinates: " + lat2 + " and " + lon2);
            }

            Location locationA = new Location("User-Point");
            locationA.setLatitude(lat1);
            locationA.setLongitude(lon1);
            Location locationB = new Location("Box-Point");
            locationB.setLatitude(lat2);
            locationB.setLongitude(lon2);
            float distance = locationA.distanceTo(locationB); //distance in m
            box.setDistance(distance);
        }
    }

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
//        BigDecimal bd = box.getPrice().getBigIntValue();
//        bd.divide(new BigDecimal(100));
//        bd = bd.setScale(2, RoundingMode.HALF_UP);
//        return String.valueOf(bd + " FD");
        return box.getPrice().toString();
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
            Log.e(TAG, "Box " + box.getId() + " has invalid rating: " + fullRating);
            bd = new BigDecimal(0);
        }
        return bd.floatValue();
    }

    public String getFormattedDistanceForBox(Box box) {
        String result;
        float distance = box.getDistance();
        if (distance >= 1000) {
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
