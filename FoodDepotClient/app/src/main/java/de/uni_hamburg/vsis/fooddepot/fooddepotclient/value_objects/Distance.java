package de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Phil on 07.07.2016.
 */
public class Distance {
    @SerializedName("value")
    @Expose
    public float value;
    @SerializedName("metric")
    @Expose
    public String metric;
    @SerializedName("normalizedValue")
    @Expose
    public float normalizedValue;
    @SerializedName("unit")
    @Expose
    public String unit;
}
