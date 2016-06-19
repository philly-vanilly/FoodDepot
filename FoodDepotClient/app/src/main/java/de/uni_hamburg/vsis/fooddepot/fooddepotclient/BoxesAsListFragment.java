package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


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

        BoxSingleton boxSingleton = BoxSingleton.get(getActivity());
        List<Box> boxes = boxSingleton.getBoxes();

        mBoxesListAdapter = new BoxesListAdapter(boxes);
        mBoxesListRecyclerView.setAdapter(mBoxesListAdapter);

//        BoxActivityInterface activity = (BoxActivityInterface)getActivity();
//        activity.requestBoxListUpdate();

        return view;
    }

    /**
     * Holder for one single list entry in a box list. Sets attributes for a specific box. Is
     * managed by RecyclerView.
     */
    private class BoxesHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewBoxesName;
        //private TextView mTextViewBoxesContent;
        private TextView mTextViewDistance;
        private TextView mTextViewPrice;
//        private TextView mTextViewDescription; //to make scrollable: http://stackoverflow.com/questions/1748977/making-textview-scrollable-in-android
//        private TextView mTextViewSellerName;
//        private TextView mTextViewExpiration;
        private ImageView mImageViewFruit;
        private ImageButton mImageButtonExpand;
//        private ImageButton mImageButtonShoppingCart;
        private RatingBar mRatingBar;
//        private ProgressBar mProgressBarExpiration;

        private View mItemView;

        public BoxesHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            mImageViewFruit = (ImageView) itemView.findViewById(R.id.imageViewFruit);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            mTextViewBoxesName = (TextView) itemView.findViewById(R.id.textViewName);
            mTextViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
            mTextViewDistance = (TextView) itemView.findViewById(R.id.textViewDistance);
            mImageButtonExpand = (ImageButton) itemView.findViewById(R.id.imageButtonExpand);
        }

        public void bindBox(Box box){
            BoxService boxService = new BoxService(box, mItemView);
            mImageViewFruit.setImageDrawable(boxService.getImageForBox());
            mRatingBar.setRating(boxService.getRatingForBox());
            mTextViewBoxesName.setText(box.getName());
            mTextViewPrice.setText(boxService.getPriceForBox());
            mTextViewDistance.setText(boxService.getDistanceForBox(53.551086, 9.993682)); //TODO: replace dummy data with current location
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
            View view = layoutInflater.inflate(R.layout.boxes_list_row, parent, false);
            BoxesHolder boxesHolder = new BoxesHolder(view);
            return boxesHolder;
        }

        @Override
        public void onBindViewHolder(BoxesHolder holder, int position) {
            Box box = mBoxes.get(position);
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
