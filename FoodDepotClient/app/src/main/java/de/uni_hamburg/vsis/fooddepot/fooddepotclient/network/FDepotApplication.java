package de.uni_hamburg.vsis.fooddepot.fooddepotclient.network;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Objects;

import de.uni_hamburg.vsis.fooddepot.fooddepotclient.R;
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

    private Account mCurrentAccount;

    @Override
    public void onCreate(){
        super.onCreate();
        application = this;
    }

    public Account getCurrentAccount(){
        if (mCurrentAccount == null){
            setCurrentAccount(new Account());
            loadUser();
        }
        return mCurrentAccount;
    }

    public void setCurrentAccount(Account currentAccount){
        mCurrentAccount = currentAccount;
    }

    public void saveUser(){
        //TODO: replace dummy values
        //TODO: save password not accessible and not in plaintext
        //MODE_PRIVATE = opened SharedPreferences-file is accessible only by my app
        SharedPreferences profileSharedPreferences = getSharedPreferences("food_depot_profile_shared_preferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putString(getString(R.string.saved_profile_username), mCurrentAccount.getUsername());
        editor.putString(getString(R.string.saved_profile_firstname), mCurrentAccount.getFirstName());
        editor.putString(getString(R.string.saved_profile_lastname), mCurrentAccount.getLastName());
        editor.putString(getString(R.string.saved_profile_email), mCurrentAccount.getEmail());
        editor.putString(getString(R.string.saved_profile_password), mCurrentAccount.getPassword());
        editor.commit();
    }

    public void loadUser(){
        SharedPreferences profileSharedPreferences = getSharedPreferences("food_depot_profile_shared_preferences", Activity.MODE_PRIVATE);
        String defaultValue = "";
        mCurrentAccount.setUsername(profileSharedPreferences.getString(getString(R.string.saved_profile_username), defaultValue));
        mCurrentAccount.setFirstName(profileSharedPreferences.getString(getString(R.string.saved_profile_firstname), defaultValue));
        mCurrentAccount.setLastName(profileSharedPreferences.getString(getString(R.string.saved_profile_lastname), defaultValue));
        mCurrentAccount.setEmail(profileSharedPreferences.getString(getString(R.string.saved_profile_email), defaultValue));
        mCurrentAccount.setPassword(profileSharedPreferences.getString(getString(R.string.saved_profile_password), defaultValue));
    }
}
