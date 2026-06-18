package com.smart_home.manager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ThresholdTest {

    @Test
    void settersAndGetters() {
        Threshold t = new Threshold();
        t.setRoom("bedroom");
        t.setSensorType("humidity");
        t.setValue(45.5f);

        assertEquals("bedroom", t.getRoom());
        assertEquals("humidity", t.getSensorType());
        assertEquals(45.5f, t.getValue());
    }

    @Test
    void equals_sameRoomAndType() {
        Threshold a = new Threshold();
        a.setRoom("bedroom");
        a.setSensorType("light");

        Threshold b = new Threshold();
        b.setRoom("bedroom");
        b.setSensorType("light");

        assertEquals(a, b);
    }

    @Test
    void equals_sameRoomTypeDifferentValue() {
        Threshold a = new Threshold();
        a.setRoom("bedroom");
        a.setSensorType("light");
        a.setValue(10.0f);

        Threshold b = new Threshold();
        b.setRoom("bedroom");
        b.setSensorType("light");
        b.setValue(99.0f);

        assertEquals(a, b, "equals should ignore value by design");
    }

    @Test
    void equals_differentRoom() {
        Threshold a = new Threshold();
        a.setRoom("bedroom");
        a.setSensorType("light");

        Threshold b = new Threshold();
        b.setRoom("livingroom");
        b.setSensorType("light");

        assertNotEquals(a, b);
    }

    @Test
    void equals_differentSensorType() {
        Threshold a = new Threshold();
        a.setRoom("bedroom");
        a.setSensorType("light");

        Threshold b = new Threshold();
        b.setRoom("bedroom");
        b.setSensorType("temperature");

        assertNotEquals(a, b);
    }

    @Test
    void equals_nullObject() {
        Threshold a = new Threshold();
        a.setRoom("bedroom");
        a.setSensorType("light");

        assertNotEquals(null, a);
    }

    @Test
    void equals_differentClass() {
        Threshold a = new Threshold();
        a.setRoom("bedroom");
        a.setSensorType("light");

        assertNotEquals("some string", a);
    }

    @Test
    void toString_containsFields() {
        Threshold t = new Threshold();
        t.setRoom("livingroom");
        t.setSensorType("temperature");
        t.setValue(25.0f);

        String str = t.toString();
        assertTrue(str.contains("livingroom"));
        assertTrue(str.contains("temperature"));
        assertTrue(str.contains("25.0"));
    }
}
