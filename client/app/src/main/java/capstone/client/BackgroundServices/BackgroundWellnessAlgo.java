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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import capstone.client.DataManagement.DBManager;
import capstone.client.DRDCClient;
import welfareSM.WelfareTracker;
import welfareSM.WelfareStatus;

/**
 * Background Thread for computing the wellness algorithm
 */

public class BackgroundWellnessAlgo extends Service {
    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static final String STATUS_UPDATE = "STATUS_UPDATE";
    static private BackgroundWellnessAlgo _BackgroundWellnessAlgo = null;
    private DBManager dbManager = null;

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

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("WellnessAlgoService.HandlerThread");
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

    public void notifyUI(WelfareStatus state) {
        Intent intent = new Intent();
        intent.setAction("capstone.client.BackgroundWellnessAlgo.STATUS_UPDATE");
        intent.putExtra(STATUS_UPDATE, state.toString());
        sendBroadcast(intent);
    }

    public void calculateWellness() {
        WelfareTracker wt = ((DRDCClient) this.getApplication()).getWelfareTracker();
        Database userDB = dbManager.getDatabase(DBManager.DATA_DB);
        Calendar now = new GregorianCalendar();
        now.set(2017, 02, 25); //hardcode for datasim
        JSONArray last5Minutes = dbManager.QueryLastXMinutes(now, 5);
        Object[] thearray = wt.calculateWelfareStatus(last5Minutes);
        final WelfareStatus nextState = (WelfareStatus) thearray[1];
        HashMap<String, WelfareStatus> hmap = (HashMap<String, WelfareStatus>) thearray[0];

        Iterator it = hmap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue() == WelfareStatus.YELLOW || pair.getValue() == WelfareStatus.RED) {


                Intent intent = new Intent("yelloworgreen");
                // You can also include some extra data.
                intent.putExtra("key", (String) pair.getKey());
                String status = ((WelfareStatus) pair.getValue()).toString();
                intent.putExtra("color", status);
//                Bundle b = new Bundle();
//                b.putParcelable("Location", l);
//                intent.putExtra("Location", b);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


            }
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

        Calendar nearestMinute =
                org.apache.commons.lang3.time.DateUtils.round(now, Calendar.MINUTE);
        Document saveStateDoc = userDB.getDocument(String.valueOf(nearestMinute.getTimeInMillis()));
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
        } catch (CouchbaseLiteException e) {
            //handle this
        }
        notifyUI(nextState);
        ((DRDCClient) this.getApplication()).setLastState(nextState);

    }
}
