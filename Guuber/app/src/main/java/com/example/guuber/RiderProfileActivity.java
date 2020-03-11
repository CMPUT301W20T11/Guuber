package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RiderProfileActivity extends AppCompatActivity {

    Rider myself;
    String username;
    String email;
    String phoneNumber;
    EditText emailField;
    EditText usernameField;
    EditText phoneNumberField;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_disp);
        myself = new Rider("+263784446345", "musariri@ualberta.ca", "Tinashe",
                "Musariri");

        phoneNumberField = findViewById(R.id.phoneTextRdIn);
        usernameField = findViewById(R.id.usernameTextRdIn);
        emailField = findViewById(R.id.emailTextRdIn);

        phoneNumber = myself.getPhoneNumber();
        username = myself.getFirstName();
        email = myself.getEmail();

        phoneNumberField.setText("Found");
        usernameField.setText("Found");
        emailField.setText("Found");

        deleteButton = findViewById(R.id.deleteAccButtonRdIn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("first statement.");
            }
        });
    }
}
