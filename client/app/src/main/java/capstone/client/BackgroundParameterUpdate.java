package capstone.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.couchbase.lite.Database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class BackgroundParameterUpdate extends Service {
    public static String HEART = "HEART";
    public static String BREATH = "BREATH";
    public static String CORE = "CORE";
    public static String SKIN = "SKIN";
    DBManager dbManager;

    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static private BackgroundWellnessAlgo _BackgroundWellnessAlgo = null;

    public BackgroundParameterUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void notifyUI(String hr, String action) {
        Intent intent = new Intent();
        intent.setAction("capstone.client.BackgroundParameterUpdate.PARAM_UPDATE");
        intent.putExtra(action + "_UPDATE", hr);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("ParamUpdateService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        final String action = intent.getAction();
        //int seconds = intent.getIntExtra("SECONDS", 60);
        dbManager = new DBManager(this);
        final int seconds = 5;
        if (!action.equals(HEART) && !action.equals(BREATH) && !action.equals(CORE) && !action.equals(SKIN)) {
            stopSelf();
        } else {
            mServiceHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateRate(seconds, action);
                }
            });
        }
        return START_STICKY;
    }

    private void updateRate(int updateSeconds, String action) {

        while (true) {

            Database db = dbManager.getDatabase(dbManager.DATA_DB);
            Date now = new Date();
            JSONArray jArray = dbManager.QueryLastXSeconds(now, 1, 0);
            JSONObject jObj = new JSONObject();
            String paramVal = "";
            try {
                jObj = jArray.getJSONObject(jArray.length() - 1);
            }catch (Exception e){

            }
            try {
                if (action.equals(HEART)) {
                    paramVal = jObj.getString("heartRate");
                    ((DRDCClient) getApplication()).setLastHeartRate(paramVal);
                } else if (action.equals(BREATH)) {
                    paramVal = jObj.getString("breathRate");
                    ((DRDCClient) getApplication()).setLastBreathingRate(paramVal);
                } else if (action.equals(CORE)) {
                    paramVal = jObj.getString("coreTemp");
                    ((DRDCClient) getApplication()).setLastCoreTemp(paramVal);
                } else if (action.equals(SKIN)) {
                    paramVal = jObj.getString("skinTemp");
                    ((DRDCClient) getApplication()).setLastSkinTemp(paramVal);
                }
            }catch (Exception e){

            }
            if (paramVal != null && !paramVal.isEmpty()) {
                notifyUI(paramVal, action);
            }
            try {
                Thread.sleep(updateSeconds * 1000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mHandlerThread.interrupt();
        mHandlerThread.quit();
    }
}

