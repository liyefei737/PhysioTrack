package capstone.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import welfareSM.IndividualWelfareTracker;
import welfareSM.WelfareStatus;

import static capstone.client.BackgroundDataSim.DB_UPDATE;

/**
 * Created by Grace on 2017-01-25.
 */

public class BackgroundAlgo extends Service {
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    private DBManager dbManager = null;
    static private BackgroundAlgo _backgroundAlgo = null;
    private LocalBroadcastManager broadcaster = null;

    public static final int PERIOD = 15000;

    public BackgroundAlgo() {
    }

    //singleton to
    static public BackgroundAlgo get_backgroundAlgo() {
        return _backgroundAlgo;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _backgroundAlgo = this;
        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("Algo.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new Handler(mHandlerThread.getLooper());
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                calculateWellness();
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

    public void calculateWellness() {
        IndividualWelfareTracker iwt = new IndividualWelfareTracker();
        dbManager = new DBManager(this);
        Database userDB = dbManager.getDatabase("data");
        SimpleDateFormat keyFormat = new SimpleDateFormat("01/24/2017 HH:mm:ss.SSS");
        JSONArray last15seconds = new JSONArray();

        Query query = userDB.createAllDocumentsQuery();
        query.setDescending(true);
        Date now = new Date();
        String startKey = keyFormat.format(now);
        now.setTime(now.getTime() - 15000);
        String endKey = keyFormat.format(now);
        query.setStartKey(new String(startKey));
        query.setEndKey(new String(endKey));
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                last15seconds.put(row.getDocument().toString());
            }
        } catch (CouchbaseLiteException e) {
            //handle this
        }
        WelfareStatus nextState = iwt.calculateWelfareStatus(last15seconds);
    }

    /*public void calculateSleep() {
        IndividualWelfareTracker iwt = new IndividualWelfareTracker();
        dbManager = new DBManager(this);
        Database userDB = dbManager.getDatabase("data");
        SimpleDateFormat keyFormat = new SimpleDateFormat("01/24/2017 HH:mm:ss.SSS");
        JSONArray last8seconds = new JSONArray();

        Query query = userDB.createAllDocumentsQuery();
        query.setDescending(true);
        Date now = new Date();
        String startKey = keyFormat.format(now);
        now.setTime(now.getTime() - 8000);
        String endKey = keyFormat.format(now);
        query.setStartKey(new String(startKey));
        query.setEndKey(new String(endKey));
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                last8seconds.put(row.getDocument().toString());
            }
        } catch (CouchbaseLiteException e) {
        }
        int thres = 1;
        int val0 = 0.04 * Math.sqrt(Math.pow(last7seconds.getJSONObject(n - 4).x, 2) + Math.pow(last7seconds.getJSONObject(n - 4).y, 2) + Math.pow(last7seconds.getJSONObject(n - 4).z, 2))
                + 0.04 * Math.sqrt(Math.pow(last7seconds.getJSONObject(n - 3).x, 2) + Math.pow(last7seconds.getJSONObject(n - 3).y, 2) + Math.pow(last7seconds.getJSONObject(n - 3).z, 2))
                + 0.2 * Math.sqrt(Math.pow(last7seconds.getJSONObject(n - 2).x, 2) + Math.pow(last7seconds.getJSONObject(n - 2).y, 2) + Math.pow(last7seconds.getJSONObject(n - 2).z, 2))
                + 0.2 * Math.sqrt(Math.pow(last7seconds.getJSONObject(n - 1).x, 2) + Math.pow(last7seconds.getJSONObject(n - 1).y, 2) + Math.pow(last7seconds.getJSONObject(n - 1).z, 2))
                + 0.2 * Math.sqrt(Math.pow(last7seconds.getJSONObject(n).x, 2) + Math.pow(last7seconds.getJSONObject(n).y, 2) + Math.pow(last7seconds.getJSONObject(n).z, 2))
                + 0.2 * Math.sqrt(Math.pow(last7seconds.getJSONObject(n + 1).x, 2) + Math.pow(last7seconds.getJSONObject(n + 1).y, 2) + Math.pow(last7seconds.getJSONObject(n + 1).z, 2))
                + 0.04 * Math.sqrt(Math.pow(last7seconds.getJSONObject(n + 2).x, 2) + Math.pow(last7seconds.getJSONObject(n + 2).y, 2) + Math.pow(last7seconds.getJSONObject(n + 2).z, 2)
                + 0.04 * Math.sqrt(Math.pow(last7seconds.getJSONObject(n + 3).x, 2) + Math.pow(last7seconds.getJSONObject(n + 3).y, 2) + Math.pow(last7seconds.getJSONObject(n + 3).z, 2)));

//        for (int x = 0; x < 8; x = x + 1) {
//            JSONArray last7seconds = new JSONArray();
//            for (int k = 0; k < 7; k = k + 1) {
//                last7seconds.put(last7seconds.getJSONObject(k));
//            }
//            last7seconds
//        }

        if (val0 > thres) {
            System.err.println("awake");
        } else {
            System.err.println("sleeping");

        }

        WelfareStatus nextState = iwt.calculateWelfareStatus(last15seconds);
    }*/
}