package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.MyClusterRender;
import com.mikpuk.vava_project.MyMarker;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static com.mikpuk.vava_project.Constants.MAPVIEW_BUNDLE_KEY;


/*
https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/main/java/com/example/mapdemo/RawMapViewDemoActivity.java
 */
    public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private Location mLocation;
        private ClusterManager mClusterManager;
        private MyClusterRender mClusterRender;
        private ArrayList<MyMarker> mClusterMarkers = new ArrayList<>();
        private User user;
        private static final String TAG = "Map Activity";
        @Override

        protected void onCreate(Bundle savedInstanceState) {

            HyperLog.i(TAG, "Map view activity");
            user = (User)getIntent().getSerializableExtra("user");
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
            HyperLog.i(TAG, "Map is ready");
            Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            mLocation = intent.getParcelableExtra("location");
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),15f));
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            new AsyncOtherItemsMapGetter().execute(user.getId(),(long) 0,(long) 100);
    }

    private void addMapMarkers(Item[] items){

        if(mMap != null){

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<MyMarker>(getApplicationContext(), mMap);
                mClusterManager.setAnimation(true);

            }
            if(mClusterRender == null){
                mClusterRender = new MyClusterRender(
                       this,
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterRender);
            }

            for(Item userItem: items){


                try{
                    String snippet = "";
                    snippet = userItem.getDescription();

                    int avatar = (int)userItem.getType_id();


                    MyMarker newClusterMarker = new MyMarker(
                            userItem.getLatitude(),
                            userItem.getLongtitude(),
                            userItem.getName(),
                            snippet,
                            avatar,
                            userItem
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                }
                catch (NullPointerException e)
                {
                    HyperLog.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }

            }
            mClusterManager.cluster();

            //setCameraView();
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        MapViewActivity.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
    }

    class AsyncOtherItemsMapGetter extends AsyncTask<Long,Void,Void>
    {
        Item[] items;

        @Override
        protected Void doInBackground(Long... args) {
            Long user_id = args[0];
            Long limit_start = args[1];
            Long limit_end = args[2];

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/getotheritems/limit/{id}/{limit_start}/{limit_end}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                items = restTemplate.exchange(uri, HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders), Item[].class,user_id,limit_start,limit_end).getBody();

            } catch (HttpServerErrorException e)
            {
                //Error v pripade chyby servera
               HyperLog.e(TAG,"Server exception ", e);

            }
            catch (HttpClientErrorException e2)
            {
                //Error v pripade ziadosti klienka
                HyperLog.e(TAG,"Clients exception", e2);

            }
            catch (Exception e3)
            {
                HyperLog.e(TAG,"Unknown exception", e3);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            addMapMarkers(items);
        }

    }

}
