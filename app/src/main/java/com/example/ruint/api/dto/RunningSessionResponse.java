package com.example.ruint.api.dto;

public class RunningSessionResponse {
    private Long id;
    private Long userId;
    private Long raceId;
    private String startedAt;
    private Long elapsedSeconds;
    private Double distanceMeters;
    private Double paceMinPerKm;
    private Integer avgHeartRate;
    private Integer peakHeartRate;
    private Double elevationGainMeters;
    private Boolean active;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getRaceId() { return raceId; }
    public String getStartedAt() { return startedAt; }
    public Long getElapsedSeconds() { return elapsedSeconds; }
    public Double getDistanceMeters() { return distanceMeters; }
    public Double getPaceMinPerKm() { return paceMinPerKm; }
    public Integer getAvgHeartRate() { return avgHeartRate; }
    public Integer getPeakHeartRate() { return peakHeartRate; }
    public Double getElevationGainMeters() { return elevationGainMeters; }
    public Boolean getActive() { return active; }
}
