package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DriverProfileActivity extends AppCompatActivity {

    Driver myself;
    String username;
    String email;
    String phoneNumber;
    String carReg;
    String status;
    EditText emailField;
    EditText usernameField;
    EditText phoneNumberField;
    EditText vehicleRegField;
    EditText statusField;
    Button deleteButton;
    Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_disp);

        vehicle = new Vehicle("Toyota", "RunX", "Gold", "AEJ 0430");

        myself = new Driver("+15879388900", "osiemusariri@gmail.com", "Oswell",
                "Musariri", vehicle);

        vehicleRegField = findViewById(R.id.carRegTextDrIn);
        phoneNumberField = findViewById(R.id.phoneTextRdIn);
        usernameField = findViewById(R.id.usernameTextRdIn);
        emailField = findViewById(R.id.emailTextRdIn);
        statusField = findViewById(R.id.availabilityTextDrIn);

        phoneNumber = myself.getPhoneNumber();
        username = myself.getFirstName();
        email = myself.getEmail();
        carReg = myself.getVehicle().getReg();

        vehicleRegField.setText("Found");
        phoneNumberField.setText("Found");
        usernameField.setText("Found");
        emailField.setText("Found");
        statusField.setText("Found");

        deleteButton = findViewById(R.id.deleteAccButtonRdIn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("first statement.");
            }
        });

    }
}
