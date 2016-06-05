package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import rest.beans.User;

/**
 * Created by paul on 26.05.16.
 */
public class FDepotApplication extends Application{

    private static FDepotApplication application = null;

    public static FDepotApplication getApplication(){
        return application;
    }


    private User currentUser = null;




    @Override
    public void onCreate(){
        super.onCreate();
        application = this;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }


    public void saveUser(Activity activity){
        SharedPreferences settings = getSharedPreferences("userPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        editor.putString("user", gson.toJson(currentUser));
        editor.commit();

    }

    public void loadUser(Activity activity){
        SharedPreferences settings = getSharedPreferences("userPrefs", 0);
        String userAsString = settings.getString("user", "");
        if(!userAsString.equals("")) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(userAsString, User.class);
        }
    }


}
