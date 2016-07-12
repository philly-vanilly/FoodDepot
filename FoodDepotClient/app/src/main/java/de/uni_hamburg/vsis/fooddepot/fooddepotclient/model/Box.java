package de.uni_hamburg.vsis.fooddepot.fooddepotclient.model;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Box {
    private static final transient String TAG = "Box";

    //DTO attributes for serialization and REST-communication:
    @SerializedName("id")
    @Expose
    private UUID mId;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("latitude")
    @Expose
    private double mLatitude;
    @SerializedName("longitude")
    @Expose
    private double mLongitude;
    @SerializedName("overallRating")
    @Expose
    private double mOverallUserRating;
    @SerializedName("price")
    @Expose
    private double mPrice;
    @SerializedName("ownerName")
    @Expose
    private String mOwnerName;
    @SerializedName("temperature")
    @Expose
    private double mTemperature;
    @SerializedName("userRatingCount")
    @Expose
    private int mUserRatingCount;
    @SerializedName("lastFillingDate")
    @Expose
    public Object lastFillingDate;
    @SerializedName("fillingStatus")
    @Expose
    public float fillingStatus;

    //TODO: change REST interface!
    @Expose(serialize = false, deserialize = false)
    private transient String mContent;
    @Expose(serialize = false, deserialize = false)
    private transient double mWeight; //in Kg
    @Expose(serialize = false, deserialize = false)
    private transient Drawable mImage;
    @Expose(serialize = false, deserialize = false)
    private transient String mAddress;
    @Expose(serialize = false, deserialize = false)
    private transient String mSmell;
    @Expose(serialize = false, deserialize = false)
    private transient String mReservationExpiration;

    //NOTE: Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    //There is also ExclusionStrategy interface for GSON

    //UI helper attributes:
    @Expose(serialize = false, deserialize = false)
    private transient boolean mIsClicked; //transient fields -should theoretically not- get serialized by GSON etc
    @Expose(serialize = false, deserialize = false)
    private transient float mDistance;
    @Expose(serialize = false, deserialize = false)
    private transient boolean mIsBoxReservedByMe;

    public Box(){}

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
    public double getPrice() { return mPrice; }
    public void setPrice(double price) { mPrice = price; }
    public String getAddress() {
        return mAddress;
    }
    public void setAddress(String address) {
        mAddress = address;
    }
    public boolean isClicked() { return mIsClicked; }
    public void setClicked(boolean isClicked){ mIsClicked = isClicked; }
    public void setDistance(float distance) {
        mDistance = distance;
    }
    public float getDistance() {
        return mDistance;
    }
    public String getOwnerName() {
        return mOwnerName;
    }
    public void setOwnerName(String ownerName){ mOwnerName = ownerName; }
    public double getOverallUserRating() {
        return mOverallUserRating;
    }
    public void setOverallUserRating(double overallUserRating) {
        mOverallUserRating = overallUserRating;
    }
    public int getUserRatingCount() {
        return mUserRatingCount;
    }
    public void setUserRatingCount(int userRatingCount) {
        mUserRatingCount = userRatingCount;
    }
    public double getTemperature() {
        return mTemperature;
    }
    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }
    public String getSmell() {
        return mSmell;
    }
    public void setSmell(String smell) {
        mSmell = smell;
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
