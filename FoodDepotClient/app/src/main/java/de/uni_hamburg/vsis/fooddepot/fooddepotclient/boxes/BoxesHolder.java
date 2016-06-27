package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxFactoryInterface;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxFactoryMock;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.DisplayService;

/**
 * Holder for one single list entry in a box list. Sets attributes for a specific box. Is
 * managed by RecyclerView.
 */
class BoxesHolder extends RecyclerView.ViewHolder{
    private static final boolean BELOW_HONEYCOMB = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;    //rotation etc support from honeycomb upwards
    private static final float POINTING_UPWARDS = 0.0f;
    private static final float POINTING_DOWNWARDS = 180f;

    private static final String TAG = "BoxesHolder";
    private BoxFactoryInterface mBoxFactory;

    //logic member variables:
    private View mItemView;
    private RecyclerView.Adapter mBoxesListAdapter;
    private BoxesAsListFragment mBoxesAsListFragment;


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

    public BoxesHolder(BoxesAsListFragment boxesAsListFragment, View itemView, RecyclerView.Adapter adapter) {
        super(itemView);
        mBoxesAsListFragment = boxesAsListFragment;
        mBoxesListAdapter = adapter;
        mItemView = itemView;
        mBoxFactory = BoxFactoryMock.get(boxesAsListFragment.getContext());

        //toolbar (from Activity's layout, not from the Fragments ListView layout!):
        mTabLayoutSortList = (TabLayout) boxesAsListFragment.getActivity().findViewById(R.id.tabLayoutSortList);

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
        //basic content:
        mImageViewFruit.setImageDrawable(DisplayService.getImageForBox(box, itemView));
        mTextViewBoxesName.setText(box.getName());
        mTextViewPrice.setText(box.getPriceForBox());
        mTextViewDistance.setText(box.makeDistanceForBox(53.551086, 9.993682)); //TODO: replace dummy data with current location

        //expandable content
        mTextViewBoxesContent.setText("(" + box.getContent() + ")");
        mRatingBar.setRating(box.getRoundedOverallRatingForBox());
        mTextViewOwnerName.setText("Doedel_1995");
        mTextViewRatingCount.setText("(10)");

        String targetDateString = "Jul 16 00:00:00 2016"; //TODO: get target date as string from JSON > BEAN instead
        long remainingTime = box.makeReservation(targetDateString);
        DisplayService.displayRemainingTime(remainingTime, mTextViewTimeLeft);

        //listeners and general settings:
        //color rows differently based on whether the position is even or not
        if(mBoxFactory.getBoxes().indexOf(box) % 2 == 0){
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
                Intent intent = BoxActivity.makeIntent(mBoxesAsListFragment.getActivity(), box.getId());
                mBoxesAsListFragment.startActivity(intent);
            }
        });

        mTabLayoutSortList.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBoxFactory.sortByTabSelection(tab.getPosition());
                mBoxesListAdapter.notifyDataSetChanged(); //update whole list
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                return;
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mBoxFactory.sortByTabSelection(tab.getPosition());
                mBoxesListAdapter.notifyDataSetChanged(); //update whole list
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
