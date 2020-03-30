package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mikpuk.vava_project.OtherReqItemAdapter;
import com.mikpuk.vava_project.Person;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.User;

import java.util.ArrayList;

import static com.mikpuk.vava_project.Constants.ERROR_DIALOG_REQUEST;
import static com.mikpuk.vava_project.Constants.LOCATION_PERM_CODE;
import static com.mikpuk.vava_project.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.mikpuk.vava_project.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MenuScreenActivity extends AppCompatActivity {

    Button myReqButton = null;
    Button acReqButton = null;
    Button mapButton = null;
    private boolean permissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLocation;
    private User user = null;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);
        ListView myLView = findViewById(R.id.lisView);

        user = (User)getIntent().getSerializableExtra("user");

        myReqButton = findViewById(R.id.myReqButton);
        acReqButton = findViewById(R.id.acceptButton);
        mapButton = findViewById(R.id.mapbutton);

        getLocationPermission();

        myReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMyReqUi();
            }
        });



/*
       Moje vlastne testovanie
 */
        Person number1 = new Person("David", "Nadlan", "Pero", "Moje Modre azurove pero");
        Person number2 = new Person("Divad", "Rajcan", "Rajciak", "Chybas mi");
        Person number3 = new Person("Peter", "Topik", "Alkohol", "Dopekla");
        Person number4 = new Person("Retep", "Batovany", "Pero", "Kybel maciek");
        Person number5 = new Person("Jano", "Naj Diera", "je", "Prievidza");
        Person number6 = new Person("Onaj", "Kuko", "Horky", "ma Sliz");
        Person number7 = new Person("Corona", "China", "Virus", "Covid-19");

        ArrayList<Person> myList = new ArrayList<>();
        myList.add(number1);
        myList.add(number2);
        myList.add(number3);
        myList.add(number4);
        myList.add(number5);
        myList.add(number6);
        myList.add(number7);

        OtherReqItemAdapter adapter = new OtherReqItemAdapter(this, R.layout.item_main_menu, myList);
        myLView.setAdapter(adapter);
    }


    /*
     * Function checks permissions and then initialize the button form mapView
     */
    private void init()
    {
        System.out.println("Everything ok");
        if (permissionGranted)
        {
            getDeviceLocation();
        }
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuScreenActivity.this, MapViewActivity.class );
                intent.putExtra("location", mLocation);
                startActivity(intent);
            }
        });
    }


    private void loadMyReqUi()
    {
        Intent intent = new Intent(this, MyRequestsActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }


    /*
     * Function gets location of device and stores it into mLocation variable
     */
    private void getDeviceLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {

            // If permission is granted continue
            if(permissionGranted)
            {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            System.out.println("Found it");
                            mLocation = (Location) task.getResult();
                        }
                        else
                        {
                            System.out.println("Not found it");
                        }
                    }
                });
            }
        } catch (SecurityException e)
        {
            System.out.println("Exception");
        }

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

}
