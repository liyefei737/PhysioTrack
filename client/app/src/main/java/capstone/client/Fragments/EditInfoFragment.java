package capstone.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import capstone.client.BaseFragment;
import capstone.client.DBManager;
import capstone.client.R;
import capstone.client.Soldier;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditInfoFragment extends BaseFragment {

    public static EditInfoFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        EditInfoFragment fragment = new EditInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DBManager dbManager = new DBManager(getActivity());
        Soldier soldier = dbManager.getSoldierDetails();
        View view = inflater.inflate(R.layout.fragment_edit_info, container, false);
        if (soldier != null) {
            view = setEditFields(soldier, view);
        }
        return view;

    }

    public View setEditFields(Soldier s, View view) {
        String strID = s.getSoldierID();
        if (strID != null && !strID.isEmpty()) {
            EditText id = (EditText) view.findViewById(R.id.etSoldierId);
            id.setHint(strID);
            id.setText(strID);
        }

        int iAge = s.getAge();
        if (iAge > 0) {
            EditText age = (EditText) view.findViewById(R.id.etAge);
            age.setHint(String.valueOf(iAge));
            age.setText(String.valueOf(iAge));
        }

        int iWeight = s.getWeight();
        if (iWeight > 0) {
            EditText weight = (EditText) view.findViewById(R.id.etWeight);
            weight.setHint(String.valueOf(iWeight));
            weight.setText(String.valueOf(iWeight));
        }

        int iHeight = s.getHeight();
        if (iHeight > 0) {
            EditText height = (EditText) view.findViewById(R.id.etHeight);
            height.setHint(String.valueOf(iHeight));
            height.setText(String.valueOf(iHeight));
        }

        return view;
    }

}
