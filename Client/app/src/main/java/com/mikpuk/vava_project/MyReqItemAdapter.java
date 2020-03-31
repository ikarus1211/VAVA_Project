package com.mikpuk.vava_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MyReqItemAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private int mResource;

    public MyReqItemAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AppLocationManager appLocationManager = new AppLocationManager(mContext);

        System.out.println("---------- "+getItem(position).getLatitude()+" | "+getItem(position).getLongtitude());

        String adress = appLocationManager.generateAddress(getItem(position).getLatitude(), getItem(position).getLongtitude());
        String itemName = getItem(position).getName();
        String description = getItem(position).getDescription();
        String userName = getItem(position).getUser_name();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textName = (TextView) convertView.findViewById(R.id.myListName);
        TextView textItemName = (TextView) convertView.findViewById(R.id.myListItem);
        TextView textAdress = (TextView) convertView.findViewById(R.id.myListAdress);
        TextView textDes = (TextView) convertView.findViewById(R.id.myListDes);

        textName.setText(userName);
        textAdress.setText(adress);
        textDes.setText(description);
        textItemName.setText(itemName);

        return convertView;

    }
}
