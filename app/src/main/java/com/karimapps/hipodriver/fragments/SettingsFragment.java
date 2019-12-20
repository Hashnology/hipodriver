package com.karimapps.hipodriver.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.WelcomeActivity;

import universal.UniversalConstants;
import universal.Utils;

public class SettingsFragment extends Fragment
{
    private Context context;
    private TextView tv_firstname, tv_lastname, tv_suborgname, tv_linename;
    private TextView tv_language;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getActivity();

        bindViews(view);
        setValues();
        return view;

    }

    private void setValues()
    {
        /*set action bar title*/
//        ((MainActivity) getActivity())
//                .setActionBarTitle("Settings");

        tv_firstname.setText(Utils.getPreferences(UniversalConstants.FIRSTNAME, context));
        tv_lastname.setText(Utils.getPreferences(UniversalConstants.LASTNAME, context));
        tv_suborgname.setText(Utils.getPreferences(UniversalConstants.SUBORGANIZATION_NAME, context));
        tv_linename.setText(Utils.getPreferences(UniversalConstants.LINE_NAME, context));
        tv_language.setText(Utils.getPreferences(UniversalConstants.LANGUAGE, context));
        tv_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.savePreferences(UniversalConstants.SET_LAN,"true",context);
               startActivity(new Intent(getActivity(), WelcomeActivity.class));
            }
        });
    }

    private void bindViews(View view)
    {
        tv_firstname = view.findViewById(R.id.tv_firstname);
        tv_lastname = view.findViewById(R.id.tv_lastname);
        tv_suborgname = view.findViewById(R.id.tv_suborg_name);
        tv_linename = view.findViewById(R.id.tv_linename);
        tv_language = view.findViewById(R.id.tv_language);
    }
}
