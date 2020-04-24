package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
<<<<<<< HEAD
import androidx.drawerlayout.widget.DrawerLayout;
=======
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
>>>>>>> 244c67c91656da773483a3410f1fbba18f43bbaa

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
<<<<<<< HEAD
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
=======
import android.os.Handler;
>>>>>>> 244c67c91656da773483a3410f1fbba18f43bbaa
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.navigation.NavigationView;
import com.mikpuk.vava_project.AppLocationManager;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.OtherReqItemAdapter;
import com.mikpuk.vava_project.PaginationScrollListener;
import com.mikpuk.vava_project.R;
<<<<<<< HEAD
import com.mikpuk.vava_project.SceneManager;
=======
import com.mikpuk.vava_project.RecViewAdapter;
>>>>>>> 244c67c91656da773483a3410f1fbba18f43bbaa
import com.mikpuk.vava_project.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mikpuk.vava_project.Constants.LOCATION_PERM_CODE;
import static com.mikpuk.vava_project.PaginationScrollListener.PAGE_START;

<<<<<<< HEAD
public class MenuScreenActivity extends AppCompatActivity{
=======
public class MenuScreenActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,RecViewAdapter.OnItemListener {
>>>>>>> 244c67c91656da773483a3410f1fbba18f43bbaa

    private boolean permissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLocation;
    private User user = null;

    private AppLocationManager appLocationManager;
    private Dialog mDialog;
<<<<<<< HEAD
=======

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


>>>>>>> 244c67c91656da773483a3410f1fbba18f43bbaa
    private static final String TAG = "MainActivity";

    //Navigation bar
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);

        mDialog = new Dialog(this);
        user = (User)getIntent().getSerializableExtra("user");

        //Set up navigation bar
        SceneManager.initNavigationBar(getString(R.string.navigation_main_menu),R.id.menu_screen_dl,R.id.menu_navView,this,this,user);

        getGpsStatus();
        getLocationPermission();

<<<<<<< HEAD
        myLView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                runDialog(i);
=======
        myReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMyReqUi();
            }
        });

        acReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAccepted();
>>>>>>> 244c67c91656da773483a3410f1fbba18f43bbaa
            }
        });

        //Spusta nacitanie listView
        AsyncOtherItemsGetter getter = new AsyncOtherItemsGetter();
        getter.execute();

        /*myLView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                runDialog(i);
            }
        });*/

        ButterKnife.bind(this);

        swipeRefresh.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new RecViewAdapter(new ArrayList<Item>(), this, this);
        mRecyclerView.setAdapter(adapter);
        doApiCall();

        /**
         * add scroll listener while user reach in bottom load more will call
         */
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
        mDialog.setContentView(R.layout.activity_pop_up_my_request);
        TextView txtclose;
        TextView textName;
        TextView textItemName;
        TextView textDescription;
        TextView textAddress;
        TextView accpetButton;


        txtclose = mDialog.findViewById(R.id.popTxtClose);
        textName = mDialog.findViewById(R.id.popMyName);
        textItemName = mDialog.findViewById(R.id.popItemName);
        textDescription = mDialog.findViewById(R.id.popMyDescription);
        textAddress = mDialog.findViewById(R.id.popAddress);
        accpetButton = mDialog.findViewById(R.id.accept);

        accpetButton.setVisibility(View.VISIBLE);
        textName.setText(user.getUsername());
        Item item = items.get(pos);
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
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    /**
     * do api call here to fetch data from server
     * In example i'm adding data manually
     */

    private void doApiCall() {
        items.clear();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                List<Item> itemList = new ArrayList<>();
                for (Item item:fetchedItems)
                {
                    items.add(item);
                }
                /*for (int i = 0; i < 10; i++) {
                    itemCount++;
                    Item postItem = new Item();
                    postItem.setName(Integer.toString(itemCount));
                    items.add(postItem);
                }/
                /**
                 * manage progress view
                 */
                if (currentPage != PAGE_START) adapter.removeLoading();
                adapter.addItems(items);
                swipeRefresh.setRefreshing(false);

                // check weather is last page or not
                if (currentPage < totalPage) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            }
        }, 1500);
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        doApiCall();
    }


    private void getGpsStatus()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private void initGps() {
        System.out.println("Everything ok");
        if (permissionGranted) {

            appLocationManager = new AppLocationManager(MenuScreenActivity.this);
            mLocation = appLocationManager.getmLocation();
            System.out.println(appLocationManager.getmLocation());
            System.out.println(appLocationManager.generateAddress());
        }
    }

    // TODO maybe rework
    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
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


<<<<<<< HEAD
    private void fillMyRequestsList(Item[] items)
    {
        List<Item> itemList = new ArrayList<>();
        for (Item item:items
        ) {
            itemList.add(item);
        }

        System.out.println("VYKONAM 6");
        final  OtherReqItemAdapter adapter = new OtherReqItemAdapter(this, R.layout.item_main_menu, itemList);

        runOnUiThread(new Runnable() {
            public void run() {
                myLView.setAdapter(adapter);
            }
        });
    }

=======
    /*
     * Function checks permissions and then initialize the button form mapView
     */
    private void init()
    {
        System.out.println("Everything ok");
        if (permissionGranted) {

            appLocationManager = new AppLocationManager(MenuScreenActivity.this);
            mLocation = appLocationManager.getmLocation();
            System.out.println(appLocationManager.getmLocation());
            System.out.println(appLocationManager.generateAddress());

            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLocation = appLocationManager.getmLocation();
                    System.out.println(appLocationManager.getmLocation());
                    System.out.println(appLocationManager.generateAddress());

                    if(mLocation == null) {
                        showToast("Location not set");
                        return;
                    }
                    Intent intent = new Intent(MenuScreenActivity.this, MapViewActivity.class);
                    intent.putExtra("location", mLocation);
                    startActivity(intent);
                }
            });
        }
    }


    private void loadMyReqUi()
    {
        Intent intent = new Intent(this, MyRequestsActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }

    private void loadAccepted()
    {
        Intent intent = new Intent(this, AcceptedRequest.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }
>>>>>>> 244c67c91656da773483a3410f1fbba18f43bbaa


    /*
     * Function which checks if the permissions were granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        permissionGranted = false;
        System.out.println("4");
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
                            return;
                        }
                    }
                    permissionGranted = true;
                    initGps();
                    System.out.print("GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                }
            }
        }

    }


<<<<<<< HEAD
=======
    @Override
    protected void onResume() {
        super.onResume();

    }


>>>>>>> 244c67c91656da773483a3410f1fbba18f43bbaa
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
                permissionGranted = true;
                initGps();
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
        protected Void doInBackground(Void... voids) {

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/getotheritems/{id}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                fetchedItems = restTemplate.exchange(uri, HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders), Item[].class,user.getId()).getBody();

                showToast("ITEMS LOADED!");
                doApiCall();

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
