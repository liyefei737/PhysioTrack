package capstone.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

/**
 * Created by Grace on 2017-01-08.
 */

public class BackgroundDataSim extends Service {

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundDataSim _backgroundDataSim;
    private Database database;
    private Manager manager;
    private LocalBroadcastManager broadcaster;

    public BackgroundDataSim() {
    }

    static public BackgroundDataSim get_backgroundDataSim(){return _backgroundDataSim;}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        openDatabase("development");
        _backgroundDataSim = this;
        broadcaster = LocalBroadcastManager.getInstance(this);
        //sit and spin on remote db.
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }

    public void notifyUI(Map<String,Object> document) {
        Intent intent = new Intent(DB_UPDATE);
        for(String key :document.keySet()){
            intent.putExtra(key,document.get(key).toString());
        }
        broadcaster.sendBroadcast(intent);
    }

    private void openDatabase(String dbName) {
        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);
        try {
            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            database = manager.openDatabase(dbName, options);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        //triger event when there is a change to db e.g. to update a UI
        database.addChangeListener(new Database.ChangeListener() {
            @Override
            public void changed(Database.ChangeEvent event) {
                for(DocumentChange dc:event.getChanges()){
                    Log.i(this.getClass().getSimpleName(), "Document changed: "+ dc.getDocumentId());
                    notifyUI(database.getDocument(dc.getDocumentId()).getProperties());
                }
            }
        });
    }
}

