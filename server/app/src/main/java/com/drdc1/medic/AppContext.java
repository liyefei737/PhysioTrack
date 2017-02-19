package com.drdc1.medic;

import android.app.Application;
import android.content.Context;

/**
 * Created by ub on 2/14/17.
 */

public class AppContext extends Application {

    private static AppContext instance;
    public DataManager dataManager = null;

    @Override
    public void onCreate() {
        super.onCreate();
        dataManager = new DataManager(this);
    }

    public AppContext() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
    public DataManager getDataManager() { return dataManager;}

}
