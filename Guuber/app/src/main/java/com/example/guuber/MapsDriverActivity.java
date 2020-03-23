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

import com.example.guuber.model.Rider;
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
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpGet;

/**
 * This class contains the home screen for a Driver.
 *  The home screen includes a menu enabling navigation
 *  between activities related to the account
 *  as well as the google map fragment
 *  and other related functionality for browsing ride requests.
 *  Class is representative of current application functionality
 */
public class MapsDriverActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {

    /**
     * spinner codes
     **/
    private static final int MENU = 0;
    private static final int VIEWTRIPS = 1;
    private static final int MYPROFILE = 2;
    private static final int WALLET = 3;
    private static final int SCANQR = 4;


    private GoogleMap guuberDriverMap;
    private Spinner driverSpinner;
    private Button driverSearchButton;
    private EditText geoLocationSearch;
    private LatLng search;
    private LatLng driverLocation;

    /*******NEW MAPS INTEGRATION**/
    private boolean isLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    private static int REQUEST_FINE_LOCATION_PERMISSION = 11;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 12;
    public static final int ERROR_DIALOG_REQUEST = 13;
    private static final String TAG = "MapsDriverActivity";
    private GeoApiContext geoApiContext = null;

    /*********************/


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
                String toastStr = "Click on the Map and press Search to Browse Open Requests in That Area!";
                Toast.makeText(MapsDriverActivity.this, toastStr, Toast.LENGTH_LONG).show();
            }
        }, 3000);

        /**Obtain the SupportMapFragment and get notified when the map is ready to be used.**/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        mapFragment.getMapAsync(this);
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.maps_key))
                    .build();
        }


        /**initialize a spinner and set its adapter, strings are in 'values'**/
        /**CITATION: Youtube, Coding Demos, Android Drop Down List, Tutorial,
         * published on August 4,2016 Standard License, https://www.youtube.com/watch?v=urQp7KsQhW8 **/
        ArrayAdapter<String> driverSpinnerAdapter = new ArrayAdapter<String>(MapsDriverActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menuDriver));
        driverSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(driverSpinnerAdapter);


        /**when driver clicks search button,
         * zoom in on area to view open requests*/
        driverSearchButton = findViewById(R.id.driver_search_button);
        driverSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSearch() != null) {
                    LatLng parse = getSearch();

                    /**move the camera to searching location**/
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(parse)
                            .zoom(11)
                            .build();
                    guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    invalidSearchToast();
                }
            }
        });

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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                driverSpinner.setSelection(MENU);
            }
        });

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
                android.util.Log.i("onMapClick", arg0.toString());
                geoLocationSearch.setText(arg0.toString());
                setSearch(arg0);

                //polyline proof of concept
                Polyline line = guuberDriverMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(37.413255, -122.0801542), new LatLng(arg0.latitude, arg0.longitude))
                        .width(5)
                        .color(Color.RED));

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
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));


            if (location != null) {
                /**create a new LatLng location object for the user current location**/
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                setDriverLocation(currentLocation);

                /**move the camera to current location**/
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)
                        .zoom(10)
                        .build();
                guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } else {
            /**
             * if user permission have been checked
             * and location permission has not been granted...
             **/
            guuberDriverMap.setMyLocationEnabled(false);
            LatLng UniversityOfAlberta = new LatLng(53.5213, -113.5213);

            /**move the camera to current location**/
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(UniversityOfAlberta)
                    .zoom(12)
                    .build();
            guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        /**
         * draw the open requests on the map
         * currently they are just mock requests
         **/
        drawOpenRequests();

    }

    public void setDriverLocation(LatLng location){
        this.driverLocation = location;
    }

    public LatLng getDriverLocation(){
        return driverLocation;
    }


    /**
     * render markers on the map based on open requests
     * currently  there are just mock open requests
     */
    public void drawOpenRequests() {
        /**near San Fran google Plex (where emulator location is)**/
        LatLng mockLatLng = new LatLng(37.40748, -122.062959);
        Rider mockRider = new Rider("780-123-4565", "mockEmail", "leah", "copeland");

        guuberDriverMap.addMarker(new MarkerOptions()
                .position(mockLatLng)
                .flat(false)
                .title("OPEN REQUEST\n" +
                        "name: " + mockRider.getFirstName() + " " +
                        mockRider.getLastName() + "\n " +
                        "Phone Number: " + mockRider.getPhoneNumber() + " " +
                        "Email: " + mockRider.getEmail()));

    }


    /**
     * set a marker given LATLNG information
     *
     * @param locationToMark is location to set marker on
     **/
    public void setMarker(LatLng locationToMark) {
        guuberDriverMap.addMarker(new MarkerOptions().position(locationToMark));
    }

    /**
     * get the LatLng object from
     * where the driver has set their search to
     *
     * @return LatLng object to navigate to
     */
    public LatLng getSearch() {
        return search;
    }


    /**
     * ser the LatLng object from
     * where the driver has set their search to
     *
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
     *
     * @return false all other times besides onMyLocationButtonClick event
     **/
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "clicked on current location", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * displays the details of your location upon click
     *
     * @param mylocation is a Location object representing your devices
     *                   real time location
     **/
    @Override
    public void onMyLocationClick(@NonNull Location mylocation) {
        Toast.makeText(this, "Current location:\n" + mylocation, Toast.LENGTH_LONG).show();
    }

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
        final Intent driverProfileIntent = new Intent(MapsDriverActivity.this, DriverProfilActivity.class);
        startActivity(driverProfileIntent);
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
        startActivity(scanQrProfileIntent);
    }


    /***************Leah Adding code here **********************
     * *********************************************************/

    /**
     * CHECKS IF LOCATION SERVICES HAVE BEEN ENABLED
     *
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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
        /*if(marker.getSnippet().equals(getUsername())){
            marker.hideInfoWindow();
        }
        else{*/


        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsDriverActivity.this);
        //builder.setView(R.layout.driver_profile_disp) setting the builder to the riders profile
        builder
                .setMessage("Determine Route to Rider?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        calculateDirections(marker);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void calculateDirections(Marker marker) {
        Log.d(TAG, "calculateDirections: calculating directions.");

        /**to the clicked markers destination**/
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        /**from the drivers current position**/
        LatLng currDriverLocation = getDriverLocation();
        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        currDriverLocation.latitude,
                        currDriverLocation.longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                Log.d(TAG, "onResult: successfully retrieved directions.");
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }



    private void addPolylinesToMap(final DirectionsResult result){


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {



                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));

                    }


                    /**PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.RED);
                    for (LatLng coord : newDecodedPath){
                         android.util.Log.i("HERES A COORD", coord.toString());
                        polylineOptions.add(coord);
                    }
                    guuberDriverMap.addPolyline(polylineOptions);**/

                    /**for (LatLng coord : newDecodedPath) {
                        Log.d(TAG, "HERE IS THE LAT LNG TO DRAW" + coord.toString());
                        Polyline polyline = guuberDriverMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(coord.latitude, coord.longitude), new LatLng(coord.latitude, coord.longitude))
                                .width(5)
                                .color(Color.RED));
                    }**/


                    Polyline polyline = guuberDriverMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(MapsDriverActivity.this, R.color.polyLinesColors));
                    polyline.setClickable(true);


                }
            }
        });
    }

}




