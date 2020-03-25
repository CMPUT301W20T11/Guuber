package com.example.guuber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guuber.model.User;
import com.example.guuber.model.Vehicle;

/**
 * code for one of the options in
 * makeRequestScreen1 immediately after a rider
 * makes a request
 */
public class makeRequestScreen3 extends AppCompatActivity{
    private EditText pickup;
    private EditText dropoff;
    private TextView picklabel;
    private TextView droplabel;
    private TextView fareamount;
    private Button viewProfileButton;
    private Button emailButton;
    private Button callButton;
    User theDriver;
    Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_request_dialog3);

        vehicle = new Vehicle("Toyota", "RunX", "Gold", "AEJ 0430");
        //theDriver = new Driver("+15879388900", "osiemusariri@gmail.com", "Oswell",
		//		"Musariri", "1", "TestUserName", vehicle);

        pickup = findViewById(R.id.pickupAddRq3);
        dropoff = findViewById(R.id.dropoffAddRq3);
        picklabel = findViewById(R.id.pickupAddTRq3);
        droplabel = findViewById(R.id.dropoffAddTRq3);
        fareamount = findViewById(R.id.fareamtRq3);

        viewProfileButton = findViewById(R.id.viewDrProfRq3);
        emailButton = findViewById(R.id.emailDrRq3);
        callButton = findViewById(R.id.callDrRq3);

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent riderProfileIntent = new Intent(makeRequestScreen3.this, DriverProfilActivity.class);
                startActivity(riderProfileIntent);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:osiemusariri@gmail.com"));
                startActivity(intent);
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+263784446345"));
                startActivity(intent);
            }
        });

    }
}
//public class makeRequestFragmentScreen3 extends DialogFragment {
//
//    private EditText pickup;
//    private EditText dropoff;
//    private TextView picklabel;
//    private TextView droplabel;
//    private TextView fareamount;
//    private Button viewProfileButton;
//    private Button emailButton;
//    private Button callButton;
//    Driver theDriver;
//    Vehicle vehicle;
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.make_request_dialog2, null);
//        vehicle = new Vehicle("Toyota", "RunX", "Gold", "AEJ 0430");
//        theDriver = new Driver("+15879388900", "osiemusariri@gmail.com", "Oswell",
//                "Musariri", vehicle);
//
//        pickup = view.findViewById(R.id.pickupAddTRq3);
//        dropoff = view.findViewById(R.id.dropoffAddTRq3);
//        picklabel = view.findViewById(R.id.pickupAddRq3);
//        droplabel = view.findViewById(R.id.dropoffAddRq3);
//        fareamount = view.findViewById(R.id.fareamtRq3);
//
//        viewProfileButton = view.findViewById(R.id.viewDrProfRq2);
//        emailButton = view.findViewById(R.id.emailDrRq3);
//        callButton = view.findViewById(R.id.callDrRq3);
//
//        viewProfileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                theDriver.viewProfile();
//            }
//
//        });
//        emailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("first statement.");
//            }
//        });
//        callButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("first statement.");
//            }
//        });
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        return builder
//                .setView(view)
//                .setTitle("Ride status")
//                .create();
//
//    }
//}
