package com.karimapps.hipodriver.apis;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.karimapps.hipodriver.activities.LoginActivity;
import com.karimapps.hipodriver.models.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import universal.UniversalConstants;
import universal.HelperProgressDialog;
import universal.Utils;

public class GetPlanStations
{
    private Context context;
    private List<Station> stations;

    public GetPlanStations(Context context) {
        this.context = context;
    }


    public synchronized List<Station> getPlanStations()
    {
        String url = Utils.GET_PLAN_STATIONS;
        HelperProgressDialog.showDialog(context, "Sending a confirmation code", "Please wait...");
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        JSONObject jsonObject;
                        Station station;
                        stations = new ArrayList<>();
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray dataarray = (JSONArray) data.get("PlanStations");

                            for (int i = 0; i < dataarray.length(); i++) {
                                jsonObject = dataarray.getJSONObject(i);
                                Gson g = new Gson();
                                station = g.fromJson(jsonObject.toString(), Station.class);
                                /*String id = station.StationPlanId;
                                String name = station.StationName;
                                String passenger = station.TotalPassengers;
                                String etd = station.ETD;
                                String lat = station.Lat;
                                String lon = station.Long;*/

                                /*Station s = new Station(id, name, passenger, lat, lon, etd);

                                stations.add(station);*/
                            }
                        }
                            catch(JSONException e)
                            {
                                Utils.savePreferences(UniversalConstants.LOGGED_IN, "false", context);
                                Utils.savePreferencesBool(UniversalConstants.LOGIN, false, context);
                                context.startActivity(new Intent(context, LoginActivity.class));

//                                Utils.showCustomDialog(context, e.getMessage());
                            }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null)
                        {
                            int code = error.networkResponse.statusCode;
                            Log.d("Error", error +" Volley");
                            Log.d("Code", code+" code");
                        }

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "bearer "+Utils.getPreferences(UniversalConstants.ACCESS_TOKEN, context));

                        return params;
                    }
                };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        return stations;
        }
    }

