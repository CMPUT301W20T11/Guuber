package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        TripsHistoryList = findViewById(R.id.trips_history_list);


        triphistoryAdapter = new TripHistoryAdapter(this);
        TripsHistoryList.setAdapter(triphistoryAdapter);

    }
}
