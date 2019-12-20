package com.karimapps.hipodriver.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.models.Passenger;
import com.karimapps.hipodriver.viewholders.PassengerViewHolder;

import java.util.List;

public class PassengersAdapter extends RecyclerView.Adapter<PassengerViewHolder>
{
    private Context context;
    private List<Passenger> passengers;
    private LayoutInflater inflater;
    private PassengerViewHolder holder;
    private Passenger passenger;

    public PassengersAdapter(Context context, List<Passenger> passengers) {
        this.context = context;
        this.passengers = passengers;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = inflater.inflate(R.layout.item_passenger, viewGroup, false);
        holder = new PassengerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PassengerViewHolder holder, int i)
    {
        passenger = passengers.get(i);
        holder.tv_name.setText(passenger.getFirstName()+" "+passenger.getLastName());
        holder.tv_phone.setText(passenger.getPhoneNumber());

        /*MAKE A CALL*/
        holder.btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = holder.getPosition();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + passengers.get(pos).getPhoneNumber()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                activity.startActivity(callIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }
}
