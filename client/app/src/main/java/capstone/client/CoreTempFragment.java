package capstone.client;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CoreTempFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CoreTempFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CoreTempFragment extends BaseFragment {

    public static CoreTempFragment  newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        CoreTempFragment fragment = new CoreTempFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_core_temp, container, false);
    }
}
