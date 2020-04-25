package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hypertrack.hyperlog.HyperLog;
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
import java.util.logging.*;


public class RegistrationActivity extends AppCompatActivity {

    Button registerButton = null;
    EditText usernameText = null;
    EditText passwordText1 = null;
    EditText passwordText2 = null;

    TextInputLayout usernameLayout;
    TextInputLayout password1Layout;
    TextInputLayout password2Layout;

    private static final String TAG = "Registration activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registration);

        HyperLog.i(TAG, "Starting registration");

        //Nacitanie UI premennych
        registerButton = findViewById(R.id.button);
        usernameText = findViewById(R.id.usernameEditText);
        passwordText1 = findViewById(R.id.passwEditTextReg);
        passwordText2 = findViewById(R.id.passwEditTextReg2);

        usernameLayout = findViewById(R.id.usernameInputLayout);
        password1Layout = findViewById(R.id.passwEditTextLayoutReg);
        password2Layout = findViewById(R.id.passwEditTextLayoutReg2);

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

    private boolean validateFields(String username, String password1, String password2)
    {
        boolean showError = false;

        if(username.length() < 6)
        {
            usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
            usernameLayout.setError(getString(R.string.short_username_error));
            showError = true;
        }
        else {
            if(!isValidUserName(username)) {
                usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
                usernameLayout.setError(getString(R.string.invalid_chars_error));
                showError = true;
            }
        }

        if(!password1.equals(password2)){
            password1Layout.setErrorIconDrawable(R.drawable.ic_error);
            password1Layout.setError(getString(R.string.password_equal_error));
            password2Layout.setErrorIconDrawable(R.drawable.ic_error);
            password2Layout.setError(getString(R.string.password_equal_error));
            showError = true;
        }

        if(password1.length() < 8) {
            password1Layout.setErrorIconDrawable(R.drawable.ic_error);
            password1Layout.setError(getString(R.string.short_password_error));
            showError = true;
        }
        if(password2.length() < 8) {
            password2Layout.setErrorIconDrawable(R.drawable.ic_error);
            password2Layout.setError(getString(R.string.short_password_error));
            showError = true;
        }

        if(showError) {
            return false;
        }

        return true;
    }

    public static boolean isValidUserName(String name) {
        String control = "^[a-zA-Z0-9._-]+";
        return name.matches(control);
    }

    //Registrovanie do DB
    public void registerUser()
    {
        final String username = usernameText.getText().toString();
        final String password1 = passwordText1.getText().toString();
        final String password2 = passwordText2.getText().toString();

        //Reset errors
        usernameLayout.setErrorIconDrawable(null);
        usernameLayout.setError(null);
        password1Layout.setError(null);
        password1Layout.setErrorIconDrawable(null);
        password2Layout.setErrorIconDrawable(null);
        password2Layout.setError(null);

        if(!validateFields(username,password1,password2)) {
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
                    HyperLog.i(TAG, "User registered");
                    loadLoginScreen();
                } catch (HttpServerErrorException e)
                {
                    //Error v pripade chyby servera
                    HyperLog.e(TAG, "Server error",e);
                    System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
                    showToast("SERVER ERROR "+e.getStatusCode());
                } catch (HttpClientErrorException e2)
                {
                    //Error v pripade ziadosti klienka
                    HyperLog.e(TAG, "Client exception",e2);
                    System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
                    if(e2.getStatusCode() == HttpStatus.BAD_REQUEST)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
                                usernameLayout.setError(getString(R.string.username_taken_error));
                            }
                        });
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
