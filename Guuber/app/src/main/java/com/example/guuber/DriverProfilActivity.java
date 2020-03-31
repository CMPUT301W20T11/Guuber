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
import com.example.guuber.model.Vehicle;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Code to display drivers information on their profile
 */

public class DriverProfilActivity extends AppCompatActivity {
    String username;
    String email;
    String phoneNumber;
    String carReg;
    Integer posRate;
    Integer negRate;

    TextView emailField;
    TextView usernameField;
    TextView phoneNumberField;
    TextView posRateDisplay;
    TextView negRateDisplay;
    ImageView likeButton;
    ImageView dislikeButton;
    ImageView profileImg;

    Button deleteButton;
    User userInfo;
    Boolean editable;

    /***********the database******/
    private FirebaseFirestore driverMapsDB = FirebaseFirestore.getInstance();
    private GuuDbHelper driverDBHelper = new GuuDbHelper(driverMapsDB);
    private CollectionReference uRef = driverMapsDB.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_profile_disp);
        //UserData userData = UserData.getInstance();
        String caller = getIntent().getStringExtra("caller");
        editable = caller.equals("internal");
        /**display the back button**/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(DriverProfilActivity.this, "Click and hold the information you would like to edit !",Toast.LENGTH_LONG);



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
                    if (!editable){userInfo.adjustRating(true);
                        Toast.makeText(DriverProfilActivity.this, "Profile liked!", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(DriverProfilActivity.this, "Profile NOT liked!", Toast.LENGTH_LONG).show();
                        rateUser(false);
                        //updateDatabase();
                        dislikeButton.setClickable(false);
                    }
                }
            });
            //onClickListeners for email and phone number fields to contact User

            emailField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"+ email));
                    startActivity(intent);
                }
            });

            phoneNumberField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+phoneNumber));
                    startActivity(intent);
                }
            });

        } //finish uneditable else

        deleteButton = findViewById(R.id.deleteAccButtonDrIn);
        if (!editable){deleteButton.setVisibility(View.INVISIBLE);}
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editable){
                    DeleteAccountFragment deleteFragment = new DeleteAccountFragment();
                    Bundle callingActivity = new Bundle();
                    callingActivity.putString("callingActivity", "driver");
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
            Toast.makeText(DriverProfilActivity.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
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
        //negRateDisplay.setText(negRate.toString()+"%");
        //posRateDisplay.setText(posRate.toString()+"%");
        negRateDisplay.setText(negRate.toString());
        posRateDisplay.setText(posRate.toString());
    }

}

