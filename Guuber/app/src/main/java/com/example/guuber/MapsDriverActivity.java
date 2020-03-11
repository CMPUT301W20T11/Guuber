package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;


public class MapsDriverActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {

    private static int REQUEST_FINE_LOCATION_PERMISSION = 11;
    private static final int MENU = 0;
    private static final int VIEWTRIPS = 1;
    private static final int MYPROFILE = 2;
    private static final int  WALLET = 3;

    private GoogleMap guuberDriverMap;
    Spinner driverSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_maps);
        driverSpinner =  findViewById(R.id.driver_spinner); //set the driver spinner

        /**
         * for Tinashe testing view profile activities
         * delete late upto clear marked commet
         */

        //start here
        Button butn = findViewById(R.id.make_request_button);
        butn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsDriverActivity.this, RiderProfileActivity.class);
                //final Intent viewTripsIntent = new Intent(MapsDriverActivity.this, ViewTripsActivity.class);
                startActivity(intent);
            }
        });
        //DELETE UPTO HERE TINASHE TEST

        /**Obtain the SupportMapFragment and get notified when the map is ready to be used.**/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        mapFragment.getMapAsync(this);


        /**initialize a spinner and set its adapter, strings are in 'values'**/
        /**CITATION: Youtube, Coding Demos, Android Drop Down List, Tutorial,
         * published on August 4,2016 Standard License, https://www.youtube.com/watch?v=urQp7KsQhW8 **/
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MapsDriverActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.menu));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(spinnerAdapter);


        /**calling methods based on the item in the spinner drop down menu that is clicked**/
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == MYPROFILE){
                    /**start the view trips activity**/
                    viewTrips();
                    driverSpinner.setSelection(MENU);
                }else if (position == VIEWTRIPS) {
                    /**start the my profile activity*/
                    Vehicle vehicle = new Vehicle("Toyota", "RunX", "Gold", "AEJ 0430");

                    Driver myself = new Driver("+15879388900", "osiemusariri@gmail.com", "Oswell",
                            "Musariri", vehicle);
                    myself.displayProfile(MapsDriverActivity.this);
                    //spinner.setSelection(MENU);
                }else if (position == WALLET){
                    /**start the walleett activity**/
                    //spinner.setSelection(OPTIONS);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                driverSpinner.setSelection(MENU);
            }
        });

    }


    /**Manipulates the map once available.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.**/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        guuberDriverMap = googleMap;
        guuberDriverMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        guuberDriverMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark_mapstyle_json)));

        /**log the coords in console upon map click**/
        guuberDriverMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng arg0){
                android.util.Log.i("onMapClick", arg0.toString());
            }
        });


        if (checkUserPermission()) {
            /**if user permission have been checked
             * and location permission has been granted...**/
            guuberDriverMap.setMyLocationEnabled(true);
            guuberDriverMap.setOnMyLocationButtonClickListener(this);
            guuberDriverMap.setOnMyLocationClickListener(this);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

            if (location != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(12)
                        .build();
                guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } else {
            /**display a fragment that tells user their
             * location permission is required*/
            guuberDriverMap.setMyLocationEnabled(false);
            new EnableLocationServices().show(getSupportFragmentManager(), "ENABLE_LOCATION");
        }

    }

    /**check user permissions**/
    public boolean checkUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            /**this dialog box appears only if the user has previously denied the request and has NOT selected don't ask again**/
            if  (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                /**start activity disabling app usage until user has granted location permissions**/
            }else{
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
            }
            return false;
        } else {
            /**user has already set location permission preferences**/
            return true;
        }
    }


    /**indicates current location button has been clicked... do we need?**/
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "clicked on current location", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**displays the details of your location upon click**/
    @Override
    public void onMyLocationClick(@NonNull Location mylocation) {
        Toast.makeText(this, "Current location:\n" + mylocation, Toast.LENGTH_LONG).show();
    }

    /**launches the view trips history activity**/
    public void viewTrips(){
        final Intent viewTripsIntent = new Intent(MapsDriverActivity.this, ViewTripsActivity.class);
        startActivity(viewTripsIntent);
    }

}
