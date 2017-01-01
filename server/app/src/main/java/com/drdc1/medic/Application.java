package com.drdc1.medic;

import android.content.Intent;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/*************
 * App Initialization class
 */
public class Application extends android.app.Application {

    static BackgroundService backgroundService;

    @Override
    public void onCreate() {
        super.onCreate();
        starBackgroundService();
        showApp();
    }

    private void showApp() {
        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getApplicationContext(), StartActivity.class);
        startActivity(intent);
    }

    private void starBackgroundService() {
        Intent Serviceintent = new Intent();
        Serviceintent.setClass(this, BackgroundService.class);
        startService(Serviceintent);
    }
}






