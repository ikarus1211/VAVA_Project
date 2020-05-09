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

import java.util.List;
import java.util.Random;

/*
   This is custom adapter for list view.
   It creates one block of different textViews witch is than taken to create list view.
 */
public class OtherReqItemAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private int mResource;

    public OtherReqItemAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        System.out.println("VYKONAVAM 1");
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AppLocationManager appLocationManager = new AppLocationManager(mContext);

        String adress = appLocationManager.generateAddress(getItem(position).getLatitude(), getItem(position).getLongtitude());
        System.out.println("VYKONAVAM 2");
        String itemName = getItem(position).getName();
        String description = getItem(position).getDescription();
        //String userName = getItem(position).getUser_name();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textDistance = (TextView) convertView.findViewById(R.id.myReqDistance);
        TextView textItemName = (TextView) convertView.findViewById(R.id.listItemX);
        TextView textAdress = (TextView) convertView.findViewById(R.id.listAdress);
        TextView openProfile = (TextView) convertView.findViewById(R.id.popTxtInfo);
        ImageView imageView = convertView.findViewById(R.id.imageView);



        BackGrounPicker bp = new BackGrounPicker();
        bp.randomBackground(textDistance);
        imageView.setImageResource((int)getItem(position).getType_id());

        textAdress.setText(adress);

        textItemName.setText(itemName);

        return convertView;

    }


}
