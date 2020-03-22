package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikpuk.vava_project.R;

import static com.mikpuk.vava_project.Constants.MAPVIEW_BUNDLE_KEY;


/*
https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/main/java/com/example/mapdemo/RawMapViewDemoActivity.java
 */
    public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

        private MapView mMapView;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_map_view);

            // *** IMPORTANT ***
            // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
            // objects or sub-Bundles.
            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            }
            mMapView = (MapView) findViewById(R.id.mapView);
            mMapView.onCreate(mapViewBundle);

            mMapView.getMapAsync(this);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
            if (mapViewBundle == null) {
                mapViewBundle = new Bundle();
                outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
            }

            mMapView.onSaveInstanceState(mapViewBundle);
        }

        @Override
        public void onResume() {
            super.onResume();
            mMapView.onResume();
        }

        @Override
        public void onStart() {
            super.onStart();
            mMapView.onStart();
        }

        @Override
        public void onStop() {
            super.onStop();
            mMapView.onStop();
        }

        @Override
        public void onMapReady(GoogleMap map) {
            map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            map.setMyLocationEnabled(true);
        }

        @Override
        public void onPause() {
            mMapView.onPause();
            super.onPause();
        }

        @Override
        public void onDestroy() {
            mMapView.onDestroy();
            super.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mMapView.onLowMemory();
        }
}
