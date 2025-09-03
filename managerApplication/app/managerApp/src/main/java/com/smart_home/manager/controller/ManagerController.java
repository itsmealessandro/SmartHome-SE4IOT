package com.smart_home.manager.controller;

import java.util.Set;

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
    Set<Threshold> thresholds = thresholdsService.getThresholds();
    model.addAttribute("thresholds", thresholds);

    return "thresholdsPage";
  }
}
