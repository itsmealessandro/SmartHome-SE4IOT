package com.smart_home.manager.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    System.out.println("[SERVER] page asked");
    List<Threshold> thresholds;
    try {
      thresholds = thresholdsService.getThresholds();
    } catch (IOException e) {
      e.printStackTrace();
      model.addAttribute("message", "internal error");
      return "thresholdsPage";
    }
    model.addAttribute("thresholds", thresholds);

    return "thresholdsPage";
  }

  @PostMapping("/changeThresholds")
  public String changeThresholds(@ModelAttribute("thresholds") ThresholdsForm form, Model model) {
    List<Threshold> thresholds = form.getThresholds();
    // Qui gestisci i valori aggiornati
    try {
      thresholdsService.updateThresholds(thresholds);
    } catch (IOException e) {

      model.addAttribute("message", "INTERNAL ERROR");
      return "thresholdsPage";
    }

    model.addAttribute("message", "Thresholds updated!");
    model.addAttribute("thresholds", thresholds);

    return "thresholdsPage";
  }
}
