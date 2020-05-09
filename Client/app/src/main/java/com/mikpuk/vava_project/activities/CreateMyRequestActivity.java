package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.AppLocationManager;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


/*
    Class for working wotch request creating
 */
public class CreateMyRequestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button createReq = null;
    EditText itemNameText = null;
    EditText descriptionText = null;

    TextInputLayout titleLayout;
    TextInputLayout descLayout;
    User user = null;
    Spinner spinner = null;
    private static final String TAG = "Create new request activity";
    private int selectedType = R.drawable.tools;
    private AppLocationManager appLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HyperLog.i(TAG, "Create request activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_request_creation);
        spinner = findViewById(R.id.category_spinner);
        appLocationManager = new AppLocationManager(this);

        ArrayAdapter<CharSequence>  myAdapter = ArrayAdapter.createFromResource(this, R.array.cate, R.layout.create_request_spinner_layout);
        myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(this);

        user = (User)getIntent().getSerializableExtra("user");

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

                        handler.postDelayed(r, 1000);

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
            //Pokus o znova dostanie GPS
            appLocationManager = new AppLocationManager(this);
            showGPSErrorDialog();
            return;
        }

        final double latitude = appLocationManager.getLatitude();
        final double longitude = appLocationManager.getLongitude();

        System.out.println("!!!!!!!!!!!!!!!!! "+longitude+" | "+latitude);

        //Call REST web services
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
                    loadNewRequestScreen();

                } catch (HttpServerErrorException e)
                {
                    HyperLog.e(TAG, "Server exception",e);
                    //Error v pripade chyby servera
                    System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
                    showToast("SERVER ERROR "+e.getStatusCode());
                } catch (HttpClientErrorException e2)
                {
                    HyperLog.e(TAG, "Client exception",e2);
                    //Error v pripade ziadosti klienka
                    System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
                    e2.printStackTrace();
                    showToast("CLIENT ERROR "+e2.getStatusCode());
                } catch (Exception e3)
                {
                    HyperLog.e(TAG, "Unknown error",e3);
                    e3.printStackTrace();
                    showToast("SOMETHING WENT WRONG");
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNewRequestScreen()
    {
        Intent intent = new Intent(this, MyRequestsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Aby sa pouzivatel nevratil back tlacidlom do vytvorenia requestu
        intent.putExtra("user",user);
        startActivity(intent);
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
                selectedType = R.drawable.kategorie_gauc;
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
                selectedType = R.drawable.animals;
                break;
            case 11:
                selectedType = R.drawable.animals;
                break;
            case 12:
                selectedType = R.drawable.animals;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedType = R.drawable.animals;
    }
/*
    private void runDialog(int pos)
    {
        Dialog mDialog = new Dialog(this);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        mDialog.setContentView(R.layout.activity_pop_up_my_request);
        TextView txtclose;
        TextView textName;
        TextView textItemName;
        TextView textDescription;
        TextView textAddress;
        TextView accpetButton;
        ImageView imageView;
        Button finish;
        TextView status;

        status = mDialog.findViewById(R.id.popStatus);
        finish = mDialog.findViewById(R.id.finish101);
        imageView = mDialog.findViewById(R.id.dialog_image);
        txtclose = mDialog.findViewById(R.id.popTxtClose);
        textName = mDialog.findViewById(R.id.popMyName);
        textItemName = mDialog.findViewById(R.id.popItemName);
        textDescription = mDialog.findViewById(R.id.popMyDescription);
        textAddress = mDialog.findViewById(R.id.popAddress);
        accpetButton = mDialog.findViewById(R.id.accept);

        finish.setVisibility(View.INVISIBLE);
        accpetButton.setVisibility(View.VISIBLE);
        textName.setText(user.getUsername());

        Item item = adapter.getItem(pos);
        if (item.isAccepted())
            status.setText(R.string.request_taken);

        imageView.setImageResource((int)item.getType_id());
        textItemName.setText(item.getName());
        textDescription.setText(item.getDescription());
        textAddress.setText(appLocationManager.generateAddress(item.getLatitude(), item.getLongtitude()));


        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });



        accpetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //mDialog.dismiss();
                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.request_accept_title))
                        .setMessage(getString(R.string.request_accept_desc))
                        .setPositiveButton(getString(R.string.request_accept_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new MenuScreenActivity.AsyncAcceptedItemsSetter().execute(user.getId(),item.getId());
                                HyperLog.i(TAG,"Request accepted");
                                mDialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.request_accept_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        mDialog.show();
    }

 */
}
