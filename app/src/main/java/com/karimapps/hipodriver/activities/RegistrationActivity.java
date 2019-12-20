package com.karimapps.hipodriver.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karimapps.hipodriver.R;

import java.util.HashMap;

import universal.UniversalConstants;
import universal.Utils;

public class RegistrationActivity extends AppCompatActivity
{
    private Context context;
    private RelativeLayout activity;
    private EditText et_username, et_password;
    private Button btn_submit;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        activity = findViewById(R.id.activity);
        context = RegistrationActivity.this;
        FirebaseApp.initializeApp(context);
        mAuth = FirebaseAuth.getInstance();

        if (Utils.getPreferencesBool(UniversalConstants.LOGIN, context))
        {
            startActivity(new Intent(context, LocationActivity.class));
            finish();
        }
        bindViews();
    }

    private void bindViews() {
        et_password = findViewById(R.id.et_password);
        et_username = findViewById(R.id.et_username);
        btn_submit = findViewById(R.id.btn_submit);

        /*SUBMIT REGISTRATION*/
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dataValid())
                {
                    final ProgressDialog progressDialog =
                            ProgressDialog.show(context, "Registering your account",
                                    "Please wait...", true);
                    final String email = et_username.getText().toString() + "driver@hippo.com";
                    String password = et_password.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("email", email);
                                        data.put("token", FirebaseInstanceId.getInstance().getToken());
                                        /*FirebaseDatabase.getInstance().getReference("drivers")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .setValue(data);*/
                                        /*SAVE DATA TO SHARED PREF*/
                                        Utils.savePreferences(UniversalConstants.DRIVER_UID, mAuth.getCurrentUser().getUid(), context);
                                        Utils.savePreferences(UniversalConstants.DRIVER_TOKEN, FirebaseInstanceId.getInstance().getToken(), context);
                                        Utils.savePreferences(UniversalConstants.DRIVER_USERNAME, et_username.getText().toString(), context);
                                        Utils.savePreferencesBool(UniversalConstants.LOGIN, true, context);
                                        progressDialog.dismiss();
                                        startActivity(new Intent(context, LocationActivity.class));
                                        finish();
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Snackbar.make(activity, "Try again", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Snackbar.make(activity, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            ;

                        }
                    });
                }
            }
        });
    }

    private boolean dataValid()
    {
        if (et_username.getText().toString().equalsIgnoreCase(""))
        {
            et_username.setError("Required");
            return false;
        }
        if (et_password.getText().toString().equalsIgnoreCase(""))
        {
            et_password.setError("Required");
            return false;
        }
        return true;
    }
}
