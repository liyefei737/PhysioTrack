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


public class BreathFragment extends capstone.client.BaseFragment implements DataObserver {
    private LineChart lineChart;
    private BottomBarActivity bottomBarActivity;
    private static float breathMin = 0;
    private static float breathMax = 70;
    public static BreathFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        BreathFragment fragment = new BreathFragment();
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
        View view = inflater.inflate(R.layout.fragment_breath, container, false);
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
     * @param data is the breath rate data received from the background. Currently its type is int array.
     */
    @Override
    public void update(Map data) {
        lineChart = (LineChart) getActivity().findViewById(R.id.breathChart);
        TextView brNum = (TextView) getActivity().findViewById(R.id.currentBreathRate);
        int[] breathRates = (int[]) data.get("br");
        String latestBR = String.valueOf(breathRates[0]);
        updateParam(latestBR, brNum);

        List<Entry> entries = new ArrayList<Entry>();

        int arrLength = breathRates.length;
        //X values need to be sorted or line chart throws exception
        //Do reversed order b/c showing last ten minutes (most recent data at 10)
        for (int i = 0; i < arrLength ; i++) {
            entries.add(new Entry(i, breathRates[arrLength - 1 - i]));
        }

        ViewUtils.formatUpdateLineChart(getResources(),lineChart, entries, breathMin, breathMax);
    }

    @Override
    public void onDestroyView() {
        bottomBarActivity.unregisterFragment(this);
        super.onDestroyView();
    }

    public void updateParam(String param, TextView br) {
        br.setText(param);
        br.refreshDrawableState();
    }
}
