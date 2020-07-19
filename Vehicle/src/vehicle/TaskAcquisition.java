
package vehicle;

import com.pi4j.system.SystemInfo;
import java.util.concurrent.BlockingQueue;

public class TaskAcquisition extends Thread {
	
    BlockingQueue<SensorData> queueLogger;
    BlockingQueue<SensorData> queueMQTT;
    BlockingQueue<String> queueGUI;
    SensorControl sensors;

    public TaskAcquisition(BlockingQueue<SensorData> queueLogger, BlockingQueue<SensorData> queueMQTT,  BlockingQueue<String> queueGUI, SensorControl sensors)
    {
        this.queueMQTT = queueMQTT;
        this.queueGUI = queueGUI;
        this.queueLogger = queueLogger;
        this.sensors = sensors;
    }

    public void run()
    {	
        //Get data from sensors
        SensorData data = acquisiteData();

        //Add data to Logging Queue
        addDataToQueue(queueLogger, data);
        
        //Add data to MQTT Queue
        addDataToQueue(queueMQTT, data);                

    }
    
    private boolean addDataToQueue(BlockingQueue<SensorData> queue, SensorData data)
    {
        Boolean success = queue.offer(data);           	
        
        //If queue is full
        if(!success)
        {
            SensorData removedData = queue.poll();
            queueGUI.offer("[" + queue + "] Queue is full. Data lost: " + removedData);

            //Try to add data again            
            success = queue.offer(data);
        }
        
        return success;
    }

    private SensorData acquisiteData() 
    {
        SensorData data = new SensorData();

        //TODO Errorhandling
        data.setGyro(sensors.readGyro());
        data.setAcc(sensors.readAcc());
        data.setTemp(sensors.readTemp());
        data.setAltitude(sensors.pressureToAltitude(sensors.readPressure()));
        data.setMagnet(sensors.readMagnet());
        
        try 
        {
            data.setCPUTemp(SystemInfo.getCpuTemperature());
        } 
        catch (Exception ex) {
            queueGUI.offer("Couldnt retrieve CPU temperature.");
        }

        return data;
    }

}
