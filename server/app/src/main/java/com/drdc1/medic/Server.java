package com.drdc1.medic;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {

    static private DataManager dataManagermy;
    private static SimpleDateFormat keyFormat = new SimpleDateFormat("02/25/2017 HH:mm:");
    static Map connectionlist = new HashMap();
    private Squad squad;
    private static String regexSecondsAndMilli = "[0-9]{2}\\.[0-9]{3}";

    public Server(int port, DataManager dataManager) {
        super(port);
        dataManagermy = dataManager;
    }

    @Override
    public Response serve(IHTTPSession session) {
        squad = Squad.getInstance();
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
            String soldierID = body.getString("ID");

            if (!connectionlist.containsKey(soldierID)) {
                //a new id comes in, check if the current connection list has less than 10 soldiers
                if (connectionlist.size() >= 10) {
                    return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain",
                            "too many soldiers connected");
                }

                connectionlist.put(soldierID,
                        session.getHeaders().get("http-client-ip"));

                if (!dataManagermy.soldierInSystem(soldierID)) {
                    Map<String, Object> soldierInfo = new HashMap<String, Object>();
                    soldierInfo.put("name", "djsa");
                    soldierInfo.put("age", "23");
                    soldierInfo.put("gender", "M");
                    soldierInfo.put("id", soldierID);
                    //1 indicates solider is currently being monitored and shows on namelist, 0 means inactive, not shown on namelist
                    soldierInfo.put("active", 1);
                    soldierInfo.put("height", "170");
                    soldierInfo.put("weight", "70");

                    dataManagermy.addSoldier(soldierID, soldierInfo);

                    String url = "http://" + session.getHeaders().get("http-client-ip");

                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
//                                    try {
//                                        JSONObject jsonResponse =
//                                                new JSONObject(response).getJSONObject("form");
//                                        String site = jsonResponse.getString("site"),
//                                                network = jsonResponse.getString("network");
//                                        System.out
//                                                .println("Site: " + site + "\nNetwork: " + network);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
                                }
                            },
                            new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            // the POST parameters:
                            params.put("handshake", "1");
                            return params;
                        }
                    };
                    Volley.newRequestQueue(AppContext.getContext()).add(postRequest);
                }

                Log.d("sender", "Broadcasting message");
                Intent intent = new Intent("custom-event-name");
                // You can also include some extra data.
//            intent.putExtra("message", "99999");
                intent.putExtra("message", body.getString("ECG Heart Rate"));
                LocalBroadcastManager.getInstance(AppContext.getContext()).sendBroadcast(intent);

//                Calendar keyCal = new GregorianCalendar();
//                try {
//                    keyCal.setTime(keyFormat.parse(body.getString("DateTime")));
//                } catch (Exception e) {
//                    keyCal.set(2017, 02, 25);
//                }
//
//                Calendar nearestMinute =
//                        DateUtils.round(keyCal, Calendar.MINUTE);
                Calendar now = new GregorianCalendar();
                now.set(2017, 02, 25);
                keyFormat.setCalendar(now);
                String dateID = keyFormat.format(now.getTime()) + regexSecondsAndMilli;
                Calendar nearestMinute =
                        org.apache.commons.lang3.time.DateUtils.round(now, Calendar.MINUTE);

                Database db = dataManagermy.getSoldierDB(soldierID);

                Document saveStateDoc =
                        db.getDocument(String.valueOf(nearestMinute.getTimeInMillis()));
                try {
                    saveStateDoc.update(new Document.DocumentUpdater() {
                        @Override
                        public boolean update(UnsavedRevision newRevision) {
                            String hrate = null;
                            try {
                                Map<String, Object> properties = newRevision.getUserProperties();
                                properties.put("timeCreated", body.getString("DateTime"));
                                properties.put("accSum", body.getString("accSum"));
                                properties.put("skinTemp", body.getString("Skin_Temp"));
                                properties.put("coreTemp", body.getString("Core_Temp"));
                                properties.put("heartRate", body.getString("ECG Heart Rate"));
                                hrate = body.getString("ECG Heart Rate");
                                properties.put("breathRate", body.getString("Belt Breathing Rate"));
                                properties.put("bodyPosition", body.getString("BodyPosition"));
                                properties.put("motion", body.getString("Motion"));
                                newRevision.setUserProperties(properties);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                } catch (CouchbaseLiteException e) {
                    //handle this
                    e.printStackTrace();
                }

            } else if (!connectionlist.get(soldierID).equals(session.getHeaders().get("http-client-ip"))) {
                //update ip if ip changes for a soldier
                connectionlist.put(soldierID,
                        session.getHeaders().get("http-client-ip"));
            } else {

                Log.d("sender", "Broadcasting message");
                Intent intent = new Intent("custom-event-name");
                intent.putExtra("message", body.getString("ECG Heart Rate"));
                LocalBroadcastManager.getInstance(AppContext.getContext()).sendBroadcast(intent);

//                Calendar keyCal = new GregorianCalendar();
//                try {
//                    keyCal.setTime(keyFormat.parse(body.getString("DateTime")));
//                } catch (Exception e) {
//                    keyCal.set(2017, 02, 25);
//                }
//                Calendar nearestMinute =
//                        DateUtils.round(keyCal, Calendar.MINUTE);

                Calendar now = new GregorianCalendar();
                now.set(2017, 02, 25);
                keyFormat.setCalendar(now);
                String dateID = keyFormat.format(now.getTime()) + regexSecondsAndMilli;
                Calendar nearestMinute =
                        org.apache.commons.lang3.time.DateUtils.round(now, Calendar.MINUTE);

                Database db = dataManagermy.getSoldierDB(soldierID);

                Document saveStateDoc =
                        db.getDocument(String.valueOf(nearestMinute.getTimeInMillis()));
                try {
                    saveStateDoc.update(new Document.DocumentUpdater() {
                        @Override
                        public boolean update(UnsavedRevision newRevision) {
                            String hrate = null;
                            try {
                                Map<String, Object> properties = newRevision.getUserProperties();
                                properties.put("timeCreated", body.getString("DateTime"));
                                properties.put("accSum", body.getString("accSum"));
                                properties.put("skinTemp", body.getString("Skin_Temp"));
                                properties.put("coreTemp", body.getString("Core_Temp"));
                                properties.put("heartRate", body.getString("ECG Heart Rate"));
                                hrate = body.getString("ECG Heart Rate");
                                properties.put("breathRate", body.getString("Belt Breathing Rate"));
                                properties.put("bodyPosition", body.getString("BodyPosition"));
                                properties.put("motion", body.getString("Motion"));
                                newRevision.setUserProperties(properties);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        return newFixedLengthResponse(Response.Status.OK, "text/plain", "success");
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