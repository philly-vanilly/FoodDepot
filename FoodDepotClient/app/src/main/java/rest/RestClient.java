package rest;

import android.os.Handler;
import com.loopj.android.http.*;

/**
 * Created by paul on 24.04.16.
 */
public class RestClient {


    private final static String BASE_ADDRESS = "https://localhost:5555/";
    private static AsyncHttpClient client = new AsyncHttpClient();


    private static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String endpoint) {
        return BASE_ADDRESS + endpoint;
    }




    public static void createUser(String username, String password, String email, AsyncHttpResponseHandler handler){

        RequestParams params = new RequestParams("username", username);
        params.add("password" , password);
        params.add("email", email);


        post("api/createUser", params, handler);
    }


    public static void login(String username, String password, AsyncHttpResponseHandler handler){
        RequestParams params = new RequestParams("username", username);
        params.add("password" , password);


        get("api/login", params, handler);

    }

    public static void logout(AsyncHttpResponseHandler handler){
        get("api/logout", new RequestParams(), handler);
    }



}
