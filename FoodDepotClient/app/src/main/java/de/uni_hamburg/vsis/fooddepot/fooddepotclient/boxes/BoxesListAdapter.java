package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;

/**
 * Created by Phil on 30.06.2016.
 */
// RecyclerView will communicate with this adapter when ViewHolder needs to be created or
// connected with a Note object
public class BoxesListAdapter extends RecyclerView.Adapter<BoxesHolder>{
    private List<Box> mBoxes;
    private BoxesAsListFragment mBoxesAsListFragment;

    public BoxesListAdapter(BoxesAsListFragment boxesAsListFragment){
        mBoxesAsListFragment = boxesAsListFragment;
        mBoxes = BoxFactory.getFactory().getBoxes(); //mBoxesAsListFragment.getContext()
    }

    public void updateBoxesInList(){
        mBoxes = BoxFactory.getFactory().getBoxes(); //mBoxesAsListFragment.getContext()
        notifyDataSetChanged();
    }

    @Override
    public BoxesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mBoxesAsListFragment.getContext());
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

    public void collapseNonClickedRows(Box box) {
        String clickedBoxId = box.getId();
        for (Box boxIter : mBoxes){
            String boxIterId = boxIter.getId();
            if(!Objects.equals(clickedBoxId, boxIterId) && boxIter.isClicked()){
                boxIter.setClicked(false);
                Integer boxIterPos = BoxFactory.getFactory().getBoxDao().getPosition(boxIterId); //mBoxesAsListFragment.getContext()
                if (boxIterPos != -1) {
                    notifyItemChanged(boxIterPos);
                }
            }
        }
    }
}
