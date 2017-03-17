package com.drdc1.medic;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.drdc1.medic.BullsEye.BullsEyeDrawTask;
import com.drdc1.medic.BullsEye.BullsEyeInfo;

import java.util.Arrays;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = ((AppContext) getActivity().getApplication()).getDataManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int numSoldiers = 10;//= dbManager.getNumSoldiers();
        View view = inflater.inflate(R.layout.fragment_squad_status, container, false);
        RelativeLayout relLayoutOverall =
                (RelativeLayout) view.findViewById(R.id.bullsEyeOverallHealth);
        RelativeLayout relLayoutCore = (RelativeLayout) view.findViewById(R.id.bullsEyeCoreTemp);
        RelativeLayout relLayoutFatigue = (RelativeLayout) view.findViewById(R.id.bullsEyeFatigue);
        RelativeLayout relLayoutSkin = (RelativeLayout) view.findViewById(R.id.bullsEyeSkinTemp);
        if (numSoldiers != 0) {
            WelfareStatus[] statusArray =
                    {RED, GREEN, YELLOW, YELLOW, GREEN, RED, YELLOW, RED, YELLOW, GREEN};
            Resources resources = getActivity().getResources();
            BullsEyeDrawTask bullsEyeTask0 = new BullsEyeDrawTask(resources, numSoldiers);
            BullsEyeDrawTask bullsEyeTask1 = new BullsEyeDrawTask(resources, numSoldiers);
            BullsEyeDrawTask bullsEyeTask2 = new BullsEyeDrawTask(resources, numSoldiers);
            BullsEyeDrawTask bullsEyeTask3 = new BullsEyeDrawTask(resources, numSoldiers);
            bullsEyeTask0.execute(new BullsEyeInfo(relLayoutOverall, false, Arrays.asList(statusArray)));
            bullsEyeTask1.execute(new BullsEyeInfo(relLayoutCore, true, Arrays.asList(statusArray)));
            bullsEyeTask2.execute(new BullsEyeInfo(relLayoutFatigue, true, Arrays.asList(statusArray)));
            bullsEyeTask3.execute(new BullsEyeInfo(relLayoutSkin, true, Arrays.asList(statusArray)));
        }
        return view;
    }

}
