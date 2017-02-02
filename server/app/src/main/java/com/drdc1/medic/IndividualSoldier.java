package com.drdc1.medic;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class IndividualSoldier extends AppCompatActivity {
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;
    private LocalBroadcastManager broadcaster = null;
    private DataManager dataManager = null;

    TextView NameList, SquadStatus, IndSoldier;
    EditText NameEditable, GenderEditable, AgeEditable, BodyOrientEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        broadcaster = LocalBroadcastManager.getInstance(this);

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("MyCustomService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new Handler(mHandlerThread.getLooper());

        dataManager = ((Application) this.getApplication()).getDataManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_soldier);


        // Setup Button Links to new activity
        NameEditable = (EditText) findViewById(R.id.etSoldierName);
        GenderEditable = (EditText) findViewById(R.id.etGender);
        AgeEditable = (EditText) findViewById(R.id.editText4);
        BodyOrientEditable = (EditText) findViewById(R.id.editText8);
        NameList = (TextView) findViewById(R.id.tvNameList);
        SquadStatus = (TextView) findViewById(R.id.tvSStatus);
        IndSoldier = (TextView) findViewById(R.id.tvIndSoldier);

        NameList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent in = new Intent(IndividualSoldier.this, NameList.class);
//                startActivity(in);
            }
        });
        SquadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(IndividualSoldier.this, StartActivity.class);
                startActivity(in);
            }
        });
        IndSoldier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(IndividualSoldier.this, IndividualSoldier.class);
                startActivity(in);
            }
        });
        //End of OnClick Links
        NameEditable.setText("thistest");
        GenderEditable.setText("thistest1");
        AgeEditable.setText("thistest2");
        BodyOrientEditable.setText("thistest3");

        SquadStatus.setText("DENEME");
    }
}
