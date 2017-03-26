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
import com.drdc1.medic.DataStructUtils.HelperMethods;
import com.drdc1.medic.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import welfareSM.WelfareStatus;
import welfareSM.WelfareTracker;

/**
 * Background Thread for computing the wellness algorithm
 */

public class BackgroundWellnessAlgo extends Service {
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundWellnessAlgo _BackgroundWellnessAlgo = null;
    static private List<Integer> hrRange;
    static private List<Integer> brRange;
    static private List<Float> stRange;
    static private List<Float> ctRange;

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
        if (intent != null) {
            if (intent.getExtras() != null) {
                hrRange = intent.getExtras().getIntegerArrayList("hrRange");
                brRange = intent.getExtras().getIntegerArrayList("brRange");
                stRange =
                        HelperMethods.arrayToFloatList(intent.getExtras().getFloatArray("stRange"));
                ctRange =
                        HelperMethods.arrayToFloatList(intent.getExtras().getFloatArray("ctRange"));

            }
        }

        Timer timer = new Timer();
        TimerTask doWellnessAlgoCallback = new TimerTask() {
            @Override
            public void run() {
                mServiceHandler.post(new Runnable() {
                    public void run() {
                        try {

                            calculateWellness(dataManager, getApplicationContext());
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

    public static void calculateWellness(DataManager dataManager, Context context) {
        JSONArray lastMinute;
        Calendar now = new GregorianCalendar();
        now.set(2017, 02, 25); //hard code for sim data
        int numMinutes = 1;

        Map<String, Database> physioDataMap = dataManager.getPhysioDataMap();
        int numSoldiers = dataManager.getNumSoldiers();
        Map<String, String> overallWithID = new HashMap<>();
        String [] overall = new String[numSoldiers];
        String [] skin = new String[numSoldiers];
        String [] core = new String[numSoldiers];

        if (physioDataMap != null) {
            int i = 0;
            for (Map.Entry<String, Database> entry : physioDataMap.entrySet()) {
                String id = entry.getKey();
                WelfareTracker wt = dataManager.getWellnessTracker(id);
                lastMinute = dataManager.QueryLastXMinutes(id, now, numMinutes);

                if (wt == null){
                    wt = new WelfareTracker();
                }

                if (hrRange != null) {
                    wt.setPhysioParamThresholds(hrRange, brRange, stRange, ctRange);
                }

                if (lastMinute.length() != 0) {
                    Object[] statusResults = wt.calculateWelfareStatus(lastMinute);
                    WelfareStatus nextState = (WelfareStatus) statusResults[1];
                    try {
                        dataManager.updateState(id, ((JSONObject) lastMinute.get(0)).getString("_id"), nextState);
                    } catch (Exception e) {

                    }
                    dataManager.saveWellnessTracker(id, wt);
                    overall[i] = nextState.toString();
                    overallWithID.put(id, nextState.toString());
                    skin[i] = ((WelfareStatus) ((Map) statusResults[0]).get("SKIN")).toString();
                    core[i] = ((WelfareStatus) ((Map) statusResults[0]).get("CORE")).toString();
                    i++;
                }
            }
            if (i > 0) {
                String intentStr = context.getResources().getString(R.string.bulls_eye_update);
                Intent intent = new Intent(intentStr);
                intent.setAction(intentStr);
                intent.putExtra("OVERALL", overall);
                intent.putExtra("SKIN", skin);
                intent.putExtra("CORE", core);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                String intentStr2 = "NAMELIST_OVERALL";
                Intent intent2 = new Intent(intentStr);
                intent2.setAction(intentStr2);
                for (Map.Entry<String, String> entry : overallWithID.entrySet())
                    intent2.putExtra(entry.getKey(), entry.getValue());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
            }
        }
    }
}
