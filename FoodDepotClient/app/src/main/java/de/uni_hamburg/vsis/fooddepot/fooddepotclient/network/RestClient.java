package de.uni_hamburg.vsis.fooddepot.fooddepotclient.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Account;

/**
 * Created by paul on 24.04.16.
 */
public class RestClient {

    private final static String TAG = "RestClient";
    private final static String BASE_ADDRESS = "http://fdepot.herokuapp.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void post(String url, RequestParams params, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        Context context = FDepotApplication.getApplication().getApplicationContext();
        client.post(context, getAbsoluteUrl(url), entity, "application/json" ,  responseHandler);
    }

    private static String getAbsoluteUrl(String endpoint) {
        return BASE_ADDRESS + endpoint;
    }

    public static void createAccount(final String username, final String password, final String email, AsyncHttpResponseHandler handler){
        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setPassword(password);
        newAccount.setEmail(email); //TODO: replace dummy data
        newAccount.setFirstName("Paul");
        newAccount.setLastName("Test");

        FDepotApplication.getApplication().setCurrentAccount(newAccount);
        Gson gson = new Gson();
        String requestBody = gson.toJson(newAccount);

        Log.d(TAG, "url:" + "createAccount" + " body: " + requestBody );
        post("createAccount", new RequestParams(), new StringEntity(requestBody, "UTF-8"), handler);

//        try {
//            ByteArrayEntity entity = new ByteArrayEntity(jsonCreateUserObject.getBytes("UTF-8"));
//            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            post("/createAccount", params, entity , handler);
//        } catch (Exception e){
//            Log.e("createAccount", "probably encoding exception");
//        }
    }


    public static void login(String username, String password, AsyncHttpResponseHandler handler){
        try {
            Account newAccount = new Account();
            newAccount.setUsername(username);
            newAccount.setPassword(password);
            newAccount.setEmail(username +  "@blabla.com");//TODO: replace dummy data

            newAccount.setFirstName("Paul");
            newAccount.setLastName("Test");

            FDepotApplication.getApplication().setCurrentAccount(newAccount);
            RequestParams params = new RequestParams();
            //params.add("password" , password);

            client.setBasicAuth(username, password);
            get("login", params, handler);

        } catch (Exception e){
            Log.e("login", "probably encoding exception");
        }
    }

    public static void logout(AsyncHttpResponseHandler handler){
        //get("api/logout", new RequestParams(), handler)
        client.removeAllHeaders();

        Header[] emptyHeaders = new Header[] {};
        byte[] emptyBody = new byte[] {};

        handler.onSuccess(0, emptyHeaders, emptyBody);
    }

    public static void search(String searchString, double latitude, double longitude, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams("keys", searchString);
        params.add("latitude", ""+latitude);
        params.add("longitude", ""+longitude);

        get("searchBox", params, handler);
    }
}
