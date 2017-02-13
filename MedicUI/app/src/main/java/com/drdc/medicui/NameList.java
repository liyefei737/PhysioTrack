package com.drdc.medicui;

/**
 * Created by mehmetatmaca on 2017-02-11.
 */

import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static com.drdc.medicui.R.layout.fragment_name_list;

public class NameList extends Fragment {

    String[] mobileArray = {"soldier1", "soldier2", "soldier3", "soldier4",
            "soldier5", "soldier6", "soldier7", "soldier8"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ArrayAdapter adapter = new ArrayAdapter<String>(fragment_name_list.this,
//                fragment_name_list, mobileArray);
//

        View rootView = inflater.inflate(fragment_name_list, container, false);
        return rootView;
    }
}
