package com.drdc1.medic;

import android.content.Intent;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/*************
 * App Initialization class
 */
public class Application extends android.app.Application {

    public DataManager dataManager = null;
    @Override
    public void onCreate() {
        super.onCreate();
        dataManager = new DataManager(this);
        startBackgroundServices();
        showApp();
    }

    private void showApp() {
        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getApplicationContext(), StartActivity.class);
        startActivity(intent);
    }

    private void startBackgroundServices() {
        Intent Serviceintent = new Intent();
        Serviceintent.setClass(this, BackgroundServer.class);
        startService(Serviceintent);

        Intent sleepAlgo = new Intent();
        sleepAlgo.setClass(this, BackgroundSleepAlgo.class);
        startService(sleepAlgo);

        Intent wellnessAlgo = new Intent();
        wellnessAlgo.setClass(this, BackgroundWellnessAlgo.class);
        startService(wellnessAlgo);
    }

    public DataManager getDataManager(){
        return dataManager;
    }
}






