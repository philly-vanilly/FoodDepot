package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.LinearLayoutManagerWithSmoothScroller;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;

public class BoxesAsListFragment extends Fragment implements BoxesFragmentInterface {
    public static final String TAG = "BoxesAsListFragment";
    private RecyclerView mBoxesListRecyclerView;
    private LinearLayoutManagerWithSmoothScroller mLinearLayoutManager;

    @Nullable
    public BoxesListAdapter getBoxesListAdapter() {
        return mBoxesListAdapter;
    }

    private BoxesListAdapter mBoxesListAdapter;

    //doesn't get Intent/Extra, so no need for:
//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        // code ...
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // RecyclerView recycles views and positions them on the screen by using a
        // LayoutManager. LayoutManager defines positioning and Scrolling behavior.
        mBoxesListRecyclerView = (RecyclerView) view.findViewById(R.id.boxes_list_recycler_view);
        mLinearLayoutManager = new LinearLayoutManagerWithSmoothScroller(getActivity());
        mBoxesListRecyclerView.setLayoutManager(mLinearLayoutManager);

        mBoxesListAdapter = new BoxesListAdapter(this);
        mBoxesListRecyclerView.setAdapter(mBoxesListAdapter);

        return view;
    }

    @Override
    public void updateBoxList() {
        Log.d(TAG, "updateBoxList called");
        mBoxesListAdapter.updateBoxesInList();
    }

    @Override
    public void centerOnSelectedBox(UUID boxUUID) {
        Integer position = BoxFactory.getFactory().getPosition(boxUUID);
        if (position != null ) {
            mBoxesListRecyclerView.smoothScrollToPosition(position);
        }
    }
}
