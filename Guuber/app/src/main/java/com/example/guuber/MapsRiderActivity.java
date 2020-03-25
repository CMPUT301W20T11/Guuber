package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class contains the home screen for a Rider.
 *  The home screen includes a menu enabling navigation
 *  between activities related to the account
 *  as well as the google map fragment
 *  and other functionality for making a ride request.
 *  Class is representative of current application functionality
 */

public class MapsRiderActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {


    /**spinner codes**/
    private static final int MENU = 0;
    private static final int MYPROFILE = 1;
    private static final int VIEWTRIPS = 2;
    private static final int  WALLET = 3;
    private static final int  QR = 4;

    private GoogleMap guuberRiderMap;
    private Button changeOriginButton, changeDestinationButton;
    private Spinner riderSpinner;
    private LatLng origin;
    private LatLng destination;
    private String coordsToChange;

    /*******NEW MAPS INTEGRATION**/
    private boolean isLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 12;
    private static final String TAG = "MapsRiderActivity";
    private GeoApiContext geoRiderApiContext = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider_maps);

        /**Obtain the SupportMapFragment and get notified when the map is ready to be used.**/
        /*******************FORSURE NEEDS TO BE CHANGED***********************/
        if (!checkUserPermission()) {
            checkUserPermission();
        }


        if (geoRiderApiContext == null) {
            geoRiderApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.maps_key))
                    .build();
        }


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
        riderSpinner = findViewById(R.id.rider_spinner); //set the rider spinner
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
                    /**start the wallet activity**/
                    openRiderWallet();
                    riderSpinner.setSelection(MENU);
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_map);
        mapFragment.getMapAsync(this);

    }

    /**
     * WONT LET THE USER AVOID GIVING PERMISSIONS
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (isLocationPermissionGranted == false) {
                checkUserPermission();
            }
        }
    }

    /**********************************SPINNER METHODS*****************************************/

    /**
     * Starts activity containing trip history for rider
     **/
    public void viewRiderTrips() {
        final Intent riderTripsIntent = new Intent(MapsRiderActivity.this, ViewTripsActivity.class);
        startActivity(riderTripsIntent);
    }

    /**
     * Starts activity to display riders profile
     **/
    public void viewRiderProfile() {
        final Intent riderProfileIntent = new Intent(MapsRiderActivity.this, RiderProfileActivity.class);
        startActivity(riderProfileIntent);
    }

    /**
     * Starts activity to display riders wallet information
     **/
    public void openRiderWallet(){
        final Intent riderWalletIntent = new Intent(MapsRiderActivity.this, WalletActivity.class);
        startActivity(riderWalletIntent);
    }

    /**
     * Starts activity to allow rider to generate QR
     **/
    public void makeQR(){
        final Intent qrProfileIntent = new Intent(MapsRiderActivity.this, QrActivity.class);
        startActivity(qrProfileIntent);
    }

    /**********************************END SPINNER METHODS*****************************************/




     /**
     * Manipulates the map once available.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
      * @param googleMap fragment to display
      **/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        guuberRiderMap = googleMap;
        guuberRiderMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        guuberRiderMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark_mapstyle_json)));
        guuberRiderMap.setOnInfoWindowClickListener(MapsRiderActivity.this);

        /**
         * logs the coordinates in console upon map click
         * this is giving the user a chance to set their pickup
         * location and drop-off location
         * @params latitude on longitude retrieved from map click
         **/
        guuberRiderMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                //android.util.Log.i("onMapClick", arg0.toString());
                //android.util.Log.i("onMapClick", arg0.toString());
                if (getChangingCoordinate() == "origin") {
                    setMarker(arg0, "Origin");
                    setOrigin(arg0);
                    originSetToast();
                } else if (getChangingCoordinate() == "destination") {
                    setMarker(arg0, "Destination");
                    setDestination(arg0);
                    destinationSetToast();
                }
            }
        });


        if (checkUserPermission()) {
            /**
             * if user permission have been checked
             * and location permission has been granted...
             **/
            guuberRiderMap.setMyLocationEnabled(true);
            guuberRiderMap.setOnMyLocationButtonClickListener(this);
            guuberRiderMap.setOnMyLocationClickListener(this);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

            if (location != null) {
                /**create a new LatLng location object for the user current location**/
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                setOrigin(currentLocation);

                /**move the camera to current location**/
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)
                        .zoom(10)
                        .build();
                guuberRiderMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                /**because use provided origin, assume they are ready to pick their destination*/
                setChangingCoordinate("destination");
            }
        }
    }



    /**
     * set user origin as their currentlocation
     * @param origin is LatLng object  used to set pickup location
     **/
    public void setOrigin(LatLng origin){
        this.origin = origin;
    }


    /**
     * @returns rider origin (pickup location)
     **/
    public LatLng getOrigin(){
        return origin;
    }


    /**
     * let the user know they have chosen their origin
     **/
    public void originSetToast(){
        String message = "Origin has been changed!";
        Toast.makeText(MapsRiderActivity.this,message,Toast.LENGTH_LONG).show();
    }

    /**
     * set user destination upon map click
     * if origin has been set
     * @param destination is LatLng object used to set drop-off location
     **/
    public void setDestination(LatLng destination){
        this.destination = destination;
    }


    /**
     * returns user destination (drop-off location)
     **/
    public LatLng getDestination(){
        return destination;
    }

    /**
     * let the user know they have chosen their destination
     * and prompt them to make a request
     **/
    public void destinationSetToast(){
        String message = "Destination has been changed!\n Make Request?";
        Toast.makeText(MapsRiderActivity.this,message,Toast.LENGTH_LONG).show();
    }


    /**
     * determine whether or not the user is changing their origin or destination
     * @param coordsToChange will either be set to "origin" or "destination"
     *  detemined by user click on "set destination" button
     *  or "set origin" button
     **/
    public void setChangingCoordinate(String coordsToChange){
        this.coordsToChange = coordsToChange;
    }

    /**
     * getter method to determine if onMapClick will be setting
     * origin or destination
     * @return "origin" or "destination"
     */
    public String getChangingCoordinate(){
        return coordsToChange;
    }



    /**
     * set a marker given LATLNG information
     * @param locationToMark is location to set marker on
     **/
    public void setMarker(LatLng locationToMark, String title){
        guuberRiderMap.addMarker(new MarkerOptions()
                .position(locationToMark)
                .flat(false)
                .title(title)
                );
    }



    /**
     * indicates current location button has been clicked...
     * @return false all other times besides onMyLocationButtonClick event
     **/
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "clicked on current location", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * displays the details of your location upon click
     * @param mylocation is a Location object representing your devices
     *  real time location
     **/
    @Override
    public void onMyLocationClick(@NonNull Location mylocation) {
        Toast.makeText(this, "Current location:\n" + mylocation, Toast.LENGTH_LONG).show();
    }


    /**
     * CHECKS IF LOCATION SERVICES HAVE BEEN ENABLED
     * @return true if they have, false if they haven't
     */
    private boolean checkMapServices() {
        if (isMapsEnabled()) {
            return true;
        }
        return false;
    }


    /**
     * MAKING SURE GPS IS ENABLED ON THE DEVICE
     **/
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }


    /**
     * OPENS UP SETTINGS FOR THEM TO TURN ON GPS
     * IF IT IN NOT ALREADY ON
     **/
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    /**
     * iF USER PERMISSION HAS ALREADY BEEN ACCEPTED IT WILL NOT PROMPT THE USER
     * AND SET BOOLEAN  TO TRUE
     * ELSE IT WILL RETURN FALSE
     * @return true if user has granted permission, false if user has not
     */
    private boolean checkUserPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }



    /**
     * SETTING A BOOLEAN TO TRUE IF
     * ON ACTIVITY REQUEST PERMISSION FINISH
     * USER HAS ENABLED LOCATION SERVICES
     * RUNS RIGHT AFTER PERMISSION RESULT
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionGranted = true;
                }
            }
        }
    }


    /**
     * GET THE RESULT OF THE REQUEST PERMISSION EVENT
     * KEEP CHECKING PERMISSION UNTIL GRANTED
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (isLocationPermissionGranted == false) {
                    checkUserPermission();

                }
            }
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsRiderActivity.this);
        builder
                .setMessage("What would you like to do?")
                .setCancelable(true)
                .setPositiveButton("Display Route Details ", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        calculateDirections();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Make a request", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeRequest(marker);
                            }
                        }
                )
                .setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * parse the coordinates of the origin and the destination
     * destination is necessarily the marker that was clicked on
     * convert them to strings and send to Db
     * @param marker
     */
    public void makeRequest(Marker marker){
        Double originLatitude = getOrigin().latitude;
        Double originLongitude = getOrigin().longitude;
        Double destinationLatitude = marker.getPosition().latitude;
        Double destinationLongitude = marker.getPosition().longitude;
        android.util.Log.i(TAG, "origin latitude = "+ originLatitude.toString());
        android.util.Log.i(TAG, "origin longitude = "+ originLongitude.toString());
        android.util.Log.i(TAG, "destination latitude = "+ destinationLatitude.toString());
        android.util.Log.i(TAG, "destination longitude = "+destinationLongitude.toString());

    }


    private void calculateDirections() {
        Log.d(TAG, "calculateDirections: calculating directions.");

        /**from riders set destination**/
        LatLng riderDestination = getDestination();
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                riderDestination.latitude,
                riderDestination.longitude
        );
        DirectionsApiRequest riderDirections = new DirectionsApiRequest(geoRiderApiContext);

        /**from the riders set Origin**/
        LatLng currRiderLocation = getOrigin();
        riderDirections.origin(
                new com.google.maps.model.LatLng(
                        currRiderLocation.latitude,
                        currRiderLocation.longitude
                )
        );

        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        riderDirections.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                /**Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                Log.d(TAG, "onResult: successfully retrieved directions.");**/
                addPolylinesToMap(result);
            }
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
            }
        });
    }

    /**
     * add polyline to map based on the geo coords from the calculated route
     * @param result is the route determined by calculate directions
     **/
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for(DirectionsRoute route: result.routes){
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = guuberRiderMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(MapsRiderActivity.this, R.color.polyLinesColors));
                    polyline.setClickable(true);
                }
            }
        });
    }


}
