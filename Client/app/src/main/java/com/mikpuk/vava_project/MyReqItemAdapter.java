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

public class MyReqItemAdapter extends ArrayAdapter<Person> {

    private Context mContext;
    private int mResource;

    public MyReqItemAdapter(@NonNull Context context, int resource, @NonNull List<Person> objects) {
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

        TextView textName = (TextView) convertView.findViewById(R.id.myListName);
        TextView textItemName = (TextView) convertView.findViewById(R.id.myListItem);
        TextView textAdress = (TextView) convertView.findViewById(R.id.myListAdress);
        TextView textDes = (TextView) convertView.findViewById(R.id.myListDes);

        textName.setText(name);
        textAdress.setText(adress);
        textDes.setText(description);
        textItemName.setText(itemName);

        return convertView;

    }
}