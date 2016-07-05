package de.uni_hamburg.vsis.fooddepot.fooddepotclient.dao;

/**
 * Created by Phil on 05.07.2016.
 */
public class BoxDaoOnline {
    //TODO: implement, simply moved from BoxesActivity
//    private void updateBoxList(){
//        if(mLastLocation != null) {
//            updateBoxList(mLastLocation.getLatitude(), mLastLocation.getLongitude(), mCurrentSearchString);
//        }
//    }

//    private void updateBoxList(double latitude, double longitude, String keys) {
//        RestClient.search(keys, latitude, longitude, new BaseResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//            if (responseBody != null) {
//                String responseAsString = new String(responseBody);
//                Log.d(TAG, "search box success:" + responseAsString);
//
//                Type collectionType = new TypeToken<Response<List<Box>>>() {}.getType();
//                Response<List<Box>> boxResponse = gson.fromJson(responseAsString, collectionType);
//
//                updateBoxFragment(boxResponse.data);
//
//            } else {
//                Log.e(TAG, "search box success but response body null");
//            }
//            }
//        });
//    }
}
