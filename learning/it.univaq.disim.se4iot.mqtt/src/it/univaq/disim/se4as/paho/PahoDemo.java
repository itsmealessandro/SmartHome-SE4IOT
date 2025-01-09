package it.univaq.disim.se4as.paho;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class PahoDemo {

  MqttClient client;
  
  public PahoDemo() {}

  public static void main(String[] args) {
      new PahoDemo().publish1();
     //new PahoDemo().publish2();
    try {
		new PahoDemo().publish3();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  public void publish1() {
    try {
      client = new MqttClient("tcp://localhost:1883", "pahomqttpublish1");
      client.connect();
      MqttMessage message = new MqttMessage();
      message.setPayload("31".getBytes());
      client.publish("/home/outsidetemperature", message);
      client.disconnect();
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
  
  public void publish2() {
	    try {
	      client = new MqttClient("tcp://localhost:1883", "pahomqttpublish1");
	      
	      MqttConnectOptions options = new MqttConnectOptions();
	      options.setKeepAliveInterval(480);
	      options.setWill(client.getTopic("WillTopic"), "Something bad happend again from pahomqttpublish1".getBytes(), 1, true);
	      client.connect(options);
	      
	      MqttMessage message = new MqttMessage();
	      message.setPayload("29".getBytes());
	      
	      message.setRetained(false); // try with true
	      message.setQos(0);

	      //client.publish("home/outsidetemperature", message);
	      // Or
	      MqttTopic topic = client.getTopic("home/outsidetemperature");
	      topic.publish(message);
	      
	     // client.disconnect(); //Try to comment it, execute and stop the execution
	    } catch (MqttException e) {
	      e.printStackTrace();
	    }
	  }
  
  public void publish3() throws IOException {
	    try {
	      client = new MqttClient("tcp://localhost:1883", "pahomqttpublish1");

	      String jsondata = readFile("resources/temp.json", Charset.defaultCharset());
	     	      
	      MqttConnectOptions options = new MqttConnectOptions();
	      options.setKeepAliveInterval(480);
	      options.setWill(client.getTopic("WillTopic"), "Something bad happend".getBytes(), 1, true);
	      client.connect(options);
	      
	      MqttMessage message = new MqttMessage();
	      message.setPayload(jsondata.getBytes());
	      
	      
	      message.setRetained(false); // try with true
	      message.setQos(0);

	      client.publish("home/outsidetemperature", message);
	      client.disconnect();  
	    } catch (MqttException e) {
	      e.printStackTrace();
	    }
	  }

  
  static String readFile(String path, Charset encoding) 
		  throws IOException 
		{
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return new String(encoded, encoding);
		}
}
	  
