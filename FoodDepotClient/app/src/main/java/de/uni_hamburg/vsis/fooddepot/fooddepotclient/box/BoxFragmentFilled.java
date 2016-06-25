package de.uni_hamburg.vsis.fooddepot.fooddepotclient.box;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private Button mButtonDirections;
    private Button mButtonReserve;



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
        mButtonDirections = (Button) itemView.findViewById((R.id.buttonDirections));
        mButtonReserve = (Button) itemView.findViewById((R.id.buttonReserve));

        mTextViewBoxesContent.setText(mBox.getContent());
        mRatingBar.setRating(boxService.getRatingForBox());
        mTextViewOwnerName.setText("Doedel_1995");
        mTextViewRatingCount.setText("(10)");
        mTextViewLocation.setText("Von-Sauer-Strasse 89a"); //TODO: use google maps API to determine address
        mTextViewDistance.setText(boxService.makeDistanceForBox(53.551086, 9.993682) + " from here"); //TODO: replace dummy data with current location
        mTextViewPrice.setText(boxService.getPriceForBox());

        String targetDateString = "Jul 16 00:00:00 2016"; //TODO: get target date as string from JSON > BEAN instead
        BoxService.setRemainingTime(targetDateString, mTextViewTimeLeft);

        mButtonReserve.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { //TODO: see 1. and 2.
                // 1. send reservation to server, check  if you can reserve a depot:
                // you are registered, have enough money, no other depot reserved by you,
                // this depot not reserved by someone else:

                // 2. get response back:
                if(true){
                    // 3. set Button text:
                    Calendar date = Calendar.getInstance();
                    long currentTime= date.getTimeInMillis();
                    Date currentTimePlusThirtyMinutes=new Date(currentTime + (600000));
                    mButtonReserve.setText("Yours till " + new SimpleDateFormat("HH:mm").format(currentTimePlusThirtyMinutes));

                    Context context = getContext();
                    CharSequence text = "To cancel reservation click button again.";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        mButtonDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double startLatitude = 53.551086; //TODO: get from Account Singleton (force update there)
                Double startLongitude = 9.993682;
                Double directionLatitude = mBox.getLatitude();
                Double directionLongitude = mBox.getLongitude();

                /**
                 * NOTE: To add Google API key:
                 * 1. Go to Java JRE bin, shift+rightclick, start CMD from here,
                 * 2. keytool -list -v -keystore "C:\Users\Phil\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
                 * 3. copy SHA key
                 * 4. log in into gmail account and go to https://console.developers.google.com/apis/credentials
                 * 5. Create Android credentials
                 * 6. paste SHA1 key and also package name (de.uni_hamburg.vsis.fooddepot.fooddepotclient) from manifest.xml, PRESS ENTER INSIDE TEXTFIELD!!!!
                 * 7. In manifest.xml paste this at the end of (but inside) <application>: <meta-data android:name="com.google.android.geo.API_KEY" android:value="YOUR_API_KEY"/>
                 */

                StringBuilder mapsUri = new StringBuilder("http://maps.google.com/maps?saddr=");
                mapsUri.append(startLatitude).append(", ").append(startLongitude).append("&daddr=").append(directionLatitude).append(", ").append(directionLongitude);

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUri.toString()));
                mapIntent.setPackage("com.google.android.apps.maps");

                // open google maps directly, no map app chooser dialogue:
                // mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

                startActivity(mapIntent);
            }
        });


        return itemView;
    }
}
