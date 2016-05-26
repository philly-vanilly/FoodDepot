package rest;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import de.uni_hamburg.vsis.fooddepot.fooddepotclient.FDepotApplication;

/**
 * Created by paul on 24.04.16.
 */
public class RestClient {


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




    public static void createAccount(final String userName, final String password_, final String email_, AsyncHttpResponseHandler handler){

        RequestParams params = new RequestParams();
        params.add("username", userName);
        params.add("password" , password_);
        params.add("email", email_);
        params.add("firstName","asdf");
        params.add("lastName", "adfadfadf");

        Object createUserObject = new Object() {
            String username = userName;
            String password = password_;
            String email =  email_;

            String firstname = "asdf";
            String lastname = "lastname";

        };
        get("createAccount", params, handler);



        Gson gson = new GsonBuilder().create();
        String jsonCreateUserObject = gson.toJson(createUserObject);

        try {
            ByteArrayEntity entity = new ByteArrayEntity(jsonCreateUserObject.getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post("/createAccount", params, entity , handler);
        } catch (Exception e){
            Log.e("createAccount", "probably encoding exception");
        }



    }


    public static void login(String username, String password, AsyncHttpResponseHandler handler){

        try {
            Log.d("login", "username:" + username);
            Log.d("login", "password:" + password);
//            String authString = "Basic " + Base64.encodeToString((username + ":" + password).getBytes("UTF-8"), Base64.DEFAULT);
//            Log.d("authString", authString);
            RequestParams params = new RequestParams();
            //params.add("password" , password);

            client.setBasicAuth(username, password);
            get("getAccount", params, handler);

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
