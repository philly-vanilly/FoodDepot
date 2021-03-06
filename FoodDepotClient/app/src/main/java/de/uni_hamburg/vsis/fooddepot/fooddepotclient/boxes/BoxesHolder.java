package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.box.BoxActivity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxDao;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Box;
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
    private BoxFactory mBoxFactory;
    private BoxDao mBoxDao;

    //logic member variables:
    private View mItemView;
    private BoxesAsListFragment mBoxesAsListFragment;

    //basic elements:
    private LinearLayout mWholeRow;
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
        mItemView = itemView;
        mBoxFactory = BoxFactory.getFactory(); //boxesAsListFragment.getContext()
        mBoxDao = mBoxFactory.getBoxDao();

        //basic content
        mWholeRow = (LinearLayout) itemView.findViewById(R.id.wholeRow);
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
        mTextViewPrice.setText(mBoxDao.getRoundedPriceForBox(box));
        mTextViewDistance.setText(mBoxDao.getFormattedDistanceForBox(box));

        //expandable content
        mTextViewBoxesContent.setText("(" + box.getContent() + ")");
        mRatingBar.setRating(mBoxDao.getRoundedOverallRatingForBox(box));
        mTextViewOwnerName.setText("Doedel_1995");
        mTextViewRatingCount.setText("(10)");

        String targetDateString = "Jul 16 00:00:00 2016"; //TODO: get target date as string from JSON > BEAN instead
        long remainingTime = mBoxDao.makeReservation(box, targetDateString);
        DisplayService.displayRemainingTime(remainingTime, mTextViewTimeLeft);

        //listeners and general settings:
        //color rows differently based on whether the position is even or not
        if(mBoxFactory.getBoxes().indexOf(box) % 2 == 0){
            mItemView.setBackgroundColor(Color.parseColor("#02000000"));
            //mItemView.setBackgroundColor(ContextCompat.getColor(mBoxesAsListFragment.getActivity(), R.color.listRowOne));
        } else {
            mItemView.setBackgroundColor(Color.parseColor("white"));
            //mItemView.setBackgroundColor(ContextCompat.getColor(mBoxesAsListFragment.getActivity(), R.color.listRowTwo));
        }
        updateDetailsVisibility(box);

        mWholeRow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                box.setClicked(!box.isClicked());
                updateDetailsVisibility(box);

                if (box.isClicked()) {//dont zoom in on closing a card
                    BoxesActivity boxesActivity = (BoxesActivity) mBoxesAsListFragment.getActivity();
                    boxesActivity.onBoxSelected(box.getId(), mBoxesAsListFragment.TAG);
                }
            }
        });

        mDetailsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = BoxActivity.makeIntent(mBoxesAsListFragment.getContext(), box.getId());
                mBoxesAsListFragment.startActivity(intent);
            }
        });
    }

    private void updateDetailsVisibility(Box box){
        if (box.isClicked()){
            //1. expanding clicked
            mExpandableContent.setVisibility(View.VISIBLE);
            //2. collapsing all others:
            //noinspection ConstantConditions
            mBoxesAsListFragment.getBoxesListAdapter().collapseNonClickedRows(box);
        } else {
            mExpandableContent.setVisibility(View.GONE);
        }
        if (!BELOW_HONEYCOMB){
            if (box.isClicked()){
                mImageButtonExpand.setRotation(POINTING_DOWNWARDS);
            } else {
                mImageButtonExpand.setRotation(POINTING_UPWARDS);
            }
        }
    }
}
