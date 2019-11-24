package com.example.jarvis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;

    private String myemail;
    private GoogleSignInAccount acct;

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    private Double prevLat;
    private Double prevLon;
    private boolean refLocation = false;

    Toolbar mMapToolbar;
    private String eventId;




    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"onMapReady: Map is ready");
        mMap = googleMap;

//        mMap.setMinZoomPreference(1f);
//        mMap.setMaxZoomPreference(15f);

        if(mLocationPermissionsGranted) {
            //mHandlerTask.run();
            //getDeviceLocation();
            refLocation = true;
            new getLocationOfUsers().execute();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

        }

        if (eventId.equals("")) {
            Toast.makeText(MapActivity.this, "Could not find specified event",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId"); //if id not found cannot find other use locations
        Log.d("MapActivity", eventId);

        acct = GoogleSignIn.getLastSignedInAccount(this);
        myemail = acct.getEmail();

        prevLat = 0.0;
        prevLon = 0.0;

        mMapToolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(mMapToolbar);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || !refLocation) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    //Toast.makeText(MapActivity.this,"Lat: " + location.getLatitude() + " Lon: " + location.getLongitude(),Toast.LENGTH_SHORT).show();

                    if (prevLat != location.getLatitude() || prevLon != location.getLongitude()) { //update status only if location has changed
                        new updateLocation(Double.toString(location.getLatitude()), Double.toString(location.getLongitude())).execute(); //update server

                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM); //update UI
                        prevLat = location.getLatitude();
                        prevLon = location.getLongitude();
                    }
                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();
        getLocationPermission();
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient;

        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete:found location!");
                            Location currentLocation = (Location) task.getResult();
                            new updateLocation(Double.toString(currentLocation.getLatitude()),Double.toString(currentLocation.getLongitude())).execute();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM);
                        }
                        else {
                            Log.d(TAG, "onComplete:current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng:" + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this); //goes to onMapReady

    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this,
                    permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize map
                    initMap();
                }
                break;
            }
            default: {
                Toast.makeText(MapActivity.this, "Could not get location", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onRequestPermissionsResult: permission denied");
                Intent intent = new Intent(MapActivity.this, Home.class);
                startActivity(intent);
            }

        }
    }

    private class updateLocation extends AsyncTask<Void, Void, Void> {

        String lat;
        String lon;

        updateLocation(String lat, String lon) {
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        protected Void doInBackground(Void... v) {
            Log.d("updateLocation", "running doInBackground");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + acct.getEmail() + "/location");

                JSONObject json = new JSONObject();
                json.put("lat", lat);
                json.put("lon", lon);

                httpPut.setEntity(new StringEntity(json.toString()));
                httpPut.setHeader("Authorization", "Bearer " + acct.getIdToken());
                httpPut.addHeader("Content-Type", "application/json");

                HttpResponse response = httpClient.execute(httpPut);
                final String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject jsonObj = new JSONObject(responseBody);
                String status = jsonObj.getString("status");
                Log.i("Information", "Location update was " + status);
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return null;
        }


    }

    private class getLocationOfUsers extends AsyncTask<Void, Void, JSONArray> {
        boolean early = false;

        @Override
        protected JSONArray doInBackground(Void... v) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            JSONArray jsonArray = new JSONArray();
            HttpGet httpGet = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/events/" + eventId + "/locations");
            try {
                httpGet.addHeader("Authorization", "Bearer " + acct.getIdToken());
                httpGet.addHeader("Content-Type", "application/json");
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String json_string = EntityUtils.toString(httpEntity);
                Log.d("MapActivity", "json_string: " + json_string);
                JSONObject jsonObject = new JSONObject(json_string);
                if(jsonObject.getString("error").equals("event time not close")) {
                    early = true;
                }
                else {
                    jsonArray = jsonObject.getJSONArray("locations");
                }
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            JSONObject cur;
            if (early) {Toast.makeText(MapActivity.this, "Too Early to get other people's locations!", Toast.LENGTH_LONG).show();}
            else if(jsonArray == null || jsonArray.length() == 0){
                Toast.makeText(MapActivity.this, "Couldn't get location", Toast.LENGTH_LONG).show();
            }
            else{
                Log.d(TAG, "jsonArray: " + jsonArray);
                for(int index = 0; index < jsonArray.length(); index++){
                    try{
                        cur = jsonArray.getJSONObject(index);
                        LatLng userLocation = new LatLng(Double.parseDouble(cur.getString("lat")), Double.parseDouble(cur.getString("lon")));
                        mMap.addMarker(new MarkerOptions().position(userLocation)
                                .title(cur.getString("user")));
                        Log.d(TAG, "jsonobj: " + cur.getString("summary"));
                    }catch(JSONException e){
                        e.printStackTrace();
                        Log.e(TAG, "Something wrong with retrieved JSONObject", e);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            mMap.clear();
            new getLocationOfUsers().execute();
            //Toast.makeText(MapActivity.this, "Refreshing Locations of Users", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationPermissionsGranted) {
            startLocationUpdates();
            Log.d("onResume", "am i working??");
        }
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mMap.clear();
    }
}
