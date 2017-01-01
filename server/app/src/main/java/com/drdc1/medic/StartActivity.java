package com.drdc1.medic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.couchbase.lite.Database;


public class StartActivity extends AppCompatActivity {
    BackgroundService backgroundService;
    private Database db;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        backgroundService = BackgroundService.get_backgroundService();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView  userNames = (TextView) findViewById(R.id.text_id);
                StringBuilder sb = new StringBuilder();
                sb.append("Id: ");
                sb.append(intent.getStringExtra("_id"));
                sb.append("\t");
                sb.append("Name: ");
                sb.append(intent.getStringExtra("fistName"));
                sb.append(" ");
                sb.append(intent.getStringExtra("lastName"));
                userNames.append(" "+sb.toString()+" \n");
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(BackgroundService.DB_UPDATE)
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}
