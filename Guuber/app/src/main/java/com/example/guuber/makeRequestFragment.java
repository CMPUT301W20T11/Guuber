package com.example.guuber;

import android.app.AlertDialog;
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
import androidx.fragment.app.DialogFragment;

public class makeRequestFragment extends DialogFragment {
    private EditText pickup;
    private EditText dropoff;
    private EditText tip;
    private TextView picklabel;
    private TextView droplabel;
    private TextView fareamount;
    private Button tipButton;
    private Button makeRequestButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.make_request_dialog1, null);
        pickup = view.findViewById(R.id.pickupAddTRq1);
        dropoff = view.findViewById(R.id.dropoffAddTRq1);
        tip = view.findViewById(R.id.tipamtRq1);
        picklabel = view.findViewById(R.id.pickupAddRq1);
        droplabel = view.findViewById(R.id.dropoffAddRq1);
        fareamount = view.findViewById(R.id.fareamtRq1);
        tipButton = view.findViewById(R.id.tipButtonRq1);
        makeRequestButton = view.findViewById(R.id.makeRequestButtonRq1);

        makeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("first statement.");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Make a ride request")
                .create();


    }
}
