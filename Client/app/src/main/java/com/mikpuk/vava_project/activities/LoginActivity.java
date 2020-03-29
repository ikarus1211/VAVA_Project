package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.db_things.SQLConnector;
import com.mikpuk.vava_project.db_things.SQLQueries;

import static com.mikpuk.vava_project.Constants.ERROR_DIALOG_REQUEST;
import static com.mikpuk.vava_project.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class LoginActivity extends AppCompatActivity {

    Button loginButton = null;
    Button registerButton = null;
    EditText loginText = null;
    EditText passwordText = null;
    SQLConnector connector = new SQLConnector();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        //Nacitanie UI premennych

        registerButton = findViewById(R.id.RegisterButton);
        loginText = findViewById(R.id.editText);
        passwordText = findViewById(R.id.PasswText);

        //Nastavenie onClick spravani tlacidiel
        if (services())
        {
            loginButton = findViewById(R.id.LoginButton);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logInUser();
                }
            });
        }


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
                int id = SQLQueries.getUserID(username,password,connector.getConnection());
                //Kontrola riesena zatial takto
                if (id > 0) {
                    loadMenuScreen();
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

        Intent intent = new Intent(this, RegistrationActivity.class);
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
    private void loadMenuScreen ()
    {
        //Nacitanie hlavneho menu
        Intent intent = new Intent(this, MenuScreenActivity.class);
        startActivity(intent);
    }
    /*
     * Checking if google play services are available
     */
    private boolean services()
    {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if (available == ConnectionResult.SUCCESS)
        {
            System.out.print("Service check is ok");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
            {
            Toast.makeText(this, "You cant do anything", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



}
