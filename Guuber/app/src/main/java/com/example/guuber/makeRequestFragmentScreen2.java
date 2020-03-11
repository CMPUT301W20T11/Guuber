package com.example.guuber;

import android.app.Dialog;
import android.content.DialogInterface;
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

public class makeRequestFragmentScreen2 extends DialogFragment {

    private EditText pickup;
    private EditText dropoff;
    private TextView picklabel;
    private TextView droplabel;
    private TextView fareamount;
    private Button viewProfileButton;
    private Button acceptButton;
    private Button declineButton;
    Driver theDriver;
    Vehicle vehicle;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.make_request_dialog2, null);
        vehicle = new Vehicle("Toyota", "RunX", "Gold", "AEJ 0430");
        theDriver = new Driver("+15879388900", "osiemusariri@gmail.com", "Oswell",
                "Musariri", vehicle);

        pickup = view.findViewById(R.id.pickupAddTRq2);
        dropoff = view.findViewById(R.id.dropoffAddTRq2);
        picklabel = view.findViewById(R.id.pickupAddRq2);
        droplabel = view.findViewById(R.id.dropoffAddRq2);
        fareamount = view.findViewById(R.id.fareamtRq2);

        viewProfileButton = view.findViewById(R.id.viewDrProfRq2);
        acceptButton = view.findViewById(R.id.AcceptButtonRq2);
        declineButton = view.findViewById(R.id.DeclineRq2);

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theDriver.viewProfile();
            }

        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("first statement.");
            }
        });
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("first statement.");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Confirm ride")
                .create();

    }
}
