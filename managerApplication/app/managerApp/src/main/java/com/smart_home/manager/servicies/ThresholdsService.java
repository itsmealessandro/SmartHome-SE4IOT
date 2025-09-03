package com.smart_home.manager.servicies;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.smart_home.manager.model.Threshold;

public interface ThresholdsService {

  List<Threshold> getThresholds() throws IOException;
}
