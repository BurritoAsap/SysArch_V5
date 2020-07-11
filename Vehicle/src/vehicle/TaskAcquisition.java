
package vehicle;

import java.util.concurrent.BlockingQueue;

public class TaskAcquisition extends Thread {
	
    BlockingQueue<Data> queue;
    SensorControl sensors;

    public TaskAcquisition(BlockingQueue<Data> queue, SensorControl sensors)
    {
        this.queue = queue;
        this.sensors = sensors;
    }

    public void run()
    {	
        //Get data from sensors
        Data data = acquisiteData();

        //Set current data for GUI
        Vehicle.setCurrentData(data);

        //Add data to queue
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
            System.out.println("Queue is full. Data lost: " + data);
        }

        //Notify waiting threads (gui and logger)
        synchronized(queue)
        {
            queue.notifyAll();
        }

    }

    private Data acquisiteData() 
    {
        Data data = new Data();

        //TODO Errorhandling
        data.setGyroData(sensors.readGyro());
        data.setAccData(sensors.readAcc());
        data.setTempData(sensors.readTemp());

        return data;
    }

}
