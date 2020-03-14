package com.mikpuk.vava_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MenuScreen extends AppCompatActivity {

    Button myReqButton = null;
    Button acReqButton = null;
    Button mapButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        ListView myLView = findViewById(R.id.lisView);

        myReqButton = findViewById(R.id.myReqButton);
        acReqButton = findViewById(R.id.accpetButton);
        mapButton = findViewById(R.id.mapbutton);

        myReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMyReqUi();
            }
        });

/*
       Moje vlastne testovanie
 */
        Person number1 = new Person("David", "Nadlan", "Pero", "Moje Modre azurove pero");
        Person number2 = new Person("Divad", "Rajcan", "Rajciak", "Chybas mi");
        Person number3 = new Person("Peter", "Topik", "Alkohol", "Dopekla");
        Person number4 = new Person("Retep", "Batovany", "Pero", "Kybel maciek");
        Person number5 = new Person("Jano", "Naj Diera", "je", "Prievidza");
        Person number6 = new Person("Onaj", "Kuko", "Horky", "ma Sliz");
        Person number7 = new Person("Corona", "China", "Virus", "Covid-19");

        ArrayList<Person> myList = new ArrayList<>();
        myList.add(number1);
        myList.add(number2);
        myList.add(number3);
        myList.add(number4);
        myList.add(number5);
        myList.add(number6);
        myList.add(number7);

        ItemAdapter adapter = new ItemAdapter(this, R.layout.custom_list_view, myList);
        myLView.setAdapter(adapter);
    }

    private void loadMyReqUi()
    {
        Intent intent = new Intent(this, UsersRequest.class);
        startActivity(intent);
    }

}
