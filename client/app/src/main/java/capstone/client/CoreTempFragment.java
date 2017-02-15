package capstone.client;

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


public class CoreTempFragment extends BaseFragment implements DataObserver {
    private LineChart lineChart;
    private BottomBarActivity bottomBarActivity;

    public static CoreTempFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        CoreTempFragment fragment = new CoreTempFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate() {
        bottomBarActivity = (BottomBarActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bottomBarActivity.registerFragment(this);
        View view = inflater.inflate(R.layout.fragment_core_temp, container, false);
        TextView tv = (TextView) view.findViewById(R.id.currentCoreTemp);
        updateParam(((DRDCClient) bottomBarActivity.getApplication()).getLastCoreTemp(), tv);
        return view;
    }

    /***
     * @param data the update data from from the background
     */
    @Override
    public void update(Map data) {
        lineChart = (LineChart) getActivity().findViewById(R.id.coreTempChart);
        float[] skinTemps = (float[]) data.get("coreTemp");
        String latestCT = String.valueOf(skinTemps[skinTemps.length - 1]);
        bottomBarActivity.updateCoreFragment(latestCT);

        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < skinTemps.length; i++) {
            entries.add(new Entry(i, skinTemps[i]));
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
        yAxis.setAxisMinimum(30f); // start at zero
        yAxis.setAxisMaximum(45f); // the axis maximum is 100
        yAxis.setTextColor(Color.WHITE);
        //yAxis.setValueFormatter(new MyValueFormatter());
        //yAxis.setGranularity(1f); // interval 1
        yAxis.setLabelCount(5, true); // force 6 labels
        lineChart.setNoDataText("Loading...");
        lineChart.invalidate(); // refresh
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
