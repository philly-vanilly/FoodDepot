package de.uni_hamburg.vsis.fooddepot.fooddepotclient.box;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.BoxService;

/**
 * Created by Phil on 22.06.2016.
 */
public class BoxFragmentFilled extends Fragment implements BoxFragmentInterface {
    Box mBox;

    private ImageView mBoxPhoto;
    private TextView mTextViewBoxesContent;
    private TextView mTextViewOwnerName;
    private TextView mTextViewRatingCount;
    private RatingBar mRatingBar;
    private TextView mTextViewLocation;
    private TextView mTextViewDistance;
    private TextView mTextViewPrice;
    private TextView mTextViewTimeLeft;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID boxID = (UUID) getActivity().getIntent().getSerializableExtra(BoxActivity.EXTRA_BOX_ACTIVITY_ID);
        BoxFactory boxFactory = BoxFactory.get(getActivity());
        mBox = boxFactory.getBox(boxID);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState){
        View itemView = inflater.inflate(R.layout.fragment_box, parent, false);
        BoxService boxService = new BoxService(mBox, itemView);

        //set title of toolbar/actionbar of parent activity:
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mBox.getName());

        mBoxPhoto = (ImageView) itemView.findViewById(R.id.boxPhoto);
        mBoxPhoto.setImageDrawable(getContext().getDrawable(R.drawable.fruitpaperbox));

        mTextViewBoxesContent = (TextView) itemView.findViewById(R.id.textViewContent);
        mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        mTextViewOwnerName = (TextView) itemView.findViewById((R.id.textViewOwnerName));
        mTextViewRatingCount = (TextView) itemView.findViewById((R.id.ratingCount));
        mTextViewLocation = (TextView) itemView.findViewById(R.id.textViewLocation);
        mTextViewDistance = (TextView) itemView.findViewById(R.id.textViewDistance);
        mTextViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
        mTextViewTimeLeft = (TextView) itemView.findViewById(R.id.textViewTimeLeft);


        mTextViewBoxesContent.setText(mBox.getContent());
        mRatingBar.setRating(boxService.getRatingForBox());
        mTextViewOwnerName.setText("Doedel_1995");
        mTextViewRatingCount.setText("(10)");
        mTextViewLocation.setText("Von-Sauer-Strasse 89a"); //TODO: use google maps API to determine address
        mTextViewDistance.setText(boxService.makeDistanceForBox(53.551086, 9.993682) + " from here"); //TODO: replace dummy data with current location
        mTextViewPrice.setText(boxService.getPriceForBox());

        String targetDateString = "Jul 16 00:00:00 2016"; //TODO: get target date as string from JSON > BEAN instead
        BoxService.setRemainingTime(targetDateString, mTextViewTimeLeft);

        return itemView;
    }
}
