
package vehicle;

import java.util.concurrent.BlockingQueue;

public class TaskGUI extends Thread {

    BlockingQueue<SensorData> queue;

    public TaskGUI(BlockingQueue<SensorData> queue)
    {
        this.queue = queue;
    }

    public void run()
    {
        while(true)
        {           
            try
            {
                SensorData data = queue.take();  
                //System.out.println(data.toString());              
            }
            catch(InterruptedException e){
                
            }
            
        }		
    }
    	
	
}
