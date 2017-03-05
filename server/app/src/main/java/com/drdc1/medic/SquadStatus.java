package com.drdc1.medic;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import welfareSM.WelfareStatus;

import static welfareSM.WelfareStatus.GREEN;
import static welfareSM.WelfareStatus.RED;
import static welfareSM.WelfareStatus.YELLOW;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SquadStatus.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SquadStatus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SquadStatus extends Fragment {
    private DataManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dbManager = ((AppContext) getActivity().getApplication()).getDataManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int numSoldiers =10;//= dbManager.getNumSoldiers();
        View view = inflater.inflate(R.layout.fragment_squad_status, container, false);
        RelativeLayout relLayoutOverall = (RelativeLayout) view.findViewById(R.id.bullsEyeOverallHealth);
        RelativeLayout relLayoutCore = (RelativeLayout) view.findViewById(R.id.bullsEyeCoreTemp);
        RelativeLayout relLayoutFatigue = (RelativeLayout) view.findViewById(R.id.bullsEyeFatigue);
        RelativeLayout relLayoutSkin = (RelativeLayout) view.findViewById(R.id.bullsEyeSkinTemp);
        if (numSoldiers != 0) {
            WelfareStatus[] statusArray = {RED, GREEN, YELLOW, YELLOW, GREEN, RED, YELLOW,RED,YELLOW,GREEN};
            Resources resources = getActivity().getResources();
            BullsEye.drawBullsEye(resources, relLayoutOverall, numSoldiers, Arrays.asList(statusArray));
//            BullsEye.drawBullsEye(resources, relLayoutCore, numSoldiers, Arrays.asList(statusArray));
//            BullsEye.drawBullsEye(resources, relLayoutFatigue, numSoldiers, Arrays.asList(statusArray));
//            BullsEye.drawBullsEye(resources, relLayoutSkin, numSoldiers, Arrays.asList(statusArray));
        }
        return view;
    }



}
