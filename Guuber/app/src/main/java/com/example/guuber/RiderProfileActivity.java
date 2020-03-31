package com.example.guuber;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guuber.model.GuuDbHelper;
import com.example.guuber.model.User;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Code to display riders information on their profile
 */

public class RiderProfileActivity extends AppCompatActivity {

    String username;
    String email;
    String phoneNumber;
    TextView emailField;
    TextView usernameField;
    TextView phoneNumberField;
    TextView posRateDisplay;
    TextView negRateDisplay;
    Button deleteButton;
    ImageView likeButton;
    ImageView dislikeButton;
    ImageView profileImg;
    User userInfo;
    Boolean editable;
    Integer posRate;
    Integer negRate;

    /***********the database******/
    private FirebaseFirestore driverMapsDB = FirebaseFirestore.getInstance();
    private GuuDbHelper riderDBHelper = new GuuDbHelper(driverMapsDB);

    private static final String TAG = "RiderProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_disp);
        userInfo = ((UserData)(getApplicationContext())).getUser();

        Toast.makeText(RiderProfileActivity.this, "Click and hold the information you would like to edit !",Toast.LENGTH_LONG);

        String caller = getIntent().getStringExtra("caller");
        editable = caller.equals("internal");
        if (!editable){
            userInfo = (User) getIntent().getSerializableExtra("riderProfile");
        }
        /**display the back button**/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phoneNumberField = findViewById(R.id.phoneTextRdIn);
        usernameField = findViewById(R.id.usernameTextRdIn);
        emailField = findViewById(R.id.emailTextRdIn);
        likeButton = findViewById(R.id.likeButtonRdIn);
        dislikeButton = findViewById(R.id.dislikeButtonRdIn);
        profileImg = findViewById(R.id.imageViewRdIn);
        posRateDisplay = findViewById(R.id.posRate);
        negRateDisplay = findViewById(R.id.negRate);

        phoneNumber = userInfo.getPhoneNumber();
        username = userInfo.getUsername();
        email = userInfo.getEmail();
        posRate = userInfo.getPosRating();
        negRate = userInfo.getNegRating();

        //like and dislike buttons onclick listeners to rate drivers and riders from their profile view

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editable){userInfo.adjustRating(true);
                    Toast.makeText(RiderProfileActivity.this, "Profile liked!", Toast.LENGTH_LONG).show();
                    // for quick on screen test delete System.out.print("LIKE##################################################");
                }
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editable){userInfo.adjustRating(true);
                    Toast.makeText(RiderProfileActivity.this, "Profile NOT liked!", Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * allows for editing userdata
         */
        if (editable) {
            phoneNumberField.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    EditUserdataFragment fragment = new EditUserdataFragment();
                    Bundle phoneBundle = new Bundle();
                    phoneBundle.putString("field", "phone number");
                    phoneBundle.putString("old", phoneNumber);
                    phoneBundle.putString("activity", "RiderProfileActivity");
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
                    phoneBundle.putString("activity", "RiderProfileActivity");
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
                    phoneBundle.putString("activity", "RiderProfileActivity");
                    fragment.setArguments(phoneBundle);
                    fragment.show(getSupportFragmentManager(), "Edit Phone Number");
                    return true;
                }
            });
        }

        phoneNumberField.setText(phoneNumber);
        // for testing please disregard
        Log.d(TAG, "documentSnapshot.getString(\"phoneNumber\")" +" "+userInfo.getPhoneNumber());
        usernameField.setText(username);
        emailField.setText(email);
        likeButton.setImageResource(R.drawable.smile);
        dislikeButton.setImageResource(R.drawable.frowny);
        profileImg.setImageResource(R.drawable.profilepic);
        negRateDisplay.setText(negRate.toString()+"%");
        posRateDisplay.setText(posRate.toString()+"%");

        deleteButton = findViewById(R.id.deleteAccButtonRdIn);
        if (!editable){deleteButton.setVisibility(View.INVISIBLE);}
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editable){
                    DeleteAccountFragment deleteFragment = new DeleteAccountFragment();
                    Bundle callingActivity = new Bundle();
                    callingActivity.putString("callingActivity", "rider");
                    deleteFragment.setArguments(callingActivity);
                    deleteFragment.show(getSupportFragmentManager(), "Delete Account");}
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

    public void updateData(String field, String value)  {
        if (field.equals("email")){
            userInfo.setEmail(value);
        }
        else if (field.equals("phone number")){
            userInfo.setPhoneNumber(value);
        }
        else if (field.equals("username")){
            userInfo.setUsername(value);
        }

        /***
        try {
            riderDBHelper.updateProfileAll(userInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         ***/
    }

    public void deleteSelf(){
        riderDBHelper.deleteUser(userInfo.getEmail());
        Toast.makeText(RiderProfileActivity.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
