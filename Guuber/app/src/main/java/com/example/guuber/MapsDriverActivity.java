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
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;


public class MapsDriverActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {

    private static int REQUEST_FINE_LOCATION_PERMISSION = 11;
    private static final int MENU = 0;
    private static final int VIEWTRIPS = 1;
    private static final int MYPROFILE = 2;
    private static final int WALLET = 3;


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
                            .zoom(15)
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

        /**log the coords in console upon map click
         * this is giving the user a chance to set their destination**/
        guuberDriverMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0){
                android.util.Log.i("onMapClick", arg0.toString());
                geoLocationSearch.setText(arg0.toString());
                setSearch(arg0);
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
                /**create a new LatLng location object for the user current location**/
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                /**move the camera to current location**/
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)
                        .zoom(10)
                        .build();
                guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                /**set a marker on Drivers current location**/
                setMarker(currentLocation);

            }
        } else {
            /**if user permission has been checked and
             * location services have been denied
             * set map to display Edmonton (for testing)*/
            guuberDriverMap.setMyLocationEnabled(false);
            LatLng UniversityOfAlberta = new LatLng( 53.5213 , -113.5213);

            /**move the camera to current location**/
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(UniversityOfAlberta)
                    .zoom(12)
                    .build();
            guuberDriverMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    /**set a marker given LATLNG information**/
    public void setMarker(LatLng locationToMark){
        guuberDriverMap.addMarker(new MarkerOptions().position(locationToMark));
    }

    public LatLng getSearch(){
        return search;
    }

    public void setSearch(LatLng search){
        this.search = search;
    }

    public void invalidSearchToast(){
        String toastStr = "Invalid Search! Click on the Map and press Search to Browse Open Requests in That Area";
        Toast.makeText(MapsDriverActivity.this,toastStr,Toast.LENGTH_LONG).show();
    }

    public void drawRoute(LatLng origin, LatLng destination) throws IOException {
        final String url = getURL(origin,destination);

        AndroidHttpClient client = AndroidHttpClient.newInstance("somename");

        //HttpGet request = new HttpGet(url);
        //HttpResponse response = client.execute(request);

        /*InputStream source = response.getEntity().getContent();
        String returnValue = IOUtilsIsAnNPC(source, Charset.defaultCharset());

        return returnValue;*/
    }

    /**return the route origin and destination points**/
    public String getURL(LatLng origin, LatLng destination) {
        //String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=bicycling&language=fr-FR&key=YOUR_API_KEY
        String url =
                "https://maps.googleapis.com/maps/api/directions/json?origin="
                        + origin.latitude + "," + origin.longitude + "&destination="
                        + destination.latitude + "," + destination.longitude + "&key=AIzaSyBrMB718EfayxLwWRqw3MMRYq_bWooDkm8";
        android.util.Log.i("URL FOR PARSING = ", url);
        return url;
    }



    /**returns a string from the input stream**/
    public String IOUtilsIsAnNPC(InputStream inputStream, Charset charset) throws IOException {
        android.util.Log.i("IN IO UTIL NPC", "......");
        StringBuilder stringBuilder = new StringBuilder();
        String line = null; //reference

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }


    /*** check user permissions**/
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


    /*** indicates current location button has been clicked... do we need?**/
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "clicked on current location", Toast.LENGTH_SHORT).show();
        return false;
    }

    /*** displays the details of your location upon click**/
    @Override
    public void onMyLocationClick(@NonNull Location mylocation) {
        Toast.makeText(this, "Current location:\n" + mylocation, Toast.LENGTH_LONG).show();
    }

    /**
     * launches the view trips history activity
     **/
    public void viewDriverTrips() {
        final Intent driverTripsIntent = new Intent(MapsDriverActivity.this, ViewTripsActivity.class);
        startActivity(driverTripsIntent);
    }


    public void viewDriverProfile() {
        final Intent driverProfileIntent = new Intent(MapsDriverActivity.this, DriverProfilActivity.class);
        startActivity(driverProfileIntent);
    }






}
