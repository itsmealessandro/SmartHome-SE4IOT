package it.univaq.disim.se4as.paho;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PahoDemoImplCallBack implements MqttCallback  {

	 MqttClient client;

	  
	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println("The connection with the server is lost. !!!!");
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		System.out.println("The delivery has been complete. The delivery token is " + arg0.toString());
		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage message) throws Exception {
		System.out.println("A new message arrived from the topic: \"" + arg0 + "\". The payload of the message is " + message.toString());
		
	}
	
	public static void main(String[] args) {
			//new PahoDemoImplCallBack().publish1();
			new PahoDemoImplCallBack().subscribe();
		  }

		  public void publish1() {
		    try {
		      client = new MqttClient("tcp://localhost:1883", "pahomqttpublish1");
		      client.setCallback(this);
		      client.connect();
		      MqttMessage message = new MqttMessage();
		      message.setPayload("19.0".getBytes());
		      client.publish("home/outsidetemperature", message);
		      client.disconnect();
		    } catch (MqttException e) {
		      e.printStackTrace();
		    }
		  }

		  public void subscribe() {
			    try {
			      client = new MqttClient("tcp://localhost:1883", "pahomqttpublish12");
			      
			      client.setCallback(this);
			      client.connect();
			      client.subscribe("/home/outsidetemperature");
			     // client.subscribe(new String[]{"home/outsidetemperature","home/livingroomtemperature"});
			     // client.subscribe("home/#");
			      System.out.println("Hello!");
			      
			    } catch (MqttException e) {
			      e.printStackTrace();
			    }
			  }
}
