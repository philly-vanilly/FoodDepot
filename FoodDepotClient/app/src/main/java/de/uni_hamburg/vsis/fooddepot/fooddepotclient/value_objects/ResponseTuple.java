package de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Phil on 08.07.2016.
 */
public class ResponseTuple<C, D> {
    @SerializedName("content")
    @Expose
    public C content;
    @SerializedName("distance")
    @Expose
    public D distance;
}
