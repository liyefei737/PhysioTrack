package com.drdc1.medic.Fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.drdc1.medic.Activities.HomeActivity;
import com.drdc1.medic.AppContext;
import com.drdc1.medic.DataManagement.DataManager;
import com.drdc1.medic.ViewUtils.NameSuggestion;
import com.drdc1.medic.R;
import com.drdc1.medic.DataManagement.Soldier;
import com.drdc1.medic.ViewUtils.LineChartWithBackground;
import com.drdc1.medic.DataStructUtils.Trie;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import welfareSM.WelfareAlgoParams;

import static welfareSM.WelfareStatus.GREY;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualSoldierTab extends Fragment implements OnChartValueSelectedListener {
    private DataManager dataManager = null;
    TextView NameNonEditable, AgeNonEditable;
    private FloatingSearchView seachView;
    private String solid;
    private LineChartWithBackground hrchart, respchart, skinchart, ctchart;
    private HashMap<String, String> activeSoldierNameIDMap = new HashMap<>();
    Button btLinerRequest;

    private static float chartMin = 0;
    private static float chartMax = 200;
    View rootView;

    public IndividualSoldierTab() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate() {
//        bottomBarActivity = (capstone.client.Activities.BottomBarActivity) getActivity();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =
                inflater.inflate(R.layout.fragment_individual_soldier_tab, container, false);


        seachView = (FloatingSearchView) rootView.findViewById(R.id.searchBar);
        setupSearchBar();

        dataManager = ((AppContext) this.getActivity().getApplication()).getDataManager();

        super.onCreate(savedInstanceState);

        // Setup Button Links to new activity
        NameNonEditable = (TextView) rootView.findViewById(R.id.NameNonEditable);
        AgeNonEditable = (TextView) rootView.findViewById(R.id.AgeNonEditable);
        btLinerRequest = (Button) rootView.findViewById(R.id.btLinerRequest);
        btLinerRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((HomeActivity) getActivity()).onSelectIndividualSoldier(solid);
            }
        });

        hrchart = (LineChartWithBackground) rootView.findViewById(R.id.hrrchart);
        respchart =
                (LineChartWithBackground) rootView.findViewById(R.id.resprchart);
        skinchart =
                (LineChartWithBackground) rootView.findViewById(R.id.skinrchart);
        ctchart = (LineChartWithBackground) rootView.findViewById(R.id.ctrchart);

        // Inflate the layout for this fragment

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            update();

                        }
                    });

                } else {

                }

            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String IDpassed = ((HomeActivity) getActivity()).popSoldierId();
                            if (IDpassed != null) {
                                //TODO: handle code for id from namelist here.
                                solid = IDpassed;

                            }

                            HashMap hm = dataManager.getStaticInfo(solid);
                            NameNonEditable.setText((CharSequence) hm.get("name"));
                            AgeNonEditable.setText((CharSequence) hm.get("age"));

                        }
                    });
                }

            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
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
                    List<NameSuggestion> nameSearchSuggestions = getNameSearchSuggestions(newQuery);
                    seachView.swapSuggestions(nameSearchSuggestions);
                    seachView.hideProgress();
                }
            }
        });
        seachView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                //TODO when a user clicked the soldier from the search suggestions, write code here
                //prints the id of the soldier clicked
//                System.out.println(activeSoldierNameIDMap.get(searchSuggestion.getBody()));
                solid = activeSoldierNameIDMap.get(searchSuggestion.getBody());

            }

            @Override
            public void onSearchAction(String query) {
//                mLastQuery = query;
//
//                DataHelper.findColors(getActivity(), query,
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<ColorWrapper> results) {
//                                mSearchResultsAdapter.swapData(results);
//                            }
//
//                        });
//                Log.d(TAG, "onSearchAction()");
            }
        });
    }

    /***
     * @param searchString is the user input in the search box
     * @return a List of Suggestions for the searchString
     */
    private List<NameSuggestion> getNameSearchSuggestions(String searchString) {

        Trie trie = new Trie();
        for (Soldier s : dataManager.getActiveSoldiers()) {
            String solName = s.getName();
            String solID = s.getId();
            trie.insert(s.getName());
            activeSoldierNameIDMap.put(solName, solID);
        }
        List<NameSuggestion> nameSuggestions = new ArrayList<NameSuggestion>();
        for (String name : trie.autoComplete(searchString)) {
            nameSuggestions.add(new NameSuggestion(name));
        }
        return nameSuggestions;
    }

    //    /***
//     * @param data is the heart rate data received from the background. Currently its type is int array.
//     */
    public void update() {
        hrchart = (LineChartWithBackground) rootView.findViewById(R.id.hrrchart);
        respchart =
                (LineChartWithBackground) rootView.findViewById(R.id.resprchart);
        skinchart =
                (LineChartWithBackground) rootView.findViewById(R.id.skinrchart);
        ctchart = (LineChartWithBackground) rootView.findViewById(R.id.ctrchart);

        int num_data_pts = 10;
        float[] coreTemp = new float[num_data_pts];
        float[] skinTemp = new float[num_data_pts];
        int[] br = new int[num_data_pts];
        int[] hr = new int[num_data_pts];
        String state = GREY.toString();
        //hardcode for simdata

        Calendar date = new GregorianCalendar();
        date.set(2017, 02, 25);
        JSONArray last10Minutes = dataManager.QueryLastXMinutes(solid, date, 10);
        int last10MinutesArrLength = last10Minutes.length();
        if (last10MinutesArrLength != 0) {

            for (int i = 0; i < Math.min(10, last10MinutesArrLength); i++) {
                try {
                    JSONObject jsonRow = last10Minutes.getJSONObject(i);
                    br[i] = Integer.valueOf(jsonRow.getString("breathRate"));
                    hr[i] = Integer.valueOf(jsonRow.getString("heartRate"));
                    coreTemp[i] = Float.valueOf(jsonRow.getString("coreTemp"));
                    skinTemp[i] = Float.valueOf(jsonRow.getString("skinTemp"));
                } catch (Exception e) {
                    com.couchbase.lite.util.Log.e("UIUpdator", String.format(" index %d", i));
                }
            }
        }

        String latestHR = String.valueOf(hr[0]);
//        TextView hrText = (TextView) getActivity().findViewById(R.id.currentHeartRate);
//        updateParam(latestHR, hrText);

        List<Entry> entries = new ArrayList<Entry>();
        List<Entry> entries2 = new ArrayList<Entry>();
        List<Entry> entries3 = new ArrayList<Entry>();
        List<Entry> entries4 = new ArrayList<Entry>();

        int arrLength = hr.length;
        for (int i = 0; i < arrLength; i++) {
            entries.add(new Entry(i, hr[arrLength - 1 - i]));
            entries2.add(new Entry(i, br[arrLength - 1 - i]));
            entries3.add(new Entry(i, skinTemp[arrLength - 1 - i]));
            entries4.add(new Entry(i, coreTemp[arrLength - 1 - i]));
        }

        Resources res = getResources();
        WelfareAlgoParams wap = dataManager.get_welfareAlgoParams();
        hrchart.formatUpdateLineChart(res, entries, 0, 200, wap.getHrRangeObj());
        respchart.formatUpdateLineChart(res, entries, 0, 100, wap.getBrRangeObj());
        skinchart.formatUpdateLineChart(res, entries, 25, 100, wap.getStRangeObj());
        ctchart.formatUpdateLineChart(res, entries, 25, 100, wap.getCtRangeObj());

    }

    @Override
    public void onDestroyView() {
//        bottomBarActivity.unregisterFragment(this);
        super.onDestroyView();
    }

    public void updateParam(String param, TextView hr) {
        hr.setText(param);
        hr.refreshDrawableState();
    }

}

/***
 * TODO SearchBar and Invidual soldier page
 * 1. when a user select a soldier, show/populates the fields i.e. Name, body orentation graphs
 * 2. graphes need to be updatable
 * 3. a better UI for the searchbar
 */