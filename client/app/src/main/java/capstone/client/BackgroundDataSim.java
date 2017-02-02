package capstone.client;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

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
import java.util.Date;
import java.util.Map;


/**
This class does:
 1. Generate Data every x seconds
 2. Insert data into the database
 *
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
    private DBManager databseManager = null;
    private LocalBroadcastManager broadcaster = null;
    //Volley is a easy to use http lib
    private RequestQueue rQueue = null;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

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
                dataSim();
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
    private void dataSim()
    {
        databseManager = new DBManager(this);

        Database dataDB = databseManager.getDatabase("data");
        String phpRequestScriptURL = "http://atmacausa.com/ReadRequest.php";
        JSONObject r;
        String responseStr;

        SimpleDateFormat keyFormat = new SimpleDateFormat("01/30/2017 HH:mm:ss.");

        Date now = new Date();
        String dateID = keyFormat.format(now);
        int millis = (int) Math.ceil((double)((now.getTime() % 1000) / 40.0)) * 40;

        while(true) {
            if (millis == 1000)
                millis = 0;
            String updateDateID = dateID + String.format ("%03d", millis);
            responseStr = doRemoteQuery(phpRequestScriptURL, updateDateID);
            //Log.i(this.getClass().toString(), responseStr);
            try {
                r = new JSONArray(responseStr).getJSONObject(0);

//                //for now hard-coded medic ip and port
               //TODO: WAYNE: when soldier name, id, etc, is set attach to request
                //r.put ("name",...
                //r.put("soldierID",...
                String MedicURL ="http://100.64.207.208:8080";
                JsonObjectRequest jsonRequest = new JsonObjectRequest(MedicURL, r,
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
                        VolleyLog.e("Error: ", error.getStackTrace());
                    }
                });
                rQueue.add(jsonRequest);

                Document doc = dataDB.getDocument(r.getString("DateTime"));
                final JSONObject JSONrow = r;
                doc.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        Map<String, Object> properties = newRevision.getUserProperties();
                        try {
                            properties.put("timeCreated", JSONrow.getString("DateTime"));
                            properties.put("accX", JSONrow.getString("AccX"));
                            properties.put("accY", JSONrow.getString("AccY"));
                            properties.put("accZ", JSONrow.getString("AccZ"));
                            properties.put("skinTemp", JSONrow.getString("Skin_Temp"));
                            properties.put("coreTemp", JSONrow.getString("Core_Temp"));
                            properties.put("heartRate", JSONrow.getString("ECG Heart Rate"));
                            properties.put("breathRate", JSONrow.getString("Belt Breathing Rate"));
                            properties.put("bodyPosition", JSONrow.getString("BodyPosition"));
                            properties.put("motion", JSONrow.getString("Motion"));
                        }
                        catch (JSONException e){
                            //
                        }

                        newRevision.setUserProperties(properties);
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            now.setTime(now.getTime() + 80);
            int nextMillis = (int) Math.ceil((double)((now.getTime() % 1000)/ 80.0)) * 80;
            millis = nextMillis;
            dateID = keyFormat.format(now);
        }
    }


    private void sendToMedic(){

    }
    private String doRemoteQuery(String phpRequestURL, String id){
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
}


