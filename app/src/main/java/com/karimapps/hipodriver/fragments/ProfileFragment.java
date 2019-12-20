package com.karimapps.hipodriver.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.LoginActivity;

import universal.UniversalConstants;
import universal.Utils;

public class ProfileFragment extends Fragment
{
    private Context context;
    private TextView tv_firstname, tv_lastname, tv_suborg, tv_date;
    private ImageView btn_logout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        context = getActivity();

        bindViews(view);
        setValues();


        return view;
    }

    private void setValues()
    {
        tv_firstname.setText(Utils.getPreferences(UniversalConstants.FIRSTNAME, context));
        tv_lastname.setText(Utils.getPreferences(UniversalConstants.LASTNAME, context));
        tv_suborg.setText(Utils.getPreferences(UniversalConstants.SUBORGANIZATION_NAME, context));
        tv_date.setText(Utils.getPreferences(UniversalConstants.DRIVER_DEPARTURE_DATE, context));
    }

    private void bindViews(View view)
    {
        btn_logout = view.findViewById(R.id.btn_logout);
        tv_firstname = view.findViewById(R.id.tv_firstname);
        tv_lastname = view.findViewById(R.id.tv_lastname);
        tv_suborg = view.findViewById(R.id.tv_suborg);
        tv_date = view.findViewById(R.id.tv_date);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Utils.savePreferencesBool(UniversalConstants.LOGIN, false, context);
                activity.startActivity(new Intent(context, LoginActivity.class));
                activity.finish();
            }
        });
    }
}
