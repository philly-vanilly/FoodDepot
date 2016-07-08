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

/**
 * Created by Phil on 01.07.2016.
 */
public class BoxDaoMock extends BoxDao {

    public BoxDaoMock(BoxesActivity context, List<Box> boxes) {
        super(context, boxes);
    }

    @Override
    public void getNumberOfBoxesMatchingString(String searchString, int fetchedBoxes, int numberOfBoxes, String authToken, double lat1, double lon1) {
        List<Box> result = new ArrayList<>();

        List<String> food = new ArrayList<>();
        food.add("Apples"); food.add("Oranges"); food.add("Peas"); food.add("Kiwi"); food.add("Watermelons");
        food.add("Melons"); food.add("Apricot"); food.add("Strawberries"); food.add("Pear"); food.add("Peach");
        List<String> prefixes = new ArrayList<>();
        prefixes.add("Mark's_"); prefixes.add("Tasty"); prefixes.add("Star"); prefixes.add("Frank's_"); prefixes.add("Homegrown");
        Random random = new Random();
        for(int i = 0; i < numberOfBoxes; i++){
            Box box = new Box();
            box.setId(UUID.randomUUID());
            box.setLatitude(ThreadLocalRandom.current().nextDouble(53.2, 53.6));
            box.setLongitude(ThreadLocalRandom.current().nextDouble(9.99, 10.0));
            box.setContent(food.get(random.nextInt(food.size()-1)));
            box.setName(prefixes.get(random.nextInt(prefixes.size()-1)) + box.getContent() + "_" + random.nextInt(15));
            box.setOwnerName("doedel_95");
            box.setOverallUserRating(random.nextDouble()*5);
            box.setUserRatingCount(random.nextInt(85));
            box.setPrice(random.nextDouble()*100);
            box.setImage(null);
            box.setClicked(false);
            box.setAddress(null);
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
}
