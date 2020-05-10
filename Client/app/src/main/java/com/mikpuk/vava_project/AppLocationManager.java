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
import com.hypertrack.hyperlog.HyperLog;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This class controls everything connected to GPS
 * Through this class location and address can be obtained
 */
public class AppLocationManager implements LocationListener {

    private Location mLocation = null;
    private Context mContext;
    private static final String TAG = "Location Manager";

    public AppLocationManager(Context context) {
        loadLocationManager(context);
    }

    /**
     * Initializing location manager, checking permissions and
     * getting device location
     *
     * @param context current state of application
     */
    private void loadLocationManager(Context context) {
        HyperLog.i(TAG,"Initializing location manager");
        mContext = context;

        // Base initialization
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);

        // Checking for permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                HyperLog.w(TAG,"Permission were not granted");
            }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1,
                0, this);
        setMostRecentLocation(locationManager.getLastKnownLocation(provider));
        getDeviceLocation();
    }

    /**
     * Getting Device location and setting global variable mLocation to it
     */
    private void getDeviceLocation()
    {
        HyperLog.i(TAG,"Getting device location");
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
                            HyperLog.i(TAG,"Device location found"+mLocation);
                        }
                        else
                        {
                            HyperLog.i(TAG,"Device location not found");
                            System.out.println("Not found it");
                        }
                    }
                });

        } catch (SecurityException e)
        {
            HyperLog.e(TAG,"Exception in location getting",e);
            System.out.println("Exception in getDeviceLocation()");
        }

    }

    /**
     * This function is called in initialization and set the last known location in case
     * of error while setting current location
     * @param lastKnownLocation last known location
     */
    private void setMostRecentLocation(Location lastKnownLocation) {
        mLocation = lastKnownLocation;
    }

    public String generateAddress() {
        HyperLog.i(TAG,"Generating address");
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

    /**
     * Generate address based on latitude and longitude
     * @param lat latitude of location that you want to generate address to
     * @param lon longitude of location that you want to generate address to
     * @return returns String which represents address
     */
    public String generateAddress(double lat, double lon) {
        HyperLog.i(TAG,"Generating address");
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

    /**
     * Function gets and return latitude if mLocation is null returns 0.0 coordinates
     * @return double which represents latitude
     */
    public double getLatitude() {
        if(mLocation == null)
            return 0.0;
        else
            return mLocation.getLatitude();
    }
    /**
     * Function gets and return longitude if mLocation is null returns 0.0 coordinates
     * @return double which represents longitude
     */
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

    /**
     * this method is called when user changes his location thus rewriting mLocation
     * @param location current location
     */
    @Override
    public void onLocationChanged(Location location) {
        double lon = (double) (location.getLongitude());
        double lat = (double) (location.getLatitude());

        String latitude = lat + "";
        String longitude = lon + "";

        mLocation.setLongitude(lon);
        mLocation.setLatitude(lat);
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