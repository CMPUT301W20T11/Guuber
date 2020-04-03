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

    /***********the database******/
    private FirebaseFirestore driverMapsDB = FirebaseFirestore.getInstance();
    private GuuDbHelper driverDBHelper = new GuuDbHelper(driverMapsDB);
    private CollectionReference uRef = driverMapsDB.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_disp);
        String caller = getIntent().getStringExtra("caller");
        editable = caller.equals("internal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
<<<<<<< HEAD:Guuber/app/src/main/java/com/example/guuber/DriverProfilActivity.java
        Toast.makeText(DriverProfilActivity.this, "Click and hold the information you would like to edit !",Toast.LENGTH_LONG);
=======

        Toast.makeText(DriverProfileActivity.this, "Click and hold the information you would like to edit !",Toast.LENGTH_LONG);
>>>>>>> da86671f01a1534a1b011716696a6134db2f97d5:Guuber/app/src/main/java/com/example/guuber/DriverProfileActivity.java



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
<<<<<<< HEAD:Guuber/app/src/main/java/com/example/guuber/DriverProfilActivity.java
            likeButton.setOnClickListener(v -> {
                if (!editable){userInfo.adjustRating(true);
                    Toast.makeText(DriverProfilActivity.this, "Profile liked!", Toast.LENGTH_LONG).show();
                    rateUser(true);
                    //updateDatabase();
                    likeButton.setClickable(false);
                }
            });

            dislikeButton.setOnClickListener(v -> {
                if (!editable){userInfo.adjustRating(true);
                    Toast.makeText(DriverProfilActivity.this, "Profile NOT liked!", Toast.LENGTH_LONG).show();
                    rateUser(false);
                    //updateDatabase();
                    dislikeButton.setClickable(false);
=======
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editable){userInfo.adjustRating(true);
                        Toast.makeText(DriverProfileActivity.this, "Profile liked!", Toast.LENGTH_LONG).show();
                        rateUser(true);
                        //updateDatabase();
                        likeButton.setClickable(false);
                    }
                }
            });

            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editable){userInfo.adjustRating(true);
                        Toast.makeText(DriverProfileActivity.this, "Profile NOT liked!", Toast.LENGTH_LONG).show();
                        rateUser(false);
                        //updateDatabase();
                        dislikeButton.setClickable(false);
                    }
>>>>>>> da86671f01a1534a1b011716696a6134db2f97d5:Guuber/app/src/main/java/com/example/guuber/DriverProfileActivity.java
                }
            });
            //onClickListeners for email and phone number fields to contact User

            emailField.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+ email));
                startActivity(intent);
            });

            phoneNumberField.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
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
    public void updateData(String field, String value) {
        if (field.equals("phone number")) {
            userInfo.setPhoneNumber(value);
        } else if (field.equals("username")) {
            userInfo.setUsername(value);
        }
        updateDatabase();
        updateViews();
    }

    public void updateDatabase(){
        uRef.document(userInfo.getEmail()).set(userInfo);
    }

    public void rateUser(Boolean rating){

        // Update the db object's rating
        if(!rating){
            uRef.document(email).update("negRating", FieldValue.increment(1));
        }else{
            uRef.document(email).update("posRating", FieldValue.increment(1));
        }
    }

    public void deleteSelf(){
            driverDBHelper.deleteUser(userInfo.getEmail());
            Toast.makeText(DriverProfileActivity.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
    }

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

