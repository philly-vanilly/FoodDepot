package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rest.beans.Box;

public class BoxesAsListFragment extends Fragment implements BoxesFragmentInterface {
    private static final boolean BELOW_HONEYCOMB = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;    //rotation etc support from honeycomb upwards
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
        //we do not create a new list (no new keyword). mBoxes is the same boxes list as in BoxFactory, memory-wise
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
        private List<Box> mBoxes;

        //UI variables:
        TabLayout mTabLayoutSortList;
        //basic elements:
        private TextView mTextViewBoxesName;
        private TextView mTextViewBoxesContent;
        private TextView mTextViewDistance;
        private TextView mTextViewPrice;
        private ImageView mImageViewFruit;
        private ImageButton mImageButtonExpand;

        //expandable elements:
        private LinearLayout mExpandableContent;
        private TextView mTextViewOwnerName;
        private TextView mTextViewRatingCount;
        private RatingBar mRatingBar;
        private TextView mTextViewTimeLeft;
        private Button mDetailsButton;

        public BoxesHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mBoxes = BoxFactory.get(getContext()).getBoxes();

            //toolbar (from Activity's layout, not from the Fragments ListView layout!):
            mTabLayoutSortList = (TabLayout) getActivity().findViewById(R.id.tabLayoutSortList);

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
            mTextViewOwnerName = (TextView) itemView.findViewById((R.id.textViewOwnerName));
            mTextViewRatingCount = (TextView) itemView.findViewById((R.id.ratingCount));
            mTextViewTimeLeft = (TextView) itemView.findViewById(R.id.textViewTimeLeft);
            mDetailsButton = (Button) itemView.findViewById(R.id.detailsButton);
        }

        public void bindBox(final Box box){
            BoxService boxService = new BoxService(box, mItemView);

            //basic content:
            mImageViewFruit.setImageDrawable(boxService.getImageForBox());
            mTextViewBoxesName.setText(box.getName());
            mTextViewPrice.setText(boxService.getPriceForBox());
            mTextViewDistance.setText(boxService.makeDistanceForBox(53.551086, 9.993682)); //TODO: replace dummy data with current location

            //expandable content
            mTextViewBoxesContent.setText("(" + box.getContent() + ")");
            mRatingBar.setRating(boxService.getRatingForBox());
            mTextViewOwnerName.setText("Doedel_1995");
            mTextViewRatingCount.setText("(10)");

            String targetDateString = "Jul 16 00:00:00 2016"; //TODO: get target date as string from JSON > BEAN instead
            setRemainingTime(targetDateString);

            //listeners and general settings:
            //color rows differently based on whether the position is even or not
            if(mBoxes.indexOf(box) % 2 == 0){
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

            mDetailsButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = BoxActivity.makeIntent(getActivity(), box.getId());
                    startActivity(intent);
                }
            });

            mTabLayoutSortList.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mBoxesListAdapter.sortByTabSelection(tab.getPosition());
                    mBoxesListAdapter.notifyDataSetChanged(); //update whole list
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    return;
                }
                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    mBoxesListAdapter.sortByTabSelection(tab.getPosition());
                    mBoxesListAdapter.notifyDataSetChanged(); //update whole list
                }
            });
        }

        private void setRemainingTime(String targetDateString) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
            Date targetDate = null;
            try {
                targetDate = dateFormat.parse(targetDateString);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            Date nowDate = new Date();
            if (targetDate != null) {
                long msDiff = targetDate.getTime() - nowDate.getTime();
                if (msDiff / 1000 / 60 / 60 / 24 > 1){
                    long finalDiff = TimeUnit.DAYS.convert(msDiff, TimeUnit.MILLISECONDS);
                    mTextViewTimeLeft.setText("Expires in: " + finalDiff + " days");
                } else if (msDiff / 1000 / 60 / 60 > 1) {
                    long finalDiff = TimeUnit.HOURS.convert(msDiff, TimeUnit.MILLISECONDS);
                    mTextViewTimeLeft.setText("Expires in: " + finalDiff + " hours");
                } else if (msDiff / 1000 / 60 > 1) {
                    long finalDiff = TimeUnit.MINUTES.convert(msDiff, TimeUnit.MILLISECONDS);
                    mTextViewTimeLeft.setText("Expires in: " + finalDiff + " minutes");
                    mTextViewTimeLeft.setTextColor(Color.RED);
                } else {
                    long finalDiff = TimeUnit.SECONDS.convert(msDiff, TimeUnit.MILLISECONDS);
                    mTextViewTimeLeft.setText("Expires in: " + finalDiff + " seconds");
                    mTextViewTimeLeft.setTextColor(Color.RED);
                }
            } else {
                mTextViewTimeLeft.setText("Expires in: [NO DATA]");
                mTextViewTimeLeft.setTextColor(Color.RED);
            }
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
        private Comparator currentComparator;

        public BoxesListAdapter(List<Box> boxes){
            mBoxes = boxes;
            currentComparator = null;
            sortByTabSelection(0);
        }

        @Override
        public BoxesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.fragment_list_row, parent, false);
            BoxesHolder boxesHolder = new BoxesHolder(view);
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

        public void sortByTabSelection(int tabPosition) {
            currentComparator = BoxService.sortByTabSelection(tabPosition, currentComparator, mBoxes);
        }

    }

    @Override
    public void updateBoxList(List<Box> boxList) {
        Log.d(TAG, "updateBoxList called");
    }
}
