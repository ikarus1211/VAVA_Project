package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.MD5Hashing;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class RegistrationActivity extends AppCompatActivity {

    Button registerButton = null;
    EditText usernameText = null;
    EditText passwordText1 = null;
    EditText passwordText2 = null;
    EditText emailText = null; //Na tomto sme sa dohodli? Ale tak da sa implementovat
                                // To tam je zatial len pre efekt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registration);

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

    //Zavretie registracneho okna a navrat do login screenu
    private void loadLoginScreen()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Aby sa pouzivatel nevratil back tlacidlom do registracie
        startActivity(intent);
    }

    //Skontrolovanie vstupov a registrovanie do DB
    public void registerUser()
    {
        final String username = usernameText.getText().toString();
        final String password1 = passwordText1.getText().toString();
        final String password2 = passwordText2.getText().toString();

        if(!password1.equals(password2)){
            showToast("Passwords do not match");
            return;
        }

        if(username.isEmpty() || password1.isEmpty())
        {
            showToast("All fields must be filled!");
            return;
        }

        //Call REST web services
        new Thread()
        {
            public void run() {
                try {
                    String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                    String uri2 = ConfigManager.getApiUrl(getApplicationContext())+"/register/{username}/{password}";
                    RestTemplate restTemplate = new RestTemplate();

                    //Vytvorenie hlaviciek s tokenom
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth", AUTH_TOKEN);

                    //Poslanie udajov na vytvorenie zaznamu v databaze
                    restTemplate.exchange(uri2, HttpMethod.POST, new HttpEntity<String>(httpHeaders),
                            Void.class, username,MD5Hashing.getSecurePassword(password1));

                    showToast("User registered");
                    loadLoginScreen();
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
                        showToast("Username already used!");
                        return;
                    }
                    e2.printStackTrace();
                    showToast("CLIENT ERROR "+e2.getStatusCode());
                } catch (Exception e3)
                {
                    e3.printStackTrace();
                    showToast("SOMETHING WENT WRONG");
                }
            }
        }.start();

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
