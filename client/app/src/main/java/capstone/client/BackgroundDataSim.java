package capstone.client;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import org.json.JSONArray;
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
import java.util.HashMap;
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

        for (int i = 1; i<4735;i++) {
            responseStr = doRemoteQuery(phpRequestScriptURL, i);
            Log.i(this.getClass().toString(), responseStr);
            try {
                r = new JSONArray(responseStr).getJSONObject(0);

//                //for now hard-coded medic ip and port
//                String MedicURL ="http://100.64.207.208:8080";
//                JsonObjectRequest jsonRequest = new JsonObjectRequest(MedicURL, r,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    VolleyLog.v("Response:%n %s", response.toString(4));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        VolleyLog.e("Error: ", error.getStackTrace());
//                    }
//                });
//                rQueue.add(jsonRequest);
                //Document doc = database.getDocument(r.getString("DateTime"));
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("timeCreated", r.getString("DateTime"));
                properties.put("accX", r.getString("AccX"));
                properties.put("accY", r.getString("AccY"));
                properties.put("accZ", r.getString("AccZ"));
                properties.put("skinTemp", r.getString("Skin_Temp"));
                properties.put("coreTemp", r.getString("Core_Temp"));
                //properties.put("heartRate", r.getString("ECG heart rate"));
                //properties.put("breathRate", r.getString("Belt Breathing rate"));
                //properties.put("bodyPosition", r.getString("BodyPosition"));
                //properties.put("motion", r.getString("Motion"));
                Log.i("saving data", properties.toString());

                Document doc = dataDB.createDocument();
                doc.putProperties(properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendToMedic(){

    }
    private String doRemoteQuery(String phpRequestURL, int id){
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
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("searchQuery", String.valueOf(id));
            String query = builder.build().getEncodedQuery();
            Log.i("info", query);
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


