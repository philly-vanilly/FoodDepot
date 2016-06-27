package de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.Box;

/**
 * Created by Phil on 19.06.2016.
 * Class for retrieving Box information from basic box values with error checking
 */
public class SortingService {

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
            Double boxVal1 = box1.getPrice();
            Double boxVal2 = box2.getPrice();
            return boxVal2.compareTo(boxVal1);
        }
    };
    public static final Comparator<Box> sBoxPriceDescendingComparator = new Comparator<Box>() {
        public int compare(Box box1, Box box2) {
            Double boxVal1 = box1.getPrice();
            Double boxVal2 = box2.getPrice();
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

    public static Comparator sortByTabSelection(int tabPosition, Comparator currentComparator, List<Box> boxes) throws RuntimeException {
        switch(tabPosition){
            case 0: //name
                if (currentComparator == null || currentComparator == SortingService.sBoxNameAscendingComparator) {
                    Collections.sort(boxes, SortingService.sBoxNameDescendingComparator);
                    currentComparator = SortingService.sBoxNameDescendingComparator;
                } else if (currentComparator == SortingService.sBoxNameDescendingComparator){
                    Collections.sort(boxes, SortingService.sBoxNameAscendingComparator);
                    currentComparator = SortingService.sBoxNameAscendingComparator;
                }  else {
                    Collections.sort(boxes, SortingService.sBoxNameDescendingComparator);
                    currentComparator = SortingService.sBoxNameDescendingComparator;
                }
                break;
            case 1: //price
                if (currentComparator == null || currentComparator == SortingService.sBoxPriceAscendingComparator) {
                    Collections.sort(boxes, SortingService.sBoxPriceDescendingComparator);
                    currentComparator = SortingService.sBoxPriceDescendingComparator;
                } else if (currentComparator == SortingService.sBoxPriceDescendingComparator){
                    Collections.sort(boxes, SortingService.sBoxPriceAscendingComparator);
                    currentComparator = SortingService.sBoxPriceAscendingComparator;
                }  else {
                    Collections.sort(boxes, SortingService.sBoxPriceDescendingComparator);
                    currentComparator = SortingService.sBoxPriceDescendingComparator;
                }
                break;
            case 2: //distance
                if (currentComparator == null || currentComparator == SortingService.sBoxDistanceAscendingComparator) {
                    Collections.sort(boxes, SortingService.sBoxDistanceDescendingComparator);
                    currentComparator = SortingService.sBoxDistanceDescendingComparator;
                } else if (currentComparator == SortingService.sBoxDistanceDescendingComparator){
                    Collections.sort(boxes, SortingService.sBoxDistanceAscendingComparator);
                    currentComparator = SortingService.sBoxDistanceAscendingComparator;
                }  else {
                    Collections.sort(boxes, SortingService.sBoxDistanceDescendingComparator);
                    currentComparator = SortingService.sBoxDistanceDescendingComparator;
                }
                break;
            case 3: //rating
                if (currentComparator == null || currentComparator == SortingService.sBoxRatingAscendingComparator) {
                    Collections.sort(boxes, SortingService.sBoxRatingDescendingComparator);
                    currentComparator = SortingService.sBoxRatingDescendingComparator;
                } else if (currentComparator == SortingService.sBoxRatingDescendingComparator){
                    Collections.sort(boxes, SortingService.sBoxRatingAscendingComparator);
                    currentComparator = SortingService.sBoxRatingAscendingComparator;
                }  else {
                    Collections.sort(boxes, SortingService.sBoxRatingDescendingComparator);
                    currentComparator = SortingService.sBoxRatingDescendingComparator;
                }
                break;
            default:
                throw new RuntimeException("Invalid Toolbar Tab position");
        }
        return currentComparator;
    }
}