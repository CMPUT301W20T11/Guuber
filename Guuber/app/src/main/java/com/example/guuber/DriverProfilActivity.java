package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.guuber.model.Driver;
import com.example.guuber.model.Vehicle;

public class DriverProfilActivity extends AppCompatActivity {
    Driver myself;
    String username;
    String email;
    String phoneNumber;
    String carReg;
    String status;

    TextView emailField;
    TextView usernameField;
    TextView phoneNumberField;
    TextView vehicleRegField;
    TextView statusField;

    Button deleteButton;
    Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_disp);

        /**display the back button**/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vehicle = new Vehicle("Toyota", "RunX", "Gold", "AEJ 0430");

        myself = new Driver("+15879388900", "osiemusariri@gmail.com", "Oswell",
        "Musariri", vehicle);

        vehicleRegField = findViewById(R.id.carRegTextDrIn);
        phoneNumberField = findViewById(R.id.phoneTextDrIn);
        usernameField = findViewById(R.id.usernameTextDrIn);
        emailField = findViewById(R.id.emailTextDrIn);
        statusField = findViewById(R.id.availabilityTextDrIn);

        phoneNumber = myself.getPhoneNumber();
        username = myself.getFirstName();
        email = myself.getEmail();
        carReg = myself.getVehicle().getReg();
        status = myself.getStatus();

        vehicleRegField.setText(carReg);
        phoneNumberField.setText(phoneNumber);
        usernameField.setText(username);
        emailField.setText(email);
        statusField.setText(status);

        //deleteButton = findViewById(R.id.deleteAccButtonRdIn);
        //deleteButton.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //  public void onClick(View v) {
         //   System.out.println("first statement.");
        //}
        //});

        //deleteButton = findViewById(R.id.deleteAccButtonDrIn);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("first statement.");
//            }
//        });

    }
    /**implement logic here for what you want to
     * happen upon back button press**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
