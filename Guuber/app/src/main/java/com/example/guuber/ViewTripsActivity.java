package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ViewTripsActivity extends AppCompatActivity {

    ListView TripsHistoryList;
    TripHistoryAdapter triphistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_activity);

        /**display the back button**/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TripsHistoryList = findViewById(R.id.trips_history_list);
        triphistoryAdapter = new TripHistoryAdapter(this);
        TripsHistoryList.setAdapter(triphistoryAdapter);


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
