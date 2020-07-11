package vehicle;

import java.sql.Timestamp;

public class Data {
        
    private double[] acc;
    private double[] gyro;
    private double temp;

    private Timestamp accStamp;
    private Timestamp gyroStamp;
    private Timestamp tempStamp;

    public Data()
    {

    }
    
    @Override
    public String toString()
    {
        String data_string = "Gyro(" + gyro[0] + ", " + gyro[1] + ", " + gyro[2] + ") " 
                            + "Acc(" + acc[0] + ", " + acc[1] + ", " + acc[2] + ") "
                            + "Temp(" + temp + ")";
        return  data_string;
    }

    public void setAccData(double[] acc)
    {
        this.acc = acc;
        this.accStamp = new Timestamp(System.currentTimeMillis());
    }

    public void setGyroData(double[] gyro)
    {
        this.gyro = gyro;
        this.gyroStamp = new Timestamp(System.currentTimeMillis());
    }

    public void setTempData(double temp)
    {
        this.temp = temp;
        this.tempStamp = new Timestamp(System.currentTimeMillis());
    }
    
    public double[] getAccData()
    {
        return acc;
    }
    
    public double[] getGyroData()
    {
        return gyro;
    }
    
    public double getTempData()
    {
        return temp;
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
	
}
