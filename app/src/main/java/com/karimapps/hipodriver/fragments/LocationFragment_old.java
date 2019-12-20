package com.karimapps.hipodriver.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.karimapps.hipodriver.MapModules.DirectionFinder;
import com.karimapps.hipodriver.MapModules.DirectionFinderListener;
import com.karimapps.hipodriver.MapModules.Route;
import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.LoginActivity;
import com.karimapps.hipodriver.activities.MainActivity;
import com.karimapps.hipodriver.models.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import universal.UniversalConstants;
import universal.Utils;

import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class LocationFragment_old extends Fragment
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        View.OnClickListener, DirectionFinderListener
{
    private Context context;
    private CoordinatorLayout fragment;

    /*bottom sheet*/
    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView tv_name;
    private TextView tv_day, tv_time, tv_location, tv_client;
    private TextView tv_confirm_name;
    private RadioGroup rg_name, rg_vehicle_clean, rg_fuel, rg_previous_client;
    private RadioButton rb_name_yes, rb_name_no, rb_vehicle_yes, rb_vehicle_no,
            rb_fuel_yes, rb_fuel_no, rb_client_ok, rb_client_not_ok;
    private FloatingActionButton btn_submit;
    private boolean name_checked, fuel_checked,
            vehicle_clean_checked, previous_client_checked;
    private boolean name_yes, fuel_yes, vehicle_clean_yes, previous_client_ok;

    Bundle savedInstance;
    private Location mLastLocation;
    private GoogleMap map_object;
    private GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private double driver_current_latitude;
    private double driver_current_longitude;
    private Marker marker_driver, marker_station;

    private List<Station> stations;
    Station station;

    private static final long UPDATE_INTERVAL = 1000, FASTEST_INTERVAL = 1000,
            DISPLACEMENT = 10; // = 1 seconds

    private boolean is_map_location_available = false;
    private TimerTask doAsynchronousTask;
    private Timer timer;
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private MapView map_location;
    private ImageView btn_logout, btn_refresh;
    /*firebase*/
    private FirebaseDatabase firebaseDatabase;


    /*for routing*/
    private int station_number = 0;
    private boolean stations_available = false;
    Marker custome_marker_object;
    private Marker marker = null;

    double drop_lat = 31.4472539, drop_long = 74.3361504;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Marker maker_car;

    public LocationFragment_old() {
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_fragment, container, false);
        context = getActivity();

        bindViews(view);
        stations = new ArrayList<>();
        FirebaseApp.initializeApp(context);

        showMap(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        /*GET PLAN STATIONS*/
        /*getPlanStations();*/
        ((MainActivity) getActivity()).setActionBarTitle("Hipo Driver App");
        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        } else {
            if (checkMobileGPSState()) {
                if (checkPlayServices()) {
                    buildGoogleApiClient();
                    createLocationRequest();
                    btn_refresh.setVisibility(View.GONE);

                }
                is_map_location_available = true;
            } else {
                showGPSEnableDialog();
            }
        }

        /*if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            if (latitude <= 0 || longitude <= 0) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                Log.d("hantash_my_location", "GPS Tracker is Updated");
            }
        } else {
            gps.showSettingsAlert();
        }*/
        return view;

    }

    /*bind all the views*/
    private void bindViews(View view)
    {
        /*show the navigation drawer toolbar*/
        MainActivity.toolbar.setVisibility(View.VISIBLE);

        map_location = view.findViewById(R.id.map_location);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        fragment = view.findViewById(R.id.fragment);
        tv_name = view.findViewById(R.id.tv_name);
        tv_day = view.findViewById(R.id.tv_day);
        tv_time = view.findViewById(R.id.tv_time);
        tv_client = view.findViewById(R.id.tv_client);
        tv_confirm_name = view.findViewById(R.id.tv_confirm_name);
        /*radio groups*/
        rg_name = view.findViewById(R.id.rg_name);
        rg_fuel = view.findViewById(R.id.rg_fuel);
        rg_previous_client = view.findViewById(R.id.rg_previous_client);
        rg_vehicle_clean = view.findViewById(R.id.rg_vehicle_clean);

        /*radio buttons*/
        rb_name_yes = view.findViewById(R.id.rb_name_yes);
        rb_name_no = view.findViewById(R.id.rb_name_no);
        rb_fuel_yes = view.findViewById(R.id.rb_fuel_yes);
        rb_fuel_no = view.findViewById(R.id.rb_fuel_no);
        rb_vehicle_yes = view.findViewById(R.id.rb_vehicle_yes);
        rb_vehicle_no = view.findViewById(R.id.rb_vehicle_no);
        rb_client_ok = view.findViewById(R.id.rb_client_yes);
        rb_client_not_ok = view.findViewById(R.id.rb_client_no);

        btn_submit = view.findViewById(R.id.btn_submit);

        btn_logout = view.findViewById(R.id.btn_logout);
        btn_refresh = view.findViewById(R.id.btn_refresh);

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                /*transaction.detach(LocationFragment.this).attach(LocationFragment.this);*/
                transaction.replace(R.id.container, new LocationFragment_old());
                transaction.commit();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Utils.savePreferencesBool(UniversalConstants.LOGIN, false, context);
                activity.startActivity(new Intent(context, LoginActivity.class));
                activity.finish();
            }
        });

        setOnClicks();
        setValues();
    }

    /*set on clicks*/
    private void setOnClicks()
    {
        rb_name_no.setOnClickListener(this);
        rb_name_yes.setOnClickListener(this);
        rb_fuel_no.setOnClickListener(this);
        rb_fuel_yes.setOnClickListener(this);
        rb_vehicle_no.setOnClickListener(this);
        rb_vehicle_yes.setOnClickListener(this);
        rb_client_not_ok.setOnClickListener(this);
        rb_client_ok.setOnClickListener(this);

        btn_submit.setOnClickListener(this);
    }

    private void setValues()
    {
        /*set actio bar title*/
        ((MainActivity) getActivity())
                .setActionBarTitle("Hipo Driver App");

        tv_name.setText(Utils.getPreferences(UniversalConstants.USERNAME, context));
        tv_confirm_name.setText(getString(R.string.confirm_your_name) +" "+
                Utils.getPreferences(UniversalConstants.USERNAME, context)+
                " "+getString(R.string.confirm_vehicle_ready));
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.rb_name_no:
                name_checked = true;
                name_yes = false;
                break;
            case R.id.rb_name_yes:
                name_checked = true;
                name_yes = true;
                break;
            case R.id.rb_fuel_no:
                fuel_checked = true;
                fuel_yes = false;
                break;
            case R.id.rb_fuel_yes:
                fuel_checked = true;
                fuel_yes = true;
                break;
            case R.id.rb_vehicle_no:
                vehicle_clean_checked= true;
                vehicle_clean_yes = false;
                break;
            case R.id.rb_vehicle_yes:
                vehicle_clean_checked= true;
                vehicle_clean_yes = true;
                break;
            case R.id.rb_client_no:
                previous_client_checked = true;
                previous_client_ok = false;
                break;
            case R.id.rb_client_yes:
                previous_client_checked = true;
                previous_client_ok = true;
                break;

            case R.id.btn_submit:
                /*name check*/
                if (!name_checked)
                {
                    Utils.showSnackbar(fragment, getString(R.string.error_name_check));
                    break;
                }
                /*fuel check*/
                if (!fuel_checked)
                {
                    Utils.showSnackbar(view, getString(R.string.error_fuel_check));
                    break;
                }
                /*vehicle clean check*/
                if (!vehicle_clean_checked)
                {
                    Utils.showSnackbar(view, getString(R.string.error_vehicle_clean_check));
                    break;
                }
                /*client check*/
                if (!previous_client_checked)
                {
                    Utils.showSnackbar(view, getString(R.string.error_client_check));
                    break;
                }
                if (!checkMobileGPSState())
                {
                    showGPSEnableDialog();
                    break;
                }
                /*Utils.showSnackbar(view, "Ok, all checked");*/
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
        }
    }

    /*get all the stations in the plan*/
    private void getPlanStations() {
        String url = Utils.GET_PLAN_STATIONS;
        final ProgressDialog dialog = ProgressDialog.show(context, "Fetching stations data", "Please wait...");
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        JSONObject jsonObject;
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray dataarray = (JSONArray) data.get("PlanStations");

                            for (int i = 0; i < dataarray.length(); i++) {
                                jsonObject = dataarray.getJSONObject(i);
                                Gson g = new Gson();
                                station = g.fromJson(jsonObject.toString(), Station.class);

                                /*station = new Station(id, name, passengers, lat, lon, etd);*/
                                stations.add(station);
                            }
                            dialog.dismiss();
                            /*Utils.showToast(context, "Sattions: "+stations.size());*/
                            for (int i = stations.size() - 1; i > -1; i--) {
                                Station station = stations.get(i);
                                LatLng station_location = new LatLng(Double.parseDouble(station.getLat()),
                                        Double.parseDouble(station.getLong()));

                                BitmapDescriptor icon_driver = BitmapDescriptorFactory.fromResource(R.drawable.marker_station);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(station_location);
                                markerOptions.icon(icon_driver);
                                markerOptions.title("" + (i + 1)).snippet(station.getTotalPassengers() + " " + getString(R.string.txt_passengers) + "\n"
                                        + station.getStationName());
                                markerOptions.flat(true);
                                marker_station = map_object.addMarker(markerOptions);
                                marker_station.setTag(station.getStationPlanId());
                                marker_station.showInfoWindow();

                                map_object.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                    @Override
                                    public View getInfoWindow(Marker marker) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(final Marker marker) {
                                        LinearLayout info = new LinearLayout(context);
                                        info.setOrientation(LinearLayout.VERTICAL);

                                        TextView title = new TextView(context);
                                        title.setTextColor(Color.BLACK);
                                        title.setGravity(Gravity.CENTER);
                                        title.setTypeface(null, Typeface.BOLD);
                                        title.setText(marker.getTitle());

                                        TextView snippet = new TextView(context);
                                        snippet.setTextColor(Color.GRAY);
                                        snippet.setGravity(Gravity.CENTER);
                                        snippet.setText(marker.getSnippet());
                                        info.addView(title);
                                        info.addView(snippet);

                                        snippet.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Utils.showToast(context, "You touched the text");
                                            }
                                        });

                                        return info;
                                    }
                                });
                                /*map_object.addMarker(new MarkerOptions().position(station_location)
                                .title(station.getStationName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_station)));
                                map_object.animateCamera(CameraUpdateFactory.newLatLng(station_location));*/
                            }
                            stations_available = true;
                            for (int i=0; i<stations.size(); i++)
                            {
                                try {
//            context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));

                                    LatLng origin;

                                    if (i == 0)
                                    {
                                        origin = new LatLng(driver_current_latitude, driver_current_longitude);
                                    }
                                    else
                                    {
                                        origin = new LatLng(Double.parseDouble(stations.get(i-1).getLat()),Double.parseDouble(stations.get(i-1).getLong()));
                                    }
                                    /*LatLng origin = new LatLng(driver_current_latitude, driver_current_longitude);*/
                                    LatLng destination = new LatLng(Double.parseDouble(stations.get(i).getLat()),Double.parseDouble(stations.get(i).getLong()));

                                    DirectionFinder directionFinder = new DirectionFinder((DirectionFinderListener) LocationFragment_old.this,
                                            origin, destination, "path");



                                    Log.d("showDirection", "showDirection ");
                                    directionFinder.showDirection();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Utils.showToast(context, "Your route is ready. Go!");

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
                            Utils.savePreferences(UniversalConstants.LOGGED_IN, "false", context);
                            Utils.savePreferencesBool(UniversalConstants.LOGIN, false, context);
                            startActivity(new Intent(context, LoginActivity.class));

//                            Utils.showCustomDialog(context, "Error code: " + code);
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

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            driver_current_latitude = mLastLocation.getLatitude();
            driver_current_longitude = mLastLocation.getLongitude();
            if (marker_driver == null) {
                BitmapDescriptor icon_driver = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_driver);
                /*LatLng driver_position = new LatLng(31.4363632, 74.3613435);*/
                LatLng driver_position = new LatLng(driver_current_latitude, driver_current_longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(driver_position);
                markerOptions.icon(icon_driver);
                markerOptions.flat(true);
                marker_driver = map_object.addMarker(markerOptions);
                /*map_object.animateCamera(CameraUpdateFactory.newLatLngZoom(driver_position, 18));*/
                /*map_object.moveCamera(CameraUpdateFactory.newLatLngZoom(driver_position, 16));*/

                if (!stations_available) {
                    getPlanStations();
                }
            }
        }
        startLocationUpdates();

        /*getStations();*/
    }

    private void getStations()
    {
        Station ghazi_chowk = new Station("31.4569168","74.3491059");
        Station kalma_chowk = new Station("31.5035372","74.3296502");
        Station barkat_market = new Station("31.5010202","74.3194017");
        Station bhekewal_mor = new Station("31.5091131","74.3006766");
        Station canal_station = new Station("31.519486","74.3244473");
        stations.add(ghazi_chowk);
        stations.add(kalma_chowk);
        stations.add(barkat_market);
        stations.add(bhekewal_mor);
        stations.add(canal_station);

        /*stations_available = true;*/
        /*draw the route*/

        for (int i=0; i<stations.size(); i++)
        {
        try {
//            context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));

            LatLng origin;

            if (i == 0)
            {
                origin = new LatLng(driver_current_latitude, driver_current_longitude);
            }
            else
            {
                origin = new LatLng(Double.parseDouble(stations.get(i-1).getLat()),Double.parseDouble(stations.get(i-1).getLong()));
            }
            /*LatLng origin = new LatLng(driver_current_latitude, driver_current_longitude);*/
            LatLng destination = new LatLng(Double.parseDouble(stations.get(i).getLat()),Double.parseDouble(stations.get(i).getLong()));

            DirectionFinder directionFinder = new DirectionFinder((DirectionFinderListener) LocationFragment_old.this,
                    origin, destination, "path");

            

            Log.d("showDirection", "showDirection ");
            directionFinder.showDirection();


        } catch (Exception e) {
            e.printStackTrace();
        }
        }
        Utils.showToast(context, "Your route is ready. Go!");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        /*UPDATE LATLONG IN FIREBASE*/
        HashMap<String, Object> location_data = new HashMap<>();
        location_data.put("latitude", location.getLatitude());
        location_data.put("longitude", location.getLongitude());
        firebaseDatabase.getReference("drivers")
                .child(Utils.getPreferences(UniversalConstants.DRIVER_UID, context))
                .child("tempdriver")
                .updateChildren(location_data);

        try {
            LatLng updated_driver_latlng = new LatLng(driver_current_latitude, driver_current_longitude);

            Location previous_location = new Location("");
            previous_location.setLatitude(marker_driver.getPosition().latitude);
            previous_location.setLongitude(marker_driver.getPosition().longitude);

            Location update_location = new Location("");
            update_location.setLatitude(updated_driver_latlng.latitude);
            update_location.setLongitude(updated_driver_latlng.longitude);

            float distance_covered = previous_location.distanceTo(update_location);

            Utils.showToastTest(context, "Latitude: "+driver_current_latitude+"\n"+
                    "Longitude: "+driver_current_longitude);

            //ToastMessage.showSampleToast(getActivity(), "Distance: "+distance_covered);



            if(distance_covered >= UniversalConstants.DISTANCE_TO_ROTATE){
                float bearing = previous_location.bearingTo(update_location);
                Log.d("driver_bearing", "Bearing: " + bearing);
                marker_driver.setRotation(bearing);
            }

            smoothlyMoveDriver(marker_driver, updated_driver_latlng);

            Log.d("update_movement", "--------------------------------------------------------------------");
            Log.d("update_movement", "----------------------Vehicle Movement Handler----------------------");
            Log.d("update_movement", "New Latitude: "+updated_driver_latlng.latitude+
                    "New Longitude: "+updated_driver_latlng.latitude);
            Log.d("update_movement", "--------------------------------------------------------------------");
        } catch (Exception e) {
        }
    }

    /*map is ready*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Utils.showToastTest(context, "Map is ready");

        map_object = googleMap;
        map_object.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                Fragment f = new StationPassengersFragment();
                Bundle args = new Bundle();
                args.putString(UniversalConstants.STATION_NUMBER, marker.getTag().toString());
                f.setArguments(args);
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.replace(R.id.container, f);
                t.addToBackStack(null);
                t.commit();
            }
        });
        // Add a marker in Sydney and move the camera
        if (marker_driver == null) {
            BitmapDescriptor icon_driver = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_driver);
            LatLng driver_position = new LatLng(31.4363632, 74.3613435);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(driver_position);
            markerOptions.icon(icon_driver);
            markerOptions.flat(true);
            marker_driver = map_object.addMarker(markerOptions);
            /*map_object.animateCamera(CameraUpdateFactory.newLatLngZoom(driver_position, 17));*/
        }
        /*LatLng sydney = new LatLng(31.4363632, 74.3613435);
        map_object.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map_object.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private void showMap(Bundle savedInstanceState) {
        map_location.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map_location.getMapAsync(this);
    }

    /*build google api client*/
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Fix first time run app if permission doesn't grant yet so can't get anything
        mGoogleApiClient.connect();
    }

    /*create location request*/
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UniversalConstants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(UniversalConstants.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*mLocationRequest.setSmallestDisplacement(UniversalConstants.DISPLACEMENT);*/
    }

    @Override
    public void onResume() {
        super.onResume();

            map_location.onResume();

        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
            createLocationRequest();
            Utils.showToastTest(getActivity(), "GOOGLE API CLIENT CONNECT IF: " + mGoogleApiClient.isConnected());
        } else {
            mGoogleApiClient.connect();
            Utils.showToastTest(getActivity(), "GOOGLE API CLIENT CONNECT: " + mGoogleApiClient.isConnected());
        }
        is_map_location_available = true;
        /*if (is_map_location_available) {
            if (doAsynchronousTask == null) {
                startDriverMovementHandler();

                Log.d("handler_state", "Movement Vehicle Handler is started");
            }*/

            /*if(asynchronous_task_driver_location == null){
                startDriverLocationHandler();
                Log.d("handler_state", "Driver location Update Handler is started");
            }*/
        }

    @Override
    public void onPause() {
        super.onPause();

        // stop location updates
        if (mGoogleApiClient != null  &&  mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        map_location.onPause();
    }

    @Override
    public void onStop() {
        is_map_location_available = false;
        map_location.onStop();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map_location.onDestroy();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map_location.onLowMemory();
    }

    /*check gps state*/
    private boolean checkMobileGPSState()
        {
        boolean is_gps_enabled = false;
        //boolean is_network_enabled = false;

        LocationManager location_manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        is_gps_enabled = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //is_network_enabled = location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return is_gps_enabled;
    }


    /*show enable gps dialog*/
    private void showGPSEnableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("GPS Location Required");
        builder.setMessage("Please Enable GPS Location in order to use this application.");

        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    /*permissions to request*/
    private ArrayList<String> permissionsToRequest
    (ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /*has permissions*/
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /*check play services*/
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), UniversalConstants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
                Utils.showToast(getActivity(), "This device does not supported");
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    /*set position of the driver*/
    private void setDriverPosition() {
        /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Utils.showToastTest(getActivity(), "LOcation longitude: "+mLastLocation);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            driver_current_latitude = latitude;
            driver_current_longitude = longitude;

            if(marker_driver == null){
                BitmapDescriptor icon_driver = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_driver);
                LatLng driver_position = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(driver_position);
                markerOptions.icon(icon_driver);
                markerOptions.flat(true);
                marker_driver = map_object.addMarker(markerOptions);
               /* map_object.animateCamera(CameraUpdateFactory.newLatLngZoom(driver_position, 19));*/
            }

            SharedPreferences preferences =
                    getActivity().getSharedPreferences(UniversalConstants.SHARED_PREFERENCE_APP, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(UniversalConstants.PREFERENCE_EXTRA_USER_CURRENT_LATITUDE, latitude+"");
            editor.putString(UniversalConstants.PREFERENCE_EXTRA_USER_CURRENT_LONGITUDE, longitude+"");
            editor.commit();

            //HelperToastMessage.showSampleToast(getActivity(), latitude + " / " + longitude);

        } else {
            Toast.makeText(getActivity(), "Couldn't get the location. Make sure location is enable on the device", Toast.LENGTH_LONG).show();
        }
    }

    /*start location updates*/
    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        /*mLocationRequest.setSmallestDisplacement(UniversalConstants.DISPLACEMENT);
*/
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /*move driver smoothly on the map*/
    private void smoothlyMoveDriver(final Marker driver_marker, final LatLng final_position) {
        final LatLng startPosition = driver_marker.getPosition();
        final LatLng finalPosition = final_position;
        final Handler handler = new Handler();
        Projection proj = map_object.getProjection();
        Point startPoint = proj.toScreenLocation(marker_driver.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        final float durationInMs = 500;
        final boolean hideMarker = false;

        if (marker_driver == null) {
            BitmapDescriptor icon_driver = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_driver);
            /*LatLng driver_position = new LatLng(31.4363632, 74.3613435);*/
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(final_position);
            markerOptions.icon(icon_driver);
            markerOptions.flat(true);
            marker_driver = map_object.addMarker(markerOptions);
        }


            handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                /*t = interpolator.getInterpolation((float) elapsed
                        / durationInMs);*/
                double lng = t * final_position.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * final_position.latitude + (1 - t)
                        * startLatLng.latitude;

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                driver_marker.setPosition(currentPosition);
                /*map_object.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));*/

                // Repeat till progress is complete.
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);

                    /*Utils.showToast(context, "updatedaflsaljhfd");*/
                } else {
                    if (hideMarker) {
                        driver_marker.setVisible(false);
                    } else {
                        driver_marker.setVisible(true);
                    }
                }
                /*map_location.onResume();*/
            }
        });
    }

    private void startDriverMovementHandler() {
        final Handler handler = new Handler();
        timer = new Timer();
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            LatLng updated_driver_latlng = new LatLng(driver_current_latitude, driver_current_longitude);

                            HashMap<String, Object> location_data = new HashMap<>();
                            location_data.put("latitude", driver_current_latitude);
                            location_data.put("longitude", driver_current_longitude);
                            firebaseDatabase.getReference("drivers")
                                    .child("tempdriver")
                                    .updateChildren(location_data);

                            Location previous_location = new Location("");
                            previous_location.setLatitude(marker_driver.getPosition().latitude);
                            previous_location.setLongitude(marker_driver.getPosition().longitude);

                            Location update_location = new Location("");
                            update_location.setLatitude(updated_driver_latlng.latitude);
                            update_location.setLongitude(updated_driver_latlng.longitude);

                            float distance_covered = previous_location.distanceTo(update_location);

                            Utils.showToastTest(context, "Latitude: "+driver_current_latitude+"\n"+
                            "Longitude: "+driver_current_longitude);

                            //ToastMessage.showSampleToast(getActivity(), "Distance: "+distance_covered);

                            if(distance_covered >= UniversalConstants.DISTANCE_TO_ROTATE){
                                float bearing = previous_location.bearingTo(update_location);
                                Log.d("driver_bearing", "Bearing: " + bearing);
                                marker_driver.setRotation(bearing);
                            }

                            marker_driver.setPosition(updated_driver_latlng);

                            /*smoothlyMoveDriver(marker_driver, updated_driver_latlng);*/

                            Log.d("update_movement", "--------------------------------------------------------------------");
                            Log.d("update_movement", "----------------------Vehicle Movement Handler----------------------");
                            Log.d("update_movement", "New Latitude: "+updated_driver_latlng.latitude+
                                    "New Longitude: "+updated_driver_latlng.latitude);
                            Log.d("update_movement", "--------------------------------------------------------------------");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 4000);
    }

    @Override
    public void onDirectionFinderStart(String operationName)
    {
        if (operationName.equals("path")) {
            progressDialog = ProgressDialog.show(context, "Please wait.",
                    "Finding direction..!", true);

            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }

            progressDialog.dismiss();
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes, String operationName)
    {
        try {
            station_number++;
            if (operationName.equals("path")) {
                polylinePaths = new ArrayList<>();
                originMarkers = new ArrayList<>();
                destinationMarkers = new ArrayList<>();

                for (Route route : routes) {


                    if (marker != null) {
                        marker.remove();
                        marker = null;
                    }
                    map_object.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(driver_current_latitude, driver_current_longitude), 16));
                    maker_car = map_object.addMarker(new MarkerOptions()
                            .icon(Utils.bitmapDescriptorFromVector(context, R.drawable.ic_my_location))
                            .title(route.startAddress)
                            .position(route.startLocation));
                    custome_marker_object = map_object.addMarker(new MarkerOptions()
                            .title(route.endAddress)
                            .position(route.endLocation)
                            .icon(Utils.bitmapDescriptorFromVector(context, R.drawable.ic_bookmark)));


                    originMarkers.add(maker_car);


                    /*List<PatternItem> pattern = Arrays.<PatternItem>asList(
                            new Dot(), new Gap(20), new Dash(30), new Gap(20));*/
                    List<PatternItem> pattern = Arrays.<PatternItem>asList(
                            new Gap(10), new Dash(30));
                    /*mPolyline.setPattern(pattern);*/

                    PolylineOptions polylineOptions;
                    if (station_number == 1) {
                        polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.GREEN).
                                width(7).
                                pattern(pattern);
                    }
                    else {
                        polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.BLUE).
                                width(7);
                    }

                    for (int i = 0; i < route.points.size(); i++)
                        polylineOptions.add(route.points.get(i));

                    polylinePaths.add(map_object.addPolyline(polylineOptions));
//                    Utils.savePreferences("polyline", "false", context);
                }
            } /*else {

                for (Route route : routes) {


                    String content = route.duration.text;

                    String distance = route.distance.text;
 try {

dis = Double.parseDouble(distance);
                        dis_mile = dis * 0.621371;
                    } catch (Exception e) {

                    }


                    String regex = "mins";
                    String duration = content.replaceAll(regex, "");
                    tv_marker.setText(duration + "");

                    slide_btn_text2.setText("ETA" + duration + " Distance "+dis_mile);
                    TextViewDistanceEta.showText = "" + dis_mile + "Miles" + duration + "Min";

Log.d("route", "route distnace" + route.distance.value + "duration " + route.duration);

                }
            }*/


        } catch (Exception e) {
            e.printStackTrace();
            Utils.showCustomDialog(context, e.getMessage());
        }
                }




            }
