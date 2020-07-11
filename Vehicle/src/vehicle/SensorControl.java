package vehicle;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SensorControl {

    //LSM6DS33 Adresses
    public static final int GYROACC_ADRESS 	  = 0b1101011;  //LSM6DS33 IIC Adress
    public static final byte CTRL1_XL          = (byte)0x10; //Gyro Control Register
    public static final byte CTRL2_G           = (byte)0x11; //Accel. Control Register
    public static final byte CTRL3_C           = (byte)0x12; //Common Control Register
    public static final byte OUTX_L_XL         = (byte)0x28; //Accel. data 6 Bytes
    public static final byte OUTX_L_G          = (byte)0x22; //Gyro data 6 Bytes
    public static final byte OUT_L_TEMP          = (byte)0x22; //Temp data 2 Bytes
    
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
           System.out.println("i2c bus instance assigned.");
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
           System.out.println("gyroacc R/W device assigned.");
           initGyroacc();
        } 
        catch (IOException e) {
           e.printStackTrace();
        }

    }

    //Initialize LSM6DS33 
    public void initGyroacc() 
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

            System.out.println("gyroacc initialized.");

            //Wait for output to stabilize
            try 
            {
                Thread.sleep(50);
            } 
            catch (InterruptedException ex) {
                System.out.println("Couldnt sleep after sensor init.");
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
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
        acc[0] = ((int)((reg_values[1] & 0b01111111) << 8 | reg_values[0])) / 32767.0 * 2;
        acc[1] = ((int)((reg_values[3] & 0b01111111) << 8 | reg_values[2])) / 32767.0 * 2;
        acc[2] = ((int)((reg_values[5] & 0b01111111) << 8 | reg_values[4])) / 32767.0 * 2;

        if((reg_values[1] & 0b10000000) != 0) acc[0] *= -1;
        if((reg_values[3] & 0b10000000) != 0) acc[1] *= -1;
        if((reg_values[5] & 0b10000000) != 0) acc[2] *= -1;

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

        //Join MSB and LSB as two-complement, scaled to 2g
        double[] gyro = new double[3];
        gyro[0] = ((int)((reg_values[1] & 0b01111111) << 8 | reg_values[0])) / 32767.0 * 2;
        gyro[1] = ((int)((reg_values[3] & 0b01111111) << 8 | reg_values[2])) / 32767.0 * 2;
        gyro[2] = ((int)((reg_values[5] & 0b01111111) << 8 | reg_values[4])) / 32767.0 * 2;

        if((reg_values[1] & 0b10000000) != 0) gyro[0] *= -1;
        if((reg_values[3] & 0b10000000) != 0) gyro[1] *= -1;
        if((reg_values[5] & 0b10000000) != 0) gyro[2] *= -1;

        return gyro;
    }

    public double readTemp()
    {
        byte[] reg_values = new byte[2];

        try 
        {
            reg_values[0] = (byte)gyroacc.read(OUT_L_TEMP); 
            reg_values[1] = (byte)gyroacc.read(OUT_L_TEMP + 1); 
        } 
        catch (IOException ex) {
            System.out.println("Couldnt read temperature data.");
        }  

        //Join MSB and LSB as two-complement, 0 at 25°C, 16 LSB/°C
        double temp = ((int)((reg_values[1] & 0b01111111) << 8 | reg_values[0])) / 16.0 + 25;
        if((reg_values[1] & 0b10000000) != 0) temp *= -1;

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

//Gyroacc Register Values
/*enum regAddr
    {
      FUNC_CFG_ACCESS(0x01),

      FIFO_CTRL1        = 0x06,
      FIFO_CTRL2        = 0x07,
      FIFO_CTRL3        = 0x08,
      FIFO_CTRL4        = 0x09,
      FIFO_CTRL5        = 0x0A,
      ORIENT_CFG_G      = 0x0B,

      INT1_CTRL         = 0x0D,
      INT2_CTRL         = 0x0E,
      WHO_AM_I          = 0x0F,
      CTRL1_XL          = 0x10,
      CTRL2_G           = 0x11,
      CTRL3_C           = 0x12,
      CTRL4_C           = 0x13,
      CTRL5_C           = 0x14,
      CTRL6_C           = 0x15,
      CTRL7_G           = 0x16,
      CTRL8_XL          = 0x17,
      CTRL9_XL          = 0x18,
      CTRL10_C          = 0x19,

      WAKE_UP_SRC       = 0x1B,
      TAP_SRC           = 0x1C,
      D6D_SRC           = 0x1D,
      STATUS_REG        = 0x1E,

      OUT_TEMP_L        = 0x20,
      OUT_TEMP_H        = 0x21,
      OUTX_L_G          = 0x22,
      OUTX_H_G          = 0x23,
      OUTY_L_G          = 0x24,
      OUTY_H_G          = 0x25,
      OUTZ_L_G          = 0x26,
      OUTZ_H_G          = 0x27,
      OUTX_L_XL         = 0x28,
      OUTX_H_XL         = 0x29,
      OUTY_L_XL         = 0x2A,
      OUTY_H_XL         = 0x2B,
      OUTZ_L_XL         = 0x2C,
      OUTZ_H_XL         = 0x2D,

      FIFO_STATUS1      = 0x3A,
      FIFO_STATUS2      = 0x3B,
      FIFO_STATUS3      = 0x3C,
      FIFO_STATUS4      = 0x3D,
      FIFO_DATA_OUT_L   = 0x3E,
      FIFO_DATA_OUT_H   = 0x3F,
      TIMESTAMP0_REG    = 0x40,
      TIMESTAMP1_REG    = 0x41,
      TIMESTAMP2_REG    = 0x42,

      STEP_TIMESTAMP_L  = 0x49,
      STEP_TIMESTAMP_H  = 0x4A,
      STEP_COUNTER_L    = 0x4B,
      STEP_COUNTER_H    = 0x4C,

      FUNC_SRC          = 0x53,

      TAP_CFG           = 0x58,
      TAP_THS_6D        = 0x59,
      INT_DUR2          = 0x5A,
      WAKE_UP_THS       = 0x5B,
      WAKE_UP_DUR       = 0x5C,
      FREE_FALL         = 0x5D,
      MD1_CFG           = 0x5E,
      MD2_CFG           = 0x5F,
    };*/