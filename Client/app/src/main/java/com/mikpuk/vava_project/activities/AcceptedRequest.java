package com.mikpuk.vava_project.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.OtherReqItemAdapter;
import com.mikpuk.vava_project.PaginationScrollListener;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.RecViewAdapter;

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
    private Item[] fetchedItems;

    int itemCount = 0;

    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_request);


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


    @Override
    public void onItemClick(int posistion) {

    }
}
