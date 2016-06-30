package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.content.Context;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.SortingService;

/**
 * Created by Phil on 27.06.2016.
 */
public class AbstractBoxFactory {
    protected static AbstractBoxFactory sAbstractBoxFactory;
    protected static final String TAG = "AbstractBoxFactory";
    protected Context mApplicationContext;
    protected Comparator mCurrentComparator = null;
    protected List<Box> mBoxes;

    public List<Box> getBoxes() {
        return mBoxes;
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public Box getBox(UUID id) {
        for (Box Box : mBoxes) {
            if (Box.getId().equals(id)) { //equals returns true for String value, == returns true only for same object reference
                return Box;
            }
        }
        return null;
    }

    public void sortByTabSelection(int position) {
        mCurrentComparator = SortingService.sortByTabSelection(position, mCurrentComparator, mBoxes);
    }

    //Singleton getter = constructor for a single class in whole app:
    public static AbstractBoxFactory get(Context context) {
        if (sAbstractBoxFactory == null) {
            sAbstractBoxFactory = new BoxFactoryMock(context);
        }
        return sAbstractBoxFactory;
    }
}
