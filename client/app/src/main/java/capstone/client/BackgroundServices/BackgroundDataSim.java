package capstone.client.BackgroundServices;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import capstone.client.DBManager;
import capstone.client.Soldier;

/**
 * This class does:
 * 1. Generate Data every x seconds
 * 2. Insert data into the database
 * <p>
 * Comment:I would propose to put algorithms on background service
 * on PDA side there are two ways a algorithm can get a hold of the data it needs:
 * 1. process data as data are being generated. For this option you can add the algorithm in this file
 * 2. query the data base every x seconds to compute the the result from e.g. the last 10 entries or the last 10 seconds....
 * for option two you can write the algorithms in "BackgroundSleepAlgo" or "BackgroundWellnessAlgo" file which is a separate background thread
 */

public class BackgroundDataSim extends Service {

    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundDataSim _backgroundDataSim = null;
    private DBManager dbManager = null;
    private Database dataDB = null;
    private LocalBroadcastManager broadcaster = null;
    //Volley is a easy to use http lib
    private RequestQueue rQueue = null;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private static String phpRequestScriptURL = "http://atmacausa.com/ReadRequestMinute.php";
    private static SimpleDateFormat keyFormat = new SimpleDateFormat("01/30/2017 HH:mm:");
    private static String regexSecondsAndMilli = "[0-9]{2}\\.[0-9]{3}";

    public BackgroundDataSim() {
    }

    //singleton to
    static public BackgroundDataSim get_backgroundDataSim() {
        return _backgroundDataSim;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _backgroundDataSim = this;
        rQueue = Volley.newRequestQueue(this);
        broadcaster = LocalBroadcastManager.getInstance(this);
        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("DataSimService.HandlerThread");
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
        dataDB = dbManager.getDatabase(dbManager.DATA_DB);
        if (dbManager.getSoldierDetails() == null){
            dbManager.initDefaultSoldierDetails();
        }
        Timer timer = new Timer();
        TimerTask doDataSimCallback = new TimerTask() {
            @Override
            public void run() {
                mServiceHandler.post(new Runnable() {
                    public void run() {
                        try {
                            dataSim();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doDataSimCallback, 0, DateUtils.MINUTE_IN_MILLIS); //execute every minute

        // Keep service around "sticky"
        return START_STICKY;
    }


    private void dataSim() {
        String responseStr;
        double accSum = 0;

        Calendar now = new GregorianCalendar();
        now.set(2017, 01, 30);
        keyFormat.setCalendar(now);
        String dateID = keyFormat.format(now.getTime()) + regexSecondsAndMilli;
        Calendar nearestMinute  = org.apache.commons.lang3.time.DateUtils.round(now, Calendar.MINUTE);
        //get one minute worth of data
        responseStr = doRemoteQuery(phpRequestScriptURL, dateID);
        try {
            JSONArray minuteData = new JSONArray(responseStr);
            //iterate through minute data to sum up acceleration
            for (int i = 0; i < minuteData.length(); i++ ){
                accSum += getAccelerationMagnitude(minuteData.getJSONObject(i));
            }

            //get last data row in minute for the representative row to be saved and transmitted
            JSONObject lastRow = minuteData.getJSONObject(minuteData.length() - 1);

            //replace accelerations with magnitude
            lastRow.remove("AccX");
            lastRow.remove("AccY");
            lastRow.remove("AccZ");
            lastRow.put("accSum", accSum);

            //put ID on http request
            Soldier soldierDetails = dbManager.getSoldierDetails();
            JSONObject jsonObjForRequest = lastRow;
            jsonObjForRequest.put("ID", soldierDetails.getSoldierID());

            //for now hard-coded medic ip and port
            String MedicURL = "http://100.64.207.208:8080";
            JsonObjectRequest jsonRequest = new JsonObjectRequest(MedicURL, jsonObjForRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // VolleyLog.e("Error: ", error.getStackTrace());
                    }
                });
                rQueue.add(jsonRequest);

                //save to DB
                Document doc = dataDB.getDocument(String.valueOf(nearestMinute.getTimeInMillis()));
                final JSONObject JSONrow = lastRow;
                doc.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        Map<String, Object> properties = newRevision.getUserProperties();
                        try {
                            properties.put("timeCreated", JSONrow.getString("DateTime"));
                            properties.put("accSum", JSONrow.getString("accSum"));
                            properties.put("skinTemp", JSONrow.getString("Skin_Temp"));
                            properties.put("coreTemp", JSONrow.getString("Core_Temp"));
                            properties.put("heartRate", JSONrow.getString("ECG Heart Rate"));
                            properties.put("breathRate", JSONrow.getString("Belt Breathing Rate"));
                            properties.put("bodyPosition", JSONrow.getString("BodyPosition"));
                            properties.put("motion", JSONrow.getString("Motion"));
                        } catch (JSONException e) {
                            //
                        }

                        newRevision.setUserProperties(properties);
                        return true;
                    }
                });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String doRemoteQuery(String phpRequestURL, String id) {

        URL url=null;
        HttpURLConnection conn = null;
        try {
            url = new URL(phpRequestURL);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            // setDoInput and setDoOutput to true as we send and receive data
            conn.setDoInput(true);
            conn.setDoOutput(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("searchQuery", id);
            String query = builder.build().getEncodedQuery();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {

            int response_code = conn.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return (result.toString());

            } else {
                return ("Connection error");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        } finally {

            conn.disconnect();
        }
    }

    public double getAccelerationMagnitude(JSONObject dataObj){
        double accX = 0, accY = 0, accZ = 0;
        try {
            accX = Double.valueOf(dataObj.getString("AccX"));
            accY = Double.valueOf(dataObj.getString("AccY"));
            accZ = Double.valueOf(dataObj.getString("AccZ"));

        } catch (JSONException e){

        }
        return Math.sqrt(accX*accX + accY * accY + accZ*accZ);
    }
}


