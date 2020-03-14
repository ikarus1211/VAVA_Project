package com.mikpuk.vava_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/*
    Class for working wotch request creating
 */
public class RequestCreation extends AppCompatActivity {

    Button createReq = null;
    EditText itemNameText = null;
    EditText descriptionText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_creation);

        createReq = findViewById(R.id.createReqButton);
        itemNameText = findViewById(R.id.reqCreationItem);
        descriptionText = findViewById(R.id.reqCreationDes);

        createReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCreation();
            }
        });
    }

    private void requestCreation()
    {

    }
}
