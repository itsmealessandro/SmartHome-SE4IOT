package com.smart_home.manager.model;

import java.util.List;

public class Sensor {
    private String room;
    private String sensorType;
    private String value;
    private String health;
    private boolean enabled;
    private List<String> alertHistory;
    private String actuatorName;
    private String actuatorStatus;

    public Sensor() {
    }

    public Sensor(String room, String sensorType, String value, String health, boolean enabled) {
        this(room, sensorType, value, health, enabled, new java.util.ArrayList<>(), null, null);
    }

    public Sensor(String room, String sensorType, String value, String health, boolean enabled, List<String> alertHistory, String actuatorName, String actuatorStatus) {
        this.room = room;
        this.sensorType = sensorType;
        this.value = value;
        this.health = health;
        this.enabled = enabled;
        this.alertHistory = alertHistory;
        this.actuatorName = actuatorName;
        this.actuatorStatus = actuatorStatus;
    }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getSensorType() { return sensorType; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getHealth() { return health; }
    public void setHealth(String health) { this.health = health; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<String> getAlertHistory() { return alertHistory; }
    public void setAlertHistory(List<String> alertHistory) { this.alertHistory = alertHistory; }

    public String getActuatorName() { return actuatorName; }
    public void setActuatorName(String actuatorName) { this.actuatorName = actuatorName; }

    public String getActuatorStatus() { return actuatorStatus; }
    public void setActuatorStatus(String actuatorStatus) { this.actuatorStatus = actuatorStatus; }
}
