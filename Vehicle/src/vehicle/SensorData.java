package vehicle;

import java.sql.Timestamp;

public class SensorData {
        
    private double[] acc;
    private double[] gyro;
    private double[] magnet;
    private double temperature;
    private double altitude;

    private Timestamp accStamp;
    private Timestamp gyroStamp;
    private Timestamp tempStamp;
    private Timestamp altStamp;
    private Timestamp magnetStamp;

    public SensorData()
    {

    }
    
    @Override
    public String toString()
    {
        String data_string = "Gyro(" + gyro[0] + ", " + gyro[1] + ", " + gyro[2] + ") " 
                            + "Acc(" + acc[0] + ", " + acc[1] + ", " + acc[2] + ") "
                            + "Temp(" + temperature + ") " + "Alt(" + altitude + ")"
                            + "Magnet(" + magnet[0] + ", " + magnet[1] + ", " + magnet[2] + ") ";
        return  data_string;
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
