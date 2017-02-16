package capstone.client;

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
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

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
    public String ID_KEY = "id";
    public String NAME_KEY = "name";
    public String WEIGHT_KEY = "weight";
    public String AGE_KEY = "age";
    public String HEIGHT_KEY = "height";

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
//        //triger event when there is a change to db e.g. to update a UI
//        database.addChangeListener(new Database.ChangeListener() {
//            @Override
//            public void changed(Database.ChangeEvent event) {
//                for(DocumentChange dc:event.getChanges()){
//                    Log.i(this.getClass().getSimpleName(), "Document changed: "+ dc.getDocumentId());
//                    notifyUI(database.getDocument(dc.getDocumentId()).getProperties());
//                }
//            }
//        });
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
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
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
        now.set(2017,01,30);
        Calendar nearestMinute = DateUtils.round(now, Calendar.MINUTE);
        Document doc = _dataDB.getDocument(String.valueOf(nearestMinute.getTimeInMillis()));
        JSONObject jObj = new JSONObject(doc.getProperties());
        return jObj;
    }

    public JSONObject getDataRowAtTime(Calendar time)
    {
        Calendar nearestMinute = DateUtils.round(time, Calendar.MINUTE);
        nearestMinute.set(2017, 01, 30);
        Document doc = _dataDB.getDocument(String.valueOf(nearestMinute.getTimeInMillis()));
        JSONObject jObj = new JSONObject((doc.getProperties()));
        return jObj;
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
            int age = (Integer)tmpMap.get(AGE_KEY);
            int weight = (Integer) tmpMap.get(WEIGHT_KEY);
            int height = (Integer) tmpMap.get(HEIGHT_KEY);
            Soldier s = new Soldier(id, name, age, weight, height);
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    public void initDefaultSoldierDetails() {
        Document doc = _userDB.getDocument("default");
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
                    properties.put(HEIGHT_KEY, 5);
                    properties.put(WEIGHT_KEY, 5);
                    properties.put(NAME_KEY, "a a");
                    properties.put(AGE_KEY, 5);
                    newRevision.setUserProperties(properties);
                    return true;
                }
            });
        } catch (CouchbaseLiteException e){
            //handle this
        }
    }

    public void setSoldierDetails(Soldier soldier) {

    }
}
