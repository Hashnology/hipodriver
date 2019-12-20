package com.karimapps.hipodriver.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.LoginActivity;
import com.karimapps.hipodriver.activities.MainActivity;
import com.karimapps.hipodriver.adapters.PassengersAdapter;
import com.karimapps.hipodriver.models.Passenger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import universal.UniversalConstants;
import universal.Utils;

@SuppressLint("ValidFragment")
public class StationPassengersFragment extends Fragment {
    private Context context;
    private PassengersAdapter adapter;
    private RecyclerView rv_passengers;
    private TextView tv_count;
    private List<Passenger> passengers;
    private FloatingActionButton btn_back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_passengers, container, false);
        context = getActivity();
        rv_passengers = view.findViewById(R.id.rv_passengers);
        tv_count = view.findViewById(R.id.tv_count);
        GridLayoutManager layoutManager =
                new GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false);
        rv_passengers.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_passengers.getContext(),
                layoutManager.getOrientation());
        rv_passengers.addItemDecoration(dividerItemDecoration);
        getStationPassengers(getArguments().getString(UniversalConstants.STATION_NUMBER));

        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = new LocationFragmentAws();
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.replace(R.id.container, f);
                t.commit();
            }
        });

        return view;
    }

    /*get passengers of a station*/
    private void getStationPassengers(String stationId) {
        String url = Utils.GET_STATION_PASSENGERS + "?stationPlanId=" + stationId;
        final ProgressDialog dialog = ProgressDialog.show(context, "Fetching Passengers Data", "Please wait...");
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        JSONObject jsonObject;
                        passengers = new ArrayList<>();
                        Passenger passenger;
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray dataarray = (JSONArray) data.get("Passengers");

                            for (int i = 0; i < dataarray.length(); i++) {
                                jsonObject = dataarray.getJSONObject(i);
                                Gson g = new Gson();
                                passenger = g.fromJson(jsonObject.toString(), Passenger.class);

                                /*station = new Station(id, name, passengers, lat, lon, etd);*/
                                passengers.add(passenger);
                            }
                            dialog.dismiss();
                            /*for (int i=0; i<passengers.size(); i++)
                            {
                                Utils.showToast(context, "Passenger "+i+" Phone: "+passengers.get(i).getPhoneNumber());
                            }*/
                            ((MainActivity) getActivity()).setActionBarTitle(passengers.size() + " " + getString(R.string.txt_passengers));
                            adapter = new PassengersAdapter(context, passengers);
                            rv_passengers.setAdapter(adapter);
                        } catch (JSONException e) {
                            dialog.dismiss();
                            Utils.savePreferences(UniversalConstants.LOGGED_IN, "false", context);
                            Utils.savePreferencesBool(UniversalConstants.LOGIN, false, context);
                            startActivity(new Intent(context, LoginActivity.class));

//                            Utils.showCustomDialog(context, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if (error != null) {
                            int code = error.networkResponse.statusCode;
                            Log.d("Error", error + " Volley");
                            Log.d("Code", code + " code");
                        }

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "bearer " + Utils.getPreferences(UniversalConstants.ACCESS_TOKEN, context));

                        return params;
                    }
                };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
}
