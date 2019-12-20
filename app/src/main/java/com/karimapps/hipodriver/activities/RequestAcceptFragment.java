//package com.karimapps.hipodriver.activities;
//
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.SystemClock;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.AccelerateDecelerateInterpolator;
//import android.view.animation.Interpolator;
//import android.view.animation.LinearInterpolator;
//import android.widget.Button;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.VolleyLog;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.lineztech.farhan.cabbiipassenger.R;
//import com.lineztech.farhan.cabbiipassenger.interfaces.DurationDistanceFinderListener;
//import com.lineztech.farhan.cabbiipassenger.main.MainActivityDrawer;
//import com.lineztech.farhan.cabbiipassenger.model.Booking;
//import com.lineztech.farhan.cabbiipassenger.model.DurationDistanceFinder;
//import com.lineztech.farhan.cabbiipassenger.model.MyRequestQueue;
//import com.lineztech.farhan.cabbiipassenger.utils.CONSTANTS;
//import com.lineztech.farhan.cabbiipassenger.utils.Utils;
//import com.mikhaellopez.circularimageview.CircularImageView;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import me.zhanghai.android.materialratingbar.MaterialRatingBar;
//
//import static android.content.Context.MODE_PRIVATE;
//
//public class RequestAcceptFragment extends Fragment implements View.OnClickListener,
//        DurationDistanceFinderListener {
//
//    private View fragment_view;
//    private TextView tv_pick_up_location;
//    private TextView tv_destination;
//    private TextView tv_driver_name;
//    private TextView tv_vehicle_name;
//    private TextView tv_driver_reg_no;
//    private TextView tv_driver_tel_no;
//    private Button btn_cancel_taxi;
//    private CircularImageView iv_driver_pic;
//    private TextView tv_eta_value;
//    private RelativeLayout rv_btn_call_driver;
//    private MaterialRatingBar rating_bar;
//    private SupportMapFragment map_fragment;
//
//    private FragmentManager fragmentManager;
//
//    private ArrayList<Booking> booking_details;
//
//    private GoogleMap google_map;
//    private Marker pick_up_marker;
//    private Marker driver_marker;
//
//    private double driver_latitude;
//    private double driver_longitude;
//
//    private Timer timer;
//    private TimerTask doAsynchronousTask;
//
//    private Timer timer_eta;
//    private TimerTask doAsynchronousTask_eta;
//
//    private boolean is_duration_is_get;
//
//    private boolean is_address_string_get = true;
//    private boolean is_pick_up = false;
//    private boolean is_destination = false;
//    private boolean is_driver_location_updated;
//
//    private double latitude_pick_up;
//    private double longitude_pick_up;
//    private double latitude_destination;
//    private double longitude_destination;
//    private LatLng driver_updated_latlng;
//    private LatLng driver_pick_up_latlng;
//    private LatLng driver_previous_updated_latlng;
//
//    private boolean isMarkerRotating = false;
//    private boolean is_arrived;
//
//    private ProgressDialog progressDialog_address;
//    private boolean is_progress_dialog_shown;
//
//    public RequestAcceptFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        fragment_view = inflater.inflate(R.layout.fragment_request_accrpt, container, false);
//
//        linkViews();
//
//        SharedPreferences preferences_screen =
//                getActivity().getSharedPreferences(CONSTANTS.PREFERENCE_NAME_APP_FOREGROUND, MODE_PRIVATE);
//        SharedPreferences.Editor editor_screen = preferences_screen.edit();
//        editor_screen.putString(CONSTANTS.PREFERENCE_CURRENT_FOREGROUND_SCREEN_NAME, CONSTANTS.SCREEN_NAME_ACCEPT_SCREEN_SCREEN);
//        editor_screen.commit();
//
//        is_duration_is_get = true;
//        is_arrived = false;
//        is_driver_location_updated = true;
//
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            booking_details = (ArrayList<Booking>) bundle.getSerializable(CONSTANTS.BOOKING_EXTRA);
//        }
//
//        showDialogRequestCancel();
//
//        /*else {
//            //sample data
//            booking_details = new ArrayList<>();
//            Booking booking = new Booking("31.507132439663142", "74.33724816888571", "31.427405299999997",
//                    "74.18361", "1", "", "Hantash Nadeem", "31.514164", "74.3442217",
//                    "032323235", "121212", "3", "file_58ef04138e1a058ef04138e1a8.png", "Cabbii");
//            booking_details.add(booking);
//        }*/
//
//        map_fragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                google_map = googleMap;
//
//                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//
//                Booking booking = booking_details.get(0);
//
//                if (booking.getPick_up_latitude() != null && booking.getPick_up_longitude() != null
//                        && booking.getDestination_latitude() != null && booking.getDestination_longitude() != null) {
//
//                    setPassengerDriverMarkers(booking.getPick_up_latitude(),
//                            booking.getPick_up_longitude(),
//                            booking.getDriver_latitude(),
//                            booking.getDriver_longitude());
//
//                    List<LatLng> latLng_lists = new ArrayList<>();
//                    latLng_lists.add(new LatLng(Double.parseDouble(booking.getPick_up_latitude()),
//                            Double.parseDouble(booking.getPick_up_longitude())));
//
//                    latLng_lists.add(new LatLng(Double.parseDouble(booking.getDriver_latitude()),
//                            Double.parseDouble(booking.getDriver_longitude())));
//
//                    computeCentered(latLng_lists);
//                }
//            }
//        });
//
//        return fragment_view;
//    }
//
//    private void setPassengerDriverMarkers(String pick_up_latitude, String pick_up_longitude, String driver_latitude, String driver_longitude) {
//        if (pick_up_marker != null) {
//            pick_up_marker.remove();
//            pick_up_marker = null;
//        }
//        if (driver_marker != null) {
//            driver_marker.remove();
//            driver_marker = null;
//        }
//
//        MarkerOptions pick_up_option = new MarkerOptions();
//        pick_up_option.position(new LatLng(Double.parseDouble(pick_up_latitude),
//                Double.parseDouble(pick_up_longitude)));
//        pick_up_option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_yellow));
//
//        MarkerOptions driver_option = new MarkerOptions();
//        driver_option.position(new LatLng(Double.parseDouble(driver_latitude),
//                Double.parseDouble(driver_longitude)));
//        driver_option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cabbii));
//        //driver_option.anchor(0.5f, 0.5f);
//        driver_option.flat(true);
//
//        pick_up_marker = google_map.addMarker(pick_up_option);
//        driver_marker = google_map.addMarker(driver_option);
//
//        /*here*/
//        /*driver_marker.setVisible(false);
//        rotateMarker(driver_marker, (float) bearingBetweenLocations(
//                new LatLng(Double.parseDouble(driver_latitude), Double.parseDouble(driver_longitude)),
//                new LatLng(Double.parseDouble(pick_up_latitude), Double.parseDouble(pick_up_longitude))));*/
//
//        //get the driver Asynchrounous here
//        if (doAsynchronousTask == null) {
//            callAsynchronousTask();
//        }
//
//        if (doAsynchronousTask_eta == null) {
//            callAsynchronousTaskETA();
//        }
//    }
//
//    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
//
//        double PI = 3.14159;
//        double lat1 = latLng1.latitude * PI / 180;
//        double long1 = latLng1.longitude * PI / 180;
//        double lat2 = latLng2.latitude * PI / 180;
//        double long2 = latLng2.longitude * PI / 180;
//
//        double dLon = (long2 - long1);
//
//        double y = Math.sin(dLon) * Math.cos(lat2);
//        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
//                * Math.cos(lat2) * Math.cos(dLon);
//
//        double brng = Math.atan2(y, x);
//
//        brng = Math.toDegrees(brng);
//        brng = (brng + 360) % 360;
//
//        return brng;
//    }
//
//    private void rotateMarker(final Marker marker, final float toRotation) {
//        if (!isMarkerRotating) {
//            final Handler handler = new Handler();
//            final long start = SystemClock.uptimeMillis();
//            final float startRotation = marker.getRotation();
//            final long duration = 1000;
//
//            final Interpolator interpolator = new LinearInterpolator();
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    isMarkerRotating = true;
//
//                    long elapsed = SystemClock.uptimeMillis() - start;
//                    float t = interpolator.getInterpolation((float) elapsed / duration);
//
//                    float rot = t * toRotation + (1 - t) * startRotation;
//
//                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
//                    if (t < 1.0) {
//                        // Post again 16ms later.
//                        handler.postDelayed(this, 16);
//                    } else {
//                        driver_marker.setVisible(true);
//                        isMarkerRotating = false;
//                    }
//                }
//            });
//        }
//    }
//
//    private void linkViews() {
//        fragmentManager = getChildFragmentManager();
//        map_fragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
//
//        tv_pick_up_location = (TextView) fragment_view.findViewById(R.id.tv_pick_up);
//        tv_destination = (TextView) fragment_view.findViewById(R.id.tv_destination);
//        tv_driver_name = (TextView) fragment_view.findViewById(R.id.tv_driver_name);
//        tv_vehicle_name = (TextView) fragment_view.findViewById(R.id.tv_driver_name_2);
//        tv_driver_reg_no = (TextView) fragment_view.findViewById(R.id.tv_driver_reg_no);
//        tv_driver_tel_no = (TextView) fragment_view.findViewById(R.id.tv_driver_tel_no);
//        btn_cancel_taxi = (Button) fragment_view.findViewById(R.id.btn_cancel_taxi);
//        iv_driver_pic = (CircularImageView) fragment_view.findViewById(R.id.iv_driver_pic);
//        rv_btn_call_driver = (RelativeLayout) fragment_view.findViewById(R.id.rv_btn_call_driver);
//        rating_bar = (MaterialRatingBar) fragment_view.findViewById(R.id.rating_bar);
//        tv_eta_value = (TextView) fragment_view.findViewById(R.id.tv_eta_value);
//
//        btn_cancel_taxi.setOnClickListener(this);
//        rv_btn_call_driver.setOnClickListener(this);
//    }
//
//    private void showBookingInfo(ArrayList<Booking> booking_details) {
//        Booking booking = booking_details.get(0);
//        tv_driver_name.setText(booking.getDriver_name());
//        tv_vehicle_name.setText(booking.getVehicle_name());
//        tv_driver_reg_no.setText(booking.getDriver_reg_no());
//        tv_driver_tel_no.setText(booking.getDriver_tel_no());
//        rating_bar.setRating(Float.parseFloat(booking.getDriver_rating()));
//
//        try {
//            latitude_pick_up = Double.parseDouble(booking.getPick_up_latitude());
//            longitude_pick_up = Double.parseDouble(booking.getPick_up_longitude());
//            latitude_destination = Double.parseDouble(booking.getDestination_latitude());
//            longitude_destination = Double.parseDouble(booking.getDestination_longitude());
//        } catch (NumberFormatException e) {
//
//        }
//
//        double driver_latitude = 0;
//        double driver_longitude = 0;
//        try {
//            driver_latitude = Double.parseDouble(booking.getDriver_latitude());
//            driver_longitude = Double.parseDouble(booking.getDriver_longitude());
//        } catch (NumberFormatException e) {
//
//        }
//
//        driver_updated_latlng = new LatLng(driver_latitude, driver_longitude);
//        driver_pick_up_latlng = new LatLng(latitude_pick_up, longitude_pick_up);
//        driver_previous_updated_latlng = new LatLng(driver_latitude, driver_longitude);
//
//        Log.d("driver_movement_pick_up", "---------------------------------Starting----------------------------------------------------");
//        Log.d("driver_movement_pick_up", "Update Latitude: " + driver_updated_latlng.latitude + "Update Longitude" + driver_updated_latlng.longitude);
//        Log.d("driver_movement_pick_up", "Latitude Pick Up: " + driver_pick_up_latlng.latitude + "Longitude Pick Up" + driver_pick_up_latlng.longitude);
//        Log.d("driver_movement_pick_up", "----------------------------------------------------------------------------------------------");
//
//        if (booking.getPick_up_latitude() != null && booking.getPick_up_longitude() != null
//                && booking.getDestination_latitude() != null && booking.getDestination_longitude() != null) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                tv_pick_up_location.setText(getCompleteAddressString(Double.parseDouble(booking.getPick_up_latitude()),
//                        Double.parseDouble(booking.getPick_up_longitude())));
//
//                tv_destination.setText(getCompleteAddressString(Double.parseDouble(booking.getDestination_latitude()),
//                        Double.parseDouble(booking.getDestination_longitude())));
//            } else {
//                is_pick_up = true;
//                is_progress_dialog_shown = false;
//                new GetAddressBackEndTask().execute();
//            }
//
//        }
//
//        Log.d("hantash_linez", "Latitude: " + booking.getDestination_latitude());
//        Log.d("hantash_linez", "Longitude: " + booking.getDestination_longitude());
//
//        ConvertImageURL convert_image_url = new ConvertImageURL();
//        convert_image_url.execute(booking.getDriver_picture());
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.btn_cancel_taxi) {
//            showRequestCancelDialog();
//        } else if (v.getId() == R.id.rv_btn_call_driver) {
//            String driver_no = tv_driver_tel_no.getText().toString();
//            Uri number = Uri.parse("tel:" + driver_no);
//            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//            startActivity(callIntent);
//        }
//    }
//
//    private void showRequestCancelDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage("Are you sure to cancel the request");
//
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                requestCancel();
//            }
//        });
//
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//    public void callAsynchronousTask() {
//        final Handler handler = new Handler();
//        timer = new Timer();
//        doAsynchronousTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        try {
//                            SharedPreferences preferences = getActivity().getSharedPreferences(CONSTANTS.REQUEST_ID_PREFERENCE_NAME, Context.MODE_PRIVATE);
//                            final String request_id = preferences.getString(CONSTANTS.REQUEST_ID_EXTRA, "");
//                            final String passenger_id = Utils.getPreferences("id", getActivity());
//                            final String RegistrationID = Utils.getPreferences("RegistrationID", getActivity());
//
//                            if (is_driver_location_updated) {
//                                is_driver_location_updated = false;
//                                Log.d("hantash_driver_status", "Driver location updated");
//                                getBookingDetails(passenger_id, RegistrationID, request_id, "");
//                            }
//
//                        } catch (Exception e) {
//                            // TODO Auto-generated catch block
//                        }
//                    }
//                });
//            }
//        };
//        timer.schedule(doAsynchronousTask, 0, 5000);
//    }
//
//    public void callAsynchronousTaskETA() {
//        final Handler handler = new Handler();
//        timer_eta = new Timer();
//        doAsynchronousTask_eta = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        try {
//                            if (is_duration_is_get) {
//                                if (driver_updated_latlng != null) {
//                                    if (driver_updated_latlng.latitude != 0 && driver_updated_latlng.longitude != 0) {
//                                        DurationDistanceFinder durationDistanceFinder = new DurationDistanceFinder(
//                                                RequestAcceptFragment.this,
//                                                driver_updated_latlng,
//                                                driver_pick_up_latlng);
//                                        try {
//                                            durationDistanceFinder.getDurationDistanceLatLng();
//                                        } catch (UnsupportedEncodingException e) {
//                                            e.printStackTrace();
//                                        }
//                                        is_duration_is_get = false;
//                                        Log.d("hantash", "Getting Driver LatLng");
//                                    } else {
//                                        Log.d("hantash_duration", "Both Latlng are zero");
//                                    }
//                                }
//
//                            }
//
//                        } catch (Exception e) {
//                            // TODO Auto-generated catch block
//                        }
//                    }
//                });
//            }
//        };
//        timer_eta.schedule(doAsynchronousTask_eta, 0, 10000);
//    }
//
//    private void requestCancel() {
//
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "",
//                "Loading...", true);
//
//        SharedPreferences preferences = getActivity().getSharedPreferences(CONSTANTS.REQUEST_ID_PREFERENCE_NAME, Context.MODE_PRIVATE);
//        final String request_id = preferences.getString(CONSTANTS.REQUEST_ID_EXTRA, "");
//        final String passenger_id = Utils.getPreferences("id", getActivity());
//        final String RegistrationID = Utils.getPreferences("RegistrationID", getActivity());
//        Log.d("hantash_linez", request_id);
//        Log.d("hantash_linez", passenger_id);
//        Log.d("hantash_linez", RegistrationID);
//
//        String url_file = CONSTANTS.baseUrl + "update_booking_request_status_passenger.php";
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("request_id", request_id);
//        params.put("status", "CANCELLED");
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_file, new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        String message = null;
//                        try {
//                            JSONObject json_status = response.getJSONObject("status");
//                            if (json_status.getString("code").equals("1000")) {
//                                progressDialog.dismiss();
//                                message = json_status.getString("message");
//                                Toast.makeText(getActivity(), "Request Has Been Cancelled.", Toast.LENGTH_LONG).show();
//                                startActivity(new Intent(getActivity(), MainActivityDrawer.class));
//                            } else {
//                                Toast.makeText(getActivity(), "Request Has Been Cancelled.", Toast.LENGTH_LONG).show();
//                                startActivity(new Intent(getActivity(), MainActivityDrawer.class));
//                                progressDialog.dismiss();
//                                message = json_status.getString("message");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            progressDialog.dismiss();
//                        }
//                        //Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("hantash", error + "");
//                progressDialog.dismiss();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                headers.put("passenger_id", passenger_id);
//                headers.put("registration_id", RegistrationID);
//                return headers;
//            }
//        };
//
//        MyRequestQueue.getRequestInstance(getActivity()).addRequest(jsonObjectRequest);
//        //Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
//    }
//
//    private void showDialogRequestCancel() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(R.layout.dialog_request_accepted);
//        final AlertDialog alert_dialog = builder.create();
//
//        alert_dialog.setCancelable(false);
//        alert_dialog.setCanceledOnTouchOutside(false);
//
//        alert_dialog.show();
//
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                alert_dialog.dismiss();
//                showBookingInfo(booking_details);
//            }
//        }, 3000);
//    }
//
//    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
//        String strAdd = "";
//        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
//            if (addresses != null) {
//                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");
//
//                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                }
//                strAdd = strReturnedAddress.toString();
//                Log.w("My Current", "" + strReturnedAddress.toString());
//            } else {
//                Log.w("My Current location", "No Address returned!");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.w("My Current", "Canont get Address!");
//        }
//        return strAdd;
//    }
//
//    private void computeCentered(List<LatLng> points) {
//        double latitude = 0;
//        double longitude = 0;
//        int n = points.size();
//
//        for (LatLng point : points) {
//            latitude += point.latitude;
//            longitude += point.longitude;
//        }
//
//        LatLng latLng = new LatLng(latitude / n, longitude / n);
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
//        google_map.animateCamera(cameraUpdate);
//    }
//
//    private void getBookingDetails(final String paseenger_id, final String registration_id, String request_id, final String status) {
//        String url = CONSTANTS.baseUrl + "get_booking_detail.php";
//        Map<String, String> postParam = new HashMap<String, String>();
//        postParam.put("request_id", request_id);
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                url, new JSONObject(postParam),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//                        try {
//                            VolleyLog.d("TAG", "jsonObject: " + jsonObject);
//                            JSONObject jsonStatus = jsonObject.getJSONObject("status");
//                            ArrayList<Booking> booking_detail = null;
//                            if (jsonStatus.getString("code").equalsIgnoreCase("1000")) {
//
//                                is_driver_location_updated = true;
//
//                                JSONObject jsonDriver = jsonObject.getJSONObject("driver");
//                                String driver_latitude = jsonDriver.getString("driver_latitude");
//                                String driver_longitude = jsonDriver.getString("driver_longitude");
//
//                                Booking booking = new Booking(driver_latitude, driver_longitude);
//
//                                booking_detail = new ArrayList<>();
//                                booking_detail.add(booking);
//                            }
//
//                            //Toast.makeText(getActivity(), "Request Accept\n"+jsonStatus.getString("message"), Toast.LENGTH_LONG).show();
//
//                            if (booking_detail.get(0).getDriver_latitude() != null &&
//                                    booking_detail.get(0).getDriver_longitude() != null) {
//                                try {
//                                    Log.d("driver_movement_pick_up", "Driver Location is available");
//
//                                    updateDriverLocation(booking_detail.get(0).getDriver_latitude(),
//                                            booking_detail.get(0).getDriver_longitude());
//                                } catch (Exception e) {
//
//                                }
//                            }
//
//                            Log.d("driver_lat", "" + booking_detail.get(0).getDriver_latitude());
//                            Log.d("driver_lng", "" + booking_detail.get(0).getDriver_longitude());
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d("TAG", "Error: " + error.getMessage());
//                is_driver_location_updated = true;
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                headers.put("passenger_id", paseenger_id);
//                headers.put("registration_id", registration_id);
//                return headers;
//            }
//        };
//
//        MyRequestQueue.getRequestInstance(getActivity()).addRequest(jsonObjReq);
//        //Volley.newRequestQueue(context).add(jsonObjReq);
//    }
//
//    private void updateDriverLocation(String latitude, String longitude) {
//
//        double driver_latitude = 0;
//        double driver_longitude = 0;
//        try {
//            driver_latitude = Double.parseDouble(latitude);
//            driver_longitude = Double.parseDouble(longitude);
//
//            LatLng driver_update = new LatLng(driver_latitude, driver_longitude);
//
//            driver_updated_latlng = driver_update;
//            //rotateMarker(driver_marker, (float) bearingBetweenLocations(driver_marker.getPosition(), driver_update));
//
//            //Rotating the driver
//            Location previous_location = new Location("");
//            previous_location.setLatitude(driver_marker.getPosition().latitude);
//            previous_location.setLongitude(driver_marker.getPosition().longitude);
//            Location update_location = new Location("");
//            update_location.setLatitude(driver_updated_latlng.latitude);
//            update_location.setLongitude(driver_updated_latlng.longitude);
//
//            Log.d("driver_bearing", "---------------------------------CHECK Bearing----------------------------------------------------");
//            Log.d("driver_bearing", "Previous Latitude: " + previous_location.getLatitude() + "Previous Longitude: " + previous_location.getLongitude());
//            Log.d("driver_bearing", "Update Latitude: " + update_location.getLatitude() + "Update Longitude: " + update_location.getLongitude());
//            Log.d("driver_bearing", "----------------------------------------------------------------------------------------------");
//
//            float bearing = previous_location.bearingTo(update_location);
//            Log.d("driver_bearing", "Bearing: " + bearing);
//            /*here*/
//            driver_marker.setRotation(bearing);
//            /*if(bearing > 0.0){
//
//            }*/
//
//            //Moving the driver
//            smoothlyMoveTaxi(driver_updated_latlng);
//
//        } catch (NumberFormatException e) {
//
//        }
//
//        Log.d("driver_movement_pick_up", "---------------------------------After----------------------------------------------------");
//        Log.d("driver_movement_pick_up", "Update Latitude: " + driver_updated_latlng.latitude + "Update Longitude" + driver_updated_latlng.longitude);
//        Log.d("driver_movement_pick_up", "Latitude Pick Up: " + driver_pick_up_latlng.latitude + "Longitude Pick Up" + driver_pick_up_latlng.longitude);
//        Log.d("driver_movement_pick_up", "----------------------------------------------------------------------------------------------");
//
//        /*if(driver_previous_updated_latlng != driver_updated_latlng){
//
//        }*/
//        /*LatLng passenger_latlng = new LatLng(Double.parseDouble(booking_details.get(0).getPick_up_latitude()),
//                Double.parseDouble(booking_details.get(0).getPick_up_longitude()));*/
//
//        /*if(driver_marker != null){
//            driver_marker.remove();
//            driver_marker = null;
//        }
//
//        MarkerOptions marker_options = new MarkerOptions();
//        marker_options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cabbii));
//        marker_options.position(driver_latlng);
//
//        driver_marker = google_map.addMarker(marker_options);*/
//        /*driver_marker.setPosition(driver_latlng);*/
//    }
//
//    private void smoothlyMoveTaxi(LatLng final_position) {
//        final LatLng startPosition = driver_marker.getPosition();
//        final LatLng finalPosition = final_position;
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
//        final float durationInMs = 5000;
//        final boolean hideMarker = false;
//
//        handler.post(new Runnable() {
//            long elapsed;
//            float t;
//            float v;
//
//            @Override
//            public void run() {
//                // Calculate progress using interpolator
//                elapsed = SystemClock.uptimeMillis() - start;
//                t = elapsed / durationInMs;
//
//                LatLng currentPosition = new LatLng(
//                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
//                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);
//
//                driver_marker.setPosition(currentPosition);
//
//                // Repeat till progress is complete.
//                if (t < 1) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                } else {
//                    if (hideMarker) {
//                        driver_marker.setVisible(false);
//                    } else {
//                        driver_marker.setVisible(true);
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onDuration(String duration) {
//        is_duration_is_get = true;
//        //Log.d("hantash_duration", "duration: "+duration);
//        //Log.d("hantash_duration", "duration Size: "+duration.length());
//
//        try {
//            if (duration.length() <= 7) {
//                Log.d("hantash_duration", "duration: " + duration);
//                tv_eta_value.setText(duration);
//            }
//        } catch (NullPointerException e) {
//
//        }
//    }
//
//    @Override
//    public void onDistance(String distance) {
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
//
//        if (doAsynchronousTask == null) {
//            callAsynchronousTask();
//        }
//
//        if (doAsynchronousTask_eta == null) {
//            callAsynchronousTaskETA();
//        }
//
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    // handle back button's click listener
//                    //startActivity(new Intent(getActivity(), MainActivityDrawer.class));
//                    Intent startMain = new Intent(Intent.ACTION_MAIN);
//                    startMain.addCategory(Intent.CATEGORY_HOME);
//                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(startMain);
//                    return true;
//                }
//                return false;
//            }
//        });
//
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        try {
//            if (doAsynchronousTask != null) {
//                timer.cancel();
//                doAsynchronousTask.cancel();
//                doAsynchronousTask = null;
//            }
//
//            if (doAsynchronousTask_eta != null) {
//                timer_eta.cancel();
//                doAsynchronousTask_eta.cancel();
//                doAsynchronousTask_eta = null;
//            }
//            getActivity().unregisterReceiver(mMessageReceiver);
//        } catch (Exception e) {
//
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        try {
//            Log.d("hantash_frag_life_cycle", "Request Accept is destroyed");
//            //getActivity().unregisterReceiver(mMessageReceiver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//    }
//
//    public static String getStringFromLocation(double lat, double lng)
//            throws ClientProtocolException, IOException, JSONException {
//
//        String address_loc = null;
//
//        String address = String
//                .format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
//                        + Locale.getDefault().getCountry(), lat, lng);
//
//        HttpGet httpGet = new HttpGet(address);
//        HttpClient client = new DefaultHttpClient();
//        HttpResponse response;
//        StringBuilder stringBuilder = new StringBuilder();
//
//        List<Address> retList = null;
//
//        response = client.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//        InputStream stream = entity.getContent();
//        int b;
//        while ((b = stream.read()) != -1) {
//            stringBuilder.append((char) b);
//        }
//
//        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
//
//        retList = new ArrayList<Address>();
//
//        if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
//            JSONArray results = jsonObject.getJSONArray("results");
//            for (int i = 0; i < results.length(); i++) {
//                JSONObject result = results.getJSONObject(i);
//                String indiStr = result.getString("formatted_address");
//
//                if (i == 0) {
//                    address_loc = indiStr;
//                }
//
//                Address addr = new Address(Locale.getDefault());
//                addr.setAddressLine(0, indiStr);
//                retList.add(addr);
//            }
//        }
//
//        return address_loc;
//    }
//
//    private class GetAddressBackEndTask extends AsyncTask<String, Void, String> {
//
//        String pick_up_address;
//
//        @Override
//        protected void onPreExecute() {
//            if (!is_progress_dialog_shown) {
//                progressDialog_address = ProgressDialog.show(getActivity(), "",
//                        "Loading...", true);
//                is_progress_dialog_shown = true;
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//
//                try {
//
//                    if (is_pick_up) {
//                        pick_up_address = getStringFromLocation(latitude_pick_up, longitude_pick_up);
//                    } else if (is_destination) {
//                        pick_up_address = getStringFromLocation(latitude_destination, longitude_destination);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    if (progressDialog_address.isShowing()) {
//                        progressDialog_address.dismiss();
//                    }
//                }
//
//            } catch (IOException e) {
//                System.out.println(e);
//                if (progressDialog_address.isShowing()) {
//                    progressDialog_address.dismiss();
//                }
//            }
//            return pick_up_address;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            //is_address_string_get = true;
//            //Toast.makeText(getActivity(), "background is called", Toast.LENGTH_LONG).show();
//            if (is_destination) {
//                tv_destination.setText(result);
//
//                SharedPreferences preferences2 =
//                        getActivity().getSharedPreferences(CONSTANTS.PREFERENCE_NAME_PICK_UP_DESTINATION, MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences2.edit();
//                editor.putString(CONSTANTS.PREFERENCE_DESTINATION_LOCATION_EXTRA, result);
//                editor.commit();
//
//                if (progressDialog_address.isShowing()) {
//                    progressDialog_address.dismiss();
//                }
//
//            } else if (is_pick_up) {
//                tv_pick_up_location.setText(result);
//                is_pick_up = false;
//
//                SharedPreferences preferences2 =
//                        getActivity().getSharedPreferences(CONSTANTS.PREFERENCE_NAME_PICK_UP_DESTINATION, MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences2.edit();
//                editor.putString(CONSTANTS.PREFERENCE_PICK_UP_LOCATION_EXTRA, result);
//                editor.commit();
//
//                is_destination = true;
//                new GetAddressBackEndTask().execute();
//            }
//            //idCurrentAddress.setText(result);
//        }
//    }
//
//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            String status = intent.getStringExtra("status");
//
//            if (status.equals("ARRIVED")) {
//                if (!is_arrived) {
//                    is_arrived = true;
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone r = RingtoneManager.getRingtone(context, notification);
//                    r.play();
//
//                    //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
//
//                    if (doAsynchronousTask != null) {
//                        timer.cancel();
//                        doAsynchronousTask.cancel();
//                        doAsynchronousTask = null;
//                    }
//
//                    if (doAsynchronousTask_eta != null) {
//                        timer_eta.cancel();
//                        doAsynchronousTask_eta.cancel();
//                        doAsynchronousTask_eta = null;
//                    }
//
//                    StartRideFragment start_ride = new StartRideFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(CONSTANTS.BOOKING_EXTRA, booking_details);
//                    bundle.putBoolean(CONSTANTS.IS_TOUR_COMPLETED_FROM_NOTIFICATION, false);
//                    start_ride.setArguments(bundle);
//
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.container_body, start_ride);
//                    fragmentTransaction.commitAllowingStateLoss();
//                }
//            } else if (status.equals("BROKEN DOWN")) {
//                Toast.makeText(getActivity(), "Vehicle Has Broken Down", Toast.LENGTH_LONG).show();
//                startActivity(new Intent(getActivity(), MainActivityDrawer.class));
//            }
//        }
//    };
//
//    private class ConvertImageURL extends AsyncTask<String, Void, Void> {
//        Bitmap image;
//
//        @Override
//        protected Void doInBackground(String... params) {
//            String image_url = CONSTANTS.ImageUrl + params[0];
//            Log.d("hantash_linez", "image url" + image_url);
//            try {
//                URL url = new URL(image_url);
//                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//
//            } catch (Exception e) {
//                System.out.println(e);
//                Log.d("hantash_linez", "Error Message" + e);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            //iv_nav_passenger.setImageBitmap(image);
//            iv_driver_pic.setImageBitmap(image);
//            //Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
//        }
//
//    }
//
//}
