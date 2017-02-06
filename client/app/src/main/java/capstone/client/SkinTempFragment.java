package capstone.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HeartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HeartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SkinTempFragment extends BaseFragment {

    private ParamReceiver mReceiver;
    private Intent intent;

    public class ParamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String param = intent.getStringExtra("SKIN_UPDATE");
            if(param != null) {
                BottomBarActivity activity = (BottomBarActivity) getActivity();
                activity.updateSkinFragment(param);
            }

        }
    }

    public static SkinTempFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        SkinTempFragment fragment = new SkinTempFragment();
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
        intent.setAction("SKIN");
        intent.setClass(getActivity(), BackgroundParameterUpdate.class);
        getActivity().startService(intent);
        View view = inflater.inflate(R.layout.fragment_skin_temp, container, false);
        TextView tv = (TextView) view.findViewById(R.id.currentSkinTemp);
        updateParam(((DRDCClient) getActivity().getApplication()).getLastSkinTemp(), tv);
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


    public void updateParam(String param, TextView hr){
        hr.setText(param);
        hr.refreshDrawableState();
    }
}
