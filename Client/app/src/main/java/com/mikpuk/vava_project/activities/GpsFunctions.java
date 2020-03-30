package com.mikpuk.vava_project.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import static com.mikpuk.vava_project.Constants.LOCATION_PERM_CODE;

public class GpsFunctions extends AppCompatActivity
{

    private Location mLocation = null;
    private boolean permissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getDeviceLocation();

    }


    /*
     * Function gets location of device and stores it into mLocation variable
     */
    protected Location getDeviceLocation()
    {
        System.out.println("Ffefefe");
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        System.out.println("bbbbbbbbbb");
        if (permissionGranted)
        {
            try {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            System.out.println("Found it");
                            mLocation = (Location) task.getResult();
                        } else {
                            System.out.println("Not found it");
                        }
                    }
                });
            } catch (SecurityException e) {
                System.out.println("Exception");
            }
            return mLocation;
        }
        else
        {
            System.out.println("GPS Permissions revoked");
            return null;
        }
    }

    protected String generateAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        String finalAdress = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        mLocation = getDeviceLocation();
        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            finalAdress = addresses.get(0).getAddressLine(0);
            System.out.println("Final address " + finalAdress);
        } catch (IOException e) {
            System.out.println("IO Excepltion");
        }
        return finalAdress;

    }


}
