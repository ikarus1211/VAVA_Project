package com.mikpuk.vava_project.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.mikpuk.vava_project.R;

public class PopUpOtherRequest extends AppCompatActivity {

    ImageView accpet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_other_request);
        accpet.findViewById(R.id.image_view_accpet);
        accpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("dede");
            }
        });

    }
}
