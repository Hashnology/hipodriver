package com.karimapps.hipodriver.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.karimapps.hipodriver.R;

public class PassengerViewHolder extends RecyclerView.ViewHolder
{
    public TextView tv_name, tv_phone;
    public ImageButton btn_call;

    public PassengerViewHolder(@NonNull View itemView)
    {
        super(itemView);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_phone = itemView.findViewById(R.id.tv_phone);
        btn_call = itemView.findViewById(R.id.btn_call);
    }
}
