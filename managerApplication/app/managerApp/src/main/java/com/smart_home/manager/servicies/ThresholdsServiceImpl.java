package com.smart_home.manager.servicies;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.smart_home.manager.model.Threshold;

@Service
public class ThresholdsServiceImpl implements ThresholdsService {

  @Override
  public Set<Threshold> getThresholds() {
    Set<Threshold> thresholds = new HashSet<>();

    // NOTE: only for test
    Threshold t1 = new Threshold();
    t1.setRoom("roomName");
    t1.setSensorType("setSensorTypeName");
    t1.setValue(12.5f);
    thresholds.add(t1);

    Threshold t2 = new Threshold();
    t2.setRoom("roomName2");
    t2.setSensorType("setSensorTypeName2");
    t2.setValue(12.5f);
    thresholds.add(t2);

    return thresholds;
  }

}
