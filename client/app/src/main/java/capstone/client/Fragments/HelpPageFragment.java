package capstone.client.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import capstone.client.DataManagement.DBManager;
import capstone.client.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelpPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelpPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpPageFragment extends Fragment {
    Button btTutor;
    public HelpPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_page, container, false);
        EditText simURL = (EditText) view.findViewById(R.id.editSimServerURL);
        Button btTutor = (Button) view.findViewById(R.id.btTutor);
        btTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Tutorial will be implement later",Toast.LENGTH_LONG).show();
            }
        });
        DBManager dbManager = new DBManager(getActivity());
        String currentURL = dbManager.getPHPURL();
        if (currentURL != null && !currentURL.equals("")) {
            simURL.setHint(currentURL);
            simURL.setText(currentURL);
        }
        ImageView check = (ImageView) view.findViewById(R.id.simURLCheckmark);
        check.setVisibility(View.INVISIBLE);
        return view;
    }


}
