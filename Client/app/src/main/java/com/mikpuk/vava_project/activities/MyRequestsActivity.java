package com.mikpuk.vava_project.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MenuItem;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.AppLocationManager;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.MyReqItemAdapter;
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



import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.mikpuk.vava_project.PaginationScrollListener.PAGE_START;


/*
    Class for displaying request that user created
 */
public class MyRequestsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RecViewAdapter.OnItemListener {

    Button createReq = null;
    User user = null;
    private Dialog mDialog;
    private AppLocationManager appLocationManager;


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
    private static final String TAG = "User requests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HyperLog.i(TAG,"My request activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_requests);
        createReq = findViewById(R.id.createButton101);

        user = (User)getIntent().getSerializableExtra("user");
        //Set up navigation bar
        Toolbar test = findViewById(R.id.toolbar);

        SceneManager.initNavigationBar(getString(R.string.navigation_my_requests),R.id.my_requests_dl,R.id.my_requests_navView,this,this,user);

        createReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCreateReqWindow();
            }
        });
        mDialog = new Dialog(this);
        user = (User)getIntent().getSerializableExtra("user");
        /*AsyncMyItemsGetter asyncItemGetter = new AsyncMyItemsGetter();
        asyncItemGetter.execute();*/

        HyperLog.i(TAG,"Creating recycle view");
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
        Button finishButton;

        txtclose = mDialog.findViewById(R.id.popTxtClose);
        textName = mDialog.findViewById(R.id.popMyName);
        textItemName = mDialog.findViewById(R.id.popItemName);
        textDescription = mDialog.findViewById(R.id.popMyDescription);
        textAddress = mDialog.findViewById(R.id.popAddress);
        finishButton = mDialog.findViewById(R.id.finish101);


        textName.setText(user.getUsername());
        Item item = items.get(pos);
        textItemName.setText(item.getName());
        textDescription.setText(item.getDescription());
        textAddress.setText(appLocationManager.generateAddress(item.getLatitude(), item.getLongtitude()));

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ak je prijaty tak potvrdit dorucenie ak nie tak potvrdit zrusenie
                if(item.isAccepted()) {
                    new AlertDialog.Builder(view.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.request_confirm_title))
                            .setMessage(getString(R.string.request_confirm_desc))
                            .setPositiveButton(getString(R.string.answer_yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new AsyncItemConfirm().execute(item.getId());
                                    mDialog.dismiss();
                                }
                            })
                            .setNegativeButton(getString(R.string.answer_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDialog.dismiss();
                                }
                            })
                            .show();
                }else {
                    new AlertDialog.Builder(view.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.request_delete_title))
                            .setMessage(getString(R.string.request_delete_desc))
                            .setPositiveButton(getString(R.string.answer_yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new AsyncItemDelete().execute(item.getId());
                                    mDialog.dismiss();
                                }
                            })
                            .setNegativeButton(getString(R.string.answer_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

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

    /**
     * do api call here to fetch data from server
     * In example i'm adding data manually
     */

    private void doApiCall() {
        if(allItemsLoaded){
            System.out.println("ALL ITEMS ARE LOADED");
            return;
        }

        items.clear();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                fetchedItems = new Item[0];

                new AsyncMyItemsGetter().execute();

                while(fetchedItems.length == 0)
                {
                    if(allItemsLoaded)  {
                        swipeRefresh.setRefreshing(false);
                        adapter.removeLoading();
                        showToast("There are no more items left!");
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


                if(fetchedItems.length == 0) {
                    allItemsLoaded = true;
                    return null;
                }
                HyperLog.i(TAG,"Loading new items");
                showToast("LOADING NEW ITEMS");

                itemCount+=10;

            } catch (HttpServerErrorException e)
            {
                HyperLog.e(TAG,"Server exception",e);
                //Error v pripade chyby servera
                System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
                showToast("SERVER ERROR "+e.getStatusCode());
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onRefresh();
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onRefresh();
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
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.my_requests_dl);
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
