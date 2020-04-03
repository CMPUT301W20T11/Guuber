package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guuber.model.GuuDbHelper;
import com.example.guuber.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Code to display drivers information on their profile
 */

public class DriverProfileActivity extends AppCompatActivity {
    private String username;
    private String email;
    private String phoneNumber;
    private Integer posRate;
    private Integer negRate;

    private TextView emailField;
    private TextView usernameField;
    private TextView phoneNumberField;
    private TextView posRateDisplay;
    private TextView negRateDisplay;
    private ImageView likeButton;
    private ImageView dislikeButton;
    private ImageView profileImg;

    private Button deleteButton;
    private User userInfo;
    private Boolean editable;

    /**
     * Handle to the firebase database helper class and the collection of user profiles in the database
     * */
    private FirebaseFirestore driverMapsDB = FirebaseFirestore.getInstance();
    private GuuDbHelper driverDBHelper = new GuuDbHelper(driverMapsDB);
    private CollectionReference uRef = driverMapsDB.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_disp);

        /**Intent string extra with the id CALLER has 2 values internal and external
         * If the caller is internal then the driver is viewing their own profile and if the caller is external then another user is viewing the user's profile
         **/
        String caller = getIntent().getStringExtra("caller");
        editable = caller.equals("internal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(DriverProfileActivity.this, "Click and hold the information you would like to edit !",Toast.LENGTH_LONG);

        phoneNumberField = findViewById(R.id.phoneTextDrIn);
        usernameField = findViewById(R.id.usernameTextDrIn);
        emailField = findViewById(R.id.emailTextDrIn);
        likeButton = findViewById(R.id.likeButtonDrIn);
        dislikeButton = findViewById(R.id.dislikeButtonDrIn);
        profileImg = findViewById(R.id.imageViewDrIn);
        posRateDisplay = findViewById(R.id.posRateDr);
        negRateDisplay = findViewById(R.id.negRateDr);

        if (editable){
            userInfo = ((UserData)(getApplicationContext())).getUser();
            phoneNumber = userInfo.getPhoneNumber();
            username = userInfo.getUsername();
            email = userInfo.getEmail();
            posRate = userInfo.getPosRating();
            negRate = userInfo.getNegRating();

            /**
             * allows for editing userdata
             */
            phoneNumberField.setOnLongClickListener(v -> {
                EditUserdataFragment fragment = new EditUserdataFragment();
                Bundle phoneBundle = new Bundle();
                phoneBundle.putString("field", "phone number");
                phoneBundle.putString("old", phoneNumber);
                phoneBundle.putString("activity", "DriverProfilActivity");
                fragment.setArguments(phoneBundle);
                fragment.show(getSupportFragmentManager(), "Edit Phone Number");
                return true;
            });

            usernameField.setOnLongClickListener(v -> {
                EditUserdataFragment fragment = new EditUserdataFragment();
                Bundle phoneBundle = new Bundle();
                phoneBundle.putString("field", "username");
                phoneBundle.putString("old", username);
                phoneBundle.putString("activity", "DriverProfilActivity");
                fragment.setArguments(phoneBundle);
                fragment.show(getSupportFragmentManager(), "Edit Phone Number");
                return true;
            });
            phoneNumberField.setText(phoneNumber);
            usernameField.setText(username);
            emailField.setText(email);
            likeButton.setImageResource(R.drawable.smile);
            dislikeButton.setImageResource(R.drawable.frowny);

            negRateDisplay.setText(negRate.toString());
            posRateDisplay.setText(posRate.toString());
        }//finish editable if
        else{
            String externalEmail = getIntent().getStringExtra("external_email");
            uRef.document(externalEmail).addSnapshotListener(this, (documentSnapshot, e) -> {
                userInfo = documentSnapshot.toObject(User.class);
                phoneNumber = userInfo.getPhoneNumber();
                username = userInfo.getUsername();
                email = userInfo.getEmail();
                posRate = userInfo.getPosRating();
                negRate = userInfo.getNegRating();
                phoneNumberField.setText(phoneNumber);
                usernameField.setText(username);
                emailField.setText(email);
                likeButton.setImageResource(R.drawable.smile);
                dislikeButton.setImageResource(R.drawable.frowny);
                negRateDisplay.setText(negRate.toString());
                posRateDisplay.setText(posRate.toString());

            });
            //like and dislike buttons onclick listeners to rate drivers and riders from their profile view

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editable) {
                        userInfo.adjustRating(true);
                        Toast.makeText(DriverProfileActivity.this, "Profile liked!", Toast.LENGTH_LONG).show();
                        rateUser(true);
                        likeButton.setClickable(false);
                    }
                }
            });
            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editable) {
                        userInfo.adjustRating(true);
                        Toast.makeText(DriverProfileActivity.this, "Profile NOT liked!", Toast.LENGTH_LONG).show();
                        rateUser(false);
                        dislikeButton.setClickable(false);
                    }
                }
            });
                            //onClickListeners for email and phone number fields to contact User

            emailField.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email));
                startActivity(intent);
            });

            phoneNumberField.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            });

        } //finish uneditable else


        deleteButton = findViewById(R.id.deleteAccButtonDrIn);
        if (!editable){deleteButton.setVisibility(View.INVISIBLE);}
        deleteButton.setOnClickListener(v -> {
            if (editable){
                DeleteAccountFragment deleteFragment = new DeleteAccountFragment();
                Bundle callingActivity = new Bundle();
                callingActivity.putString("callingActivity", "driver");
                deleteFragment.setArguments(callingActivity);
                deleteFragment.show(getSupportFragmentManager(), "Delete Account");}
        });

    }

    /**when back button pressed activity finishes and returns to calling activity**/
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

    /**
     * Called exclusively by EditUserDataFragment, changes the value for whatever field has been updated in the user class
     * calls separate methods to update the database and the views
     * @param field
     * @param value
     */
    public void updateData(String field, String value) {
        if (field.equals("phone number")) {
            userInfo.setPhoneNumber(value);
        } else if (field.equals("username")) {
            userInfo.setUsername(value);
        }
        updateDatabase();
        updateViews();
    }

    /**
     * Updates the database with the new user information from when it was changed
     * */
    public void updateDatabase(){
        uRef.document(userInfo.getEmail()).set(userInfo);
    }

    /**
     * directly increments the user rating in the database
     * */
    public void rateUser(Boolean rating){
        if(!rating){
            uRef.document(email).update("negRating", FieldValue.increment(1));
        }else{
            uRef.document(email).update("posRating", FieldValue.increment(1));
        }
    }

    /***
     * deletes user's profile from the database records
     * returns user to login screen
     */
    public void deleteSelf(){
            driverDBHelper.deleteUser(userInfo.getEmail());
            Toast.makeText(DriverProfileActivity.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
    }

    /**
     * Updates the views with the current information on user profile
     * */
    public void updateViews(){
        phoneNumber = userInfo.getPhoneNumber();
        username = userInfo.getUsername();
        email = userInfo.getEmail();
        posRate = userInfo.getPosRating();
        negRate = userInfo.getNegRating();

        phoneNumberField.setText(phoneNumber);
        usernameField.setText(username);
        emailField.setText(email);
        negRateDisplay.setText(negRate.toString());
        posRateDisplay.setText(posRate.toString());
    }

}

