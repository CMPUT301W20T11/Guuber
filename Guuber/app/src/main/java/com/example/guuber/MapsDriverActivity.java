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
//import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.guuber.model.Rider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpGet;


public class MapsDriverActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {

    private static int REQUEST_FINE_LOCATION_PERMISSION = 11;

    /**spinner codes**/
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
                Toast.makeText(MapsDriverActivity.this,toastStr,Toast.LENGTH_LONG).show();
            }
        },3000);


        /**Obtain the SupportMapFragment and get notified when the map is ready to be used.**/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        mapFragment.getMapAsync(this);


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
                            .zoom(10)
                            .build();
                    guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }else{
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
                }else if (position == SCANQR){
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

        /**
         * logs the coordinates in console upon map click
         * this is giving the driver a chance to browse
         * a specified area for open requests
         * @params latitude on longitude retrieved from map click
         **/
        guuberDriverMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0){
                android.util.Log.i("onMapClick", arg0.toString());
                geoLocationSearch.setText(arg0.toString());
                setSearch(arg0);
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
            LatLng UniversityOfAlberta = new LatLng( 53.5213 , -113.5213);

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

    /**
     * render markers on the map based on open requests
     * currently  there are just mock open requests
     */
    public void drawOpenRequests(){
        /**near San Fran google Plex (where emulator location is)**/
        LatLng mockLatLng = new LatLng(37.5200, -122.08856);
        Rider mockRider = new Rider("780-123-4565","mockEmail","leah","copeland");

        guuberDriverMap.addMarker(new MarkerOptions()
                .position(mockLatLng)
                .flat(false)
                .title( "OPEN REQUEST\n" +
                        "name: " +  mockRider.getFirstName() + " " +
                        mockRider.getLastName() + "\n " +
                        "Phone Number: " + mockRider.getPhoneNumber() + " " +
                        "Email: " + mockRider.getEmail()));

        /**near U of A (where default location is set if location permission is not granted)**/
        LatLng mockLatLng2 = new LatLng(53.5213, -113.5213);
        Rider mockRider2 = new Rider("780-123-4565","mockEmail","otherLeah","copeland");

        guuberDriverMap.addMarker(new MarkerOptions().position(mockLatLng2)
                .title( "OPEN REQUEST\n" +
                        mockRider2.getFirstName() + " " +
                        mockRider2.getLastName() + "\n " +
                        mockRider2.getPhoneNumber() + " " +
                        mockRider2.getEmail()));

    }

    /**
     * set a marker given LATLNG information
     * @param locationToMark is location to set marker on
     **/
    public void setMarker(LatLng locationToMark){
        guuberDriverMap.addMarker(new MarkerOptions().position(locationToMark));
    }

    /**
     * get the LatLng object from
     * where the driver has set their search to
     * @return LatLng object to navigate to
     */
    public LatLng getSearch(){
        return search;
    }

    /**
     * ser the LatLng object from
     * where the driver has set their search to
     * @param search object to navigate to
     */
    public void setSearch(LatLng search){
        this.search = search;
    }

    /**
     * inform the driver they have searched an invalid location
     **/
    public void invalidSearchToast(){
        String toastStr = "Invalid Search! Click on the Map and press Search to Browse Open Requests in That Area";
        Toast.makeText(MapsDriverActivity.this,toastStr,Toast.LENGTH_LONG).show();
    }


    /**
     * check user permissions
     * @return true if user has reponded to permission request
     * @return false if user has not responded to permission request
     **/
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
            return true;
        }
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
    public void openDriverWallet(){
        final Intent driverWalletIntent = new Intent(MapsDriverActivity.this, WalletActivity.class);
        startActivity(driverWalletIntent);
    }

    /**
     * Starts activity to allow rider to generate QR
     **/
    public void scanQR(){
        final Intent scanQrProfileIntent = new Intent(MapsDriverActivity.this, scanQrActivity.class);
        startActivity(scanQrProfileIntent);
    }


}
