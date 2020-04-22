package com.mikpuk.vava_project.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.User;

import java.util.Locale;

public class SettingsActivity  extends AppCompatActivity {

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        user = (User)getIntent().getSerializableExtra("user");

        //Set up navigation bar
        SceneManager.initNavigationBar("Settings menu",R.id.settings_dl,R.id.settings_navView,this,this,user);

        Button languageButton = findViewById(R.id.language_button);
        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("sk");
            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, SettingsActivity.class);
        refresh.putExtra("user",user);
        startActivity(refresh);
        finish();
    }

    //Nastavenie kliknutia na hornu listu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.settings_dl);
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
