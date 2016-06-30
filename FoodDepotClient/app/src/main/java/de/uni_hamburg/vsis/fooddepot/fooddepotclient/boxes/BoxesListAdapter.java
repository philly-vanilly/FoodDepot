package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.AbstractBoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.Box;

/**
 * Created by Phil on 30.06.2016.
 */
// RecyclerView will communicate with this adapter when ViewHolder needs to be created or
// connected with a Note object
public class BoxesListAdapter extends RecyclerView.Adapter<BoxesHolder>{
    private List<Box> mBoxes;
    private BoxesAsListFragment mBoxesAsListFragment;

    public BoxesListAdapter(BoxesAsListFragment boxesAsListFragment){
        mBoxes = AbstractBoxFactory.get(boxesAsListFragment.getContext()).getBoxes();
        mBoxesAsListFragment = boxesAsListFragment;
    }

    @Override
    public BoxesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mBoxesAsListFragment.getActivity());
        View view = layoutInflater.inflate(R.layout.fragment_list_row, parent, false);

        BoxesHolder boxesHolder = new BoxesHolder(mBoxesAsListFragment, view, this);
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
