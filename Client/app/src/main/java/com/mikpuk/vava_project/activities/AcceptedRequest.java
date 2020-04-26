package com.mikpuk.vava_project.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.mikpuk.vava_project.ConfigManager;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.OtherReqItemAdapter;
import com.mikpuk.vava_project.PaginationScrollListener;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.User;
import com.mikpuk.vava_project.RecViewAdapter;

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

import static com.mikpuk.vava_project.PaginationScrollListener.PAGE_START;

public class AcceptedRequest extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RecViewAdapter.OnItemListener {



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

    private User user = null;

    private static final String TAG = "Accepted Request";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HyperLog.i(TAG,"Accepted request activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_request);
        user = (User)getIntent().getSerializableExtra("user");

        //Set up navigation bar
        SceneManager.initNavigationBar(getString(R.string.navigation_accepted_requests),R.id.accepted_requests_dl,R.id.accepted_requests_navView,this,this,user);



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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        AcceptedRequest.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
    }

    private void doApiCall() {
        items.clear();

        if(allItemsLoaded){
            System.out.println("ALL ITEMS ARE LOADED");
            adapter.removeLoading();
            swipeRefresh.setRefreshing(false);
            isLastPage = true;
            return;
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                fetchedItems = new Item[0];

                new AsyncAcceptedItemsGetter().execute();

                while(fetchedItems.length == 0)
                {
                    if(allItemsLoaded)  {
                        adapter.removeLoading();
                        swipeRefresh.setRefreshing(false);
                        isLastPage = true;
                        showToast("There are no more items left!");
                        System.out.println("ALL ITEMS ARE LOADED2");
                        return;
                    }
                    System.out.println("SLEEPING");
                    SystemClock.sleep(500); //Aby sme nevytazili UI thread
                }

                for (Item item:fetchedItems)
                {
                    items.add(item);
                }

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
                        "/getapproveditems/limit/{id}/{limit_start}/{limit_end}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                fetchedItems = restTemplate.exchange(uri, HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders), Item[].class,user.getId(),itemCount,itemCount+10).getBody();


                if(fetchedItems.length == 0) {
                    allItemsLoaded = true;
                    return null;
                }

                showToast("LOADING NEW ITEMS");

                itemCount+=10;

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
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.accepted_requests_dl);
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
