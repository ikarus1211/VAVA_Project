package com.mikpuk.vava_project;


import android.Manifest;
import android.content.Context;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;


import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AppLocationManager implements LocationListener {

    private Location mLocation = null;
    private Context mContext;

    public AppLocationManager(Context context) {
        mContext = context;
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                System.out.println("You dont have permissions");
            }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1,
                0, this);
        setMostRecentLocation(locationManager.getLastKnownLocation(provider));
        getDeviceLocation();

    }

    private void getDeviceLocation()
    {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        try {
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

        } catch (SecurityException e)
        {
            System.out.println("Exception in getDeviceLocation()");
        }

    }


    private void setMostRecentLocation(Location lastKnownLocation) {
        System.out.println("Heeeeeeeereeeeeeeee");
        System.out.println(lastKnownLocation);
        mLocation = lastKnownLocation;


    }
    public String generateAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        String finalAddress = null;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            finalAddress = addresses.get(0).getAddressLine(0);
            System.out.println("Final address " + finalAddress);
        } catch (Exception e) {
            System.out.println("Exception in generateAdress()");
        }
        return finalAddress;

    }

    public String generateAddress(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        String finalAddress = null;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        if (lat == 0 && lon == 0)
            return "Address not given";
        try {
            addresses = geocoder.getFromLocation(lat,lon, 1);
            finalAddress = addresses.get(0).getAddressLine(0);
            System.out.println("Final address " + finalAddress);
        } catch (IOException e) {
            System.out.println("IO Excepltion");
        }
        return finalAddress;

    }

    //DOCASNE RIESENI!!! Aby som dokazal pridat ziadost
    public double getLatitude() {
        if(mLocation == null)
            return 0.0;
        else
            return mLocation.getLatitude();
    }
    public double getLongitude() {
        if(mLocation == null)
            return 0.00;
        else
            return mLocation.getLongitude();
    }
    public Location getmLocation()
    {
        return mLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lon = (double) (location.getLongitude());/// * 1E6);
        double lat = (double) (location.getLatitude());// * 1E6);

        String latitude = lat + "";
        String longitude = lon + "";

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderDisabled(String arg0) {
    }
    @Override
    public void onProviderEnabled(String arg0) {
    }



}