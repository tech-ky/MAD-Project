package com.sp.mad_project.placeholder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.sp.mad_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    private static final String API_KEY = "AIzaSyAcx4LuaXA5YsbHtjOf9G6oG3wfk3TyvWY";
    private static final int FINE_PERMISSION_CODE = 1;
    private GoogleMap googleMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Fetch the last known location
        fetchLastLocation();

        // Set up the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up the go-back button
        ImageButton goBackButton = findViewById(R.id.gomorefragment2);
        goBackButton.setOnClickListener(view -> finish());
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                // Initialize the map fragment once location is obtained
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(Map.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.d("MapActivity", "onMapReady: Google Map is ready");

        if (currentLocation != null) {
            LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("My Location"))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            findNearbyGyms(currentLocation.getLatitude(),currentLocation.getLongitude());
            Toast.makeText(this, "Location set on map!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Waiting for location...", Toast.LENGTH_SHORT).show();
        }

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void findNearbyGyms(double latitude, double longtitude) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                latitude + "," + longtitude + "&radius=5000&type=gym&key=" + API_KEY;


        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");


                    for (int i = 0; i < results.length(); i++) {
                        JSONObject place = results.getJSONObject(i);
                        JSONObject location = place.getJSONObject("geometry").getJSONObject("location");

                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");
                        String name = place.getString("name");


                        LatLng gymLocation = new LatLng(lat, lng);
                        googleMap.addMarker(new MarkerOptions().position(gymLocation).title(name));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GymFinder", "Volley Error: " + error.getMessage()); // Log Volley errors
                Toast.makeText(Map.this, "Error fetching nearby gyms", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied. App may not function as intended.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
