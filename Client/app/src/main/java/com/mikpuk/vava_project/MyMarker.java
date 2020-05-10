package com.mikpuk.vava_project;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.mikpuk.vava_project.data.Item;


public class MyMarker implements ClusterItem {


        private  LatLng mPosition;
        private  String mTitle;
        private  String mSnippet;
        private  int iconPicture;
        private Item item;


    public MyMarker(double lat, double lng)
    {
            mPosition = new LatLng(lat, lng);
    }

    public MyMarker(double lat, double lng, String title, String snippet, int av, Item it)
    {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
            iconPicture = av;
            item = it;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public Item getUser() {
        return item;
    }
}
