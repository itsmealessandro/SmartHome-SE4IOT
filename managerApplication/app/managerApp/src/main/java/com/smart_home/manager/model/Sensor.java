package com.smart_home.manager.model;

public class Sensor {
    private String room;
    private String sensorType;
    private String health;
    private boolean enabled;

    public Sensor() {}

    public Sensor(String room, String sensorType, String health, boolean enabled) {
        this.room = room;
        this.sensorType = sensorType;
        this.health = health;
        this.enabled = enabled;
    }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getSensorType() { return sensorType; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }

    public String getHealth() { return health; }
    public void setHealth(String health) { this.health = health; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
