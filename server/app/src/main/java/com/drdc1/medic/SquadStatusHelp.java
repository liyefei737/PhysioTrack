package com.drdc1.medic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SquadStatusHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad_status_help);
    }

    public void goBacktoSquadStatus (View v){
        Intent goBacktoSquadStatus = new Intent(SquadStatusHelp.this, HomeActivity.class);
        startActivity(goBacktoSquadStatus);
    }

}
