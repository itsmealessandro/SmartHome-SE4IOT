package com.smart_home.manager.servicies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import com.smart_home.manager.model.Sensor;
import com.smart_home.manager.model.Threshold;

@ExtendWith(MockitoExtension.class)
class ThresholdsServiceImplTest {

    private ThresholdsServiceImpl service;

    @Mock
    private MqttClient mqttClient;

    @TempDir
    Path tempDir;

    private File thresholdsFile;
    private File envFile;

    @BeforeEach
    void setUp() {
        service = new ThresholdsServiceImpl();
        thresholdsFile = tempDir.resolve("thresholds.json").toFile();
        envFile = tempDir.resolve("env.json").toFile();
        service.thresholdsFile = thresholdsFile;
        service.envFile = envFile;
        ReflectionTestUtils.setField(service, "MQTT_CLIENT", mqttClient);
        ReflectionTestUtils.setField(service, "isConnected", true);
    }

    // --- writeFixture helpers ---

    private void writeThresholdsFile(String content) throws IOException {
        Files.writeString(thresholdsFile.toPath(), content);
    }

    private void writeEnvFile(String content) throws IOException {
        Files.writeString(envFile.toPath(), content);
    }

    // ====================
    // getThresholds tests
    // ====================

    @Nested
    class GetThresholds {

        @Test
        void shouldReturnListOfThresholds() throws Exception {
            writeThresholdsFile("""
                    [{"room":"bedroom","sensorType":"temperature","value":25.0}]
                    """);

            List<Threshold> result = service.getThresholds();

            assertEquals(1, result.size());
            assertEquals("bedroom", result.getFirst().getRoom());
            assertEquals("temperature", result.getFirst().getSensorType());
            assertEquals(25.0f, result.getFirst().getValue());
        }

        @Test
        void shouldReturnEmptyListForEmptyArray() throws Exception {
            writeThresholdsFile("[]");

            List<Threshold> result = service.getThresholds();

            assertTrue(result.isEmpty());
        }

        @Test
        void shouldThrowIOExceptionWhenFileMissing() {
            assertThrows(IOException.class, () -> service.getThresholds());
        }
    }

    // ====================
    // updateThresholds tests
    // ====================

    @Nested
    class UpdateThresholds {

        @Test
        void shouldWriteThresholdsToFile() throws Exception {
            List<Threshold> input = List.of(createThreshold("bedroom", "temperature", 25.0f));

            List<Threshold> result = service.updateThresholds(input);

            assertSame(input, result);
            String content = Files.readString(thresholdsFile.toPath());
            assertTrue(content.contains("bedroom"));
            assertTrue(content.contains("temperature"));
            assertTrue(content.contains("25.0"));
        }
    }

    // ====================
    // addThreshold tests
    // ====================

    @Nested
    class AddThreshold {

        @BeforeEach
        void setUp() throws Exception {
            writeThresholdsFile("""
                    [{"room":"bedroom","sensorType":"temperature","value":25.0}]
                    """);
            writeEnvFile("{}");
        }

        @Test
        void shouldAddNewThreshold() throws Exception {
            Threshold newThreshold = createThreshold("livingroom", "light", 9.0f);

            service.addThreshold(newThreshold);

            List<Threshold> thresholds = service.getThresholds();
            assertEquals(2, thresholds.size());
        }

        @Test
        void shouldThrowWhenDuplicate() throws Exception {
            Threshold duplicate = createThreshold("bedroom", "temperature", 30.0f);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.addThreshold(duplicate));

            assertTrue(ex.getMessage().contains("already exists"));
        }

        @Test
        void shouldTrimAndLowercaseRoomAndType() throws Exception {
            Threshold threshold = createThreshold("  KITCHEN  ", "  CO2  ", 15.0f);

            service.addThreshold(threshold);

            assertEquals("kitchen", threshold.getRoom());
            assertEquals("co2", threshold.getSensorType());
        }

        @Test
        void shouldUpdateEnvFile() throws Exception {
            writeEnvFile("""
                    {"bedroom":{"temperature":{"value":20,"enabled":true}}}
                    """);
            Threshold newThreshold = createThreshold("bedroom", "light", 5.0f);

            service.addThreshold(newThreshold);

            List<Sensor> sensors = service.getSensors();
            assertTrue(sensors.stream().anyMatch(s ->
                    "bedroom".equals(s.getRoom()) && "light".equals(s.getSensorType())));
        }
    }

    // ====================
    // getSensors tests
    // ====================

    @Nested
    class GetSensors {

        @Test
        void shouldBuildSensorListFromEnvJson() throws Exception {
            writeEnvFile("""
                    {
                        "bedroom": {
                            "temperature": {"value": 22, "enabled": true},
                            "co2": {"value": 30, "enabled": false}
                        }
                    }
                    """);

            List<Sensor> sensors = service.getSensors();

            assertEquals(2, sensors.size());
            Sensor temp = sensors.stream().filter(s -> "temperature".equals(s.getSensorType())).findFirst().orElseThrow();
            assertTrue(temp.isEnabled());
            assertEquals("Good", temp.getHealth());

            Sensor co2 = sensors.stream().filter(s -> "co2".equals(s.getSensorType())).findFirst().orElseThrow();
            assertFalse(co2.isEnabled());
            assertEquals("Offline", co2.getHealth());
        }

        @Test
        void shouldHandleLegacyPlainIntFormat() throws Exception {
            writeEnvFile("""
                    {
                        "bedroom": {
                            "temperature": 22,
                            "light": 0
                        }
                    }
                    """);

            List<Sensor> sensors = service.getSensors();

            assertEquals(2, sensors.size());
            assertTrue(sensors.stream().allMatch(Sensor::isEnabled));
            assertTrue(sensors.stream().allMatch(s -> "Good".equals(s.getHealth())));
        }

        @Test
        void shouldHandleMultipleRooms() throws Exception {
            writeEnvFile("""
                    {
                        "bedroom": {"light": {"value": 0, "enabled": true}},
                        "livingroom": {"temperature": {"value": 25, "enabled": false}}
                    }
                    """);

            List<Sensor> sensors = service.getSensors();

            assertEquals(2, sensors.size());
            assertEquals("bedroom", sensors.get(0).getRoom());
            assertEquals("livingroom", sensors.get(1).getRoom());
        }

        @Test
        void shouldThrowIOExceptionWhenFileMissing() {
            assertThrows(IOException.class, () -> service.getSensors());
        }
    }

    // ========================
    // toggleSensorStatus tests
    // ========================

    @Nested
    class ToggleSensorStatus {

        @BeforeEach
        void setUp() throws Exception {
            writeEnvFile("""
                    {
                        "bedroom": {
                            "temperature": {"value": 22, "enabled": true}
                        }
                    }
                    """);
        }

        @Test
        void shouldDisableSensor() throws Exception {
            service.toggleSensorStatus("bedroom", "temperature", false);

            List<Sensor> sensors = service.getSensors();
            Sensor temp = sensors.stream().filter(s -> "temperature".equals(s.getSensorType())).findFirst().orElseThrow();
            assertFalse(temp.isEnabled());
            assertEquals("Offline", temp.getHealth());
        }

        @Test
        void shouldReEnableSensor() throws Exception {
            service.toggleSensorStatus("bedroom", "temperature", false);
            service.toggleSensorStatus("bedroom", "temperature", true);

            List<Sensor> sensors = service.getSensors();
            Sensor temp = sensors.stream().filter(s -> "temperature".equals(s.getSensorType())).findFirst().orElseThrow();
            assertTrue(temp.isEnabled());
            assertEquals("Good", temp.getHealth());
        }

        @Test
        void shouldConvertLegacyIntToObject() throws Exception {
            writeEnvFile("""
                    {"bedroom": {"light": 0}}
                    """);

            service.toggleSensorStatus("bedroom", "light", false);

            List<Sensor> sensors = service.getSensors();
            Sensor light = sensors.stream().filter(s -> "light".equals(s.getSensorType())).findFirst().orElseThrow();
            assertFalse(light.isEnabled());
        }

        @Test
        void shouldDoNothingForUnknownRoom() throws Exception {
            assertDoesNotThrow(() -> service.toggleSensorStatus("nonexistent", "light", false));
        }

        @Test
        void shouldDoNothingForUnknownSensor() throws Exception {
            assertDoesNotThrow(() -> service.toggleSensorStatus("bedroom", "nonexistent", false));
        }

        @Test
        void shouldDoNothingWhenEnvFileMissing() throws Exception {
            envFile.delete();

            assertDoesNotThrow(() -> service.toggleSensorStatus("bedroom", "temperature", false));
        }
    }

    // ==============================
    // publishThresholdsMQTT tests
    // ==============================

    @Nested
    class PublishThresholdsMQTT {

        @BeforeEach
        void setUp() throws Exception {
            writeThresholdsFile("""
                    [{"room":"bedroom","sensorType":"temperature","value":25.0}]
                    """);
        }

        @Test
        void shouldPublishThresholdsWhenConnected() throws Exception {
            when(mqttClient.isConnected()).thenReturn(true);
            ReflectionTestUtils.setField(service, "isConnected", true);

            service.publishThresholdsMQTT();

            verify(mqttClient, never()).connect(any(MqttConnectOptions.class));
            verify(mqttClient, times(1))
                .publish(eq("SmartHome/thresholds/bedroom/temperature"), any(MqttMessage.class));
        }

        @Test
        void shouldConnectThenPublishWhenDisconnected() throws Exception {
            ReflectionTestUtils.setField(service, "isConnected", false);

            service.publishThresholdsMQTT();

            verify(mqttClient, times(1)).connect(any(MqttConnectOptions.class));
            verify(mqttClient, times(1))
                .publish(eq("SmartHome/thresholds/bedroom/temperature"), any(MqttMessage.class));
        }

        @Test
        void shouldPublishMultipleThresholds() throws Exception {
            writeThresholdsFile("""
                    [
                        {"room":"bedroom","sensorType":"temperature","value":25.0},
                        {"room":"livingroom","sensorType":"light","value":9.0}
                    ]
                    """);
            ReflectionTestUtils.setField(service, "isConnected", true);
            when(mqttClient.isConnected()).thenReturn(true);

            service.publishThresholdsMQTT();

            verify(mqttClient, times(1))
                .publish(eq("SmartHome/thresholds/bedroom/temperature"), any(MqttMessage.class));
            verify(mqttClient, times(1))
                .publish(eq("SmartHome/thresholds/livingroom/light"), any(MqttMessage.class));
        }

        @Test
        void shouldHandleConnectFailure() throws Exception {
            when(mqttClient.isConnected()).thenReturn(false);
            ReflectionTestUtils.setField(service, "isConnected", false);
            doThrow(new MqttException(0)).when(mqttClient).connect(any(MqttConnectOptions.class));

            assertDoesNotThrow(() -> service.publishThresholdsMQTT());

            verify(mqttClient, never()).publish(anyString(), any(MqttMessage.class));
        }

        @Test
        void shouldHandleIOException() throws Exception {
            thresholdsFile.delete();

            assertDoesNotThrow(() -> service.publishThresholdsMQTT());

            verify(mqttClient, never()).publish(anyString(), any(MqttMessage.class));
        }
    }

    // ====================
    // helpers
    // ====================

    private static Threshold createThreshold(String room, String type, float value) {
        Threshold t = new Threshold();
        t.setRoom(room);
        t.setSensorType(type);
        t.setValue(value);
        return t;
    }
}
