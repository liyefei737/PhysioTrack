package com.drdc1.medic.BackgroundServices;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;

import com.couchbase.lite.Database;
import com.drdc1.medic.AppContext;
import com.drdc1.medic.DataManagement.DataManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sleepS.DateStatePair;
import sleepS.SleepAlgo;
import sleepS.SleepState;

import static sleepS.SleepAlgo.CalculateSleepStatus;

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

        dataManager = ((AppContext) this.getApplication()).getDataManager();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        TimerTask doSleepCallback = new TimerTask() {
            @Override
            public void run() {
                mServiceHandler.post(new Runnable() {
                    public void run() {
                        try {
                            run_sleep_algo();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doSleepCallback, 0, DateUtils.MINUTE_IN_MILLIS); //execute every minute


        TimerTask doSleepRescoreCallback = new TimerTask() {
            @Override
            public void run() {
                mServiceHandler.post(new Runnable() {
                    public void run() {
                        try {
                            run_sleep_rescorer();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doSleepRescoreCallback, 0, DateUtils.MINUTE_IN_MILLIS * 30); //execute every minute

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

    private void run_sleep_algo() {
        int numMinutes = 7;
        Calendar now = new GregorianCalendar();
        now.set(2017, 02, 25); // hard code for sim data
        JSONArray last7Minutes;
        Map<String, Database> physioDataMap = dataManager.getPhysioDataMap();
        if (physioDataMap != null) {
            for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
                last7Minutes = dataManager.QueryLastXMinutes(entry.getKey(), now, numMinutes);
                final DateStatePair sleepResult = CalculateSleepStatus(last7Minutes);
                if (sleepResult.getDate() != null) {
                    dataManager.updateSleep(entry.getKey(), sleepResult.getDate(), sleepResult.getState());
                }
            }
        }

    }

    private void run_sleep_rescorer() {
        int numMinutes = 30;
        Calendar now = new GregorianCalendar();
        now.set(2017, 02, 25); // hard code for sim data
        JSONArray last30Minutes;
        Map<String, Database> physioDataMap = dataManager.getPhysioDataMap();
        if (physioDataMap != null) {
            for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
                String soldierID = entry.getKey();
                last30Minutes = dataManager.QueryLastXMinutes(soldierID, now, numMinutes);
                JSONArray rescored30Minutes = SleepAlgo.RescoreSleep(last30Minutes);
                if (rescored30Minutes != null) {
                    for (int i = 0; i < rescored30Minutes.length(); i++) {
                        try {
                            JSONObject min = rescored30Minutes.getJSONObject(i);
                            dataManager.updateSleep(soldierID, min.getString("id"), (SleepState) min.get("sleep"));
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }


    }
}


