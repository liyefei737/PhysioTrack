package com.drdc.medicui;

/**
 * Created by mehmetatmaca on 2017-02-11.
 */
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;

public class HelpPage extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help_page, container, false);
        return rootView;
    }
}
