package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

import android.content.Context;

import java.util.List;
import java.util.UUID;

/**
 * Created by Phil on 27.06.2016.
 */
public interface BoxFactoryInterface {
    List<Box> getBoxes();

    Context getApplicationContext();

    Box getBox(UUID id);

    void sortByTabSelection(int position);
}
