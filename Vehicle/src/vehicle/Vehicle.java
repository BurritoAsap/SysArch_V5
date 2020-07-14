package vehicle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Vehicle {

    private static volatile SensorData currentData;

    public static void main(String args[])
    {
        BlockingQueue<SensorData> queueLogger = new ArrayBlockingQueue<>(10);
        BlockingQueue<SensorData> queueMQTT = new ArrayBlockingQueue<>(50);
        
        currentData = null;

        SensorControl sensors = new SensorControl();
        
        TaskAcquisition acquisition = new TaskAcquisition(queueLogger, queueMQTT, sensors);
        TaskLogger logger = new TaskLogger(queueLogger);
        TaskGUI gui = new TaskGUI(queueLogger);
        TaskMQTT mqtt = new TaskMQTT(queueMQTT);

        acquisition.setPriority(6);
        logger.setPriority(5);
        mqtt.setPriority(4);
        gui.setPriority(3);	

        //Run acquisiton task every 100ms
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(acquisition, 0, 100, TimeUnit.MILLISECONDS);

        //Start logger, mqtt and gui thread to run indefinitely
        logger.start();
        gui.start();
        mqtt.start();




        while(true)
        {

        }
    }

    public synchronized static SensorData getCurrentData()
    {		
        return currentData;			
    }

    public synchronized static void setCurrentData(SensorData data)
    {	
        currentData = data;				
    }

	
	
	
	
}
