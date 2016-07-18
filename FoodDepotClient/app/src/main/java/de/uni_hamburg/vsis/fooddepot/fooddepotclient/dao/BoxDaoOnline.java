package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.graphics.drawable.Drawable;
import android.os.Looper;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.BaseResponseHandler;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Response;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.RestClient;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;

/**
 * Created by Phil on 05.07.2016.
 */
public class BoxDaoOnline extends BoxDao {
    private static final String TAG = "BoxDaoOnline";

    public BoxDaoOnline(BoxesActivity context, List<Box> boxes) {
        super(context, boxes);
    }

    @Override
    public void getNumberOfBoxesMatchingString(final String searchString, int fetchedBoxes, int numberOfBoxes, String authToken, final double latitude, final double longitude) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                RestClient.search(searchString, latitude, longitude, new BaseResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (responseBody != null) {
                            String responseAsString = new String(responseBody);

                            Type collectionType = new TypeToken<Response<List<Box>>>() {}.getType();
                            Response boxResponse = gson.fromJson(responseAsString, collectionType);

                            List<Box> boxesToAdd = new ArrayList<>();
                            for (Box boxIter : (List<Box>) boxResponse.getData()) {
                                Box boxToAdd = new Box();
                                boxToAdd.setId(boxIter.getId());
                                boxToAdd.setName(boxIter.getName());
                                boxToAdd.setContent(boxIter.getContent());
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
                        }
                    }
                });
                Looper.loop();
            }
        };
        final Thread myThread = new Thread(runnable);
        myThread.start();
    }

    @Override
    public List<Box> getNumberOfEmptyBoxes(String searchString, int fetchedBoxes, int numberOfBoxes, String queryId, double lat1, double lon1) {
        return null;
    }

    @Override
    public Drawable getPhotoForBox(String boxId) {
        return null;
    }

    @Override
    public Box getBoxById(String boxId) {
        return null;
    }
    //TODO: implement, simply moved from BoxesActivity
}
