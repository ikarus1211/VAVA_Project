package com.mikpuk.vava_project;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mikpuk.vava_project.data.Item;

import java.util.List;

public class MyReqItemAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private int mResource;
    private ImageView popUpButton;


    public MyReqItemAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {

        AppLocationManager appLocationManager = new AppLocationManager(mContext);

        System.out.println("---------- "+getItem(position).getLatitude()+" | "+getItem(position).getLongtitude());
        String adress = appLocationManager.generateAddress(getItem(position).getLatitude(), getItem(position).getLongtitude());
        String itemName = getItem(position).getName();
        String description = getItem(position).getDescription();
        String userName = getItem(position).getUser().getUsername();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);


        TextView textDistance = (TextView) convertView.findViewById(R.id.myReqDistance);
        TextView textItemName = (TextView) convertView.findViewById(R.id.myListItem);
        TextView textAdress = (TextView) convertView.findViewById(R.id.myListAdress);
        BackGrounPicker bp = new BackGrounPicker();
        bp.randomBackground(textDistance);


        textAdress.setText(adress);
        textItemName.setText(itemName);

        return convertView;

    }



}
