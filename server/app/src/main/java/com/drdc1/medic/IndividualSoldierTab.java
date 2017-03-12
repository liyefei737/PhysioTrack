package com.drdc1.medic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.drdc1.medic.utils.Trie;
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

import static welfareSM.WelfareStatus.GREY;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualSoldierTab extends Fragment implements OnChartValueSelectedListener {
    private DataManager dataManager = null;
    TextView NameNonEditable, GenderNonEditable, AgeNonEditable;
    private FloatingSearchView seachView;
    private String solid = "fjffy";
    private com.drdc1.medic.LineChartWithBackground hrchart, respchart, skinchart, ctchart;
    private HashMap<String, String> activeSoldierNameIDMap = new HashMap<>();
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

//        Bundle bundle = this.getArguments();
//        if(bundle != null){
//            String IDpassed = bundle.getString("id");
//            // handle code for id from namelist here.
//
//        }

        seachView = (FloatingSearchView) rootView.findViewById(R.id.searchBar);
        setupSearchBar();

        dataManager = ((AppContext) this.getActivity().getApplication()).getDataManager();

        super.onCreate(savedInstanceState);

        // Setup Button Links to new activity
        NameNonEditable = (TextView) rootView.findViewById(R.id.NameNonEditable);
        GenderNonEditable = (TextView) rootView.findViewById(R.id.GenderNonEditable);
        AgeNonEditable = (TextView) rootView.findViewById(R.id.AgeNonEditable);

        //End of OnClick Links
//            NameEditable.setText("thistest");
//            GenderEditable.setText("thistest1");
//            AgeEditable.setText("thistest2");

        hrchart = (com.drdc1.medic.LineChartWithBackground) rootView.findViewById(R.id.hrrchart);
        respchart =
                (com.drdc1.medic.LineChartWithBackground) rootView.findViewById(R.id.resprchart);
        skinchart =
                (com.drdc1.medic.LineChartWithBackground) rootView.findViewById(R.id.skinrchart);
        ctchart = (com.drdc1.medic.LineChartWithBackground) rootView.findViewById(R.id.ctrchart);

        // Inflate the layout for this fragment

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        update();

                    }
                });

            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        HashMap hm = dataManager.getStaticInfo(solid);
                        NameNonEditable.setText((CharSequence) hm.get("name"));
                        GenderNonEditable.setText((CharSequence) hm.get("gender"));
                        AgeNonEditable.setText((CharSequence) hm.get("age"));

                    }
                });

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
        for (Soldier s : dataManager.getActiveSoldier()) {
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
        hrchart = (com.drdc1.medic.LineChartWithBackground) rootView.findViewById(R.id.hrrchart);
        respchart =
                (com.drdc1.medic.LineChartWithBackground) rootView.findViewById(R.id.resprchart);
        skinchart =
                (com.drdc1.medic.LineChartWithBackground) rootView.findViewById(R.id.skinrchart);
        ctchart = (com.drdc1.medic.LineChartWithBackground) rootView.findViewById(R.id.ctrchart);

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
//            entries.add(new Entry(i, 60f + (float) (Math.random() * ((60 - 40) + 1))));
            entries.add(new Entry(i, hr[arrLength - 1 - i]));
        }
        for (int i = 0; i < arrLength; i++) {
//            entries2.add(new Entry(i, 40f + (float) (Math.random() * ((40 - 30) + 1))));
            entries2.add(new Entry(i, br[arrLength - 1 - i]));
        }
        for (int i = 0; i < arrLength; i++) {
//            entries3.add(new Entry(i, 70f + (float) (Math.random() * ((70 - 0) + 1))));
            entries3.add(new Entry(i, skinTemp[arrLength - 1 - i]));
        }
        for (int i = 0; i < arrLength; i++) {
//            entries4.add(new Entry(i, 30f + (float) (Math.random() * ((30 - 10) + 1))));
            entries4.add(new Entry(i, coreTemp[arrLength - 1 - i]));
        }
        float[] zoneLimits = {60, 40, 5, 1};
        com.drdc1.medic.LineChartWithBackground
                .formatUpdateLineChart(getResources(), hrchart, entries, chartMin, chartMax,
                        zoneLimits);
        com.drdc1.medic.LineChartWithBackground
                .formatUpdateLineChart(getResources(), respchart, entries2, chartMin, chartMax,
                        zoneLimits);
        com.drdc1.medic.LineChartWithBackground
                .formatUpdateLineChart(getResources(), skinchart, entries3, chartMin, chartMax,
                        zoneLimits);
        com.drdc1.medic.LineChartWithBackground
                .formatUpdateLineChart(getResources(), ctchart, entries4, chartMin, chartMax,
                        zoneLimits);
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
 * 1. when a user select a soldier, show/populates the fields i.e. Name, gender, body orentation graphs
 * 2. graphes need to be updatable
 * 3. a better UI for the searchbar
 */