package com.mikpuk.vava_project.activities;


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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;



import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AppLocationManager implements LocationListener {

    private LocationManager locationManager;
    private Location mLocation = null;
    private String latitude;
    private String longitude;
    private Criteria criteria;
    private String provider;
    private Context mContext;

    public AppLocationManager(Context context) {
        mContext = context;
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                System.out.println("You dont have permissions");
            }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1,
                0, this);
        setMostRecentLocation(locationManager.getLastKnownLocation(provider));

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
        } catch (IOException e) {
            System.out.println("IO Excepltion");
        }
        return finalAddress;

    }

    public String generateAddress(float lat, float lon) {
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

    public double getLatitude() {
        return mLocation.getLatitude();
    }

    public double getLongitude() {
        return mLocation.getLongitude();
    }
    public Location getmLocation()
    {
        return mLocation;
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * android.location.LocationListener#onLocationChanged(android.location.
     * Location)
     */
    @Override
    public void onLocationChanged(Location location) {
        double lon = (double) (location.getLongitude());/// * 1E6);
        double lat = (double) (location.getLatitude());// * 1E6);

        latitude = lat + "";
        longitude = lon + "";

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see android.location.LocationListener#onStatusChanged(java.lang.String,
     * int, android.os.Bundle)
     */
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

}