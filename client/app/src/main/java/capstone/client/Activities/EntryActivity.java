package capstone.client.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import capstone.client.BackgroundServices.BackgroundDataSim;
import capstone.client.BackgroundServices.BackgroundUIUpdator;
import capstone.client.BackgroundServices.BackgroundWellnessAlgo;
import capstone.client.R;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_entry);
        /***
         * starting background threads
         */

        Intent dataSim = new Intent();
        dataSim.setClass(this, BackgroundDataSim.class);
        startService(dataSim);

        Intent wellnessAlgo = new Intent();
        wellnessAlgo.setClass(this, BackgroundWellnessAlgo.class);
        startService(wellnessAlgo);

        Intent uiUpdator = new Intent();
        uiUpdator.setClass(this, BackgroundUIUpdator.class);
        startService(uiUpdator);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.previously_started), false);
        if(!previouslyStarted) {
            startActivity(new Intent(EntryActivity.this, SetupActivity.class));
        }
        else
            startActivity(new Intent(EntryActivity.this, BottomBarActivity.class));

        finish();


    }



}
