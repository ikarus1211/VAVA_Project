package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Scene;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.AppLocationManager;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.PaginationScrollListener;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.RecViewAdapter;
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


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mikpuk.vava_project.Constants.LOCATION_PERM_CODE;
import static com.mikpuk.vava_project.PaginationScrollListener.PAGE_START;


public class MenuScreenActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,RecViewAdapter.OnItemListener {

    private boolean permissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLocation;
    private User user = null;

    private AppLocationManager appLocationManager;
    private Dialog mDialog;

    @BindView(R.id.recyclerView100)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipeRefresh100)
    SwipeRefreshLayout swipeRefresh;

    private ArrayList<Item> items = new ArrayList<>();
    private RecViewAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private Item[] fetchedItems;

    int itemCount = 0;
    boolean allItemsLoaded = false;

    private static final String TAG = "Menu Screen";

    //Navigation bar
    private Context context;
    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HyperLog.i(TAG,"Menu screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);

        mDialog = new Dialog(this);
        user = (User)getIntent().getSerializableExtra("user");

        //Set up navigation bar
        SceneManager.initNavigationBar(getString(R.string.navigation_main_menu),R.id.menu_screen_dl,R.id.menu_navView,this,this,user);
        context = this;
        
    }
    private void initialize()
    {
        if(initialized)
            return;

        initialized = true;

        initGps();
        ButterKnife.bind(this);

        swipeRefresh.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new RecViewAdapter(new ArrayList<Item>(), this, this);
        mRecyclerView.setAdapter(adapter);
        doApiCall();

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                doApiCall();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    private void runDialog(int pos)
    {
        mDialog = new Dialog(this);
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
        TextView openProfile;

        status = mDialog.findViewById(R.id.popStatus);
        finish = mDialog.findViewById(R.id.finish101);
        imageView = mDialog.findViewById(R.id.dialog_image);
        txtclose = mDialog.findViewById(R.id.popTxtClose);
        textName = mDialog.findViewById(R.id.popMyName);
        textItemName = mDialog.findViewById(R.id.popItemName);
        textDescription = mDialog.findViewById(R.id.popMyDescription);
        textAddress = mDialog.findViewById(R.id.popAddress);
        accpetButton = mDialog.findViewById(R.id.accept);
        openProfile = mDialog.findViewById(R.id.popTxtInfo);

        finish.setVisibility(View.INVISIBLE);
        accpetButton.setVisibility(View.VISIBLE);

        Item item = adapter.getItem(pos);
        if (item.isAccepted())
            status.setText(R.string.request_taken);

        textName.setText(item.getUser().getUsername());
        imageView.setImageResource((int)item.getType_id());
        textItemName.setText(item.getName());
        textDescription.setText(item.getDescription());
        textAddress.setText(appLocationManager.generateAddress(item.getLatitude(), item.getLongtitude()));


        openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SceneManager.loadOtherProfile(context,user,item.getUser());
            }
        });

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
                                new AsyncAcceptedItemsSetter().execute(user.getId(),item.getId());
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

    @Override
    protected void onResume() {
        super.onResume();

        if(initialized)
            return;

        //HyperLog.w(TAG,"ON RESUME !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            getLocationPermission();
        } else{
            showGPSDisabledAlertToUser();
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        MenuScreenActivity.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
    }

    private void doApiCall() {
        items.clear();
        fetchedItems = new Item[0];
        new AsyncOtherItemsGetter().execute();
    }

    private void doneApiCall() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(allItemsLoaded){
                    showToast(getString(R.string.no_more_items_error));
                }

                for (Item item:fetchedItems)
                {
                    items.add(item);
                }

                /**
                 * manage progress view
                 */
                if (currentPage != PAGE_START || allItemsLoaded)
                    adapter.removeLoading();

                adapter.addItems(items);
                swipeRefresh.setRefreshing(false);

                // check weather is last page or not
                if (currentPage < totalPage && !allItemsLoaded) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            }
        }, 100);
    }

    @Override
    public void onRefresh() {
        doRefresh();
    }

    private void doRefresh()
    {
        allItemsLoaded = false;
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        doApiCall();
    }


    private boolean getGpsStatus()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            showGPSDisabledAlertToUser();
            return false;
        }
    }

    private void initGps() {
        System.out.println("Everything ok");
        if (permissionGranted) {

            appLocationManager = new AppLocationManager(MenuScreenActivity.this);
            //TODO zmazat?
            mLocation = appLocationManager.getmLocation();
            System.out.println(appLocationManager.getmLocation());
            System.out.println(appLocationManager.generateAddress());
        }
    }

    // TODO maybe rework
    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        HyperLog.w(TAG, "GPS is disabled");
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    /*
     * Function which checks if the permissions were granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        permissionGranted = false;
        System.out.println("44444");
        switch (requestCode)
        {
            case LOCATION_PERM_CODE:
            {
                if (grantResults.length > 0)
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                            HyperLog.w(TAG,"Permissions not granted");
                            return;
                        }
                    }
                    permissionGranted = true;
                    HyperLog.i(TAG,"Permissions set to true");
                }
            }
        }

    }

    /*
     * Request location permission, so that we can get the location of the
     * device.
     */
    private void getLocationPermission() {

        String[] premissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)

        {
            System.out.println("1");
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                HyperLog.i(TAG,"Permissions set to true");
                permissionGranted = true;
                initialize();
            }
            else
            {

                System.out.println("2");
                ActivityCompat.requestPermissions(this, premissions, LOCATION_PERM_CODE );
            }
        }
        else
        {
            System.out.println("3");
            ActivityCompat.requestPermissions(this, premissions, LOCATION_PERM_CODE );
        }

    }

    @Override
    public void onItemClick(int posistion) {
        runDialog(posistion);
    }

    class AsyncOtherItemsGetter extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... args) {

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/getotheritems/limit/{id}/{limit_start}/{limit_end}/{user_long}/{user_lat}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                if(appLocationManager == null){
                    initGps();
                }

                double lon,lat;
                if(appLocationManager == null){
                    //In case we can`t get location
                    lon = 0;
                    lat = 0;
                }
                else {
                    lon = appLocationManager.getLongitude();
                    lat = appLocationManager.getLatitude();
                }

                fetchedItems = restTemplate.exchange(uri, HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders), Item[].class,user.getId(),itemCount,itemCount+10,lon,lat).getBody();

                if(fetchedItems.length < 10) {
                    allItemsLoaded = true;
                }

                HyperLog.i(TAG,"Loading new items");

                itemCount+=fetchedItems.length;

            } catch (HttpServerErrorException e)
            {
                HyperLog.e(TAG,"Server exception",e);
                //Error v pripade chyby servera
                System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
                showToast(""+e.getStatusCode());
            } catch (HttpClientErrorException e2)
            {
                HyperLog.e(TAG,"Client exception",e2);
                //Error v pripade ziadosti klienka
                System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
                e2.printStackTrace();
                showToast("CLIENT ERROR "+e2.getStatusCode());
            } catch (Exception e3)
            {
                HyperLog.e(TAG,"Unknown error",e3);
                e3.printStackTrace();
                showToast("SOMETHING WENT WRONG");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            doneApiCall();
        }
    }

    class AsyncAcceptedItemsSetter extends AsyncTask<Long,Void,Void>
    {
        @Override
        protected Void doInBackground(Long... args) {
            long user_id = args[0];
            long item_id = args[1];

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/setaccepteditem/{user_id}/{item_id}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                restTemplate.exchange(uri, HttpMethod.POST,
                        new HttpEntity<String>(httpHeaders), Item[].class,user_id,item_id).getBody();
                showToast("Item accepted!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doRefresh();
                    }
                });

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

    //Nastavenie kliknutia na hornu listu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.menu_screen_dl);
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Skopirovane z https://stackoverflow.com/questions/7563725/android-how-can-i-detect-if-the-back-button-will-exit-the-app-i-e-this-is-the
    //Aby sme zabranili nechcenemu vypnutiu
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.quit_title))
                    .setMessage(getString(R.string.quit_desc))
                    .setPositiveButton(getString(R.string.quit_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Stop the activity
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.quit_no), null)
                    .show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
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