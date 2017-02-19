package capstone.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Map;

import capstone.client.BackgroundServices.BackgroundUIUpdator;
import capstone.client.BaseFragment;
import capstone.client.Activities.BottomBarActivity;
import capstone.client.DRDCClient;
import capstone.client.R;
import welfareSM.WelfareStatus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment implements DataObserver {

    private BottomBarActivity bottomBarActivity;

    public static HomeFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate() {
        bottomBarActivity = (BottomBarActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bottomBarActivity.registerFragment(this);
        BackgroundUIUpdator.updateDataAndBroadcast(new DBManager(getContext()), getContext());
        WelfareStatus state = ((DRDCClient) getActivity().getApplication()).getLastState();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageView iv = (ImageView) view.findViewById(R.id.wellness_status);
        if (state != null)
            updateWellnessStatus(state.toString(), iv);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        bottomBarActivity.unregisterFragment(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bottomBarActivity.registerFragment(this);
    }

    @Override
    public void onDestroyView() {
        bottomBarActivity.unregisterFragment(this);
        super.onDestroyView();
    }

    public void updateWellnessStatus(String state, ImageView wellnessStatus) {
        if (state == null){
            //TODO: GREY HOME SYMBOL
        }
        else {
            if (state.equals("GREEN")) {
                wellnessStatus.setImageResource(R.drawable.home_center_green);
            } else if (state.equals("YELLOW")) {
                wellnessStatus.setImageResource(R.drawable.home_center_yellow);
            } else {
                wellnessStatus.setImageResource(R.drawable.home_center_red);
            }
        }
        wellnessStatus.refreshDrawableState();

    }

    public void update(Map data){
        String state = (String) data.get("state");
        ImageView wellnessView = (ImageView) getActivity().findViewById(R.id.wellness_status);
        updateWellnessStatus(state, wellnessView);
    }
}
