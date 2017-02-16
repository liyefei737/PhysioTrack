package capstone.client.Activities;

import android.content.Intent;
import android.os.Bundle;
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

        Intent mainActivity = new Intent(MainActivity.this, BottomBarActivity.class);
        startActivity(mainActivity);

        Intent uiUpdator = new Intent();
        uiUpdator.setClass(this, BackgroundUIUpdator.class);
        startService(uiUpdator);

    }

}
