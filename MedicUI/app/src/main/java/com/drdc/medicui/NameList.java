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

public class NameList extends Fragment {

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_name_list, container, false);
        return rootView;
    }
}
