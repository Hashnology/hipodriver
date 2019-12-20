package com.karimapps.hipodriver.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.MainActivity;

import java.util.ArrayList;

import universal.UniversalConstants;
import universal.Utils;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class QuestionairFragment extends Fragment
        implements View.OnClickListener {
    private Context context;
    private TextView tv_name,tv_line_num;
    private TextView tv_day, tv_time, tv_location, tv_client;
    private TextView tv_confirm_name;
    private RadioGroup rg_name, rg_vehicle_clean, rg_fuel, rg_previous_client;
    private RadioButton rb_name_yes, rb_name_no, rb_vehicle_yes, rb_vehicle_no,
            rb_fuel_yes, rb_fuel_no, rb_client_ok, rb_client_not_ok;
    private FloatingActionButton btn_submit;
    private boolean name_checked, fuel_checked,
            vehicle_clean_checked, previous_client_checked;
    private boolean name_yes, fuel_yes, vehicle_clean_yes, previous_client_ok;

    /*parameters for gps*/
    private boolean gps_enabled;
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    private boolean is_permission_granted;
    private boolean is_permission_sent = false;
    private int APP_PERMISSION_CODE = 1;


    private Bundle _savedInstanceState;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_questnair, container, false);
        context = getActivity();
        _savedInstanceState = savedInstanceState;


        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (!is_permission_granted) {
            if (checkAppPermissions()) {

                bindViews(_savedInstanceState);
                setOnClicks();
                setValues();
                is_permission_granted = true;
                //Toast.makeText(getActivity(), "Permission is granted", Toast.LENGTH_SHORT).show();
            }
        }


        return view;
    }

    //for location service
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

    //for location service
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

    private void bindViews(Bundle savedInstanceState) {
        /*show the navigation drawer toolbar*/
        MainActivity.toolbar.setVisibility(View.VISIBLE);

        tv_name = view.findViewById(R.id.tv_name);
        tv_day = view.findViewById(R.id.tv_day);
        tv_time = view.findViewById(R.id.tv_time);
        tv_client = view.findViewById(R.id.tv_client);
        tv_line_num = view.findViewById(R.id.tv_line_num);
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

        btn_submit = view.findViewById(R.id.btn_submit_ques_frag);
    }

    private void setOnClicks() {
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

    private void setValues() {
        /*set actio bar title*/
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.app_name));


        tv_day.setText(Utils.getPreferences(UniversalConstants.DEPARTURE_DATE, context));

        tv_line_num.setText(Utils.getPreferences(UniversalConstants.DRIVER_PLANLANE_ID, context));
        tv_client.setText(Utils.getPreferences(UniversalConstants.LINE_NAME, context));

        tv_name.setText(Utils.getPreferences(UniversalConstants.FIRSTNAME,context )+" "+Utils.getPreferences(UniversalConstants.LASTNAME, context));

        tv_confirm_name.setText(getString(R.string.confirm_your_name) + " " +
                Utils.getPreferences(UniversalConstants.DRIVER_USERNAME, context) +
                " " + getString(R.string.confirm_vehicle_ready));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                vehicle_clean_checked = true;
                vehicle_clean_yes = false;
                break;
            case R.id.rb_vehicle_yes:
                vehicle_clean_checked = true;
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

            case R.id.btn_submit_ques_frag:
                /*name check*/
                if (!name_checked) {
                    Utils.showSnackbar(view, getString(R.string.error_name_check));
                    break;
                }
                /*fuel check*/
                else if (!fuel_checked) {
                    Utils.showSnackbar(view, getString(R.string.error_fuel_check));
                    break;
                }
                /*vehicle clean check*/
                else if (!vehicle_clean_checked) {
                    Utils.showSnackbar(view, getString(R.string.error_vehicle_clean_check));
                    break;
                }
                /*client check*/
                else if (!previous_client_checked) {
                    Utils.showSnackbar(view, getString(R.string.error_client_check));
                    break;
                } else {

                    /*Utils.showSnackbar(view, "Ok, all checked");*/
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction t = fm.beginTransaction();
                    t.replace(R.id.container, new LocationFragmentAws());
                    t.commit();
                }


                break;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (permissionsToRequest.size() > 0) {
//                        requestPermissions(permissionsToRequest.toArray(
//                                new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//                    }
//                } else {
////                    if (checkMobileGPSState())
////                    {
////                        t.replace(R.id.container, new LocationFragment());
////                        t.commit();
////                    } else {
////                        showGPSEnableDialog();
////                    }
//                }


        }
    }


    /*check gps state*/
    private boolean checkMobileGPSState() {
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
}
