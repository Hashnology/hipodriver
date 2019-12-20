package com.karimapps.hipodriver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.karimapps.hipodriver.R;

import universal.UniversalConstants;
import universal.Utils;

public class CodeConfirmationFragment extends Fragment
    implements View.OnClickListener
{
    private Context context;
    private TextView tv_code_sent;
    private EditText et_code;
    private FloatingActionButton btn_submit;
    private String confirmation_code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_code_confirmation, container, false) ;
        context = getActivity();

        bindViews(view);
        setOnClicks();
        setValues();
        return view;
    }

    private void setValues()
    {
        confirmation_code = getArguments().getString(UniversalConstants.CONFIRMATION_CODE);
        tv_code_sent.setText(getString(R.string.txt_we_sent_code)+" "+
                    Utils.getPreferences(UniversalConstants.DRIVER_PHONE, context)+"."+
                    getString(R.string.txt_put_code_here));
    }

    private void setOnClicks()
    {
        btn_submit.setOnClickListener(this);
    }

    private void bindViews(View view)
    {
        tv_code_sent = view.findViewById(R.id.tv_code_sent);
        et_code = view.findViewById(R.id.et_code);
        btn_submit = view.findViewById(R.id.btn_submit);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_submit:
                if (dataValid())
                {
                    if (et_code.getText().toString().equalsIgnoreCase(confirmation_code))
                    {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        Utils.savePreferencesBool(UniversalConstants.LOGIN, true, context);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, new QuestionairFragment());
                            transaction.commit();
                    }
                    else
                    {
                        Utils.showSnackbar(view, "Invalid code");
                    }
                }
                break;
        }

    }

    private boolean dataValid()
    {
        if (et_code.getText().toString().equalsIgnoreCase(""))
        {
            et_code.setError("Required");
            return false;
        }
        return true;
    }
}
