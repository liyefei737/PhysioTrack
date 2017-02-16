package capstone.client;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capstone.client.Activities.BottomBarActivity;

public class HeartFragment extends capstone.client.BaseFragment implements DataObserver {
    private LineChart lineChart;
    private capstone.client.Activities.BottomBarActivity bottomBarActivity;

    public static HeartFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        HeartFragment fragment = new HeartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate() {
        bottomBarActivity = (capstone.client.Activities.BottomBarActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bottomBarActivity.registerFragment(this);
        View view = inflater.inflate(R.layout.fragment_heart, container, false);
        TextView tv = (TextView) view.findViewById(R.id.currentHeartRate);
        updateParam(((DRDCClient) bottomBarActivity.getApplication()).getLastHeartRate(), tv);
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        bottomBarActivity.unregisterFragment(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bottomBarActivity.registerFragment(this);
    }

    /***
     * @param data is the heart rate data received from the background. Currently its type is int array.
     */
    @Override
    public void update(Map data) {
        lineChart = (LineChart) getActivity().findViewById(R.id.heartChart);
        int[] heartRates = (int[]) data.get("hr");
        String latestHR = String.valueOf(heartRates[0]);
        TextView hrText = (TextView) getActivity().findViewById(R.id.currentHeartRate);
        bottomBarActivity.updateHeartFragment(latestHR);
        updateParam(latestHR, hrText);

        List<Entry> entries = new ArrayList<Entry>();

        int arrLength = heartRates.length;
        for (int i = 0; i < arrLength ; i++) {
            entries.add(new Entry(i, heartRates[arrLength - 1 - i]));
        }

        //the following code is playing around with the graph for heart rate, feel free to play around
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        //dataSet.setHighlightEnabled(true);
        //dataSet.setColor();
        //dataSet.setValueTextColor(...); // styling, ...
        LineData lineData = new LineData(dataSet);
        //dataSet.setDrawFilled(true);
        //dataSet.setFillDrawable(gradientDrawable);
        lineChart.setData(lineData);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        YAxis yAxis = lineChart.getAxisLeft();
        //yAxis.setTypeface(...);
        yAxis.setTextSize(14f); // set the text size
        yAxis.setAxisMinimum(0f); // start at zero
        yAxis.setAxisMaximum(200f); // the axis maximum is 100
        yAxis.setTextColor(Color.WHITE);
        //yAxis.setValueFormatter(new MyValueFormatter());
        //yAxis.setGranularity(1f); // interval 1
        yAxis.setLabelCount(5, true); // force 6 labels
        lineChart.setNoDataText("Loading...");
        lineChart.refreshDrawableState();
        lineChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        bottomBarActivity.unregisterFragment(this);
        super.onDestroyView();
    }

    public void updateParam(String param, TextView hr) {
        hr.setText(param);
        hr.refreshDrawableState();
    }
}
