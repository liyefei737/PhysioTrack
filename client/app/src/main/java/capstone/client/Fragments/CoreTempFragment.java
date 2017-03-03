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

import capstone.client.Activities.BottomBarActivity;
import capstone.client.BackgroundServices.BackgroundUIUpdator;
import capstone.client.DataManagement.DBManager;
import capstone.client.DataManagement.DataObserver;
import capstone.client.R;
import capstone.client.ViewTools.LineChartWithBackground;


public class CoreTempFragment extends capstone.client.Fragments.BaseFragment implements DataObserver {
    private LineChartWithBackground lineChart;
    private BottomBarActivity bottomBarActivity;
    private static float coreMin = 30;
    private static float coreMax = 40;

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
        BackgroundUIUpdator.updateDataAndBroadcast(new DBManager(getContext()), getContext());
        View view = inflater.inflate(R.layout.fragment_core_temp, container, false);
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
     * @param data is the core tmp data received from the background. Currently its type is float array.
     */
    @Override
    public void update(Map data) {
        lineChart = (LineChartWithBackground) getActivity().findViewById(R.id.coreTempChart);
        TextView ctNum = (TextView) getActivity().findViewById(R.id.currentCoreTemp);
        float[] coreTemps = (float[]) data.get("coreTemp");
        String latestCT = String.valueOf(coreTemps[0]);
        updateParam(latestCT, ctNum);

        List<Entry> entries = new ArrayList<Entry>();

        int arrLength = coreTemps.length;
        //X values need to be sorted or line chart throws exception
        //Do reversed order b/c showing last ten minutes (most recent data at 10)
        for (int i = 0; i < arrLength ; i++) {
            entries.add(new Entry(i, coreTemps[arrLength - 1 - i]));
        }
        float [] zoneLimits = {60, 40, 5, 1};
        LineChartWithBackground.formatUpdateLineChart(getResources(), lineChart, entries, coreMin, coreMax, zoneLimits);
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
