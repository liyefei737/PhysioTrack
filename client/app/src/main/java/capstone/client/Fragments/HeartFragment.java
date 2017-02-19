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
import capstone.client.BackgroundServices.BackgroundUIUpdator;

import static android.R.attr.format;

public class HeartFragment extends capstone.client.BaseFragment implements DataObserver {
    private LineChart lineChart;
    private capstone.client.Activities.BottomBarActivity bottomBarActivity;
    private static float heartMin = 0;
    private static float heartMax = 200;

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
        BackgroundUIUpdator.updateDataAndBroadcast(new DBManager(getContext()), getContext());
        View view = inflater.inflate(R.layout.fragment_heart, container, false);
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
        updateParam(latestHR, hrText);

        List<Entry> entries = new ArrayList<Entry>();

        int arrLength = heartRates.length;
        for (int i = 0; i < arrLength ; i++) {
            entries.add(new Entry(i, heartRates[arrLength - 1 - i]));
        }

        ViewUtils.formatUpdateLineChart(lineChart, entries, heartMin, heartMax);
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
