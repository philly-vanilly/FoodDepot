package de.uni_hamburg.vsis.fooddepot.fooddepotclient.network;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Objects;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.model.Account;

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

    public void saveUser(Activity activity){
        SharedPreferences settings = getSharedPreferences("accountPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();

        Gson compactPrintGson = new Gson();
        String jsonStringAccount = compactPrintGson.toJson(currentAccount);
        editor.putString("account", jsonStringAccount);
        editor.commit();
    }

    public void loadUser(Activity activity){
        SharedPreferences settings = getSharedPreferences("accountPrefs", 0);
        String accountAsString = settings.getString("account", "");
        if(!Objects.equals(accountAsString, "")) {
            Gson gson = new Gson();
            currentAccount = gson.fromJson(accountAsString, Account.class);
        }
    }
}
