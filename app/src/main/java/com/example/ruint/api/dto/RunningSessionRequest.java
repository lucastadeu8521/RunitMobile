package com.example.ruint.api.dto;

public class RunningSessionRequest {
    private Long raceId;
    private String startedAt;
    private Long elapsedSeconds;
    private Double distanceMeters;
    private Double paceMinPerKm;
    private Integer avgHeartRate;
    private Integer peakHeartRate;
    private Double elevationGainMeters;
    private Boolean active;

    public RunningSessionRequest(Long raceId, String startedAt, Long elapsedSeconds, Double distanceMeters,
                                 Double paceMinPerKm, Integer avgHeartRate, Integer peakHeartRate,
                                 Double elevationGainMeters, Boolean active) {
        this.raceId = raceId;
        this.startedAt = startedAt;
        this.elapsedSeconds = elapsedSeconds;
        this.distanceMeters = distanceMeters;
        this.paceMinPerKm = paceMinPerKm;
        this.avgHeartRate = avgHeartRate;
        this.peakHeartRate = peakHeartRate;
        this.elevationGainMeters = elevationGainMeters;
        this.active = active;
    }

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
