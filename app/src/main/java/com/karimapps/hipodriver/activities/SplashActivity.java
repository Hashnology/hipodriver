package com.karimapps.hipodriver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.karimapps.hipodriver.R;

import universal.UniversalConstants;
import universal.Utils;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_activity);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if((Utils.getPreferences(UniversalConstants.LOGGED_IN,SplashActivity.this).equalsIgnoreCase("true"))){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    finish();
                }
            }
        },2000);
    }
}
