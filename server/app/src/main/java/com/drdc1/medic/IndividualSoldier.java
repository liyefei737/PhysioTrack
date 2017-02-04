package com.drdc1.medic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class IndividualSoldier extends AppCompatActivity implements OnChartValueSelectedListener {
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;
    private LocalBroadcastManager broadcaster = null;
    private DataManager dataManager = null;
    int valueForTesting = 0;
    TextView NameList, SquadStatus, IndSoldier;
    EditText NameEditable, GenderEditable, AgeEditable, BodyOrientEditable;
    private LineChart mChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        broadcaster = LocalBroadcastManager.getInstance(this);
//
//        // An Android handler thread internally operates on a looper.
//        mHandlerThread = new HandlerThread("MyCustomService.HandlerThread");
//        mHandlerThread.start();
//        // An Android service handler is a handler running on a specific background thread.
//        mServiceHandler = new Handler(mHandlerThread.getLooper());

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

        mChart = (LineChart) findViewById(R.id.chart);
        mChart.setOnChartValueSelectedListener(this);

        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(LegendForm.LINE);
//        l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
//        xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.realtime, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionAdd: {
                addEntry();
                break;
            }
            case R.id.actionClear: {
                mChart.clearValues();
                Toast.makeText(this, "Chart cleared!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionFeedMultiple: {
                feedMultiple();
                break;
            }
        }
        return true;
    }

    private void addEntry() {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }


            Date now;
            int numSeconds = 1, millistep = 1000;
            JSONArray lastXSeconds;
//            while (true) {
//            try {
//                Thread.sleep(numSeconds * 1000);
//            } catch (Exception e) {
//                //
//            }

            now = new Date();

            Map<String, Object> infoMap = new HashMap<String, Object>();
            infoMap.put("name", "etst2");
            infoMap.put("age", "ets55");
            dataManager.addSoldier("tess8392", infoMap);

            Database db = dataManager.getSoldierDB("tess8392");
            Document doc = db.getDocument("02/03/2017 00:00:00.000");

            try {
                doc.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        Map<String, Object> properties = newRevision.getUserProperties();
                        properties.put("timeCreated", "02/03/2017 00:00:00.000");
                        properties.put("accX", "153");
                        properties.put("accY", "155");
                        properties.put("accZ", "85");
                        properties.put("skinTemp", "35.0");
                        properties.put("coreTemp", "36.8");
                        properties.put("heartRate", "68");
                        properties.put("breathRate", "14");
                        properties.put("bodyPosition", "PRONE");
                        properties.put("motion", "STOPPED");
                        newRevision.setUserProperties(properties);
                        return true;
                    }
                });
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }



            Map<String, Database> physioDataMap = dataManager.getPhysioDataMap();




            if (physioDataMap != null) {

                for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
//                    Database userDB = entry.getValue();



                    lastXSeconds = dataManager.QueryLastXSeconds(entry.getKey(), now, numSeconds, millistep);
                    Toast.makeText(getApplicationContext(), lastXSeconds.toString(), Toast.LENGTH_LONG).show();
                    NameEditable.setText(lastXSeconds.toString());

//                    try {
//                        NameEditable.setText(lastXSeconds.toString());
////                        data.addEntry(new Entry(set.getEntryCount(), (float) (lastXSeconds.get(valueForTesting)) + 30f), 0);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                }
                valueForTesting++;
            }
//            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_LONG).show();
//            }


//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private Thread thread;

    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry();
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

}
