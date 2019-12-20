package com.karimapps.hipodriver.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
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
import com.karimapps.hipodriver.MapModules.DirectionFinder;
import com.karimapps.hipodriver.MapModules.DirectionFinderListener;
import com.karimapps.hipodriver.MapModules.Route;
import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.LoginActivity;
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
import universal.Utils;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class LocationFragmentAwsOld extends Fragment
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        DirectionFinderListener {
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private static final long UPDATE_INTERVAL = 1000, FASTEST_INTERVAL = 1000,
            DISPLACEMENT = 10;
    /*parameters for location*/
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    boolean stations_available;
    private Context context;
    private double latitude;
    private double longitude;
    /*for driver some info*/
    private TextView tv_line_num_label;
    private TextView tv_station_distance, tv_station_time, tv_station_name;
    private String station_distance, station_time, station_name;
    private int active_station;
    private FloatingActionButton btn_passengers;
    private MapView map_driver;
    private GoogleMap map_obj;
    private Bundle _savedInstanceState;
    private Location mLastLocation;
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    /*show driver position parameters*/
    private double driver_current_latitude;
    private double driver_current_longitude;
    private Marker marker_driver;
    private LatLng driver_position;
    /*parameters for stations*/
    private int station_number = 0;
    private List<Station> stations;
    private Station station;
    private Marker marker_station, custome_marker_object, maker_car, marker;
    private ProgressDialog progressDialog_stations;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        _savedInstanceState = savedInstanceState;

        context = getActivity();
        map_driver = view.findViewById(R.id.map_driver);
        tv_line_num_label = view.findViewById(R.id.tv_line_num_label);

        tv_station_name = view.findViewById(R.id.tv_station_name);
        tv_station_distance = view.findViewById(R.id.tv_station_distance);
        tv_station_time = view.findViewById(R.id.tv_station_time);
        btn_passengers = view.findViewById(R.id.btn_passengers);

        tv_line_num_label.setText("Line: " + Utils.getPreferences(UniversalConstants.DRIVER_PLANLANE_ID, context));

        btn_passengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            public void onClick(View view) {
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
        return view;
    }

    /*when map is ready and is visible*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_driver.onResume();
        map_obj = googleMap;
        map_obj.setBuildingsEnabled(true);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
            /*Utils.showToast(getActivity(), "GOOGLE API CLIENT NULL: " + mGoogleApiClient.isConnected());*/
        } else {
            mGoogleApiClient.connect();
            Utils.showToastTest(getActivity(), "GOOGLE API CLIENT CONNECT: " + mGoogleApiClient.isConnected());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        map_driver.onResume();
    }

    /*function to show the map*/
    private void showMap(Bundle savedInstanceState) {
        map_driver.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map_driver.getMapAsync(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mGoogleApiClient.isConnected()) {
            // we add permissions we need to request location of the users
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsToRequest = permissionsToRequest(permissions);
            startLocationUpdates();
            Utils.showToast(context, "Google APi client is connected");
        }
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

            /*show driver car on map*/
            if (marker_driver == null) {
                BitmapDescriptor icon_driver =
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_driver);
                /*LatLng driver_position = new LatLng(31.4363632, 74.3613435);*/
                driver_position =
                        new LatLng(driver_current_latitude, driver_current_longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(driver_position);
                markerOptions.icon(icon_driver);
                markerOptions.flat(true);
                marker_driver = map_obj.addMarker(markerOptions);
                marker_driver.setZIndex(100);

                if (!stations_available) {
                    getPlanStations();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        getLocation();
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
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /*get all the stations in the plan*/
    private void getPlanStations() {
        stations = new ArrayList<>();
        String url = Utils.GET_PLAN_STATIONS;
        final ProgressDialog dialog = ProgressDialog.show(context, "Fetching stations data", "Please wait...");
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        Log.d("res", "stations data " + result);
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
                                marker_station = map_obj.addMarker(markerOptions);
                                marker_station.setTag(station.getStationPlanId());
                                marker_station.showInfoWindow();

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
                                            public void onClick(View view) {
                                                Utils.showToast(context, "You touched the text");
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
                            for (int i = 0; i < stations.size(); i++) {
                                try {
//            context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
                                    active_station = 0;
                                    station_name = stations.get(active_station).getStationName();
                                    station_time = stations.get(active_station).getETD();

                                    tv_station_name.setText(station_name);
                                    tv_station_time.setText(station_time);
                                    btn_passengers.setVisibility(View.VISIBLE);
                                    LatLng origin;

                                    if (i == 0) {
                                        origin = new LatLng(driver_current_latitude, driver_current_longitude);
                                    } else {
                                        origin = new LatLng(Double.parseDouble(stations.get(i - 1).getLat()), Double.parseDouble(stations.get(i - 1).getLong()));
                                    }
                                    /*LatLng origin = new LatLng(driver_current_latitude, driver_current_longitude);*/
                                    LatLng destination = new LatLng(Double.parseDouble(stations.get(i).getLat()), Double.parseDouble(stations.get(i).getLong()));

                                    DirectionFinder directionFinder = new DirectionFinder(LocationFragmentAwsOld.this,
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
    public void onDirectionFinderStart(String operationName) {
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
    public void onDirectionFinderSuccess(List<Route> routes, String operationName) {
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
                    List<PatternItem> pattern = Arrays.asList(
                            new Gap(10), new Dash(30));
                    /*mPolyline.setPattern(pattern);*/

                    PolylineOptions polylineOptions;
                    if (station_number == 1) {
                        polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.GREEN).
                                width(7).
                                pattern(pattern);
                    } else {
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
        }
    }


    /*function for static stations*/
    private void getStations() {
        stations = new ArrayList<>();
        Station ghazi_chowk = new Station("31.4569168", "74.3491059");
        Station kalma_chowk = new Station("31.5035372", "74.3296502");
        Station barkat_market = new Station("31.5010202", "74.3194017");
        Station bhekewal_mor = new Station("31.5091131", "74.3006766");
        Station canal_station = new Station("31.519486", "74.3244473");
        stations.add(ghazi_chowk);
        stations.add(kalma_chowk);
        stations.add(barkat_market);
        stations.add(bhekewal_mor);
        stations.add(canal_station);

        /*stations_available = true;*/
        /*draw the route*/

        for (int i = 0; i < stations.size(); i++) {
            try {
//            context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));

                LatLng origin;

                if (i == 0) {
                    origin = new LatLng(driver_current_latitude, driver_current_longitude);
                } else {
                    origin = new LatLng(Double.parseDouble(stations.get(i - 1).getLat()), Double.parseDouble(stations.get(i - 1).getLong()));
                }
                /*LatLng origin = new LatLng(driver_current_latitude, driver_current_longitude);*/
                LatLng destination = new LatLng(Double.parseDouble(stations.get(i).getLat()), Double.parseDouble(stations.get(i).getLong()));

                DirectionFinder directionFinder = new DirectionFinder(LocationFragmentAwsOld.this,
                        origin, destination, "path");


                Log.d("showDirection", "showDirection ");
                directionFinder.showDirection();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Utils.showToast(context, "Your route is ready. Go!");
    }

    private void getLocation() {
        try {
            LatLng updated_driver_latlng = null;

            /*this is for current location*/
//            double latitude_d = Double.parseDouble(latitude);
//            double longitude_d = Double.parseDouble(location_current_longitude);
            updated_driver_latlng = new LatLng(latitude, longitude);

            Location previous_location = new Location("");
            previous_location.setLatitude(maker_car.getPosition().latitude);
            previous_location.setLongitude(maker_car.getPosition().longitude);
            Location update_location = new Location("");
            update_location.setLatitude(updated_driver_latlng.latitude);
            update_location.setLongitude(updated_driver_latlng.longitude);

            double distance = previous_location.distanceTo(update_location);
            /*******************ETA Finder Method*************************************/
   /*         DirectionFinder directionFinder = new DirectionFinder(LocationFragmentAws.this,
                    new LatLng(latitude, longitude),

                    new LatLng(Double.parseDouble(pickup_latitude),
                            Double.parseDouble(pickup_longitude)), "Eta");

            Log.d("onResume", "latitude " + latitude + "longitude " + longitude + "pickup_latitude " + pickup_latitude + "pickup_longitude " + pickup_longitude);

            Log.d("showDirection", "showDirection ");
            directionFinder.showDirection();*/

            /*****************************ETA Finder Method*************************************/

            float bearing = previous_location.bearingTo(update_location);
            Log.d("hantash_driver_bearing", "Bearing: " + bearing);
            //Toast.makeText(getActivity(), "Bearing: "+bearing, Toast.LENGTH_LONG).show();
            /*here*/
            float distance_covered = previous_location.distanceTo(update_location);

//ToastMessage.showSampleToast(getActivity(), "Distance: "+distance_covered);

            if (distance_covered >= Utils.DISTANCE_TO_ROTATE) {
                float bearing2 = previous_location.bearingTo(update_location);
                Log.d("driver_bearing", "Bearing: " + bearing2);
                maker_car.setRotation(bearing2);
            }
                            /*if (bearing > 0.0) {
                                maker_car.setRotation(bearing);
                            }*/

            //Moving the driver
            smoothlyMoveTaxi(updated_driver_latlng);

            Log.d("check_driver_movement", "---------------------------------CHECK Bearing----------------------------------------------------");
            Log.d("check_driver_movement", "Previous Latitude: " + previous_location.getLatitude() + "Previous Longitude: " + previous_location.getLongitude());
            Log.d("check_driver_movement", "Update Latitude: " + update_location.getLatitude() + "Update Longitude: " + update_location.getLongitude());
            Log.d("check_driver_movement", "Distance: " + distance);
            Log.d("check_driver_movement", "----------------------------------------------------------------------------------------------");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void smoothlyMoveTaxi(LatLng final_position) {
        final LatLng startPosition = maker_car.getPosition();
        final LatLng finalPosition = final_position;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 10000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                maker_car.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        maker_car.setVisible(false);
                    } else {
                        maker_car.setVisible(true);
                    }
                }
            }
        });
    }
}
