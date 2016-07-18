package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import java.util.UUID;

/**
 * Created by paul on 05.06.16.
 */
public interface BoxesFragmentInterface {

    void updateBoxList();

    void centerOnSelectedBox(String boxUUID);
}
