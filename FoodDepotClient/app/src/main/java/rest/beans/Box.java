package rest.beans;

import android.graphics.drawable.Drawable;

import com.google.android.gms.plus.model.people.Person;

import java.util.UUID;

/**
 * Created by paul on 05.06.16.
 */
public class Box {
    //DAO attributes for serialization and REST-communication:
    private UUID mId;
    private String mName;
    private String mContent;
    private double mWeight; //in Kg
    private double mLatitude;
    private double mLongitude;
    private Drawable mImage;
    private double mRating;
    private double mPrice;

    //UI helper attributes:
    private boolean mClicked;
    private float mDistance;

    public Box(){}

    //DAO getters/setters
    public UUID getId() {
        return mId;
    }
    public void setId(UUID id) {
        mId = id;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
    public String getContent() {
        return mContent;
    }
    public void setContent(String content) {
        mContent = content;
    }
    public double getWeight() { return mWeight; }
    public void setWeight(double weight) { mWeight = weight; }
    public double getLatitude() {
        return mLatitude;
    }
    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
    public Drawable getImage() { return mImage; }
    public void setImage(Drawable image) { mImage = image; }
    public double getRating() {
        return mRating;
    }
    public void setRating(double rating) {
        mRating = rating;
    }
    public double getPrice() { return mPrice; }
    public void setPrice(double price) { mPrice = price; }

    //UI getters/setters:
    public boolean isClicked() { return mClicked; }
    public void setClicked(boolean clickedOrNot){ mClicked = clickedOrNot; }
    public void setDistance(float distance) {
        mDistance = distance;
    }
    public float getDistance() {
        return mDistance;
    }

    @Override
    public int hashCode() {
        return getId().hashCode() * 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Box))
            return false;
        return this.getId().equals(((Box) obj).getId());
    }
}
