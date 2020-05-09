package com.mikpuk.vava_project.activities;


import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;

public class PopUpMyRequest extends AppCompatActivity
{

    private static final String TAG = "My Custom Dialog";

    TextView closeButton;
    TextView accpetButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_my_request);
        closeButton.findViewById(R.id.popTxtClose);
        accpetButton.findViewById(R.id.accept);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }
}
