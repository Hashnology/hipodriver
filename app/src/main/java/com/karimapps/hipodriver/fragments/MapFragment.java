package com.karimapps.hipodriver.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karimapps.hipodriver.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import universal.UniversalConstants;
import universal.Utils;

import static android.content.Context.MODE_PRIVATE;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private View fragment_view;
    private MapView map_home_screen;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap map_object;
    private Marker marker_driver;
    private double driver_current_latitude;
    private double driver_current_longitude;
    private boolean is_map_location_available = false;
    private Timer timer_driver_location;
    private TimerTask doAsynchronousTask;
    private Timer timer;
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment_view = inflater.inflate(R.layout.map_fragment, container, false);

        map_home_screen = fragment_view.findViewById(R.id.map_home_screen);

        showMap(savedInstanceState);

        /*/ we add permissions we need to request location of the users*/
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //Run-time request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, UniversalConstants.MY_PERMISSION_REQUEST_CODE);

        } else {
            if (checkMobileGPSState()) {
                if (checkPlayServices()) {
                    buildGoogleApiClient();
                    createLocationRequest();

                }
                is_map_location_available = true;
            } else {
                showGPSEnableDialog();
            }
        }

        return fragment_view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
            createLocationRequest();
            Utils.showToastTest(getActivity(), "GOOGLE API CLIENT CONNECT IF: "+mGoogleApiClient.isConnected());
        } else {
            mGoogleApiClient.connect();
            Utils.showToastTest(getActivity(), "GOOGLE API CLIENT CONNECT: "+mGoogleApiClient.isConnected());
            setDriverPosition();

            //HelperToastMessage.showSampleToast(getActivity(), "Google API is connected");
        }
        is_map_location_available = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        map_home_screen.onResume();

        if (is_map_location_available) {

        }

//        if (is_map_location_available) {
//            if (doAsynchronousTask == null) {
//                startDriverMovementHandler();
//
//                Log.d("handler_state", "Movement Vehicle Handler is started");
//            }
//
//            if(asynchronous_task_driver_location == null){
//                startDriverLocationHandler();
//                Log.d("handler_state", "Driver location Update Handler is started");
//            }
//        }


    }

    @Override
    public void onPause() {
        super.onPause();
        map_home_screen.onPause();

    }

    @Override
    public void onStop() {

        is_map_location_available = false;


        if (doAsynchronousTask != null) {
            doAsynchronousTask.cancel();
            doAsynchronousTask = null;
            timer.cancel();

            Log.d("handler_state", "Movement Vehicle Handler is stopped");
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map_home_screen.onDestroy();

        if (mGoogleApiClient != null) {

            mGoogleApiClient.disconnect();
        }



    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map_home_screen.onLowMemory();
    }


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

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UniversalConstants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(UniversalConstants.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement((float) UniversalConstants.DISPLACEMENT);
    }

    private boolean checkMobileGPSState() {
        boolean is_gps_enabled = false;
        //boolean is_network_enabled = false;

        LocationManager location_manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        is_gps_enabled = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //is_network_enabled = location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return is_gps_enabled;
    }

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

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Fix first time run app if permission doesn't grant yet so can't get anything
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_object = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map_object.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map_object.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void showMap(Bundle savedInstanceState) {
        map_home_screen.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map_home_screen.getMapAsync(this);
    }




    private void setDriverPosition() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                map_object.animateCamera(CameraUpdateFactory.newLatLngZoom(driver_position, 17));
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

    private void smoothlyMoveDriver(final Marker driver_marker, LatLng final_position) {
        final LatLng startPosition = driver_marker.getPosition();
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

                driver_marker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        driver_marker.setVisible(false);
                    } else {
                        driver_marker.setVisible(true);
                    }
                }
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

                            Location previous_location = new Location("");
                            previous_location.setLatitude(marker_driver.getPosition().latitude);
                            previous_location.setLongitude(marker_driver.getPosition().longitude);

                            Location update_location = new Location("");
                            update_location.setLatitude(updated_driver_latlng.latitude);
                            update_location.setLongitude(updated_driver_latlng.longitude);

                            float distance_covered = previous_location.distanceTo(update_location);

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
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);
    }

//    private void startDriverLocationHandler() {
//        final Handler handler = new Handler();
//        timer_driver_location = new Timer();
//        asynchronous_task_driver_location = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        try {
//
//                            if(driver_current_latitude != 0.0 && driver_current_longitude != 0.0){
//                                updateDriverLocation(driver_id, registration_id,
//                                        driver_current_latitude+"", driver_current_longitude+"");
//                            }
//
//                            Log.d("update_movement_2", "--------------------------------------------------------------------");
//                            Log.d("update_movement_2", "----------------------Driver Location Is Updated---------------------");
//                            Log.d("update_movement_2", "--------------------------------------------------------------------");
//
//                        } catch (Exception e) {
//                            Log.d("update_movement_2", "Problem in handler");
//                        }
//                    }
//                });
//            }
//        };
//
//        timer_driver_location.schedule(asynchronous_task_driver_location, 0, 10000);
//
//    }

}
