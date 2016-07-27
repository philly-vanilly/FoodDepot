package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes.BoxesActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.DepotBeacon;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Price;

/**
 * Created by Phil on 01.07.2016.
 */
public class BoxDaoMock extends BoxDao {

    public BoxDaoMock(BoxesActivity context, List<Box> boxes) {
        super(context, boxes);
    }

    @Override
    public Integer getNumberOfBoxesMatchingString(String searchString, int fetchedBoxes, int numberOfBoxes, String authToken, double lat1, double lon1) {
        List<Box> result = new ArrayList<>();

        List<String> food = new ArrayList<>();
        food.add("Apples"); food.add("Oranges"); food.add("Peas"); food.add("Kiwi"); food.add("Watermelons");
        food.add("Melons"); food.add("Apricot"); food.add("Strawberries"); food.add("Pear"); food.add("Peach");
        List<String> prefixes = new ArrayList<>();
        prefixes.add("Mark's_"); prefixes.add("Tasty"); prefixes.add("Star"); prefixes.add("Frank's_"); prefixes.add("Homegrown");
        Random random = new Random();
        for(int i = 0; i < numberOfBoxes; i++){
            Box box = new Box();
            box.setId(UUID.randomUUID().toString());
            box.setLatitude(ThreadLocalRandom.current().nextDouble(53.2, 53.6));
            box.setLongitude(ThreadLocalRandom.current().nextDouble(9.99, 10.0));
            box.setContent(food.get(random.nextInt(food.size()-1)));
            box.setName(prefixes.get(random.nextInt(prefixes.size()-1)) + box.getContent() + "_" + random.nextInt(15));
            box.setOwnerName("doedel_95");
            box.setOverallUserRating(random.nextDouble()*5);
            box.setUserRatingCount(random.nextInt(85));
            Price newPrice = new Price();
            newPrice.setCent(50);
            newPrice.setEuro(3);
            newPrice.setCurrency("EURO");
            box.setPrice(newPrice);
            box.setImage(null);
            box.setClicked(false);
            box.setAddress(null);
            if (i == 0) {
                DepotBeacon beacon = new DepotBeacon();
                beacon.setMajor(23774);
                beacon.setMinor(21333);
                beacon.setUUID("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                box.setDepotBeacon(beacon);
            }
            if (i == 1) {
                DepotBeacon beacon = new DepotBeacon();
                beacon.setMajor(32018);
                beacon.setMinor(41230);
                beacon.setUUID("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                box.setDepotBeacon(beacon);
            }
            result.add(box);
        }

        if(searchString != null && searchString != "") {
            Iterator<Box> iterator = result.iterator();
            String searchLower = searchString.toLowerCase();
            while (iterator.hasNext()) {
                Box box = iterator.next();
                if (!box.getName().toLowerCase().contains(searchLower) && !box.getContent().toLowerCase().contains(searchLower)) {
                    iterator.remove();
                }
            }
        }

        addBoxes(result);
        updateDistanceForAllBoxes(mContext.getLastLocation());

        mContext.updateBoxesInFragments();

        Toast toast = Toast.makeText(mContext, "Scroll down to load more", Toast.LENGTH_LONG);
        toast.show();
        return null;
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
}
