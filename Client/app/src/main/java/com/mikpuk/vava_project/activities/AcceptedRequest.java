package com.mikpuk.vava_project.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Handler;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mikpuk.vava_project.AppLocationManager;
import com.mikpuk.vava_project.ConfigManager;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.data.Item;
import com.mikpuk.vava_project.PaginationScrollListener;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.data.User;
import com.mikpuk.vava_project.RecViewAdapter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mikpuk.vava_project.PaginationScrollListener.PAGE_START;

/**
 * This class controls UI which displays request accepted by User
 * It controls adding item into a recycle view
 */
public class AcceptedRequest extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RecViewAdapter.OnItemListener {

    //Infinite sctroll variables
    @BindView(R.id.recyclerView102)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh102)
    SwipeRefreshLayout swipeRefresh;
    private ArrayList<Item> items = new ArrayList<>();
    private RecViewAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private Item[] fetchedItems = new Item[0];
    int itemCount = 0;
    boolean allItemsLoaded = false;

    //Logged in user
    private User user = null;

    private AppLocationManager appLocationManager;

    private static final String TAG = "AcceptedRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HyperLog.i(TAG,"Accepted request activity");
        setContentView(R.layout.activity_accepted_request);
        user = (User)getIntent().getSerializableExtra("user");

        //Set up navigation bar
        SceneManager.initNavigationBar(getString(R.string.navigation_accepted_requests),R.id.accepted_requests_dl,R.id.accepted_requests_navView,this,this,user);

        // Location manager for getting user location
        appLocationManager = new AppLocationManager(this);

        setUpInfiniteScroll();
    }

    private void setUpInfiniteScroll()
    {
        ButterKnife.bind(this);
        swipeRefresh.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new RecViewAdapter(new ArrayList<Item>(), this, this);
        mRecyclerView.setAdapter(adapter);
        doApiCall();

        /*
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

    /**
     * Controls what happens when back button is pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AcceptedRequest.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
    }

    /**
     * Calling items filling
     * It first grabs the items from database in Async class
     */
    private void doApiCall() {
        items.clear();
        fetchedItems = new Item[0];
        new AsyncAcceptedItemsGetter().execute();
    }

    /**
     * Filling recycle view with items
     */
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

                /*
                 * manage progress view
                 */
                if (currentPage != PAGE_START || allItemsLoaded)
                    adapter.removeLoading();

                adapter.addItems(items);
                swipeRefresh.setRefreshing(false);

                if (currentPage < totalPage && !allItemsLoaded) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            }
        }, 100);
    }

    /**
     * Triggers when user refreshes the recycle view
     * It again calls the filling of the list
     */
    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        doApiCall();
    }


    @Override
    public void onItemClick(int posistion) {
    }

    class AsyncAcceptedItemsGetter extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... args) {

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/getapproveditems/limit/{id}/{limit_start}/{limit_end}/{user_long}/{user_lat}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

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
            } catch (HttpClientErrorException e2)
            {
                HyperLog.e(TAG,"Client exception",e2);
            } catch (Exception e3)
            {
                HyperLog.e(TAG,"Server exception",e3);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            doneApiCall();
        }

    }


    /**
     * Set up click on top navigaton bar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.accepted_requests_dl);
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Function for showing toasts
     * @param text string that you wanna display
     */
    private void showToast(final String text)
    {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }
}
