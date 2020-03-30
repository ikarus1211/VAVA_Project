package com.mikpuk.vava_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/*
   This is custom adapter for list view.
   It creates one block of different textViews witch is than taken to create list view.
 */
public class OtherReqItemAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private int mResource;
    private String userName;

    public OtherReqItemAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects, @NonNull String name) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.userName = name;
        System.out.println("VYKONAVAM 1");
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //String adress = getItem(position).getAdress();
        System.out.println("VYKONAVAM 2");
        String itemName = getItem(position).getName();
        String description = getItem(position).getDescription();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textName = (TextView) convertView.findViewById(R.id.listNameX);
        TextView textItemName = (TextView) convertView.findViewById(R.id.listItemX);
        //TextView textAdress = (TextView) convertView.findViewById(R.id.listAdress);
        TextView textDes = (TextView) convertView.findViewById(R.id.listDesX);

        if(textName == null)
            System.out.println("null text");
        if(userName == null)
            System.out.println("null user");

        textName.setText(userName);
        //textAdress.setText(adress);
        textDes.setText(description);
        textItemName.setText(itemName);

        return convertView;

    }


}
