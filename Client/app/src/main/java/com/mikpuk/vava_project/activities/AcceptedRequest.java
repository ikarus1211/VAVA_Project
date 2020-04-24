package com.mikpuk.vava_project.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.OtherReqItemAdapter;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.User;

import java.util.ArrayList;
import java.util.List;

public class AcceptedRequest extends AppCompatActivity {

    private ListView mListView;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_request);
        mListView = findViewById(R.id.acceptedlisView);
        user = (User)getIntent().getSerializableExtra("user");

        //Set up navigation bar
        SceneManager.initNavigationBar(getString(R.string.navigation_accepted_requests),R.id.accepted_requests_dl,R.id.accepted_requests_navView,this,this,user);
    }


    private void mListFill(Item[] items)
    {
        List<Item> itemList = new ArrayList<>();
        for (Item item:items
        ) {
            itemList.add(item);
        }

        final OtherReqItemAdapter adapter = new OtherReqItemAdapter(this, R.layout.item_my_request, itemList);

        runOnUiThread(new Runnable() {
            public void run() {
                mListView.setAdapter(adapter);
            }
        });
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
}
