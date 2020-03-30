package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
//import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.guuber.model.GuuDbHelper;
import com.example.guuber.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class contains the home screen for a Driver.
 *  The home screen includes a menu enabling navigation
 *  between activities related to the account
 *  as well as the google map fragment
 *  and other related functionality for browsing ride requests.
 *  Class is representative of current application functionality
 */
public class MapsDriverActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {

    /*** spinner codes**/
    private static final int MENU = 0;
    private static final int VIEWTRIPS = 1;
    private static final int MYPROFILE = 2;
    private static final int WALLET = 3;
    private static final int SCANQR = 4;
    private static final int SIGNOUT = 5;

    // for signing out of app
    private static final int RC_SIGN_OUT = 1000;

    private GoogleMap guuberDriverMap;
    private Spinner driverSpinner;
    private Button driverSearchButton;
    private EditText geoLocationSearch;
    private LatLng search;
    private LatLng driverLocation;
    private boolean offerSent;
    private boolean offerAccepted;
    private boolean routeInProgress;


    /*******NEW MAPS INTEGRATION**/
    private boolean isLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 12;
    private static final String TAG = "MapsDriverActivity";
    private GeoApiContext geoApiContext = null;
    private Polyline polyline;
    /*********************/

    // Activity result codes
    private static final int QR_SCAN_CODE = 4;

    /***********the database******/
    private FirebaseFirestore driverMapsDB = FirebaseFirestore.getInstance();
    private GuuDbHelper driverDBHelper = new GuuDbHelper(driverMapsDB);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_driver_maps);
        driverSpinner = findViewById(R.id.driver_spinner);
        geoLocationSearch = findViewById(R.id.geo_location_EditText);


        /**instructions for User to provide their destination
         * delayed to give time for map rendering**/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String toastStr = "Click on the Map and double click Search to Browse Open Requests in That Area!";
                Toast.makeText(MapsDriverActivity.this, toastStr, Toast.LENGTH_LONG).show();
            }
        }, 3000);


        /**when driver clicks search button,
         * zoom in on area to view open requests*/
        driverSearchButton = findViewById(R.id.driver_search_button);
        driverSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offerSent == false || routeInProgress == false) {
                    if (getSearch() != null) {
                        LatLng parse = getSearch();
                        drawOpenRequests(); //doest draw on first call

                        /**move the camera to searching location**/
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(parse).zoom(5).build();
                        guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                        invalidSearchToast();
                    }
                } else {
                    youveSentAnOfferToast();
                }
            }
        });

        /**initialize a spinner and set its adapter, strings are in 'values'**/
        /**CITATION: Youtube, Coding Demos, Android Drop Down List, Tutorial,
         * published on August 4,2016 Standard License, https://www.youtube.com/watch?v=urQp7KsQhW8 **/
        ArrayAdapter<String> driverSpinnerAdapter = new ArrayAdapter<String>(MapsDriverActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menuDriver));
        driverSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(driverSpinnerAdapter);

        /**calling methods based on the item in the spinner drop down menu that is clicked**/
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == MYPROFILE) {
                    /**start the view trips activity**/
                    viewDriverTrips();
                    driverSpinner.setSelection(MENU);
                } else if (position == VIEWTRIPS) {
                    /**start the my profile activity*/
                    viewDriverProfile();
                    driverSpinner.setSelection(MENU);
                } else if (position == WALLET) {
                    /**start the wallet activity**/
                    openDriverWallet();
                    driverSpinner.setSelection(MENU);
                } else if (position == SCANQR) {
                    /**start the scanQR activity**/
                    scanQR();
                    driverSpinner.setSelection(MENU);
                }else if (position == SIGNOUT) {
                    /**start the scanQR activity**/
                    signOut();
                    driverSpinner.setSelection(MENU);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                driverSpinner.setSelection(MENU);
            }
        });

        /**Obtain the SupportMapFragment and get notified when the map is ready to be used.**/
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.maps_key)).build();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_map);
        mapFragment.getMapAsync(this);

    }

    /**
     * LITERALLY WONT LET THE USER AVOID GIVING PERMISSIONS
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

    /****************************************SPINNER METHODS***********************************************/

    /**
     * Starts activity containing trip history for driver
     **/
    public void viewDriverTrips() {
        final Intent driverTripsIntent = new Intent(MapsDriverActivity.this, ViewTripsActivity.class);
        startActivity(driverTripsIntent);
    }

    /**
     * Starts activity to display drivers profile
     **/
    public void viewDriverProfile() {
        Intent driverProfileIntent = new Intent(MapsDriverActivity.this, DriverProfilActivity.class);
        driverProfileIntent.putExtra("caller", "internal");
        startActivity(driverProfileIntent);
    }

    public void viewRiderProfile(User user) {
        Intent riderProfileIntent = new Intent(MapsDriverActivity.this, RiderProfileActivity.class);
        //to be deleted, need to initialize all users properly
        user.setNegRating(0);
        user.setPosRating(0);
        //delete only up to here
        riderProfileIntent.putExtra("caller", "external");
        riderProfileIntent.putExtra("riderProfile", user);
        startActivity(riderProfileIntent);
    }

    /**
     * Starts activity to display riders wallet information
     **/
    public void openDriverWallet() {
        final Intent driverWalletIntent = new Intent(MapsDriverActivity.this, WalletActivity.class);
        startActivity(driverWalletIntent);
    }

    /**
     * Starts activity to allow rider to generate QR
     **/
    public void scanQR() {
        final Intent scanQrProfileIntent = new Intent(MapsDriverActivity.this, scanQrActivity.class);
        startActivityForResult(scanQrProfileIntent, QR_SCAN_CODE);
    }
    /**
     * Sign out a user and return to the login activity
     **/
    public void signOut() {
        Intent signOutConfirm  = new Intent(MapsDriverActivity.this, LoginActivity.class);
        signOutConfirm.putExtra("SignOut", "TRUE");
        setResult(RC_SIGN_OUT, signOutConfirm);
        finish();
    }

    /****************************************END SPINNER METHODS***********************************************/

    /**
     * Manipulates the map once available.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        guuberDriverMap = googleMap;
        guuberDriverMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        guuberDriverMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark_mapstyle_json)));
        guuberDriverMap.setOnInfoWindowClickListener(MapsDriverActivity.this);

        /**
         * logs the coordinates in console upon map click
         * this is giving the driver a chance to browse
         * a specified area for open requests
         * @params latitude on longitude retrieved from map click
         **/
        guuberDriverMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                if (routeInProgress == true || offerSent == true) {
                    youveSentAnOfferToast(); //clicking on the map wont do anything when youre on a route or have sent an offer
                } else {
                    geoLocationSearch.setText(arg0.toString()); //set the search bar to the coordinates clicked
                    setSearch(arg0);
                }
            }
        });


        if (checkUserPermission()) {
            /**
             * if user permission have been checked
             * and location permission has been granted...
             **/
            guuberDriverMap.setMyLocationEnabled(true);
            guuberDriverMap.setOnMyLocationButtonClickListener(this);
            guuberDriverMap.setOnMyLocationClickListener(this);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            assert locationManager != null;
            Location location = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager.getBestProvider(criteria, true)));


            if (location != null) {
                /**create a new LatLng location object for the user current location**/
                LatLng currLocation = new LatLng(location.getLatitude(), location.getLongitude());
                setDriverLocation(currLocation); //driver must provide their location

                /**move the camera to current location**/
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currLocation).zoom(10).build();
                guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }else{
                location = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager.getBestProvider(criteria, true)));
                android.util.Log.i("DRIVER LOCATION = ", null);
            }
        }



    }

    /**
     * render markers on the map based on open requests
     * containing user the requester's profile and
     * switch case to pull values from the request object
     */
    public void drawOpenRequests() {
        Double offeredTip = null, destinationLat = null, destinationLong = null, originLat = null, originLong = null, tripCost = null;
        String email = null;
        ArrayList<Map<String, Object>> openRequestList = driverDBHelper.getReqList(); //needs to be called twice to draw open requests. fine fore now
        android.util.Log.i(TAG, "OPEN REQUEST LIST RAW" + openRequestList.toString());

        for (Map<String, Object> map : openRequestList) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {

                String key = entry.getKey();
                Object value = entry.getValue();

                switch (key) {
                    case "tripCost":
                        tripCost = Double.parseDouble(value.toString());
                        break;
                    case "reqTip":
                        offeredTip = Double.parseDouble(value.toString());
                        break;
                    case "desLat":
                        destinationLat = Double.parseDouble(value.toString());
                        break;
                    case "oriLat":
                        originLat = Double.parseDouble(value.toString());
                        break;
                    case "desLng":
                        destinationLong = Double.parseDouble(value.toString());
                        break;
                    case "oriLng":
                        originLong = Double.parseDouble(value.toString());
                        break;
                    case "email":
                        email = value.toString();
                        break;
                }
            }
            draw(originLat, originLong, destinationLat, destinationLong, email, tripCost, offeredTip);
        }

    }

    /**
     * draw the open requests on the map
     * @param originLat riders origin
     * @param originLong riders origin
     * @param destinationLat riders destination
     * @param destinationLong riders destination
     * @param email riders email
     */
    public void draw(Double originLat, Double originLong, Double destinationLat, Double destinationLong, String email, Double tripCost, Double offeredTip) {
        if (originLat == null || originLong == null || email == null || destinationLat == null || destinationLong == null || tripCost == null || offeredTip == null) {
            noOpenRequestToast();
        } else {
            LatLng requestStart = new LatLng(originLat, originLong);
            setOpenRequestMarkers(requestStart, email, tripCost, offeredTip);
            LatLng requestEnd = new LatLng(destinationLat, destinationLong);
            setOpenRequestMarkers(requestEnd, email, tripCost, offeredTip);

            calculateDirectionsBetweenPickupandDropOff(email, requestStart, requestEnd);
        }
    }

    /**
     * letting driver know there are no open requests
     */
    private void noOpenRequestToast() {
        Toast.makeText(MapsDriverActivity.this, "No Open Requests! Try clicking search again", Toast.LENGTH_LONG).show();
    }

    /**
     * letting driver know they have already made an offer
     * they must be denied or be accepted and complete the trip before sending another offer
     */
    private void youveSentAnOfferToast() {
        Toast.makeText(MapsDriverActivity.this, "Please wait for Rider response before making another offer", Toast.LENGTH_LONG).show();
    }

    public void setMarker(LatLng locationToMark, String title){
        guuberDriverMap.addMarker(new MarkerOptions()
                .position(locationToMark).flat(false).title(title)
        );
    }

    /**
     * set a marker given LATLNG information
     * @param locationToMark is location to set marker on
     **/
    public void setOpenRequestMarkers(LatLng locationToMark, String title, Double TripCost, Double offeredTip) {
        guuberDriverMap.addMarker(new MarkerOptions()
                .position(locationToMark).flat(false).title(title).snippet("Trip Cost: $" + TripCost + " Tip Offered: $" + offeredTip)
        );
    }

    /**
     * set the driver location
     * (is necessarily set upon map render
     * @param location is the drivers current location
     **/
    public synchronized void setDriverLocation(LatLng location) {
        //making sure we   have a driver location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if(location == null){
            if (!checkUserPermission()){
                checkUserPermission();
            }else {
                Location getLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
                LatLng driverLocation = new LatLng(getLocation.getLatitude(), getLocation.getLongitude());
                this.driverLocation = driverLocation;
            }
        }else{
            this.driverLocation = location;
        }

    }

    /**
     * @return driver current location
     */
    public LatLng getDriverLocation() {
        return driverLocation;
    }


    /**
     * get the LatLng object from
     * where the driver has set their search to
     * @return LatLng object to navigate to
     */
    public LatLng getSearch() {
        return search;
    }


    /**
     * ser the LatLng object from
     * where the driver has set their search to
     * @param search object to navigate to
     */
    public void setSearch(LatLng search) {
        this.search = search;
    }


    /**
     * inform the driver they have searched an invalid location
     **/
    public void invalidSearchToast() {
        String toastStr = "Invalid Search! Click on the Map and press Search to Browse Open Requests in That Area";
        Toast.makeText(MapsDriverActivity.this, toastStr, Toast.LENGTH_LONG).show();
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
     * real time location
     **/
    @Override
    public void onMyLocationClick(@NonNull Location mylocation) {
        Toast.makeText(this, "Current location:\n" + mylocation, Toast.LENGTH_LONG).show();
    }


    /**
     * CHECKS IF LOCATION SERVICES HAVE BEEN ENABLED
     * @return true if they have, false if they havent
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
     *
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
            case QR_SCAN_CODE: {
                // TODO for LEAH
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Transaction processed", Toast.LENGTH_SHORT).show();
                    offerSent  = false;
                    routeInProgress = false;
                    offerAccepted = false;
                    guuberDriverMap.clear();
                    drawOpenRequests();
                } else {
                    Toast.makeText(this, "Transaction failed. Try Paying your Driver Again", Toast.LENGTH_SHORT).show();
                    routeInProgress = true;
                }
            }
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        calculateDirectionsToPickup(marker); //draw drivers route to pickup
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsDriverActivity.this);

        if(offerSent == false && offerAccepted == false) {
            builder
                    .setMessage("What would you like to do?").setCancelable(false)
                    .setPositiveButton("View This Riders Profile", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /**********TINASHE********/
                            //view the rider of this requests profile
                            //marker.getTitle() is equal to email
                            try {
                                User user = driverDBHelper.getUser(marker.getTitle());
                                viewRiderProfile(user);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            guuberDriverMap.clear();
                            drawOpenRequests();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Offer Them a Ride", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            offerSent = true;
                            try {
                                offerRide(marker); //first crash
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }else if (offerSent == true && offerAccepted == false){
            builder
                    .setMessage("Offer Has Been Sent")
                    .setPositiveButton("Check Status", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User currdriver = ((UserData)(getApplicationContext())).getUser();
                            String statusCheck = null; //second crash
                            try {
                                statusCheck = driverDBHelper.checkOfferStatus(currdriver);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (statusCheck != null){
                                android.util.Log.i(TAG, "STATUS CHECK WAS NOT NULL");
                                android.util.Log.i(TAG, statusCheck);
                                if (statusCheck.equals("accepted")){
                                    offerAccepted = true;
                                    routeInProgress = true;
                                    try {
                                        offerAccepted(marker.getTitle(), marker);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }else if (statusCheck.equals("declined")){
                                    offerAccepted = false;
                                    offerDeclined();
                                    dialog.dismiss();
                                }else if (statusCheck.equals("pending") || statusCheck.equals("none")){
                                    stillPendingToast();
                                    dialog.dismiss();
                                }
                            }else{
                                android.util.Log.i(TAG, "STATUS CHECK WAS NULL");
                            }
                        }
                    });
            final AlertDialog alert = builder.create();alert.show();
        }else if (routeInProgress == true){
            builder
                    .setMessage("Route In Progress")
                    .setPositiveButton("Let The Rider Know You have Arrived", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.util.Log.i(TAG, "DRIVER HAS ARRIVED"); //TO DO AFTER CRASHES ARE FIXED
                        }
                    });
            final AlertDialog alert = builder.create();alert.show();
        }
    }


    /**
     * Offer a ride to the Rider and be accepted or denied
     */
    public void offerRide(Marker marker) throws InterruptedException {
        calculateDirectionsToPickup(marker);

        offerSent = true;
        guuberDriverMap.clear();
        LatLng currReqMarker = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        String currReqRiderEmail = marker.getTitle();
        setMarker(currReqMarker,currReqRiderEmail);


        User currDriver = ((UserData)(getApplicationContext())).getUser();
        android.util.Log.i("CURR DRIVER EMAIL",  currDriver.getEmail()); //works fine


        User riderToOfferTo = driverDBHelper.getUser(currReqRiderEmail); //<------ FIRST crash. even though rider email is correct
        android.util.Log.i("Rider to Offer To: ",  riderToOfferTo.toString());

        String theRidersEmail = marker.getTitle();
        riderToOfferTo.setEmail(theRidersEmail); //<-----one thing that is fixing the crash
        android.util.Log.i("The Riders Email", riderToOfferTo.getEmail());


        driverDBHelper.offerRide(currDriver,riderToOfferTo);
        android.util.Log.i("Rider to Offer To: ",  "ride offered");

    }

    /**
     * Crash on first try?
     * re draws the current route as green to indicate its in process
     * lets the driver know the route is in progress
     * @param riderEmail
     * @param marker
     */
    private void offerAccepted(String riderEmail, Marker marker) throws InterruptedException {
        routeInProgress = true;
        guuberDriverMap.clear();

        LatLng pickupPoint = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        setMarker(pickupPoint,riderEmail);
        calculateDirectionsToPickup(marker);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String toastStr = "Your Offer Was Accepted. Click on the Riders Pickup Location to Let them know when you have arrived";
                Toast.makeText(MapsDriverActivity.this, toastStr, Toast.LENGTH_LONG).show();
            }
        }, 500);


        User currUser = ((UserData)(getApplicationContext())).getUser();
        User riderForRoute = driverDBHelper.getUser(riderEmail); //crash three
        try {
            driverDBHelper.reqAccepted(riderForRoute, currUser); //crash four
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * toast to let the driver know their offer was declined
     */
    private void offerDeclined(){
        Toast.makeText(MapsDriverActivity.this, "Your Offer Was Declined. Press Search to Continue Browsing Requests", Toast.LENGTH_SHORT).show();
        guuberDriverMap.clear();
        drawOpenRequests();
    }

    /**
     * let the driver know that the rider has yet to see or make a decision
     */
    private void stillPendingToast(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String toastStr = "Your Offer is Still Pending!";
                Toast.makeText(MapsDriverActivity.this, toastStr, Toast.LENGTH_LONG).show();
            }
        }, 500);
    }


    /**
     * SIXTH CRASH
     */
    private void calculateDirectionsBetweenPickupandDropOff(String email, LatLng pickup, LatLng dropOff) {

        android.util.Log.i("USERS EMAIL", email);
        android.util.Log.i("USERS PICKUP", pickup.toString());
        android.util.Log.i("USERS DROPOFF", dropOff.toString());


        /**from riders set destination**/
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                dropOff.latitude, dropOff.longitude
        );
        DirectionsApiRequest driverDirections = new DirectionsApiRequest(geoApiContext);

        /**from the riders set Origin**/
        driverDirections.origin(
                new com.google.maps.model.LatLng(
                        pickup.latitude, pickup.longitude
                )
        );

        driverDirections.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                addPolylinesToMap(result);
            }
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
            }
        });
    }




    /**
     * directions from the drivers location to the rider pickup
     * method is good.
     * @param marker the marker indicating the riders pickup location
     */
    private void calculateDirectionsToPickup(Marker marker) {

        Log.d(TAG, "calculateDirections: calculating directions.");

        /**to the clicked markers destination**/
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest driverDirections = new DirectionsApiRequest(geoApiContext);

        /**from the drivers current position**/
        LatLng currDriverLocation = getDriverLocation();
        driverDirections.origin(
                new com.google.maps.model.LatLng(
                        currDriverLocation.latitude,
                        currDriverLocation.longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        driverDirections.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
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

                    polyline = guuberDriverMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    if (offerAccepted == false && offerSent == false){
                        polyline.setColor(ContextCompat.getColor(MapsDriverActivity.this, R.color.drawPolyLinesColors));
                    } else if (routeInProgress == true) {
                        polyline.setColor(ContextCompat.getColor(MapsDriverActivity.this, R.color.TripInProgressPolyLinesColors));
                    } else if (offerSent == true) {
                        polyline.setColor(ContextCompat.getColor(MapsDriverActivity.this, R.color.TripIsPendingPolyLinesColors));
                    }

                }
            }
        });
    }

}




