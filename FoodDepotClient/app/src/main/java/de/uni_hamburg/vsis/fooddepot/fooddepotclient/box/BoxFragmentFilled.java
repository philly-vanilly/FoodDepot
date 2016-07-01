package de.uni_hamburg.vsis.fooddepot.fooddepotclient.box;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao.BoxDao;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects.Box;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.factories.BoxFactory;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.AnimationService;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.helpers.DisplayService;

/**
 * Created by Phil on 22.06.2016.
 */
public class BoxFragmentFilled extends Fragment implements BoxFragmentInterface {

    private static String TAG = "BoxFragmentFilled";

    private Box mBox;
    private BoxFactory mBoxFactory;
    private BoxDao mBoxDao;

    private ImageButton mBoxPhotoThumb;
    private ImageView mBoxPhotoFull;
    private View mScrollableFrameLayout;

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
        mBoxFactory = BoxFactory.getFactory();
        mBox = mBoxFactory.getBox(boxID);
        mBoxDao = mBoxFactory.getBoxDao();
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState){
        final View itemView = inflater.inflate(R.layout.fragment_box, parent, false);

        //set title of toolbar/actionbar of parent activity:
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mBox.getName());

        mBoxPhotoThumb = (ImageButton) itemView.findViewById(R.id.boxPhotoThumb);
        mBoxPhotoThumb.setImageDrawable(getContext().getDrawable(R.drawable.fruitpaperbox)); //TODO: get thumbnail from server instead
        mBoxPhotoFull = (ImageView) itemView.findViewById(R.id.boxPhotoFull);
        mScrollableFrameLayout = (View) itemView.findViewById(R.id.scrollableFrameLayout);


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
        mRatingBar.setRating(mBoxDao.getRoundedOverallRatingForBox(mBox));
        mTextViewOwnerName.setText(mBox.getOwnerName());
        mTextViewRatingCount.setText("(" + mBox.getOverallUserRating() + ")");

        String addressString = mBox.getAddress();
        if(addressString == null){
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mBox.getLatitude(), mBox.getLongitude(), 1);

                Log.d(TAG, "Following Address(es) identified:");
                for(Address address : addresses){
                    Log.d(TAG, address.getAddressLine(0) + "\n" + address.getPostalCode() + " " + address.getLocality() + "\n" + address.getCountryName());
                }
                addressString = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getPostalCode() + " " + addresses.get(0).getLocality() + "\n" + addresses.get(0).getCountryName();

            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        mTextViewLocation.setText(addressString);

        mTextViewDistance.setText(mBoxDao.getTriangularDistanceForBox(mBox, 53.551086, 9.993682) + " from here"); //TODO: replace dummy data with current location
        mTextViewPrice.setText(mBoxDao.getRoundedPriceForBox(mBox));

        String targetDateString = "Jul 16 00:00:00 2016"; //TODO: get target date as string from JSON > BEAN instead
        long remainingTime = mBoxDao.makeReservation(mBox, targetDateString);
        DisplayService.displayRemainingTime(remainingTime, mTextViewTimeLeft);

        mBoxPhotoThumb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AnimationService.zoomImageFromThumb(mBoxPhotoThumb, mBoxPhotoFull, mScrollableFrameLayout, R.drawable.fruitpaperbox, itemView);
            }
        });

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
