package capstone.client.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capstone.client.BackgroundServices.BackgroundUIUpdator;
import capstone.client.DRDCClient;
import capstone.client.DataManagement.DBManager;
import capstone.client.DataManagement.DataObserver;
import capstone.client.R;
import capstone.client.ViewTools.LineChartWithBackground;

public class HeartFragment extends capstone.client.Fragments.BaseFragment implements DataObserver {
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
        BackgroundUIUpdator.updateDataAndBroadcast(new DBManager(getContext()), getContext());
        return inflater.inflate(R.layout.fragment_heart, container, false);
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
        LineChartWithBackground lineChart = (LineChartWithBackground) getActivity().findViewById(R.id.heartChart);
        int[] heartRates = (int[]) data.get("hr");
        String latestHR = String.valueOf(heartRates[0]);
        TextView hrText = (TextView) getActivity().findViewById(R.id.currentHeartRate);
        updateParam(latestHR, hrText);

        List<Entry> entries = new ArrayList<>();

        int arrLength = heartRates.length;
        for (int i = 0; i < arrLength ; i++) {
            entries.add(new Entry(i, heartRates[arrLength - 1 - i]));
        }

        List<Object> zoneLimits = ((DRDCClient) getActivity().getApplication()).getWelfareTracker().getWAP().getHrRangeObj();
        float heartMax = 200;
        float heartMin = 0;
        lineChart.formatUpdateLineChart(getResources(), entries, heartMin, heartMax, zoneLimits);
        lineChart.postInvalidate();
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
