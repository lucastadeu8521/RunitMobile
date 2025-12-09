package com.example.ruint;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.ruint.api.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotalDistance, tvTotalTime, tvTotalRuns;
    private TextView tvRecentDistance, tvRecentPace;
    private RecyclerView rvRuns;
    private RunAdapter runAdapter;
    private List<RunData> allRuns = new ArrayList<>();

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);
        initializeViews();
        setupBottomNavigation();
        loadSavedRuns();
        setupRecyclerView();
        updateDashboard();
    }

    private void initializeViews() {
        tvTotalDistance = findViewById(R.id.tvTotalDistance);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvTotalRuns = findViewById(R.id.tvTotalRuns);
        tvRecentDistance = findViewById(R.id.tvRecentDistance);
        tvRecentPace = findViewById(R.id.tvRecentPace);
        rvRuns = findViewById(R.id.rvRuns);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.perfil);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.run) {
                    startActivity(new Intent(getApplicationContext(), RunTrackerActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.historico) {
                    startActivity(new Intent(getApplicationContext(), RunHistoryActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.perfil) {
                    return true;
                }
                return false;
            }
        });
    }

    private void setupRecyclerView() {
        runAdapter = new RunAdapter(allRuns);
        rvRuns.setLayoutManager(new LinearLayoutManager(this));
        rvRuns.setAdapter(runAdapter);
    }

    private void loadSavedRuns() {
        List<RunData> savedRuns = sessionManager.getSavedRuns();
        allRuns.clear();
        allRuns.addAll(savedRuns);
    }

    private void updateDashboard() {
        if (allRuns.isEmpty()) {
            showEmptyState();
            return;
        }

        updateWeeklyStats();

        updateRecentRun();

        runAdapter.updateData(allRuns);
    }

    private void updateWeeklyStats() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        long startOfWeek = calendar.getTimeInMillis();

        double totalDistance = 0;
        long totalDuration = 0;
        int weeklyRuns = 0;

        for (RunData run : allRuns) {
            if (run.getTimestamp() >= startOfWeek) {
                totalDistance += run.getDistance();
                totalDuration += run.getDuration();
                weeklyRuns++;
            }
        }

        tvTotalDistance.setText(String.format("%.2f", totalDistance));
        tvTotalTime.setText(formatTime(totalDuration));
        tvTotalRuns.setText(String.valueOf(weeklyRuns));
    }

    private void updateRecentRun() {
        if (!allRuns.isEmpty()) {
            RunData recentRun = allRuns.get(allRuns.size() - 1);
            tvRecentDistance.setText(recentRun.getFormattedDistance());
            tvRecentPace.setText(recentRun.getFormattedPace());
        }
    }

    private void showEmptyState() {
        tvTotalDistance.setText("0.00");
        tvTotalTime.setText("00:00");
        tvTotalRuns.setText("0");
        tvRecentDistance.setText("Nenhuma corrida registrada");
        tvRecentPace.setText("");
    }

    private String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    public void saveRun(RunData runData) {
        allRuns.add(runData);
        saveRunsToPrefs();
        updateDashboard();
    }

    private void saveRunsToPrefs() {
        sessionManager.saveRuns(allRuns);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveRunsToPrefs();
    }
}
