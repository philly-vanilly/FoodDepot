package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.DepotBeacon;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.BaseResponseHandler;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Response;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.network.RestClient;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;

/**
 * Created by Phil on 05.07.2016.
 */
public class BoxDaoOnline extends BoxDao {
    private static final String TAG = "BoxDaoOnline";
    private Integer numberOfAddedBoxes;

    public BoxDaoOnline(BoxesActivity context, List<Box> boxes) {
        super(context, boxes);
    }

    @Override
    public Integer getNumberOfBoxesMatchingString(final String searchString, int fetchedBoxes, int numberOfBoxes, String authToken, final double latitude, final double longitude) {
        numberOfAddedBoxes = 0;
            RestClient.search(searchString, latitude, longitude, new BaseResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (responseBody != null) {
                        String responseAsString = new String(responseBody);

                        Type collectionType = new TypeToken<Response<List<Box>>>() {}.getType();
                        Response boxResponse = gson.fromJson(responseAsString, collectionType);

                        Log.d(TAG, gson.toJson(boxResponse));

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

                            if (boxToAdd.getName().equals("Abaton")) {
                                DepotBeacon beacon = new DepotBeacon();
                                beacon.setMajor(23774);
                                beacon.setMinor(21333);
                                beacon.setUUID("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                                boxToAdd.setDepotBeacon(beacon);
                            }
                            if (boxToAdd.getName().equals("Grindel")) {
                                DepotBeacon beacon = new DepotBeacon();
                                beacon.setMajor(32018);
                                beacon.setMinor(41230);
                                beacon.setUUID("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                                boxToAdd.setDepotBeacon(beacon);
                            }

                            boxesToAdd.add(boxToAdd);
                        }
                        addBoxes(boxesToAdd);
                        numberOfAddedBoxes = boxesToAdd.size();
                    }
                }
            });
        return numberOfAddedBoxes;
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
