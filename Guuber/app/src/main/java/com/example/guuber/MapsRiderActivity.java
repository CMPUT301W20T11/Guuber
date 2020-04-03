package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ui.AppBarConfiguration;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.guuber.model.GuuDbHelper;
import com.example.guuber.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import java.util.List;
import java.util.Objects;


/**
 * This class contains the home screen for a Rider. The home screen includes a menu enabling navigation
 *  between activities related to the account as well as the google map fragment
 *  and other functionality for making a ride request.Class is representative of current application functionality
 */

public class MapsRiderActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener{


    //spinner codes
    private static final int MENU = 0;
    private static final int MYPROFILE = 1;
    private static final int  WALLET = 2;
    private static final int  QR = 3;
    private static final int SIGNOUT = 4;

    //permissions / results codes
    private static final int QR_REQ_CODE = 3;
    private static final int RC_SIGN_OUT = 1000;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 12;

    //booleans for offer status
    private boolean rideisPending = false;
    private boolean rideInProgress = false;
    private boolean paymentFailed = false;

    //location
    private boolean  isLocationPermissionGranted = false;
    private GoogleMap guuberRiderMap;
    private Spinner riderSpinner;
    private LatLng origin, destination;
    private String coordsToChange;
    private String potentialOfferer = null;  //holds drivers email. very important
    private Double tripCost, tip;
    private Polyline polyline;

    //maps / globals
    private static final String TAG = "MapsRiderActivity";
    private GeoApiContext geoRiderApiContext = null;
    LocationManager locationManager;
    Criteria criteria = new Criteria();



    /***********the database******/
    private FirebaseFirestore riderMapsDB = FirebaseFirestore.getInstance();
    private GuuDbHelper riderDBHelper = new GuuDbHelper(riderMapsDB);
    private CollectionReference uRefRequests = riderMapsDB.collection("requests");
    private CollectionReference uRefUsers = riderMapsDB.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_maps);


        //instructions for User to provide their destinatiom delayed to give time for map rendering
        new Handler().postDelayed(() -> Toast.makeText(MapsRiderActivity.this,"Click on The Map to Start Building your Route!",Toast.LENGTH_LONG).show(),3000);


        //initialize a spinner and set its adapter, strings are in 'values'
        // CITATION: Youtube, Coding Demos, Android Drop Down List, Tutorial, published on August 4,2016 Standard License, https://www.youtube.com/watch?v=urQp7KsQhW8
        riderSpinner = findViewById(R.id.rider_spinner); //set the rider spinner
        ArrayAdapter<String> RiderSpinnerAdapter = new ArrayAdapter<>(MapsRiderActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.menuRider));
        RiderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riderSpinner.setAdapter(RiderSpinnerAdapter);


        //display instructions to change origin
        Button changeOriginButton = findViewById(R.id.change_origin_button);
        changeOriginButton.setOnClickListener(v -> {
            Toast.makeText(MapsRiderActivity.this,"Click on the Map to Set Your Pickup Location",Toast.LENGTH_LONG).show();
            setChangingCoordinate("Origin");
        });

        //display instructions to change destination
        Button changeDestinationButton = findViewById(R.id.change_destination_button);
        changeDestinationButton.setOnClickListener(v -> {
            Toast.makeText(MapsRiderActivity.this,"Click on the Map to Set Your Drop-Off Location",Toast.LENGTH_LONG).show();
            setChangingCoordinate("Destination");
        });


        //onClickListener for calling methods based on the item in the spinner drop down menu that is clicked
        riderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == MYPROFILE){
                    viewRiderProfile();
                }else if (position == WALLET){
                    openRiderWallet();
                }else if (position == QR){
                    makeQR();
                } else if (position == SIGNOUT) {
                    signOut();
                }
                riderSpinner.setSelection(MENU);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                riderSpinner.setSelection(MENU);
            }
        });



        //Initialize Geo API context.
        if (geoRiderApiContext == null) {
            geoRiderApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.maps_key)).build();
        }
        //Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.rider_map);
        assert mapFragment != null;
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    /**
     * WONT LET THE USER AVOID GIVING PERMISSIONS
     * if they have a route pending or in progress upon re-open, draw it
     */
    @Override
    protected void onResume() {
        super.onResume();
        //get location if have not gotten
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        if (getOrigin() == null) {
                            LatLng riderLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            setOrigin(riderLocation);
                            setChangingCoordinate("Destination");
                        }
                    } else {
                        pleaseCloseAndOpenAppDialog();
                    }
                });
        //check that permission services are granted upon re-open
        if (checkMapServices()) {
            if (!isLocationPermissionGranted) {
                checkUserPermission(); }
        }
        //if there is an unfinished ride, re draw it
        updateMapPendingRider();

    }


    /**
     * upon first app open, we need to grab you location. your location may not have been intialized with the
     * device before. In some situation, this may be null
     * https://developer.android.com/training/location/retrieve-current
     */
    private void pleaseCloseAndOpenAppDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device location has not been initialized! Please close and re-open the application and we will get it for you!")
                .setCancelable(false).setPositiveButton("Got It!", (dialog, id) -> finish());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    protected void updateMapPendingRider() {
        User currRider = ((UserData)(getApplicationContext())).getUser(); //current rider

        uRefUsers.document(currRider.getEmail()).addSnapshotListener(this, (documentSnapshot, e) -> {
            assert documentSnapshot != null;
            if (documentSnapshot.get("canceled") != null){
                android.util.Log.i(TAG, "canceled in non null after ride end");
                rideisPending = false;
                if (documentSnapshot.get("canceled").toString().equals("true")) {
                    rideInProgress = false; //the route is in progress --> could be limiting rider
                }
                else {
                    rideInProgress = true;
                }
            }else if (documentSnapshot.get("oriLat") != null){
                android.util.Log.i(TAG, "ori lat in non null after ride end");
                rideisPending = true; //the route is pending
                rideInProgress = false;
            }
        });

        uRefRequests.document(currRider.getEmail()).addSnapshotListener(this, (documentSnapshot, e) -> {
            assert documentSnapshot != null;
            if (documentSnapshot.get("oriLat") != null && documentSnapshot.get("desLat") != null) {
                //rideisPending = true;
                //rideInProgress = false;
                android.util.Log.i("ResumeMapTesting", documentSnapshot.toString());
                double originLat = Double.parseDouble(Objects.requireNonNull(documentSnapshot.get("oriLat")).toString());
                double originLong = Double.parseDouble(Objects.requireNonNull(documentSnapshot.get("oriLng")).toString());
                double destinationLong = Double.parseDouble(Objects.requireNonNull(documentSnapshot.get("desLng")).toString());
                double destinationLat = Double.parseDouble(Objects.requireNonNull(documentSnapshot.get("desLat")).toString());
                LatLng start = new LatLng(originLat, originLong);
                LatLng end = new LatLng(destinationLat, destinationLong);
                setDestination(end);
                setOrigin(start);
                calculateDirections();
                setMarker(getOrigin(), "Origin");
                setMarker(getDestination(), "Destination");
            }
        });
    }


    /**********************************SPINNER METHODS*****************************************/

    /**
     * Starts activity to display riders profile
     * Restricted by flag to use with internal profile view only and not view by external user
     **/
    public void viewRiderProfile() {
        Intent riderProfileIntent = new Intent(MapsRiderActivity.this, RiderProfileActivity.class);
        riderProfileIntent.putExtra("caller", "internal");
        startActivity(riderProfileIntent);
    }

    /***
     * start the activity to view the drivers profile (you are a rider). the external tag is to indicate that
     * you may not edit the profile information in this instance
     * @param d_email driver email
     */
    public void viewDriverProfile(String d_email) {
        Intent driverProfileIntent = new Intent(MapsRiderActivity.this, DriverProfilActivity.class);
        driverProfileIntent.putExtra("caller", "external");
        driverProfileIntent.putExtra("external_email", d_email);
        startActivity(driverProfileIntent);
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
        // TODO: Template for how I expect the QR info to be passed (rideremail,amount)
        String info = "md801003@gmail.com,20";
        qrProfileIntent.putExtra("INFO_TAG", info);
        startActivityForResult(qrProfileIntent, QR_REQ_CODE);
    }

    /**
     * Sign out a user and return to the login activity
     **/
    public void signOut() {
        Intent signOutConfirm  = new Intent(MapsRiderActivity.this, LoginActivity.class);
        signOutConfirm.putExtra("SignOut", "TRUE");
        setResult(RC_SIGN_OUT, signOutConfirm);
        finish();
    }


    /**********************************END SPINNER METHODS*****************************************/

     /**
     * Manipulates the map once available. If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has installed Google Play services and returned to the app.
      * @param googleMap fragment to display
      **/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        guuberRiderMap = googleMap;
        guuberRiderMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        guuberRiderMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark_mapstyle_json)));
        guuberRiderMap.setOnInfoWindowClickListener(MapsRiderActivity.this);

        /*
         * logs the coordinates in console upon map click this is giving the user a chance to set their pickup
         * location and drop-off location
         * @params latitude on longitude retrieved from map click
         */
        guuberRiderMap.setOnMapClickListener(arg0 -> {
            //cant build a route unless you cancel the current one
            if (!rideisPending || !rideInProgress) {
                if (getChangingCoordinate().equals("Origin")) {
                    setMarker(arg0, "Origin");
                    setOrigin(arg0);
                    originSetToast();
                } else if (getChangingCoordinate().equals("Destination")) {
                    setMarker(arg0, "Destination");
                    setDestination(arg0);
                    destinationSetToast();
                }
                guuberRiderMap.clear(); //clear the screen of other routes if you choose to build a new one
                setMarker(getOrigin(), "Origin");
                setMarker(getDestination(), "Destination");
                calculateDirections(); //automatically calculates directions and draws a route
            }else{
                cancelRequestFirstToast();
            }
        });

        //redundant
        if (checkUserPermission()) {
            //if user permission have been checked and location permission has been granted...**/
            guuberRiderMap.setMyLocationEnabled(true);
            guuberRiderMap.setOnMyLocationButtonClickListener(this);
            guuberRiderMap.setOnMyLocationClickListener(this);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            Location currLocation = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager.getBestProvider(criteria, true)));

            if (currLocation != null) {
                //create a new LatLng location object for the user current location**/
                LatLng currentLocation = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
                setOrigin(currentLocation);

                //move the camera to current location**/
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(10).build();
                guuberRiderMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                //because use provided origin, assume they are ready to pick their destination*/
                setChangingCoordinate("Destination");
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
     ** @returns rider origin (pickup location)
     **/
    public LatLng getOrigin(){
        return origin;
    }


    /**
     * let the user know they have chosen their origin
     **/
    public void originSetToast(){
        Toast.makeText(MapsRiderActivity.this,"Origin has been changed!",Toast.LENGTH_LONG).show();
    }

    /**
     * set user destination upon map click if origin has been set
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
     * let the user know they have chosen their destination and prompt them to make a request
     **/
    public void destinationSetToast(){
        Toast.makeText(MapsRiderActivity.this,"Click On your destination for details",Toast.LENGTH_LONG).show();
    }


    /**
     * determine whether or not the user is changing their origin or destination
     * @param coordsToChange will either be set to "origin" or "destination" detemined by user click on
     * "set destination" button or "set origin" button
     **/
    public void setChangingCoordinate(String coordsToChange){
        this.coordsToChange = coordsToChange;
    }

    /**
     * getter method to determine if onMapClick will be setting origin or destination
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
        guuberRiderMap.addMarker(new MarkerOptions().position(locationToMark).flat(false).title(title));
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
        return isMapsEnabled();
    }


    /**
     * MAKING SURE GPS IS ENABLED ON THE DEVICE
     **/
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }


    /**
     * OPENS UP SETTINGS FOR THEM TO TURN ON GPS IF IT IN NOT ALREADY ON
     **/
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                });
        final AlertDialog alert = builder.create();alert.show();
    }


    /**
     * iF USER PERMISSION HAS ALREADY BEEN ACCEPTED IT WILL NOT PROMPT THE USER
     * @return true if user has granted permission, false if user has not
     */
    private boolean checkUserPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }


    /**
     * SETTING A BOOLEAN TO TRUE IF ON ACTIVITY REQUEST PERMISSION FINISH
     * USER HAS ENABLED LOCATION SERVICES RUNS RIGHT AFTER PERMISSION RESULT
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
            }
        }
    }


    /**
     * GET THE RESULT OF THE REQUEST PERMISSION EVENT KEEP CHECKING PERMISSION UNTIL GRANTED
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (!isLocationPermissionGranted) {
                    checkUserPermission();
                }
            }
            case QR_REQ_CODE:{
                if(resultCode == RESULT_OK){
                    android.util.Log.i(TAG,"transaction processed");
                    Toast.makeText(this, "Transaction processed",  Toast.LENGTH_SHORT).show();
                    guuberRiderMap.clear();
                    rideInProgress = false;
                    rideisPending = false;
                }
            }
        }
    }

    /**
     * letting the user know they must pay the driver before using the map (application) again
     */
    private void payDriverFirstToast(){
        new Handler().postDelayed(() -> Toast.makeText(MapsRiderActivity.this, "You Have Yet To Pay Your Driver! Click Menu --> GenerateQR", Toast.LENGTH_LONG).show(), 500);
    }


    /**
     * Determines what to do on Info Window click. This is dependant on what stage of the ride sequence the rider is in
     * @param marker either marker (origin, destination) the user has set
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsRiderActivity.this);
        User currRider = ((UserData)(getApplicationContext())).getUser();

        uRefUsers.document(currRider.getEmail()).addSnapshotListener(this, (documentSnapshot, e) -> {
            assert documentSnapshot != null;
            if (documentSnapshot.get("rideOfferFrom") != null) {
                potentialOfferer = documentSnapshot.get("rideOfferFrom").toString();
            }
        });

        if (!rideInProgress && !rideisPending) {
            final NumberPicker numberPicker = new NumberPicker(MapsRiderActivity.this);
            numberPicker.setMaxValue(100); numberPicker.setMinValue(0);
            builder
                    .setTitle("This Trip Will Cost You: $" + getTripCost()).setCancelable(false)
                    .setMessage("Choose A Tip Percentage").setView(numberPicker)
                    .setNegativeButton("Make a request", (dialog, which) -> {
                        riderMapsDB.collection("Users").document(currRider.getEmail()).get().addOnSuccessListener(documentSnapshot -> {
                                    boolean canPay = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getWallet().validateTrans(getTripCost());
                                    if(!canPay){
                                        depositSomeMoneyToast();
                                    } else{
                                        rideisPending = true; //ride is an open request
                                        setTip(numberPicker.getValue()); //set the tip percentage
                                        makeRequest(marker); //make the request
                                    }
                                });
                        dialog.dismiss();
                    })
                    .setNeutralButton("Exit", (dialog, id) -> dialog.cancel());
            final AlertDialog alert = builder.create(); alert.show();

        }else if (rideisPending && !rideInProgress){
            builder
                .setTitle("Ride Is Pending")
                    .setPositiveButton("Check For Offers", (dialog, which) -> {
                        try {
                            potentialOfferer = riderDBHelper.seeOffer(currRider);
                        } catch (InterruptedException e) {
                            e.printStackTrace(); }

                        if (potentialOfferer != null){
                            willYouAcceptThisOfferDialog(potentialOfferer);
                        }else{
                            noOffersYetToast();
                        }
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel request", (dialog, which) -> {
                        rideisPending = false; //unhandled from the drivers end.
                        //requestsList.remove(0);//remove the request from the requestslist
                        riderDBHelper.cancelRequest(currRider); //remove request from the database
                        guuberRiderMap.clear();
                        dialog.dismiss();
                    });
                final AlertDialog alert = builder.create(); alert.show();

            }else if (rideInProgress) {
                builder
                    .setTitle("Driver Is On Way")
                        .setNeutralButton("View driver profile", (dialog, which) -> viewDriverProfile(potentialOfferer))
                    .setPositiveButton("Check if driver has arrived", (dialog, which) -> {
                        String arrivalStat  = riderDBHelper.getArrival(currRider.getEmail());
                        if (arrivalStat.equals("true")) {
                            driverIsHereDialog(currRider.getEmail());
                        }else {
                            driverHasNotArrivedYetToast();
                        }
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel request", (dialog, which) -> {
                        rideisPending = false;
                        rideInProgress = false;
                        //requestsList.remove(0);//remove the request from the requestslist
                        riderDBHelper.setCancellationStatus(currRider.getEmail(), potentialOfferer); //set status as canceled in the database
                        riderDBHelper.cancelRequest(currRider); //remove it from requests
                        guuberRiderMap.clear();
                        dialog.dismiss();
                    });
            final AlertDialog alert = builder.create();  alert.show();
        }
    }

    /**
     * rider is broke
     */
    private void depositSomeMoneyToast(){
        new Handler().postDelayed(() -> Toast.makeText(MapsRiderActivity.this, "You Don't Have Enough Money for This Trip", Toast.LENGTH_LONG).show(), 400);
    }


    /**
     * let the rider know the driver is on the way
     */
    private void driverHasNotArrivedYetToast(){
        new Handler().postDelayed(() -> Toast.makeText(MapsRiderActivity.this, "Your driver has not arrived yet", Toast.LENGTH_LONG).show(), 400);
    }


    /**
     * letting the user know their is no takers yet for their request
     */
    public void noOffersYetToast(){
        new Handler().postDelayed(() -> Toast.makeText(MapsRiderActivity.this, "No Offers Yet! Keep Checking Back In", Toast.LENGTH_LONG).show(), 400);

    }

    /**
     * Dialog prompting user to accept or offer
     * @param potentialOfferer is a global string containing the email of the potential drivers email
     */
    public void willYouAcceptThisOfferDialog(String potentialOfferer) {
        User currRider = ((UserData)(getApplicationContext())).getUser();
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsRiderActivity.this);
        builder
                .setTitle("Offer From: " + potentialOfferer).setCancelable(false)
                .setPositiveButton("Accept", (dialog, which) -> {
                    rideInProgress = true;
                    rideisPending = false;
                    //currRequest.setStatus("in Progress"); //is this changing in the array?
                    polyline.setColor(ContextCompat.getColor(MapsRiderActivity.this, R.color.TripInProgressPolyLinesColors));
                    riderDBHelper.acceptOffer(currRider);
                    yourDriverIsOnTheWayToast();
                    dialog.dismiss();
                }).setNeutralButton("View Driver Profile", (dialog, id) -> {
                    viewDriverProfile(potentialOfferer);
                    dialog.dismiss();
                }).setNegativeButton("Decline", (dialog, which) -> {
                    riderDBHelper.declineOffer(currRider);
                    youDeclinedTheOfferToast();
                    dialog.dismiss();
        });
        final AlertDialog alert = builder.create(); alert.show();
    }


    /**
     * let the rider know the driver has been notified and they are on the way**
     */
    private void yourDriverIsOnTheWayToast(){
        new Handler().postDelayed(() -> Toast.makeText(MapsRiderActivity.this, "Your Driver is on The Way!", Toast.LENGTH_LONG).show(), 600);
    }


    /**
     * let the rider know they have declined the offer
     */
    private void youDeclinedTheOfferToast(){
        new Handler().postDelayed(() -> Toast.makeText(MapsRiderActivity.this, "You Declined The offer", Toast.LENGTH_LONG).show(), 600);
    }


    /**
     * parse the coordinates of the origin and the destination destination is necessarily the marker that was clicked on
     * convert them to strings and send to Db
     * @param marker is the clicked upon marker. makers request to the title of the marker, which is the riders email
     */
    public void makeRequest(Marker marker){
        Toast.makeText(this, "Request Pending! Click On Your Destination To Check for Offers or  To Cancel", Toast.LENGTH_LONG).show();
        polyline.setColor(ContextCompat.getColor(MapsRiderActivity.this, R.color.TripIsPendingPolyLinesColors));
        User currRider = ((UserData)(getApplicationContext())).getUser(); //should this be global

        double originLatitude = getOrigin().latitude;
        double originLongitude = getOrigin().longitude;
        double destinationLatitude = getDestination().latitude; //new , test  this (apr 1)
        double destinationLongitude = getDestination().longitude;
        double tip = getTip();
        double tripCost = getTripCost();


        riderDBHelper.makeReq(currRider, tip, originLatitude , originLongitude, destinationLatitude,destinationLongitude,tripCost);
        rideisPending = true;
    }


    /**
     * if rider tries to build another request when they already have one open
     */
    private void cancelRequestFirstToast(){
        Toast.makeText(this, "Cancel current request to make a new one", Toast.LENGTH_LONG).show();
    }


    /**
     * calculate the direction from the rider's origin to the riders destination
     */
    private void calculateDirections() {

        //from riders set destination
        LatLng riderDestination = getDestination();
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                riderDestination.latitude, riderDestination.longitude
        );
        DirectionsApiRequest riderDirections = new DirectionsApiRequest(geoRiderApiContext);

        //from the riders set Origin
        LatLng currRiderLocation = getOrigin(); //make sure this is non null
        riderDirections.origin(
                new com.google.maps.model.LatLng(
                        currRiderLocation.latitude, currRiderLocation.longitude
                )
        );

        riderDirections.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                setTripCost(result.routes[0].legs[0].duration.inSeconds); //set trip cost based on duration in seconds for trip
                addPolylinesToMap(result);
            }
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
            }
        });
    }


    /**
     * a function returning the price of the trip
     * @param durationInSeconds is the route duration, in seconds
     */
    public void setTripCost(long durationInSeconds){
        long minutes = durationInSeconds/60;
        this.tripCost = minutes * 0.75;
    }

    /**
     * @return cost of the trip
     */
    public Double getTripCost(){
        return tripCost;
    }

    /**
     * @param tipPercentage is an integer representing the tip percentage the rider has chosen
     */
    public void setTip(int tipPercentage){
        tip = getTripCost() * (tipPercentage/100.0);
        this.tip = (double)Math.round(tip);
    }

    /**
     * @return the tip offered
     */
    public Double getTip(){
        return tip;
    }


    /**
     * add polyline to map based on the geo coords from the calculated route
     * @param result is the route determined by calculate directions
     **/
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(() -> {
            for(DirectionsRoute route: result.routes){
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                List<LatLng> newDecodedPath = new ArrayList<>();

                for(com.google.maps.model.LatLng latLng: decodedPath){
                    newDecodedPath.add(new LatLng(
                            latLng.lat, latLng.lng
                    ));
                }
                polyline = guuberRiderMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                if (!rideisPending && !rideInProgress){
                    polyline.setColor(ContextCompat.getColor(MapsRiderActivity.this, R.color.drawPolyLinesColors));
                } else if (rideInProgress) {
                    polyline.setColor(ContextCompat.getColor(MapsRiderActivity.this, R.color.TripInProgressPolyLinesColors));
                } else if (rideisPending) {
                    polyline.setColor(ContextCompat.getColor(MapsRiderActivity.this, R.color.TripIsPendingPolyLinesColors));
                }
            }
        });
    }


    /**
     * Dialog Builder for when the driver has arrived
     * for testing / building purposes, the driver is here dialog just delays,
     * displays after you have checked if the driver has arrived, and they "have"
     */
    private void driverIsHereDialog(String ridersEmail){
        User currRider = ((UserData)(getApplicationContext())).getUser();
        rideInProgress = false;
        rideisPending = false;

        uRefUsers.document(ridersEmail).addSnapshotListener(this, (documentSnapshot, e) -> {
            assert documentSnapshot != null;
            if (documentSnapshot.get("rideOfferFrom") != null) {
                potentialOfferer = documentSnapshot.get("rideOfferFrom").toString(); }
            if (documentSnapshot.get("tripCost") != null){
                tripCost = (Double) documentSnapshot.get("tripCost"); }
            if (documentSnapshot.get("reqTip") != null){
                tip = (Double) documentSnapshot.get("reqTip"); }
        });

        new Handler().postDelayed(() -> {
            polyline.setColor(ContextCompat.getColor(MapsRiderActivity.this, R.color.TripOverPolyLinesColors));
            final AlertDialog.Builder builder = new AlertDialog.Builder(MapsRiderActivity.this);
            builder
                    .setTitle("Your diver has arrived!! That was pretty fast... ").setCancelable(false)
                    .setNegativeButton("Rate driver", (dialog, which) -> {
                        viewDriverProfile(potentialOfferer);
                    })
                    .setPositiveButton("Pay driver", (dialog, id) -> {
                        rideisPending = false;
                        rideInProgress = false;
                        final Intent payDriverIntent = new Intent(MapsRiderActivity.this, QrActivity.class);
                        String amount = String.valueOf(tip + tripCost); //get total fee
                        payDriverIntent.putExtra("INFO_TAG", ridersEmail +"," + amount);   // Send email and fee to intent by a comma separated string
                        startActivityForResult(payDriverIntent, QR_REQ_CODE);// Show the generated qr
                        riderDBHelper.rideIsOver(currRider);
                        guuberRiderMap.clear();
                    });
            final AlertDialog alert = builder.create(); alert.show();
        },1000);
    }

}
