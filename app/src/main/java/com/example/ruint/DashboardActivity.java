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
import com.example.ruint.api.ApiClient;
import com.example.ruint.api.ApiService;
import com.example.ruint.api.SessionManager;
import com.example.ruint.api.dto.RunningSessionResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotalDistance, tvTotalTime, tvTotalRuns;
    private TextView tvRecentDistance, tvRecentPace;
    private RecyclerView rvRuns;
    private RunAdapter runAdapter;
    private List<RunData> allRuns = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private ApiService apiService;
    private SessionManager sessionManager;
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
        loadRemoteRuns();
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
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getService(this);
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

    private void loadRemoteRuns() {
        if (!sessionManager.isLoggedIn()) {
            return;
        }

        apiService.getSessions().enqueue(new Callback<List<RunningSessionResponse>>() {
            @Override
            public void onResponse(Call<List<RunningSessionResponse>> call, Response<List<RunningSessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RunData> remoteRuns = new ArrayList<>();
                    for (RunningSessionResponse session : response.body()) {
                        remoteRuns.add(mapSessionToRun(session));
                    }
                    allRuns.clear();
                    allRuns.addAll(remoteRuns);
                    saveRunsToPrefs();
                    updateDashboard();
                }
            }

            @Override
            public void onFailure(Call<List<RunningSessionResponse>> call, Throwable t) {
                // Keep offline data if API fails
            }
        });
    }

    private RunData mapSessionToRun(RunningSessionResponse session) {
        RunData runData = new RunData();
        runData.setId(session.getId() != null ? String.valueOf(session.getId()) : String.valueOf(System.currentTimeMillis()));
        double distanceKm = session.getDistanceMeters() != null ? session.getDistanceMeters() / 1000.0 : 0.0;
        runData.setDistance(distanceKm);
        runData.setDuration(session.getElapsedSeconds() != null ? session.getElapsedSeconds() : 0);
        runData.setAveragePace(session.getPaceMinPerKm() != null ? session.getPaceMinPerKm() : 0.0);

        long timestamp = parseSessionDate(session.getStartedAt());
        runData.setTimestamp(timestamp);
        runData.setDateString(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(timestamp)));

        double hours = runData.getDuration() / 3600.0;
        double calories = DEFAULT_WEIGHT_KG * 8.0 * hours;
        runData.setCalories(calories);

        return runData;
    }

    private long parseSessionDate(String dateString) {
        if (dateString == null) {
            return System.currentTimeMillis();
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
            Date parsed = dateFormat.parse(dateString);
            if (parsed != null) {
                return parsed.getTime();
            }
        } catch (ParseException ignored) {
        }
        return System.currentTimeMillis();
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
