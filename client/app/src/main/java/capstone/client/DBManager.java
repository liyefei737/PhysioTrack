package capstone.client;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    SimpleDateFormat dateFormat = new SimpleDateFormat("01/30/2017 HH:mm:ss.");

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
     *
     * @param type of the data base
     * @return returns the the instance of that type
     */
    public Database getDatabase(String type) {
        if(type.equals("user")){
            return _userDB;
        }else if(type.equals("data")){
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

    public String GetQueryStartKey(Date now, int millistep){

        int millis = (int) (Math.ceil((double)((now.getTime() %1000) /(float)millistep)) * millistep);
        return dateFormat.format(now) + String.format("%03d", millis);

    }

    public String GetQueryEndKey(Date now, int seconds, int millistep){

       Date XsecondsAgo = new Date();
        XsecondsAgo.setTime(now.getTime() - (seconds * 1000));
        int millis = (int) (Math.ceil((double)((now.getTime() %1000) /(float)millistep)) * millistep);
        return dateFormat.format(XsecondsAgo) + String.format("%03d", millis);
    }


    public JSONArray QueryLastXSeconds(Date now, int seconds, int millistep ) {
        JSONArray lastXseconds = new JSONArray();
        Query query = _dataDB.createAllDocumentsQuery();
        query.setDescending(false);
        String startKey = GetQueryStartKey(now, millistep);
        String endKey = GetQueryEndKey(now, seconds, millistep);

        try {
            query.setEndKeyDocId(endKey);
            query.setStartKeyDocId(startKey);
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
}
