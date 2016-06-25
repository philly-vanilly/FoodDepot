package de.uni_hamburg.vsis.fooddepot.fooddepotclient.network;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.beans.Account;

/**
 * Created by paul on 26.05.16.
 */
public class FDepotApplication extends Application{

    private static final String TAG = "FDepotApplication";

    private static FDepotApplication application = null;

    public static FDepotApplication getApplication(){
        return application;
    }

    private Account currentAccount = null;

    @Override
    public void onCreate(){
        super.onCreate();
        application = this;
    }

    public Account getCurrentAccount(){
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount){
        this.currentAccount = currentAccount;
    }


    // Gson is a Java library that can be used to convert Java Objects into their JSON representation.
    // It can also be used to convert a JSON string to an equivalent Java object.

    public void saveUser(Activity activity){
        SharedPreferences settings = getSharedPreferences("accountPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();

        // Java Object to pretty String:
        Gson prettyPrintGson = new GsonBuilder().setPrettyPrinting().create();
        //method name says toJson but is actually toString:
        Log.d(TAG, "Putting following data into SharedPreferences.Editor (accountPrefs.account): " + prettyPrintGson.toJson(currentAccount));

        Gson compactPrintGson = new Gson();
        String jsonStringAccount = compactPrintGson.toJson(currentAccount);
        editor.putString("account", jsonStringAccount);
        editor.commit();
    }

    public void loadUser(Activity activity){
        SharedPreferences settings = getSharedPreferences("accountPrefs", 0);
        String accountAsString = settings.getString("account", "");
        if(!accountAsString.equals("")) {
            Gson gson = new Gson();

            //Ugly JSON to pretty String:
            JsonParser jp = new JsonParser();
            JsonElement accountAsJsonElement = jp.parse(accountAsString);
            Gson prettyPrintGson = new GsonBuilder().setPrettyPrinting().create();
            //method name says toJson but is actually toString:
            Log.d(TAG, "Getting following data from SharedPreferences.Editor (accountPrefs.account): " + prettyPrintGson.toJson(accountAsJsonElement));

            currentAccount = gson.fromJson(accountAsString, Account.class);
        }
    }
}
