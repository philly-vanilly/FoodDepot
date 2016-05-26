package de.uni_hamburg.vsis.fooddepot.fooddepotclient;

import android.app.Application;

/**
 * Created by paul on 26.05.16.
 */
public class FDepotApplication extends Application{

    private static FDepotApplication application = null;

    public static FDepotApplication getApplication(){
        return application;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        application = this;
    }


}
