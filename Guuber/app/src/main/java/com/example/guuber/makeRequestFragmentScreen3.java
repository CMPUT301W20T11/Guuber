package com.example.guuber;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class makeRequestFragmentScreen3 extends DialogFragment {

    private EditText pickup;
    private EditText dropoff;
    private TextView picklabel;
    private TextView droplabel;
    private TextView fareamount;
    private Button viewProfileButton;
    private Button emailButton;
    private Button callButton;
    Driver theDriver;
    Vehicle vehicle;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.make_request_dialog2, null);
        vehicle = new Vehicle("Toyota", "RunX", "Gold", "AEJ 0430");
        theDriver = new Driver("+15879388900", "osiemusariri@gmail.com", "Oswell",
                "Musariri", vehicle);

        pickup = view.findViewById(R.id.pickupAddTRq3);
        dropoff = view.findViewById(R.id.dropoffAddTRq3);
        picklabel = view.findViewById(R.id.pickupAddRq3);
        droplabel = view.findViewById(R.id.dropoffAddRq3);
        fareamount = view.findViewById(R.id.fareamtRq3);

        viewProfileButton = view.findViewById(R.id.viewDrProfRq2);
        emailButton = view.findViewById(R.id.emailDrRq3);
        callButton = view.findViewById(R.id.callDrRq3);

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theDriver.viewProfile();
            }

        });
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("first statement.");
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("first statement.");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Ride status")
                .create();

    }
}
