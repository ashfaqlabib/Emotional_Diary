package com.example.emotionaldiary;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.emotionaldiary.fragments.DiaryFragment;
import com.example.emotionaldiary.fragments.HomeFragment;
import com.example.emotionaldiary.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            0
                    );

                    return insets;
                }
        );

        bottomNavigation =
                findViewById(R.id.bottomNavigation);

        // =========================
        // DEFAULT HOME FRAGMENT
        // =========================

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // =========================
        // BOTTOM NAVIGATION
        // =========================

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                fragment = new HomeFragment();

            } else if (id == R.id.nav_diary) {

                fragment = new DiaryFragment();

            } else if (id == R.id.nav_profile) {

                fragment = new ProfileFragment();
            }

            if (fragment != null) {

                loadFragment(fragment);

                return true;
            }

            return false;
        });
    }

    // =========================
    // LOAD FRAGMENT
    // =========================

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}