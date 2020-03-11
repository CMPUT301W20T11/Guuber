package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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


public class MapsRiderActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, EnableLocationServices.OnFragmentInteractionListener {

    private static int REQUEST_FINE_LOCATION_PERMISSION = 11;
    private static final int MENU = 0;
    private static final int VIEWTRIPS = 1;
    private static final int MYPROFILE = 2;
    private static final int  WALLET = 3;

    private static final int SIGNOUT = 4;

    private GoogleMap guuberRiderMap;
    Spinner riderSpinner;

    Button makeRequestButton, goButton;
    EditText riderDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider_maps);
        riderSpinner =  findViewById(R.id.rider_spinner); //set the driver spinner

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_map);
        mapFragment.getMapAsync(this);


        makeRequestButton.findViewById(R.id.make_request_button);
        /**makeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Click where you want to go on the map and press Go!", Toast.LENGTH_SHORT).show();

                /**after make request button is clicked
                 * set on map click listener and retrieve the coords
                 * from where the user clicks on the map
                 * set this information in the edit text
                 * when user presses go button the information is sent to the data base


            }
        });


        riderDestination = findViewById(R.id.destination_location_EditText);
        goButton.findViewById(R.id.go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                riderDestination.setText("working on it");
            }
        });

        /**initialize a spinner and set its adapter, strings are in 'values'**/
        /**CITATION: Youtube, Coding Demos, Android Drop Down List, Tutorial,
         * published on August 4,2016 Standard License, https://www.youtube.com/watch?v=urQp7KsQhW8 **/
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MapsRiderActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.menu));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riderSpinner.setAdapter(spinnerAdapter);


        /**calling methods based on the item in the spinner drop down menu that is clicked**/
        riderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == VIEWTRIPS){
                    /**start the view trips activity**/
                    //spinner.setSelection(MENU);
                }else if (position == MYPROFILE) {
                    /**start the my profile activity*/
                    //spinner.setSelection(MENU);
                }else if (position == WALLET){
                    /**start the walleett activity**/
                    //spinner.setSelection(OPTIONS);
                }else if (position == SIGNOUT){
                    /**sign out of activity**/
                    Log.d("myTag", "This is my message");
                    //spinner.setSelection(OPTIONS);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                riderSpinner.setSelection(MENU);
            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        guuberRiderMap = googleMap;
        guuberRiderMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        guuberRiderMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark_mapstyle_json)));

        guuberRiderMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                android.util.Log.i("onMapClick", arg0.toString());
            }
        });


        if (checkUserPermission()) {
            guuberRiderMap.setMyLocationEnabled(true);
            guuberRiderMap.setOnMyLocationButtonClickListener(this);
            guuberRiderMap.setOnMyLocationClickListener(this);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

            if (location != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(10)
                        .build();

                guuberRiderMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } else {
            guuberRiderMap.setMyLocationEnabled(false);
            new EnableLocationServices().show(getSupportFragmentManager(), "ENABLE_LOCATION");

        }

    }

    /**once app is further developed, this request should be made upon ride request, not on app open**/
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
            //user has already set location permission preferences
            return true;
        }
    }

    /**gives you the results what the users decision was for location preferences**/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /**Fine location permission granted**/
                Toast.makeText(this,"location services enabled. Welcome to Guuber!",Toast.LENGTH_LONG).show();
            } else {
                /**fine location permission denied**/
                Toast.makeText(this,"To use Guuber, enable location services",Toast.LENGTH_LONG).show();
            }

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

}
