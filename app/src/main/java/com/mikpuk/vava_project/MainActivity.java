package com.mikpuk.vava_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    SQLConnector connector = new SQLConnector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Na testovanie zatial
        new Thread() {
            public void run() {
                connector.connectToDB();
                if(connector.isConnectedToDB()) {

                    //connector.deleteUserDB();

                    //connector.addUserToDB("Tester123","testik123");

                    int id = connector.getUserInDB("Tester123","testik123");
                    if(id > 0)
                        System.out.println("FOUND - ID: "+id);
                    else
                        System.out.println("NOT FOUND "+id);

                    int id2 = connector.getUserInDB("tester123","testik123");
                    if(id2 > 0)
                        System.out.println("FOUND - ID: "+id2);
                    else
                        System.out.println("NOT FOUND "+id2);

                    connector.closeConnection();
                }
                else {
                    System.out.println("Not connected to database");
                }
            }
        }.start();
    }
    public void regInit(View view)
    {
        Intent intent = new Intent(this, ReggActivity.class);
        startActivity(intent);
    }
}
