package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.MD5Hashing;
import com.mikpuk.vava_project.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_request_creation);

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
        final float latitude = 0.01f;
        final float longtitude = 0.02f;
        final int userId = 1;

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

                    restTemplate.exchange(uri, HttpMethod.GET,
                            new HttpEntity<String>(httpHeaders), Item.class,itemNameText.getText(),descriptionText.getText(),
                            longtitude,latitude,userId,7);

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
}
