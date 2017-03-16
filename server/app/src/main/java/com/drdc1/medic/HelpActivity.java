package com.drdc1.medic;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

/*
red
0
yellow
1
green
2
yellow
3
red
*/

public class HelpActivity extends AppCompatActivity {
    private ImageButton ib;
    Button cancel, save;
    EditText hrate0, hrate1, hrate2, hrate3, rrate0, rrate1, rrate2, rrate3, ct0, ct1, ct2, ct3,
            st0, st1, st2, st3;
    Integer hrate0i, hrate1i, hrate2i, hrate3i, rrate0i, rrate1i, rrate2i, rrate3i;
    Float ct0i, ct1i, ct2i, ct3i, st0i, st1i, st2i, st3i;
    private List<Integer> hrRange = new ArrayList<Integer>();
    private List<Integer> brRange = new ArrayList<Integer>();
    private List<Float> stRange = new ArrayList<Float>();
    private List<Float> ctRange = new ArrayList<Float>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        hrate0 = (EditText) findViewById(R.id.hrate0);
        hrate1 = (EditText) findViewById(R.id.hrate1);
        hrate2 = (EditText) findViewById(R.id.hrate2);
        hrate3 = (EditText) findViewById(R.id.hrate3);
        rrate0 = (EditText) findViewById(R.id.rrate0);
        rrate1 = (EditText) findViewById(R.id.rrate1);
        rrate2 = (EditText) findViewById(R.id.rrate2);
        rrate3 = (EditText) findViewById(R.id.rrate3);
        ct0 = (EditText) findViewById(R.id.ct0);
        ct1 = (EditText) findViewById(R.id.ct1);
        ct2 = (EditText) findViewById(R.id.ct2);
        ct3 = (EditText) findViewById(R.id.ct3);
        st0 = (EditText) findViewById(R.id.st0);
        st1 = (EditText) findViewById(R.id.st1);
        st2 = (EditText) findViewById(R.id.st2);
        st3 = (EditText) findViewById(R.id.st3);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hrate0i = Integer.valueOf(hrate0.getText().toString());
                hrate1i = Integer.valueOf(hrate1.getText().toString());
                hrate2i = Integer.valueOf(hrate2.getText().toString());
                hrate3i = Integer.valueOf(hrate3.getText().toString());
                rrate0i = Integer.valueOf(rrate0.getText().toString());
                rrate1i = Integer.valueOf(rrate1.getText().toString());
                rrate2i = Integer.valueOf(rrate2.getText().toString());
                rrate3i = Integer.valueOf(rrate3.getText().toString());
                ct0i = Float.valueOf(ct0.getText().toString());
                ct1i = Float.valueOf(ct1.getText().toString());
                ct2i = Float.valueOf(ct2.getText().toString());
                ct3i = Float.valueOf(ct3.getText().toString());
                st0i = Float.valueOf(st0.getText().toString());
                st1i = Float.valueOf(st1.getText().toString());
                st2i = Float.valueOf(st2.getText().toString());
                st3i = Float.valueOf(st3.getText().toString());
                hrRange.add(hrate0i);
                hrRange.add(hrate1i);
                hrRange.add(hrate2i);
                hrRange.add(hrate3i);
                brRange.add(rrate0i);
                brRange.add(rrate1i);
                brRange.add(rrate2i);
                brRange.add(rrate3i);
                stRange.add(st0i);
                stRange.add(st1i);
                stRange.add(st2i);
                stRange.add(st3i);
                ctRange.add(ct0i);
                ctRange.add(ct1i);
                ctRange.add(ct2i);
                ctRange.add(ct3i);

                Intent wellnessAlgo = new Intent();
                wellnessAlgo.setClass(HelpActivity.this, BackgroundWellnessAlgo.class);
                wellnessAlgo.putExtra("hrRange", (Parcelable) hrRange);
                wellnessAlgo.putExtra("brRange", (Parcelable) brRange);
                wellnessAlgo.putExtra("stRange", (Parcelable) stRange);
                wellnessAlgo.putExtra("ctRange", (Parcelable) ctRange);
                startService(wellnessAlgo);

//                BackgroundWellnessAlgo bgalgo = new BackgroundWellnessAlgo();
//                bgalgo.setPhysioParamThresh(hrRange, brRange, stRange, ctRange);

            }
        });

        ib = (ImageButton) findViewById(R.id.ibBack);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpActivity.this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

    }
}
