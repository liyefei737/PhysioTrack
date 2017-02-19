package com.drdc1.medic;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {

    static private DataManager dataManager;

    public Server(int port, DataManager dataManager) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.i(this.getClass().getSimpleName(), "request type: " + session.getMethod());
        final Map<String, String> map = new HashMap<>();
        try {
            session.parseBody(map);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }

        final String jsonStr = map.get("postData");

        try {
            final JSONObject body = new JSONObject(jsonStr);

            Log.d("sender", "Broadcasting message");
            Intent intent = new Intent("custom-event-name");
            // You can also include some extra data.
//            intent.putExtra("message", "99999");
            intent.putExtra("message", body.getString("ECG Heart Rate"));
            LocalBroadcastManager.getInstance(AppContext.getContext()).sendBroadcast(intent);

            String soldierID = body.getString("soldierID");
            if (!dataManager.soldierInSystem(soldierID)) {
                Map<String, Object> infoMap = null;
                infoMap.put("name", body.getString("name"));
                infoMap.put("age", body.getString("age"));
                infoMap.put("height", body.getString("height"));
                infoMap.put("weight", body.getString("weight"));

                dataManager.addSoldier(soldierID, infoMap);
            }
            Database db = dataManager.getSoldierDB(soldierID);
            Document doc = db.getDocument(body.getString("DateTime"));

            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
//                    HashMap<String, Object> hM = null;
                    String hrate = null;
                    try {
                        properties.put("timeCreated", body.getString("DateTime"));
                        properties.put("accX", body.getString("AccX"));
                        properties.put("accY", body.getString("AccY"));
                        properties.put("accZ", body.getString("AccZ"));
                        properties.put("skinTemp", body.getString("Skin_Temp"));
                        properties.put("coreTemp", body.getString("Core_Temp"));
                        properties.put("heartRate", body.getString("ECG Heart Rate"));
                        hrate = body.getString("ECG Heart Rate");
                        properties.put("breathRate", body.getString("Belt Breathing Rate"));
                        properties.put("bodyPosition", body.getString("BodyPosition"));
                        properties.put("motion", body.getString("Motion"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    newRevision.setUserProperties(properties);

                    return true;
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        String msg = "<html><body><h1> request success </h1></body></html>\n";
//        String userName = null;
//        Map<String, String> parms = session.getParms();
//        if (parms.get("username") == null) {
//            userName = "anonymous";
//        } else {
//            userName = parms.get("username");
//        }

        dbWrite();

        //put userName into the db
        // Map<String, Object> properties = new HashMap<String, Object>();
        // properties.put("username", userName);

//        try {
//            document.putProperties(properties);
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }

        return newFixedLengthResponse(msg);
    }

    void dbWrite() {
        //write data into database
    }

    private JSONObject inputStreamToJSON(java.io.InputStream in) throws IOException, JSONException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        return new JSONObject(responseStrBuilder.toString());
    }

    static public String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> allNIC = NetworkInterface.getNetworkInterfaces();
            while (allNIC.hasMoreElements()) {
                NetworkInterface nic = allNIC.nextElement();
                Enumeration<InetAddress> ipAddres = nic.getInetAddresses();
                while (ipAddres.hasMoreElements()) {
                    InetAddress addrs = ipAddres.nextElement();
                    if (!addrs.isLoopbackAddress() && addrs instanceof Inet4Address) {
                        return addrs.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {

        }
        return null;
    }
}