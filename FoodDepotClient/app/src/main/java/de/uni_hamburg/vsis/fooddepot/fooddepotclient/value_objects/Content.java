package de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Phil on 07.07.2016.
 */
public class Content {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("latitude")
    @Expose
    public float latitude;
    @SerializedName("ownerName")
    @Expose
    public String ownerName;
    @SerializedName("temperature")
    @Expose
    public int temperature;
    @SerializedName("overallRating")
    @Expose
    public float overallRating;
    @SerializedName("price")
    @Expose
    public String price;
    @SerializedName("userRatingCount")
    @Expose
    public int userRatingCount;
    @SerializedName("longitude")
    @Expose
    public float longitude;
    @SerializedName("lastFillingDate")
    @Expose
    public Object lastFillingDate;
    @SerializedName("fillingStatus")
    @Expose
    public float fillingStatus;
}
