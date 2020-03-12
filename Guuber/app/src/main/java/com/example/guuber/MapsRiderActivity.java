package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapsRiderActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {

    private static int REQUEST_FINE_LOCATION_PERMISSION = 11;

    /**spinner codes**/
    private static final int MENU = 0;
    private static final int MYPROFILE = 1;
    private static final int VIEWTRIPS = 2;
    private static final int  WALLET = 3;
    private static final int  QR = 4;

    private GoogleMap guuberRiderMap;

    private Button makeRqButton, changeOriginButton, changeDestinationButton;
    private Spinner riderSpinner;

    private LatLng origin;
    private LatLng destination;

    private String coordsToChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider_maps);

        /**Obtain the SupportMapFragment and get notified when the map is ready to be used.**/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_map);
        mapFragment.getMapAsync(this);

        /**instructions for User to provide their destination
         * delayed to give time for map rendering**/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String toastStr = "Click on The Map to Start Building your Route!";
                Toast.makeText(MapsRiderActivity.this,toastStr,Toast.LENGTH_LONG).show();
            }
        },3000);


        /**initialize a spinner and set its adapter, strings are in 'values'**/
        /**CITATION: Youtube, Coding Demos, Android Drop Down List, Tutorial,
         * published on August 4,2016 Standard License, https://www.youtube.com/watch?v=urQp7KsQhW8 **/
        riderSpinner =  findViewById(R.id.rider_spinner); //set the rider spinner
        ArrayAdapter<String> RiderSpinnerAdapter = new ArrayAdapter<String>(MapsRiderActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.menuRider));
        RiderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riderSpinner.setAdapter(RiderSpinnerAdapter);


        /**display instructions to change origin**/
        changeOriginButton = findViewById(R.id.change_origin_button);
        changeOriginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Click on the Map to Set Your Pickup Location";
                Toast.makeText(MapsRiderActivity.this,message,Toast.LENGTH_LONG).show();
                setChangingCoordinate("origin");
            }
        });

        /**display instructions to change destination**/
        changeDestinationButton = findViewById(R.id.change_destination_button);
        changeDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Click on the Map to Set Your Drop-Off Location";
                Toast.makeText(MapsRiderActivity.this,message,Toast.LENGTH_LONG).show();
                setChangingCoordinate("destination");
            }
        });

        /**
         * onClickListener for the make a request button
         */
        makeRqButton = findViewById(R.id.make_request_button);
        makeRqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent riderProfileIntent = new Intent(MapsRiderActivity.this, makeRequestScreen1.class);
                /**send getDestination and get getOrigin through the intent**/
                startActivity(riderProfileIntent);
            }
        });

        /** onClickListener for calling methods based on the item in
         * the spinner drop down menu that is clicked**/
        riderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == MYPROFILE){
                    /**start the view trips activity**/
                    viewRiderProfile();
                    riderSpinner.setSelection(MENU);
                }else if (position == VIEWTRIPS) {
                    /**start the my profile activity*/
                    viewRiderTrips();
                    riderSpinner.setSelection(MENU);
                }else if (position == WALLET){
                    /**start the walleett activity**/
                    //spinner.setSelection(OPTIONS);
                }else if (position == QR){
                /**generate a QR code**/
                makeQR();
                riderSpinner.setSelection(MENU);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                riderSpinner.setSelection(MENU);
            }
        });

    }


     /** Manipulates the map once available.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.**/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        guuberRiderMap = googleMap;
        guuberRiderMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        guuberRiderMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark_mapstyle_json)));

        /**log the coords in console upon map click
         * this is giving the user a chance to set their destination**/
        guuberRiderMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0){
                android.util.Log.i("onMapClick", arg0.toString());
                if (getChangingCoordinate() == "origin"){
                    setMarker(arg0);
                    setOrigin(arg0);
                    originSetToast();
                }else if (getChangingCoordinate() == "destination") {
                    setMarker(arg0);
                    setDestination(arg0);
                    destinationSetToast();
                }
            }
        });


        if (checkUserPermission()) {
            /**if user permission have been checked
             * and location permission has been granted...**/
            guuberRiderMap.setMyLocationEnabled(true);
            guuberRiderMap.setOnMyLocationButtonClickListener(this);
            guuberRiderMap.setOnMyLocationClickListener(this);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

            if (location != null) {
                /**create a new LatLng location object for the user current location**/
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                /**move the camera to current location**/
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)
                        .zoom(10)
                        .build();
                guuberRiderMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                /**set a marker on users current location**/
                setMarker(currentLocation);

                /**set origin as currentLocation**/
                setOrigin(currentLocation);

                /**because use provided origin, assume they are ready to pick their destination*/
                setChangingCoordinate("destination");
            }
        } else {
            /**if user permission has been checked and
             * location services have been denied
             * set map to display Edmonton (for testing)*/
            guuberRiderMap.setMyLocationEnabled(false);
            LatLng UniversityOfAlberta = new LatLng( 53.5213 , -113.5213);

            /**move the camera to current location**/
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(UniversityOfAlberta)
                    .zoom(12)
                    .build();
            guuberRiderMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            /**because use provided origin, assume they are ready to pick their destination*/
            setChangingCoordinate("origin");
        }

    }

    /**set user origin as their currentlocation**/
    public void setOrigin(LatLng origin){
        this.origin = origin;
    }

    /**get user origin**/
    public LatLng getOrigin(){
        return origin;
    }

    /**let the user know they have chosen their origin**/
    public void originSetToast(){
        String message = "Origin has been changed!";
        Toast.makeText(MapsRiderActivity.this,message,Toast.LENGTH_LONG).show();
    }

    /**set user destination upon map click**/
    public void setDestination(LatLng destination){
        this.destination = destination;
    }

    /**get currently set user destination**/
    public LatLng getDestination(){
        return destination;
    }

    /**let the user know they have chosen their destination**/
    public void destinationSetToast(){
        String message = "Destination has been changed!\n Make Request?";
        Toast.makeText(MapsRiderActivity.this,message,Toast.LENGTH_LONG).show();

    }

    /**set a marker given LATLNG information**/
    public void setMarker(LatLng locationToMark){
        guuberRiderMap.addMarker(new MarkerOptions().position(locationToMark));
    }

    /**determine whether or not the user is changing their origin or destination
    ** coords to change will either be set to "origin" or "destination"**/
    public void setChangingCoordinate(String coordsToChange){
        this.coordsToChange = coordsToChange;
    }

    /**getter method to determine if onMapClick will be setting
     * origin or destination
     * @return "origin" or "destination"
     */
    public String getChangingCoordinate(){
        return coordsToChange;
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


    /**indicates current location button has been clicked... **/
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

    /**displays a screen containing trip history for rider**/
    public void viewRiderTrips() {
        final Intent riderTripsIntent = new Intent(MapsRiderActivity.this, ViewTripsActivity.class);
        startActivity(riderTripsIntent);
    }

    /**displays rider profile**/
    public void viewRiderProfile() {
        final Intent riderProfileIntent = new Intent(MapsRiderActivity.this, RiderProfileActivity.class);
        startActivity(riderProfileIntent);
    }

    /**calls an activity to generate a QR code**/
    public void makeQR(){
        final Intent qrProfileIntent = new Intent(MapsRiderActivity.this, QrActivity.class);
        startActivity(qrProfileIntent);
    }

}
