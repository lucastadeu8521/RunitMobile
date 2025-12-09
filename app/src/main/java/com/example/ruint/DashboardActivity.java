package com.example.ruint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotalDistance, tvTotalTime, tvTotalRuns;
    private TextView tvRecentDistance, tvRecentPace;
    private RecyclerView rvRuns;
    private RunAdapter runAdapter;
    private List<RunData> allRuns = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "RunTrackerPrefs";
    private static final String RUNS_KEY = "saved_runs";
    private static final double DEFAULT_WEIGHT_KG = 70.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
        String runsJson = sharedPreferences.getString(RUNS_KEY, "");
        if (!runsJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<RunData>>() {}.getType();
            List<RunData> savedRuns = gson.fromJson(runsJson, type);
            if (savedRuns != null) {
                allRuns.addAll(savedRuns);
            }
        }
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
        Gson gson = new Gson();
        String runsJson = gson.toJson(allRuns);
        sharedPreferences.edit().putString(RUNS_KEY, runsJson).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveRunsToPrefs();
    }
}
