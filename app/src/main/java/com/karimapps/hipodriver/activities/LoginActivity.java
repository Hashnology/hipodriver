package com.karimapps.hipodriver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.gson.JsonSyntaxException;
import com.karimapps.hipodriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import universal.UniversalConstants;
import universal.HelperAlertDialogMessage;
import universal.HelperProgressDialog;
import universal.Utils;

public class LoginActivity extends AppCompatActivity {
    private Context context;
    private EditText et_username, et_password;
    private Button btn_submit;
    private TextView tv_forgot;
    private RelativeLayout activity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        activity = findViewById(R.id.activity);
        FirebaseApp.initializeApp(context);

        /*if (Utils.getPreferencesBool(UniversalConstants.LOGIN, context))
        {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }*/
        linkViews();

    }

    private void linkViews() {

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_submit = findViewById(R.id.btn_submit);
        tv_forgot = findViewById(R.id.tv_forgot);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loginMethod();
            }
        });
    }

    private void loginMethod() {

        if (dataValid())
        {
            login(et_username.getText().toString(), et_password.getText().toString()
                    );
        }

        /*str_username = et_car_reg.getText().toString();
        str_password = et_phone.getText().toString();
        if (et_car_reg.getText().toString().equalsIgnoreCase("")) {
            et_car_reg.setError("Required");
        } else if (et_phone.getText().toString().equalsIgnoreCase("")) {
            et_phone.setError("Required");
        } else {
            final ProgressDialog progressDialog =
                    ProgressDialog.show(context, "Getting you signin",
                            "Please wait...", true);

            Utils.showToastTest(context, "Loging successfully");
            *//**//*loginDriverMethod(str_username,str_password);*//**//*
            String email = str_username+"driver@hippo.com";
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, str_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Utils.savePreferencesBool(UniversalConstants.LOGIN, true, context);
                                startActivity(new Intent(context, MainActivity.class));
                                finish();
                            } else {
                                Log.d("created", "createUserWithEmail:failure", task.getException());
                                progressDialog.dismiss();
                                Snackbar.make(activity,
                                        "Authentication failed.",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Error", e.getMessage());
                    progressDialog.dismiss();
                    Snackbar.make(activity,
                            "Error: " + e.getMessage(),
                            Snackbar.LENGTH_LONG).show();

                }
            });*//*
        }*/
    }

//    private void loginDriverMethod(final String str_username, final String str_password) {
//
//        HelperProgressDialog.showDialog(this, "", "Loading...");
//
//        Map<String, String> postParam = new HashMap<String, String>();
//        postParam.put("UserName", "mp03");
//        postParam.put("Password", "00001");
//        postParam.put("grant_type", "password");
//
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                Utils.LOGIN_API,
//                new JSONObject(postParam),
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//                        Log.d("TAG", jsonObject.toString());
//
////                        try {
////                            JSONObject jsonStatus = jsonObject.getJSONObject("status");
////                            if (jsonStatus.getString("code").equals("1000")) {
////
////                                JSONObject data_json_object = jsonObject.getJSONObject("data");
////
////
////                                HelperProgressDialog.closeDialog();
////                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
////                                finish();
////
////                            } else {
////                                HelperProgressDialog.closeDialog();
//////                                Utils.showSampleToast(LoginScreen.this, jsonStatus.getString("message"));
////                            }
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                            HelperAlertDialogMessage.showAlertMessage(LoginActivity.this, e + "");
////                            HelperProgressDialog.closeDialog();
////                        }
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d("TAG", "Error: " + error.getMessage());
//                //Toast.makeText(getActivity(), error.getMessage() + "", Toast.LENGTH_LONG).show();
//                HelperProgressDialog.closeDialog();
//            }
//        }) {
//            /**
//             * Passing some request headers
//             * */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/x-www-form-urlencoded");
//                return headers;
//            }
//
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                500000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        HelperRequestQueue.getRequestInstance(LoginActivity.this).addRequest(jsonObjReq);
//    }

    private boolean dataValid() {
        if (et_username.getText().toString().equalsIgnoreCase("")) {
            et_username.setError("Required");
            return false;
        }
        if (et_password.getText().toString().equalsIgnoreCase("")) {
            et_password.setError("Required");
            return false;
        }
        return true;
    }
    private void login(final String username, final String password) {
        String url = Utils.LOGIN_API;
        HelperProgressDialog.showDialog(this, getString(R.string.Getting_you_signin), getString(R.string.Please_wait));
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String result = response.toString();
                        Log.d("res",result);
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
                            try {
                                jsonObject=new JSONObject(response);
                                Utils.savePreferences(UniversalConstants.ACCESS_TOKEN, jsonObject.getString("access_token"), context);
                                Utils.savePreferences(UniversalConstants.FIRSTNAME, jsonObject.getString("DriverFirstName"), context);
                                Utils.savePreferences(UniversalConstants.LASTNAME, jsonObject.getString("DriverLastName"), context);
                                Utils.savePreferences(UniversalConstants.USERNAME, jsonObject.getString("DriverFirstName")+" "+jsonObject.getString("DriverLastName"), context);
                                Utils.savePreferences(UniversalConstants.SUBORGANIZATION_ID, jsonObject.getString("SubOrganizationId"), context);
                                Utils.savePreferences(UniversalConstants.DRIVER_PLANLANE_ID, jsonObject.getString("DriverPlanLineId"), context);
                                Utils.savePreferences(UniversalConstants.DEPARTURE_DATE,jsonObject.getString("DepartureDate"), context);
                                Utils.savePreferences(UniversalConstants.LATITUDE, jsonObject.getString("Lat"), context);
                                Utils.savePreferences(UniversalConstants.LONGITUDE, jsonObject.getString("Long"), context);
                                Utils.savePreferences(UniversalConstants.LINE_NAME, jsonObject.getString("LineName"), context);
                                Utils.savePreferences(UniversalConstants.SUBORGANIZATION_NAME, jsonObject.getString("SubOrganizationName"), context);
                            } catch (JSONException ignored) {
                                ignored.printStackTrace(); //now it is in log
                            }
                            Utils.savePreferencesBool(UniversalConstants.LOGIN, true, context);
                            HelperProgressDialog.closeDialog();
                            Utils.savePreferences(UniversalConstants.LOGGED_IN,"true",context);
                            startActivity(new Intent(context, MainActivity.class));
                            finish();

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
                        postParam.put("UserName", username);
                        postParam.put("Password", password);
                        postParam.put("grant_type", "password");
                        return postParam;
                    }
                };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
