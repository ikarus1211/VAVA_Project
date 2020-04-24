package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
public class CreateMyRequestActivity extends AppCompatActivity {

    Button createReq = null;
    EditText itemNameText = null;
    EditText descriptionText = null;
    User user = null;
    private static final String TAG = "Create new request activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HyperLog.i(TAG, "Create request activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_request_creation);
        Spinner spinner = findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence>  myAdapter = ArrayAdapter.createFromResource(this, R.array.cate, android.R.layout.simple_spinner_item);
        myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(myAdapter);

        user = (User)getIntent().getSerializableExtra("user");

        createReq = findViewById(R.id.createReqButton);
        itemNameText = findViewById(R.id.reqCreationItem);
        descriptionText = findViewById(R.id.reqCreationDes);

        createReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
            }
        });
    }

    private void createRequest()
    {
        AppLocationManager appLocationManager = new AppLocationManager(this);


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
                            "/createitem/{name}/{description}/{longtitude}/{latitude}/{user_id}/{type_id}";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth",AUTH_TOKEN);

                    restTemplate.exchange(uri, HttpMethod.POST,
                            new HttpEntity<String>(httpHeaders), Item.class,itemNameText.getText(),descriptionText.getText(),
                            longitude,latitude,user.getId(),7);

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
}
