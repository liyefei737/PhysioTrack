package com.drdc1.medic.BackgroundServices;

import android.app.Service;
import android.content.Context;
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
import java.util.HashMap;
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

    private static int MINUTES_IN_DAY = 60*24;
    private static double PERCENT_FAT_PER_MINUTE = 100.0/MINUTES_IN_DAY;
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    private DataManager dataManager = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

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
        timer.schedule(doSleepRescoreCallback, DateUtils.MINUTE_IN_MILLIS*30 , DateUtils.MINUTE_IN_MILLIS*30 ); //execute every 30 minute

        TimerTask doFatigueCalcCallback = new TimerTask() {
            @Override
            public void run() {
                mServiceHandler.post(new Runnable() {
                    public void run() {
                        try {
                            run_fatigue_calc(getApplicationContext());
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doFatigueCalcCallback, 0, DateUtils.MINUTE_IN_MILLIS*15 ); //execute every 15 minute


        // Keep service around "sticky"
        return START_STICKY;
    }

    private void run_sleep_algo() {
        int numMinutes = 10;
        Calendar now = new GregorianCalendar();
        now.set(2017, 02, 25); // hard code for sim data
        JSONArray last7Minutes;
        Map<String, Database> physioDataMap = dataManager.getPhysioDataMap();
        if (physioDataMap != null) {
            for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
                last7Minutes = dataManager.QueryLastXMinutes(entry.getKey(), now, numMinutes);
                final DateStatePair sleepResult = CalculateSleepStatus(last7Minutes);
                if (sleepResult.getDate() != null && sleepResult.getState() != null) {
                    dataManager.updateSleep(entry.getKey(), sleepResult.getDate(), sleepResult.getState());
                }
            }
        }

    }

    private void run_sleep_rescorer() {
        int numMinutes = 30;
        Calendar now = new GregorianCalendar();
        now.set(2017, 02, 25); // hard code for sim data
        now.add(Calendar.MINUTE, -7);
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
                            dataManager.updateSleep(soldierID, min.getString("_id"), (SleepState) min.get("sleep"));
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }
    }

    private void run_fatigue_calc(Context context) {
        Map<String, Double> idFatigueMap = new HashMap<>();
        Map<String, Database> physioDataMap = dataManager.getPhysioDataMap();
        if (physioDataMap != null) {
            for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
                String soldierID = entry.getKey();
                double minutesAsleep = dataManager.getSleepPercentageOfSoldier(soldierID);
                double totalMinutes = dataManager.getTotalMinutesActiveOfSoldier(soldierID);

                double fatigue = (totalMinutes - minutesAsleep) * PERCENT_FAT_PER_MINUTE;
                idFatigueMap.put(entry.getKey(), fatigue);

            }
            if (idFatigueMap.size() != 0) {
                String intentStr = "NAMELIST_SLEEP";
                Intent intent = new Intent(intentStr);
                intent.setAction(intentStr);
                for (Map.Entry<String, Double> entry : idFatigueMap.entrySet()) {
                    intent.putExtra(entry.getKey(), (int) Math.floor(entry.getValue()));
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
    }
}


