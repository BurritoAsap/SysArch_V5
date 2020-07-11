# System Architecture Project: Vehicle

This Java program is supposed to run on a raspberry controlled vehicle.
It collects sensor data in realtime, logs them to disk and also sends them through MQTT to a connected web-service.

##Prerequisites/Requirements
* Java OpenJDK 8 (1.8) for compatibility with the Pi4J library
* Pi4J library for IIC communication

##Implementation details
There are currently three tasks:
* **TaskAcquisition** is responsible for the acquisition of data from the connected sensors. It has the highest priority and is executed every 100ms. Sensor readings are collected through the **SensorControl** object and stored in a **Data** object, which is subsequently put in a commonly shared **BlockingQueue** for processing by the 'Logger Task'. A synchronized variable, declared in the main class, is used to store the most recent sensor values.  
* **TaskGUI** displays the most recently collected sensor values to the user via command line. After doing so, it waits on the BlockingQueue for notification by the 'Acquisition Task'.
* **TaskLogger** polls the **Data** objects from the BlockingQueue and writes the values of each object to a new line of a file.

##Development
The NetBeans IDE is used for remote development on the Raspberry via SSH.

##TODO
* Add error-handling when retrieving the sensor values



































