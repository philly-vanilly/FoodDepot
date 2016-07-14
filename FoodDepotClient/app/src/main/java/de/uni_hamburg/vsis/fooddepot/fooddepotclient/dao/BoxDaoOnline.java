package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.BaseResponseHandler;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Content;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Distance;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Response;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.RestClient;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.ResponseTuple;

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

                    Type collectionType = new TypeToken<List<Box>>() {}.getType();
                    List<Box> boxResponse = gson.fromJson(responseAsString, collectionType);

                    List<Box> boxesToAdd = new ArrayList<>();
                    for (Box boxIter : boxResponse) {
                        Box boxToAdd = new Box();
                        boxToAdd.setId(boxIter.getId());
                        boxToAdd.setName(boxIter.getName());
                        boxToAdd.setName(boxIter.getContent());
                        boxToAdd.setLatitude(boxIter.getLatitude());
                        boxToAdd.setLongitude(boxIter.getLongitude());
                        boxToAdd.setOwnerName(boxIter.getOwnerName());
                        boxToAdd.setOverallUserRating(boxIter.getOverallUserRating());
                        boxToAdd.setPrice(boxIter.getPrice());
                        boxToAdd.setAddress(boxIter.getAddress());
                        boxToAdd.setTemperature(boxIter.getTemperature());
                        boxToAdd.setFillingStatus(boxIter.getFillingStatus());
                        boxToAdd.setSmell(boxIter.getSmell());
                        boxesToAdd.add(boxToAdd);
                    }

                    addBoxes(boxesToAdd);

                    mContext.updateBoxesInFragments();

                    Toast toast = Toast.makeText(mContext, "Scroll down to load more", Toast.LENGTH_LONG);
                    toast.show();

//                    Gson prettyPrintGson = new GsonBuilder().setPrettyPrinting().create();
//                    JsonParser parser = new JsonParser();
//                    JsonElement el = parser.parse(responseAsString);
//                    //String responseAsPrettyString = prettyPrintGson.toJson(el); // done
//                    Log.d(TAG, "Received following Box List data from server:\n" + ));
//                    //Log.d(TAG, "search box success:" + responseAsString);

//                    Type collectionType = new TypeToken<Response<List<Box>>>() {}.getType();
//                    Response<List<Box>> boxResponse = gson.fromJson(responseAsString, collectionType);


                    //method name says toJson but is actually toString:

                    //addBoxes(boxResponse.data);

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
