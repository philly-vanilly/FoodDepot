package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import rest.beans.Box;

/**
 * Created by Phil on 18.06.2016.
 */
public class BoxFactory {
    private static BoxFactory sBoxFactory;

    private List<Box> mBoxes;

    private BoxFactory(Context context) {
        //TODO: when creating boxes, read data from REST instead of making up own data
        mBoxes = new ArrayList<>();
        List<String> food = new ArrayList<>();
        food.add("Apples"); food.add("Oranges"); food.add("Peas"); food.add("Kiwi"); food.add("Watermelons");
        food.add("Melons"); food.add("Apricot"); food.add("Strawberries"); food.add("Pear"); food.add("Peach");
        List<String> prefixes = new ArrayList<>();
        prefixes.add("Mark's_"); prefixes.add("Tasty"); prefixes.add("Star"); prefixes.add("Frank's_"); prefixes.add("Homegrown");
        Random random = new Random();
        for(int i = 0; i < 50; i++){
            Box box = new Box();
            box.setId(UUID.randomUUID().toString());
            box.setLatitude(ThreadLocalRandom.current().nextDouble(50.0, 60.0));
            box.setLongitude(ThreadLocalRandom.current().nextDouble(9.0, 10.0));
            box.setContent(food.get(random.nextInt(food.size()-1)));
            box.setName(prefixes.get(random.nextInt(prefixes.size()-1)) + box.getContent() + "_" + random.nextInt(15));
            box.setRating(random.nextDouble()*3);
            box.setPrice(random.nextDouble()*100);
            box.setImage(null);
            box.setClicked(false);
            mBoxes.add(box);
        }
    }

    public static BoxFactory get(Context context) {
        if (sBoxFactory == null) {
            sBoxFactory = new BoxFactory(context);
        }
        return sBoxFactory;
    }

    public List<Box> getBoxes() {
        return mBoxes;
    }

    public Box getBox(String id) {
        for (Box Box : mBoxes) {
            if (Box.getId().equals(id)) {
                return Box;
            }
        }
        return null;
    }
}
