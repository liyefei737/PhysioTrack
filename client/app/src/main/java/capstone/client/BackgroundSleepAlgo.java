package capstone.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.Iterator;
import java.util.Map;


/**
 * Background algorithm thread
 */

public class BackgroundSleepAlgo extends Service {

    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundSleepAlgo _backgroundSleepAlgo = null;
    private DBManager databseManager = null;
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
        databseManager = new DBManager(this);
        Database dataDB = databseManager.getDatabase("data");
        QueryEnumerator rows = null;
        try {
            rows = dataDB.createAllDocumentsQuery().run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        if (saa == null) {
            saa = new float[8][];

        }
        for (Iterator<QueryRow> it = rows; it.hasNext(); ) {
            QueryRow row = it.next();
            Document doc = row.getDocument();
            Map<String, Object> result = doc.getProperties();
            if (result.size() > 1) {
                System.out.println(result.values());
                saa[k] = new float[3];
                saa[k][0] = Float.parseFloat((String) result.get("accX"));
                saa[k][1] = Float.parseFloat((String) result.get("accY"));
                saa[k][2] = Float.parseFloat((String) result.get("accZ"));
                k++;
                if (k == 8) {
                    double thres = 1;
                    double val0 = 0.04 * Math.sqrt(Math.pow(saa[0][0], 2) + Math.pow(saa[0][1], 2) + Math.pow(saa[0][2], 2))
                            + 0.04 * Math.sqrt(Math.pow(saa[1][0], 2) + Math.pow(saa[1][1], 2) + Math.pow(saa[1][2], 2))
                            + 0.2 * Math.sqrt(Math.pow(saa[2][0], 2) + Math.pow(saa[2][1], 2) + Math.pow(saa[2][2], 2))
                            + 0.2 * Math.sqrt(Math.pow(saa[3][0], 2) + Math.pow(saa[3][1], 2) + Math.pow(saa[3][2], 2))
                            + 0.2 * Math.sqrt(Math.pow(saa[4][0], 2) + Math.pow(saa[4][1], 2) + Math.pow(saa[4][2], 2))
                            + 0.2 * Math.sqrt(Math.pow(saa[5][0], 2) + Math.pow(saa[5][1], 2) + Math.pow(saa[5][2], 2))
                            + 0.04 * Math.sqrt(Math.pow(saa[6][0], 2) + Math.pow(saa[6][1], 2) + Math.pow(saa[6][2], 2)
                            + 0.04 * Math.sqrt(Math.pow(saa[7][0], 2) + Math.pow(saa[7][1], 2) + Math.pow(saa[7][2], 2)));
                    if (val0 > thres) {
                        System.err.println("awake");
                    } else {
                        System.err.println("sleeping");

                    }
                    k = 0;

                }
            }

        }
        /******************************************************************************
         ************************** Write algorithms here******************************
         * ****************************************************************************
         * */

    }

}


