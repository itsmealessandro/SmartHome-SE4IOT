package com.smart_home.manager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SensorTest {

    @Test
    void defaultConstructor() {
        Sensor sensor = new Sensor();
        assertNull(sensor.getRoom());
        assertNull(sensor.getSensorType());
        assertNull(sensor.getHealth());
        assertFalse(sensor.isEnabled());
    }

    @Test
    void parameterizedConstructor() {
        Sensor sensor = new Sensor("bedroom", "temperature", "Good", true);
        assertEquals("bedroom", sensor.getRoom());
        assertEquals("temperature", sensor.getSensorType());
        assertEquals("Good", sensor.getHealth());
        assertTrue(sensor.isEnabled());
    }

    @Test
    void settersAndGetters() {
        Sensor sensor = new Sensor();
        sensor.setRoom("livingroom");
        sensor.setSensorType("light");
        sensor.setHealth("Offline");
        sensor.setEnabled(false);

        assertEquals("livingroom", sensor.getRoom());
        assertEquals("light", sensor.getSensorType());
        assertEquals("Offline", sensor.getHealth());
        assertFalse(sensor.isEnabled());
    }

    @Test
    void enabledSensorHasGoodHealth() {
        Sensor sensor = new Sensor("kitchen", "co2", "Good", true);
        assertEquals("Good", sensor.getHealth());
        assertTrue(sensor.isEnabled());
    }

    @Test
    void disabledSensorHasOfflineHealth() {
        Sensor sensor = new Sensor("kitchen", "co2", "Offline", false);
        assertEquals("Offline", sensor.getHealth());
        assertFalse(sensor.isEnabled());
    }
}
