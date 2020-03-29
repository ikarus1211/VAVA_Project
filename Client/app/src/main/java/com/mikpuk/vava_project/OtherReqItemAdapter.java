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

/*
   This is custom adapter for list view.
   It creates one block of tifferent textViews witch is than taken to create list view.
 */
public class OtherReqItemAdapter extends ArrayAdapter<Person> {

    private Context mContext;
    private int mResource;

    public OtherReqItemAdapter(@NonNull Context context, int resource, @NonNull List<Person> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String adress = getItem(position).getAdress();
        String itemName = getItem(position).getItemName();
        String description = getItem(position).getDescription();

        Person person = new Person(name, adress, itemName, description);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textName = (TextView) convertView.findViewById(R.id.listName);
        TextView textItemName = (TextView) convertView.findViewById(R.id.listItem);
        TextView textAdress = (TextView) convertView.findViewById(R.id.listAdress);
        TextView textDes = (TextView) convertView.findViewById(R.id.listDes);

        textName.setText(name);
        textAdress.setText(adress);
        textDes.setText(description);
        textItemName.setText(itemName);

        return convertView;

    }


}
