
package vehicle;

import com.pi4j.system.SystemInfo;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskAcquisition extends Thread {
	
    BlockingQueue<SensorData> queueLogger;
    BlockingQueue<SensorData> queueMQTT;
    BlockingQueue<SensorData> queueGUI;
    SensorControl sensors;

    public TaskAcquisition(BlockingQueue<SensorData> queueLogger, BlockingQueue<SensorData> queueMQTT,  BlockingQueue<SensorData> queueGUI, SensorControl sensors)
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
        
        //Add data to GUI Queue
        addDataToQueue(queueGUI, data);
        

    }
    
    private boolean addDataToQueue(BlockingQueue<SensorData> queue, SensorData data)
    {
        Boolean success;
        
        try
        {
            success = queue.add(data);

        } catch (IllegalStateException  e) 
        {
            success = false;
            e.printStackTrace();
        }		
        
        if(!success)
        {
            SensorData removedData = queue.poll();
            System.out.println("[" + queue + "] Queue is full. Data lost: " + removedData);

            //Try to add again
            try
            {
                success = queue.add(data);
            } 
            catch (IllegalStateException  e) 
            {
                success = false;
                e.printStackTrace();
            }
        }

        //Notify waiting threads
        synchronized(queue)
        {
            queue.notifyAll();
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
            System.out.println("Couldnt retrieve CPU temperature.");
        }

        return data;
    }

}
