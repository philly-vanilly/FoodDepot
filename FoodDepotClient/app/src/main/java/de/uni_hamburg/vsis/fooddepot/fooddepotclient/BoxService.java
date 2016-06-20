package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;


import rest.beans.Box;

/**
 * Created by Phil on 19.06.2016.
 * Class for retrieving Box information from basic box values with error checking
 */
public class BoxService {

    private static String TAG = "BoxService";

    private Box mBox;
    private View mItemView;
    private static HashMap<String, Integer> mFoodNamesOfSupportedIcons = initFoodNames();

    public BoxService(Box box, View itemView){
        mBox = box;
        mItemView = itemView;
    }


    private static HashMap<String, Integer> initFoodNames(){ //looks better (nice icons) and faster than enum, leave it
        HashMap<String, Integer> result = new HashMap<>();
        result.put("apple", R.drawable.ic_fruit_apple);
        result.put("apfel", R.drawable.ic_fruit_apple);
        result.put("äpfel", R.drawable.ic_fruit_apple);
        result.put("apricot", R.drawable.ic_fruit_apricot);
        result.put("aprikose", R.drawable.ic_fruit_apricot);
        result.put("banana", R.drawable.ic_fruit_banana);
        result.put("banane", R.drawable.ic_fruit_banana);
        result.put("kiwi", R.drawable.ic_fruit_kiwi);
        result.put("lemon", R.drawable.ic_fruit_lemon);
        result.put("limone", R.drawable.ic_fruit_lemon);
        result.put("mango", R.drawable.ic_fruit_mango);
        result.put("orange", R.drawable.ic_fruit_orange);
        result.put("peach", R.drawable.ic_fruit_peach);
        result.put("pflaume", R.drawable.ic_fruit_peach);
        result.put("pear", R.drawable.ic_fruit_pear);
        result.put("birne", R.drawable.ic_fruit_pear);
        result.put("strawberry", R.drawable.ic_fruit_strawberry);
        result.put("strawberrie", R.drawable.ic_fruit_strawberry);
        result.put("erdbeere", R.drawable.ic_fruit_strawberry);
        result.put("tomato", R.drawable.ic_fruit_tomato);
        result.put("tomate", R.drawable.ic_fruit_tomato);
        return result;
    }

    /**
     * Image to display as thumbnail for a food-box. Either transmitted value from server or icon
     * based on content type or (if content not found) dummy content icon
     * @return Image as Drawable
     */
    public Drawable getImageForBox() {
        Drawable drawable = null;
        drawable = (Drawable) mBox.getImage();
        if(drawable == null) { //TODO: perhaps some test to check if valid drawable
            String str = mBox.getContent();
            char lastChar = str.charAt(str.length() - 1);
            if (str != null && str.length() > 0 && (lastChar == 's' || lastChar == 'n')) { //german or english plural
                str = str.substring(0, str.length() - 1);
            }
            str = str.toLowerCase();
            Integer resourceId = null;
            resourceId = mFoodNamesOfSupportedIcons.get(str);
            if (resourceId != null) { //resource found
                drawable = ResourcesCompat.getDrawable(mItemView.getResources(), resourceId, null);
            } else { //dummy image
                drawable = ResourcesCompat.getDrawable(mItemView.getResources(), R.drawable.ic_fruit_dummy, null);
            }
        }
        return drawable;
    }

    /**
     * Rating for a box, between 0.0 and the number of stars, with 0.5, 1.5, ... being allowed.
     * @return rating in float with one decimal places
     */
    public float getRatingForBox() {
        double fullRating = -1;
        fullRating = mBox.getRating();

        BigDecimal bd = null;
        if (fullRating >= 0 && fullRating <= 3){ //allowed rating range
            bd = new BigDecimal(fullRating);
            bd = bd.multiply(new BigDecimal(2));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            bd = bd.divide(new BigDecimal(2.0f));
        } else {
            Log.e(TAG, "Invalid Rating for Box " + mBox.getId() + ": rating  = " + fullRating);
            bd = new BigDecimal(0);
        }

        return bd.floatValue();
    }

    //TODO: Multiple currencies, including FoodDoubloons
    public String getPriceForBox() {
        double fullPrice = mBox.getPrice();

        BigDecimal bd = new BigDecimal(fullPrice);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return String.valueOf(bd + " FD");
    }

    /**
     * Distance calculator from user- and box- latitude/longitude
     * @param lat1 User latitude
     * @param lon1 User longitude
     * @return distance in km or m
     */
    public String getDistanceForBox(double lat1, double lon1) {
        double lon2 = mBox.getLongitude();
        double lat2 = mBox.getLatitude();

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