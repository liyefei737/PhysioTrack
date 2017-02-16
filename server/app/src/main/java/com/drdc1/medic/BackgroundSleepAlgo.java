package com.drdc1.medic;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import org.json.JSONArray;

import java.util.Date;
import java.util.Map;

import sleepS.DateStatePair;
import sleepS.sleepStatus;

/**
 * Background algorithm thread
 */

public class BackgroundSleepAlgo extends Service {

    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundSleepAlgo _backgroundSleepAlgo = null;
    private DataManager dataManager = null;
    private LocalBroadcastManager broadcaster = null;

    //singleton to to pass an instance of BackgroundSleepAlgo
    static public BackgroundSleepAlgo get_backgroundSleepAlgo() {
        return _backgroundSleepAlgo;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _backgroundSleepAlgo = this;
        broadcaster = LocalBroadcastManager.getInstance(this);

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("MyCustomService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new Handler(mHandlerThread.getLooper());

        dataManager = ((Application) this.getApplication()).getDataManager();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                run_algo();
            }
        });
        // Keep service around "sticky"
        return START_STICKY;
    }

    public void notifyUI(Map<String, Object> document) {
        Intent intent = new Intent(DB_UPDATE);
        for (String key : document.keySet()) {
            intent.putExtra(key, document.get(key).toString());
        }
        broadcaster.sendBroadcast(intent);
    }

    private void run_algo() {

        Date now;
        int numSeconds = 9, millistep = 1000;
        JSONArray lastXSeconds;
        while (true) {
            try {
                Thread.sleep(numSeconds * 1000);
            } catch (Exception e) {
                //
            }

            now = new Date();
            Map<String, Database> physioDataMap = dataManager.getPhysioDataMap();
            if (physioDataMap != null) {
                for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
                    Database userDB = entry.getValue();
                    lastXSeconds = dataManager.QueryLastXSeconds(entry.getKey(), now, numSeconds, millistep);
                    final DateStatePair sleepResult = sleepStatus.CalculateSleepStatus(lastXSeconds);
                    if (sleepResult.getDate() != null) {
                        Document saveStateDoc = userDB.getDocument(sleepResult.getDate());
                        try {
                            saveStateDoc.update(new Document.DocumentUpdater() {
                                @Override
                                public boolean update(UnsavedRevision newRevision) {
                                    Map<String, Object> properties = newRevision.getUserProperties();
                                    properties.put("sleep", sleepResult.getState());
                                    newRevision.setUserProperties(properties);
                                    return true;
                                }
                            });
                        } catch (CouchbaseLiteException e) {
                            //handle this
                        }
                    }
                }
            }
        }
    }
}


