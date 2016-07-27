package de.uni_hamburg.vsis.fooddepot.fooddepotclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response<D> {
    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    public D data;

    public boolean isSuccess() {
        return success;
    }

    public D getData() {
        return data;
    }
}
