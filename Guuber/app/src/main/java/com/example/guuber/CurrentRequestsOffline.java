package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;


public class CurrentRequestsOffline extends AppCompatActivity {


    private TextView title, pickupDropoff, riderOrDriverEmail, costTip, status;
    Requests currentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_requests_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.current_requests_title);
        pickupDropoff = findViewById(R.id.pickup_and_dropoff);
        riderOrDriverEmail = findViewById(R.id.driver_rider_email);
        costTip = findViewById(R.id.cost_tip);
        status = findViewById(R.id.req_status);


        Intent here = getIntent();
        ArrayList<Parcelable> potentialRequest = here.getParcelableArrayListExtra("REQUEST_INFO");

        if (potentialRequest == null){
            title.setText("NO CURRENT REQUESTS");
        }else{
            String email = here.getStringExtra("EMAIL");
            currentRequest = (Requests) potentialRequest.get(0);
            display(currentRequest, email);
        }

    }

    /**
     * displays the details of the current request
     * @param currentRequest
     */
    private void display(Requests currentRequest, String email){
        title.setText("CURRENT REQUESTS");
        pickupDropoff.setText("pickup:" + currentRequest.getPickup().toString() + "       dropoff:" +  currentRequest.getDropoff().toString());
        riderOrDriverEmail.setText(email);
        costTip.setText("Trip Cost: $" + currentRequest.getCost() + "    Tip: $" + currentRequest.getTip());
        status.setText("Status: " + currentRequest.getStatus());
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
