package capstone.client.DataManagement;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import welfareSM.WelfareStatus;

/**
 * Each User has its own database for its simulated data
 * This is refered as "data database" because it store s a user's simulated data
 * A "data database" is named by the solier ID to relate it to a soldier
 * Also there is a database to to keep track of user information i.e. each user's id, name age....
 */

public class DBManager {

    //log TAGs
    private static final String TAG = "DBManager";

    private Database _userDB = null;
    private Database _dataDB = null;
    private Context _context = null;
    public static String USER_DB = "user";
    public static String DATA_DB = "data";

    //property keys for soldier details
    public static String ID_KEY = "id";
    public static String NAME_KEY = "name";
    public static String WEIGHT_KEY = "weight";
    public static String AGE_KEY = "age";
    public static String HEIGHT_KEY = "height";
    public static String IP_KEY = "ip";
    public static String PHP_URL_KEY="php";

    public DBManager(Context context) {
        _context = context;
        _userDB = openDatabase("meta");
        _dataDB = openDatabase("bulk");
        if (_userDB == null) {
            Log.e(TAG, " Failed to open user Database");
        }
        if (_dataDB == null) {
            Log.e(TAG, " Failed to open user Database");
        }
    }

    /***
     * @param type of the data base
     * @return returns the the instance of that type
     */
    public Database getDatabase(String type) {
        if (type.equals("user")) {
            return _userDB;
        } else if (type.equals("data")) {
            return _dataDB;
        }

        return null;
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

    public JSONArray QueryLastXMinutes(Calendar now, int minutes ) {
        JSONArray lastXseconds = new JSONArray();
        Calendar nearestMinute = DateUtils.round(now, Calendar.MINUTE);
        Query query = _dataDB.createAllDocumentsQuery();
        query.setDescending(true);
        String startKey = String.valueOf(nearestMinute.getTimeInMillis());
        String endKey = String.valueOf(nearestMinute.getTimeInMillis() - android.text.format.DateUtils.MINUTE_IN_MILLIS* minutes);

        try {
            query.setEndKey(endKey);
            query.setStartKey(startKey);
            QueryEnumerator result = query.run();
            for (; result.hasNext(); ) {
                QueryRow row = result.next();
                Map<String, String> tmpMap = (Map) row.getDocument().getProperties();
                if (tmpMap.size() > 3)
                    lastXseconds.put(new JSONObject(tmpMap));
            }
        } catch (CouchbaseLiteException e) {
            //handle this
        }
        return lastXseconds;
    }

    public JSONObject getCurrentDataRow() {
        Calendar now = new GregorianCalendar();
        //because of dataSim, hardcode date
        now.set(2017,02,25);
        Calendar nearestMinute = DateUtils.round(now, Calendar.MINUTE);
        Document doc = _dataDB.getDocument(String.valueOf(nearestMinute.getTimeInMillis()));
        return new JSONObject(doc.getProperties());
    }

    public JSONObject getDataRowAtTime(Calendar time)
    {
        Calendar nearestMinute = DateUtils.round(time, Calendar.MINUTE);
        nearestMinute.set(2017, 02, 25);
        Document doc = _dataDB.getDocument(String.valueOf(nearestMinute.getTimeInMillis()));
        return new JSONObject((doc.getProperties()));
    }

    public Soldier getSoldierDetails() {
        Query query = _userDB.createAllDocumentsQuery();
        QueryEnumerator result;
        try {
            result = query.run();
        } catch (CouchbaseLiteException e) {
            return null;
        }
        try {
            Map<String, Object> tmpMap = result.getRow(0).getDocument().getProperties();
            String id = (String) tmpMap.get(ID_KEY);
            String name = (String) tmpMap.get(NAME_KEY);
            String age = (String)tmpMap.get(AGE_KEY);
            String weight = (String)tmpMap.get(WEIGHT_KEY);
            String height = (String) tmpMap.get(HEIGHT_KEY);
            String ip = (String) tmpMap.get(IP_KEY);
            String url = (String) tmpMap.get(PHP_URL_KEY);

            int iAge = -1, iWeight = -1, iHeight = -1;
            if (!age.equals(""))
                iAge = Integer.valueOf(age);
            if (!weight.equals(""))
                iWeight = Integer.valueOf(weight);
            if(!height.equals(""))
                iHeight = Integer.valueOf(height);
            return new Soldier(id, name, iAge, iWeight, iHeight, ip, url);
        } catch (Exception e) {
            return null;
        }
    }

    public Soldier getDefaultSoldier() {
        return new Soldier("ID", "name", -1, -1, -1, "127.0.0.1", "");
    }

    public void updateSoldier(String id, String newName, String newAge, String newWeight, String newHeight, String newIP ){
        Document doc = _userDB.getDocument(id);
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
                    properties.put(DBManager.ID_KEY, id);
                    properties.put(DBManager.NAME_KEY, newName);
                    properties.put(DBManager.AGE_KEY, newAge);
                    properties.put(DBManager.WEIGHT_KEY, newWeight);
                    properties.put(DBManager.HEIGHT_KEY, newHeight);
                    properties.put(DBManager.IP_KEY, newIP);
                    newRevision.setUserProperties(properties);
                    return true;
                }
            });
        } catch (CouchbaseLiteException e) {

        }

    }

    public void updatePHPURL(String phpURL){
        Document doc = _userDB.getDocument(getSoldierDetails().getSoldierID());
        if (doc != null) {
            try {
                doc.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        Map<String, Object> properties = newRevision.getUserProperties();
                        properties.put(DBManager.PHP_URL_KEY, phpURL);
                        newRevision.setUserProperties(properties);
                        return true;
                    }
                });
            } catch (CouchbaseLiteException e) {

            }
        }

    }

    public String getPHPURL(){
        Soldier s = getSoldierDetails();
        if (s!= null)
            return s.getPhpURL();
        else return "";
    }

    public void deleteSoldier(String id){
        Document doc = _userDB.getDocument(id);
        try {
            doc.delete();
        }catch (CouchbaseLiteException e){

        }
    }

    public void addRow(JSONObject JSONrow, String id){
        Document doc = _dataDB.getDocument(id);
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
                    try {
                        properties.put("timeCreated", JSONrow.getString("DateTime"));
                        properties.put("skinTemp", JSONrow.getString("Skin_Temp"));
                        properties.put("coreTemp", JSONrow.getString("Core_Temp"));
                        properties.put("heartRate", JSONrow.getString("ECG Heart Rate"));
                        properties.put("breathRate", JSONrow.getString("Belt Breathing Rate"));
                    } catch (JSONException e) {
                        //
                    }

                    newRevision.setUserProperties(properties);
                    return true;
                }
            });
        }
        catch (CouchbaseLiteException e){

        }

    }

    public void updateRowState(String id, WelfareStatus nextState){
        Document saveStateDoc = _userDB.getDocument(id);
        try {
            saveStateDoc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
                    properties.put("state", nextState);
                    newRevision.setUserProperties(properties);
                    return true;
                }
            });
        } catch (CouchbaseLiteException e) {
            //handle this
        }
    }

    public void newSoldier(String id, String newName, String newAge, String newWeight, String newHeight, String newIP ){
        Document doc = _userDB.getDocument(id);
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
                    properties.put(DBManager.ID_KEY, id);
                    properties.put(DBManager.NAME_KEY, newName);
                    properties.put(DBManager.AGE_KEY, newAge);
                    properties.put(DBManager.WEIGHT_KEY, newWeight);
                    properties.put(DBManager.HEIGHT_KEY, newHeight);
                    properties.put(DBManager.IP_KEY, newIP);
                    newRevision.setUserProperties(properties);
                    return true;
                }
            });
        } catch (CouchbaseLiteException e) {

        }

    }

}
