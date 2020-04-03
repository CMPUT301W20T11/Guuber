package com.example.guuber;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guuber.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Code to display drivers information on their profile
 */

public class ViewProfileActivity extends AppCompatActivity {
    String username;
    String email;
    String phoneNumber;
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

    User user;

    // Instantiate the database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference uRef = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Get the view objects
        phoneNumberField = findViewById(R.id.phoneTextDrIn);
        usernameField = findViewById(R.id.usernameTextDrIn);
        emailField = findViewById(R.id.emailTextDrIn);
        likeButton = findViewById(R.id.likeButtonDrIn);
        dislikeButton = findViewById(R.id.dislikeButtonDrIn);
        profileImg = findViewById(R.id.imageViewDrIn);
        posRateDisplay = findViewById(R.id.posRateDr);
        negRateDisplay = findViewById(R.id.negRateDr);

        // Get the passed email info
        Intent intent = getIntent();
        email = intent.getExtras().getString("EMAIL");

        // On like button
        likeButton.setOnClickListener(v -> {
           rateUser(true);
            Toast.makeText(ViewProfileActivity.this, "Profile liked!", Toast.LENGTH_LONG).show();
        });

        // On dislike button
        dislikeButton.setOnClickListener(v -> {
            rateUser(false);
            Toast.makeText(ViewProfileActivity.this, "Profile NOT liked!", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Db query to get the user info with the intent email
        uRef.document(email).addSnapshotListener(this , (documentSnapshot, e) -> {
            user = documentSnapshot.toObject(User.class);

            // Get user attributes, no point in getting email since we already got it
            phoneNumber = user.getPhoneNumber();
            username = user.getUsername();
            posRate = user.getPosRating();
            negRate = user.getNegRating();

            // Set the texts
            phoneNumberField.setText(phoneNumber);
            usernameField.setText(username);
            emailField.setText(email);
            likeButton.setImageResource(R.drawable.smile);
            dislikeButton.setImageResource(R.drawable.frowny);
            profileImg.setImageResource(R.drawable.profilepic);
            negRateDisplay.setText(negRate.toString());
            posRateDisplay.setText(posRate.toString());

            // Set the profile image
            if(user.getRider() == 1){
                profileImg.setImageResource(R.drawable.riderprf);
            }else{
                profileImg.setImageResource(R.drawable.driverprf);
            }
        });

        // On like button
        likeButton.setOnClickListener(v -> {
           rateUser(true);
           Toast.makeText(ViewProfileActivity.this, "Profile liked!", Toast.LENGTH_LONG).show();
        });

        // On dislike button
        dislikeButton.setOnClickListener(v -> {
            rateUser(false);
            Toast.makeText(ViewProfileActivity.this, "Profile NOT liked!", Toast.LENGTH_LONG).show();
        });

    }

    // On back button pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Rate the user
    public void rateUser(Boolean rating){

        // Update the db object's rating
        if(!rating){
            uRef.document(email).update("negRating", FieldValue.increment(1));
        }else{
            uRef.document(email).update("posRating", FieldValue.increment(1));
        }
    }
}
