package capstone.client;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BreathFragment extends BaseFragment implements DataObserver {
    private LineChart lineChart;

    public static BreathFragment  newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        BreathFragment fragment = new BreathFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_breath, container, false);
        FragmentDataManager fm = (FragmentDataManager)getActivity();
        fm.registerFragment(this);
        return inflated;
    }


    @Override
    public void onDestroyView() {
        FragmentDataManager fm = (FragmentDataManager)getActivity();
        fm.unregisterFragment(this);
        super.onDestroyView();
    }

    /***
     *
     * @param data the update data from from the background
     */
    @Override
    public void update(Map data) {
        lineChart = (LineChart) getActivity().findViewById(R.id.breathChart);
        int[] breathRates= (int[]) data.get("br");

        List<Entry> entries = new ArrayList<Entry>();

        for(int i = 0; i< breathRates.length; i++){
            entries.add(new Entry(i, breathRates[i]));
        }
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
        yAxis.setAxisMinimum(10f); // start at zero
        yAxis.setAxisMaximum(20f); // the axis maximum is 100
        yAxis.setTextColor(Color.WHITE);
        //yAxis.setValueFormatter(new MyValueFormatter());
        //yAxis.setGranularity(1f); // interval 1
        yAxis.setLabelCount(5, true); // force 6 labels
        lineChart.setNoDataText("Loading...");
        lineChart.invalidate(); // refresh


    }
}
