package com.drdc1.medic.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.drdc1.medic.Activities.HomeActivity;
import com.drdc1.medic.AppContext;
import com.drdc1.medic.DataManagement.DataManager;
import com.drdc1.medic.R;

import java.util.HashMap;
import java.util.Iterator;

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
            patientnatstatus_noncoalitioncivil, patientnatstatus_opforces, patientnatstatus_child,
            airway, breathing, pulserate, conscious;
    EditText location, callsign_freq, number_patient, terrainobstacles, pzterrain, mechanisminjury,
            injurysustained, treatmentgiven;
    Button btSubmit;
    String sendingid;
    int precedence, eqreq, patienttype, securityatpickup, pzmarking, patientnatstatus, symptoms;
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
        String IDpassed = ((HomeActivity) getActivity()).popIndividualSoldierId();
        if (IDpassed != null) {
            sendingid = IDpassed;
            //TODO: handle code for id from namelist here.
        }

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

        airway = (CheckBox) rootView.findViewById(R.id.airway);
        breathing = (CheckBox) rootView.findViewById(R.id.breathing);
        pulserate = (CheckBox) rootView.findViewById(R.id.pulserate);
        conscious = (CheckBox) rootView.findViewById(R.id.conscious);

        pzterrain = (EditText) rootView.findViewById(R.id.pzterrain);
        mechanisminjury = (EditText) rootView.findViewById(R.id.mechanisminjury);
        injurysustained = (EditText) rootView.findViewById(R.id.injurysustained);
        treatmentgiven = (EditText) rootView.findViewById(R.id.treatmentgiven);

        location = (EditText) rootView.findViewById(R.id.location);
        callsign_freq = (EditText) rootView.findViewById(R.id.callsign_freq);
        number_patient = (EditText) rootView.findViewById(R.id.number_patient);
        terrainobstacles = (EditText) rootView.findViewById(R.id.terrainobstacles);

        btSubmit = (Button) rootView.findViewById(R.id.btSubmit);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Intent myIntent = new Intent(getActivity(), LoginActivity.class);
//                startActivity(myIntent);    // change to startActivity

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
                if (airway.isChecked()) {
                    symptoms = 0;
                }
                if (breathing.isChecked()) {
                    symptoms = 1;
                }
                if (pulserate.isChecked()) {
                    symptoms = 2;
                }
                if (conscious.isChecked()) {
                    symptoms = 3;
                }


                dataManager.save9Liner(sendingid, precedence, eqreq, patienttype, securityatpickup, pzmarking, patientnatstatus,
                        symptoms, location.getText().toString(),callsign_freq.getText().toString(), number_patient.getText().toString(),
                        pzterrain.getText().toString(), mechanisminjury.getText().toString(), injurysustained.getText().toString(),
                        treatmentgiven.getText().toString(), terrainobstacles.getText().toString());


            }
        });

        // Inflate the layout for this fragment

        Database db = dataManager.getNinelinerDatabase();
        if (db.getExistingDocument(sendingid) != null) {

            HashMap hm = new HashMap();
            try {
                Query allDocsQuery = db.createAllDocumentsQuery();
                QueryEnumerator result = allDocsQuery.run();
                for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                    QueryRow row = it.next();
                    Document doc = row.getDocument();
                    if (doc.getProperty("id").equals(sendingid)) {
                        hm.put("precedence", doc.getProperty("precedence"));
                        hm.put("eqreq", doc.getProperty("eqreq"));
                        hm.put("patienttype", doc.getProperty("patienttype"));
                        hm.put("securityatpickup", doc.getProperty("securityatpickup"));
                        hm.put("pzmarking", doc.getProperty("pzmarking"));
                        hm.put("patientnatstatus", doc.getProperty("patientnatstatus"));
                        hm.put("symptoms", doc.getProperty("symptoms"));
                        hm.put("location", doc.getProperty("location"));
                        hm.put("callsign_freq", doc.getProperty("callsign_freq"));
                        hm.put("number_patient", doc.getProperty("number_patient"));
                        hm.put("pzterrain", doc.getProperty("pzterrain"));
                        hm.put("mechanisminjury", doc.getProperty("mechanisminjury"));
                        hm.put("injurysustained", doc.getProperty("injurysustained"));
                        hm.put("treatmentgiven", doc.getProperty("treatmentgiven"));
                        hm.put("terrainobstacles", doc.getProperty("terrainobstacles"));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((int) hm.get("precedence") == 0) {
                precedence_urgent.setChecked(true);
            }
            if ((int) hm.get("precedence") == 1) {
                precedence_priority.setChecked(true);
            }
            if ((int) hm.get("precedence") == 2) {
                precedence_routine.setChecked(true);
            }
            if ((int) hm.get("eqreq") == 0) {
                eqreq_none.setChecked(true);
            }
            if ((int) hm.get("eqreq") == 1) {
                eqreq_hoist.setChecked(true);
            }
            if ((int) hm.get("eqreq") == 2) {
                eqreq_extrication.setChecked(true);
            }
            if ((int) hm.get("eqreq") == 3) {
                eqreq_ventilator.setChecked(true);
            }
            if ((int) hm.get("patienttype") == 0) {
                patienttype_litter.setChecked(true);
            }
            if ((int) hm.get("patienttype") == 1) {
                patienttype_walking.setChecked(true);
            }
            if ((int) hm.get("patienttype") == 2) {
                patienttype_escort.setChecked(true);
            }
            if ((int) hm.get("securityatpickup") == 0) {
                securityatpickup_noenem.setChecked(true);
            }
            if ((int) hm.get("securityatpickup") == 1) {
                securityatpickup_possibileenem.setChecked(true);
            }
            if ((int) hm.get("securityatpickup") == 2) {
                securityatpickup_eneminarea.setChecked(true);
            }
            if ((int) hm.get("securityatpickup") == 3) {
                securityatpickup_hotpz.setChecked(true);
            }
            if ((int) hm.get("pzmarking") == 0) {
                pzmarking_panles.setChecked(true);
            }
            if ((int) hm.get("pzmarking") == 1) {
                pzmarking_pyro.setChecked(true);
            }
            if ((int) hm.get("pzmarking") == 2) {
                pzmarking_smoke.setChecked(true);
            }
            if ((int) hm.get("pzmarking") == 3) {
                pzmarking_other.setChecked(true);
            }
            if ((int) hm.get("patientnatstatus") == 0) {
                patientnatstatus_coalitionmil.setChecked(true);
            }
            if ((int) hm.get("patientnatstatus") == 1) {
                patientnatstatus_civiliancf.setChecked(true);
            }
            if ((int) hm.get("patientnatstatus") == 2) {
                patientnatstatus_noncoalitionsf.setChecked(true);
            }
            if ((int) hm.get("patientnatstatus") == 3) {
                patientnatstatus_noncoalitioncivil.setChecked(true);
            }
            if ((int) hm.get("patientnatstatus") == 4) {
                patientnatstatus_opforces.setChecked(true);
            }
            if ((int) hm.get("patientnatstatus") == 5) {
                patientnatstatus_child.setChecked(true);
            }
            if ((int) hm.get("symptoms") == 0) {
                airway.setChecked(true);
            }
            if ((int) hm.get("symptoms") == 1) {
                breathing.setChecked(true);
            }
            if ((int) hm.get("symptoms") == 2) {
                pulserate.setChecked(true);
            }
            if ((int) hm.get("symptoms") == 3) {
                conscious.setChecked(true);
            }
            pzterrain.setText((String) hm.get("pzterrain"));

            mechanisminjury.setText((String) hm.get("mechanisminjury"));

            injurysustained.setText((String) hm.get("injurysustained"));

            treatmentgiven.setText((String) hm.get("treatmentgiven"));

            location.setText((String) hm.get("location"));

            callsign_freq.setText((String) hm.get("callsign_freq"));

            number_patient.setText((String) hm.get("number_patient"));

            terrainobstacles.setText((String) hm.get("terrainobstacles"));

        }

        return rootView;

    }

    @Override
    public void onClick(View v) {
    }
}
