package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class CurrentRequestsOffline extends AppCompatActivity {

    private TextView title, pickupDropoff, riderOrDriverEmail, costTip, status;

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
