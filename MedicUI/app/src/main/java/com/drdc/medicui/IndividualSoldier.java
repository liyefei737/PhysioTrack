package com.drdc.medicui;

/**
 * Created by mehmetatmaca on 2017-02-11.
 */

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.TextView;

public class IndividualSoldier  extends Fragment {

    private static ImageButton imageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_individual_soldier, container, false);
        return rootView;
    }
}