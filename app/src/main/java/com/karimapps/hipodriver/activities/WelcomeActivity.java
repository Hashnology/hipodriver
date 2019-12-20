package com.karimapps.hipodriver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karimapps.hipodriver.R;

import java.util.Locale;

import universal.AppUtils;
import universal.UniversalConstants;
import universal.Utils;

public class WelcomeActivity extends AppCompatActivity
{
    private Context context;
    private RelativeLayout fragment;
    private RelativeLayout btn_language;
    private TextView tv_language;
    private FloatingActionButton btn_next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        context = WelcomeActivity.this;

        bindViews();
    }

    private void bindViews()
    {

        fragment = findViewById(R.id.fragment);
        btn_language = findViewById(R.id.btn_language);
        tv_language = findViewById(R.id.tv_language);
        btn_next = findViewById(R.id.btn_next);

        /*check if language is set*/
        if (!Utils.getPreferences(UniversalConstants.LANGUAGE, context).equalsIgnoreCase("") && Utils.getPreferences(UniversalConstants.SET_LAN, context).equalsIgnoreCase("false"))
        {
            tv_language.setText(Utils.ucFirst(Utils.getPreferences(UniversalConstants.LANGUAGE, context)));
            if(Utils.getPreferences(UniversalConstants.LANGUAGE, context).equals("עברית")){
                Locale hebLocale = new Locale("iw");
                AppUtils.setLocale(hebLocale);
                AppUtils.setConfigChange(context);
            }else if(Utils.getPreferences(UniversalConstants.LANGUAGE, context).equals("ENGLISH")){
                Locale enLocale = new Locale("en");
                AppUtils.setLocale(enLocale);
                AppUtils.setConfigChange(context);
            }else if(Utils.getPreferences(UniversalConstants.LANGUAGE, context).equals("عربى")){
                Locale arLocale = new Locale("ar");
                AppUtils.setLocale(arLocale);
                AppUtils.setConfigChange(context);
            }else if(Utils.getPreferences(UniversalConstants.LANGUAGE, context).equals("русский")){
                Locale ruLocale = new Locale("ru");
                AppUtils.setLocale(ruLocale);
                AppUtils.setConfigChange(context);
            }
            Utils.savePreferences(UniversalConstants.SET_LAN,"false",context);
            if(Utils.getPreferences(UniversalConstants.LOGGED_IN,context).equalsIgnoreCase("true")){
                startActivity(new Intent(context, MainActivity.class));
            }else{
                startActivity(new Intent(context, LoginActivity.class));
            }

            finish();
        }

        /*choose language*/
        btn_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                PopupMenu menu = new PopupMenu(context, view);
                menu.getMenuInflater().inflate(R.menu.menu_language,
                        menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.lang_engilsh:
                                tv_language.setText("Language: Engilsh");
                                Utils.savePreferences(UniversalConstants.LANGUAGE, "ENGLISH", context);
                                Locale enLocale = new Locale("en");
                                AppUtils.setLocale(enLocale);
                                AppUtils.setConfigChange(context);
                                break;
                            case R.id.lang_hebrew:
                                tv_language.setText("שפה: עברית");
                                Utils.savePreferences(UniversalConstants.LANGUAGE, "עברית", context);
                                Locale hebLocale = new Locale("iw");
                                AppUtils.setLocale(hebLocale);
                                AppUtils.setConfigChange(context);
                                break;
                            case R.id.lang_arabic:
                                tv_language.setText("لغة: عربى");
                                Utils.savePreferences(UniversalConstants.LANGUAGE, "عربى", context);
                                Locale arLocale = new Locale("ar");
                                AppUtils.setLocale(arLocale);
                                AppUtils.setConfigChange(context);
                                break;
                            case R.id.lang_russian:
                                tv_language.setText("язык: русский");
                                Utils.savePreferences(UniversalConstants.LANGUAGE, "русский", context);
                                Locale ruLocale = new Locale("ru");
                                AppUtils.setLocale(ruLocale);
                                AppUtils.setConfigChange(context);
                                break;
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });

        /*go next*/
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (Utils.getPreferences(UniversalConstants.LANGUAGE, context).equalsIgnoreCase(""))
                {
                    Snackbar.make(view, "Please choose your language", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Utils.savePreferences(UniversalConstants.SET_LAN,"false",context);
                if(Utils.getPreferences(UniversalConstants.LOGGED_IN,context).equalsIgnoreCase("true")){
                    startActivity(new Intent(context, MainActivity.class));
                }else{
                    startActivity(new Intent(context, LoginActivity.class));
                }
                finish();
            }
        });
    }
}

