package com.karimapps.hipodriver.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.maps.android.SphericalUtil;
import com.karimapps.hipodriver.MapModules.DirectionFinder;
import com.karimapps.hipodriver.MapModules.DirectionFinderListener;
import com.karimapps.hipodriver.MapModules.Route;
import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.LoginActivity;
import com.karimapps.hipodriver.activities.MainActivity;
import com.karimapps.hipodriver.adapters.PassengersAdapter;
import com.karimapps.hipodriver.models.Passenger;
import com.karimapps.hipodriver.models.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import universal.UniversalConstants;
import universal.HelperAlertDialogMessage;
import universal.HelperProgressDialog;
import universal.Utils;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class LocationFragment extends Fragment
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        DirectionFinderListener
{
    private Context context;

    /*for driver some info*/
    private TextView tv_line_num_label;
    private TextView tv_station_distance, tv_station_time, tv_station_name;
    private String station_distance, station_time, station_name;
    private int active_station;
    private FloatingActionButton btn_passengers;

    private Marker now;
    private MapView map_driver;
    private GoogleMap map_obj;
    private Bundle _savedInstanceState;

    /*parameters for location*/
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private Location mLastLocation;
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private static final long UPDATE_INTERVAL = 1000, FASTEST_INTERVAL = 1000,
            DISPLACEMENT = 10;

    /*show driver position parameters*/
    private double driver_current_latitude;
    private double driver_current_longitude;
    private Marker marker_driver;
    private LatLng driver_position;

    /*parameters for stations*/
    private int station_number = 0;
    boolean stations_available=false;
    private ArrayList<Station> stations;
    private Station station;
    private Marker marker_station, custome_marker_object, maker_car, marker;
    private ProgressDialog progressDialog_stations;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    //----------------------------------------------------------------------------

    private ArrayList<Station> planStation=new ArrayList<Station>();
    private String last_station_lat,last_station_lng,station_id;
    private boolean voice_message=false;
    private boolean ok_pressed=false;


    //----------------------------------------------------------------------------

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    //Passenger work
    private PassengersAdapter adapter;
    private RecyclerView rv_passengers;
    private TextView tv_count;
    private List<Passenger> passengers;
    private FloatingActionButton btn_back;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_location, container,false);
        _savedInstanceState = savedInstanceState;

        context = getActivity();
        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.app_name));

        map_driver = view.findViewById(R.id.map_driver);
        tv_line_num_label = view.findViewById(R.id.tv_line_num_label);

        tv_station_name = view.findViewById(R.id.tv_station_name);
        tv_station_distance = view.findViewById(R.id.tv_station_distance);
        tv_station_time = view.findViewById(R.id.tv_station_time);
        btn_passengers = view.findViewById(R.id.btn_passengers);

        tv_line_num_label.setText("Line: "+Utils.getPreferences(UniversalConstants.DRIVER_PLANLANE_ID, context));

        btn_passengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Bundle args = new Bundle();
                args.putString(UniversalConstants.STATION_NUMBER, stations.get(active_station).getStationPlanId());
                Fragment f = new StationPassengersFragment();
                f.setArguments(args);
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.replace(R.id.container, f);
                t.commit();
            }
        });

        Button btn_showmap = view.findViewById(R.id.btn_showmap);
        btn_showmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CameraPosition cameraPosition = new CameraPosition.Builder().
                        target(driver_position).
                        /*tilt(90).*/
                                bearing(-30).
                                zoom(18).
                                build();
                map_obj.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });
        showMap(_savedInstanceState);
        //---------------------------------location service---------------------------------------------------------

        if(!isLocationEnabled(context)){
            showGPSEnableDialog();
        }
//        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch(Exception ex) {}
//
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}
//
//        if(!gps_enabled && !network_enabled) {
//            // notify user
//           showGPSEnableDialog();
//        }

        //-------------------------------Internet Access--------------------------------------------

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else{
            connected = false;
        }
        if(!connected){
            Utils.showToast(context,getString(R.string.No_Internet_Access));
        }

        //------------------------------------------------------------------------------------------
        return view;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    /*when map is ready and is visible*/
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map_driver.onResume();
        map_obj = googleMap;
        map_obj.setBuildingsEnabled(true);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        if (mGoogleApiClient == null)
        {
            buildGoogleApiClient();
            /*Utils.showToast(getActivity(), "GOOGLE API CLIENT NULL: " + mGoogleApiClient.isConnected());*/
        } else {
            mGoogleApiClient.connect();
            Utils.showToastTest(getActivity(), "GOOGLE API CLIENT CONNECT: " + mGoogleApiClient.isConnected());
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        map_driver.onResume();
    }

    /*function to show the map*/
    private void showMap(Bundle savedInstanceState)
    {
        map_driver.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map_driver.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (mGoogleApiClient.isConnected())
        {
            // we add permissions we need to request location of the users
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsToRequest = permissionsToRequest(permissions);
            startLocationUpdates();
//            Utils.showToast(context, "Google APi client is connected");
        }
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Permissions ok, we get last location

        locationManager = (LocationManager) getActivity()
                .getSystemService(LOCATION_SERVICE);

//        mLastLocation = locationManager
//                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (mLastLocation != null) {
            driver_current_latitude = mLastLocation.getLatitude();
            driver_current_longitude = mLastLocation.getLongitude();



            /*show driver car on map*/
            if (marker_driver == null) {
//                BitmapDescriptor icon_driver =
//                        BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_driver);
//                /*LatLng driver_position = new LatLng(31.4363632, 74.3613435);*/
//                driver_position =
//                        new LatLng(driver_current_latitude, driver_current_longitude);
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(driver_position);
//                markerOptions.icon(icon_driver);
//                markerOptions.flat(true);
//                marker_driver = map_obj.addMarker(markerOptions);
//                marker_driver.setZIndex(100);

                if (!stations_available) {
                    getPlanStations();
                }
            }
        }else{
//            Utils.showToast(context,"Your location is Null!");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    //------------------Tracking
    int loc_chng=0;
    Double[] station_range;
    Boolean[] reached;
    int totalStations;
    Double TotalDistance,mving_distance;
    Handler handler=new Handler();
    Handler innerhandler=new Handler();
    Handler markerhandler=new Handler();

    int time = 0;
    int time_marker =0;
    //------------------Tracking

    @Override
    public void onLocationChanged(final Location location) {

        final double latitude = location.getLatitude();

        // Getting longitude of the current location
        final double longitude = location.getLongitude();

//        double latitude = Double.valueOf(last_station_lat);
//        double longitude = Double.valueOf(last_station_lng).doubleValue();


        // Creating a LatLng object for the current location
        final LatLng latLng = new LatLng(latitude, longitude);

        if(time_marker==0) {
            time_marker = 3;
            markerhandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (now != null) {
                        now.remove();
                    }
//        TextView tvLocation = (TextView) findViewById(R.id.tv_location);

                    // Getting latitude of the current location
                    BitmapDescriptor icon_driver =
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_driver);
                    /*LatLng driver_position = new LatLng(31.4363632, 74.3613435);*/
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(icon_driver);
                    markerOptions.flat(true);
//        marker_driver = map_obj.addMarker(markerOptions);
                    now = map_obj.addMarker(markerOptions);
                    // Showing the current location in Google Map
                    map_obj.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    // Zoom in the Google Map
                    map_obj.animateCamera(CameraUpdateFactory.zoomTo(15));
//                    Utils.showToast(context,"car location updated after 3 sec");
                    time_marker=0;
                }
            }, 5000);
        }

        if(time==0) {
            time=30;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Tracking Api Handling
                    if (planStation.size() != 0) {

                        loc_chng++;

                        final Station[] station = {new Station()};
                        station[0] = planStation.get(0);

                        if (loc_chng == 1) {
                            sending_db_time_location("1", driver_current_latitude, driver_current_longitude, station[0].getStationPlanId());
                        }

                        innerhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mving_distance = CalculateDistance("" + location.getLatitude(), "" + location.getLongitude(), last_station_lat, last_station_lng);

                                int i = 0;
                                if (mving_distance >= station_range[0]) {
                                    sending_db_time_location("2", location.getLatitude(), location.getLongitude(), station[0].getStationPlanId());
                                    if (((mving_distance-station_range[0]) <= Double.parseDouble("15")) && (    reached[0] == false)) {
                                        reached[0] = true;
                                        sending_db_time_location("3", location.getLatitude(), location.getLongitude(), station[0].getStationPlanId());
                                    }
                                } else {
                                    while (i < (totalStations - 1)) {
                                        if (i == (totalStations - 2)) {
                                            sending_db_time_location("2", location.getLatitude(), location.getLongitude(), station_id);
                                        } else if ((mving_distance <= station_range[i]) && (mving_distance >= station_range[i + 1])) {
                                            station[0] = planStation.get(i + 1);
                                            sending_db_time_location("2", location.getLatitude(), location.getLongitude(), station[0].getStationPlanId());
                                        }
                                        i++;
                                    }
                                    i = 1;
                                    while (i < totalStations) {
                                        if (((station_range[i] - mving_distance) <= Double.parseDouble("15")) && (reached[i] == false)) {
                                            reached[i] = true;
                                            sending_db_time_location("3", location.getLatitude(), location.getLongitude(), station[0].getStationPlanId());
                                        }
                                    }
                                }
                            }
                        },20000);

                    }

                    //------------------Tracking


                    //----------------------------------------------------------------------------

                    String last_lat = (latitude + "");
                    final String last_long = (longitude + "");

                    LatLng latLngFrom;

                    if (last_station_lat != null && last_station_lng != null) {
                        latLngFrom = new LatLng(Double.parseDouble(last_station_lat), Double.parseDouble(last_station_lng));
//          latLng=new LatLng(Double.parseDouble(last_station_lat), Double.parseDouble(last_station_lng));
                        //returns distance in meters
                        double distance = SphericalUtil.computeDistanceBetween(latLngFrom, latLng);


                        if (distance <= Double.parseDouble("15") && voice_message == false) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setView(R.layout.alert_for_last_station);
                            final AlertDialog dialog = builder.create();
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (ok_pressed == false) {

                                        //To increase media player volume
                                        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

                                        MediaPlayer ring = MediaPlayer.create(context, R.raw.beep);
                                        ring.start();
//                             dialog.dismiss();


                                    }
                                }
                            }, 20000);

                            MediaPlayer ring = MediaPlayer.create(context, R.raw.driver_last_station_voice);
                            ring.start();

                            voice_message = true;

                            Button btn_ok = dialog.findViewById(R.id.btn_ok_alert);
                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ok_pressed = true;
                                    sending_db_time_location("3", latitude, longitude, station_id);
                                    dialog.dismiss();
                                }
                            });
                            //To decrease media player volume
                            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                        }
                        //----------------------------------------------------------------------------
                    }
                    time=0;
                }
            }, 30000);
        }
    }

    //------------------Tracking
    private Double CalculateDistance(String start_latitude, String start_longitude, String last_station_lat, String last_station_lng) {
        LatLng latLngStation=new LatLng(Double.parseDouble(last_station_lat), Double.parseDouble(last_station_lng));
        LatLng latLngStart=new LatLng(Double.parseDouble(start_latitude), Double.parseDouble(start_longitude));

        double distance = SphericalUtil.computeDistanceBetween(latLngStart, latLngStation);


        return distance;

    }
    //------------------Tracking

    /*build google api client*/
    private synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Fix first time run app if permission doesn't grant yet so can't get anything
        mGoogleApiClient.connect();
    }

    /*create location request*/
    private void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UniversalConstants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(UniversalConstants.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*mLocationRequest.setSmallestDisplacement(UniversalConstants.DISPLACEMENT);*/
    }

    /*check gps state*/
    private boolean checkMobileGPSState()
    {
        boolean is_gps_enabled = false;
        //boolean is_network_enabled = false;

        LocationManager location_manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

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

    /*start location updates*/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startLocationUpdates()
    {
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

    /*get all the stations in the plan*/
    private void getPlanStations()
    {
        stations = new ArrayList<>();
        String url = Utils.GET_PLAN_STATIONS;
        final ProgressDialog dialog = ProgressDialog.show(context, getString(R.string.Fetching_stations_data), getString(R.string.Please_wait));
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
                                if (station != null)
                                    /*station = new Station(id, name, passengers, lat, lon, etd);*/
                                    stations.add(station);

                                //----------------------------------------------------------------------------


                                String StationPlanId=jsonObject.getString("StationPlanId");
                                    String StationName=jsonObject.getString("StationName");
                                    String TotalPassengers=jsonObject.getString("TotalPassengers");
                                    String Position=jsonObject.getString("Position");
                                    String Lat=jsonObject.getString("Lat");
                                    String Long=jsonObject.getString("Long");
                                    String ETD=jsonObject.getString("ETD");
                                    Station station_plan = new Station(StationPlanId, StationName, TotalPassengers,"1", Lat, Long, ETD);



                                    planStation.add(station_plan);



                                station_id=StationPlanId;
                                last_station_lat=Lat;
                                last_station_lng=Long;

                                //----------------------------------------------------------------------------
                            }

                            //------------------Tracking
                            totalStations=planStation.size();
                            reached=new Boolean[totalStations];
                            for(int i=0;i<totalStations;i++){
                                reached[i]=false;
                            }
                            if(last_station_lat !=null && last_station_lng!=null ) {
                                TotalDistance= CalculateDistance("" + driver_current_latitude, "" + driver_current_longitude, last_station_lat, last_station_lng);
                            }
                            station_range= new Double[totalStations-1];
                            for(int z=0;z<totalStations-1;z++){
                                Station station=planStation.get(z);
                                station_range[z]=CalculateDistance("" + station.getLat(), "" + station.getLong(), last_station_lat, last_station_lng);
                            }
                            //------------------Tracking


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
                                marker_station = map_obj.addMarker(markerOptions);
                                marker_station.setTag(station.getStationPlanId());
                                marker_station.showInfoWindow();

                                //to add click listner to snippet
                                map_obj.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        dialogbox_passengers(stations.get(active_station).getStationPlanId());
//                                        Bundle args = new Bundle();
//                                        args.putString(UniversalConstants.STATION_NUMBER, stations.get(active_station).getStationPlanId());
//                                        Fragment f = new StationPassengersFragment();
//                                        f.setArguments(args);
//                                        FragmentTransaction t = getFragmentManager().beginTransaction();
//                                        t.replace(R.id.container, f);
//                                        t.commit();
                                    }
                                });

                                //---------to add click listner to snippet---------
                                map_obj.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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

//                                                Utils.showToast(context, "You touched the text");
                                            }
                                        });

                                        return info;
                                    }
                                });
                                /*map_obj.addMarker(new MarkerOptions().position(station_location)
                                .title(station.getStationName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_station)));
                                map_obj.animateCamera(CameraUpdateFactory.newLatLng(station_location));*/
                            }
                            stations_available = true;
                            for (int i=0; i<stations.size(); i++)
                            {
                                try {
//            context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
                                    active_station = 0;
                                    station_name = stations.get(active_station).getStationName();
                                    station_time = stations.get(active_station).getETD();

                                    tv_station_name.setText(station_name);
                                    tv_station_time.setText(station_time);
//                                    btn_passengers.setVisibility(View.VISIBLE);
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

                                    DirectionFinder directionFinder = new DirectionFinder((DirectionFinderListener) LocationFragment.this,
                                            origin, destination, "path");



                                    Log.d("showDirection", "showDirection ");
                                    directionFinder.showDirection();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Utils.showToast(context, getString(R.string.Your_route_is_ready_Go));

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
//                            int code = error.networkResponse.statusCode;
//                            Log.d("Error", error + " Volley");
//                            Log.d("Code", code + " code");
//                            Utils.showCustomDialog(context, "Error code plan stat: " + code);
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
    public void onDirectionFinderStart(String operationName)
    {
        if (operationName.equals("path")) {
            progressDialog_stations = ProgressDialog.show(context, "Please wait.",
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

            progressDialog_stations.dismiss();
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
                    map_obj.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(driver_current_latitude, driver_current_longitude), 16));
                    maker_car = map_obj.addMarker(new MarkerOptions()
                            .icon(Utils.bitmapDescriptorFromVector(context, R.drawable.ic_my_location))
                            .title(route.startAddress)
                            .position(route.startLocation));
                    custome_marker_object = map_obj.addMarker(new MarkerOptions()
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

                    polylinePaths.add(map_obj.addPolyline(polylineOptions));
//                    Utils.savePreferences("polyline", "false", context);
                }
            }
            if (station_number == 1) {
                for (Route route : routes) {

                    String content = route.duration.text;
                    tv_station_time.setText(content);
                    String distance = route.distance.text;
                }
            }
            /*else {

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
            Utils.savePreferences(UniversalConstants.LOGGED_IN, "false", context);
            Utils.savePreferencesBool(UniversalConstants.LOGIN, false, context);
            startActivity(new Intent(context, LoginActivity.class));
//            Utils.showCustomDialog(context, e.getMessage());
        }
    }


    /*function for static stations*/
    private void getStations()
    {
        stations = new ArrayList<>();
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

                DirectionFinder directionFinder = new DirectionFinder((DirectionFinderListener) LocationFragment.this,
                        origin, destination, "path");



                Log.d("showDirection", "showDirection ");
                directionFinder.showDirection();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Utils.showToast(context, "Your route is ready. Go!");
    }

//    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
//        int Radius = 6371;// radius of earth in Km
//        double lat1 = StartP.latitude;
//        double lat2 = EndP.latitude;
//        double lon1 = StartP.longitude;
//        double lon2 = EndP.longitude;
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLon = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
//                + Math.cos(Math.toRadians(lat1))
//                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
//                * Math.sin(dLon / 2);
//        double c = 2 * Math.asin(Math.sqrt(a));
//        double valueResult = Radius * c;
//        double km = valueResult / 1;
//        DecimalFormat newFormat = new DecimalFormat("####");
//        int kmInDec = Integer.valueOf(newFormat.format(km));
//        double meter = valueResult % 1000;
//        int meterInDec = Integer.valueOf(newFormat.format(meter));
//        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
//                + " Meter   " + meterInDec);
//
//        return Radius * c;
//    }

    private void sending_db_time_location( final String TrackingTypeId,final double latitude, final double longitude, final String StationPlanId) {
        String url = Utils.DRIVER_TIME_LOCATION;
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String result = response;
                        /*try {
                            JSONArray jsonArray = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utils.showCustomDialog(context, e.getMessage());
                        }*/
                       /* if (jsonStatus.getString("error").equals("invalid_grant"))
                        {
                            HelperProgressDialog.closeDialog();
                            Utils.showCustomDialog(context, jsonStatus.getString("error_description"));
                        }*/
                        try {
                            JSONObject jsonObject=new JSONObject();

                            if(TrackingTypeId=="1"){
                                Utils.showToast(context,"Departure Update Send to Tracking Server!");
                            }
                            else if(TrackingTypeId=="2"){
                                Utils.showToast(context,"Moving Update Send to Tracking Server!");
                            }else if(TrackingTypeId=="3"){
                                Utils.showToast(context,"Arrival on Station Send to Tracking Server!");
                            }


                        } catch (JsonSyntaxException e) {
                            HelperProgressDialog.closeDialog();
                            HelperAlertDialogMessage.showAlertMessage(context, e.getMessage());
                            Utils.showToastTest(context, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        HelperProgressDialog.closeDialog();
//                        HelperAlertDialogMessage.showAlertMessage(context, "Invalid Credentials");
                        Log.d("error", error+"");
//                        Utils.showToastTest(context, error+"");
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        params.put("Authorization", "bearer " + Utils.getPreferences(UniversalConstants.ACCESS_TOKEN, context));

                        return params;
                    }
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> postParam = new HashMap<>();
                        postParam.put("TrackingTypeId", TrackingTypeId);
                        postParam.put("DriversPlansLinesId", Utils.getPreferences(UniversalConstants.DRIVER_PLANLANE_ID,context));
                        postParam.put("Lat", ""+latitude);
                        postParam.put("Lon", ""+longitude);
                        postParam.put("StationPlanId", StationPlanId);

                        return postParam;
                    }
                };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void dialogbox_passengers(String station_id){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(R.layout.layout_passengers);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        rv_passengers = dialog.findViewById(R.id.rv_passengers);
        tv_count = dialog.findViewById(R.id.tv_count);

        GridLayoutManager layoutManager =
                new GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false);
        rv_passengers.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_passengers.getContext(),
                layoutManager.getOrientation());
        rv_passengers.addItemDecoration(dividerItemDecoration);

        getStationPassengers(station_id);

        btn_back = dialog.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });
    }
    /*get passengers of a station*/
    private void getStationPassengers(String stationId)
    {
        String url = Utils.GET_STATION_PASSENGERS+"?stationPlanId="+stationId;
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
//                            ((MainActivity)getActivity()).setActionBarTitle(passengers.size()+" "+getString(R.string.txt_passengers));
                            tv_count.setText(passengers.size()+" "+getString(R.string.txt_passengers));
                            adapter = new PassengersAdapter(context, passengers);
                            rv_passengers.setAdapter(adapter);
                        }
                        catch(JSONException e)
                        {
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

    }
}
