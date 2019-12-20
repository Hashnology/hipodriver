package com.karimapps.hipodriver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.MainActivity;
import com.karimapps.hipodriver.models.Driver;

import java.util.HashMap;
import java.util.Map;

import universal.UniversalConstants;
import universal.HelperAlertDialogMessage;
import universal.HelperProgressDialog;
import universal.Utils;

public class LoginFragment extends Fragment {

    private Context context;
    private EditText et_car_reg, et_phone;
    private FloatingActionButton btn_submit;
    private RelativeLayout fragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        context = getActivity();

        /*check if already login*/
        if (Utils.getPreferencesBool(UniversalConstants.LOGIN, context))
        {
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
            FragmentTransaction t = fm.beginTransaction();
            t.replace(R.id.container, new QuestionairFragment());
            t.commit();
        }

        bindViews(view);
        return view;
    }

    private void bindViews(View view)
    {
        MainActivity.toolbar.setVisibility(View.GONE);
        fragment = view.findViewById(R.id.fragment);
        et_car_reg = view.findViewById(R.id.et_car_reg);
        et_phone = view.findViewById(R.id.et_phone);
        btn_submit = view.findViewById(R.id.btn_submit);

        /*submit login*/
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (dataValid())
                {
                    String car_reg, phone;
                    car_reg = et_car_reg.getText().toString();
                    phone = et_phone.getText().toString();

                    login(phone, car_reg);

                    /*if (!car_reg.equalsIgnoreCase("391316")
                            || !phone.equalsIgnoreCase("111111111"))
                    {
                        Snackbar.make(view, "Invalid Credentials", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        Utils.savePreferences(UniversalConstants.DRIVER_FIRSTNAME, "מיקה", context);
                        Utils.savePreferences(UniversalConstants.DRIVER_LASTNAME, "מיקה", context);
                        Utils.savePreferences(UniversalConstants.DRIVER_USERNAME," מיקה"+ " מיקה", context);
                        Utils.savePreferences(UniversalConstants.DRIVER_PHONE, "111111111", context);
                        Utils.savePreferences(UniversalConstants.DRIVER_CAR_REG, "391316", context);
                        Utils.savePreferencesBool(UniversalConstants.LOGIN, true, context);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, new QuestionairFragment());
                        transaction.commit();
                    }*/
                }
            }
        });

    }

    private boolean dataValid()
    {
        String phone = et_phone.getText().toString();
        if (et_car_reg.getText().toString().equalsIgnoreCase(""))
        {
            et_car_reg.setError("Required");
            return false;
        }
        if (et_phone.getText().toString().equalsIgnoreCase(""))
        {
            et_phone.setError("Required");
            return false;
        }
        if (phone.contains("+") || phone.contains(")") || phone.contains("(") ||
                phone.contains("W") || phone.contains("P") || phone.contains("N") ||
                phone.contains("-") ||
                phone.contains("/") || phone.contains("*") || phone.contains(".")
                || phone.contains("`") || phone.contains("^") || phone.contains("&")
                || phone.contains("'") || phone.contains(";") || phone.contains("%")
                || phone.contains("@") || phone.contains("#") || phone.contains("!")
                || phone.contains("~"))
        {
            et_phone.setError("No special character allowed");
            return false;
        }
        return true;
    }

    /*method to login*/
    private void login(final String mobile, final String car_reg) {
        String url = Utils.LOGIN_API;
        HelperProgressDialog.showDialog(context, "Sending a confirmation code", "Please wait...");
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String result = response;
                        try {
                            Gson g = new Gson();
                            Driver p = g.fromJson(result, Driver.class);
                            Utils.savePreferences(UniversalConstants.ACCESS_TOKEN, p.access_token, context);
                            Utils.savePreferences(UniversalConstants.DRIVER_FIRSTNAME, p.DriverFirstName, context);
                            Utils.savePreferences(UniversalConstants.DRIVER_LASTNAME, p.DriverLastName, context);
                            Utils.savePreferences(UniversalConstants.DRIVER_USERNAME," מיקה"+ " מיקה", context);
                            Utils.savePreferences(UniversalConstants.DRIVER_PHONE, mobile, context);
                            Utils.savePreferences(UniversalConstants.DRIVER_CAR_REG, car_reg, context);
                            HelperProgressDialog.closeDialog();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            fm.popBackStack();
                            /*FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, new QuestionairFragment());
                            transaction.commit();*/
                            Bundle args = new Bundle();
                            args.putString(UniversalConstants.CONFIRMATION_CODE, "123121");
                            Fragment fragment = new CodeConfirmationFragment();
                            fragment.setArguments(args);
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();

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
                        HelperAlertDialogMessage.showAlertMessage(context, "Invalid Credentials");
                        Log.d("error", error+"");
                        Utils.showToastTest(context, error+"");
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> postParam = new HashMap<>();
                        postParam.put("UserName", car_reg);
                        postParam.put("Password", mobile);
                        postParam.put("grant_type", "password");
                        return postParam;
                    }
                };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
