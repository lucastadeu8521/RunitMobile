package com.example.ruint.api.dto;

public class RaceResponse {
    private Long id;
    private String name;
    private String raceDate;
    private String venueName;
    private String registrationUrl;
    private String organizerContact;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private Integer maxParticipants;
    private Double raceDistanceKm;
    private String status;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getRaceDate() { return raceDate; }
    public String getVenueName() { return venueName; }
    public String getRegistrationUrl() { return registrationUrl; }
    public String getOrganizerContact() { return organizerContact; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public Double getRaceDistanceKm() { return raceDistanceKm; }
    public String getStatus() { return status; }
}
