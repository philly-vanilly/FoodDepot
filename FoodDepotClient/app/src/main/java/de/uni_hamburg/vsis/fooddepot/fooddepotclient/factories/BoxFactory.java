package de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
    private BoxesActivity mBoxesActivity;
    private BoxDao mBoxDao;

    public List<Box> getBoxes() {
        return mBoxes;
    }
    public BoxDao getBoxDao(){
        return mBoxDao;
    }


    //Singleton getter = constructor for a single class in whole app:
    //you have to provide the right context (of BoxesActivity) to access it
    public static BoxFactory getFactory() {
        if (sBoxFactory == null) {
            sBoxFactory = new BoxFactory();
        }
        return sBoxFactory;
    }

    //private Singleton constructor, instantiated by getter instead
    private BoxFactory() {
        mBoxes = new ArrayList<>();
        mBoxDao = new BoxDaoOnline(mBoxes);
        // mBoxDao.getNumberOfBoxesMatchingString(null, 0, 20, "AUTH_TOKEN", 53.4, 9.999);
    }

//    private BoxFactory(Context context) {
//        try {
//            if (context instanceof BoxesActivity) {
//                mBoxesActivity = (BoxesActivity) context;
//            } else if (context instanceof FragmentActivity) {
//                mBoxesActivity = (BoxesActivity) ((FragmentActivity) context).getParent();
//            }
//        } catch(Exception e){
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//
//        mBoxes = new ArrayList<>();
//        mBoxDao = new BoxDaoOnline(mBoxes);
//    }
}
