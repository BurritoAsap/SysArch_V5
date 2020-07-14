
package vehicle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class TaskMQTT extends Thread {

    BlockingQueue<SensorData> queue;

    FileWriter writer;

    public TaskMQTT(BlockingQueue<SensorData> queue)
    {
        this.queue = queue;				
    }

    public void run()
    {
        while(true)
        {
            SensorData data = queue.poll();
            
            if(data != null)
            {
                synchronized(data)
                {
                    sendViaMQTT(data);
                }
            }
            else
            {
                synchronized(queue)
                {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }	
    
    private void sendViaMQTT(SensorData data)
    {
        System.out.println("MQTT: " + data.toString());
    }
}