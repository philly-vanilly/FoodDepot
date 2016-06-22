package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.Box;

public class BoxesAsListFragment extends Fragment implements BoxesFragmentInterface {
    private static final String TAG = "BoxesAsListFragment";
    private RecyclerView mBoxesListRecyclerView;
    private BoxesListAdapter mBoxesListAdapter;
    private BoxFactory mBoxFactory;

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
        mBoxesListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBoxFactory = BoxFactory.get(getActivity());
        //we do not create a new list (no new keyword). mBoxes is the same boxes list as in BoxFactory, memory-wise
        List<Box> boxes = mBoxFactory.getBoxes();

        mBoxesListAdapter = new BoxesListAdapter(boxes);
        mBoxesListRecyclerView.setAdapter(mBoxesListAdapter);

        return view;
    }

    // RecyclerView will communicate with this adapter when ViewHolder needs to be created or
    // connected with a Note object
    private class BoxesListAdapter extends RecyclerView.Adapter<BoxesHolder>{
        private List<Box> mBoxes;

        public BoxesListAdapter(List<Box> boxes){
            mBoxes = boxes;
            // mBoxFactory.sortByTabSelection(0);
        }

        @Override
        public BoxesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.fragment_list_row, parent, false);
            BoxesHolder boxesHolder = new BoxesHolder(BoxesAsListFragment.this, view, mBoxesListAdapter);
            return boxesHolder;
        }

        @Override
        public void onBindViewHolder(BoxesHolder holder, int boxPosition) {
            Box box = mBoxes.get(boxPosition);
            holder.bindBox(box);
        }
        
        @Override
        public int getItemCount() {
            return mBoxes.size();
        }
    }

    @Override
    public void updateBoxList(List<Box> boxList) {
        Log.d(TAG, "updateBoxList called");
    }
}
