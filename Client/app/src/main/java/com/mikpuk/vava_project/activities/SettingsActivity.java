package com.mikpuk.vava_project.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.User;

import java.util.Locale;

public class SettingsActivity  extends AppCompatActivity {

    private User user = null;
    Spinner spinner;
    String language = "";
    private static final String TAG = "Settings activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HyperLog.i(TAG, "Starting settings activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        user = (User)getIntent().getSerializableExtra("user");

        //Set up navigation bar
        SceneManager.initNavigationBar(getString(R.string.navigation_settings),R.id.settings_dl,R.id.settings_navView,this,this,user);

        Button languageButton = findViewById(R.id.language_button);
        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(language);
                setLocale(language);
            }
        });

        //Load language dropdown menu
        loadLanguageSpinner();

    }

    public void loadLanguageSpinner()
    {
        HyperLog.i(TAG, "Loading language spinner");
        spinner = findViewById(R.id.language_spinner);
        String[] languages = getResources().getStringArray(R.array.languages);
        final String[] languagesShort = getResources().getStringArray(R.array.languages_short);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,languages);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(spinnerAdapter);

        spinner.getBackground().setColorFilter(Color.parseColor(getResources().getString(R.color.orangePrimary)), PorterDuff.Mode.SRC_ATOP); //Toto meni farbu tej sipocky

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = languagesShort[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setLocale(String lang) {
        HyperLog.i(TAG, "Setting location");
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        saveLocale(lang);

        Intent intent = new Intent(this, MenuScreenActivity.class);
        intent.putExtra("user",user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void saveLocale(String language) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.mikpukvava_project.PREFERENCES", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("app_language", language);
        editor.commit();
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
