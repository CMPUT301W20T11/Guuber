package com.example.guuber;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewUserActivity extends AppCompatActivity {

    private static final String phoneNumber = "";
    private static final Boolean isDriver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user_layout);
        EditText phoneNumber = findViewById(R.id.phone_number);
        EditText vehicleMake = findViewById(R.id.vehicle_make);
        EditText vehicleModel = findViewById(R.id.vehicle_model);
        EditText vehicleColor = findViewById(R.id.vehicle_color);
        EditText licensePlate = findViewById(R.id.license_plate);
        Button driverProfile = findViewById(R.id.driver_button);
        // TODO

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String email = user.getEmail();
        }
    }
}
