package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.OtherReqItemAdapter;
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
import java.util.List;

import static com.mikpuk.vava_project.Constants.LOCATION_PERM_CODE;

public class MenuScreenActivity extends AppCompatActivity {

    Button myReqButton = null;
    Button acReqButton = null;
    Button mapButton = null;
    private boolean permissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLocation;
    private User user = null;
    ListView myLView=null;
    private AppLocationManager appLocationManager;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);
        System.out.println("VYKONAM 4");
        myLView = findViewById(R.id.lisView);
        System.out.println("VYKONAM 5");

        user = (User)getIntent().getSerializableExtra("user");

        myReqButton = findViewById(R.id.myReqButton);
        acReqButton = findViewById(R.id.acceptButton);
        mapButton = findViewById(R.id.mapbutton);
        getGpsStatus();
        getLocationPermission();



        myReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMyReqUi();
            }
        });

        //Spusta nacitanie listView
       AsyncOtherItemsGetter getter = new AsyncOtherItemsGetter();
        getter.execute();

    }

    private void getGpsStatus()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    // TODO maybe rework
    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    private void fillMyRequestsList(Item[] items)
    {
        List<Item> itemList = new ArrayList<>();
        for (Item item:items
        ) {
            itemList.add(item);
        }

        System.out.println("VYKONAM 6");
        final  OtherReqItemAdapter adapter = new OtherReqItemAdapter(this, R.layout.item_main_menu, itemList,user.getUsername());

        runOnUiThread(new Runnable() {
            public void run() {
                myLView.setAdapter(adapter);
            }
        });
    }




    /*
     * Function checks permissions and then initialize the button form mapView
     */
    private void init()
    {
        System.out.println("Everything ok");
        if (permissionGranted) {

            appLocationManager = new AppLocationManager(MenuScreenActivity.this);
            mLocation = appLocationManager.getmLocation();
            System.out.println(appLocationManager.getmLocation());
            System.out.println(appLocationManager.generateAddress());

            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLocation = appLocationManager.getmLocation();
                    System.out.println(appLocationManager.getmLocation());
                    System.out.println(appLocationManager.generateAddress());

                    if(mLocation == null) {
                        showToast("Location not set");
                        return;
                    }
                    Intent intent = new Intent(MenuScreenActivity.this, MapViewActivity.class);
                    intent.putExtra("location", mLocation);
                    startActivity(intent);
                }
            });
        }
    }


    private void loadMyReqUi()
    {
        Intent intent = new Intent(this, MyRequestsActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }




    /*
     * Function which checks if the permissions were granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        permissionGranted = false;
        System.out.println("4");
        switch (requestCode)
        {
            case LOCATION_PERM_CODE:
            {
                if (grantResults.length > 0)
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    init();
                    System.out.print("GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                }
            }
        }

    }



    @Override
    protected void onResume() {
        super.onResume();

    }


    /*
     * Request location permission, so that we can get the location of the
     * device.
     */
    private void getLocationPermission() {

        String[] premissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)

        {
            System.out.println("1");
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                permissionGranted = true;
                init();
            }
            else
            {
                System.out.println("2");
                ActivityCompat.requestPermissions(this, premissions, LOCATION_PERM_CODE );
            }
        }
        else
        {
            System.out.println("3");
            ActivityCompat.requestPermissions(this, premissions, LOCATION_PERM_CODE );
        }

    }


    class AsyncOtherItemsGetter extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/getotheritems/{id}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                Item[] items = restTemplate.exchange(uri, HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders), Item[].class,user.getId()).getBody();

                showToast("ITEMS LOADED!");
                fillMyRequestsList(items);

            } catch (HttpServerErrorException e)
            {
                //Error v pripade chyby servera
                System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
                showToast("SERVER ERROR "+e.getStatusCode());
            } catch (HttpClientErrorException e2)
            {
                //Error v pripade ziadosti klienka
                System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
                e2.printStackTrace();
                showToast("CLIENT ERROR "+e2.getStatusCode());
            } catch (Exception e3)
            {
                e3.printStackTrace();
                showToast("SOMETHING WENT WRONG");
            }

            return null;
        }

    }

    //Toto vyhodi bublinu s infom - len pre nas
    private void showToast(final String text)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
