package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.MD5Hashing;
import com.mikpuk.vava_project.SceneManager;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//This activity controls registration screen
public class RegistrationActivity extends AppCompatActivity {

    //UI elements
    Button registerButton = null;
    EditText usernameText = null;
    EditText passwordText1 = null;
    EditText passwordText2 = null;
    TextInputLayout usernameLayout;
    TextInputLayout password1Layout;
    TextInputLayout password2Layout;

    private static final String TAG = "RegistrationActivity";

    RequestQueue queue;
    Context context;

    //Keys used for recaptcha
    String SITE_KEY;
    String SECRET_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registration);

        HyperLog.i(TAG, "Starting registration");

        context=this;
        SECRET_KEY = ConfigManager.getSecretKey(this);
        SITE_KEY = ConfigManager.getSiteKey(this);
        loadUI();
        queue = Volley.newRequestQueue(context);
    }

    private void loadUI()
    {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RegistrationActivity.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
    }

    //Checks if all field inputs are valid
    private void validateFields(String username, String password1, String password2)
    {
        boolean showError = false;

        if(username.length() < 6)
        {
            usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
            usernameLayout.setError(getString(R.string.short_username_error));
            showError = true;
        }
        else {
            if(username.length() > 25) {
                usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
                usernameLayout.setError(getString(R.string.long_username_error));
                showError = true;
            }
            else if(!isValidUserName(username)) {
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
        } else if(password1.length() > 25) {
            password1Layout.setErrorIconDrawable(R.drawable.ic_error);
            password1Layout.setError(getString(R.string.long_password_error));
            showError = true;
        }
        if(password2.length() < 8) {
            password2Layout.setErrorIconDrawable(R.drawable.ic_error);
            password2Layout.setError(getString(R.string.short_password_error));
            showError = true;
        } else if(password2.length() > 25) {
            password2Layout.setErrorIconDrawable(R.drawable.ic_error);
            password2Layout.setError(getString(R.string.long_password_error));
            showError = true;
        }

        if(showError) {
            return;
        }

        callUsernameCheckRest(username,password1);

    }

    //This calles the recaptcha window to show
    private void callRecaptcha(String username, String password1) {
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        if (!response.getTokenResult().isEmpty()) {
                            verifyUser(response.getTokenResult(),username,password1);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            HyperLog.i(TAG, "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            HyperLog.d(TAG, "Unknown type of error: " + e.getMessage());
                        }
                    }
                });
    }

    //Returns true if user doesnt use special chars in username
    public static boolean isValidUserName(String name) {
        String control = "^[a-zA-Z0-9._-]+";
        return name.matches(control);
    }

    //Start register process chain
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

        validateFields(username,password1,password2);

    }

    //Set all fields text to msg
    private void setFieldErrors(String msg) {
        password1Layout.setErrorIconDrawable(R.drawable.ic_error);
        password1Layout.setError(msg);
        password2Layout.setErrorIconDrawable(R.drawable.ic_error);
        password2Layout.setError(msg);
        usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
        usernameLayout.setError(msg);
    }

    //Set username fields text to msg
    public void setUsernameFieldError(String msg) {
        usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
        usernameLayout.setError(msg);
    }

    //Call REST fucntion to check if username is not taken
    private void callUsernameCheckRest(String username, String password1) {
        new Thread()
        {
            public void run() {
                try {
                    String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                    String uri = ConfigManager.getApiUrl(getApplicationContext())+"/checkusername/{username}";
                    RestTemplate restTemplate = new RestTemplate();

                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth", AUTH_TOKEN);

                    ResponseEntity<Integer> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<String>(httpHeaders),
                            Integer.class, username);

                    if(response.getBody() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUsernameFieldError(getString(R.string.username_taken_error));
                            }
                        });
                    }
                    else {
                        callRecaptcha(username,password1);
                    }

                } catch (HttpServerErrorException e)
                {
                    HyperLog.e(TAG, "Server error",e);
                } catch (HttpClientErrorException e2)
                {
                    HyperLog.e(TAG, "Client exception",e2);
                    if(e2.getStatusCode() == HttpStatus.BAD_REQUEST)
                    {
                        runOnUiThread(() -> {
                            usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
                            usernameLayout.setError(getString(R.string.username_taken_error));
                        });
                        return;
                    }
                } catch (Exception e3)
                {
                    HyperLog.e(TAG, "Other error",e3);
                }
            }
        }.start();
    }

    //Call REST function to register User to DB
    private void callRegistrationRest(String username, String password1) {
        new Thread()
        {
            public void run() {
                try {
                    String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                    String uri2 = ConfigManager.getApiUrl(getApplicationContext())+"/register/{username}/{password}";
                    RestTemplate restTemplate = new RestTemplate();

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth", AUTH_TOKEN);

                    restTemplate.exchange(uri2, HttpMethod.POST, new HttpEntity<String>(httpHeaders),
                            Void.class, username,MD5Hashing.getSecurePassword(password1));

                    showToast(getString(R.string.registration_succesful));
                    HyperLog.i(TAG, "User registered");

                    SceneManager.loadLoginScreen(context);

                } catch (HttpServerErrorException e)
                {
                    HyperLog.e(TAG, "Server error",e);
                } catch (HttpClientErrorException e2)
                {
                    HyperLog.e(TAG, "Client exception",e2);
                } catch (Exception e3)
                {
                    HyperLog.e(TAG,"Other exception",e3);
                }
            }
        }.start();
    }

    //Inspired by https://www.javatpoint.com/using-google-recaptcha-in-android-application
    private  void verifyUser(final String responseToken, String username, String password1){
        String url = "https://www.google.com/recaptcha/api/siteverify";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getBoolean("success")){
                            callRegistrationRest(username,password1);
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setFieldErrors(getString(R.string.recaptcha_error));
                                }
                            });
                        }
                    } catch (Exception ex) {
                        HyperLog.i(TAG, "JSON exception: " + ex.getMessage());

                    }
                },
                error -> HyperLog.i(TAG, "Error message: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret", SECRET_KEY);
                params.put("response", responseToken);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                49000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    //Show text to user
    private void showToast(final String text)
    {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }
}
