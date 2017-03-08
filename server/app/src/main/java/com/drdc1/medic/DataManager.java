package com.drdc1.medic;

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
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import welfareSM.IndividualWelfareTracker;

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
    private Map<String, IndividualWelfareTracker> _wellnessInfoMap = null;
    private Context _context = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("02/25/2017 HH:mm:ss.");

    public DataManager(
            Context context) {   //won't init physioDataDBMap b/c we don't know how many soldiers we have
        _context = context;
        _userInfoDB = openDatabase("staticinfo");
        _physioDataDBMap = new HashMap<String, Database>();
        _nineLinerDB = openDatabase("nineliner");
        _wellnessInfoMap = new HashMap<String, IndividualWelfareTracker>();
        if (_userInfoDB == null) {
            Log.e(TAG, " Failed to open user info Database");
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

        IndividualWelfareTracker iwt = new IndividualWelfareTracker();
        _wellnessInfoMap.put(ID, iwt);
        return true;

//        try {
//            Document doc = _userInfoDB.getDocument(ID);
//            doc.putProperties(staticInfo);
//
//            Database physioDB = openDatabase(ID);
//            _physioDataDBMap.put(ID, physioDB);
//
//            IndividualWelfareTracker iwt = new IndividualWelfareTracker();
//            _wellnessInfoMap.put(ID, iwt);
//            return true;
//
//        } catch (CouchbaseLiteException e) {
//            return false;
//        }

    }

    //get active soldier, active soldier meaning soldiers that are currently being monitored

    /***
     * @return HashMap of Soldier name and id that are active in the database
     */
    public HashMap<String, String> getActiveSoldier() {
        HashMap<String, String> activeSoldiers = new HashMap<>();
        View view = _userInfoDB.getView("active");
        if (view.getMap() == null) {
            Mapper mapper = new Mapper() {
                public void map(Map<String, Object> document, Emitter emitter) {
                    String isActive = (String) document.get("active");
                    if ("1".equals(isActive)) {
                        emitter.emit(document.get("id"), document);
                    }
                }
            };
            view.setMap(mapper, "1.0");
        }
        try {
            QueryEnumerator result = view.createQuery().run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                String id = (String) row.getKey();
                String name = (String) row.getDocumentProperties().get("name");
                activeSoldiers.put(name, id);
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
        if (soldierInSystem(ID)) {
            Database physioDB = openDatabase(ID);
            _physioDataDBMap.put(ID, physioDB);
            return _physioDataDBMap.get(ID);
        }
        return null;
    }

    public IndividualWelfareTracker getWellnessTracker(String ID) {
        if (soldierInSystem(ID)) {
            return _wellnessInfoMap.get(ID);
        }
        return null;
    }

    public void saveWellnessTracker(String ID, IndividualWelfareTracker iwt) {
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

    public JSONArray QueryLastXMinutes(String ID, Calendar now, int minutes) {

        JSONArray lastXseconds = new JSONArray();
        Calendar nearestMinute = DateUtils.round(now, Calendar.MINUTE);
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
    }
}
