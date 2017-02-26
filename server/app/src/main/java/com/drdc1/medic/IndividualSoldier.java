package com.drdc1.medic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;
import com.drdc1.medic.uitls.Trie;
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

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndividualSoldier extends AppCompatActivity implements OnChartValueSelectedListener {
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;
    private LocalBroadcastManager broadcaster = null;
    private DataManager dataManager = null;
    int valueForTesting = 0;
    TextView NameList, SquadStatus, IndSoldier;
    EditText NameEditable, GenderEditable, AgeEditable, BodyOrientEditable;
    private LineChart hrchart, respchart, skinchart, ctchart;
    private BroadcastReceiver receiver;
    List<Entry> entries = new ArrayList<Entry>();
    private FloatingSearchView seachView;
    private Squad squad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        broadcaster = LocalBroadcastManager.getInstance(this);
//
//        // An Android handler thread internally operates on a looper.
//        mHandlerThread = new HandlerThread("MyCustomService.HandlerThread");
//        mHandlerThread.start();
//        // An Android service handler is a handler running on a specific background thread.
//        mServiceHandler = new Handler(mHandlerThread.getLooper());

        dataManager = ((AppContext) this.getApplication()).getDataManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_soldier);
        squad = Squad.getInstance();
       // seachView = (FloatingSearchView) findViewById(R.id.searchBar);
        setupSearchBar();

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
                Intent in = new Intent(IndividualSoldier.this, NameList.class);
                startActivity(in);
            }
        });
        SquadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(IndividualSoldier.this, HomeFragment.class);
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

        hrchart = (LineChart) findViewById(R.id.hrrchart);
        respchart = (LineChart) findViewById(R.id.resprchart);
        skinchart = (LineChart) findViewById(R.id.skinrchart);
        ctchart = (LineChart) findViewById(R.id.ctrchart);

        hrchart.setOnChartValueSelectedListener(this);
        respchart.setOnChartValueSelectedListener(this);
        skinchart.setOnChartValueSelectedListener(this);
        ctchart.setOnChartValueSelectedListener(this);

        // enable description text
        hrchart.getDescription().setEnabled(true);
        respchart.getDescription().setEnabled(true);
        skinchart.getDescription().setEnabled(true);
        ctchart.getDescription().setEnabled(true);

        // enable touch gestures
        hrchart.setTouchEnabled(true);
        respchart.setTouchEnabled(true);
        skinchart.setTouchEnabled(true);
        ctchart.setTouchEnabled(true);

        // enable scaling and dragging
        hrchart.setDragEnabled(true);
        hrchart.setScaleEnabled(true);
        hrchart.setDrawGridBackground(false);

        respchart.setDragEnabled(true);
        respchart.setScaleEnabled(true);
        respchart.setDrawGridBackground(false);

        skinchart.setDragEnabled(true);
        skinchart.setScaleEnabled(true);
        skinchart.setDrawGridBackground(false);

        ctchart.setDragEnabled(true);
        ctchart.setScaleEnabled(true);
        ctchart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        hrchart.setPinchZoom(true);
        respchart.setPinchZoom(true);
        skinchart.setPinchZoom(true);
        ctchart.setPinchZoom(true);

        // set an alternative background color
        hrchart.setBackgroundColor(Color.LTGRAY);
        respchart.setBackgroundColor(Color.LTGRAY);
        skinchart.setBackgroundColor(Color.LTGRAY);
        ctchart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        hrchart.setData(data);
        respchart.setData(data);
        skinchart.setData(data);
        ctchart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = hrchart.getLegend();
        Legend lrespchart = respchart.getLegend();
        Legend lskinchart = skinchart.getLegend();
        Legend lctchart = ctchart.getLegend();

        // modify the legend ...
        l.setForm(LegendForm.LINE);
        lrespchart.setForm(LegendForm.LINE);
        lskinchart.setForm(LegendForm.LINE);
        lctchart.setForm(LegendForm.LINE);

//        l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);
        lrespchart.setTextColor(Color.WHITE);
        lskinchart.setTextColor(Color.WHITE);
        lctchart.setTextColor(Color.WHITE);

        XAxis xl = hrchart.getXAxis();
//        xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        XAxis xlrespchart = respchart.getXAxis();
//        xl.setTypeface(mTfLight);
        xlrespchart.setTextColor(Color.WHITE);
        xlrespchart.setDrawGridLines(false);
        xlrespchart.setAvoidFirstLastClipping(true);
        xlrespchart.setEnabled(true);

        XAxis xskinchart = skinchart.getXAxis();
//        xl.setTypeface(mTfLight);
        xskinchart.setTextColor(Color.WHITE);
        xskinchart.setDrawGridLines(false);
        xskinchart.setAvoidFirstLastClipping(true);
        xskinchart.setEnabled(true);

        XAxis xctchart = ctchart.getXAxis();
//        xl.setTypeface(mTfLight);
        xctchart.setTextColor(Color.WHITE);
        xctchart.setDrawGridLines(false);
        xctchart.setAvoidFirstLastClipping(true);
        xctchart.setEnabled(true);

        YAxis leftAxis = hrchart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = hrchart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxisrespchart = respchart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxisrespchart.setTextColor(Color.WHITE);
        leftAxisrespchart.setAxisMaximum(100f);
        leftAxisrespchart.setAxisMinimum(0f);
        leftAxisrespchart.setDrawGridLines(true);

        YAxis rightAxisrespchart = respchart.getAxisRight();
        rightAxisrespchart.setEnabled(false);

        YAxis leftAxisskinchart = skinchart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxisskinchart.setTextColor(Color.WHITE);
        leftAxisskinchart.setAxisMaximum(100f);
        leftAxisskinchart.setAxisMinimum(0f);
        leftAxisskinchart.setDrawGridLines(true);

        YAxis rightAxisskinchart = skinchart.getAxisRight();
        rightAxisskinchart.setEnabled(false);

        YAxis leftAxisctchart = ctchart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxisctchart.setTextColor(Color.WHITE);
        leftAxisctchart.setAxisMaximum(100f);
        leftAxisctchart.setAxisMinimum(0f);
        leftAxisctchart.setDrawGridLines(true);

        YAxis rightAxisctchart = ctchart.getAxisRight();
        rightAxisctchart.setEnabled(false);
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //receive your message here

                String message = intent.getStringExtra("message");
                Log.d("receiver", "Got message: " + message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                entries.add(new Entry(entries.size(), Float.parseFloat(message)));
                LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
                //dataSet.setHighlightEnabled(true);
                //dataSet.setColor();
                //dataSet.setValueTextColor(...); // styling, ...
                LineData lineData = new LineData(dataSet);
                //dataSet.setDrawFilled(true);
                //dataSet.setFillDrawable(gradientDrawable);
                hrchart.setData(lineData);
                XAxis xAxis = hrchart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextSize(14f);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);
                YAxis yAxis = hrchart.getAxisLeft();
                //yAxis.setTypeface(...);
                yAxis.setTextSize(14f); // set the text size
                yAxis.setAxisMinimum(10f); // start at zero
                yAxis.setAxisMaximum(20f); // the axis maximum is 100
                yAxis.setTextColor(Color.WHITE);
                //yAxis.setValueFormatter(new MyValueFormatter());
                //yAxis.setGranularity(1f); // interval 1
                yAxis.setLabelCount(5, true); // force 6 labels
                hrchart.setNoDataText("Loading...");
                hrchart.invalidate(); // refresh

            }

        };

        LocalBroadcastManager.getInstance(AppContext.getContext()).registerReceiver(receiver,
                new IntentFilter("custom-event-name"));

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
                hrchart.clearValues();
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

        LineData data = hrchart.getData();

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

            dataManager.addSoldier("tess81", infoMap);

            Database db = dataManager.getSoldierDB("tess81");
            Calendar nowCal = new GregorianCalendar();
            nowCal.set(2017, 01, 30);
            Document doc = db.getDocument(String.valueOf(DateUtils.round(nowCal,Calendar.MINUTE).getTimeInMillis()));

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
            int numMinutes = 5;

            if (physioDataMap != null) {

                for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
//                    Database userDB = entry.getValue();

                    lastXSeconds = dataManager.QueryLastXMinutes(entry.getKey(), nowCal, numMinutes);
                    Toast.makeText(getApplicationContext(), lastXSeconds.toString(), Toast.LENGTH_LONG).show();
                    NameEditable.setText(lastXSeconds.toString());

//                    try {
////                        NameEditable.setText(lastXSeconds.toString());
//                        NameEditable.setText(lastXSeconds.getJSONObject(0).getString("heartRate"));
//
//                        data.addEntry(new Entry(set.getEntryCount(), (float) (lastXSeconds.getJSONObject(0).getInt("heartRate")) + 30f), 0);
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
            hrchart.notifyDataSetChanged();

            // limit the number of visible entries
            hrchart.setVisibleXRangeMaximum(120);
            // hrchart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            hrchart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // hrchart.moveViewTo(data.getXValCount()-7, 55f,
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
    public void onResume() {

        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

    private void setupSearchBar() {
        seachView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    seachView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    seachView.showProgress();
                    seachView.swapSuggestions(getNameSearchSuggestions(newQuery));
                    seachView.hideProgress();
                }
            }
        });
    }

    /***
     *
     * @param searchString is the user input in the search box
     * @return a List of Suggestions for the searchString
     */
    private List<NameSuggestion> getNameSearchSuggestions (String searchString) {

        Trie trie = new Trie();
        for (Soldier s : squad.getMonitoringSoildiersSoildiers()) {
            trie.insert(s.getName());
        }
        List<NameSuggestion> nameSuggestions = new ArrayList<NameSuggestion>();
        for(String name : trie.autoComplete(searchString)) {
            nameSuggestions.add(new NameSuggestion(name));
        }
        return nameSuggestions;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
