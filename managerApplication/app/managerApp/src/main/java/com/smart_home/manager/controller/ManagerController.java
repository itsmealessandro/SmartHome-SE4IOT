package com.smart_home.manager.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.smart_home.manager.model.Threshold;
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
}
