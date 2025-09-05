package com.smart_home.manager.servicies;

import java.io.IOException;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import com.smart_home.manager.model.Threshold;

public interface ThresholdsService {

  List<Threshold> getThresholds() throws IOException;

  List<Threshold> updateThresholds(List<Threshold> thresholds) throws IOException;

  @Scheduled(fixedRate = 5000)
  void publishThresholdsMQTT();
}
