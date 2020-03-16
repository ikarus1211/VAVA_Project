package com.mikpuk.vava_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;



import static com.mikpuk.vava_project.Constants.ERROR_DIALOG_REQUEST;
import static com.mikpuk.vava_project.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.mikpuk.vava_project.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {

    Button loginButton = null;
    Button registerButton = null;
    EditText loginText = null;
    EditText passwordText = null;
    SQLConnector connector = new SQLConnector();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Nacitanie UI premennych
        loginButton = findViewById(R.id.LoginButton);
        registerButton = findViewById(R.id.RegisterButton);
        loginText = findViewById(R.id.editText);
        passwordText = findViewById(R.id.PasswText);

        //Nastavenie onClick spravani tlacidiel
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regInit();
            }
        });

    }

    //Pokus o najdenie pouzivatela v DB a nasledne prihlasenie
    private void logInUser() {
        final String username = loginText.getText().toString();
        final String password = passwordText.getText().toString();

        new Thread() {
            public void run() {
                if (!connector.isConnectedToDB()) {
                    connector.connectToDB();
                }
                int id = connector.getUserInDB(username, password);
                //Kontrola riesena zatial takto
                if (id > 0) {
                    loadMenu();
                }
                else {
                    showToast("Logging in failed");
                    //Nasledne spravit prihlasenie s poslanim IDcka
                }
            }
        }.start();
    }

    public void regInit()
    {
        //Zrusenie SQL pripojenia pred prepnutim na druhu scenu
        connector.closeConnection();

        Intent intent = new Intent(this, ReggActivity.class);
        startActivity(intent);
    }

    private void showToast(final String text)
    {
        //Toto vyhodi bublinu s infom
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadMenu ()
    {
        Intent intent = new Intent(this, MenuScreen.class);
        startActivity(intent);
    }



}
