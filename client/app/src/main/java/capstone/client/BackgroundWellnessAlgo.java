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
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import welfareSM.IndividualWelfareTracker;
import welfareSM.WelfareStatus;

/**
 * Background Thread for computing the wellness algorithm
 */

public class BackgroundWellnessAlgo  extends Service {
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundWellnessAlgo _BackgroundWellnessAlgo = null;
    private DBManager dbManager = null;
    private LocalBroadcastManager broadcaster = null;

    public static final int PERIOD = 15000;

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
        query.setDescending(false);
        while (true) {
            Date now = new Date();
            //now.setTime(now.getTime() - (1800000)); //half an hour ago because simulater is slow
            int millis = (int)(Math.ceil((double)((now.getTime() % 1000)/40)) * 40);

            String startKey = keyFormat.format(now) + String.format("%03d",millis) ;
            now.setTime(now.getTime() - 1000);
            String endKey = keyFormat.format(now)+ String.format("%03d",millis);
            try {
                query.setEndKeyDocId(String.valueOf(keyFormat.parse(startKey).getTime()));
                query.setStartKeyDocId(String.valueOf(keyFormat.parse(endKey).getTime()));
            }
            catch(Exception e){
                //all docs query
            }
            try {
                QueryEnumerator result = query.run();
                for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                    QueryRow row = it.next();
                    Map<String, String> tmpMap = (Map) row.getDocument().getProperties();
                    last15seconds.put(new JSONObject(tmpMap));
                }
            } catch (CouchbaseLiteException e) {
                //handle this
            }
            WelfareStatus nextState = iwt.calculateWelfareStatus(last15seconds);
            try {
                Thread.sleep(15000);
            }
            catch (Exception e)
            {
                //
            }
        }
    }
}
