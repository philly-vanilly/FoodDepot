package de.uni_hamburg.vsis.fooddepot.fooddepotclient.box;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.BoxFactory;

/**
 * Created by Phil on 22.06.2016.
 */
public class BoxFragmentFilled extends Fragment implements BoxFragmentInterface {
    Box mBox;

    TextView mTitleTextView;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID boxID = (UUID) getActivity().getIntent().getSerializableExtra(BoxActivity.EXTRA_BOX_ACTIVITY_ID);
        BoxFactory boxFactory = BoxFactory.get(getActivity());
        mBox = boxFactory.getBox(boxID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_box, parent, false);

        mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
        mTitleTextView.setText(mBox.getName());

        return view;
    }
}
