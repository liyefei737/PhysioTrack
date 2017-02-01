package com.drdc1.medic;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Map;


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

    public static final int PERIOD = 15000;
    int k = 0;
    public float[][] saa;


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

    }
}


