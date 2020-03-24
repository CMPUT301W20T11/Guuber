package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guuber.model.Rider;
import com.example.guuber.model.User;


/**
 * Code to display riders information on their profile
 */

public class RiderProfileActivity extends AppCompatActivity {

    Rider myself;
    String username;
    String email;
    String phoneNumber;
    TextView emailField;
    TextView usernameField;
    TextView phoneNumberField;
    Button deleteButton;
    ImageView likeButton;
    ImageView dislikeButton;
    ImageView profileImg;




    private static final String TAG = "RiderProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_disp);
        //UserData userData = UserData.getInstance();
        User userInfo = ((UserData)(getApplicationContext())).getUser();
        /**display the back button**/
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myself = new Rider("+263784446345", "musariri@ualberta.ca", "Tinashe",
                "Musariri");

        phoneNumberField = findViewById(R.id.phoneTextRdIn);
        usernameField = findViewById(R.id.usernameTextRdIn);
        emailField = findViewById(R.id.emailTextRdIn);
        likeButton = findViewById(R.id.likeButtonRdIn);
        dislikeButton = findViewById(R.id.dislikeButtonRdIn);
        profileImg = findViewById(R.id.imageViewRdIn);

//        phoneNumber = myself.getPhoneNumber();
//        username = myself.getFirstName();
//        email = myself.getEmail();
        // changed to access singleton
        phoneNumber = userInfo.getPhoneNumber();
        username = userInfo.getUsername();
        email = userInfo.getEmail();

        phoneNumberField.setText(phoneNumber);
        // for testing please disregard
        Log.d(TAG, "documentSnapshot.getString(\"phoneNumber\")" +" "+userInfo.getPhoneNumber());
        usernameField.setText(username);
        emailField.setText(email);
        likeButton.setImageResource(R.drawable.smile);
        dislikeButton.setImageResource(R.drawable.frowny);
        profileImg.setImageResource(R.drawable.profilepic);

        deleteButton = findViewById(R.id.deleteAccButtonRdIn);
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
}
