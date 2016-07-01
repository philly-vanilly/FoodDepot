package de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories;

import android.content.Context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxDao;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxDaoMock;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.SortingSelector;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.SortingService;

/**
 * Created by Phil on 27.06.2016.
 */
public class BoxFactory {
    private static BoxFactory sBoxFactory;
    private static final String TAG = "BoxFactory";
    private Comparator mCurrentComparator = null;
    private List<Box> mBoxes;
    private BoxDao mBoxDao;

    public List<Box> getBoxes() {
        return mBoxes;
    }

    public Box getBox(UUID id) {
        for (Box Box : mBoxes) {
            if (Box.getId().equals(id)) { //equals returns true for String value, == returns true only for same object reference
                return Box;
            }
        }
        return null;
    }

    public BoxDao getBoxDao(){
        return mBoxDao;
    }

    public void sortBySelection(SortingSelector selector) {
        mCurrentComparator = SortingService.sortBySelection(selector, mCurrentComparator, mBoxes);
    }

    //Singleton getter = constructor for a single class in whole app:
    public static BoxFactory getFactory() {
        if (sBoxFactory == null) {
            sBoxFactory = new BoxFactory();
        }
        return sBoxFactory;
    }

    //private Singleton constructor, instantiated by getter instead
    private BoxFactory() {
        mBoxDao = new BoxDaoMock();
        mBoxes = new ArrayList<>();

        List<Box> pulledBoxes = mBoxDao.getNumberOfBoxesMatchingString(null, 0, 20, UUID.randomUUID(), 53.4, 9.999);
        for (Box pulledBox : pulledBoxes){
            mBoxes.add(pulledBox);
        }

        //sort by distance:
        SortingService.sortBySelection(SortingSelector.DISTANCE, mCurrentComparator, mBoxes);
    }
}
