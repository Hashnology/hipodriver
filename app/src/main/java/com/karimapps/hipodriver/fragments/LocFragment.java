package com.karimapps.hipodriver.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.models.Station;
import com.karimapps.hipodriver.services.GPSTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import universal.UniversalConstants;
import universal.Utils;

public class LocFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    private Context context;

    private Location mLastLocation;
    private GoogleMap map_object;
    private GPSTracker gps;

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
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private MapView map_location;
    private ImageView btn_logout, btn_refresh;
    /*firebase*/
    private FirebaseDatabase firebaseDatabase;


    public LocFragment()
    {
        this.context = getActivity();
    }
    @SuppressLint("ValidFragment")
    public LocFragment(Context context)
    {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.location_fragment, container, false);
        context = getActivity();
        gps = new GPSTracker(getActivity());
        setUpGClient();
        stations = new ArrayList<>();
        FirebaseApp.initializeApp(context);
        map_location = view.findViewById(R.id.map_location);
        firebaseDatabase = FirebaseDatabase.getInstance();


        return view;
    }

    /*set up google api client*/
    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    mLocationRequest = new LocationRequest();
                    mLocationRequest.setInterval(UPDATE_INTERVAL);
                    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
                    mLocationRequest.setSmallestDisplacement(UniversalConstants.DISPLACEMENT);
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, mLocationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(context,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mLastLocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                        Utils.showToast(context, "LOcation is available");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_SETTINGS_GPS);
                                        /*getActivity().startIntentSenderForResult(
                                                status.getResolution().getIntentSender(),
                                                REQUEST_CHECK_SETTINGS_GPS, null,
                                                0, 0, 0, null);*/
                                    } catch (IntentSender.SendIntentException e) {
                                        Utils.showCustomDialog(context, e.getMessage());
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
            else
            {
                Utils.showCustomDialog(context, String.valueOf(googleApiClient != null));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (googleApiClient == null)
                        {
                            context = getParentFragment().getActivity().getApplicationContext();
                            setUpGClient();
                        }
                        getMyLocation();
                        /*Utils.showToast(getActivity(), "Locaiton is on");*/
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions((Activity) context,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        checkPermissions();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity.getApplicationContext();
    }
}
