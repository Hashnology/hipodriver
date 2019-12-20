/*
package com.karimapps.hipodriver.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.hashi_placesapi.Modules.DirectionFinder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.services.GPSTracker;


import java.util.ArrayList;
import java.util.List;


public class Detailed_Fragment extends Fragment implements OnMapReadyCallback, DirectionFinderListener{

    Marker custome_marker_object;
    private int APP_PERMISSION_CODE = 1;
    private MapView mapView;
    private View fragment_view;
    private Context context;


    private Bundle sanvedInstance;
    private boolean is_permission_granted;
    private boolean is_permission_sent = false;
    private GoogleMap myMap;
    private Marker marker = null;
    private double latitude;
    private double longitude;
    private double my_latitude;
    private double my_longitude;

    private GPSTracker gps;
    private View custome_marker;
    private TextView tv_pickup, tv_drop;
    private boolean wifi, mobileDataEnabled = false;
    private Button btn_go;
    private String drop_address, pickup_address, pickup_lat, pickup_long, current_lat, current_long;
    private TextView tv_location;
    double drop_lat, drop_long;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Marker maker_car;

    public Detailed_Fragment() {
        // Required empty public constructor
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment_view = inflater.inflate(R.layout.detailed_fragment, container, false);
        context = getActivity();
        gps = new GPSTracker(getActivity());
        sanvedInstance = savedInstanceState;

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            drop_lat = Double.parseDouble(bundle.getString("lat"));
            drop_long = Double.parseDouble(bundle.getString("lang"));
            drop_address = bundle.getString("address");
        }


        if (!is_permission_granted) {
            if (checkAppPermissions()) {

                linkViews(sanvedInstance);
                is_permission_granted = true;
                //Toast.makeText(getActivity(), "Permission is granted", Toast.LENGTH_SHORT).show();
            }
        }

        return fragment_view;
    }

    private void linkViews(Bundle sanvedInstance) {

        mapView = (MapView) fragment_view.findViewById(R.id.mapview_full);
        tv_drop = fragment_view.findViewById(R.id.tv_drop);
        custome_marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        showMap(sanvedInstance);


        if (!drop_address.isEmpty()) {
            tv_drop.setText(drop_address);
        }


        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            if (latitude <= 0 || longitude <= 0) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                Log.d("hantash_my_location", "GPS Tracker is Updated");
            }
        } else {
            gps.showSettingsAlert();
        }

draw route of path given

        try {
//            context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));

            DirectionFinder directionFinder = new DirectionFinder(Detailed_Fragment.this,
                    new LatLng(latitude, longitude),

                    new LatLng(drop_lat,
                            drop_long), "path");

            Log.d("onResume", "latitude " + latitude + "longitude " + longitude + "pickup_latitude " + drop_lat + "pickup_longitude " + drop_long);

            Log.d("showDirection", "showDirection ");
            directionFinder.showDirection();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void showMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        //mapView_driver_online.onResume();// needed to get the map to display immediately


        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);
    }


    private boolean checkAppPermissions() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED


                ) {
            //Toast.makeText(getActivity(), "Permission is checked", Toast.LENGTH_LONG).show();

            return true;

        } else {
            String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            if (hasPermissions(PERMISSIONS[0]) && hasPermissions(PERMISSIONS[1])) {
                //Toast.makeText(getActivity(), "All permission granted", Toast.LENGTH_LONG).show();
                return true;
            } else {
                if (!is_permission_sent) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, APP_PERMISSION_CODE);
                    is_permission_sent = true;
                }
                //Toast.makeText(getActivity(), "Permissions have not granted", Toast.LENGTH_LONG).show();
                return false;
            }
        }

    }

    public boolean hasPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(latitude, longitude), 16));

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes, String operationName) {


        try {
            if (operationName.equals("path")) {
                polylinePaths = new ArrayList<>();
                originMarkers = new ArrayList<>();
                destinationMarkers = new ArrayList<>();

                for (Route route : routes) {


                    if (marker != null) {
                        marker.remove();
                        marker = null;
                    }
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
                    maker_car = myMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.black_marker))
                            .title(route.startAddress)
                            .position(route.startLocation));
                    originMarkers.add(maker_car);


                    custome_marker_object = myMap.addMarker(new MarkerOptions()
                            .title(route.endAddress)
                            .position(route.endLocation)
                            .icon((BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, custome_marker)))));



                    PolylineOptions polylineOptions = new PolylineOptions().
                            geodesic(true).
                            color(Color.RED).
                            width(7);

                    for (int i = 0; i < route.points.size(); i++)
                        polylineOptions.add(route.points.get(i));

                    polylinePaths.add(myMap.addPolyline(polylineOptions));
//                    Utils.savePreferences("polyline", "false", context);
                }
            } else {

                for (Route route : routes) {


//                    String content = route.duration.text;
//
//                    String distance = route.distance.text;
//                    try {
//                        dis = Double.parseDouble(distance);
//                        dis_mile = dis * 0.621371;
//                    } catch (Exception e) {
//
//                    }
//
//
//                    String regex = "mins";
//                    String duration = content.replaceAll(regex, "");
//                    tv_marker.setText(duration + "");

//                    slide_btn_text2.setText("ETA" + duration + " Distance "+dis_mile);
//                    TextViewDistanceEta.showText = "" + dis_mile + "Miles" + duration + "Min";
                    Log.d("route", "route distnace" + route.distance.value + "duration " + route.duration);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart(String operationName) {

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


//    private void goBackMethod(){
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//
//                    FragmentManager fragmentManager=getFragmentManager();
//                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//                    HomeFragment categoriesFragment=new HomeFragment();
//                    fragmentTransaction.replace(R.id.container_fragments,categoriesFragment);
//                    fragmentTransaction.commit();
//
//                    return true;
//
//                }
//
//                return false;
//            }
//        });
//    }

}
*/
