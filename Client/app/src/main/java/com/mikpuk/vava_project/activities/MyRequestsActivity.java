package com.mikpuk.vava_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.AppLocationManager;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.data.Item;
import com.mikpuk.vava_project.PaginationScrollListener;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.RecViewAdapter;
import com.mikpuk.vava_project.data.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;



import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.mikpuk.vava_project.PaginationScrollListener.PAGE_START;

/*
    Class for displaying request that user created
 */
public class MyRequestsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RecViewAdapter.OnItemListener {

    //UI
    Button createReq = null;
    //Logged user
    User user = null;

    private Dialog mDialog;
    private AppLocationManager appLocationManager;
    private Context context;

    //Infinite scroll
    @BindView(R.id.recyclerView101)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh101)
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

    private static final String TAG = "MyRequestsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HyperLog.i(TAG,"My request activity");
        setContentView(R.layout.layout_my_requests);

        user = (User)getIntent().getSerializableExtra("user");
        context=this;
        appLocationManager = new AppLocationManager(this);

        SceneManager.initNavigationBar(getString(R.string.navigation_my_requests),R.id.my_requests_dl,R.id.my_requests_navView,this,this,user);

        loadUI();
    }

    private void loadUI()
    {
        createReq = findViewById(R.id.createButton101);
        createReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SceneManager.loadNewRequest(context,user);
            }
        });
        mDialog = new Dialog(this);
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

    //Open dialog after item click
    private void runDialog(int pos)
    {
        mDialog = new Dialog(this);
        Objects.requireNonNull(mDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogTheme;
        mDialog.setContentView(R.layout.activity_pop_up_my_request);

        //Set up references
        TextView status = mDialog.findViewById(R.id.popStatus);
        ImageView imageView = mDialog.findViewById(R.id.dialog_image);
        TextView txtclose = mDialog.findViewById(R.id.popTxtClose);
        TextView textName = mDialog.findViewById(R.id.popMyName);
        TextView textItemName = mDialog.findViewById(R.id.popItemName);
        TextView textDescription = mDialog.findViewById(R.id.popMyDescription);
        TextView textAddress = mDialog.findViewById(R.id.popAddress);
        Button finishButton = mDialog.findViewById(R.id.finish101);
        TextView distance = mDialog.findViewById(R.id.popDistance);
        TextView openProfile = mDialog.findViewById(R.id.popTxtInfo);
        textName.setText(user.getUsername());
        Item item = adapter.getItem(pos);
        finishButton.setText(R.string.finish_delete);
        if (item.isAccepted())
            status.setText(R.string.request_taken);
        else
            finishButton.setText(R.string.delete_finish);
        if (item.getUser() == null)
            openProfile.setVisibility(View.INVISIBLE);
        if (item.getDistance() < 0){
            distance.setText("???");
        } else {
            distance.setText(getString(R.string.menu_dis) + "\n" + String.format("%.2f", item.getDistance()) + "km");
        }
        imageView.setImageResource((int)item.getType_id());
        textItemName.setText(item.getName());
        textDescription.setText(item.getDescription());
        textAddress.setText(appLocationManager.generateAddress(item.getLatitude(), item.getLongtitude()));

        openProfile.setOnClickListener(v -> SceneManager.loadOtherProfile(context,user,item.getUser()));
        finishButton.setOnClickListener(view -> {
            //if item is accepted then show confirm otherwise delete item
            if(item.isAccepted()) {
                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.request_confirm_title))
                        .setMessage(getString(R.string.request_confirm_desc))
                        .setPositiveButton(getString(R.string.answer_yes), (dialog, which) -> {
                            new AsyncItemConfirm().execute(item.getId());
                            mDialog.dismiss();
                        })
                        .setNegativeButton(getString(R.string.answer_no), (dialog, which) -> mDialog.dismiss())
                        .show();
            }else {
                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.request_delete_title))
                        .setMessage(getString(R.string.request_delete_desc))
                        .setPositiveButton(getString(R.string.answer_yes), (dialog, which) -> {
                            new AsyncItemDelete().execute(item.getId());
                            mDialog.dismiss();
                        })
                        .setNegativeButton(getString(R.string.answer_no), (dialog, which) -> mDialog.dismiss())
                        .show();
            }
        });

        txtclose.setOnClickListener(view -> mDialog.dismiss());

        mDialog.show();
    }

    //Show text to user
    private void showToast(final String text)
    {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyRequestsActivity.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
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

    private void doApiCall() {
        items.clear();
        fetchedItems = new Item[0];
        new AsyncMyItemsGetter().execute();
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
        runDialog(posistion);
    }

    class AsyncMyItemsGetter extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/getitems/limit/{id}/{limit_start}/{limit_end}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                fetchedItems = restTemplate.exchange(uri, HttpMethod.GET,
                        new HttpEntity<String>(httpHeaders), Item[].class,user.getId(),itemCount,itemCount+10).getBody();

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
                HyperLog.e(TAG,"Other exception",e3);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            doneApiCall();
        }

    }

    class AsyncItemConfirm extends AsyncTask<Long,Void,Void>
    {
        @Override
        protected Void doInBackground(Long... args) {
            Long itemId = args[0];

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/removeaccepteditem/{item_id}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                restTemplate.exchange(uri, HttpMethod.POST,
                        new HttpEntity<String>(httpHeaders), Item[].class,itemId).getBody();

                runOnUiThread(MyRequestsActivity.this::onRefresh);


            } catch (HttpServerErrorException e)
            {
                HyperLog.e(TAG,"Server exception",e);
            } catch (HttpClientErrorException e2)
            {
                HyperLog.e(TAG,"Client exception",e2);
            } catch (Exception e3)
            {
                HyperLog.e(TAG,"Other exception",e3);
            }

            return null;
        }

    }

    class AsyncItemDelete extends AsyncTask<Long,Void,Void>
    {
        @Override
        protected Void doInBackground(Long... args) {
            Long itemId = args[0];

            try {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                String uri = ConfigManager.getApiUrl(getApplicationContext())+
                        "/removeitem/{item_id}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("auth",AUTH_TOKEN);

                restTemplate.exchange(uri, HttpMethod.POST,
                        new HttpEntity<String>(httpHeaders), Item[].class,itemId).getBody();

                runOnUiThread(MyRequestsActivity.this::onRefresh);


            } catch (HttpServerErrorException e)
            {
                HyperLog.e(TAG,"Server exception",e);
            } catch (HttpClientErrorException e2)
            {
                HyperLog.e(TAG,"Client exception",e2);
            } catch (Exception e3)
            {
                HyperLog.e(TAG,"Other exception",e3);
            }

            return null;
        }

    }

    //Set up top navigatio nabr click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            DrawerLayout drawerLayout = findViewById(R.id.my_requests_dl);
            drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
