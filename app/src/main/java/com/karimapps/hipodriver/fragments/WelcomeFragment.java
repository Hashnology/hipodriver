package com.karimapps.hipodriver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.activities.MainActivity;
import com.karimapps.hipodriver.apis.GetPlanStations;

import universal.UniversalConstants;
import universal.Utils;

public class WelcomeFragment extends Fragment
{
    private Context context;
    private RelativeLayout fragment;
    private TextView btn_language;
    private FloatingActionButton btn_next;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        context = getActivity();

        GetPlanStations getPlanStations = new GetPlanStations(context);



        bindViews(view);

        return view;
    }

    private void bindViews(View view)
    {
        MainActivity.toolbar.setVisibility(View.GONE);

        fragment = view.findViewById(R.id.fragment);
        btn_language = view.findViewById(R.id.btn_language);
        btn_next = view.findViewById(R.id.btn_next);

        /*check if language is set*/
        if (!Utils.getPreferences(UniversalConstants.LANGUAGE, context).equalsIgnoreCase(""))
        {
            btn_language.setText(Utils.ucFirst(Utils.getPreferences(UniversalConstants.LANGUAGE, context)));
        }
        if (!Utils.getPreferences(UniversalConstants.LANGUAGE, context).equalsIgnoreCase(""))
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new LoginFragment());
            transaction.commit();
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
                                btn_language.setText("Language: Engilsh");
                                Utils.savePreferences(UniversalConstants.LANGUAGE, "english", context);
                                break;
                            case R.id.lang_hebrew:
                                btn_language.setText("שפה: עברית");
                                Utils.savePreferences(UniversalConstants.LANGUAGE, "עברית", context);
                                break;
                            case R.id.lang_arabic:
                                btn_language.setText("لغة: عربى");
                                Utils.savePreferences(UniversalConstants.LANGUAGE, "عربى", context);
                                break;
                            case R.id.lang_russian:
                                btn_language.setText("язык: русский");
                                Utils.savePreferences(UniversalConstants.LANGUAGE, "русский", context);
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
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new LoginFragment());
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
    }
}
