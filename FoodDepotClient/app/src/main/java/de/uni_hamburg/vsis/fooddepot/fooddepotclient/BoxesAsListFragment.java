package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import rest.beans.Box;

public class BoxesAsListFragment extends Fragment implements BoxesFragmentInterface {

    private final String TAG = "BoxesAsListFragment";
    private RecyclerView mBoxesListRecyclerView;
    private BoxesListAdapter mBoxesListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // RecyclerView recycles views and positions them on the screen by using a
        // LayoutManager. LayoutManager defines positioning and Scrolling behavior.
        mBoxesListRecyclerView = (RecyclerView) view.findViewById(R.id.boxes_list_recycler_view);
        mBoxesListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //List<Box> boxes = getBoxes();
        List<Box> boxes = new ArrayList<Box>(20); //TODO: replace placeholder

        mBoxesListAdapter = new BoxesListAdapter(boxes);
        mBoxesListRecyclerView.setAdapter(mBoxesListAdapter);

        BoxActivityInterface activity = (BoxActivityInterface)getActivity();
        activity.requestBoxListUpdate();

        return view;
    }

    // class is managed by RecyclerView
    private class BoxesHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewBoxesName;

        public BoxesHolder(View itemView) {
            super(itemView);
            mTextViewBoxesName = (TextView) itemView;
        }
    }

    // RecyclerView will communicate with this adapter when ViewHolder needs to be created or
    // connected with a Note object
    private class BoxesListAdapter extends RecyclerView.Adapter<BoxesHolder>{

        private List<Box> mBoxes;

        public BoxesListAdapter(List<Box> boxes){
            mBoxes = boxes;
        }

        @Override
        public BoxesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            BoxesHolder boxesHolder = new BoxesHolder(view);
            return boxesHolder;
        }

        @Override
        public void onBindViewHolder(BoxesHolder holder, int position) {
            Box box = mBoxes.get(position);
            holder.mTextViewBoxesName.setText(box.getName());
        }
        
        @Override
        public int getItemCount() {
            return mBoxes.size();
        }
    }

    @Override
    public void updateBoxList(List<Box> boxList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Box box : boxList) {
        }
    }
}
