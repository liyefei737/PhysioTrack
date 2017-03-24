package com.drdc1.medic.BackgroundServices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.couchbase.lite.Manager;
import com.drdc1.medic.AppContext;
import com.drdc1.medic.DataManagement.DataManager;

import java.io.IOException;
import java.util.Map;

/**********************************************************************************************
 * TODO: make the server and database running while app is "swiped away" by making it a service
 * TODO: add proper data fields
 ***********************************************************************************************/

public class BackgroundServer extends Service {

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundServer _backgroundServer;
    static private DataManager dataManager;
    private Server server;
    private Manager manager;
    private LocalBroadcastManager broadcaster;

    public BackgroundServer() {
    }

    static public BackgroundServer getBackgroundService() {
        return _backgroundServer;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        dataManager = ((AppContext) this.getApplicationContext()).getDataManager();
        server = new Server(8080, dataManager);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(this.getClass().getSimpleName(),
                "onCreate: Server runing on " + server.getLocalIpAddress() + " Port: " +
                        server.getListeningPort());
        _backgroundServer = this;
        broadcaster = LocalBroadcastManager.getInstance(this);

        return START_STICKY;
    }

    public void notifyUI(Map<String, Object> document) {
        Intent intent = new Intent(DB_UPDATE);
        for (String key : document.keySet()) {
            intent.putExtra(key, document.get(key).toString());
        }
        broadcaster.sendBroadcast(intent);
    }

}
