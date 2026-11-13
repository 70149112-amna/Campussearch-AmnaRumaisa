package com.campussearch.app.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.campussearch.app.R;
import com.campussearch.app.data.SessionManager;
import com.campussearch.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No app screen opens without login
        if (!new SessionManager(this).isLoggedIn()) {
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment f;
            if (id == R.id.nav_home) f = new HomeFragment();
            else if (id == R.id.nav_nearby) f = new NearbyFragment();
            else if (id == R.id.nav_report) f = new ReportFragment();
            else f = new ProfileFragment();

            b.toolbar.setTitle(item.getTitle());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, f)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            b.bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            b.toolbar.setTitle(b.bottomNav.getMenu()
                    .findItem(b.bottomNav.getSelectedItemId()).getTitle());
        }
    }

    public void openTab(int menuId) {
        b.bottomNav.setSelectedItemId(menuId);
    }
}
