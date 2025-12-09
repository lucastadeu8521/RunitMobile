package com.example.ruint;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.ruint.api.SessionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RunHistoryActivity extends AppCompatActivity {

    private RecyclerView rvRunHistory;
    private SearchView searchView;
    private SearchableRunAdapter runAdapter;
    private SessionManager sessionManager;
    private final List<RunData> runs = new ArrayList<>();
    private Set<String> markedRunIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_history);

        sessionManager = new SessionManager(this);
        rvRunHistory = findViewById(R.id.rvRunHistory);
        searchView = findViewById(R.id.searchRuns);

        setupBottomNavigation();
        setupSearchList();
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

    private void setupSearchList() {
        loadRunsFromSession();
        markedRunIds = sessionManager.getMarkedRunIds();

        runAdapter = new SearchableRunAdapter(runs, markedRunIds, this::persistMarkedRuns);
        rvRunHistory.setLayoutManager(new LinearLayoutManager(this));
        rvRunHistory.setAdapter(runAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                runAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                runAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void loadRunsFromSession() {
        runs.clear();
        List<RunData> savedRuns = sessionManager.getSavedRuns();
        if (savedRuns.isEmpty()) {
            savedRuns = createSampleRuns();
            sessionManager.saveRuns(savedRuns);
        }
        runs.addAll(savedRuns);
    }

    private List<RunData> createSampleRuns() {
        List<RunData> sample = new ArrayList<>();
        sample.add(new RunData(5.2, 28 * 60 + 15, 5.1, 320));
        sample.add(new RunData(7.4, 42 * 60 + 30, 5.7, 450));
        sample.add(new RunData(10.0, 56 * 60, 5.6, 640));
        sample.add(new RunData(3.5, 20 * 60 + 10, 5.7, 210));
        sample.add(new RunData(12.3, 75 * 60 + 45, 6.1, 820));
        return sample;
    }

    private void persistMarkedRuns(Set<String> markedIds) {
        sessionManager.saveMarkedRunIds(markedIds);
        Toast.makeText(this, "Salvo na sessão", Toast.LENGTH_SHORT).show();
    }
}
