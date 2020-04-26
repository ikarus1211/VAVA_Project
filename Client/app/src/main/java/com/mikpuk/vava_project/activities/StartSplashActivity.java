package com.mikpuk.vava_project.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mikpuk.vava_project.R;

public class StartSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_splash_screen);

        ImageView imageView = findViewById(R.id.splashImage);
        ConstraintLayout constraintLayout = findViewById(R.id.splashView);
        //Animation a = AnimationUtils.loadAnimation(this, R.anim.zoom_in_slow);
        //a.reset();
        //imageView.clearAnimation();
        //imageView.startAnimation(a);

        float distance = getResources().getDimensionPixelSize(R.dimen.distance);

        imageView.animate().translationY(distance).setDuration(1000);


        /*final int newLeftMargin = 400;
        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.LayoutParams params = constraintLayout.getLayoutParams();
                params.leftMargin = (int)(newLeftMargin * interpolatedTime);
                yourView.setLayoutParams(params);
            }
        };
        a.setDuration(500); // in ms
        yourView.startAnimation(a);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadLoginScreen();
            }
        },1100);

        /*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in_slow, R.anim.fade_out);
        finish();*/

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
