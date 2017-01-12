package com.drdc1.medic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.couchbase.lite.Database;


public class StartActivity extends AppCompatActivity {
    BackgroundService backgroundService;
    private Database db;
    private BroadcastReceiver receiver;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //ListView listView = (ListView) findViewById(R.id.list);
//        setupViewAndQuery();
        //listView.setAdapter(listAdapter);
        Button show_db_data = (Button) findViewById(R.id.show_db_data);
        show_db_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, DBViewer.class));
            }
        });
        backgroundService = BackgroundService.getBackgroundService();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView  userNames = (TextView) findViewById(R.id.text_id);
                StringBuilder sb = new StringBuilder();
                sb.append("Id: ");
                sb.append(intent.getStringExtra("_id"));
                sb.append("\t");
                sb.append("\"ECG1 (raw): ");
                sb.append(intent.getStringExtra("ECG1 (raw)"));
                sb.append(" ECG2 (raw): ");
                sb.append(intent.getStringExtra("ECG2 (raw)"));
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

//    private void setupViewAndQuery() {
//        db = backgroundService.getDatabase();
//        com.couchbase.lite.View dbView = db.getView("listView");
//        if (dbView.getMap() == null) {
//            dbView.setMap(new Mapper() {
//                @Override
//                public void map(Map<String, Object> document, Emitter emitter) {
//                    String type = (String) document.get("type");
//                    if ("task-list".equals(type)) {
//                        emitter.emit(document.get("name"), null);
//                    }
//                }
//            }, "1.0");
//        }
//    }

}
