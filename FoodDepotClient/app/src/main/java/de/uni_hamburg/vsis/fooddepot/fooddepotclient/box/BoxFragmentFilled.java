package de.uni_hamburg.vsis.fooddepot.fooddepotclient.box;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.BoxFactory;

/**
 * Created by Phil on 22.06.2016.
 */
public class BoxFragmentFilled extends Fragment implements BoxFragmentInterface {
    Box mBox;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID boxID = (UUID) getActivity().getIntent().getSerializableExtra(BoxActivity.EXTRA_BOX_ACTIVITY_ID);
        BoxFactory boxFactory = BoxFactory.get(getActivity());
        mBox = boxFactory.getBox(boxID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){

        return null;
    }
}
