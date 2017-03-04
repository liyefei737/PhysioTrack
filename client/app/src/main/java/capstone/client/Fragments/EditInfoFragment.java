package capstone.client.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import capstone.client.DataManagement.DBManager;
import capstone.client.DataManagement.Soldier;
import capstone.client.R;
import capstone.client.ViewTools.EditTextHandler;

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
        EditText id = (EditText) view.findViewById(R.id.etSoldierId);
        EditText age = (EditText) view.findViewById(R.id.etAge);
        EditText weight = (EditText) view.findViewById(R.id.etWeight);
        EditText height = (EditText) view.findViewById(R.id.etHeight);
        EditTextHandler.setSoldierFields(s, id, age, weight, height);

        return view;
    }

}
