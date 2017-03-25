package com.drdc1.medic.Fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.drdc1.medic.Activities.HomeActivity;
import com.drdc1.medic.AppContext;
import com.drdc1.medic.BackgroundServices.BackgroundWellnessAlgo;
import com.drdc1.medic.DataManagement.DataManager;
import com.drdc1.medic.DataManagement.DataObserver;
import com.drdc1.medic.DataStructUtils.HelperMethods;
import com.drdc1.medic.R;
import com.drdc1.medic.ViewUtils.BullsEyeUtils.BullsEyeDrawTask;
import com.drdc1.medic.ViewUtils.BullsEyeUtils.BullsEyeInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import welfareSM.WelfareStatus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SquadStatus.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SquadStatus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SquadStatus extends Fragment implements DataObserver {
    private DataManager dbManager;
    HomeActivity homeActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = ((AppContext) getActivity().getApplication()).getDataManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BackgroundWellnessAlgo.calculateWellness(dbManager, getContext());
        int numSoldiers = dbManager.getActiveSoldiers().size();
        homeActivity = (HomeActivity)getActivity();
        homeActivity.registerBullsEyeFragment(this);
        View view = inflater.inflate(R.layout.fragment_squad_status, container, false);
        LinearLayout wrapper = (LinearLayout) view.findViewById(R.id.layoutwrapper);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        homeActivity.unregisterBullsEyeFragment(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.registerBullsEyeFragment(this);
    }

    @Override
    public void onDestroyView() {
        homeActivity.unregisterBullsEyeFragment(this);
        super.onDestroyView();
    }

    public void update(Map data){
        int numSoldiers = 0;
        if (data.size() == 3) {
            String[] overall = (String[]) data.get("overall");
            String[] skin = (String[]) data.get("skin");
            String[] core = (String[]) data.get("core");

            if (overall == null) {
                return;
            }
            for (int i = 0; i < overall.length; i++){
                if (!overall[i].isEmpty())
                    numSoldiers ++;
            }

            Resources resources = getActivity().getResources();
            BullsEyeDrawTask bullsEyeTask0 = new BullsEyeDrawTask(resources, numSoldiers);
            LinearLayout wrapper = (LinearLayout) getActivity().findViewById(R.id.layoutwrapper);
            bullsEyeTask0.execute(new BullsEyeInfo(wrapper, Arrays.asList(overall), Arrays.asList(core), Arrays.asList(skin),
                    Arrays.asList(overall)));
            wrapper.invalidate();
        }
    }

}
