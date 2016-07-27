package de.uni_hamburg.vsis.fooddepot.fooddepotclient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by Phil on 14.07.2016.
 */
public class Price {
    @SerializedName("cent")
    @Expose
    private long mCent;

    @SerializedName("euro")
    @Expose
    private long mEuro;

    @SerializedName("currency")
    @Expose
    private String mCurrency;


    public long getCent() {
        return mCent;
    }

    public void setCent(long cent) {
        mCent = cent;
    }

    public long getEuro() {
        return mEuro;
    }

    public void setEuro(long euro) {
        mEuro = euro;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public BigDecimal getBigIntValue(){
        return BigDecimal.valueOf(mEuro*100+mCent);
    }

    @Override
    public String toString(){
        BigDecimal value = getBigIntValue();
        value = value.divide(new BigDecimal(100));
        value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
        return value + " " + mCurrency;
    }
}
