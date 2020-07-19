
package vehicle;

import java.util.concurrent.BlockingQueue;

public class TaskGUI extends Thread {

    BlockingQueue<String> queue;

    public TaskGUI(BlockingQueue<String> queue)
    {
        this.queue = queue;
    }

    public void run()
    {
        while(true)
        {           
            try
            {
                String message = queue.take();  
                System.out.println(message);              
            }
            catch(InterruptedException e){
                
            }
            
        }		
    }
    	
	
}
