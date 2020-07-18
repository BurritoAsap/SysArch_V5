
package vehicle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TaskMQTT extends Thread implements MqttCallback{

    BlockingQueue<SensorData> queue;
    
    MqttClient client;    
    MqttTopic topicPub;
    
    String broker = "tcp://192.168.200.165:8883";
    int qos = 0;
    
    String topicPublishSensor = "/SysArch/V5/sensor";
    String topicSubscribeLogin = "/SysArch/V5/car";


    public TaskMQTT(BlockingQueue<SensorData> queue)
    {
        this.queue = queue;	
        
        try 
        {
            client = new MqttClient(broker, String.valueOf(System.nanoTime()));
        } 
        catch (MqttException ex) {
            Logger.getLogger(TaskMQTT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        client.setCallback(this);
        
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setCleanSession(true); //no persistent session 
        opts.setKeepAliveInterval(1000);
        opts.setPassword("DE9".toCharArray());
        opts.setUserName("V5");
        
        try 
        {
            client.connect(opts); //connects the broker with connect options
        } 
        catch (MqttException ex) {
            Logger.getLogger(TaskMQTT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        topicPub = client.getTopic(topicPublishSensor);
        
        try 
        {
            client.subscribe(topicSubscribeLogin);
        } 
        catch (MqttException ex) {
            Logger.getLogger(TaskMQTT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("MQTT initialized.");
        
    }

    public void run()
    {
        while(true)
        {           
           try
           {
               SensorData data = queue.take();  
               sendViaMQTT(data);              
           }
           catch(InterruptedException e){

           }
        }
    }	
    
    private void sendViaMQTT(SensorData data)
    {
        MqttMessage message = new MqttMessage(data.toJSON().toString().getBytes());
        message.setQos(qos);     //sets qos level 1
        message.setRetained(true); //sets retained message 
        
        try 
        { 
            topicPub.publish(message);
            //System.out.println("message sent.");
        } 
        catch (MqttException ex) {
            Logger.getLogger(TaskMQTT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) 
    {
        System.out.println("Connection lost.");
    }

    @Override
    public void messageArrived(String topic, MqttMessage m) throws Exception 
    {
        //System.out.println(m);
        JSONTokener tokener = new JSONTokener(m.toString());
        JSONObject obj = new JSONObject(tokener);

        Boolean login = obj.getBoolean("login");
        long timestamp = obj.getLong("timestamp");

        if(login)
        {                
            JSONObject user = obj.getJSONObject("user");

            String userName = user.getString("userName");
            String userFullName = user.getString("fullName");
            String email = user.getString("email");

            Vehicle.logIn(userName, userFullName, email, timestamp);
        }
        else
        {
            Vehicle.logOut(timestamp);               
        }           
        
    }
     
    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) 
    {
        //ystem.out.println("Delivery complete.");
    }
}