package com.drdc1.medic;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NameList extends Fragment implements DataObserver {

    private SoldierListAdapter adapter;
    private HomeActivity homeActivity;
    private ArrayList<Soldier> soldiers = new ArrayList<>();

    public NameList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_name_list, container, false);
        homeActivity = (HomeActivity) getActivity();
        homeActivity.registerFragment(this);
        final ListView listView = (ListView) rootView.findViewById(R.id.soldierList);
        addListHeader(listView);
        DataManager dbManager = ((AppContext) getActivity().getApplication()).getDataManager();
        soldiers = dbManager.getActiveSoldier();
        adapter = new SoldierListAdapter(getContext(), soldiers);
        listView.setAdapter(adapter);

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
                //TODO When item clicked, write code here
                String soldierID = ((Soldier)listView.getAdapter().getItem(position)).getId();
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        homeActivity.registerFragment(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.registerFragment(this);
    }

    @Override
    public void onDestroyView() {
        homeActivity.unregisterFragment(this);
        super.onDestroyView();
    }

    private void setFragmentTransition(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id); // Put anything what you want

        IndividualSoldierTab fragment2 = new IndividualSoldierTab();
        fragment2.setArguments(bundle);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment2)
                .commit();

    }

    @Override
    public void update(Map data) {

        Boolean soldierInTheList = false;
        for (Soldier s : soldiers) {
            if (data.get("ID").equals(s.getId())) {
                s.setPhysioData(data);
                soldierInTheList = true;
            }
        }

        if (!soldierInTheList) {
            Soldier newSol = new Soldier((String) data.get("name"), (String) data.get("ID"));
            newSol.setPhysioData(data);
            soldiers.add(newSol);
        }
        adapter.notifyDataSetChanged();

    }

    private void addListHeader(ListView lv) {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.list_header, lv, false);
        lv.addHeaderView(headerView);
    }

    private class SoldierListAdapter extends ArrayAdapter<Soldier> {
        private final ArrayList<Soldier> soldiers;
        private String sortedBy = ""; // the soldier list is only sorted by 1 attribute at a time. don't overthink about stable sorting

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

            ImageView overall = (ImageView) v.findViewById(R.id.overal_status);
            switch (soldier.getOverallStatus()) {
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

            if (soldier.getBodyOrientation() != null && !soldier.getBodyOrientation().isEmpty()) {
                ImageView bodyPos = (ImageView) v.findViewById(R.id.body_position);
                bodyPos.setImageResource(R.drawable.body);
                switch (soldier.getBodyOrientation()) {
                    case "UP":
                        break;
                    case "DOWN":
                        bodyPos.setRotation(180);
                        break;
                    case "LEFT":
                        bodyPos.setRotation(270);
                        break;
                    case "RIGHT":
                        bodyPos.setRotation(90);
                        break;
                    default:
                        break;
                }
            }
            String currentHR = soldier.getHeartRate();
            String lastHR = soldier.getLastHeartRate();
            if (!currentHR.equals("")) {
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
            //TODO replace image for down_arrow color not right
            String currentBR = soldier.getBreathingRate();
            String lastBR = soldier.getLastBreathingRate();
            if (!currentBR.equals("")) {
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
            if (!currentCoreTmp.equals("")) {
                TextView coreTmp = (TextView) v.findViewById(R.id.core_tmp_text);
                ImageView arrow = (ImageView) v.findViewById(R.id.core_tmp_img);
                if (!lastCoreTmp.equals("")) {
                    if (Integer.parseInt(currentCoreTmp) > Integer.parseInt(lastCoreTmp)) {
                        arrow.setImageResource(R.drawable.up_arrow);
                        coreTmp.setTextColor(Color.RED);
                    } else if (Integer.parseInt(currentCoreTmp) < Integer.parseInt(lastCoreTmp)) {
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
            if (!currentSkinTmp.equals("")) {
                TextView skinTmp = (TextView) v.findViewById(R.id.skin_tmp_text);
                ImageView arrow = (ImageView) v.findViewById(R.id.skin_tmp_img);
                if (!lastSkinTmp.equals("")) {
                    if (Integer.parseInt(currentSkinTmp) > Integer.parseInt(lastSkinTmp)) {
                        arrow.setImageResource(R.drawable.up_arrow);
                        skinTmp.setTextColor(Color.RED);
                    } else if (Integer.parseInt(currentSkinTmp) < Integer.parseInt(lastSkinTmp)) {
                        arrow.setImageResource(R.drawable.down_arrow);
                        skinTmp.setTextColor(Color.YELLOW);
                    } else {
                        arrow.setImageResource(0);
                        skinTmp.setTextColor(Color.BLACK);
                    }
                }
                skinTmp.setText(currentSkinTmp);
            }
            //TextView tvHome = (TextView) v.findViewById(R.id.tvHome);
            // Populate the data into the template view using the data object
            // Return the completed view to render on screen
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
                    .compareTo(Integer.valueOf(s2.getBreathingRate().equals("") ? "0" : s2.getBreathingRate()));
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
