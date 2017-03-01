package com.drdc1.medic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class NameList extends Fragment {

    private SoldierListAdapter adapter;

    public NameList() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_name_list, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.soldierList);
        // Construct the data source
        ArrayList<Soldier> soldiers = (ArrayList<Soldier>) Squad.getInstance().getMonitoringSoildiersSoildiers();
        adapter = new SoldierListAdapter(getContext(), soldiers);
        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
        return rootView;
    }
    private class SoldierListAdapter extends ArrayAdapter<Soldier> {

        public SoldierListAdapter(Context context, ArrayList<Soldier> soldiers) {
            super(context, R.layout.list_item_soldier,soldiers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Soldier soldier = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_soldier, parent, false);
            }
            // Lookup view for data population
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
            // Populate the data into the template view using the data object
            name.setText(soldier.getName());
            tvHome.setText(soldier.getGender());
            // Return the completed view to render on screen
            return convertView;
        }
    }
    /************************************************************************
     * Comparator classes to support different ordering of soldiers
     *************************************************************************/
    private static class OrderByName implements Comparator<Soldier> {
        @Override
        public int compare(Soldier s1, Soldier s2) {
            return s1.getName().compareTo(s2.getName());
        }
    }

}
