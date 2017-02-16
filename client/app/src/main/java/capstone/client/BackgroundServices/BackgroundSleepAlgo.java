package capstone.client.BackgroundServices;

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

import capstone.client.DBManager;
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
    private DBManager dbManager = null;
    private LocalBroadcastManager broadcaster = null;

    public static final int PERIOD = 15000;

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
        mHandlerThread = new HandlerThread("SleepAlgoService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dbManager = new DBManager(this);
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
        Database dataDB = dbManager.getDatabase(dbManager.DATA_DB);
        Calendar now = new GregorianCalendar();
        now.set(2017, 01, 30); //hardcode for datasim
        JSONArray last9Minutes = dbManager.QueryLastXMinutes(now, 9);
        if (last9Minutes.length() == 9){
            final DateStatePair sleepResult = sleepStatus.CalculateSleepStatus(last9Minutes);
            if (sleepResult.getDate() != null) {
                long docID = org.apache.commons.lang3.time.DateUtils.round(sleepResult.getDate(), Calendar.MINUTE).getTime();
                Document saveStateDoc = dataDB.getDocument(String.valueOf(docID));
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


