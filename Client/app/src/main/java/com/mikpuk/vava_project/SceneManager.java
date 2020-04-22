package com.mikpuk.vava_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mikpuk.vava_project.activities.AcceptedRequest;
import com.mikpuk.vava_project.activities.LoginActivity;
import com.mikpuk.vava_project.activities.MapViewActivity;
import com.mikpuk.vava_project.activities.MenuScreenActivity;
import com.mikpuk.vava_project.activities.MyRequestsActivity;
import com.mikpuk.vava_project.activities.SettingsActivity;

public class SceneManager {

    public static void initNavigationBar(String title,int drawerLayoutID,int navViewID, final Context context, final AppCompatActivity appCompatActivity, final User user)
    {
        ActionBarDrawerToggle toggle;
        NavigationView navigationView;

        //Nastavenie horneho panelu
        Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navigation);
        toolbar.setTitle(title);
        appCompatActivity.setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Nastavenie laveho panelu
        final DrawerLayout drawerLayout = (DrawerLayout)appCompatActivity.findViewById(drawerLayoutID);
        toggle = new ActionBarDrawerToggle(appCompatActivity, drawerLayout,R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Nastavenie kliku na itemy v lavom paneli
        navigationView = (NavigationView)appCompatActivity.findViewById(navViewID);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account:
                        //TODO
                        break;
                    case R.id.settings:
                        loadSettingsMenu(context,user);
                        if(context instanceof SettingsActivity){
                            appCompatActivity.finish();
                        }
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.main_menu:
                        loadMainMenu(context,user);
                        if(context instanceof MenuScreenActivity) {
                            appCompatActivity.finish();
                        }
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.load_map:
                        loadMap(context,user);
                        if(context instanceof MapViewActivity){
                            appCompatActivity.finish();
                        }
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.my_requests:
                        loadMyRequests(context,user);
                        if(context instanceof MyRequestsActivity){
                            appCompatActivity.finish();
                        }
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.accepted_requests:
                        loadAcceptedRequests(context,user);
                        if(context instanceof AcceptedRequest){
                            appCompatActivity.finish();
                        }
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.log_out:
                        logOut(context);
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });

        TextView textView = navigationView.getHeaderView(0).findViewById(R.id.navview_name);
        textView.setText(user.getUsername());
    }

    private static void loadMyRequests(Context context, User user)
    {
        Intent intent = new Intent(context, MyRequestsActivity.class);
        intent.putExtra("user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void loadAcceptedRequests(Context context, User user)
    {
        Intent intent = new Intent(context, AcceptedRequest.class);
        intent.putExtra("user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void logOut(final Context context)
    {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Do you want to log out?")
                .setMessage("Do you really want to log out?")
                .setPositiveButton("Yes, log out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("No, go back", null)
                .show();
    }

    private static void loadMainMenu(Context context, User user)
    {
        Intent intent = new Intent(context, MenuScreenActivity.class);
        intent.putExtra("user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void loadSettingsMenu(Context context, User user)
    {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void loadMap(Context context, User user)
    {
        System.out.println("LOAD MAP START");
        Intent intent = new Intent(context, MapViewActivity.class);
        Location mLocation = new AppLocationManager(context).getmLocation();
        intent.putExtra("user",user);
        intent.putExtra("location", mLocation);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        System.out.println("LOAD MAP STOP");
    }
}
