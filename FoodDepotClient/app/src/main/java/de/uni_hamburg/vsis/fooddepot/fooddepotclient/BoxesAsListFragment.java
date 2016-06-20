package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.graphics.Color;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;
import rest.beans.Box;

public class BoxesAsListFragment extends Fragment implements BoxesFragmentInterface {
    //rotation etc support from honeycomb upwards
    private static final boolean BELOW_HONEYCOMB = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    private static final float POINTING_UPWARDS = 0.0f;
    private static final float POINTING_DOWNWARDS = 180f;

    private static final String TAG = "BoxesAsListFragment";
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

        BoxFactory boxFactory = BoxFactory.get(getActivity());
        List<Box> boxes = boxFactory.getBoxes();

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
    private class BoxesHolder extends RecyclerView.ViewHolder{
        //logic member variables:
        private View mItemView;

        //UI variables:
        //basic elements:
        private TextView mTextViewBoxesName;
        private TextView mTextViewBoxesContent;
        private TextView mTextViewDistance;
        private TextView mTextViewPrice;
        private ImageView mImageViewFruit;
        private ImageButton mImageButtonExpand;

        //expandable elements:
        private LinearLayout mExpandableContent;
        private TextView mOwnerName;
//        private ImageButton mImageButtonShoppingCart;
        private TextView mRatingCount;
        private RatingBar mRatingBar;
//        private ProgressBar mProgressBarExpiration;
//        private TextView mTextViewDescription; //to make scrollable: http://stackoverflow.com/questions/1748977/making-textview-scrollable-in-android
//        private TextView mTextViewSellerName;
//        private TextView mTextViewExpiration;

        public BoxesHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            //basic content
            mImageViewFruit = (ImageView) itemView.findViewById(R.id.imageViewFruit);
            mTextViewBoxesName = (TextView) itemView.findViewById(R.id.textViewName);
            mTextViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
            mTextViewDistance = (TextView) itemView.findViewById(R.id.textViewDistance);
            mImageButtonExpand = (ImageButton) itemView.findViewById(R.id.imageButtonExpand);

            //expandable content
            mExpandableContent = (LinearLayout) itemView.findViewById(R.id.expandableContent);
            mTextViewBoxesContent = (TextView) itemView.findViewById(R.id.textViewContent);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            mOwnerName = (TextView) itemView.findViewById((R.id.ownerName));
            mRatingCount = (TextView) itemView.findViewById((R.id.ratingCount));
        }

        public void bindBox(final Box box){
            //basic content:
            BoxService boxService = new BoxService(box, mItemView);
            mImageViewFruit.setImageDrawable(boxService.getImageForBox());
            mTextViewBoxesName.setText(box.getName());
            mTextViewPrice.setText(boxService.getPriceForBox());
            mTextViewDistance.setText(boxService.getDistanceForBox(53.551086, 9.993682)); //TODO: replace dummy data with current location

            //expandable content
            mTextViewBoxesContent.setText("(" + box.getContent() + ")");
            mRatingBar.setRating(boxService.getRatingForBox());
            mOwnerName.setText("Doedel_1995");
            mRatingCount.setText("(10)");


            //listeners and general settings:
            //color rows differently based on whether the position is even or not
            if(box.getPosition() % 2 == 0){
                mItemView.setBackgroundColor(Color.parseColor("#02000000"));
            } else {
                mItemView.setBackgroundColor(Color.parseColor("white"));
            }
            updateDetailsVisibility(box, false);

            mImageButtonExpand.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    box.setClicked(!box.isClicked());
                    updateDetailsVisibility(box, true);
                }
            });
        }
        private void updateDetailsVisibility(Box box, boolean justClicked){ //if not just clicked, don't start animation
            if (box.isClicked()){
                mExpandableContent.setVisibility(View.VISIBLE);
            } else {
                mExpandableContent.setVisibility(View.GONE);
            }
            if (BELOW_HONEYCOMB || !justClicked){ //below honeycomb rotation not supported
                return;
            } else if (box.isClicked()){
                mImageButtonExpand.setRotation(POINTING_DOWNWARDS);
            } else {
                mImageButtonExpand.setRotation(POINTING_UPWARDS);
            }
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
            box.setPosition(position);
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
