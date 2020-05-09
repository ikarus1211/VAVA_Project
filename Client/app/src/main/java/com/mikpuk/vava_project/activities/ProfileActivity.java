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
import com.mikpuk.vava_project.User;

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

        levelText.setText("Level "+lvl);
        experienceText.setText("Reputation "+xp_act+" / "+xp_next);
        levelBar.setMax(xp_next);
        levelBar.setProgress(xp_act);
    }

    //Nastavenie kliknutia na hornu listu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.profile_dl);
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        ProfileActivity.this.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_from_right);
    }
}
