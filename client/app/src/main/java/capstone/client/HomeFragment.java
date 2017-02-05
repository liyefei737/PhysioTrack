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

import welfareSM.WelfareStatus;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {

    private AlgoReceiver mReceiver;

    public class AlgoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra("STATUS_UPDATE");
            BottomBarActivity activity = (BottomBarActivity) getActivity();
            activity.updateHomeFragment(state);

        }
    }

    public static HomeFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(){
        mReceiver = new AlgoReceiver();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("capstone.client.BackgroundWellnessAlgo.STATUS_UPDATE");
        getActivity().registerReceiver(mReceiver, ifilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WelfareStatus state = ((DRDCClient) getActivity().getApplication()).getLastState();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        if (state != null)
            updateWellnessStatus(state.toString());
        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            getActivity().unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("capstone.client.BackgroundWellnessAlgo.STATUS_UPDATE");
        getActivity().registerReceiver(mReceiver, ifilter);
    }

    @Override
    public void onDestroyView() {
        try {
            getActivity().unregisterReceiver(mReceiver);
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    public void updateWellnessStatus(String state){
        ImageView wellnessStatus = (ImageView) getActivity().findViewById(R.id.wellness_status);

        if (state.equals("GREEN")){
            wellnessStatus.setImageResource(R.drawable.home_center_green);
        }
        else if (state.equals("YELLOW")){
            wellnessStatus.setImageResource(R.drawable.home_center_yellow);
        }
        else{
            wellnessStatus.setImageResource(R.drawable.home_center_red);
        }
        wellnessStatus.refreshDrawableState();

    }
}
