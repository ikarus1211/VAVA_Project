package com.mikpuk.vava_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReggActivity extends AppCompatActivity {

    Button registerButton = null;
    EditText usernameText = null;
    EditText passwordText1 = null;
    EditText passwordText2 = null;
    EditText emailText = null; //Na tomto sme sa dohodli? Ale tak da sa implementovat
                                // To tam je zatial len pre efekt

    SQLConnector connector = new SQLConnector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regg2);

        //Nacitanie UI premennych
        registerButton = findViewById(R.id.button);
        usernameText = findViewById(R.id.editText2);
        passwordText1 = findViewById(R.id.editText3);
        passwordText2 = findViewById(R.id.editText4);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    //Skontrolovanie vstupov (PRIDAT!) a registrovanie do DB
    private void registerUser()
    {
        final String username = usernameText.getText().toString();
        final String password1 = passwordText1.getText().toString();
        final String password2 = passwordText2.getText().toString();
        new Thread() {
            public void run() {
                if(!password1.equals(password2)){
                    showToast("Passwords do not match");
                    return;
                }
                if(!connector.isConnectedToDB())
                {
                    connector.connectToDB();
                }
                //Kontrola riesena zatial takto
                if(connector.addUserToDB(username,password1)) {
                    showToast("User registered");
                    loadMainMenu();
                }
                else {
                    showToast("Error while registering?");
                }
            }
        }.start();
    }

    //Zavretie registracneho okna a navrat do hlavneho menu
    private void loadMainMenu()
    {
        //Zavretie SQL pripojenia
        connector.closeConnection();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Aby sa pouzivatel nevratil back tlacidlom do registracie
        startActivity(intent);
    }

    //Toto vyhodi bublinu s infom
    private void showToast(final String text)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
