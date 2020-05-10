package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.AppLocationManager;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.data.Item;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.data.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


/**
 * Class manages creating requests
 */
public class CreateMyRequestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //UI elements
    Button createReq = null;
    EditText itemNameText = null;
    EditText descriptionText = null;
    TextInputLayout titleLayout;
    TextInputLayout descLayout;
    Spinner spinner = null;
    private int selectedType = R.drawable.tools;

    //Logged in user
    User user = null;

    private AppLocationManager appLocationManager;
    private Context context;
    private static final String TAG = "CreateMyRequestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HyperLog.i(TAG, "Create request activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_request_creation);
        user = (User)getIntent().getSerializableExtra("user");
        appLocationManager = new AppLocationManager(this);
        context=this;

        loadUI();
    }

    /**
     * Loading UI
     */
    private void loadUI()
    {
        spinner = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence>  myAdapter = ArrayAdapter.createFromResource(this, R.array.cate, R.layout.create_request_spinner_layout);
        myAdapter.setDropDownViewResource(R.layout.simple_create_drop_down);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(this);

        createReq = findViewById(R.id.createReqButton);
        itemNameText = findViewById(R.id.createTitle);
        descriptionText = findViewById(R.id.createDesc);
        titleLayout = findViewById(R.id.createTitleLayout);
        descLayout = findViewById(R.id.createDescLayout);

        createReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
            }
        });
    }

    /**
     * Checking input fields
     * @return
     */
    private boolean checkFields() {
        boolean showError = false;

        titleLayout.setError(null);
        titleLayout.setErrorIconDrawable(null);
        descLayout.setError(null);
        descLayout.setErrorIconDrawable(null);

        if(itemNameText.getText().toString().isEmpty()) {
            titleLayout.setError(getString(R.string.empty_string_error));
            titleLayout.setErrorIconDrawable(R.drawable.ic_error);
            showError=true;
        }
        else {
            if(itemNameText.getText().length() > 30){
                titleLayout.setError(getString(R.string.create_title_too_long));
                titleLayout.setErrorIconDrawable(R.drawable.ic_error);
                showError=true;
            }
        }

        if(descriptionText.getText().toString().isEmpty()) {
            descLayout.setError(getString(R.string.empty_string_error));
            descLayout.setErrorIconDrawable(R.drawable.ic_error);
            showError=true;
        } else {
            if(descriptionText.getText().length() > 120) {
                descLayout.setError(getString(R.string.create_desc_too_long));
                descLayout.setErrorIconDrawable(R.drawable.ic_error);
                showError=true;
            }
        }

        return !showError;
    }

    /**
     * Show user that we cant get his location.
     */
    private void showGPSErrorDialog(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.gps_not_available))
                .setMessage(getString(R.string.gps_not_available_desc))
                .setPositiveButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                createRequest();
                            }
                        };

                        handler.postDelayed(r, 1500);

                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void createRequest()
    {
        if(!checkFields()) {
            return;
        }

        if(appLocationManager.getLongitude() == 0 && appLocationManager.getLatitude() ==0){
            //Try to get position again
            appLocationManager = new AppLocationManager(this);
            showGPSErrorDialog();
            return;
        }

        final double latitude = appLocationManager.getLatitude();
        final double longitude = appLocationManager.getLongitude();

        new Thread()
        {
            public void run() {
                try {
                    String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                    String uri = ConfigManager.getApiUrl(getApplicationContext())+
                            "/createitem/{longtitude}/{latitude}/{user_id}/{type_id}";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth",AUTH_TOKEN);
                    httpHeaders.add("name",itemNameText.getText().toString());
                    //Base64 creates new line so I replace them with some string here and then I replace them back to new lines at server
                    httpHeaders.add("description", Base64.encodeToString(descriptionText.getText().toString().getBytes(),Base64.URL_SAFE).replaceAll("\\R", "MySpaceLUL"));

                    restTemplate.exchange(uri, HttpMethod.POST,
                            new HttpEntity<String>(httpHeaders), Item.class,longitude,latitude,user.getId(),selectedType);//,

                    showToast("ITEM ADDED!");
                    HyperLog.i(TAG, "Item created");

                    SceneManager.loadMyRequests(context,user,true);

                } catch (HttpServerErrorException e)
                {
                    HyperLog.e(TAG, "Server exception",e);
                } catch (HttpClientErrorException e2)
                {
                    HyperLog.e(TAG, "Client exception",e2);
                } catch (Exception e3)
                {
                    HyperLog.e(TAG, "Other exception",e3);
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        CreateMyRequestActivity.this.overridePendingTransition(R.anim.in_from_top,
                R.anim.out_from_bottom);
    }


    //Toto vyhodi bublinu s infom - len pre nas
    private void showToast(final String text)
    {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position)
        {
            case 0:
                selectedType = R.drawable.animals;
                break;
            case 1:
                selectedType = R.drawable.kids;
                break;
            case 2:
                selectedType = R.drawable.home;
                break;
            case 3:
                selectedType = R.drawable.furniture;
                break;
            case 4:
                selectedType = R.drawable.garden;
                break;
            case 5:
                selectedType = R.drawable.sport;
                break;
            case 6:
                selectedType = R.drawable.food;
                break;
            case 7:
                selectedType = R.drawable.clothes;
                break;
            case 8:
                selectedType = R.drawable.technology;
                break;
            case 9:
                selectedType = R.drawable.tools;
                break;
            case 10:
                selectedType = R.drawable.art;
                break;
            case 11:
                selectedType = R.drawable.medication;
                break;
            case 12:
                selectedType = R.drawable.square;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedType = R.drawable.animals;
    }

}
