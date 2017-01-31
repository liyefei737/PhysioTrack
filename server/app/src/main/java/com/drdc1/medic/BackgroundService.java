package com.drdc1.medic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.couchbase.lite.Manager;

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
    static private DBManager dbManager;
    private Server server;
    private Manager manager;
    private LocalBroadcastManager broadcaster;

    public BackgroundService() {
    }

    static public BackgroundService getBackgroundService(){return _backgroundService;}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbManager = new DBManager(this);
        server = new Server(8080, dbManager);
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

}
