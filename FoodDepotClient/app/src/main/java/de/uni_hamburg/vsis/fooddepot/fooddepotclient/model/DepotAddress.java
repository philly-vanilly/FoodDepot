package de.uni_hamburg.vsis.fooddepot.fooddepotclient.model;

/**
 * Created by Phil on 14.07.2016.
 */
public class DepotAddress {
    private String mPostCode;
    private String mCity;
    private String mStreet;
    private String mDescription;

    public String getPostCode() {
        return mPostCode;
    }

    public void setPostCode(String postCode) {
        mPostCode = postCode;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getStreet());
        sb.append("\n");
        sb.append(getPostCode());
        sb.append("\n");
        sb.append(getCity());
        sb.append("\n");
        sb.append("(").append(getDescription()).append(")");

        return sb.toString();
    }
}
