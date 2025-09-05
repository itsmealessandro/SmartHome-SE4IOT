package com.smart_home.manager.model;

public class Threshold {

  private String room;
  private String sensorType;
  private float value;

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public String getSensorType() {
    return sensorType;
  }

  public void setSensorType(String sensorType) {
    this.sensorType = sensorType;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Threshold [room=" + room + ", sensorType=" + sensorType + ", value=" + value + "]";
  }

  /*
   * WARNING: This equals does not consider the value
   * IF room and sensorType are equals return TRUE, false otherways
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Threshold))
      return false;
    Threshold threshold_2_check = (Threshold) obj;
    if (this.getRoom().equals(threshold_2_check.getRoom()) &&
        this.getSensorType().equals(threshold_2_check.getSensorType()))
      return true;
    return false;

  }

}
