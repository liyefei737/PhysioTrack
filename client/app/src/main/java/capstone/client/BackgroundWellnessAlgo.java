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
import com.couchbase.lite.UnsavedRevision;

import org.json.JSONArray;

import java.util.Date;
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
        JSONArray lastXseconds;
        Date now;
        int numSeconds = 15, millistep = 160;

        while (true) {

            try {
                Thread.sleep(numSeconds *1000);
            }
            catch (Exception e)
            {
                //
            }
            now = new Date();
            lastXseconds = dbManager.QueryLastXSeconds(now, numSeconds, millistep);

            final WelfareStatus nextState = iwt.calculateWelfareStatus(lastXseconds);

            Document saveStateDoc = userDB.getDocument(dbManager.GetQueryStartKey(now, millistep));
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
            } catch (CouchbaseLiteException e){
                //handle this
            }

        }
    }
}
