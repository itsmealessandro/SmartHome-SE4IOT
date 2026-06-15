import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class DynamicSensors {
  public static void main(String[] args) throws Exception {
    String libraryPath = "org.eclipse.paho.client.mqttv3-1.2.0.jar:jackson-core-2.18.2.jar:jackson-databind-2.18.2.jar:jackson-annotations-2.18.2.jar";
    File file = new File("/simulated_env/env.json");
    Set<String> spawned = new HashSet<>();
    int index = 1;

    while (true) {
      JsonNode root = new ObjectMapper().readTree(file);
      for (Iterator<Map.Entry<String, JsonNode>> rooms = root.fields(); rooms.hasNext();) {
        Map.Entry<String, JsonNode> roomEntry = rooms.next();
        for (Iterator<Map.Entry<String, JsonNode>> sensors = roomEntry.getValue().fields(); sensors.hasNext();) {
          Map.Entry<String, JsonNode> sensorEntry = sensors.next();
          String topic = "SmartHome/" + roomEntry.getKey() + "/" + sensorEntry.getKey();
          if (spawned.add(topic)) {
            new ProcessBuilder("java", "-cp", ".:" + libraryPath, "CreateSensor", topic, "s" + index)
                .directory(new File("/sens"))
                .inheritIO()
                .start();
            index++;
          }
        }
      }
      Thread.sleep(5000);
    }
  }
}
