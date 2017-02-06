package capstone.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class BreathFragment extends BaseFragment {

    private ParamReceiver mReceiver;
    private Intent intent;

    public class ParamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String param = intent.getStringExtra("BREATH_UPDATE");
            if (param != null) {
                BottomBarActivity activity = (BottomBarActivity) getActivity();
                activity.updateBreathFragment(param);
            }

        }
    }

    public static BreathFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        BreathFragment fragment = new BreathFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(){
        mReceiver = new ParamReceiver();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("capstone.client.BackgroundParameterUpdate.PARAM_UPDATE");
        getActivity().registerReceiver(mReceiver, ifilter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intent = new Intent();
        intent.setAction("BREATH");
        intent.setClass(getActivity(), BackgroundParameterUpdate.class);
        getActivity().startService(intent);
        View view = inflater.inflate(R.layout.fragment_breath, container, false);
        TextView tv = (TextView) view.findViewById(R.id.currentBreathRate);
        updateParam(((DRDCClient) getActivity().getApplication()).getLastBreathingRate(), tv);
        return view;
    }

    @Override
    public void onResume(){
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("capstone.client.BackgroundParameterUpdate.PARAM_UPDATE");
        getActivity().registerReceiver(mReceiver, ifilter);
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            getActivity().unregisterReceiver(mReceiver);
            getActivity().stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        try {
            getActivity().unregisterReceiver(mReceiver);
            getActivity().stopService(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    public void updateParam(String param, TextView br){
        br.setText(param);
        br.refreshDrawableState();
    }


}
