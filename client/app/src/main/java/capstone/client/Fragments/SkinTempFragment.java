package capstone.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import capstone.client.Activities.BottomBarActivity;
import capstone.client.BackgroundServices.BackgroundUIUpdator;
import capstone.client.DataManagement.DBManager;
import capstone.client.DataManagement.DataObserver;

public class SkinTempFragment extends capstone.client.BaseFragment implements DataObserver {
    private LineChart lineChart;
    private BottomBarActivity bottomBarActivity;
    private static float skinMin = 20;
    private static float skinMax = 40;

    public static SkinTempFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        SkinTempFragment fragment = new SkinTempFragment();
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
        View view = inflater.inflate(R.layout.fragment_skin_temp, container, false);
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
     * @param data is the skin tmp data received from the background. Currently its type is float array.
     */
    @Override
    public void update(Map data) {
        lineChart = (LineChart) getActivity().findViewById(R.id.skinTempChart);
        TextView st = (TextView) getActivity().findViewById(R.id.currentSkinTemp);
        float[] skinTemps = (float[]) data.get("skinTemp");
        String latestST = String.valueOf(skinTemps[0]);
        updateParam(latestST, st);

        List<Entry> entries = new ArrayList<Entry>();

        int arrLength = skinTemps.length;
        //X values need to be sorted or line chart throws exception
        //Do reversed order b/c showing last ten minutes (most recent data at 10)
        for (int i = 0; i < arrLength ; i++) {
            entries.add(new Entry(i, skinTemps[arrLength - 1 - i]));
        }
        ViewUtils.formatUpdateLineChart(getResources(),lineChart, entries, skinMin, skinMax);
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
