package de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;

/**
 * Created by Phil on 19.06.2016.
 * Class for retrieving Box information from basic box values with error checking
 */
public class SortingService {

    private static Comparator currentComparator = null;
    private static String TAG = "SortingService";

    //Comparators for sorting boxes lists:
    public static final Comparator<Box> sBoxNameAscendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            String boxVal1 = box1.getName().toUpperCase();
            String boxVal2 = box2.getName().toUpperCase();
            return boxVal2.compareTo(boxVal1);
        }
    };
    public static final Comparator<Box> sBoxNameDescendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            String boxVal1 = box1.getName().toUpperCase();
            String boxVal2 = box2.getName().toUpperCase();
            return boxVal1.compareTo(boxVal2);
        }
    };
    public static final Comparator<Box> sBoxPriceAscendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            BigDecimal boxVal1 = box1.getPrice().getBigIntValue();
            BigDecimal boxVal2 = box2.getPrice().getBigIntValue();
            return boxVal2.compareTo(boxVal1);
        }
    };
    public static final Comparator<Box> sBoxPriceDescendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            BigDecimal boxVal1 = box1.getPrice().getBigIntValue();
            BigDecimal boxVal2 = box2.getPrice().getBigIntValue();
            return boxVal1.compareTo(boxVal2);
        }
    };
    public static final Comparator<Box> sBoxDistanceAscendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            Float boxVal1 = box1.getDistance();
            Float boxVal2 = box2.getDistance();
            return boxVal2.compareTo(boxVal1);
        }
    };
    public static final Comparator<Box> sBoxDistanceDescendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            Float boxVal1 = box1.getDistance();
            Float boxVal2 = box2.getDistance();
            return boxVal1.compareTo(boxVal2);
        }
    };
    public static final Comparator<Box> sBoxRatingAscendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            Double boxVal1 = box1.getOverallUserRating();
            Double boxVal2 = box2.getOverallUserRating();
            return boxVal1.compareTo(boxVal2);
        }
    };
    public static final Comparator<Box> sBoxRatingDescendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            Double boxVal1 = box1.getOverallUserRating();
            Double boxVal2 = box2.getOverallUserRating();
            return boxVal2.compareTo(boxVal1);
        }
    };

    public static void sortBySelection(SortingSelector selector, List<Box> boxes) throws RuntimeException {
        switch(selector){
            case NAME:
                if (currentComparator == null || currentComparator == sBoxNameAscendingComparator) {
                    Collections.sort(boxes, sBoxNameDescendingComparator);
                    currentComparator = sBoxNameDescendingComparator;
                } else if (currentComparator == sBoxNameDescendingComparator){
                    Collections.sort(boxes, sBoxNameAscendingComparator);
                    currentComparator = sBoxNameAscendingComparator;
                }  else {
                    Collections.sort(boxes, sBoxNameDescendingComparator);
                    currentComparator = sBoxNameDescendingComparator;
                }
                break;
            case PRICE:
                if (currentComparator == null || currentComparator == sBoxPriceAscendingComparator) {
                    Collections.sort(boxes, sBoxPriceDescendingComparator);
                    currentComparator = sBoxPriceDescendingComparator;
                } else if (currentComparator == sBoxPriceDescendingComparator){
                    Collections.sort(boxes, sBoxPriceAscendingComparator);
                    currentComparator = sBoxPriceAscendingComparator;
                }  else {
                    Collections.sort(boxes, sBoxPriceDescendingComparator);
                    currentComparator = sBoxPriceDescendingComparator;
                }
                break;
            case DISTANCE:
                if (currentComparator == null || currentComparator == sBoxDistanceAscendingComparator) {
                    Collections.sort(boxes, sBoxDistanceDescendingComparator);
                    currentComparator = sBoxDistanceDescendingComparator;
                } else if (currentComparator == sBoxDistanceDescendingComparator){
                    Collections.sort(boxes, sBoxDistanceAscendingComparator);
                    currentComparator = sBoxDistanceAscendingComparator;
                }  else {
                    Collections.sort(boxes, sBoxDistanceDescendingComparator);
                    currentComparator = sBoxDistanceDescendingComparator;
                }
                break;
            case RATING:
                if (currentComparator == null || currentComparator == sBoxRatingAscendingComparator) {
                    Collections.sort(boxes, sBoxRatingDescendingComparator);
                    currentComparator = sBoxRatingDescendingComparator;
                } else if (currentComparator == sBoxRatingDescendingComparator){
                    Collections.sort(boxes, sBoxRatingAscendingComparator);
                    currentComparator = sBoxRatingAscendingComparator;
                }  else {
                    Collections.sort(boxes, sBoxRatingDescendingComparator);
                    currentComparator = sBoxRatingDescendingComparator;
                }
                break;
            default:
                throw new RuntimeException("Invalid Toolbar Tab position");
        }
    }
}