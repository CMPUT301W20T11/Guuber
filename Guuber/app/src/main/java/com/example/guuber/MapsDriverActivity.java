package com.example.guuber;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.widget.Toast.makeText;


/**
 * This class contains the home screen for a Driver.
 *  The home screen includes a menu enabling navigation between activities related to the account
 *  as well as the google map fragment and other related functionality for browsing ride requests.
 *  Class is representative of current application functionality
 */
public class MapsDriverActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {

    //spinner codes
    private static final int MENU = 0;
    private static final int MYPROFILE = 1;
    private static final int WALLET = 2;
    private static final int SCANQR = 3;
    private static final int OFFLINE_REQS = 4;
    private static final int SIGNOUT = 5;


    //permissions / result codes
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 12;
    private static final int QR_SCAN_CODE = 4;
    private static final int RC_SIGN_OUT = 1000;

    //booleans for status
    private boolean offerSent;
    private boolean offerAccepted;
    private boolean routeInProgress;

    //location
    LocationManager locationManager;
    Criteria criteria = new Criteria();
    private GeoApiContext geoApiContext = null;
    private static Location currLocation;

    //maps/globals
    private boolean isLocationPermissionGranted = false;
    private static final String TAG = "MapsDriverActivity";
    private Polyline polyline;
    private String riderEmail;
    private GoogleMap guuberDriverMap;
    private LatLng search, driverLocation;


    //database
    private FirebaseFirestore driverMapsDB = FirebaseFirestore.getInstance();
    private GuuDbHelper driverDBHelper = new GuuDbHelper(driverMapsDB);
    private CollectionReference uRefRequests = driverMapsDB.collection("requests");
    private CollectionReference uRefUsers = driverMapsDB.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maps);


        //instructions for User to provide their destination delayed to give time for map rendering
        new Handler().postDelayed(() -> makeText(MapsDriverActivity.this,"Click on the Map and double click Search to Browse Open Requests in That Area!", Toast.LENGTH_LONG).show(), 3000);


        //when driver clicks search button, zoom in on area to view open requests
        Button driverSearchButton = findViewById(R.id.driver_search_button);
        driverSearchButton.setOnClickListener(v -> {
            if (!offerSent || !routeInProgress) {
                if (getSearch() != null) {
                    LatLng parse = getSearch();
                    drawOpenRequests(); //doest draw on first call

                    //move the camera to searching location
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(parse).zoom(5).build();
                    guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    invalidSearchToast(); }
            } else {
                youveSentAnOfferToast(); }
        });


        //initialize a spinner and set its adapter, strings are in 'values'**/
        //CITATION: Youtube, Coding Demos, Android Drop Down List, Tutorial, published on August 4,2016 Standard License, https://www.youtube.com/watch?v=urQp7KsQhW8 **/
        ArrayAdapter<String> driverSpinnerAdapter = new ArrayAdapter<>(MapsDriverActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menuDriver));
        Spinner driverSpinner = findViewById(R.id.driver_spinner);
        driverSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(driverSpinnerAdapter);

        //calling methods based on the item in the spinner drop down menu that is clicked
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == MYPROFILE) {
                    viewDriverProfile();
                } else if (position == WALLET) {
                    openDriverWallet();
                } else if (position == SCANQR) {
                    scanQR();
                }else if (position == OFFLINE_REQS ) {
                    currOfflineReqs();
                }else if (position == SIGNOUT) {
                    signOut();
            }
                driverSpinner.setSelection(MENU);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                driverSpinner.setSelection(MENU);
            }
        });

        //initialize GEO API context to be non null.
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.maps_key)).build();
        }

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_map);
        assert mapFragment != null;
        Objects.requireNonNull(mapFragment).getMapAsync(this);

    }

    /**
     * WONT LET THE USER AVOID GIVING PERMISSIONS
     */
    @Override
    protected void onResume() {
        super.onResume();
        //get location if have not gotten
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        driverLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        setDriverLocation(driverLocation);
                    }else {
                        pleaseCloseAndOpenAppDialogD();
                    }
                });
        //check that permission services are granted upon re-open
        if(checkMapServices()){
            if(!isLocationPermissionGranted){
                checkUserPermission(); }
        }
        updateMapDriver();
    }

    /**
     * upon first app open, we need to grab you location. your location may not have been intialized with the
    * device before. In some situation, this may be null
    * https://developer.android.com/training/location/retrieve-current
    **/
    private void pleaseCloseAndOpenAppDialogD(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device location has not been initialized! Please close and re-open the application and we will get it for you!")
                .setCancelable(false).setPositiveButton("Got it!", (dialog, id) -> finish());
        final AlertDialog alert = builder.create();
        alert.show();
    }


    protected void updateMapDriver() {
        User currRider = ((UserData)(getApplicationContext())).getUser(); //current rider

        uRefUsers.document(currRider.getEmail()).addSnapshotListener(this, (documentSnapshot, e) -> {
            // these fields should only be populated if there is an active request or pending request
            assert documentSnapshot != null;
            if (documentSnapshot.get("offerTo") != null && documentSnapshot.get("offerStatus") != null) {
                offerSent = Boolean.TRUE;

                // check if active
                if (Objects.requireNonNull(documentSnapshot.get("offerStatus")).toString().equals("accepted")) {
                    String offerToEmail = Objects.requireNonNull(documentSnapshot.get("offerTo")).toString();
                    offerAccepted = true;
                    offerSent = false;
                    routeInProgress = true;
                    getRideDetails(offerToEmail);
                }
                else if (Objects.requireNonNull(documentSnapshot.get("offerStatus")).toString().equals("pending")) {
                    String offerToEmail = Objects.requireNonNull(documentSnapshot.get("offerTo")).toString();
                    getRideDetails(offerToEmail);
                }

            }
        });
    }
    protected void getRideDetails(String offerToEmail) {

        uRefUsers.document(offerToEmail).addSnapshotListener(this, (documentSnapshot, e) -> {
            assert documentSnapshot != null;
            if (documentSnapshot.get("oriLat") != null && documentSnapshot.get("desLat") != null) {

                double originLat = Double.parseDouble(Objects.requireNonNull(documentSnapshot.get("oriLat")).toString());
                double originLong = Double.parseDouble(Objects.requireNonNull(documentSnapshot.get("oriLng")).toString());
                double destinationLong = Double.parseDouble(Objects.requireNonNull(documentSnapshot.get("desLng")).toString());
                double destinationLat = Double.parseDouble(Objects.requireNonNull(documentSnapshot.get("desLat")).toString());
                LatLng start = new LatLng(originLat, originLong);
                LatLng end = new LatLng(destinationLat, destinationLong);

                setMarker(start, offerToEmail);
                setMarker(end, offerToEmail);

                Marker startMarker =  guuberDriverMap.addMarker(new MarkerOptions().position(start).flat(false).title(offerToEmail));
                calculateDirectionsToPickup(startMarker);
                calculateDirectionsBetweenPickupandDropOff(offerToEmail,start, end);
            }
        });
    }



    /****************************************SPINNER METHODS***********************************************/


    /**
     * Starts activity to display drivers profile
     **/
    public void viewDriverProfile() {
        Intent driverProfileIntent = new Intent(MapsDriverActivity.this, DriverProfilActivity.class);
        driverProfileIntent.putExtra("caller", "internal");
        startActivity(driverProfileIntent);
    }

    /***
     * start the activity to view the riders profile (you are a driver). the external tag is to indicate that
     * you may not edit the profile information in this instance
     * @param r_email the riders email
     */
    public void viewRiderProfile(String r_email) {
        Intent riderProfileIntent = new Intent(MapsDriverActivity.this, RiderProfileActivity.class);
        riderProfileIntent.putExtra("caller", "external");
        riderProfileIntent.putExtra("external_email", r_email);
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

    /**
     * view the details of a current request if you are offline
     */
    private void currOfflineReqs(){
        Intent viewOfflineReqs = new Intent(MapsDriverActivity.this, CurrentRequestsOffline.class);
        User currDriver = ((UserData)(getApplicationContext())).getUser();
        viewOfflineReqs.putExtra("DRIVER_EMAIL", currDriver.getEmail());
        //requestsList = saveRequestForOffline.loadData(MapsRiderActivity.this);
        startActivity(viewOfflineReqs);
    }

    /****************************************END SPINNER METHODS***********************************************/

    /**
     * Manipulates the map once available.I f Google Play services is not installed on the device, the user will
     * be prompted to install it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @param googleMap the map to display
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        guuberDriverMap = googleMap;
        guuberDriverMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        guuberDriverMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark_mapstyle_json)));
        guuberDriverMap.setOnInfoWindowClickListener(MapsDriverActivity.this);

        EditText geoLocationSearch = findViewById(R.id.geo_location_EditText);
        /* logs the coordinates in console upon map click this is giving the driver a chance to browse
           a specified area for open requests
           @params latitude on longitude retrieved from map click */
        guuberDriverMap.setOnMapClickListener(arg0 -> {
            if (routeInProgress || offerSent) {
                youveSentAnOfferToast(); //clicking on the map wont do anything when youre on a route or have sent an offer
            } else {
                geoLocationSearch.setText(arg0.toString()); //set the search bar to the coordinates clicked
                setSearch(arg0);
            }
        });


        if (checkUserPermission()) {
            //if user permission have been checked* and location permission has been granted
            guuberDriverMap.setMyLocationEnabled(true);
            guuberDriverMap.setOnMyLocationButtonClickListener(this);
            guuberDriverMap.setOnMyLocationClickListener(this);
            //make sure you have location
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            currLocation = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager.getBestProvider(criteria, true)));

            if (currLocation != null) {
                //create a new LatLng location object for the user current location
                LatLng theLocation = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
                setDriverLocation(theLocation); //driver must provide their location

                //move the camera to current location
                CameraPosition cameraPosition = new CameraPosition.Builder().target(theLocation).zoom(10).build();
                guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

    }

    /**
     * render markers on the map based on open requests containing user the requester's profile and
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
                        tripCost = Double.parseDouble(value.toString()); break;
                    case "reqTip":
                        offeredTip = Double.parseDouble(value.toString()); break;
                    case "desLat":
                        destinationLat = Double.parseDouble(value.toString()); break;
                    case "oriLat":
                        originLat = Double.parseDouble(value.toString()); break;
                    case "desLng":
                        destinationLong = Double.parseDouble(value.toString()); break;
                    case "oriLng":
                        originLong = Double.parseDouble(value.toString()); break;
                    case "email":
                        email = value.toString(); break;
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

            calculateDirectionsBetweenPickupandDropOff(email, requestStart, requestEnd); //draws the riders route
        }
    }

    /**
     * letting driver know there are no open requests
     */
    private void noOpenRequestToast() {
        makeText(MapsDriverActivity.this, "No Open Requests! Try clicking search again", Toast.LENGTH_LONG).show();
    }

    /**
     * letting driver know they have already made an offer
     * they must be denied or be accepted and complete the trip before sending another offer
     */
    private void youveSentAnOfferToast() {
        makeText(MapsDriverActivity.this, "Please wait for Rider response before making another offer", Toast.LENGTH_LONG).show();
    }

    /**
     * general function to set a marker and a title
     * @param locationToMark location to mark hehe
     * @param title the title
     */
    public void setMarker(LatLng locationToMark, String title){
        guuberDriverMap.addMarker(new MarkerOptions().position(locationToMark).flat(false).title(title)
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
     * set the driver location is necessarily set upon map render
     * @param location is the drivers current location
     **/
    public synchronized void setDriverLocation(LatLng location) {
        //just really forcing the driver to have a location everywhere in this class by various means
        if(location == null){
            if (!checkUserPermission()){
                checkUserPermission();
            }else {
                Location getLocation = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager.getBestProvider(criteria, true)));
                assert getLocation != null;
                LatLng driverLocation = new LatLng(Objects.requireNonNull(getLocation).getLatitude(), getLocation.getLongitude());
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
     * get the LatLng object from where the driver has set their search to
     * @return LatLng object to navigate to
     */
    public LatLng getSearch() {
        return search;
    }


    /**
     * ser the LatLng object from where the driver has set their search to
     * @param search object to navigate to
     */
    public void setSearch(LatLng search) {
        this.search = search;
    }


    /**
     * inform the driver they have searched an invalid location
     **/
    public void invalidSearchToast() {
        makeText(MapsDriverActivity.this,"Invalid Search! Click on the Map and press Search to Browse Open Requests in That Area", Toast.LENGTH_LONG).show();
    }


    /**
     * indicates current location button has been clicked...
     * @return false all other times besides onMyLocationButtonClick event
     **/
    @Override
    public boolean onMyLocationButtonClick() {
        makeText(this, "clicked on current location", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * displays the details of your location upon click
     * @param mylocation is a Location object representing your devices real time location
     **/
    @Override
    public void onMyLocationClick(@NonNull Location mylocation) {
        makeText(this, "Current location:\n" + mylocation, Toast.LENGTH_LONG).show();
    }


    /**
     * CHECKS IF LOCATION SERVICES HAVE BEEN ENABLED
     * @return true if they have, false if they have not
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
        if (!Objects.requireNonNull(manager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
        final AlertDialog alert = builder.create(); alert.show();
    }


    /**
     * iF USER PERMISSION HAS ALREADY BEEN ACCEPTED IT WILL NOT PROMPT THE USER
     * AND SET BOOLEAN  TO TRUE ELSE IT WILL RETURN FALSE
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
     * GET THE RESULT OF THE TRANSACTION. TRANSACTION WILL ALWAYS BE PROCESSED UNLESS SCANNING ERROR FROM DRIVER
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
            case QR_SCAN_CODE: {
                if (resultCode == RESULT_OK) {
                    makeText(this, "Transaction processed", Toast.LENGTH_SHORT).show();
                    offerSent  = false;
                    routeInProgress = false;
                    offerAccepted = false;
                    guuberDriverMap.clear();
                } else {
                    makeText(this, "Transaction failed. Try Paying your Driver Again", Toast.LENGTH_SHORT).show();
                    routeInProgress = true;
                }
            }
        }
    }


    /**
     * Determines what to do on Info Window click. This is dependant on what stage of the ride sequence the driver is in
     * @param marker either marker (origin, destination) the user has set, or we have retrieved
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        calculateDirectionsToPickup(marker);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsDriverActivity.this);
        riderEmail = marker.getTitle(); //always referring to the last marker click
        User currDriver = ((UserData)(getApplicationContext())).getUser();

        if(!offerSent && !offerAccepted) {
            builder
                    .setMessage("What would you like to do?")
                    .setPositiveButton("View  Riders Profile", (dialog, which) -> viewRiderProfile(riderEmail))
                    .setNegativeButton("Offer Them a Ride", (dialog, id) -> {
                        offerSent = true;
                        try {
                            offerRide(marker);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    });
            final AlertDialog alert = builder.create(); alert.show();

        }else if (offerSent && !offerAccepted){
            builder
                    .setMessage("Offer has been sent")
                    .setPositiveButton("Check status", (dialog, which) -> {
                        String statusCheck = null;
                        try {
                            statusCheck = driverDBHelper.checkOfferStatus(currDriver);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (statusCheck != null) {
                            if (statusCheck.equals("accepted")) {
                                offerAccepted = true;
                                routeInProgress = true;
                                try {
                                    offerAccepted(riderEmail, marker);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else if (statusCheck.equals("declined")) {
                                offerAccepted = false;
                                offerSent = false;
                                offerDeclined();
                                dialog.dismiss();
                            } else {
                                stillPendingToast();
                                dialog.dismiss();
                            }
                        }
                    });
            final AlertDialog alert = builder.create();alert.show();

        } else if (routeInProgress){
            builder
                    .setMessage("Route In Progress")
                    .setNegativeButton("Check if rider has canceled", (dialog, which) -> {
                        String cancelled; // initialized in db helper
                        cancelled = driverDBHelper.getCancellationStatus(currDriver.getEmail());
                        if (cancelled.equals("false")){
                            youHaveNotBeenCancelledOnToast();
                        }else{
                            routeInProgress = false; offerSent = false; offerAccepted = false;
                            youHaveBeenCancelledOnToast();
                            guuberDriverMap.clear(); //clear the map
                            drawOpenRequests(); //draw the open requests
                        }
                        dialog.dismiss();
                    })
                    .setNeutralButton("View rider profile", (dialog, which) -> viewRiderProfile(riderEmail))
                    .setPositiveButton("Let rider know you've arrived", (dialog, which) -> {
                        weHaveToldThemToast();
                        driverDBHelper.setArrival(currDriver.getEmail());
                        });
                    }
            final AlertDialog alert = builder.create();alert.show();
        }


    /**
     Let the Driver know, they have notified the ride of their arrival
     */
    private void weHaveToldThemToast(){
        new Handler().postDelayed(() -> makeText(MapsDriverActivity.this,"Rider is notified of your arrival", Toast.LENGTH_LONG).show(), 500);
    }

    /**
     Let the Driver know, that the rider has not cancelled the request
     */
    private void youHaveNotBeenCancelledOnToast(){
        new Handler().postDelayed(() -> makeText(MapsDriverActivity.this," You Have Not Been Cancelled On", Toast.LENGTH_LONG).show(), 500);
    }


    /**
    Let the Driver know, that the rider has cancelled the request
     */
    private void youHaveBeenCancelledOnToast(){
        new Handler().postDelayed(() -> makeText(MapsDriverActivity.this,"The Rider has cancelled your trip", Toast.LENGTH_LONG).show(), 500);
    }


    /**
     * Offer a ride to the Rider and be accepted or denied
     */
    public void offerRide(Marker marker) throws InterruptedException {
        calculateDirectionsToPickup(marker); //null pointer if location is null
        User currDriver = ((UserData)(getApplicationContext())).getUser();
        riderEmail = marker.getTitle();
        offerSent = true;
        guuberDriverMap.clear();

        new Handler().postDelayed(() -> makeText(MapsDriverActivity.this,"Your ride has been offered", Toast.LENGTH_LONG).show(), 500);

        LatLng currReqMarker = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        setMarker(currReqMarker, riderEmail);

        User riderToOfferTo = driverDBHelper.getUser(riderEmail);
        riderToOfferTo.setEmail(riderEmail); //<-----one thing that is fixing the crash

        driverDBHelper.offerRide(currDriver,riderToOfferTo);

    }

    /**
     * re draws the current route as green to indicate its in process. lets the driver know the route is in progress
     * @param riderEmail the riders email
     * @param marker the clicked upon marker
     */
    private void offerAccepted(String riderEmail, Marker marker) throws InterruptedException {
        routeInProgress = true;
        guuberDriverMap.clear();
        User currDriver = ((UserData)(getApplicationContext())).getUser();

        LatLng pickupPoint = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        setMarker(pickupPoint,riderEmail);
        calculateDirectionsToPickup(marker);

        new Handler().postDelayed(() -> makeText(MapsDriverActivity.this, "Your offer was accepted. Click on the riders pickup location to let them know when you have arrived", Toast.LENGTH_LONG).show(), 500);

        User riderForRoute = driverDBHelper.getUser(riderEmail);
        try {
            driverDBHelper.reqAccepted(riderForRoute, currDriver);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    /**
     * toast to let the driver know their offer was declined
     */
    private void offerDeclined(){
        makeText(MapsDriverActivity.this, "Your Offer Was Declined. Press Search to Continue Browsing Requests", Toast.LENGTH_SHORT).show();
        guuberDriverMap.clear();
        drawOpenRequests();
    }

    /**
     * let the driver know that the rider has yet to see or make a decision
     */
    private void stillPendingToast(){
        new Handler().postDelayed(() -> makeText(MapsDriverActivity.this, "Your offer is still pending!", Toast.LENGTH_LONG).show(), 500);
    }


    /**
     * NECESSARY there is no null pointers from either direction
     * @param email logging email for reference. the riders email
     * @param pickup the riders pickup point
     * @param dropOff the riders dropoff point
     */
    private void calculateDirectionsBetweenPickupandDropOff(String email, LatLng pickup, LatLng dropOff) {
        android.util.Log.i("USERS EMAIL", email);
        //android.util.Log.i("USERS PICKUP", pickup.toString());
        //android.util.Log.i("USERS DROPOFF", dropOff.toString());

        //from riders set destination
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                dropOff.latitude, dropOff.longitude
        );
        DirectionsApiRequest driverDirections = new DirectionsApiRequest(geoApiContext);

        //from the riders set Origin
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
     *  NECESSARY there is no null pointers from either direction
     * directions from the drivers location to the rider pickup
     * @param marker the marker indicating the riders pickup location
     */
    private void calculateDirectionsToPickup(Marker marker) {
            driverLocation = new LatLng(currLocation.getLatitude(),currLocation.getLongitude());
            setDriverLocation(driverLocation);

            //to the clicked markers destination
            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    marker.getPosition().latitude,
                    marker.getPosition().longitude
            );
            DirectionsApiRequest driverDirections = new DirectionsApiRequest(geoApiContext);

            //from the drivers current position
            LatLng currDriverLocation = getDriverLocation();
            driverDirections.origin(
                    new com.google.maps.model.LatLng(
                            currDriverLocation.latitude,
                            currDriverLocation.longitude
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
                            latLng.lat,
                            latLng.lng
                    ));
                }

                polyline = guuberDriverMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                if (!offerAccepted && !offerSent){
                    polyline.setColor(ContextCompat.getColor(MapsDriverActivity.this, R.color.drawPolyLinesColors));
                } else if (routeInProgress) {
                    polyline.setColor(ContextCompat.getColor(MapsDriverActivity.this, R.color.TripInProgressPolyLinesColors));
                } else if (offerSent) {
                    polyline.setColor(ContextCompat.getColor(MapsDriverActivity.this, R.color.TripIsPendingPolyLinesColors));
                }
            }
        });
    }

}




