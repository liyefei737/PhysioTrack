package com.drdc1.medic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.Map;


/**********************************************************************************************
 * TODO: make the server and database running while app is "swiped away" by making it a service
 * TODO: add proper data fields
 *
 ***********************************************************************************************/

public class BackgroundService extends Service {

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundService _backgroundService;
    private Database database;
    private Server server;
    private Manager manager;
    private LocalBroadcastManager broadcaster;

    public BackgroundService() {
    }

    static public BackgroundService get_backgroundService(){return _backgroundService;}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        openDatabase("development");
        server = new Server(8080);
        server.setDatabaseInstance(database);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(this.getClass().getSimpleName(), "onCreate: Server runing on "+server.getLocalIpAddress()+" Port: " +server.getListeningPort());
        _backgroundService = this;
        broadcaster = LocalBroadcastManager.getInstance(this);

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
