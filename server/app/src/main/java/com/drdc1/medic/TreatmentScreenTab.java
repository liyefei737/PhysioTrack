package com.drdc1.medic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TreatmentScreenTab extends Fragment implements View.OnClickListener {
    CheckBox precedence_urgent, precedence_priority, precedence_routine, eqreq_none, eqreq_hoist,
            eqreq_extrication, eqreq_ventilator, patienttype_litter, patienttype_walking,
            patienttype_escort, securityatpickup_noenem, securityatpickup_possibileenem,
            securityatpickup_eneminarea, securityatpickup_hotpz, pzmarking_panles, pzmarking_pyro,
            pzmarking_smoke, pzmarking_other, patientnatstatus_coalitionmil,
            patientnatstatus_civiliancf, patientnatstatus_noncoalitionsf,
            patientnatstatus_noncoalitioncivil, patientnatstatus_opforces, patientnatstatus_child;
    EditText location, callsign_freq, number_patient, terrainobstacles;
    Button btSubmit;
    String sendingid;
    int precedence, eqreq, patienttype, securityatpickup, pzmarking, patientnatstatus;
    private DataManager dataManager = null;

    public TreatmentScreenTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataManager = ((AppContext) this.getActivity().getApplication()).getDataManager();

        View rootView =
                inflater.inflate(R.layout.fragment_treatment_screen_tab, container, false);

        precedence_urgent = (CheckBox) rootView.findViewById(R.id.precedence_urgent);
        precedence_priority = (CheckBox) rootView.findViewById(R.id.precedence_priority);
        precedence_routine = (CheckBox) rootView.findViewById(R.id.precedence_routine);
        eqreq_none = (CheckBox) rootView.findViewById(R.id.eqreq_none);
        eqreq_hoist = (CheckBox) rootView.findViewById(R.id.eqreq_hoist);
        eqreq_extrication = (CheckBox) rootView.findViewById(R.id.eqreq_extrication);
        eqreq_ventilator = (CheckBox) rootView.findViewById(R.id.eqreq_ventilator);
        patienttype_litter = (CheckBox) rootView.findViewById(R.id.patienttype_litter);
        patienttype_walking = (CheckBox) rootView.findViewById(R.id.patienttype_walking);
        patienttype_escort = (CheckBox) rootView.findViewById(R.id.patienttype_escort);
        securityatpickup_noenem = (CheckBox) rootView.findViewById(R.id.securityatpickup_noenem);
        securityatpickup_possibileenem =
                (CheckBox) rootView.findViewById(R.id.securityatpickup_possibileenem);
        securityatpickup_eneminarea =
                (CheckBox) rootView.findViewById(R.id.securityatpickup_eneminarea);
        securityatpickup_hotpz = (CheckBox) rootView.findViewById(R.id.securityatpickup_hotpz);
        pzmarking_panles = (CheckBox) rootView.findViewById(R.id.pzmarking_panles);
        pzmarking_pyro = (CheckBox) rootView.findViewById(R.id.pzmarking_pyro);
        pzmarking_smoke = (CheckBox) rootView.findViewById(R.id.pzmarking_smoke);
        pzmarking_other = (CheckBox) rootView.findViewById(R.id.pzmarking_other);
        patientnatstatus_coalitionmil =
                (CheckBox) rootView.findViewById(R.id.patientnatstatus_coalitionmil);
        patientnatstatus_civiliancf =
                (CheckBox) rootView.findViewById(R.id.patientnatstatus_civiliancf);
        patientnatstatus_noncoalitionsf =
                (CheckBox) rootView.findViewById(R.id.patientnatstatus_noncoalitionsf);
        patientnatstatus_noncoalitioncivil =
                (CheckBox) rootView.findViewById(R.id.patientnatstatus_noncoalitioncivil);
        patientnatstatus_opforces =
                (CheckBox) rootView.findViewById(R.id.patientnatstatus_opforces);
        patientnatstatus_child = (CheckBox) rootView.findViewById(R.id.patientnatstatus_child);
        location = (EditText) rootView.findViewById(R.id.location);
        callsign_freq = (EditText) rootView.findViewById(R.id.callsign_freq);
        number_patient = (EditText) rootView.findViewById(R.id.number_patient);
        terrainobstacles = (EditText) rootView.findViewById(R.id.terrainobstacles);

        btSubmit = (Button) rootView.findViewById(R.id.btSubmit);

//        btSubmit.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
////                Intent myIntent = new Intent(getActivity(), LoginActivity.class);
////                startActivity(myIntent);    // change to startActivity
//            }
//        });

        // Inflate the layout for this fragment
        return rootView;

    }

    @Override
    public void onClick(View v) {
        {
            if (precedence_urgent.isChecked()) {
                precedence = 0;
            }
            if (precedence_priority.isChecked()) {
                precedence = 1;
            }
            if (precedence_routine.isChecked()) {
                precedence = 2;
            }
            if (eqreq_none.isChecked()) {
                eqreq = 0;
            }
            if (eqreq_hoist.isChecked()) {
                eqreq = 1;
            }
            if (eqreq_extrication.isChecked()) {
                eqreq = 2;
            }
            if (eqreq_ventilator.isChecked()) {
                eqreq = 3;
            }
            if (patienttype_litter.isChecked()) {
                patienttype = 0;
            }
            if (patienttype_walking.isChecked()) {
                patienttype = 1;
            }
            if (patienttype_escort.isChecked()) {
                patienttype = 2;
            }
            if (securityatpickup_noenem.isChecked()) {
                securityatpickup = 0;
            }
            if (securityatpickup_possibileenem.isChecked()) {
                securityatpickup = 1;
            }
            if (securityatpickup_eneminarea.isChecked()) {
                securityatpickup = 2;
            }
            if (securityatpickup_hotpz.isChecked()) {
                securityatpickup = 3;
            }
            if (pzmarking_panles.isChecked()) {
                pzmarking = 0;
            }
            if (pzmarking_pyro.isChecked()) {
                pzmarking = 1;
            }
            if (pzmarking_smoke.isChecked()) {
                pzmarking = 2;
            }
            if (pzmarking_other.isChecked()) {
                pzmarking = 3;
            }
            if (patientnatstatus_coalitionmil.isChecked()) {
                patientnatstatus = 0;
            }
            if (patientnatstatus_civiliancf.isChecked()) {
                patientnatstatus = 1;
            }
            if (patientnatstatus_noncoalitionsf.isChecked()) {
                patientnatstatus = 2;
            }
            if (patientnatstatus_noncoalitioncivil.isChecked()) {
                patientnatstatus = 3;
            }
            if (patientnatstatus_opforces.isChecked()) {
                patientnatstatus = 4;
            }
            if (patientnatstatus_child.isChecked()) {
                patientnatstatus = 5;
            }

            Database db = dataManager.getNinelinerDatabase();
            Document doc = db.getDocument(sendingid);

            try {
                doc.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        Map<String, Object> properties = newRevision.getUserProperties();
                        properties.put("precedence", precedence);
                        properties.put("eqreq", eqreq);
                        properties.put("patienttype", patienttype);
                        properties.put("securityatpickup", securityatpickup);
                        properties.put("pzmarking", pzmarking);
                        properties.put("patientnatstatus", patientnatstatus);
                        properties.put("location", location.getText());
                        properties.put("callsign_freq", callsign_freq.getText());
                        properties.put("number_patient", number_patient.getText());
                        properties.put("terrainobstacles", terrainobstacles.getText());

                        newRevision.setUserProperties(properties);
                        return true;
                    }
                });
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }
}
