package com.smart_home.manager.servicies;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart_home.manager.model.Threshold;

@Service
public class ThresholdsServiceImpl implements ThresholdsService {

  @Override
  public List<Threshold> getThresholds() throws StreamReadException, DatabindException, IOException {

    ObjectMapper mapper = new ObjectMapper();

    // Path al file JSON
    File file = new File("/simulated_env/thresholds.json");

    // Legge l'array come lista di SensorData
    List<Threshold> thresholds = mapper.readValue(
        file,
        mapper.getTypeFactory().constructCollectionType(List.class, Threshold.class));

    // Stampa i valori
    for (Threshold threshold : thresholds) {
      System.out.println("Room: " + threshold.getRoom());
      System.out.println("Sensor Type: " + threshold.getSensorType());
      System.out.println("Value: " + threshold.getValue());
      System.out.println("----");
    }
    return thresholds;
  }

}
