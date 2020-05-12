package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;
import com.hypertrack.hyperlog.HLCallback;
import com.hypertrack.hyperlog.HyperLog;
import com.hypertrack.hyperlog.error.HLErrorResponse;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.MD5Hashing;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.data.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

import static com.mikpuk.vava_project.Constants.ERROR_DIALOG_REQUEST;

/**
 *
 *  This is the login activity which manages the login screen.
 *  Login screen is the first screen that user sees.
 *
 **/
public class LoginActivity extends AppCompatActivity {

    //UI elements
    Button loginButton = null;
    Button registerButton = null;
    EditText loginText = null;
    EditText passwordText = null;
    TextInputLayout loginInputLayout;
    TextInputLayout passwordInputLayout;

    Context context;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings();
        HyperLog.initialize(this);
        HyperLog.setLogLevel(Log.VERBOSE);
        HyperLog.i(TAG, "Starting login activity");
        HyperLog.setURL("https://encfyiz9yudyp.x.pipedream.net");
        HyperLog.pushLogs(this, true, new HLCallback() {
            @Override
            public void onSuccess(@NonNull Object response) {
                HyperLog.i(TAG, "Log push successful");
                HyperLog.deleteLogs();
            }

            @Override
            public void onError(@NonNull HLErrorResponse HLErrorResponse) {
                HyperLog.e(TAG, "Log push failed");
            }


        });

        setContentView(R.layout.layout_login);
        context = this;

        loadUI();
    }

    /**
     * Ui loading
     */
    private void loadUI()
    {
        registerButton = findViewById(R.id.RegisterButton);
        loginText = findViewById(R.id.loginEditText);
        passwordText = findViewById(R.id.passwEditText);
        loginInputLayout = findViewById(R.id.loginEditTextLayout);
        passwordInputLayout = findViewById(R.id.passwEditTextLayout);

        /*
          On click listener for registration button.
          Switching scenes to registration.
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SceneManager.loadRegistrationScreen(context);
            }
        });

        loginButton = findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });

        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if(services()){
                    loginButton = findViewById(R.id.LoginButton);
                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logInUser();
                        }
                    });
                }
                else{
                    showToast(getString(R.string.no_google_services));
                    handler.postDelayed(this, 800);
                }
            }
        };
        handler.postDelayed(r, 5);
    }

    /**
     * Loads language preferences
     */

    private void loadSettings()
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

    /**
     * Shows info to user
     */
    private void showToast(final String text)
    {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }

    //Checking if google play services are available
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
        }
        return false;

    }

    /**
     * Function tries to log in user. It checks the database, looks for the correct
     * user and then tries to log him in.
     */
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

                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth", AUTH_TOKEN);

                    //Returns logged in user
                    ResponseEntity<User> user = restTemplate.exchange(uri, HttpMethod.GET,
                            new HttpEntity<String>(httpHeaders), User.class,
                            username,MD5Hashing.getSecurePassword(password));

                    //Check if username is correct - because DB is case insensitive and Test == test
                    if(!username.equals(user.getBody().getUsername())) {
                        showWrongCredError();
                        return;
                    }

                    SceneManager.loadMainMenu(context,user.getBody(),true);
                } catch (HttpServerErrorException e)
                {
                    HyperLog.e(TAG,"Server exception "+e);
                } catch (HttpClientErrorException e2)
                {
                    HyperLog.e(TAG,"Client Exception "+e2);
                    if(e2.getStatusCode() == HttpStatus.BAD_REQUEST)
                    {
                        showWrongCredError();
                        return;
                    }
                    e2.printStackTrace();
                } catch (Exception e3)
                {
                    HyperLog.e(TAG,"Unknown exception while signing in "+e3);
                    e3.printStackTrace();
                }
            }
        }.start();
    }

    //Show user that he has wrong credentials
    private void showWrongCredError(){
        runOnUiThread(() -> {
            loginInputLayout.setError(getString(R.string.wrong_credentials_error));
            loginInputLayout.setErrorIconDrawable(R.drawable.ic_error);
            passwordInputLayout.setError(getString(R.string.wrong_credentials_error));
            passwordInputLayout.setErrorIconDrawable(R.drawable.ic_error);
        });
    }

}
