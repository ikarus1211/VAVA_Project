package com.mikpuk.vava_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.activities.AcceptedRequest;
import com.mikpuk.vava_project.activities.CreateMyRequestActivity;
import com.mikpuk.vava_project.activities.LoginActivity;
import com.mikpuk.vava_project.activities.MapViewActivity;
import com.mikpuk.vava_project.activities.MenuScreenActivity;
import com.mikpuk.vava_project.activities.MyRequestsActivity;
import com.mikpuk.vava_project.activities.ProfileActivity;
import com.mikpuk.vava_project.activities.RegistrationActivity;
import com.mikpuk.vava_project.activities.SettingsActivity;
import com.mikpuk.vava_project.data.User;

//This class is taking care of transitions between activities, toolbar and navigation bar
public class SceneManager {

    //Sets up top and left navigation bar
    public static void initNavigationBar(String title,int drawerLayoutID,int navViewID, final Context context, final AppCompatActivity appCompatActivity, final User user)
    {
        ActionBarDrawerToggle toggle;
        NavigationView navigationView;

        //Setting up top bar
        Toolbar toolbar = appCompatActivity.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navigation);
        toolbar.setTitle(title);
        appCompatActivity.setSupportActionBar(toolbar);

        //Setting up left navigation bar
        final DrawerLayout drawerLayout = (DrawerLayout)appCompatActivity.findViewById(drawerLayoutID);
        toggle = new ActionBarDrawerToggle(appCompatActivity, drawerLayout,R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setting onClick on left navigation bar
        navigationView = (NavigationView)appCompatActivity.findViewById(navViewID);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account:
                        loadMyProfile(context,user);
                        if(context instanceof ProfileActivity){
                            appCompatActivity.finish();
                        }
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.settings:
                        loadSettingsMenu(context,user);
                        if(context instanceof SettingsActivity){
                            appCompatActivity.finish();
                        }
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.main_menu:
                        loadMainMenu(context,user,false);
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
                        loadMyRequests(context,user,false);
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

    public static void loadMyRequests(Context context, User user,boolean isFromNewRequest)
    {
        Intent intent = new Intent(context, MyRequestsActivity.class);
        intent.putExtra("user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        if(!isFromNewRequest)
            ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        else
            ((Activity)context).overridePendingTransition(R.anim.in_from_top,
            R.anim.out_from_bottom);
    }

    private static void loadMyProfile(Context context, User user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("show_user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void loadOtherProfile(Context context, User user, User showUserProfile) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("show_user",showUserProfile);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private static void loadAcceptedRequests(Context context, User user)
    {
        Intent intent = new Intent(context, AcceptedRequest.class);
        intent.putExtra("user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private static void logOut(final Context context)
    {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(context.getString(R.string.log_out_title))
                .setMessage(context.getString(R.string.log_out_desc))
                .setPositiveButton(context.getString(R.string.log_out_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(context.getString(R.string.log_out_no), null)
                .show();
    }

    public static void loadMainMenu(Context context, User user, boolean isFromLoginScreen)
    {
        Intent intent = new Intent(context, MenuScreenActivity.class);
        intent.putExtra("user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if(isFromLoginScreen)
            ((Activity)context).finish();
    }

    private static void loadSettingsMenu(Context context, User user)
    {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("user",user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private static void loadMap(Context context, User user)
    {
        Intent intent = new Intent(context, MapViewActivity.class);
        AppLocationManager appLocationManager = new AppLocationManager(context);
        intent.putExtra("user",user);
        intent.putExtra("location", appLocationManager.getmLocation());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void loadRegistrationScreen(Context context)
    {
        Intent intent = new Intent(context, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
    }

    public static void loadLoginScreen(Context context)
    {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clear back button history
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }

    public static void loadNewRequest(Context context, User user)
    {
        Intent intent = new Intent(context, CreateMyRequestActivity.class);
        intent.putExtra("user",user);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.in_from_bottom, R.anim.out_from_top);
    }


}
