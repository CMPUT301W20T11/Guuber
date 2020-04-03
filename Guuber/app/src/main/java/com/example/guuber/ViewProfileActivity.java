package com.example.guuber;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guuber.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Code to display drivers information on their profile
 */

public class ViewProfileActivity extends AppCompatActivity {
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

    private User user;

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
}
