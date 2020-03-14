package com.mikpuk.vava_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
/*
    Class for displaying request that user created
 */
public class UsersRequest extends AppCompatActivity {

    Button createReq = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_request);

        ListView myLView = findViewById(R.id.reqListView);
        createReq = findViewById(R.id.createButton);
        createReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatingRequest();
            }
        });


        MyReqItemAdapter adapter = new MyReqItemAdapter(this, R.layout.my_requests_view, fillReqList());
        myLView.setAdapter(adapter);
    }

    private void creatingRequest()
    {
        Intent intent = new Intent(this, RequestCreation.class);
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
