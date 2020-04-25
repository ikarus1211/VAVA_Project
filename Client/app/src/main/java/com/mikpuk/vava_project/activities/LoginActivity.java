package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.MD5Hashing;
import com.mikpuk.vava_project.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyPairGeneratorSpi;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mikpuk.vava_project.Constants.ERROR_DIALOG_REQUEST;


public class LoginActivity extends AppCompatActivity {

    Button loginButton = null;
    Button registerButton = null;
    EditText loginText = null;
    EditText passwordText = null;

    TextInputLayout loginInputLayout;
    TextInputLayout passwordInputLayout;

    private static final String TAG = "Login Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HyperLog.initialize(this);
        HyperLog.setLogLevel(Log.VERBOSE);


        HyperLog.i(TAG, "Starting login activity");



        super.onCreate(savedInstanceState);
        loadSettings();
        setContentView(R.layout.layout_login);

        //Nacitanie UI premennych
        registerButton = findViewById(R.id.RegisterButton);
        loginText = findViewById(R.id.loginEditText);
        passwordText = findViewById(R.id.passwEditText);

        loginInputLayout = findViewById(R.id.loginEditTextLayout);
        passwordInputLayout = findViewById(R.id.passwEditTextLayout);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRegistrationScreen();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (services()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginButton = findViewById(R.id.LoginButton);
                                loginButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        logInUser();
                                    }
                                });
                            }
                        });
                        break;
                    }
                    SystemClock.sleep(100);
                }
            }
        }).start();

    }

    public void loadSettings()
    {

        HyperLog.i(TAG, "Loading settings");

        SharedPreferences sharedPreferences = getSharedPreferences("com.mikpukvava_project.PREFERENCES", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("app_language",Locale.getDefault().getLanguage());

        Locale myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Locale.setDefault(new Locale(language));;
    }


    public void loadRegistrationScreen()
    {
        HyperLog.i(TAG,"Switching to registration");
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
    private void loadMenuScreen (User user)
    {
        System.out.println("*******************************************");
        System.out.println(HyperLog.getDeviceLogsAsStringList());
        //Nacitanie hlavneho menu
        Intent intent = new Intent(this, MenuScreenActivity.class);
        showToast(user.getUsername());
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }
    /*
     * Checking if google play services are available
     */
    private boolean services()
    {
        HyperLog.i(TAG,"Checking services");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if (available == ConnectionResult.SUCCESS)
        {
            HyperLog.i(TAG,"Service check is ok");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
            {
                HyperLog.w(TAG,"Permissions were not granted");
            Toast.makeText(this, "You cant do anything", Toast.LENGTH_SHORT).show();
        }
        return false;

    }


    //Pokus o najdenie pouzivatela v DB a nasledne prihlasenie
    private void logInUser() {
        final String username = loginText.getText().toString();
        final String password = passwordText.getText().toString();
        boolean showError = false;

        //Refresh errors
        loginInputLayout.setError(null);
        loginInputLayout.setErrorIconDrawable(null);
        passwordInputLayout.setError(null);
        passwordInputLayout.setErrorIconDrawable(null);

        if(username.isEmpty()) {
            loginInputLayout.setError(getString(R.string.empty_string_error));
            loginInputLayout.setErrorIconDrawable(R.drawable.ic_error);
            showError=true;
        }
        if(password.isEmpty()) {
            passwordInputLayout.setError(getString(R.string.empty_string_error));
            passwordInputLayout.setErrorIconDrawable(R.drawable.ic_error);
            showError = true;
        }
        if(showError) {
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

                    loadMenuScreen(user.getBody());
                } catch (HttpServerErrorException e)
                {
                    //Error v pripade chyby servera
                    HyperLog.e(TAG,"Server exception "+e.getStatusCode());

                    showToast("SERVER ERROR "+e.getStatusCode());
                } catch (HttpClientErrorException e2)
                {
                    HyperLog.e(TAG,"Client Exception "+e2.getStatusCode());
                    //Error v pripade ziadosti klienka
                    //System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
                    if(e2.getStatusCode() == HttpStatus.BAD_REQUEST)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginInputLayout.setError(getString(R.string.wrong_credentials_error));
                                loginInputLayout.setErrorIconDrawable(R.drawable.ic_error);
                                passwordInputLayout.setError(getString(R.string.wrong_credentials_error));
                                passwordInputLayout.setErrorIconDrawable(R.drawable.ic_error);
                            }
                        });
                        return;
                    }
                    e2.printStackTrace();
                    showToast("CLIENT ERROR "+e2.getStatusCode());
                } catch (Exception e3)
                {
                    HyperLog.e(TAG,"Unknown exception while signing in "+e3.getMessage());
                    //System.out.println("caught other exception");
                    e3.printStackTrace();
                    showToast("SOMETHING WENT WRONG");
                }

            }
        }.start();
    }


}
