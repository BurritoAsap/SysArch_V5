
package vehicle;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class TaskLogger extends Thread {

    BlockingQueue<SensorData> queue;

    FileWriter writer;

    public TaskLogger(BlockingQueue<SensorData> queue)
    {
        this.queue = queue;				
    }

    public void run()
    {
        try {
            writer = new FileWriter("vehicle.log", true);
            writer.write("-----New Logging Session-----" + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true)
        {
            SensorData data = queue.poll();

            if(data != null)
            {
                synchronized(data)
                {
                    writeLog(data);                    
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
	
	
    public void writeLog(SensorData data) 
    {		
        try
        {
            writer = new FileWriter("vehicle.log", true);            
            writer.append(data.toString() + System.lineSeparator());	
            writer.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 		
    }
	
}
