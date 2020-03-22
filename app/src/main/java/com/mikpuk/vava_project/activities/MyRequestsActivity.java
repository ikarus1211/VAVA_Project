package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.mikpuk.vava_project.MyReqItemAdapter;
import com.mikpuk.vava_project.Person;
import com.mikpuk.vava_project.R;

import java.util.ArrayList;
/*
    Class for displaying request that user created
 */
public class MyRequestsActivity extends AppCompatActivity {

    Button createReq = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_requests);

        ListView myLView = findViewById(R.id.reqListView);
        createReq = findViewById(R.id.createButton);
        createReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCreateReqWindow();
            }
        });


        MyReqItemAdapter adapter = new MyReqItemAdapter(this, R.layout.item_my_request, fillReqList());
        myLView.setAdapter(adapter);
    }

    private void loadCreateReqWindow()
    {
        Intent intent = new Intent(this, CreateMyRequestActivity.class);
        startActivity(intent);
    }
    private ArrayList<Person> fillReqList()
    {
        Person number1 = new Person("Jano", "Naj Diera", "je", "Prievidza");
        Person number2 = new Person("Onaj", "Kuko", "Horky", "ma Sliz");
        Person number3 = new Person("Corona", "China", "Virus", "Covid-19");
        ArrayList<Person> myList = new ArrayList<>();
        myList.add(number1);
        myList.add(number2);
        myList.add(number3);

        return myList;
    }
}
