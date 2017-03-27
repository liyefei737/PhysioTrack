package com.drdc1.medic.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.drdc1.medic.Activities.HomeActivity;
import com.drdc1.medic.AppContext;
import com.drdc1.medic.BackgroundServices.Server;
import com.drdc1.medic.DataManagement.DataManager;
import com.drdc1.medic.DataManagement.DataObserver;
import com.drdc1.medic.DataManagement.DataSleepObserver;
import com.drdc1.medic.DataManagement.DataStatusObserver;
import com.drdc1.medic.DataManagement.Soldier;
import com.drdc1.medic.DataManagement.SoldierList;
import com.drdc1.medic.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class NameList extends Fragment implements DataObserver, DataStatusObserver, DataSleepObserver {

    private SoldierListAdapter adapter;
    private HomeActivity homeActivity;
    private SoldierList soldiers = new SoldierList();

    public NameList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_name_list, container, false);
        homeActivity = (HomeActivity) getActivity();
        homeActivity.registerFragment(this);
        homeActivity.registerStatusWithIDFragment(this);
        homeActivity.registerSleepFragment(this);
        final ListView listView = (ListView) rootView.findViewById(R.id.soldierList);
        addListHeader(listView);
        DataManager dbManager = ((AppContext) getActivity().getApplication()).getDataManager();
        soldiers.setSoldiers(dbManager.getActiveSoldiers());
        adapter = new SoldierListAdapter(getContext(), soldiers.getSoldiers());
        listView.setAdapter(adapter);
        TextView ip = (TextView) rootView.findViewById(R.id.ip);
        ip.setText("IP: " + Server.getLocalIpAddress());
        TextView nameHeader = (TextView) rootView.findViewById(R.id.name_header);
        nameHeader.setOnClickListener(new ClickToSort());
        TextView hrHeader = (TextView) rootView.findViewById(R.id.hr_header);
        hrHeader.setOnClickListener(new ClickToSort());
        TextView brHeader = (TextView) rootView.findViewById(R.id.br_header);
        brHeader.setOnClickListener(new ClickToSort());
        TextView skinTmpHeader = (TextView) rootView.findViewById(R.id.skin_tmp_header);
        skinTmpHeader.setOnClickListener(new ClickToSort());
        TextView core_tmp_header = (TextView) rootView.findViewById(R.id.core_tmp_header);
        core_tmp_header.setOnClickListener(new ClickToSort());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    ((HomeActivity) getActivity())
                            .onSelectSoldierByName(adapter.getItem(position - 1).getId());
                }
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        homeActivity.registerFragment(this);
        homeActivity.registerStatusWithIDFragment(this);
        homeActivity.registerSleepFragment(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.registerFragment(this);
        homeActivity.registerStatusWithIDFragment(this);
        homeActivity.registerSleepFragment(this);
    }

    @Override
    public void onDestroyView() {
        homeActivity.unregisterFragment(this);
        homeActivity.unregisterStatusWithIDFragment(this);
        homeActivity.unregisterSleepFragment(this);
        super.onDestroyView();
    }

    @Override
    public void update(Map data) {

        Soldier s = soldiers.getSoldierByID((String) data.get("ID"));
        if (s != null)
            s.setPhysioData(data);
        else {
            Soldier newSol = new Soldier((String) data.get("name"), (String) data.get("ID"));
            newSol.setPhysioData(data);
            soldiers.addSoldier(newSol);
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void updateStatus(Map<String, String> data) {

        for (Map.Entry<String, String> entry : data.entrySet()) {
            Soldier s = soldiers.getSoldierByID(entry.getKey());
            if (s != null) {
                s.setCurrentStatus(entry.getValue());
            }
        }

        adapter.notifyDataSetChanged();

    }

    @Override
    public void updateSleep(Map<String, Integer> data) {

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            Soldier s = soldiers.getSoldierByID(entry.getKey());
            if (s != null) {
                int percent = entry.getValue();
                s.setFatigue(String.format("%d", percent));
            }
        }

        adapter.notifyDataSetChanged();

    }

    private void addListHeader(ListView lv) {
        View headerView =
                LayoutInflater.from(getContext()).inflate(R.layout.list_header, lv, false);
        lv.addHeaderView(headerView);
    }

    private class SoldierListAdapter extends ArrayAdapter<Soldier> {
        private final ArrayList<Soldier> soldiers;
        private String sortedBy = "";
        // the soldier list is only sorted by 1 attribute at a time. don't overthink about stable sorting

        public SoldierListAdapter(Context context, ArrayList<Soldier> soldiers) {
            super(context, R.layout.list_item, soldiers);
            this.soldiers = soldiers;
        }

        public String getSortedBy() {
            return sortedBy;
        }

        /***
         * When the user click the header when soldiers are already sorted, reverse the order
         */
        public void reverse() {
            Collections.reverse(soldiers);
        }

        /***
         * @param x "Sort by x" Sortby is an enum
         */
        public void sort(SortBy x) {
            switch (x) {
                case NAME:
                    this.sort(new OrderByName());
                    sortedBy = "NAME";
                    break;
                case HR:
                    this.sort(new OrderByHR());
                    sortedBy = "HR";
                    break;
                case BR:
                    this.sort(new OrderByBR());
                    sortedBy = "BR";
                    break;
                case SKINTMP:
                    this.sort(new OrderBySkinTmp());
                    sortedBy = "SKINTMP";
                    break;
                case CORETMP:
                    this.sort(new OrderByCoreTmp());
                    sortedBy = "CORETMP";
                    break;
                default:

            }
        }

        @NonNull
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            // Get the data item for this position
            Soldier soldier = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (v == null) {
                v = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item, parent, false);
            }
            TextView name = (TextView) v.findViewById(R.id.name);
            name.setText(soldier.getName());
            if(soldier.getPhysioData() == null) {
                name.setTextColor(Color.parseColor("grey"));
                return v;
            }
            name.setTextColor(Color.parseColor("black"));
            ImageView overall = (ImageView) v.findViewById(R.id.overal_status);
            String status = soldier.getCurrentStatus();
            if (status != null && (!status.isEmpty())) {
                switch (status) {
                    case "RED":
                        overall.setImageResource(R.drawable.red);
                        break;
                    case "GREEN":
                        overall.setImageResource(R.drawable.green);
                        break;
                    case "YELLOW":
                        overall.setImageResource(R.drawable.yellow);
                        break;
                    default:
                        break;
                }
            }
            Set<String> legalBodyPos = new HashSet<String>(Arrays.asList("UPRIGHT", "SUPINE", "PRONE", "SIDE"));
            if (soldier.getBodyOrientation() != null && legalBodyPos.contains(soldier.getBodyOrientation())) {
                ImageView bodyPos = (ImageView) v.findViewById(R.id.body_position);
                bodyPos.setImageResource(R.drawable.body);
                if (!soldier.getBodyOrientation().equals("UPRIGHT")) {
                    bodyPos.setRotation(90);
                }
            }
            String currentHR = soldier.getHeartRate();
            String lastHR = soldier.getLastHeartRate();
            if (currentHR != null && !currentHR.equals("")) {
                TextView heartRate = (TextView) v.findViewById(R.id.hr_text);
                ImageView arrow = (ImageView) v.findViewById(R.id.hr_img);
                if (!lastHR.equals("")) {
                    if (Integer.parseInt(currentHR) > Integer.parseInt(lastHR)) {
                        arrow.setImageResource(R.drawable.up_arrow);
                        heartRate.setTextColor(Color.RED);
                    } else if (Integer.parseInt(currentHR) < Integer.parseInt(lastHR)) {
                        arrow.setImageResource(R.drawable.down_arrow);
                        heartRate.setTextColor(Color.YELLOW);
                    } else {
                        arrow.setImageResource(0);
                        heartRate.setTextColor(Color.BLACK);
                    }
                }
                heartRate.setText(currentHR);
            }
            String currentBR = soldier.getBreathingRate();
            String lastBR = soldier.getLastBreathingRate();
            if (currentBR != null && !currentBR.equals("")) {
                TextView breathingRate = (TextView) v.findViewById(R.id.br_text);
                ImageView arrow = (ImageView) v.findViewById(R.id.br_img);
                if (!lastBR.equals("")) {
                    if (Integer.parseInt(currentBR) > Integer.parseInt(lastBR)) {
                        arrow.setImageResource(R.drawable.up_arrow);
                        breathingRate.setTextColor(Color.RED);
                    } else if (Integer.parseInt(currentBR) < Integer.parseInt(lastBR)) {
                        arrow.setImageResource(R.drawable.down_arrow);
                        breathingRate.setTextColor(Color.YELLOW);
                    } else {
                        arrow.setImageResource(0);
                        breathingRate.setTextColor(Color.BLACK);
                    }
                }
                breathingRate.setText(currentBR);
            }

            String currentCoreTmp = soldier.getCoreTmp();
            String lastCoreTmp = soldier.getLastCoreTmp();
            if (currentCoreTmp != null && !currentCoreTmp.equals("")) {
                TextView coreTmp = (TextView) v.findViewById(R.id.core_tmp_text);
                ImageView arrow = (ImageView) v.findViewById(R.id.core_tmp_img);
                if (!lastCoreTmp.equals("")) {
                    if (Float.parseFloat(currentCoreTmp) > Float.parseFloat(lastCoreTmp)) {
                        arrow.setImageResource(R.drawable.up_arrow);
                        coreTmp.setTextColor(Color.RED);
                    } else if (Float.parseFloat(currentCoreTmp) < Float.parseFloat(lastCoreTmp)) {
                        arrow.setImageResource(R.drawable.down_arrow);
                        coreTmp.setTextColor(Color.YELLOW);
                    } else {
                        arrow.setImageResource(0);
                        coreTmp.setTextColor(Color.BLACK);
                    }
                }
                coreTmp.setText(currentCoreTmp);
            }

            String currentSkinTmp = soldier.getSkinTmp();
            String lastSkinTmp = soldier.getLastSkinTmp();
            if (currentSkinTmp != null && !currentSkinTmp.equals("")) {
                TextView skinTmp = (TextView) v.findViewById(R.id.skin_tmp_text);
                ImageView arrow = (ImageView) v.findViewById(R.id.skin_tmp_img);
                if (!lastSkinTmp.equals("")) {
                    if (Float.parseFloat(currentSkinTmp) > Float.parseFloat(lastSkinTmp)) {
                        arrow.setImageResource(R.drawable.up_arrow);
                        skinTmp.setTextColor(Color.RED);
                    } else if (Float.parseFloat(currentSkinTmp) < Float.parseFloat(lastSkinTmp)) {
                        arrow.setImageResource(R.drawable.down_arrow);
                        skinTmp.setTextColor(Color.YELLOW);
                    } else {
                        arrow.setImageResource(0);
                        skinTmp.setTextColor(Color.BLACK);
                    }
                }
                skinTmp.setText(currentSkinTmp);
            }


            String currentFatigue = soldier.getFatigue();
            if (currentFatigue != null && !currentFatigue.equals("")) {
                TextView fatigue = (TextView) v.findViewById(R.id.fatigue_level);

                fatigue.setText(currentFatigue + "%");
            }

            return v;
        }
    }

    private class ClickToSort implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String sortBy = (String) ((TextView) v).getText();
            switch (sortBy.toLowerCase()) {
                case "name":
                    if (adapter.getSortedBy().equals("NAME")) {
                        adapter.reverse();
                    } else {
                        adapter.sort(SortBy.NAME);
                    }
                    break;
                case "hr":
                    if (adapter.getSortedBy().equals("HR")) {
                        adapter.reverse();
                    } else {
                        adapter.sort(SortBy.HR);
                    }
                    break;
                case "br":
                    if (adapter.getSortedBy().equals("BR")) {
                        adapter.reverse();
                    } else {
                        adapter.sort(SortBy.BR);
                    }
                    break;
                case "skintmp":
                    if (adapter.getSortedBy().equals("SKINTMP")) {
                        adapter.reverse();
                    } else {
                        adapter.sort(SortBy.SKINTMP);
                    }
                    break;
                case "coretmp":
                    if (adapter.getSortedBy().equals("CORETMP")) {
                        adapter.reverse();
                    } else {
                        adapter.sort(SortBy.CORETMP);
                    }
                    break;
                default:

            }
            adapter.notifyDataSetChanged();
        }
    }

    /************************************************************************
     * Comparator classes to support different ordering of soldiers
     *************************************************************************/
    private enum SortBy {
        NAME, HR, BR, SKINTMP, CORETMP
    }

    private static class OrderByName implements Comparator<Soldier> {
        @Override
        public int compare(Soldier s1, Soldier s2) {
            return s1.getName().compareTo(s2.getName());
        }
    }

    private static class OrderByHR implements Comparator<Soldier> {
        @Override
        public int compare(Soldier s1, Soldier s2) {
            return Integer.parseInt(s1.getHeartRate().equals("") ? "0" : s1.getHeartRate())
                    - Integer.parseInt(s2.getHeartRate().equals("") ? "0" : s2.getHeartRate());
        }
    }

    private static class OrderByBR implements Comparator<Soldier> {
        @Override
        public int compare(Soldier s1, Soldier s2) {
            return Integer.valueOf(s1.getBreathingRate().equals("") ? "0" : s1.getBreathingRate())
                    .compareTo(Integer.valueOf(
                            s2.getBreathingRate().equals("") ? "0" : s2.getBreathingRate()));
        }
    }

    private static class OrderBySkinTmp implements Comparator<Soldier> {
        @Override
        public int compare(Soldier s1, Soldier s2) {
            return Integer.valueOf(s1.getSkinTmp().equals("") ? "0" : s1.getSkinTmp())
                    .compareTo(Integer.valueOf(s2.getSkinTmp().equals("") ? "0" : s2.getSkinTmp()));
        }
    }

    private static class OrderByCoreTmp implements Comparator<Soldier> {
        @Override
        public int compare(Soldier s1, Soldier s2) {
            return Integer.valueOf(s1.getCoreTmp().equals("") ? "0" : s1.getCoreTmp())
                    .compareTo(Integer.valueOf(s2.getCoreTmp().equals("") ? "0" : s2.getCoreTmp()));
        }
    }
}
