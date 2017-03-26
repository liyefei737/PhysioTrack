package capstone.client.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import capstone.client.Activities.BottomBarActivity;
import capstone.client.Activities.SetupActivity;
import capstone.client.DataManagement.DBManager;
import capstone.client.DataManagement.Soldier;
import capstone.client.R;
import capstone.client.ViewTools.EditTextHandler;

import static android.view.View.VISIBLE;

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
        Activity act = getActivity();
        DBManager dbManager = new DBManager(act);
        Soldier soldier = dbManager.getSoldierDetails();
        View view = inflater.inflate(R.layout.fragment_edit_info, container, false);
        if (!(soldier == null) ){
            view = setEditFields(soldier, view);
        }
        if (act.getClass() == BottomBarActivity.class){
            View et = view.findViewById(R.id.editSimServerURL);
            if (et != null)
                et.setVisibility(View.GONE);
        }

        return view;

    }

    public View setEditFields(Soldier s, View view) {
        EditText id = (EditText) view.findViewById(R.id.etSoldierId);
        EditText age = (EditText) view.findViewById(R.id.etAge);
        EditText weight = (EditText) view.findViewById(R.id.etWeight);
        EditText height = (EditText) view.findViewById(R.id.etHeight);
        EditText ip = (EditText) view.findViewById(R.id.etIP);
        EditText name = (EditText) view.findViewById(R.id.etSoldierName);
        EditTextHandler.setSoldierFields(s, id, name, age, weight, height, ip);
        return view;
    }

    public static void edit_info_cancel(Activity activity, DBManager dbManager){
        Button savebtn = (Button) activity.findViewById(R.id.btSave);
        Button cancelbtn = (Button) activity.findViewById(R.id.btCancel);
        ImageButton edit_data = (ImageButton) activity.findViewById(R.id.edit_data);
        savebtn.setVisibility(View.INVISIBLE);
        cancelbtn.setVisibility(View.INVISIBLE);
        edit_data.setVisibility(VISIBLE);

        List<EditText> etList = new ArrayList<>();
        etList.add((EditText) activity.findViewById(R.id.etSoldierId));
        etList.add((EditText) activity.findViewById(R.id.etSoldierName));
        etList.add((EditText) activity.findViewById(R.id.etAge));
        etList.add((EditText) activity.findViewById(R.id.etWeight));
        etList.add((EditText) activity.findViewById(R.id.etHeight));
        etList.add((EditText) activity.findViewById(R.id.etIP));

        activity.findViewById(R.id.id_edit_hint).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.name_edit_hint).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.ip_edit_hint).setVisibility(View.INVISIBLE);
        EditTextHandler.disableAndFormat(etList);
        EditTextHandler.setSoldierFields(dbManager.getSoldierDetails(), etList.get(0), etList.get(1), etList.get(2), etList.get(3), etList.get(4), etList.get(5));
    }


    //static click handlers for activities to use
    public static boolean edit_info_save(Activity activity, DBManager dbManager){
        List<EditText> etList = new ArrayList<>();
        final Button btnSave = (Button) activity.findViewById(R.id.btSave);
        final Button cancelbtn = (Button) activity.findViewById(R.id.btCancel);
        ImageButton edit_data = (ImageButton) activity.findViewById(R.id.edit_data);
        Soldier soldier = dbManager.getSoldierDetails();

        EditText id = (EditText) activity.findViewById(R.id.etSoldierId);
        final String newId = id.getText().toString();
        if (newId.equals(""))
            return false;
        etList.add(id);

        EditText age = (EditText) activity.findViewById(R.id.etAge);
        final String newAge = age.getText().toString();
        etList.add(age);

        EditText weight = (EditText) activity.findViewById(R.id.etWeight);
        final String newWeight = weight.getText().toString();
        etList.add(weight);

        EditText height = (EditText) activity.findViewById(R.id.etHeight);
        final String newHeight = height.getText().toString();
        etList.add(height);

        EditText ip = (EditText) activity.findViewById(R.id.etIP);
        final String newIP = ip.getText().toString();
        etList.add(ip);

        EditText name = (EditText) activity.findViewById(R.id.etSoldierName);
        final String newName = name.getText().toString();
        etList.add(name);


        try {
            if (soldier == null) {
                dbManager.updateSoldier(newId, newName, newAge, newWeight, newHeight, newIP);
            }
            else {
                if ( !newId.equals(soldier.getSoldierID()))
                    //delete old doc
                    dbManager.deleteSoldier(soldier.getSoldierID());
                dbManager.newSoldier(newId, newName, newAge, newWeight, newHeight, newIP);
            }

            String newURL;
            EditText url = (EditText) activity.findViewById(R.id.editSimServerURL);
            if (url != null) {
                newURL = url.getText().toString();
                if (!newURL.equals(""))
                    dbManager.updatePHPURL(newURL);
            }
            EditTextHandler.disableAndFormat(etList);

            activity.findViewById(R.id.id_edit_hint).setVisibility(View.INVISIBLE);
            activity.findViewById(R.id.name_edit_hint).setVisibility(View.INVISIBLE);
            activity.findViewById(R.id.ip_edit_hint).setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            return false;
        }
        btnSave.setVisibility(View.INVISIBLE);
        cancelbtn.setVisibility(View.INVISIBLE);
        edit_data.setVisibility(VISIBLE);
        return true;
    }

    public static void edit_fields(Activity activity){
        Button savebtn = (Button) activity.findViewById(R.id.btSave);
        Button cancelbtn = (Button) activity.findViewById(R.id.btCancel);
        ImageButton edit_data = (ImageButton) activity.findViewById(R.id.edit_data);
        savebtn.setVisibility(VISIBLE);
        cancelbtn.setVisibility(VISIBLE);
        edit_data.setVisibility(View.INVISIBLE);


        List<EditText> etList = new ArrayList<>();
        if (activity.getClass() == SetupActivity.class)
            etList.add((EditText) activity.findViewById(R.id.editSimServerURL));
        etList.add((EditText) activity.findViewById(R.id.etSoldierId));
        etList.add((EditText) activity.findViewById(R.id.etAge));
        etList.add((EditText) activity.findViewById(R.id.etWeight));
        etList.add((EditText) activity.findViewById(R.id.etHeight));
        etList.add((EditText) activity.findViewById(R.id.etIP));
        etList.add((EditText) activity.findViewById(R.id.etSoldierName));


        activity.findViewById(R.id.id_edit_hint).setVisibility(VISIBLE);
        activity.findViewById(R.id.name_edit_hint).setVisibility(VISIBLE);
        activity.findViewById(R.id.ip_edit_hint).setVisibility(VISIBLE);
        EditTextHandler.enableAndFormat(etList);
    }
}
