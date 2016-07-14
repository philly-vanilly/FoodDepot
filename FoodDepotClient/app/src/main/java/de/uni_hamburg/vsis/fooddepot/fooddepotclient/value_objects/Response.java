package de.uni_hamburg.vsis.fooddepot.fooddepotclient.value_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Response<D> {
    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    public D data;
}
