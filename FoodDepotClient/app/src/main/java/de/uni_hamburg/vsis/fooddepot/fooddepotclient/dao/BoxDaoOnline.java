package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.BaseResponseHandler;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.Response;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.RestClient;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;

/**
 * Created by Phil on 05.07.2016.
 */
public class BoxDaoOnline extends BoxDao {
    private static final String TAG = "BoxDaoOnline";

    public BoxDaoOnline(BoxesActivity context, List<Box> boxes) {
        super(context, boxes);
    }

    @Override
    public void getNumberOfBoxesMatchingString(String searchString, int fetchedBoxes, int numberOfBoxes, String authToken, double latitude, double longitude) {
        RestClient.search(searchString, latitude, longitude, new BaseResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            if (responseBody != null) {
                String responseAsString = new String(responseBody);
                Log.d(TAG, "search box success:" + responseAsString);

                Type collectionType = new TypeToken<Response<List<Box>>>() {}.getType();
                Response<List<Box>> boxResponse = gson.fromJson(responseAsString, collectionType);

                addBoxes(boxResponse.data);
            }
            }
        });
    }

    @Override
    public List<Box> getNumberOfEmptyBoxes(String searchString, int fetchedBoxes, int numberOfBoxes, UUID queryId, double lat1, double lon1) {
        return null;
    }

    @Override
    public Drawable getPhotoForBox(UUID boxId) {
        return null;
    }

    @Override
    public Box getBoxById(UUID boxId) {
        return null;
    }
    //TODO: implement, simply moved from BoxesActivity
//    private void updateBoxList(){
//        if(mLastLocation != null) {
//            updateBoxList(mLastLocation.getLatitude(), mLastLocation.getLongitude(), mCurrentSearchString);
//        }
//    }

//    private void updateBoxList(double latitude, double longitude, String keys) {
//        RestClient.search(keys, latitude, longitude, new BaseResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//            if (responseBody != null) {
//                String responseAsString = new String(responseBody);
//                Log.d(TAG, "search box success:" + responseAsString);
//
//                Type collectionType = new TypeToken<Response<List<Box>>>() {}.getType();
//                Response<List<Box>> boxResponse = gson.fromJson(responseAsString, collectionType);
//
//                updateBoxFragment(boxResponse.data);
//
//            } else {
//                Log.e(TAG, "search box success but response body null");
//            }
//            }
//        });
//    }
}
