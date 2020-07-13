/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
        BlockingQueue<SensorData> queue = new ArrayBlockingQueue<>(10);
        currentData = null;

        SensorControl sensors = new SensorControl();
        
        TaskAcquisition acquisition = new TaskAcquisition(queue, sensors);
        TaskLogger logger = new TaskLogger(queue);
        TaskGUI gui = new TaskGUI(queue);

        acquisition.setPriority(6);
        logger.setPriority(5);
        gui.setPriority(4);		

        //Run acquisiton task every 100ms
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(acquisition, 0, 100, TimeUnit.MILLISECONDS);

        //Start logger and gui thread to run indefinitely
        logger.start();
        gui.start();




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
