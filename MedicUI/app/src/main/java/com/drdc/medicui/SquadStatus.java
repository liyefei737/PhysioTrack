package com.drdc.medicui;

/**
 * Created by mehmetatmaca on 2017-02-11.
 */
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.widget.TextView;
public class SquadStatus extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_squad_status, container, false);
        return rootView;
    }
}
