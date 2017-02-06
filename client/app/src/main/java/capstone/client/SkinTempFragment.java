package capstone.client;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SkinTempFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SkinTempFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SkinTempFragment extends BaseFragment implements DataObserver {
    private LineChart lineChart;

    public static SkinTempFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        SkinTempFragment fragment = new SkinTempFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_skin_temp, container, false);
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
        lineChart = (LineChart) getActivity().findViewById(R.id.skinTempChart);
        float[] skinTemps= (float[]) data.get("skinTemp");

        List<Entry> entries = new ArrayList<Entry>();

        for(int i = 0; i< skinTemps.length; i++){
            entries.add(new Entry(i, skinTemps[i]));
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
        yAxis.setAxisMinimum(30f); // start at zero
        yAxis.setAxisMaximum(45f); // the axis maximum is 100
        yAxis.setTextColor(Color.WHITE);
        //yAxis.setValueFormatter(new MyValueFormatter());
        //yAxis.setGranularity(1f); // interval 1
        yAxis.setLabelCount(5, true); // force 6 labels
        lineChart.setNoDataText("Loading...");
        lineChart.invalidate(); // refresh


    }
}
