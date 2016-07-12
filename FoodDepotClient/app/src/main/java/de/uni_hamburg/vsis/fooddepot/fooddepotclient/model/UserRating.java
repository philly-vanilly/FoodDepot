package de.uni_hamburg.vsis.fooddepot.fooddepotclient.model;

import java.util.Date;

/**
 * Created by Phil on 26.06.2016.
 */
public class UserRating {
    private Date mDate;
    private String mUserName;
    private String mUserID;
    private double mOverallRating;
    private double mFittingDescriptionRating;
    private double mTasteRating;
    private double mCommunicationRating;
    private double mPriceRating;
    private String mComment;

    public UserRating(){}

    public double getOverallRating() {
        return mOverallRating;
    }
    public String getComment() {
        return mComment;
    }
    public void setComment(String comment) {
        mComment = comment;
    }
    public Date getDate() {
        return mDate;
    }
    public void setDate(Date date) {
        mDate = date;
    }
    public String getUserName() {
        return mUserName;
    }
    public void setUserName(String userName) {
        mUserName = userName;
    }
    public String getUserID() {
        return mUserID;
    }
    public void setUserID(String userID) {
        mUserID = userID;
    }
    public void setOverallRating(double overallRating) {
        mOverallRating = overallRating;
    }
    public double getFittingDescriptionRating() {
        return mFittingDescriptionRating;
    }
    public void setFittingDescriptionRating(double fittingDescriptionRating) {
        mFittingDescriptionRating = fittingDescriptionRating;
    }
    public double getTasteRating() {
        return mTasteRating;
    }
    public void setTasteRating(double tasteRating) {
        mTasteRating = tasteRating;
    }
    public double getCommunicationRating() {
        return mCommunicationRating;
    }
    public void setCommunicationRating(double communicationRating) {
        mCommunicationRating = communicationRating;
    }
    public double getPriceRating() {
        return mPriceRating;
    }
    public void setPriceRating(double priceRating) {
        mPriceRating = priceRating;
    }
}
