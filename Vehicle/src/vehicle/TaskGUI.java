
package vehicle;

import java.util.concurrent.BlockingQueue;

public class TaskGUI extends Thread {

    BlockingQueue<Data> queue;

    public TaskGUI(BlockingQueue<Data> queue)
    {
        this.queue = queue;
    }

    public void run()
    {
        while(true)
        {		
            if(Vehicle.getCurrentData() != null)
            {				
                System.out.println(Vehicle.getCurrentData().toString());
            }

            synchronized(queue)
            {
                try 
                {
                        queue.wait();
                } 
                catch (InterruptedException e) 
                {
                        e.printStackTrace();
                }
            }


        }		
    }
    	
	
}
