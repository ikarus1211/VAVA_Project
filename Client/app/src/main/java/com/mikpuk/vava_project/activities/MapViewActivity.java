package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikpuk.vava_project.R;

import static com.mikpuk.vava_project.Constants.MAPVIEW_BUNDLE_KEY;


/*
https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/main/java/com/example/mapdemo/RawMapViewDemoActivity.java
 */
    public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private Location mLocation;


        @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_map_view);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);

            mapFragment.getMapAsync(MapViewActivity.this);


        }
    /*
     * Sets Map camera onto user
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
            Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            mLocation = intent.getParcelableExtra("location");
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),15f));
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
    }



}
