package com.diplomatiki.krikonis.rangefinder.app.app;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.diplomatiki.krikonis.rangefinder.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = MapsActivity.class.getSimpleName();
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    public TextView txtName;
    private TextView address;
    private TextView inputradius;
    private Button btnLogout;
    private Button btnGetLoc;
    private Button btnGetUsers;
    private SQLiteHandler db;
    private SessionManager session;
    private RatingBar ratingBar;
    String username;
    String useremail;
    String useraddress;
    public double currentLatitude;
    public double currentLongitude;
    private TextView txtResponse;

    // temporary string to show the parsed response
    private String jsonResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        address = (TextView) findViewById(R.id.proaddress);
        inputradius  = (EditText) findViewById(R.id.radius);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnGetLoc = (Button) findViewById(R.id.btngetloc);
        btnGetUsers = (Button) findViewById(R.id.btn_showusers);

        getSupportActionBar().setTitle("Map Location Activity");
        addListenerOnRatingBar();
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        // Check for enabled GPS
        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Location Access");
            builder.setMessage("Please enable GPS");
            builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(
                            new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
       // txtName = (TextView) findViewById(R.id.name);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        username = name;
        useremail = email;
        // Displaying the user details on the screen
     //   txtName.setText(name);
        //address.setText("You are located at: " + getCompleteAddressString(currentLatitude,currentLongitude));

        // Logout button click event
        btnGetUsers.setOnClickListener(new View.OnClickListener() {

            //int radiusinkm = Integer.parseInt(radius);
            @Override
            public void onClick(View view) {
                final String radius = inputradius.getText().toString().trim();
                if (currentLatitude != 0 && currentLongitude != 0 && radius != null) {
                    // Toast.makeText(getApplicationContext(), useraddress, Toast.LENGTH_LONG).show();
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http")
                            .encodedAuthority("arvitis.ddns.net:62222")
                            .appendPath("android_login_api")
                            .appendPath("getprousers.php")
                            .appendQueryParameter("radius", radius)
                            .appendQueryParameter("Long", String.valueOf(currentLongitude))
                            .appendQueryParameter("Lat", String.valueOf(currentLatitude));
                           // .appendQueryParameter("Rating", String.valueOf(ratingBar.getRating()));
                    String URL_UPDATE = builder.build().toString();
                    JsonObjectRequest strReq = new JsonObjectRequest(
                            Request.Method.POST,
                            URL_UPDATE,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, "Get users Response: " + response.toString());
                                  //  Toast.makeText(getApplicationContext(), "volley response: " + response.toString(), Toast.LENGTH_LONG).show();
                                    try {
                                        JSONArray users = response.getJSONArray("users");
                                        for (int i = 0; i < users.length(); i++) {
                                            JSONObject user = users.getJSONObject(i);
                                            String name = user.getString("name");
                                            // Do you fancy stuff
                                            // Example: String gifUrl = jo.getString("url");
                                            Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();



                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(),
                                                "Error: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, "Update Error: " + error.getMessage());
                                    Toast.makeText(getApplicationContext(),
                                            error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }) ;
                    VolleyController.getInstance(getApplicationContext()).addToRequestQueue(strReq);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Coordinates or radius are empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        //useraddress = getCompleteAddressString(currentLatitude,currentLongitude);
        btnGetLoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (currentLatitude != 0 && currentLongitude != 0) {
                   // Toast.makeText(getApplicationContext(), useraddress, Toast.LENGTH_LONG).show();
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http")
                            .encodedAuthority("arvitis.ddns.net:62222")
                            .appendPath("android_login_api")
                            .appendPath("Update.php")
                            .appendQueryParameter("email", useremail)
                            .appendQueryParameter("Long", String.valueOf(currentLongitude))
                            .appendQueryParameter("Lat", String.valueOf(currentLatitude))
                            .appendQueryParameter("Rating", String.valueOf(ratingBar.getRating()));
                    String URL_UPDATE = builder.build().toString();
                    JsonObjectRequest strReq = new JsonObjectRequest(
                            Request.Method.POST,
                            URL_UPDATE,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, "Update Response: " + response.toString());
                                    Toast.makeText(getApplicationContext(), "Update successful!!!" , Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, "Update Error: " + error.getMessage());
                                    Toast.makeText(getApplicationContext(),
                                            error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }) ;
                    VolleyController.getInstance(getApplicationContext()).addToRequestQueue(strReq);
            }
            else {
                    Toast.makeText(getApplicationContext(), "Coordinates are empty", Toast.LENGTH_LONG).show();
                }}
        });



    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
       // mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        if (currentLatitude != 0 && currentLongitude != 0) {
            double LcurrentLatitude = location.getLatitude();
            double LcurrentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("You are here!")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mGoogleMap.addMarker(options);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            address.setText("You are located at (onconnected): " + getCompleteAddressString(LcurrentLatitude, LcurrentLongitude));
        } else {
            // Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // if (location == null)
            address.setText("Location not available (onconnected)!");
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        MarkerOptions markerOptions = new MarkerOptions()
            .position(latLng)
            .title("You are here!")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
        address.setText("You are located at (onlocationchanged): " + getCompleteAddressString(currentLatitude,currentLongitude));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
               // Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
                //Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
           // Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }
    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        //txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Toast.makeText(getApplicationContext(), String.valueOf(v), Toast.LENGTH_LONG).show();
            }

        });
    }

}
