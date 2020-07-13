
package vehicle;

import java.util.concurrent.BlockingQueue;

public class TaskAcquisition extends Thread {
	
    BlockingQueue<SensorData> queue;
    SensorControl sensors;

    public TaskAcquisition(BlockingQueue<SensorData> queue, SensorControl sensors)
    {
        this.queue = queue;
        this.sensors = sensors;
    }

    public void run()
    {	
        //Get data from sensors
        SensorData data = acquisiteData();

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

    private SensorData acquisiteData() 
    {
        SensorData data = new SensorData();

        //TODO Errorhandling
        data.setGyro(sensors.readGyro());
        data.setAcc(sensors.readAcc());
        data.setTemp(sensors.readTemp());
        data.setAltitude(sensors.pressureToAltitude(sensors.readPressure()));
        data.setMagnet(sensors.readMagnet());

        return data;
    }

}
