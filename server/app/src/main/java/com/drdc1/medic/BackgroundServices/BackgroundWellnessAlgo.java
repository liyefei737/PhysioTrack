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
import com.drdc1.medic.DataStructUtils.HelperMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
                String id = entry.getKey();
                WelfareTracker wt = dataManager.getWellnessTracker(id);
                lastMinute = dataManager.QueryLastXMinutes(id, now, numMinutes);

                if (wt == null){
                    wt = new WelfareTracker();;
                }

                if (hrRange != null) {
                    wt.setPhysioParamThresholds(hrRange, brRange, stRange, ctRange);
                }

                Object[] statusResults = wt.calculateWelfareStatus(lastMinute);
                WelfareStatus nextState = (WelfareStatus) statusResults[1];
                try {
                    dataManager.updateState(id, ((JSONObject) lastMinute.get(0)).getString("_id"), nextState);
                }
                catch (Exception e){

                }
                dataManager.saveWellnessTracker(id, wt);

            }
        }
    }
}
