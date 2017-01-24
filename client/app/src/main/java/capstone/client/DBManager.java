package capstone.client;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Each User has its own database for its simulated data
 * This is refered as "data database" because it store s a user's simulated data
 *  A "data database" is named by the solier ID to relate it to a soldier
 * Also there is a database to to keep track of user information i.e. each user's id, name age....
 *
 */

public class DBManager {

    //log TAGs
    private static final String TAG = "DBManager";

    private Database _userDB = null;
    private Database _data= null;
    private Context _context= null;

    public DBManager(Context context) {
        _context = context;
        _userDB = openDatabase("user");
        // for testing purpose now, later a user should be entered from user input
        Document doc = _userDB.createDocument();
        try {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("sol_id","hardcoded");
            properties.put("current_user","true");
            doc.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        if (_userDB == null){
            Log.e(TAG, " Failed to open user Database" );
        }
    }


    public Database getDatabase(String user_id) {
            return openDatabase(user_id);
        }


    public String getCurrentUserID() {
        String currentUser = null;
        QueryEnumerator rows = null;
        try {
            rows =_userDB.createAllDocumentsQuery().run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        for (Iterator<QueryRow> it = rows; it.hasNext(); ) {
            QueryRow row = it.next();
            Document user = row.getDocument();
            // 1 for current user, 0 for non current user
            if (user.getProperty("current_user").equals("true")){
                currentUser = (String) user.getProperty("id");
                break;
            }
        }
        return currentUser;
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
}
