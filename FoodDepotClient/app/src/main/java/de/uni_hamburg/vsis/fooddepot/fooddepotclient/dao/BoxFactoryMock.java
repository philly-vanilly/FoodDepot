package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.content.Context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.SortingService;

/**
 * Created by Phil on 18.06.2016.
 */
public class BoxFactoryMock implements BoxFactoryInterface {
    private static BoxFactoryMock sBoxFactoryMock;
    private static final String TAG = "BoxFactoryMock";
    private Context mApplicationContext;
    private Comparator mCurrentComparator;

    private List<Box> mBoxes;

    //private Singleton constructor, instantiated by getter instead
    private BoxFactoryMock(Context context) {
        mApplicationContext = context.getApplicationContext();
        mCurrentComparator = null;

        mBoxes = new ArrayList<>();
        List<String> food = new ArrayList<>();
        food.add("Apples"); food.add("Oranges"); food.add("Peas"); food.add("Kiwi"); food.add("Watermelons");
        food.add("Melons"); food.add("Apricot"); food.add("Strawberries"); food.add("Pear"); food.add("Peach");
        List<String> prefixes = new ArrayList<>();
        prefixes.add("Mark's_"); prefixes.add("Tasty"); prefixes.add("Star"); prefixes.add("Frank's_"); prefixes.add("Homegrown");
        Random random = new Random();
        for(int i = 0; i < 50; i++){
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
            mBoxes.add(box);
        }
    }

    //Singleton getter = constructor for a single class in whole app:
    public static BoxFactoryMock get(Context context) {
        if (sBoxFactoryMock == null) {
            sBoxFactoryMock = new BoxFactoryMock(context);
        }
        return sBoxFactoryMock;
    }

    @Override
    public List<Box> getBoxes() {
        return mBoxes;
    }
    @Override
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    @Override
    public Box getBox(UUID id) {
        for (Box Box : mBoxes) {
            if (Box.getId().equals(id)) { //equals returns true for String value, == returns true only for same object reference
                return Box;
            }
        }
        return null;
    }

    @Override
    public void sortByTabSelection(int position) {
        mCurrentComparator = SortingService.sortByTabSelection(position, mCurrentComparator, mBoxes);
    }
}
