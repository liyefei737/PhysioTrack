package capstone.client.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import capstone.client.BackgroundServices.BackgroundDataSim;
import capstone.client.BackgroundServices.BackgroundSleepAlgo;
import capstone.client.BackgroundServices.BackgroundUIUpdator;
import capstone.client.BackgroundServices.BackgroundWellnessAlgo;
import capstone.client.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        /***
         * starting background threads
         */

        Intent dataSim = new Intent();
        dataSim.setClass(this, BackgroundDataSim.class);
        startService(dataSim);

        Intent sleepAlgo = new Intent();
        sleepAlgo.setClass(this, BackgroundSleepAlgo.class);
        startService(sleepAlgo);

        Intent wellnessAlgo = new Intent();
        wellnessAlgo.setClass(this, BackgroundWellnessAlgo.class);
        startService(wellnessAlgo);

        Intent uiUpdator = new Intent();
        uiUpdator.setClass(this, BackgroundUIUpdator.class);
        startService(uiUpdator);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.previously_started), false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.previously_started), Boolean.TRUE);
            edit.commit();
            startActivity(new Intent(MainActivity.this, SetupActivity.class));
        }
        else
            startActivity(new Intent(MainActivity.this, BottomBarActivity.class));

        finish();


    }



}
