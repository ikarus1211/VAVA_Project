package com.mikpuk.vava_project.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.ConfigManager;
import com.mikpuk.vava_project.MD5Hashing;
import com.mikpuk.vava_project.R;
import com.mikpuk.vava_project.SceneManager;
import com.mikpuk.vava_project.data.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Activity which controls profile screen
 */
public class ProfileActivity extends AppCompatActivity {

    User user;
    TextView profileNameText;
    User userTmp;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);

        userTmp = (User)getIntent().getSerializableExtra("user");
        user = (User)getIntent().getSerializableExtra("show_user");

        profileNameText=findViewById(R.id.profileNameText);
        profileNameText.setText(user.getUsername());

        loadProgressBar();

        //Set up navigation bar
        SceneManager.initNavigationBar(getString(R.string.navigation_profile),R.id.profile_dl,R.id.profile_navView,this,this,userTmp);
    }

    /**
     * Function creates Progress bar which shows current user level
     */
    private void loadProgressBar()
    {
        profileNameText.setText(user.getUsername());

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

    @Override
    protected void onResume() {
        super.onResume();
        if (user.getId() != userTmp.getId())
            return;
        new Thread() {
            public void run() {
                String AUTH_TOKEN = ConfigManager.getAuthToken(getApplicationContext());

                try {
                    String uri = ConfigManager.getApiUrl(getApplicationContext()) + "/getuserbyid/{id}";
                    RestTemplate restTemplate = new RestTemplate();

                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("auth", AUTH_TOKEN);

                    //Returns logged in user
                    ResponseEntity<User> usertmp = restTemplate.exchange(uri, HttpMethod.GET,
                            new HttpEntity<String>(httpHeaders), User.class,
                            user.getId());
                    user = usertmp.getBody();
                    userTmp = user;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadProgressBar();
                        }
                    });
                } catch (HttpServerErrorException e) {
                    HyperLog.e(TAG, "Server exception " + e);
                } catch (HttpClientErrorException e2) {
                    HyperLog.e(TAG, "Client Exception " + e2);
                } catch (Exception e3) {
                    HyperLog.e(TAG, "Unknown exception while signing in " + e3);
                    e3.printStackTrace();
                }
            }
        }.start();
    }
}
