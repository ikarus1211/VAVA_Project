package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.User;
import com.mikpuk.vava_project.MD5Hashing;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.mikpuk.vava_project.Constants.ERROR_DIALOG_REQUEST;


public class LoginActivity extends AppCompatActivity {

    Button loginButton = null;
    Button registerButton = null;
    EditText loginText = null;
    EditText passwordText = null;



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
                loadRegistrationScreen();
            }
        });

    }


    public void loadRegistrationScreen()
    {
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
        finish();
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

    //Pokus o najdenie pouzivatela v DB a nasledne prihlasenie
    private void logInUser() {
        final String username = loginText.getText().toString();
        final String password = passwordText.getText().toString();

        if(username.isEmpty() || password.isEmpty()) {
            showToast("Enter your username and password");
            return;
        }

        //Call REST web services
        new Thread() {
            public void run() {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                try {
                    String uri = ConfigManager.getApiUrl(getApplicationContext())+"/getuserbydata/{username}/{password}";
                    RestTemplate restTemplate = new RestTemplate();

                    //Nezabudnut pridat pri HttpMethod.GET !!!
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    //Vytvorenie hlaviciek s tokenom
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth", AUTH_TOKEN);

                    //Poslanie udajov a cakanie na objekt
                    ResponseEntity<User> user = restTemplate.exchange(uri, HttpMethod.GET,
                            new HttpEntity<String>(httpHeaders), User.class,
                            username,MD5Hashing.getSecurePassword(password));

                    System.out.println("User: " + user.getBody().getUsername());

                    loadMenuScreen();
                } catch (HttpServerErrorException e)
                {
                    //Error v pripade chyby servera
                    System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
                    showToast("SERVER ERROR "+e.getStatusCode());
                } catch (HttpClientErrorException e2)
                {
                    //Error v pripade ziadosti klienka
                    System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
                    if(e2.getStatusCode() == HttpStatus.BAD_REQUEST)
                    {
                        showToast("Check your username or password");
                        return;
                    }
                    e2.printStackTrace();
                    showToast("CLIENT ERROR "+e2.getStatusCode());
                } catch (Exception e3)
                {
                    System.out.println("caught other exception");
                    e3.printStackTrace();
                    showToast("SOMETHING WENT WRONG");
                }

            }
        }.start();
    }


}
