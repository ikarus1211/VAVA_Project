package com.mikpuk.vava_project.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.data.User;

public class ProfileActivity extends AppCompatActivity {

    User user;
    TextView profileNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);

        User userTmp = (User)getIntent().getSerializableExtra("user");
        user = (User)getIntent().getSerializableExtra("show_user");

        profileNameText=findViewById(R.id.profileNameText);
        profileNameText.setText(user.getUsername());

        loadProgressBar();

        //Set up navigation bar
        SceneManager.initNavigationBar(getString(R.string.navigation_profile),R.id.profile_dl,R.id.profile_navView,this,this,userTmp);
    }

    private void loadProgressBar()
    {
        ProgressBar levelBar;
        TextView levelText;
        TextView experienceText;

        levelBar = findViewById(R.id.profileLevelBar);
        levelText = findViewById(R.id.profileLevelText);
        experienceText = findViewById(R.id.profileXpText);

        int lvl = 0;
        int xp_act = user.getReputation();
        int xp_next = 10;

        while (xp_act - xp_next >= 0) {
            lvl++;
            xp_act -= xp_next;
            xp_next *= 2;
        }

        levelText.setText(getString(R.string.level)+" "+lvl);
        experienceText.setText(getString(R.string.reputation)+" "+xp_act+" / "+xp_next);
        levelBar.setMax(xp_next);
        levelBar.setProgress(xp_act);
    }

    //Set up top nav bar onClick
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.profile_dl);
            drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ProfileActivity.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
    }
}
