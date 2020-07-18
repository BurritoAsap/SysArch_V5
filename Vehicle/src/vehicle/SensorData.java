package vehicle;

import java.io.DataInputStream;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;

public class SensorData {
        
    private double[] acc;
    private double[] gyro;
    private double[] magnet;
    private double temperature;
    private double altitude;
    private double cpuTemperature;

    private Timestamp accStamp;
    private Timestamp gyroStamp;
    private Timestamp tempStamp;
    private Timestamp altStamp;
    private Timestamp magnetStamp;
    private Timestamp cpuTempStamp;

    public SensorData()
    {

    }
    
    public JSONObject toJSON()
    {
        JSONObject obj = new JSONObject();
        
        JSONArray s1 = new JSONArray();
        JSONArray s3 = new JSONArray();
        
        //SensorValue1
        JSONObject vTemp = new JSONObject();
        vTemp.put("name", "Temperature");
        vTemp.put("timestamp", tempStamp.getTime());
        vTemp.put("value", temperature);
        
        JSONObject vAlt = new JSONObject();
        vAlt.put("name", "Altitude");
        vAlt.put("timestamp", altStamp.getTime());
        vAlt.put("value", altitude);
        
        JSONObject vCPUTemp = new JSONObject();
        vCPUTemp.put("name", "CPUTemperature");
        vCPUTemp.put("timestamp", cpuTempStamp.getTime());
        vCPUTemp.put("value", cpuTemperature);
        
        //SensorValue3
        JSONObject vAcc = new JSONObject();
        vAcc.put("name", "Acceleration");
        vAcc.put("timestamp", accStamp.getTime());
        vAcc.put("valueX", acc[0]);
        vAcc.put("valueY", acc[1]);
        vAcc.put("valueZ", acc[2]);
        
        JSONObject vGyro = new JSONObject();
        vGyro.put("name", "Gyro");
        vGyro.put("timestamp", gyroStamp.getTime());
        vGyro.put("valueX", gyro[0]);
        vGyro.put("valueY", gyro[1]);
        vGyro.put("valueZ", gyro[2]);
        
        JSONObject vMagnet = new JSONObject();
        vMagnet.put("name", "Magnet");
        vMagnet.put("timestamp", magnetStamp.getTime());
        vMagnet.put("valueX", magnet[0]);
        vMagnet.put("valueY", magnet[1]);
        vMagnet.put("valueZ", magnet[2]);
        
        s1.put(vTemp);
        s1.put(vAlt);
        s1.put(vCPUTemp);
        
        s3.put(vAcc);
        s3.put(vGyro);
        s3.put(vMagnet);
        
        obj.put("Sensor1Val", s1);
        obj.put("Sensor3Val", s3);  
                        
        return obj;
    }
    
    
    @Override
    public String toString()
    {
        String data_string = "Gyro(" + gyro[0] + ", " + gyro[1] + ", " + gyro[2] + ") " 
                            + "Acc(" + acc[0] + ", " + acc[1] + ", " + acc[2] + ") "
                            + "Temp(" + temperature + ") " + "Alt(" + altitude + ")"
                            + "Magnet(" + magnet[0] + ", " + magnet[1] + ", " + magnet[2] + ") "
                            + "CPUTemp (" + cpuTemperature + ")";
        return  data_string;
    }
    
    public void setCPUTemp(double cpuTemp)
    {
        this.cpuTemperature = cpuTemp;
        this.cpuTempStamp = new Timestamp(System.currentTimeMillis());
    }
    
    public void setMagnet(double[] mag)
    {
        this.magnet = mag;
        this.magnetStamp = new Timestamp(System.currentTimeMillis());
    }

    public void setAcc(double[] acc)
    {
        this.acc = acc;
        this.accStamp = new Timestamp(System.currentTimeMillis());
    }

    public void setGyro(double[] gyro)
    {
        this.gyro = gyro;
        this.gyroStamp = new Timestamp(System.currentTimeMillis());
    }

    public void setTemp(double temp)
    {
        this.temperature = temp;
        this.tempStamp = new Timestamp(System.currentTimeMillis());
    }
    
    public void setAltitude(double alt)
    {
        this.altitude = alt;
        this.altStamp = new Timestamp(System.currentTimeMillis());
    }
    
    public double getCPUTemp()
    {
        return cpuTemperature;
    }
    
    public double[] getMagnet()
    {
        return magnet;
    }
    
    public double[] getAcc()
    {
        return acc;
    }
    
    public double[] getGyro()
    {
        return gyro;
    }
    
    public double getTemp()
    {
        return temperature;
    }
    
    public double getAltitude()
    {
        return temperature;
    }
    
    public Timestamp getStampCPUTemp()
    {
        return cpuTempStamp;
    }
    
    public Timestamp getStampManget()
    {
        return magnetStamp;
    }      

    public Timestamp getStampGyro()
    {
        return gyroStamp;
    }        

    public Timestamp getStampAcc()
    {
        return accStamp;
    }        

    public Timestamp getStampTemp()
    {
        return tempStamp;
    }
    
    public Timestamp getStampAlt()
    {
        return altStamp;
    }
	
}
