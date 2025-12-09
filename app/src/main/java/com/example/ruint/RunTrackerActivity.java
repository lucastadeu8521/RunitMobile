package com.example.ruint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.ruint.api.SessionManager;

public class RunTrackerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1001;
    private static final String TAG = "RunTrackerActivity";
    private static final float MIN_DISTANCE_FOR_POINT = 10.0f; // 10 metros entre pontos

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;
    private Polyline polyline;
    private List<LatLng> pathPoints = new ArrayList<>();
    private long startTime = 0L;
    private long pausedTime = 0L;
    private double totalDistance = 0.0;
    private LatLng lastAddedPoint = null;

    private TextView tvDistance, tvPace, tvCalories, tvDuration, tvDurationLarge;
    private Chronometer chronometer;
    private ImageButton btnStartPause, btnDashboard, btnStop;
    private boolean tracking = false;
    private boolean hasPermissions = false;
    private SessionManager sessionManager;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location == null || map == null) {
                Log.d(TAG, "Location or map is null");
                return;
            }

            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d(TAG, "New location: " + currentLatLng.latitude + ", " + currentLatLng.longitude);

            boolean shouldAddPoint = lastAddedPoint == null ||
                    distanceBetween(lastAddedPoint, currentLatLng) >= MIN_DISTANCE_FOR_POINT;

            if (shouldAddPoint) {
                if (!pathPoints.isEmpty()) {
                    LatLng lastPoint = pathPoints.get(pathPoints.size() - 1);
                    float distance = distanceBetween(lastPoint, currentLatLng);
                    totalDistance += distance;
                    Log.d(TAG, "Distance added: " + distance + " meters");
                }

                pathPoints.add(currentLatLng);
                lastAddedPoint = currentLatLng;

                updateOptimizedPolyline();
            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f));


            double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
            double distanceKm = totalDistance / 1000.0;
            double pace = distanceKm > 0 ? (elapsedTime / 60.0) / distanceKm : 0.0;
            double speed = location.getSpeed() * 3.6;

            runOnUiThread(() -> updateUI(distanceKm, pace, speed, elapsedTime));
        }
    };

    private float distanceBetween(LatLng point1, LatLng point2) {
        float[] results = new float[1];
        Location.distanceBetween(
                point1.latitude, point1.longitude,
                point2.latitude, point2.longitude,
                results
        );
        return results[0];
    }

    private void updateOptimizedPolyline() {
        if (polyline != null) polyline.remove();

        if (pathPoints.size() >= 2) {

            polyline = map.addPolyline(new PolylineOptions()
                    .addAll(pathPoints)
                    .width(15f)
                    .color(0xFF215FB8)
                    .geodesic(true));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_tracker);

        sessionManager = new SessionManager(this);
        initializeViews();
        setupBottomNavigation();
        setupClickListeners();
        setupDashboardButton();
        setupBackPressedHandler();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Map fragment not found!");
        }

        checkPermissions();
    }

    private void initializeViews() {
        tvDistance = findViewById(R.id.tvDistance);
        tvPace = findViewById(R.id.tvPace);
        tvCalories = findViewById(R.id.tvCalories);
        tvDuration = findViewById(R.id.tvDuration);
        tvDurationLarge = findViewById(R.id.tvDurationLarge);
        chronometer = findViewById(R.id.chronometer);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnDashboard = findViewById(R.id.btnDashboard);
        btnStop = findViewById(R.id.btnStop);

        updateButtonStates();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.run);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.run) {
                    return true;
                } else if (itemId == R.id.historico) {
                    startActivity(new Intent(getApplicationContext(), RunHistoryActivity.class));
                    overridePendingTransition(0, 0);
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

    private void setupClickListeners() {
        btnStartPause.setOnClickListener(v -> {
            Log.d(TAG, "Start/Pause button clicked");
            if (!tracking) {
                startTracking();
            } else {
                pauseTracking();
            }
        });

        btnStop.setOnClickListener(v -> {
            Log.d(TAG, "Stop button clicked");
            if (tracking || isChronometerRunning()) {
                showStopConfirmationDialog();
            } else {
                Toast.makeText(this, "Nenhuma corrida em andamento", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setupDashboardButton() {
        btnDashboard.setOnClickListener(v -> {
            Log.d(TAG, "Dashboard button clicked");
            Intent intent = new Intent(RunTrackerActivity.this, DashboardActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (tracking) {
                    Toast.makeText(RunTrackerActivity.this,
                            "Pare o tracking antes de sair", Toast.LENGTH_SHORT).show();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            hasPermissions = true;
            Log.d(TAG, "Location permissions granted");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void startTracking() {
        if (!hasPermissions) {
            Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_SHORT).show();
            checkPermissions();
            return;
        }

        tracking = true;

        if (pausedTime > 0) {
            long currentTime = System.currentTimeMillis();
            long elapsedWhilePaused = currentTime - pausedTime;
            startTime += elapsedWhilePaused;
            chronometer.setBase(chronometer.getBase() + elapsedWhilePaused);
            pausedTime = 0;
        } else {
            startTime = System.currentTimeMillis();
            chronometer.setBase(startTime);
        }

        chronometer.start();

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1000)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        updateButtonStates();
        Toast.makeText(this, "Tracking iniciado", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Tracking started - StartTime: " + startTime);
    }

    private void pauseTracking() {
        tracking = false;
        chronometer.stop();
        fusedLocationClient.removeLocationUpdates(locationCallback);

        pausedTime = System.currentTimeMillis();

        updateButtonStates();
        Toast.makeText(this, "Tracking pausado", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Tracking paused at: " + pausedTime);
    }

    private void showStopConfirmationDialog() {
        stopTracking();
    }

    private void stopTracking() {
        tracking = false;
        chronometer.stop();
        fusedLocationClient.removeLocationUpdates(locationCallback);

        long duration = (System.currentTimeMillis() - startTime) / 1000;
        double distanceKm = totalDistance / 1000.0;
        double averagePace = distanceKm > 0 ? (duration / 60.0) / distanceKm : 0.0;
        double hours = duration / 3600.0;
        double calories = 70.0 * 5.0 * hours;

        if (distanceKm > 0.01) {
            saveRunData(distanceKm, duration, averagePace, calories, startTime);
            Toast.makeText(this, "Corrida salva com sucesso!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Corrida muito curta para salvar", Toast.LENGTH_SHORT).show();
        }

        pathPoints.clear();
        totalDistance = 0;
        lastAddedPoint = null;
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }

        updateButtonStates();
        updateUI(0, 0, 0, 0);
        Log.d(TAG, "Tracking stopped and saved");
    }

    private boolean isChronometerRunning() {
        return chronometer != null && chronometer.getBase() > 0;
    }

    private void updateButtonStates() {
        if (tracking) {
            btnStartPause.setImageResource(android.R.drawable.ic_media_pause);
            btnStop.setEnabled(true);
            btnStop.setAlpha(1.0f);
        } else {
            btnStartPause.setImageResource(android.R.drawable.ic_media_play);

            boolean hasActiveRun = isChronometerRunning() && startTime > 0;
            btnStop.setEnabled(hasActiveRun);
            btnStop.setAlpha(hasActiveRun ? 1.0f : 0.5f);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        Log.d(TAG, "Map is ready");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);

            map.getUiSettings().setZoomGesturesEnabled(true);
            map.getUiSettings().setScrollGesturesEnabled(true);
        }
    }

    private void updateUI(double distanceKm, double pace, double speed, double elapsedSeconds) {
        tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", distanceKm));

        if (pace > 0) {
            int minutes = (int) pace;
            int seconds = (int) ((pace - minutes) * 60);
            tvPace.setText(String.format(Locale.getDefault(), "%02d:%02d /km", minutes, seconds));
        } else {
            tvPace.setText("00:00 /km");
        }

        double hours = elapsedSeconds / 3600.0;
        double calories = 70.0 * 8.0 * hours;
        tvCalories.setText(String.format(Locale.getDefault(), "%.0f cal", calories));

        long seconds = (long) elapsedSeconds;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        String durationText = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
        tvDurationLarge.setText(durationText);
    }

    private void saveRunData(double distanceKm, long duration, double averagePace, double calories, long startedAtMillis) {
        RunData runData = new RunData(distanceKm, duration, averagePace, calories);
        sessionManager.addRun(runData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermissions = true;
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Location permission granted");

                if (map != null) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        map.setMyLocationEnabled(true);
                    }
                }
            } else {
                hasPermissions = false;
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Location permission denied");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
