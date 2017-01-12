package com.drdc1.medic;

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

    static private Database db;

    public Server(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.i(this.getClass().getSimpleName(), "request type: "+ session.getMethod());
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
            final JSONObject body = new JSONObject(jsonStr);;
            Document doc = db.getDocument(body.getString("id"));
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
                    try {
                        properties.put("ECG1 (raw)", body.getString("ECG1 (raw)"));
                        properties.put("ECG2 (raw)", body.getString("ECG2 (raw)"));
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

    public void setDatabaseInstance(Database newValue)
    {
        db = newValue;
    }

    void dbWrite()
    {
        //write data into database
    }

        private JSONObject inputStreamToJSON (java.io.InputStream in ) throws IOException, JSONException {
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