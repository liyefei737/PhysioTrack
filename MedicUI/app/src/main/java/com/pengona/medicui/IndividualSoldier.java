package com.pengona.medicui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class IndividualSoldier extends AppCompatActivity {
    TextView NameList, SquadStatus,IndSoldier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_soldier);



        // Setup Button Links to new activity
        NameList = (TextView) findViewById(R.id.tvNameList);
        SquadStatus=(TextView) findViewById(R.id.tvSStatus);
        IndSoldier=(TextView) findViewById(R.id.tvIndSoldier);

        NameList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(IndividualSoldier.this, NameList.class);
                startActivity(in);
            }
        });
        SquadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(IndividualSoldier.this, MainActivity.class);
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
    }
}
