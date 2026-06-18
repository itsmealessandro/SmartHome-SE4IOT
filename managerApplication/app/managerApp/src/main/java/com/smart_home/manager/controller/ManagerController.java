package com.smart_home.manager.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart_home.manager.model.Sensor;
import com.smart_home.manager.model.Threshold;
import com.smart_home.manager.model.ThresholdsForm;
import com.smart_home.manager.servicies.ThresholdsService;

@Controller
@RequestMapping("/")
public class ManagerController {

  private final ThresholdsService thresholdsService;

  public ManagerController(ThresholdsService thresholdsService) {
    this.thresholdsService = thresholdsService;
  }

  @GetMapping("/")
  public String showThresholds(Model model) {
    List<Threshold> thresholds;
    try {
      thresholds = thresholdsService.getThresholds();
    } catch (IOException e) {
      e.printStackTrace();
      model.addAttribute("message", "Internal error reading thresholds");
      model.addAttribute("messageType", "error");
      model.addAttribute("sensors", List.of());
      return "thresholdsPage";
    }
    model.addAttribute("thresholds", thresholds);

    try {
        List<Sensor> sensors = thresholdsService.getSensors();
        model.addAttribute("sensors", sensors);
    } catch (IOException e) {
        e.printStackTrace();
        model.addAttribute("sensors", List.of());
    }

    return "thresholdsPage";
  }

  @PostMapping("/changeThresholds")
  public String changeThresholds(@ModelAttribute("thresholds") ThresholdsForm form, RedirectAttributes redirectAttributes) {
    List<Threshold> thresholds = form.getThresholds();
    try {
      thresholdsService.updateThresholds(thresholds);
      redirectAttributes.addFlashAttribute("message", "Thresholds updated!");
      redirectAttributes.addFlashAttribute("messageType", "success");
    } catch (IOException e) {
      redirectAttributes.addFlashAttribute("message", "INTERNAL ERROR: " + e.getMessage());
      redirectAttributes.addFlashAttribute("messageType", "error");
    }

    return "redirect:/";
  }

  @PostMapping("/addSensor")
  public String addSensor(@ModelAttribute Threshold threshold, RedirectAttributes redirectAttributes) {
    try {
      thresholdsService.addThreshold(threshold);
      redirectAttributes.addFlashAttribute("message", "Sensor added successfully!");
      redirectAttributes.addFlashAttribute("messageType", "success");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
      redirectAttributes.addFlashAttribute("messageType", "error");
    } catch (IOException e) {
      redirectAttributes.addFlashAttribute("message", "INTERNAL ERROR: " + e.getMessage());
      redirectAttributes.addFlashAttribute("messageType", "error");
    }
    return "redirect:/";
  }

  @PostMapping("/sensorEnabled")
  public String sensorEnabled(@RequestParam("room") String room,
                              @RequestParam("sensorType") String sensorType,
                              @RequestParam("enabled") boolean enabled,
                              RedirectAttributes redirectAttributes) {
    try {
      thresholdsService.toggleSensorStatus(room, sensorType, enabled);
      String statusText = enabled ? "On" : "Off";
      redirectAttributes.addFlashAttribute("message", "Sensor " + sensorType + " in room " + room + " is now " + statusText + "!");
      redirectAttributes.addFlashAttribute("messageType", "success");
    } catch (IOException e) {
      e.printStackTrace();
      redirectAttributes.addFlashAttribute("message", "INTERNAL ERROR: " + e.getMessage());
      redirectAttributes.addFlashAttribute("messageType", "error");
    }
    return "redirect:/";
  }
}
