package capstone.client.BackgroundServices;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import capstone.client.DBManager;

import static welfareSM.WelfareStatus.GREY;

/**
 * Background server that queries data and send to UI
 */

public class BackgroundUIUpdator extends Service {
    private DBManager dbManager;

    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("UIUpdaterService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dbManager = new DBManager(this);
        Timer timer = new Timer();
        TimerTask doUIUpdateCallback = new TimerTask() {
            @Override
            public void run() {
                mServiceHandler.post(new Runnable() {
                    public void run() {
                        try {
                            updateDataAndBroadcast();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doUIUpdateCallback, 0, DateUtils.MINUTE_IN_MILLIS); //execute every minute

        // Keep service around "sticky"
        return START_STICKY;
    }

    public void updateDataAndBroadcast(){
        int num_data_pts = 10;
        float [] coreTemp = new float[num_data_pts];
        float [] skinTemp = new float[num_data_pts];
        int [] br = new int[num_data_pts];
        int [] hr = new int[num_data_pts];
        String state = GREY.toString();
        //hardcode for simdata
        Calendar date = new GregorianCalendar();
        date.set(2017, 01, 30);
        JSONArray last10Minutes = dbManager.QueryLastXMinutes(date, 10);

        if (last10Minutes.length() != 0) {


            for (int i = 0; i < last10Minutes.length(); i++) {
                try {
                    JSONObject jsonRow = last10Minutes.getJSONObject(i);
                    br[i] = Integer.valueOf(jsonRow.getString("breathRate"));
                    hr[i] = Integer.valueOf(jsonRow.getString("heartRate"));
                    coreTemp[i] = Float.valueOf(jsonRow.getString("coreTemp"));
                    skinTemp[i] = Float.valueOf(jsonRow.getString("skinTemp"));
                } catch (JSONException e) {

                }
            }
            try {
                JSONObject lastRow = last10Minutes.getJSONObject(last10Minutes.length() - 1);
                 state = lastRow.getString("state");
            } catch (JSONException e){

            }
            Intent i = new Intent();
            i.setAction("UI_UPDATE");
            i.putExtra("coreTemp", coreTemp);
            i.putExtra("skinTemp", skinTemp);
            i.putExtra("br", br);
            i.putExtra("hr", hr);
            i.putExtra("state", state);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
        }

    }
}
