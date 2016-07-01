package de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;

/**
 * This class handles complicated display logic (not just string formatting, which can be done in
 * own View classes) shared by multiple View classes.
 */
public class DisplayService {

    private static HashMap<String, Integer> mFoodNamesOfSupportedIcons = initFoodNames();

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
    public static Drawable getImageForBox(Box box, View itemView) {
        Drawable drawable = null;
        drawable = (Drawable) box.getImage();
        if(drawable == null) { //TODO: perhaps some test to check if valid drawable
            String str = box.getContent();
            char lastChar = str.charAt(str.length() - 1);
            if (str != null && str.length() > 0 && (lastChar == 's' || lastChar == 'n')) { //german or english plural
                str = str.substring(0, str.length() - 1);
            }
            str = str.toLowerCase();
            Integer resourceId = null;
            resourceId = mFoodNamesOfSupportedIcons.get(str);
            if (resourceId != null) { //resource found
                drawable = ResourcesCompat.getDrawable(itemView.getResources(), resourceId, null);
            } else { //dummy image
                drawable = ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_fruit_dummy, null);
            }
        }
        return drawable;
    }

    public static long MILLISECONDS_IN_DAYS = 1000 * 60 * 60 * 24;
    public static long MILLISECONDS_IN_HOURS = 1000 * 60 * 60;
    public static long MILLISECONDS_IN_MINUTES = 1000 * 60;

    public static void displayRemainingTime(long remainingTime, TextView textViewTimeLeft) {
        if (remainingTime > -1) {
            if (remainingTime / MILLISECONDS_IN_DAYS > 1){
                long finalDiff = TimeUnit.DAYS.convert(remainingTime, TimeUnit.MILLISECONDS);
                textViewTimeLeft.setText("Expires in: " + finalDiff + " days");
            } else if (remainingTime / MILLISECONDS_IN_HOURS > 1) {
                long finalDiff = TimeUnit.HOURS.convert(remainingTime, TimeUnit.MILLISECONDS);
                textViewTimeLeft.setText("Expires in: " + finalDiff + " hours");
            } else if (remainingTime / MILLISECONDS_IN_MINUTES > 1) {
                long finalDiff = TimeUnit.MINUTES.convert(remainingTime, TimeUnit.MILLISECONDS);
                textViewTimeLeft.setText("Expires in: " + finalDiff + " minutes");
                textViewTimeLeft.setTextColor(Color.RED);
            } else if (remainingTime > 0) {
                long finalDiff = TimeUnit.SECONDS.convert(remainingTime, TimeUnit.MILLISECONDS);
                textViewTimeLeft.setText("Expires in: " + finalDiff + " seconds");
                textViewTimeLeft.setTextColor(Color.RED);
            } else {
                textViewTimeLeft.setText("EXPIRED! Get Food For Free!");
                textViewTimeLeft.setTextColor(Color.RED);
            }
        } else {
            textViewTimeLeft.setText("Expires in: [NO DATA]");
        }
    }
}
