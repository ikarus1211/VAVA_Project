package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikpuk.vava_project.OtherReqItemAdapter;
import com.mikpuk.vava_project.Person;
import com.mikpuk.vava_project.R;

import java.util.ArrayList;

import static com.mikpuk.vava_project.Constants.ERROR_DIALOG_REQUEST;
import static com.mikpuk.vava_project.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.mikpuk.vava_project.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MenuScreenActivity extends AppCompatActivity {

    Button myReqButton = null;
    Button acReqButton = null;
    Button mapButton = null;
    private boolean mLocationPermissionGranted = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);
        ListView myLView = findViewById(R.id.lisView);

        myReqButton = findViewById(R.id.myReqButton);
        acReqButton = findViewById(R.id.accpetButton);
        mapButton = findViewById(R.id.mapbutton);

        myReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMyReqUi();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkMapServices()){
                    if(mLocationPermissionGranted){
                        loadMapUI();
                    }
                    else{
                        getLocationPermission();
                    }
                }

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

    private void loadMyReqUi()
    {
        Intent intent = new Intent(this, MyRequestsActivity.class);
        startActivity(intent);
    }

    private void loadMapUI()
    {
        Intent intent = new Intent(this, MapViewActivity.class);
        startActivity(intent);
    }

    /*
    Checks if google services are enabled
 */
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MenuScreenActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MenuScreenActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    /*
        If GPS is not enabled this will ask to do so
     */
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /*
           Checks if GPS is enabled
     */
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    loadMapUI();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    loadMapUI();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
