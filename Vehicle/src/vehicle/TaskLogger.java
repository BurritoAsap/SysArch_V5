
package vehicle;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class TaskLogger extends Thread {

    BlockingQueue<SensorData> queue;
    BlockingQueue<String> queueGUI;

    FileWriter writer;

    public TaskLogger(BlockingQueue<SensorData> queue, BlockingQueue<String> queueGUI)
    {
        this.queue = queue;	
        this.queueGUI = queueGUI;
    }

    public void run()
    {
        try {
            writer = new FileWriter("vehicle.log", true);
            writer.write("-----New Logging Session-----" + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            queueGUI.offer("Couldnt create/open log: " + e.getMessage());
        }

        while(true)
        {           
            try
            {
                SensorData data = queue.take();  
                writeLog(data);              
            }
            catch(InterruptedException e){
                
            }
            
        }


    }	
	
	
    public void writeLog(SensorData data) 
    {		
        try
        {
            writer = new FileWriter("vehicle.log", true);            
            writer.append(data.toJSON().toString() + System.lineSeparator());	
            writer.close();
        } 
        catch (IOException e) 
        {
            queueGUI.offer("Couldnt write to log file: " + e.getMessage());
        } 		
    }
	
}
