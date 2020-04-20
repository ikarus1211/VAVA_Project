package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikpuk.vava_project.AppLocationManager;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.MyReqItemAdapter;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.List;

/*
    Class for displaying request that user created
 */
public class MyRequestsActivity extends AppCompatActivity {

    Button createReq = null;
    ListView myLView = null;
    User user = null;
    private Dialog mDialog;
    private AppLocationManager appLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_requests);
        myLView = findViewById(R.id.reqListView);
        createReq = findViewById(R.id.createButton);
        createReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCreateReqWindow();
            }
        });
        mDialog = new Dialog(this);
        user = (User)getIntent().getSerializableExtra("user");

        AsyncMyItemsGetter asyncItemGetter = new AsyncMyItemsGetter();
        asyncItemGetter.execute();
        myLView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                runDialog(i);
            }
        });

        appLocationManager = new AppLocationManager(this);
    }

    private void runDialog(int pos)
    {
        mDialog.setContentView(R.layout.activity_pop_up_my_request);
        TextView txtclose;
        TextView textName;
        TextView textItemName;
        TextView textDescription;
        TextView textAddress;


        txtclose = mDialog.findViewById(R.id.popTxtClose);
        textName = mDialog.findViewById(R.id.popMyName);
        textItemName = mDialog.findViewById(R.id.popItemName);
        textDescription = mDialog.findViewById(R.id.popMyDescription);
        textAddress = mDialog.findViewById(R.id.popAddress);

        textName.setText(user.getUsername());
        Item item = (Item)myLView.getItemAtPosition(pos);
        textItemName.setText(item.getName());
        textDescription.setText(item.getDescription());
        textAddress.setText(appLocationManager.generateAddress(item.getLatitude(), item.getLongtitude()));


        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void loadCreateReqWindow()
    {
        Intent intent = new Intent(this, CreateMyRequestActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }

    private void fillMyRequestsList(Item[] items)
    {
        List<Item> itemList = new ArrayList<>();
        for (Item item:items)
        {
            itemList.add(item);
        }

        final MyReqItemAdapter adapter = new MyReqItemAdapter(this, R.layout.item_my_request, itemList);

        runOnUiThread(new Runnable() {
            public void run() {
                myLView.setAdapter(adapter);
            }
        });
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



    class AsyncMyItemsGetter extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/getitems/{id}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                Item[] items = restTemplate.exchange(uri, HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders), Item[].class,user.getId()).getBody();

                showToast("ITEMS LOADED!");
                fillMyRequestsList(items);

            } catch (HttpServerErrorException e)
            {
                //Error v pripade chyby servera
                System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
                showToast("SERVER ERROR "+e.getStatusCode());
            } catch (HttpClientErrorException e2)
            {
                //Error v pripade ziadosti klienka
                System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
                e2.printStackTrace();
                showToast("CLIENT ERROR "+e2.getStatusCode());
            } catch (Exception e3)
            {
                e3.printStackTrace();
                showToast("SOMETHING WENT WRONG");
            }

            return null;
        }

    }

}
