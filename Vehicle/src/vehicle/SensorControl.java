package vehicle;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SensorControl {

    //LSM6DS33 Adresses (Gyro Acc)
    public static final int GYROACC_ADRESS 	  = 0b1101011;  //LSM6DS33 IIC Adress
    public static final byte CTRL1_XL          = (byte)0x10; //Gyro Control Register
    public static final byte CTRL2_G           = (byte)0x11; //Accel. Control Register
    public static final byte CTRL3_C           = (byte)0x12; //Common Control Register
    public static final byte OUTX_L_XL         = (byte)0x28; //Accel. data 6 Bytes
    public static final byte OUTX_L_G          = (byte)0x22; //Gyro data 6 Bytes
    public static final byte OUT_L_TEMP          = (byte)0x22; //Temp data 
    public static final byte OUT_H_TEMP          = (byte)0x23; //Temp data 
    
    //LPS25H Adresses (Barometer)
    public static final int BAROMETER_ADRESS 	  = 0b1011101;  //LPS25H IIC Adress
    public static final int CTRL_REG1 	  = 0x20;   //Control Register
    public static final int PRESS_OUT_XL  = 0x28;   //Pressure Register 3 Byte
    public static final int TEMP_OUT_L    = 0x2B;   //Temperature Register 
    public static final int TEMP_OUT_H    = 0x2C;   //Temperature Register 
    
    //LIS3MDL  Adresses (Magnetometer)
    public static final int MAGNET_ADRESS   = 0b0011110;  //LIS3MDL IIC Adress
    public static final int CTRL_REG1_MAG   = 0x20;  //Control Reg
    public static final int CTRL_REG2_MAG   = 0x21;  //Cntrol Reg
    public static final int CTRL_REG3_MAG   = 0x22;  //Cntrol Reg
    public static final int CTRL_REG4_MAG   = 0x23;  //Cntrol Reg
    public static final int OUT_X_L         = 0x28;  //Magnet Register 6 Byte
   
    
    I2CDevice gyroacc;
    I2CDevice magnet;
    I2CDevice baro;

    I2CBus i2c;		
	
    public SensorControl()
    {
        //Create instance of I2C-bus 1
        try 
        {
           i2c = I2CFactory.getInstance(I2CBus.BUS_1);
           System.out.println("I2c bus instance assigned.");
        } 
        catch (UnsupportedBusNumberException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        }

        //Create instance of I2C device LSM6DS33, initialize sensor (Gyroscope and acceleration)
        try 
        {
           gyroacc = i2c.getDevice(GYROACC_ADRESS);
           System.out.println("Gyroacc device assigned.");
           initGyroacc();
           System.out.println("Gyroacc initialized.");
        } 
        catch (IOException e) {
           e.printStackTrace();
        }
        
        //Create instance of I2C device LPS25H, initialize sensor (Barometer)
        try 
        {
           baro = i2c.getDevice(BAROMETER_ADRESS);
           System.out.println("Barometer device assigned.");
           initBarometer();
           System.out.println("Barometer initialized.");
        } 
        catch (IOException e) {
           e.printStackTrace();
        }
        
        //Create instance of I2C device LIS3MDL, initialize sensor (Magnet)
        try 
        {
           magnet = i2c.getDevice(MAGNET_ADRESS);
           System.out.println("Magnetometer device assigned.");
           initMagnetometer();
           System.out.println("Magnetometer initialized.");
        } 
        catch (IOException e) {
           e.printStackTrace();
        }

        //Wait for sensor outputs to stabilize
        try 
        {
            Thread.sleep(50);
        } 
        catch (InterruptedException ex) {
            System.out.println("Couldnt sleep after sensor init.");
        }
    }
    
    private void initMagnetometer()
    {
        try 
        {
            // 0x70 = 0b01110000
            // OM = 11 (ultra-high-performance mode for X and Y); DO = 100 (10 Hz ODR)
            magnet.write(CTRL_REG1_MAG, (byte)0x70);
            // 0x00 = 0b00000000
            // FS = 00 (+/- 4 gauss full scale)
            magnet.write(CTRL_REG2_MAG, (byte)0x00);
            // 0x00 = 0b00000000
            // MD = 00 (continuous-conversion mode)
            magnet.write(CTRL_REG3_MAG, (byte)0x00);
            // 0x0C = 0b00001100
            // OMZ = 11 (ultra-high-performance mode for Z)
            magnet.write(CTRL_REG4_MAG, (byte)0x0C);        
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    private void initBarometer()
    {
        try 
        {
            //Enable with 12.5Hz data rate
            baro.write(CTRL_REG1, (byte) 0xB0);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Initialize LSM6DS33 
    private void initGyroacc() 
    {
        try 
        {
            // Accelerometer
            // 0x80 = 0b10000000 = 128
            // ODR = 1000 (1.66 kHz (high performance)); FS_XL = 00 (+/-2 g full scale)
            gyroacc.write(CTRL1_XL, (byte) 0x80);                
            // Gyro
            // 0x80 = 0b10000000
            // ODR = 1000 (1.66 kHz (high performance)); FS_XL = 00 (245 dps)
            gyroacc.write(CTRL2_G, (byte) 0x80);
            // Common
            // 0x04 = 0b00000100
            // IF_INC = 1 (automatically increment register address)
            gyroacc.write(CTRL3_C, (byte) 0x04);           
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public double[] readMagnet()
    {
        byte[] reg_values = new byte[6];
        
        try 
        {
            baro.read(OUT_X_L | 0x80, reg_values, 0, 6);
        } 
        catch (IOException ex) {
            System.out.println("Couldnt read magneto data.");
        }
        
        // +/- 4 Gauss
        double[] mag = new double[3];
        mag[0] = ((int)(((reg_values[1] & 0xFF) & 0b01111111) << 8 | (reg_values[0] & 0xFF))) / 32767.0 * 4;
        mag[1] = ((int)(((reg_values[3] & 0xFF) & 0b01111111) << 8 | (reg_values[2] & 0xFF))) / 32767.0 * 4;
        mag[2] = ((int)(((reg_values[5] & 0xFF) & 0b01111111) << 8 | (reg_values[4] & 0xFF))) / 32767.0 * 4;

        if(((reg_values[1] & 0xFF) & 0b10000000) != 0) mag[0] *= -1;
        if(((reg_values[3] & 0xFF) & 0b10000000) != 0) mag[1] *= -1;
        if(((reg_values[5] & 0xFF) & 0b10000000) != 0) mag[2] *= -1;
        
        return mag;
    }
    
    public double readPressure()
    {        
        byte[] reg_values = new byte[3];
        
        try 
        {
            baro.read(PRESS_OUT_XL | (1 << 7), reg_values, 0, 3);
        } 
        catch (IOException ex) {
            System.out.println("Couldnt read barometer data.");
        }
        
        /*for(int i = 0; i < reg_values.length ;i++)
        {
            System.out.println(Integer.toBinaryString(reg_values[i] & 0xFF));
            System.out.println(reg_values[i] & 0xFF);
        }*/
        
        double pressureMillibar = ((reg_values[2] & 0xFF) << 16 | (reg_values[1] & 0xFF) << 8 | (reg_values[0] & 0xFF)) / 4096.0;
        
        return pressureMillibar;
    }
    
    public double pressureToAltitude(double pressure_mbar)
    {   //1013.25
        return (1 - Math.pow(pressure_mbar / 1018.7, 0.190295)) * 44330.8;
    }

    public double[] readAcc() 
    {
        byte[] reg_values = new byte[6];

        try 
        {
            gyroacc.read(OUTX_L_XL, reg_values, 0, 6); 
        } 
        catch (IOException ex) {
            System.out.println("Couldnt read acc. data.");
        }                

        //Join MSB and LSB as two-complement, scaled to 2g
        double[] acc = new double[3];
        acc[0] = ((int)(((reg_values[1] & 0xFF) & 0b01111111) << 8 | (reg_values[0] & 0xFF))) / 32767.0 * 2;
        acc[1] = ((int)(((reg_values[3] & 0xFF) & 0b01111111) << 8 | (reg_values[2] & 0xFF))) / 32767.0 * 2;
        acc[2] = ((int)(((reg_values[5] & 0xFF) & 0b01111111) << 8 | (reg_values[4] & 0xFF))) / 32767.0 * 2;

        if(((reg_values[1] & 0xFF) & 0b10000000) != 0) acc[0] *= -1;
        if(((reg_values[3] & 0xFF) & 0b10000000) != 0) acc[1] *= -1;
        if(((reg_values[5] & 0xFF) & 0b10000000) != 0) acc[2] *= -1;

        return acc;
    }

    public double[] readGyro()
    {
        byte[] reg_values = new byte[6];

        try 
        {
            gyroacc.read(OUTX_L_G, reg_values, 0, 6); 
        } 
        catch (IOException ex) {
            System.out.println("Couldnt read gyro data.");
        }                

        //Join MSB and LSB as two-complement, scaled to 245dps
        double[] gyro = new double[3];
        gyro[0] = ((int)(((reg_values[1] & 0xFF) & 0b01111111) << 8 | (reg_values[0] & 0xFF))) / 32767.0 * 245;
        gyro[1] = ((int)(((reg_values[3] & 0xFF) & 0b01111111) << 8 | (reg_values[2] & 0xFF))) / 32767.0 * 245;
        gyro[2] = ((int)(((reg_values[5] & 0xFF) & 0b01111111) << 8 | (reg_values[4] & 0xFF))) / 32767.0 * 245;

        if(((reg_values[1] & 0xFF) & 0b10000000) != 0) gyro[0] *= -1;
        if(((reg_values[3] & 0xFF) & 0b10000000) != 0) gyro[1] *= -1;
        if(((reg_values[5] & 0xFF) & 0b10000000) != 0) gyro[2] *= -1;

        return gyro;
    }

    //Read temperature from barometer sensor
    public double readTemp()
    {
        byte[] reg_values = new byte[2];

        try 
        {
            reg_values[0] = (byte)baro.read(TEMP_OUT_L); 
            reg_values[1] = (byte)baro.read(TEMP_OUT_H); 
        } 
        catch (IOException ex) {
            System.out.println("Couldnt read temperature data.");
        }  

        //Join MSB and LSB as two-complement, 0 at 25°C, 16 LSB/°C
       // double temp = ((int)(((reg_values[1] & 0xFF) & 0b01111111) << 8 | (reg_values[0] & 0xFF))) / 16.0 + 25;
        double temp = ((int)(((reg_values[1] & 0xFF) & 0b01111111) << 8 | (reg_values[0] & 0xFF)));
        if(((reg_values[1] & 0xFF) & 0b10000000) == 1) temp *= -1;
        
        temp = temp/480.0 + 42.5;
        //temp = temp/16.0 + 25;
        
        return temp;
    }


    //Not used, only needed for setup of the I2C communication
    private void checkI2CBusses()
    {        
        // fetch all available busses
        try {
           int[] ids = I2CFactory.getBusIds();
           for(int i = 0; i < ids.length; i++)
           {
               System.out.println(ids[i]);
           }

        } catch (IOException exception) {
           System.out.println("I/O error during fetch of I2C busses occurred");
        }

        // find available busses
        for (int number = I2CBus.BUS_0; number <= I2CBus.BUS_17; ++number) {
           try {
               @SuppressWarnings("unused")
                               I2CBus bus = I2CFactory.getInstance(number);
               System.out.println("Supported I2C bus " + number + " found");
           } catch (IOException exception) {
               System.out.println("I/O error on I2C bus " + number + " occurred");
           } catch (UnsupportedBusNumberException exception) {
               System.out.println("Unsupported I2C bus " + number + " required");
           }
        }      
    }
	
}
