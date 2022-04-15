package com.example.go;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class DriversMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;


    private Button LogoutDriverButton, SettingsDriverButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean currentLogoutDriverStatus;
    private DatabaseReference assignedCustomerRef, AssignedCustomerPositionRef;
    private String driverID, customerID="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_drivers_map2);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        driverID = mAuth.getCurrentUser().getUid();

        LogoutDriverButton = (Button)findViewById(R.id.driver_logout_button);
        SettingsDriverButton = (Button)findViewById(R.id.driver_settings_button);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LogoutDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLogoutDriverStatus = true;
                mAuth.signOut();

                LogoutDriver();
                DisconnectDriver();
            }
        });
        getAssignedCustomerRequest();
    }

    private void getAssignedCustomerRequest()
    {
        assignedCustomerRef =FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Drivers").child(driverID).child("CustomerRideID");

        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    customerID = snapshot.getValue().toString();

                    getAssignedCustomerPosition();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAssignedCustomerPosition()
    {
        AssignedCustomerPositionRef = FirebaseDatabase.getInstance().getReference().child("Customer Requests")
                .child(customerID).child("l");

        AssignedCustomerPositionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    List<Object> CustomerPositionMap = (List<Object>)  snapshot.getValue();
                    double LocationLat = 0;
                    double LocationLng = 0;

                    if (CustomerPositionMap.get(0) != null)
                    {
                        LocationLat = Double.parseDouble(CustomerPositionMap.get(0).toString());

                    }
                    if (CustomerPositionMap.get(1) != null)
                    {
                        LocationLng = Double.parseDouble(CustomerPositionMap.get(1).toString());

                    }
                    LatLng DriverLatLng = new LatLng(LocationLat, LocationLng);
                    mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Здесь вы заберете пассажира!"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        if(getApplicationContext()!=null)
        {
            lastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            String userID;
            //userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userID = FirebaseAuth.getInstance().getUid();
            DatabaseReference DriverAvalablityRef = FirebaseDatabase.getInstance().getReference().child("Driver Available");
            GeoFire geoFireAvailable = new GeoFire(DriverAvalablityRef);


            DatabaseReference driverWorkingRef = FirebaseDatabase.getInstance().getReference().child("Driver Working");
            GeoFire geoFireWorking = new GeoFire(DriverAvalablityRef);


            switch (customerID)
            {
                case "":
                    geoFireWorking.removeLocation(userID);
                    geoFireAvailable.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
                default:
                    geoFireAvailable.removeLocation(userID);
                    geoFireWorking.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
            }
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(!currentLogoutDriverStatus)
        {
            DisconnectDriver();

        }


    }

    private void DisconnectDriver()
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvalablityRef = FirebaseDatabase.getInstance().getReference().child("Driver Available");

        GeoFire geoFire = new GeoFire(DriverAvalablityRef);

        geoFire.removeLocation(userID);
    }

    private void LogoutDriver()
    {
        Intent welcomeIntent = new Intent(DriversMapActivity.this, WelcomeActivity.class);
        startActivity(welcomeIntent);
        finish();
    }
}