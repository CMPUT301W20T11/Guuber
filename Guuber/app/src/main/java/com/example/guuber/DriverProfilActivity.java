package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guuber.model.User;
import com.example.guuber.model.Vehicle;

/**
 * Code to display drivers information on their profile
 */

public class DriverProfilActivity extends AppCompatActivity {
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
    ImageView likeButton;
    ImageView dislikeButton;
    ImageView profileImg;

    Button deleteButton;
    Vehicle vehicle;
    User userInfo;
    Boolean editable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_disp);
        //UserData userData = UserData.getInstance();
        userInfo = ((UserData)(getApplicationContext())).getUser();

        String caller = getIntent().getStringExtra("caller");
        editable = caller.equals("internal");

        /**display the back button**/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vehicle = new Vehicle("Toyota", "RunX", "Gold", "AEJ 0430");

        vehicleRegField = findViewById(R.id.carRegTextDrIn);
        phoneNumberField = findViewById(R.id.phoneTextDrIn);
        usernameField = findViewById(R.id.usernameTextDrIn);
        emailField = findViewById(R.id.emailTextDrIn);
        statusField = findViewById(R.id.availabilityTextDrIn);
        likeButton = findViewById(R.id.likeButtonDrIn);
        dislikeButton = findViewById(R.id.dislikeButtonDrIn);
        profileImg = findViewById(R.id.imageViewDrIn);

        phoneNumber = userInfo.getPhoneNumber();
        username = userInfo.getUsername();
        email = userInfo.getEmail();

        /**
         * allows for editing userdata
         */
        if (editable){
            phoneNumberField.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    EditUserdataFragment fragment = new EditUserdataFragment();
                    Bundle phoneBundle = new Bundle();
                    phoneBundle.putString("field", "phone number");
                    phoneBundle.putString("old", phoneNumber);
                    phoneBundle.putString("activity", "DriverProfilActivity");
                    fragment.setArguments(phoneBundle);
                    fragment.show(getSupportFragmentManager(), "Edit Phone Number");
                    return true;
                }
            });

            usernameField.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    EditUserdataFragment fragment = new EditUserdataFragment();
                    Bundle phoneBundle = new Bundle();
                    phoneBundle.putString("field", "username");
                    phoneBundle.putString("old", username);
                    phoneBundle.putString("activity", "DriverProfilActivity");
                    fragment.setArguments(phoneBundle);
                    fragment.show(getSupportFragmentManager(), "Edit Phone Number");
                    return true;
                }
            });

            emailField.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    EditUserdataFragment fragment = new EditUserdataFragment();
                    Bundle phoneBundle = new Bundle();
                    phoneBundle.putString("field", "email");
                    phoneBundle.putString("old", email);
                    phoneBundle.putString("activity", "DriverProfilActivity");
                    fragment.setArguments(phoneBundle);
                    fragment.show(getSupportFragmentManager(), "Edit Phone Number");
                    return true;
                }
            });
        }

        vehicleRegField.setText(carReg);
        phoneNumberField.setText(phoneNumber);
        usernameField.setText(username);
        emailField.setText(email);
        statusField.setText(status);
        likeButton.setImageResource(R.drawable.smile);
        dislikeButton.setImageResource(R.drawable.frowny);
        profileImg.setImageResource(R.drawable.profilepic);

        deleteButton = findViewById(R.id.deleteAccButtonDrIn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            System.out.println("first statement.");
        }
        });
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
    public void updateData(String field, String value){

        if (field.equals("email")){
            userInfo.setEmail(value);
        }
        else if (field.equals("phone number")){
            userInfo.setPhoneNumber(value);
        }
        else if (field.equals("username")){
            userInfo.setUsername(value);
        }
    }
}
