package vehicle;

import java.sql.Timestamp;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Vehicle {
    
    //Userdata
    private static String userName;
    private static String userFullName;
    private static String userEmail;
    private static Boolean loggedIn;
    private static long timestamp;

    public static void main(String args[])
    {
        BlockingQueue<String> queueGUI = new ArrayBlockingQueue<>(50);
        BlockingQueue<SensorData> queueLogger = new ArrayBlockingQueue<>(50);
        BlockingQueue<SensorData> queueMQTT = new ArrayBlockingQueue<>(500);
        
        //Start GUI Task first for printlns
        TaskGUI gui = new TaskGUI(queueGUI);
        gui.start();
        
        SensorControl sensors = new SensorControl();
        
        TaskAcquisition acquisition = new TaskAcquisition(queueLogger, queueMQTT, queueGUI, sensors);
        TaskLogger logger = new TaskLogger(queueLogger, queueGUI);
        TaskMQTT mqtt = new TaskMQTT(queueMQTT, queueGUI);

        acquisition.setPriority(6);
        logger.setPriority(5);
        mqtt.setPriority(4);
        gui.setPriority(3);	

        //Run acquisiton task every 100ms
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(acquisition, 0, 100, TimeUnit.MILLISECONDS);

        //Start logger and mqtt thread to run indefinitely
        logger.start();
        mqtt.start();


        while(true)
        {

        }
    }    
        
    public synchronized static void logIn(String name, String fullName, String email, long time)
    {	
        userName = name;
        userFullName = fullName;
        userEmail = email;
        loggedIn = true;
        timestamp = time;
    }
    
    public synchronized static void logOut(long time)
    {
        loggedIn = false;        
        timestamp = time;
    }

	
	
	
}
