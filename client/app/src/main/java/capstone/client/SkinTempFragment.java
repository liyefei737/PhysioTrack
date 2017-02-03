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
 * {@link SkinTempFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SkinTempFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SkinTempFragment extends BaseFragment {

    public static SkinTempFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        SkinTempFragment fragment = new SkinTempFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skin_temp, container, false);
    }
}
