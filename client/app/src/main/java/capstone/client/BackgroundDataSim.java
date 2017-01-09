package capstone.client;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

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
import java.util.Map;


/**
 * Created by Grace on 2017-01-08.
 */

public class BackgroundDataSim extends Service {

    private volatile HandlerThread mHandlerThread;
    private Handler mServiceHandler;

    static final String DB_UPDATE = "DB_UPDATE";
    static private BackgroundDataSim _backgroundDataSim;
    private Database database;
    private Manager manager;
    private LocalBroadcastManager broadcaster;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    public BackgroundDataSim() {
    }

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
        database = openDatabase("development");
        String phpRequestScriptURL = "http://atmacausa.com/ReadRequest.php";
        JSONObject r;
        String responseStr;

        for (int i = 1; i<4735;i++) {
            responseStr = doRemoteQuery(phpRequestScriptURL, i);
            try {
                r = new JSONObject(responseStr);
                Document doc = database.getDocument(r.getString("DateTime"));
                Map<String, Object> properties = doc.getUserProperties();
                properties.put("accX", r.getString("AccX"));
                properties.put("accY", r.getString("AccY"));
                properties.put("accZ", r.getString("AccZ"));
                properties.put("skinTemp", r.getString("Skin_Temp"));
                properties.put("coreTemp", r.getString("Core_Temp"));
                properties.put("heartRate", r.getString("ECG heart rate"));
                properties.put("breathRate", r.getString("Belt Breathing rate"));
                properties.put("bodyPosition", r.getString("BodyPosition"));
                properties.put("motion", r.getString("Motion"));
                doc.putProperties(properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Database openDatabase(String dbName) {
        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);
        try {
            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            database = manager.openDatabase(dbName, options);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        //triger event when there is a change to db e.g. to update a UI
        database.addChangeListener(new Database.ChangeListener() {
            @Override
            public void changed(Database.ChangeEvent event) {
                for (DocumentChange dc : event.getChanges()) {
                    Log.i(this.getClass().getSimpleName(), "Document changed: " + dc.getDocumentId());
                    notifyUI(database.getDocument(dc.getDocumentId()).getProperties());
                }
            }
        });
        return database;
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


