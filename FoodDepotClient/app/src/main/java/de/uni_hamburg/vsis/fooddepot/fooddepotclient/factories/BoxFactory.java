package de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxDao;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxDaoOnline;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;

/**
 * Created by Phil on 27.06.2016.
 */
public class BoxFactory {
    private static BoxFactory sBoxFactory;
    private static final String TAG = "BoxFactory";

    private List<Box> mBoxes;
    private BoxesActivity mContext;
    private BoxDao mBoxDao;

    public List<Box> getBoxes() {
        return mBoxes;
    }
    public BoxDao getBoxDao(){
        return mBoxDao;
    }


    //Singleton getter = constructor for a single class in whole app:
    //you have to provide the right context (of BoxesActivity) to access it
    public static BoxFactory getFactory(Context context) {
        if (sBoxFactory == null) {
            sBoxFactory = new BoxFactory(context);
        }
        return sBoxFactory;
    }

    //private Singleton constructor, instantiated by getter instead
    private BoxFactory(Context context) {
        try {
            mContext = (BoxesActivity) context;
        } catch(Exception e){
            Log.e(TAG, "Wrong context!\n" + Log.getStackTraceString(e));
        }
        mBoxes = new ArrayList<>();
        mBoxDao = new BoxDaoOnline(mContext, mBoxes);

        // mBoxDao.getNumberOfBoxesMatchingString(null, 0, 20, "AUTH_TOKEN", 53.4, 9.999);
    }
}
