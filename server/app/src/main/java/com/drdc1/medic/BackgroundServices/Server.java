package com.drdc1.medic.BackgroundServices;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.drdc1.medic.AppContext;
import com.drdc1.medic.DataManagement.DataManager;

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
    private static String PDA_IN_BODYPOS = "BodyPosition";
    private static String PDA_IN_MOTION = "Motion";
    private static String PDA_IN_HR = "ECG Heart Rate";
    private static String PDA_IN_BR = "Belt Breathing Rate";
    private static String PDA_IN_SKIN = "Skin_Temp";
    private static String PDA_IN_CORE = "Core_Temp";
    private static String PDA_IN_TIMESTAMP = "DateTime";
    private static String PDA_IN_ID = "ID";
    private static String PDA_IN_NAME = "name";
    private static String PDA_IN_AGE = "age";
    private static String PDA_IN_WEIGHT = "weight";
    private static String PDA_IN_HEIGHT = "height";
    private static String PDA_IN_ACC = "accSum";



    private static String[] requiredNewSoldierFields = {
            PDA_IN_TIMESTAMP,
            PDA_IN_ID,
            PDA_IN_NAME,
            PDA_IN_AGE,
            PDA_IN_WEIGHT,
            PDA_IN_HEIGHT
    };


    public Server(int port, DataManager dataManager) {
        super(port);
        dataManagermy = dataManager;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
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
            final JSONObject body;
            try {
                body = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain",
                                              "Invalid JSON Format");

            }
            try {
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

                        for (String requiredField: requiredNewSoldierFields) {
                            if (!body.has(requiredField)) {
                                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain",
                                                              "Missing Required Fields for adding a new soldier");
                            }
                        }
                        soldierInfo.put("name", body.getString(PDA_IN_NAME));
                        soldierInfo.put("age", body.getString(PDA_IN_AGE));
                        soldierInfo.put("id", soldierID);
                        //1 indicates solider is currently being monitored and shows on namelist, 0 means inactive, not shown on namelist
                        soldierInfo.put("active", 1);
                        soldierInfo.put("height", body.getInt(PDA_IN_HEIGHT));
                        soldierInfo.put("weight", body.getInt(PDA_IN_WEIGHT));
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
                    intent.putExtra("message", body.getString(PDA_IN_HR));
                    LocalBroadcastManager.getInstance(AppContext.getContext()).sendBroadcast(intent);

                    Calendar now = new GregorianCalendar();
                    now.set(2017, 02, 25);
                    keyFormat.setCalendar(now);
                    Calendar nearestMinute =
                            org.apache.commons.lang3.time.DateUtils.round(now, Calendar.MINUTE);

                    dataManagermy.updateData(soldierID, String.valueOf(nearestMinute.getTimeInMillis()), body.getString(PDA_IN_TIMESTAMP), body.getString(PDA_IN_ACC),
                            body.getString(PDA_IN_SKIN), body.getString(PDA_IN_CORE), body.getString(PDA_IN_HR), body.getString(PDA_IN_BR), body.getString(PDA_IN_BODYPOS), body.getString(PDA_IN_MOTION));


                } else if (!connectionlist.get(soldierID)
                                          .equals(session.getHeaders().get("http-client-ip"))) {
                    //update ip if ip changes for a soldier
                    connectionlist.put(soldierID,
                                       session.getHeaders().get("http-client-ip"));
                } else {

                    Log.d("sender", "Broadcasting message");
                    Intent intent = new Intent("custom-event-name");
                    intent.putExtra("message", body.getString("ECG Heart Rate"));
                    LocalBroadcastManager.getInstance(AppContext.getContext()).sendBroadcast(intent);

                    Calendar now = new GregorianCalendar();
                    now.set(2017, 02, 25);
                    keyFormat.setCalendar(now);
                    Calendar nearestMinute =
                            org.apache.commons.lang3.time.DateUtils.round(now, Calendar.MINUTE);
                    dataManagermy.updateData(soldierID, String.valueOf(nearestMinute.getTimeInMillis()), body.getString(PDA_IN_TIMESTAMP), body.getString(PDA_IN_ACC),
                    body.getString(PDA_IN_SKIN), body.getString(PDA_IN_CORE), body.getString(PDA_IN_HR), body.getString(PDA_IN_BR), body.getString(PDA_IN_BODYPOS), body.getString(PDA_IN_MOTION));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbWrite();

            Intent i = buildPDAMessageIntent(body);
            LocalBroadcastManager.getInstance(AppContext.getContext()).sendBroadcast(i);
            return newFixedLengthResponse(Response.Status.OK, "text/plain", "success");
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "test/plain", e.getClass().getName() + ": " + e.getMessage());
        }

    }

    private Intent buildPDAMessageIntent(JSONObject jsonObject) {
        //TODO this defines the format of the request
        Intent intent = new Intent("PDAMessage");
        try {
            intent.putExtra("name", jsonObject.getString(PDA_IN_NAME));
            intent.putExtra("ID", jsonObject.getString(PDA_IN_ID));
            intent.putExtra("age", jsonObject.getString(PDA_IN_AGE));
            intent.putExtra("height", jsonObject.getString(PDA_IN_HEIGHT));
            intent.putExtra("weight", jsonObject.getString(PDA_IN_WEIGHT));
            intent.putExtra("bodypos", jsonObject.getString(PDA_IN_BODYPOS));
            intent.putExtra("hr", jsonObject.getString(PDA_IN_HR));
            intent.putExtra("br", jsonObject.getString(PDA_IN_BR));
            intent.putExtra("skinTemp", jsonObject.getString(PDA_IN_SKIN));
            intent.putExtra("coreTemp", jsonObject.getString(PDA_IN_CORE));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return intent;
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