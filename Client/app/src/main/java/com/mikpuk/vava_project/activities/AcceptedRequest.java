package com.mikpuk.vava_project.activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.mikpuk.vava_project.Item;
import com.mikpuk.vava_project.OtherReqItemAdapter;
import com.mikpuk.vava_project.R;

import java.util.ArrayList;
import java.util.List;

public class AcceptedRequest extends AppCompatActivity {

    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_request);
        mListView = findViewById(R.id.acceptedlisView);


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
}
