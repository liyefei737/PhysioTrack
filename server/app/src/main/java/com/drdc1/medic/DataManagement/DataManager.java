package com.drdc1.medic.DataManagement;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import welfareSM.WelfareAlgoParams;
import welfareSM.WelfareStatus;
import welfareSM.WelfareTracker;

/**
 * Created by Grace on 2017-01-31.
 */

public class DataManager {
    //log TAGs
    private static final String TAG = "DataManager";

    private Map<String, Database> _physioDataDBMap = null;
    //keys: soldier id, values: Database of physio Data
    private Database _userInfoDB = null;
    //docIDs: soldierIDs, properties of each doc: name, age, height, weight
    private Database _nineLinerDB = null;
    //
    private WelfareAlgoParams _welfareAlgoParams;
    private Map<String, WelfareTracker> _wellnessInfoMap = null;
    private Context _context = null;

    public static String FIELD_HR = "heartRate";
    public static String FIELD_BR = "breathRate";
    public static String FIELD_SKIN = "skinTemp";
    public static String FIELD_CORE = "coreTemp";
    public static String FIELD_ACC = "accSum";
    public static String FIELD_TIMESTAMP = "DateTime";
    public static String FIELD_MOTION = "motion";
    public static String FIELD_BODYPOS = "bodyPos";

    public DataManager(
            Context context) {   //won't init physioDataDBMap b/c we don't know how many soldiers we have
        _context = context;
        _userInfoDB = openDatabase("staticinfo");
        _physioDataDBMap = new HashMap<String, Database>();
        populatePhysioMap();
        _nineLinerDB = openDatabase("nineliner");
        _wellnessInfoMap = new HashMap<String, WelfareTracker>();
        _welfareAlgoParams = new WelfareAlgoParams();
        if (_userInfoDB == null) {
            Log.e(TAG, " Failed to open user info Database");
        }
    }

    private void populatePhysioMap(){
        try {
            Query allDocsQuery = _userInfoDB.createAllDocumentsQuery();
            QueryEnumerator result = allDocsQuery.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document doc = row.getDocument();
                Database db = openDatabase(doc.getId());
                _physioDataDBMap.put(doc.getId(), db);
            }
        } catch (Exception e) {

        }
    }
    public Map<String, Database> getPhysioDataMap() {
        return _physioDataDBMap;
    }

    public Database getNinelinerDatabase() {
        return _nineLinerDB;
    }

    public int getNumSoldiers() {
        if (_physioDataDBMap != null) {
            return _physioDataDBMap.size();
        } else {
            return 0;
        }
    }

    public Database getUserInfoDatabase() {
        return _userInfoDB;
    }

    public boolean addSoldier(String ID, Map<String, Object> staticInfo) {
        if (soldierInSystem(ID)) {
            //soldier exists in db already
            return false;
        }
        Document doc = _userInfoDB.getDocument(ID);
        Document docOl = _nineLinerDB.getDocument(ID);

        try {
            doc.putProperties(staticInfo);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Database physioDB = openDatabase(ID);
        _physioDataDBMap.put(ID, physioDB);

        WelfareTracker iwt = new WelfareTracker();
        _wellnessInfoMap.put(ID, iwt);
        return true;


    }

    /*** query user_info DB for active soldiers
     * @return ArrayList<Soldier> of Soldiers that are active, with name and id filled from the userinfo db
     */
    public ArrayList<Soldier> getActiveSoldiers() {
        ArrayList<Soldier> activeSoldiers = new ArrayList<>();
        View view = _userInfoDB.getView("active");
        if (view.getMap() == null) {
            Mapper mapper = new Mapper() {
                public void map(Map<String, Object> document, Emitter emitter) {
                    String isActive = String.valueOf(document.get("active"));
                    if ("1".equals(isActive))
                        emitter.emit(document.get("id"), document);
                }
            };
            view.setMap(mapper, "1.0");
        }
        try {
            QueryEnumerator result = view.createQuery().run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                String id = String.valueOf(row.getKey());
                String name = String.valueOf(row.getDocument().getProperties().get("name"));
                Soldier soldier = new Soldier(name,id);
                activeSoldiers.add(soldier);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return activeSoldiers;
    }

    public boolean removeSoldier(String ID) {
        if (!soldierInSystem(ID))
        //doesn't exist
        {
            return false;
        }

        try {
            Document doc = _userInfoDB.getDocument(ID);
            Document docOl = _nineLinerDB.getDocument(ID);

            doc.delete();
            docOl.delete();

            _wellnessInfoMap.remove(ID);

            Database db = _physioDataDBMap.get(ID);
            _physioDataDBMap.remove(ID);
            if (db != null) {
                db.delete();
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * The given static info map should have the fields name, age, height, weight
     *
     * @param ID
     * @param staticInfo
     * @return
     */
    public boolean updateSoldierStaticInfo(String ID, Map<String, Object> staticInfo) {
        if (!soldierInSystem(ID))
        //doesn't exist
        {
            return false;
        }
        Document docStatic = _userInfoDB.getDocument(ID);
        try {
            docStatic.putProperties(staticInfo);

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return true;

    }

    /**
     * The given dynamic info map should have the fields timeCreated, accX, accY,
     * accZ, skinTemp, coreTemp, heartRate, breathRate, bodyPosition, motion
     *
     * @param ID
     * @param DateTime
     * @param dynamicInfo
     * @return
     */
    public boolean updateSoldierDynamicInfo(String ID, String DateTime,
                                            Map<String, Object> dynamicInfo) {
        if (!soldierInSystem(ID))
        //doesn't exist
        {
            return false;
        }
        Database dbPhysio = _physioDataDBMap.get(ID);
        Document docPhysio = dbPhysio.getDocument(DateTime);
        try {
            docPhysio.putProperties(dynamicInfo);

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return true;

    }

    public boolean soldierInSystem(String ID) {
        if (_userInfoDB.getExistingDocument(ID) == null) {
            return false;
        }
        return true;
    }

    public Database getSoldierDB(String ID) {
//        if (soldierInSystem(ID)) {
//            Database physioDB = openDatabase(ID);
//            _physioDataDBMap.put(ID, physioDB);
//            return _physioDataDBMap.get(ID);
//        }
//        return null;
        Database physioDB = openDatabase(ID);
        _physioDataDBMap.put(ID, physioDB);
        return _physioDataDBMap.get(ID);

    }

    public WelfareTracker getWellnessTracker(String ID) {
        if (soldierInSystem(ID)) {
            return _wellnessInfoMap.get(ID);
        }
        return null;
    }

    public void saveWellnessTracker(String ID, WelfareTracker iwt) {
        if (soldierInSystem(ID)) {
            _wellnessInfoMap.put(ID, iwt);
        }
    }

    private Database openDatabase(String dbName) {
        Database db = null;
        Manager manager = null;
        try {
            manager = new Manager(new AndroidContext(this._context), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add an option when opening the databse to create a database when there is no database named dbName
        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);

        try {
            db = manager.openDatabase(dbName, options);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return db;
    }

    public HashMap getStaticInfo(String ID) {
        HashMap hm = new HashMap();
        try {
            Query allDocsQuery = _userInfoDB.createAllDocumentsQuery();
            QueryEnumerator result = allDocsQuery.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document doc = row.getDocument();
                if (doc.getProperty("id").equals(ID)) {
                    hm.put("name", doc.getProperty("name"));
                    hm.put("age", doc.getProperty("age"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hm;
    }

    public JSONArray QueryLastXMinutes(String ID, Calendar now, int minutes) {

        JSONArray lastXseconds = new JSONArray();
        Calendar nearestMinute = DateUtils.round(now, Calendar.MINUTE);
        try {
            Query query = getSoldierDB(ID).createAllDocumentsQuery();
            query.setDescending(true);
            String startKey = String.valueOf(nearestMinute.getTimeInMillis());
            String endKey = String.valueOf(nearestMinute.getTimeInMillis() -
                    android.text.format.DateUtils.MINUTE_IN_MILLIS * minutes);

            try {
                query.setEndKey(endKey);
                query.setStartKey(startKey);
                QueryEnumerator result = query.run();
                for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                    QueryRow row = it.next();
                    Map<String, String> tmpMap = (Map) row.getDocument().getProperties();
                    if (tmpMap.size() > 3) {
                        lastXseconds.put(new JSONObject(tmpMap));
                    }
                }
            } catch (CouchbaseLiteException e) {
                //handle this
            }
            return lastXseconds;
        } catch (Exception E) {

        }
        return lastXseconds;
    }

    public WelfareAlgoParams get_welfareAlgoParams(){
        return _welfareAlgoParams;
    }

    public List<WelfareStatus> getOverallSquadStatusList(){
        List<WelfareStatus> squadList = new ArrayList<>();
        Iterator it = _wellnessInfoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            WelfareTracker wt = (WelfareTracker) pair.getValue();
            squadList.add(wt.getOverallStatus());

        }
        return squadList;
    }

    public List<WelfareStatus> getSquadSkinStatusList(){
        List<WelfareStatus> squadList = new ArrayList<>();
        Iterator it = _wellnessInfoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            WelfareTracker wt = (WelfareTracker) pair.getValue();
            squadList.add(wt.getSkinStatus());

        }
        return squadList;
    }

    public List<WelfareStatus> getSquadCoreStatusList(){
        List<WelfareStatus> squadList = new ArrayList<>();
        Iterator it = _wellnessInfoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            WelfareTracker wt = (WelfareTracker) pair.getValue();
            squadList.add(wt.getCoreStatus());

        }
        return squadList;
    }


    public void updateData(String id, String milliTime, final String timeStamp, final String acc, final String skin, final String core, final String hr,
                           final String br, final String body, final String motion){
        Database db = getSoldierDB(id);
        Document saveStateDoc =
                db.getDocument(milliTime);
        try {
            saveStateDoc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    try {
                        Map<String, Object> properties = newRevision.getUserProperties();
                        properties.put(FIELD_TIMESTAMP, timeStamp);
                        properties.put(FIELD_ACC, acc);
                        properties.put(FIELD_SKIN, skin);
                        properties.put(FIELD_CORE, core);
                        properties.put(FIELD_HR, hr);
                        properties.put(FIELD_BR, br);
                        properties.put(FIELD_BODYPOS, body);
                        properties.put(FIELD_MOTION, motion);
                        newRevision.setUserProperties(properties);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }

    public void updateState(String soldierID, String docID, final WelfareStatus nextState){
        try {
            Database db = getSoldierDB(soldierID);
            Document saveStateDoc = db.getDocument(docID);

            saveStateDoc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
                    properties.put("state", nextState);
                    newRevision.setUserProperties(properties);
                    return true;
                }
            });
        } catch (Exception e) {
            //handle this
        }
    }


    public void save9Liner(String sendingid, final int precedence, final int eqreq, final int patienttype, final int securityatpickup, final int pzmarking,
                           final int patientnatstatus, final int symptoms, final String location, final String callsign_freq, final String number_patient,
                           final String pzterrain, final String mechanisminjury, final String injurysustained, final String treatmentgiven,
                           final String terrainobstacles){

        Database db = getNinelinerDatabase();

        Document document = db.getDocument(sendingid);
        try {
            document.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> treatmentInfo = newRevision.getUserProperties();
                    treatmentInfo.put("precedence", precedence);
                    treatmentInfo.put("eqreq", eqreq);
                    treatmentInfo.put("patienttype", patienttype);
                    treatmentInfo.put("securityatpickup", securityatpickup);
                    treatmentInfo.put("pzmarking", pzmarking);
                    treatmentInfo.put("patientnatstatus", patientnatstatus);
                    treatmentInfo.put("symptoms", symptoms);
                    treatmentInfo.put("location", location);
                    treatmentInfo.put("callsign_freq", callsign_freq);
                    treatmentInfo.put("number_patient", number_patient);
                    treatmentInfo.put("pzterrain", pzterrain);
                    treatmentInfo.put("mechanisminjury", mechanisminjury);
                    treatmentInfo.put("injurysustained", injurysustained);
                    treatmentInfo.put("treatmentgiven", treatmentgiven);
                    treatmentInfo.put("terrainobstacles", terrainobstacles);

                    newRevision.setUserProperties(treatmentInfo);
                    return true;
                }

            });
        }catch (Exception e){

        }
    }
}