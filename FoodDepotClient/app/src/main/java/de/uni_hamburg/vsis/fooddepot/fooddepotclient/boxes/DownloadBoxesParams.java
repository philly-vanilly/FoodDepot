package de.uni_hamburg.vsis.fooddepot.fooddepotclient.boxes;

/**
 * Created by Phil on 17.07.2016.
 */
public class DownloadBoxesParams {
    private String mSearchString;
    private Integer mNumberOfBoxes;
    private Integer mFetchedBoxes;
    private String mAuthToken;
    private Double mLatitude;
    private Double mLongitude;

    public DownloadBoxesParams(String searchString, Integer fetchedBoxes, Integer numberOfBoxes, String authToken, Double latitude, Double longitude) {
        mSearchString = searchString;
        mFetchedBoxes = fetchedBoxes;
        mNumberOfBoxes = numberOfBoxes;
        mAuthToken = authToken;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getSearchString() {
        return mSearchString;
    }
    public Integer getNumberOfBoxes() {
        return mNumberOfBoxes;
    }
    public Integer getFetchedBoxes() {
        return mFetchedBoxes;
    }
    public String getAuthToken() {
        return mAuthToken;
    }
    public Double getLatitude() {
        return mLatitude;
    }
    public Double getLongitude() {
        return mLongitude;
    }
}
