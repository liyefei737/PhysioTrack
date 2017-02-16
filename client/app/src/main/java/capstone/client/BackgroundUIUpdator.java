package capstone.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.couchbase.lite.Database;
import com.couchbase.lite.QueryEnumerator;

import org.json.JSONArray;

import java.util.Date;

/**
 * Background server that queries data and send to UI
 */

public class BackgroundUIUpdator extends Service {
    private static final int PERIOD_IN_SECOND = 1;
    private static final int MILLI_STEP = 1000;
    private DBManager dbManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dbManager = new DBManager(getApplicationContext());

        new Thread() {
            public void run() {
                Database userDB = dbManager.getDatabase("data");
                JSONArray lastXseconds;
                while (true) {
                    try {
                        Thread.sleep(PERIOD_IN_SECOND * 1000);
                    } catch (Exception e) {
                    }
                    /****************************************************************************************
                     Long now =  new Date().getTime();
                     //long startTime = System.nanoTime();
                     //get a piece of data for each the last 1 sec 2 sec 3 sec .... 10sec
                     int lastXSec = 10;
                     JSONArray[] results =  new JSONArray[lastXSec];
                     for(int i = 0; i <= lastXSec; i++){
                     Date startTime = new Date(now - i*1000);
                     long start = System.nanoTime();
                     results[i] = dbManager.QueryLastXSeconds(new Date(), 10, MILLI_STEP);
                     long end = System.nanoTime();
                     double duration = (end - start) / 1000000000.0;
                     System.out.println(duration);

                     }
                     System.out.println(results.length);
                     //JSONArray result = dbManager.QueryLastXSeconds(new Date(), 10, MILLI_STEP);
                     //result
                     long endTime = System.nanoTime();
                     //double duration = (endTime - startTime) / 1000000000.0;
                     String s;

                     SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH-mm:ss");

                     //                    for(QueryRow row :result){
                     //                        String key = (String)row.getKey();
                     //                        try{
                     //                            Map d = row.getDocument().getProperties();
                     //                            format.parse(key);
                     //
                     //                            d.values();
                     //                            s = (String) d.get("timeCreated");
                     //                        }
                     //                        catch(ParseException e) {
                     //                            continue;
                     //                        }
                     //
                     //                    }
                     ***************************************************************************************/
                    //TODO NEED TO CHANGE THE DATA
                    int num_data_pts = 10;
                    float[] coreTemp = new float[num_data_pts];
                    float[] skinTemp = new float[num_data_pts];
                    int[] br = new int[num_data_pts];
                    int[] hr = new int[num_data_pts];
                    QueryEnumerator rows = dbManager.quickQuery(new Date(), 10, 1000);
                    int hr_min = 100;
                    int hr_diff = 70;
                    int br_min = 12;
                    int br_diff = 6;
                    float skin_min = 37.0f;
                    float skin_diff = 3.0f;
                    float core_min = 35.0f;
                    float core_diff = 2.0f;
                    for (int i = 0; i < 10; i++) {
                        //QueryRow row = rows.next();
                        //Map map = row.getDocument().getProperties();
                        //map.get("skinTemp");
                        //coreTemp[i] = Float.valueOf((String)map.get("coreTemp"));
                        //skinTemp[i] = Float.valueOf((String)map.get("skinTemp"));
                        br[i] = br_min + (int) (br_diff * Math.random());
                        hr[i] = hr_min + (int) (hr_diff * Math.random());
                        coreTemp[i] = core_min + (int) (core_diff * Math.random());
                        skinTemp[i] = skin_min + (int) (skin_diff * Math.random());
                    }

                    Intent i = new Intent();
                    i.setAction("UI_UPDATE");
                    i.putExtra("coreTemp", coreTemp);
                    i.putExtra("skinTemp", skinTemp);
                    i.putExtra("br", br);
                    i.putExtra("hr", hr);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                }
            }
        }.start();
        return START_STICKY;
    }
}
