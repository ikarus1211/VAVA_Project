package com.mikpuk.vava_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.mikpuk.vava_project.R;

//This activity makes starting animation
public class StartSplashActivity extends AppCompatActivity {

    float distance;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_splash_screen);

        imageView = findViewById(R.id.splashImage);
        distance = getResources().getDimensionPixelSize(R.dimen.distance);
        imageView.animate().translationY(distance).setDuration(1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadLoginScreen();
            }
        },1100);

    }

    private void loadLoginScreen()
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}
