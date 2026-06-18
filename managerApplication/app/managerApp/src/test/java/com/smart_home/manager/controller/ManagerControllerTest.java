package com.smart_home.manager.controller;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smart_home.manager.model.Sensor;
import com.smart_home.manager.model.Threshold;
import com.smart_home.manager.model.ThresholdsForm;
import com.smart_home.manager.servicies.ThresholdsService;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ThresholdsService thresholdsService;

    private static final List<Threshold> MOCK_THRESHOLDS = List.of(
            createThreshold("bedroom", "temperature", 25.0f),
            createThreshold("livingroom", "light", 9.0f));

    private static final List<Sensor> MOCK_SENSORS = List.of(
            new Sensor("bedroom", "temperature", "Good", true),
            new Sensor("livingroom", "light", "Offline", false));

    private static Threshold createThreshold(String room, String type, float value) {
        Threshold t = new Threshold();
        t.setRoom(room);
        t.setSensorType(type);
        t.setValue(value);
        return t;
    }

    @Test
    void getRoot_shouldReturnThresholdsPageWithSensors() throws Exception {
        when(thresholdsService.getThresholds()).thenReturn(MOCK_THRESHOLDS);
        when(thresholdsService.getSensors()).thenReturn(MOCK_SENSORS);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("thresholdsPage"))
                .andExpect(model().attributeExists("thresholds"))
                .andExpect(model().attributeExists("sensors"));
    }

    @Test
    void getRoot_whenThresholdsThrowIOException_shouldReturnErrorMessage() throws Exception {
        when(thresholdsService.getThresholds()).thenThrow(new IOException("file not found"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("thresholdsPage"))
                .andExpect(model().attribute("message", "Internal error reading thresholds"))
                .andExpect(model().attribute("messageType", "error"));
    }

    @Test
    void changeThresholds_shouldRedirectWithSuccess() throws Exception {
        mockMvc.perform(post("/changeThresholds")
                        .param("thresholds[0].room", "bedroom")
                        .param("thresholds[0].sensorType", "light")
                        .param("thresholds[0].value", "10.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Thresholds updated!"))
                .andExpect(flash().attribute("messageType", "success"));
    }

    @Test
    void changeThresholds_whenIOException_shouldRedirectWithError() throws Exception {
        doThrow(new IOException("disk full")).when(thresholdsService).updateThresholds(anyList());

        mockMvc.perform(post("/changeThresholds")
                        .param("thresholds[0].room", "bedroom")
                        .param("thresholds[0].sensorType", "light")
                        .param("thresholds[0].value", "10.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "INTERNAL ERROR: disk full"))
                .andExpect(flash().attribute("messageType", "error"));
    }

    @Test
    void addSensor_shouldRedirectWithSuccess() throws Exception {
        mockMvc.perform(post("/addSensor")
                        .param("room", "kitchen")
                        .param("sensorType", "co2")
                        .param("value", "15.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Sensor added successfully!"))
                .andExpect(flash().attribute("messageType", "success"));
    }

    @Test
    void addSensor_whenDuplicate_shouldRedirectWithErrorMessage() throws Exception {
        doThrow(new IllegalArgumentException("Sensor 'co2' in room 'kitchen' already exists!"))
                .when(thresholdsService).addThreshold(any(Threshold.class));

        mockMvc.perform(post("/addSensor")
                        .param("room", "kitchen")
                        .param("sensorType", "co2")
                        .param("value", "15.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Sensor 'co2' in room 'kitchen' already exists!"))
                .andExpect(flash().attribute("messageType", "error"));
    }

    @Test
    void addSensor_whenIOException_shouldRedirectWithError() throws Exception {
        doThrow(new IOException("permission denied")).when(thresholdsService).addThreshold(any(Threshold.class));

        mockMvc.perform(post("/addSensor")
                        .param("room", "kitchen")
                        .param("sensorType", "co2")
                        .param("value", "15.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "INTERNAL ERROR: permission denied"))
                .andExpect(flash().attribute("messageType", "error"));
    }

    @Test
    void sensorEnabled_turnOn_shouldRedirectWithOnMessage() throws Exception {
        mockMvc.perform(post("/sensorEnabled")
                        .param("room", "bedroom")
                        .param("sensorType", "humidity")
                        .param("enabled", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Sensor humidity in room bedroom is now On!"))
                .andExpect(flash().attribute("messageType", "success"));
    }

    @Test
    void sensorEnabled_turnOff_shouldRedirectWithOffMessage() throws Exception {
        mockMvc.perform(post("/sensorEnabled")
                        .param("room", "bedroom")
                        .param("sensorType", "humidity")
                        .param("enabled", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Sensor humidity in room bedroom is now Off!"))
                .andExpect(flash().attribute("messageType", "success"));
    }

    @Test
    void sensorEnabled_whenIOException_shouldRedirectWithError() throws Exception {
        doThrow(new IOException("cannot write env.json"))
                .when(thresholdsService).toggleSensorStatus("bedroom", "humidity", true);

        mockMvc.perform(post("/sensorEnabled")
                        .param("room", "bedroom")
                        .param("sensorType", "humidity")
                        .param("enabled", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "INTERNAL ERROR: cannot write env.json"))
                .andExpect(flash().attribute("messageType", "error"));
    }
}
