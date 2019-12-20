package com.karimapps.hipodriver.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.MainActivity;

public class HomeFragmentOld extends Fragment
{
    private Context context;
    private CardView btn_location, btn_profile;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.home_fragment_old, container, false);
        context = getActivity();
        MainActivity.toolbar.setVisibility(View.GONE);

        bindViews(view);

        return view;
    }

    private void bindViews(View view)
    {
        btn_location = view.findViewById(R.id.btn_location);
        btn_profile = view.findViewById(R.id.btn_profile);

        /*buses button*/
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new LocationFragmentAws());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        /*profile button*/
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new ProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
