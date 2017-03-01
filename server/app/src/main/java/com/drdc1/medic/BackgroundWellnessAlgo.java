package com.drdc1.medic;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import welfareSM.WelfareStatus;

/**
 * Background Thread for computing the wellness algorithm
 */

public class BackgroundWellnessAlgo extends Service {
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundWellnessAlgo _BackgroundWellnessAlgo = null;
    private DataManager dataManager = null;
    private LocalBroadcastManager broadcaster = null;

    //singleton to to pass an instance of BackgroundWellnessAlgo
    static public BackgroundWellnessAlgo get_BackgroundWellnessAlgo() {
        return _BackgroundWellnessAlgo;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _BackgroundWellnessAlgo = this;
        broadcaster = LocalBroadcastManager.getInstance(this);

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("MyCustomService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new Handler(mHandlerThread.getLooper());

        dataManager = ((AppContext) this.getApplication()).getDataManager();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        TimerTask doWellnessAlgoCallback = new TimerTask() {
            @Override
            public void run() {
                mServiceHandler.post(new Runnable() {
                    public void run() {
                        try {
                            calculateWellness();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doWellnessAlgoCallback, 0,
                DateUtils.MINUTE_IN_MILLIS); //execute every minute

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

    public void calculateWellness() {
        JSONArray lastMinute;
        Calendar now = new GregorianCalendar();
        now.set(2017, 02, 25); //hard code for sim data
        int numMinutes = 1;

        Map<String, Database> physioDataMap = dataManager.getPhysioDataMap();
        if (physioDataMap != null) {
            for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
                Database userDB = entry.getValue();

                lastMinute = dataManager.QueryLastXMinutes(entry.getKey(), now, numMinutes);

                final WelfareStatus nextState = dataManager.getWellnessTracker(entry.getKey())
                        .calculateWelfareStatus(lastMinute);

                Document saveStateDoc = userDB.getDocument(entry.getKey());
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
        }
    }
}
