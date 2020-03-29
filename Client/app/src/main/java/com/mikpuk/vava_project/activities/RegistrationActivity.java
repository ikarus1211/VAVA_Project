package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.db_things.MD5Hashing;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    //Skontrolovanie vstupov (PRIDAT!) a registrovanie do DB
    public void registerUser()
    {

        final String username = usernameText.getText().toString();
        final String password1 = passwordText1.getText().toString();
        final String password2 = passwordText2.getText().toString();

        new Thread()
        {
            public void run() {
                try {
                    if(!password1.equals(password2)){
                        showToast("Passwords do not match");
                        return;
                    }

                    //Pridat do configu a nacitavat z neho
                    String TOKEN = "MyToken123Haha.!@";
                    String AUTH_TOKEN = MD5Hashing.getSecurePassword(TOKEN);

                    String uri2 = "http://vavaserver-env-2.eba-z8cwmvuf.eu-central-1.elasticbeanstalk.com/register/{name}/{pass}";
                    RestTemplate restTemplate = new RestTemplate();

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth", AUTH_TOKEN);

                    ResponseEntity<Void> user = restTemplate.exchange(uri2, HttpMethod.POST,
                            new HttpEntity<String>(httpHeaders), Void.class, username,MD5Hashing.getSecurePassword(password1));

                    System.out.println("STATUS CODE " + user.getStatusCode());
                    showToast("User registered");
                    loadLoginScreen();
                } catch (HttpServerErrorException e)
                {
                    System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
                    showToast("Error while registering?");
                } catch (HttpClientErrorException e2)
                {
                    System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
                    showToast("Error while registering?");
                } catch (Exception e3)
                {
                    System.out.println("caught other exception");
                    e3.printStackTrace();
                    showToast("Error while registering?");
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
