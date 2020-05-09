package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import java.util.Map;


/**
 * Class which is made for user registration UI handling.
 * Class is responsible for loading UI and checking if user put
 * correct inputs in text fields.
 */

public class RegistrationActivity extends AppCompatActivity {

    Button registerButton = null;
    EditText usernameText = null;
    EditText passwordText1 = null;
    EditText passwordText2 = null;
    TextInputLayout usernameLayout;
    TextInputLayout password1Layout;
    TextInputLayout password2Layout;

    private static final String TAG = "Registration activity";

    RequestQueue queue;
    String SITE_KEY = "6LfN2u4UAAAAAAw5mySE7FT56yQEj2fVRv7HQXVG";
    String SECRET_KEY = "6LfN2u4UAAAAAK3_yn0WnYd5gy45stVr8SoTBmOR";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registration);

        HyperLog.i(TAG, "Starting registration");

        /*
         UI loading
         */
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
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    /**
     * Closing of registration window and returning into login screen
     */
    private void loadLoginScreen()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        /*
          This blocks user in returning into registration screen with Back button press.
         */
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        RegistrationActivity.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
    }

    /**
     * This is validating text field.
     * It checks if user put correct input into each text field
     * @param username username that he entered
     * @param password1 first password that he entered
     * @param password2 second password. This must mach the first password.
     */
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
            return;
        }

        callUsernameCheckRest(username,password1);

    }
    //TODO domino help
    /**
     *
     * @param username
     * @param password1
     */
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

    public static boolean isValidUserName(String name) {
        String control = "^[a-zA-Z0-9._-]+";
        return name.matches(control);
    }

    /**
     * Registering user into database
     */
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

    private void setFieldErrors(String msg) {
        password1Layout.setErrorIconDrawable(R.drawable.ic_error);
        password1Layout.setError(msg);
        password2Layout.setErrorIconDrawable(R.drawable.ic_error);
        password2Layout.setError(msg);
        usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
        usernameLayout.setError(msg);
    }

    public void setUsernameFieldError(String msg) {
        usernameLayout.setErrorIconDrawable(R.drawable.ic_error);
        usernameLayout.setError(msg);
    }

    private void callUsernameCheckRest(String username, String password1) {
        //Call REST web services
        new Thread()
        {
            public void run() {
                try {
                    String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                    String uri2 = ConfigManager.getApiUrl(getApplicationContext())+"/checkusername/{username}";
                    RestTemplate restTemplate = new RestTemplate();

                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    //Vytvorenie hlaviciek s tokenom
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth", AUTH_TOKEN);

                    //Poslanie udajov na vytvorenie zaznamu v databaze
                    ResponseEntity<Integer> response = restTemplate.exchange(uri2, HttpMethod.GET, new HttpEntity<String>(httpHeaders),
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

    private void callRegistrationRest(String username, String password1) {
        showToast("Recaptcha succesful!");
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

    //Inspirovane z https://www.javatpoint.com/using-google-recaptcha-in-android-application
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

    /**
     * Simple text toast show function
     * @param text is the text that I want to be seen on output.
     */
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
