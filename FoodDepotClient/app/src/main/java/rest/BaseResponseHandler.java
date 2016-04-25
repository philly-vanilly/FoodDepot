package rest;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by paul on 26.04.16.
 */
public abstract class BaseResponseHandler extends AsyncHttpResponseHandler {

    private static String REQUEST_FAILED = "REQUEST_FAILED";

//    @Override
//    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        //TODO: show toast
        Log.e(REQUEST_FAILED, responseBody == null?"code:" + statusCode:new String(responseBody));
    }
}
