package com.amdocs.sas.bean;

public class AccessPoint {
    private int accessPointId;
    private String locationName;

    public AccessPoint() {
    }

    public AccessPoint(int accessPointId, String locationName) {
        this.accessPointId = accessPointId;
        this.locationName = locationName;
    }

    public int getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(int accessPointId) {
        this.accessPointId = accessPointId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
