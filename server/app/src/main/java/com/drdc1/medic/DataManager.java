package com.drdc1.medic;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.Map;

import welfareSM.IndividualWelfareTracker;

/**
 * Created by Grace on 2017-01-31.
 */

public class DataManager {
    //log TAGs
    private static final String TAG = "DataManager";

    private Map<String, Database> _physioDataDBMap = null;   //keys: soldier id, values: Database of physio Data
    private Database _userInfoDB = null;   //docIDs: soldierIDs, properties of each doc: name, age, height, weight
    private Map<String, IndividualWelfareTracker> _wellnessInfoMap = null;
    private Context _context = null;

    public DataManager(Context context) {   //won't init physioDataDBMap b/c we don't know how many soldiers we have
        _context = context;
        _userInfoDB = openDatabase("staticInfo");
        if (_userInfoDB == null) {
            Log.e(TAG, " Failed to open user info Database");
        }
    }

    public Map<String, Database> getPhysioDataMap(){
        return _physioDataDBMap;
    }

    public int getNumSoldiers(){
        return _physioDataDBMap.size();
    }
    public Database getUserInfoDatabase() {
        return _userInfoDB;
    }

    public boolean addSoldier(String ID, Map<String, Object> staticInfo){
        if (soldierInSystem(ID))
            //soldier exists in db already
            return false;
        try {
            Document doc = _userInfoDB.getDocument(ID);
            doc.putProperties(staticInfo);

            Database physioDB = openDatabase(ID);
            _physioDataDBMap.put(ID, physioDB);

            IndividualWelfareTracker iwt = new IndividualWelfareTracker();
            _wellnessInfoMap.put(ID, iwt);
            return true;

        } catch (CouchbaseLiteException e) {
            return false;
        }

    }

    public boolean removeSoldier(String ID){
        if (!soldierInSystem(ID))
            //doesn't exist
            return false;

        try {
            Document doc = _userInfoDB.getDocument(ID);
            doc.delete();

            _wellnessInfoMap.remove(ID);

            Database db = _physioDataDBMap.get(ID);
            _physioDataDBMap.remove(ID);
            if (db != null)
                db.delete();
            else return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean soldierInSystem(String ID){
        if (_userInfoDB.getExistingDocument(ID) == null){
            return false;
        }
        return true;
    }

    public Database getSoldierDB(String ID){
        if (soldierInSystem(ID))
            return _physioDataDBMap.get(ID);
        return null;
    }

    public IndividualWelfareTracker getWellnessTracker(String ID){
        if (soldierInSystem(ID))
            return _wellnessInfoMap.get(ID);
        return null;
    }

    public void saveWellnessTracker(String ID, IndividualWelfareTracker iwt){
        if (soldierInSystem(ID)){
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
}
