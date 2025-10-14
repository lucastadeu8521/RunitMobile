package com.example.ruint;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RunHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_history);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.historico);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.run) {
                    startActivity(new Intent(getApplicationContext(), RunTrackerActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.historico) {
                    return true;
                } else if (itemId == R.id.perfil) {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
}
