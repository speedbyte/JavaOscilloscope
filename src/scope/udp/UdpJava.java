package scope.udp;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;

import scope.mutex.*;

import javax.xml.bind.DatatypeConverter;;

public class UdpJava
{
		static byte[] receiveData = new byte[200];
		static byte[] sendData = new byte[200];
		static DatagramSocket serverSocket;
		static DatagramPacket receivePacket;
		public Mutex udpMutex = null;
		static double time = 0;
		public UdpJava()
		{
			udpMutex = new Mutex();
			System.out.println("constructor Receive UDP Data");
		}
            
        public Boolean startServer() throws Exception
		{
        	serverSocket = new DatagramSocket(5000);			
			System.out.println("Started server");
        	//serverSocket.setSoTimeout(1000);
	        return true;
		}
        
        public double[] receiveNonBlocking() throws Exception
        {
    		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    		byte[] tv_sec_array = {0,0,0,0};
    		byte[] tv_nsec_array = {0,0,0,0};
    		byte[] dummy_array = {0,0,0,0,0,0,0,0};
    		byte[] acc_x_array = {0,0,0,0,0,0,0,0};
    		byte[] acc_y_array = {0,0,0,0,0,0,0,0};
    		byte[] acc_z_array = {0,0,0,0,0,0,0,0};
    		byte[] mag_x_array = {0,0,0,0,0,0,0,0};
    		byte[] mag_y_array = {0,0,0,0,0,0,0,0};
    		byte[] mag_z_array = {0,0,0,0,0,0,0,0};
    		byte[] gyro_x_array = {0,0,0,0,0,0,0,0};
    		byte[] gyro_y_array = {0,0,0,0,0,0,0,0};
    		byte[] gyro_z_array = {0,0,0,0,0,0,0,0};
    		byte[] temp_array = {0,0,0,0,0,0,0,0};
    		byte[] pressure_array = {0,0,0,0,0,0,0,0};
    		byte[] kalman_x_array = {0,0,0,0,0,0,0,0};
    		byte[] kalman_y_array = {0,0,0,0,0,0,0,0};
    		byte[] kalman_z_array = {0,0,0,0,0,0,0,0};
    		byte[] compl_x_array = {0,0,0,0,0,0,0,0};
    		byte[] compl_y_array = {0,0,0,0,0,0,0,0};
    		byte[] compl_z_array = {0,0,0,0,0,0,0,0};
    		
    		double[] sentence = new double[19];
    		int tv_sec = 0;
    		int tv_nsec = 0;
    		long dummy = 0;
    		long acc_x = 0;
    		long acc_y = 0;
    		long acc_z = 0;
    		long mag_x = 0;
    		long mag_y = 0;
    		long mag_z = 0;
    		long gyro_x = 0;
    		long gyro_y = 0;
    		long gyro_z = 0;
    		long temp = 0;
    		long pressure = 0;
    		long kalman_x = 0;
    		long kalman_y = 0;
    		long kalman_z = 0;
    		long compl_x = 0;
    		long compl_y = 0;
    		long compl_z = 0;
    		
    		double acc_x_double = 0.0;
    		double acc_y_double = 0.0;
    		double acc_z_double = 0.0;
    		double mag_x_double = 0.0;
    		double mag_y_double = 0.0;
    		double mag_z_double = 0.0;
    		double gyro_x_double = 0.0;
    		double gyro_y_double = 0.0;
    		double gyro_z_double = 0.0;
    		double temp_double = 0.0;
    		double pressure_double = 0.0;
    		double kalman_x_double = 0.0;
    		double kalman_y_double = 0.0;
    		double kalman_z_double = 0.0;
    		double compl_x_double = 0.0;
    		double compl_y_double = 0.0;
    		double compl_z_double = 0.0;
    		
	        double[] helikopterElements = {0.0, 0.0, 0.0};
	        double[] data = new double[100];
	        char[] charbuff = new char[20000];
	        byte[] buffer = new byte[1024];
    		int i = 0;
    		try {
        		serverSocket.receive(receivePacket);
        		//System.arraycopy(receivePacket.getData(), 0, tv_sec_array, 0, 4);
    	        for ( i = 0; i< 4; i++)
    	        {
    	        	tv_sec_array[i] = 	receivePacket.getData()[i];
    	        	tv_nsec_array[i] = 	receivePacket.getData()[i+4];
    	        	tv_sec |= ((tv_sec_array[i] & 0xFF) << i*8);  
    	        	tv_nsec |= ((tv_nsec_array[i] & 0xFF) << i*8);  
    	        }
    	       
    	        for (int j = 0; j < 8; j++) {
					buffer[j] = receivePacket.getData()[j];
				}
    	        
    	        String out = new String(buffer, 0, receivePacket.getLength());
	    	    System.out.println(out);  
    	        data[1] = Double.parseDouble(out);
    	        time += 0.02;
    	        data[0] = time;
    	            	        
    	    
    	    
    		} catch (SocketTimeoutException e1) {
    			System.out.println("nothing to do yet");
    			//e1.printStackTrace();
    		}
			return data;
    		
        }
        
        /*
         * 8 byte data from each packet
         * Acc x
         * acc y
         * acc z
         * mag x
         * mag y
         * mag z
         * gyro yaw
         * gyro pitch
         * gyro roll
         * temp 
         * pressure
         */
        
//    	        
//    	        for ( i = 0; i < 8; i++)
//    	        {
////    	        	dummy_array[i] = 	receivePacket.getData()[i+8];
//    	        	
//        	        
//    	        	acc_x_array[i] = 	receivePacket.getData()[i+8];
//    	        	acc_y_array[i] = 	receivePacket.getData()[i+16];
//    	        	acc_z_array[i] = 	receivePacket.getData()[i+24];
//    	        	mag_x_array[i] = 	receivePacket.getData()[i+32];
//    	        	mag_y_array[i] = 	receivePacket.getData()[i+40];
//    	        	mag_z_array[i] = 	receivePacket.getData()[i+48];
//    	        	gyro_x_array[i] = 	receivePacket.getData()[i+56];
//    	        	gyro_y_array[i] = 	receivePacket.getData()[i+64];
//    	        	gyro_z_array[i] = 	receivePacket.getData()[i+72];
////    	        	temp_array[i] = 	receivePacket.getData()[i+88];
////    	        	pressure_array[i] = receivePacket.getData()[i+96];
//    	        	kalman_x_array[i] = receivePacket.getData()[i+80];
//    	        	kalman_y_array[i] = receivePacket.getData()[i+88];
//    	        	kalman_z_array[i] = receivePacket.getData()[i+96];
//    	        	compl_x_array[i] = 	receivePacket.getData()[i+104];
//    	        	compl_y_array[i] = 	receivePacket.getData()[i+112];
//    	        	compl_z_array[i] = 	receivePacket.getData()[i+120];
////    	        	kalman_x_array[i] = receivePacket.getData()[i+104];
////    	        	kalman_y_array[i] = receivePacket.getData()[i+112];
////    	        	kalman_z_array[i] = receivePacket.getData()[i+120];
////    	        	compl_x_array[i] = 	receivePacket.getData()[i+128];
////    	        	compl_y_array[i] = 	receivePacket.getData()[i+136];
////    	        	compl_z_array[i] = 	receivePacket.getData()[i+144];
//    	        	// 152 bytes
////    	        	dummy |= ((dummy_array[i] & 0xFF) << i*8);  
//
//    	        	acc_x |= (long)(acc_x & 0xFFFFFFFFL) | ((long)(acc_x_array[i] & 0xFF) << i*8);
//    	        	acc_y |= (long)(acc_y & 0xFFFFFFFFL) | ((long)(acc_y_array[i] & 0xFF) << i*8);
//    	        	acc_z |= (long)(acc_z & 0xFFFFFFFFL) | ((long)(acc_z_array[i] & 0xFF) << i*8);
////    	        	angVelocity_x |= (long)(angVelocity_x & 0xFFFFFFFFL) | ((long)(acc_x_array[i] & 0xFF) << i*8);
////    	        	angVelocity_y |= (long)(angVelocity_y & 0xFFFFFFFFL) | ((long)(acc_y_array[i] & 0xFF) << i*8);
////    	        	angVelocity_z |= (long)(angVelocity_z & 0xFFFFFFFFL) | ((long)(acc_z_array[i] & 0xFF) << i*8);
//    	        	mag_x |= (long)(mag_x & 0xFFFFFFFFL) | ((long)(mag_x_array[i] & 0xFF) << i*8);
//    	        	mag_y |= (long)(mag_y & 0xFFFFFFFFL) | ((long)(mag_y_array[i] & 0xFF) << i*8);
//    	        	mag_z |= (long)(mag_z & 0xFFFFFFFFL) | ((long)(mag_z_array[i] & 0xFF) << i*8);
////    	        	angleGyroStep_x |= (long)(angleGyroStep_x & 0xFFFFFFFFL) | ((long)(mag_x_array[i] & 0xFF) << i*8);
////    	        	angleGyroStep_y |= (long)(angleGyroStep_y & 0xFFFFFFFFL) | ((long)(mag_y_array[i] & 0xFF) << i*8);
////    	        	angleGyroStep_z |= (long)(angleGyroStep_z & 0xFFFFFFFFL) | ((long)(mag_z_array[i] & 0xFF) << i*8);
//    	        	gyro_x |= (long)(gyro_x & 0xFFFFFFFFL) | ((long)(gyro_x_array[i] & 0xFF) << i*8);
//    	        	gyro_y |= (long)(gyro_y & 0xFFFFFFFFL) | ((long)(gyro_y_array[i] & 0xFF) << i*8);
//    	        	gyro_z |= (long)(gyro_z & 0xFFFFFFFFL) | ((long)(gyro_z_array[i] & 0xFF) << i*8);
////    	        	angleAccMagCalc_x |= (long)(angleAccMagCalc_x & 0xFFFFFFFFL) | ((long)(gyro_x_array[i] & 0xFF) << i*8);
////    	        	angleAccMagCalc_y |= (long)(angleAccMagCalc_y & 0xFFFFFFFFL) | ((long)(gyro_y_array[i] & 0xFF) << i*8);
////    	        	angleAccMagCalc_z |= (long)(angleAccMagCalc_z & 0xFFFFFFFFL) | ((long)(gyro_z_array[i] & 0xFF) << i*8);
//    	        	temp |= (long)(temp & 0xFFFFFFFFL) | ((long)(temp_array[i] & 0xFF) << i*8);  
//    	        	pressure |= (long)(pressure & 0xFFFFFFFFL) | ((long)(pressure_array[i] & 0xFF) << i*8);
//    	        	kalman_x |= (long)(kalman_x & 0xFFFFFFFFL) | ((long)(kalman_x_array[i] & 0xFF) << i*8);
//    	        	kalman_y |= (long)(kalman_y & 0xFFFFFFFFL) | ((long)(kalman_y_array[i] & 0xFF) << i*8);
//    	        	kalman_z |= (long)(kalman_z & 0xFFFFFFFFL) | ((long)(kalman_z_array[i] & 0xFF) << i*8);
//    	        	compl_x |= (long)(compl_x & 0xFFFFFFFFL) | ((long)(compl_x_array[i] & 0xFF) << i*8);
//    	        	compl_y |= (long)(compl_y & 0xFFFFFFFFL) | ((long)(compl_y_array[i] & 0xFF) << i*8);
//    	        	compl_z |= (long)(compl_z & 0xFFFFFFFFL) | ((long)(compl_z_array[i] & 0xFF) << i*8);
//    	        }
//    	        acc_x_double = java.lang.Double.longBitsToDouble(acc_x);
//    	        acc_y_double = java.lang.Double.longBitsToDouble(acc_y);
//    	        acc_z_double = java.lang.Double.longBitsToDouble(acc_z);
////    	        angVelocity_x_double = java.lang.Double.longBitsToDouble(angVelocity_x);
////    	        angVelocity_y_double = java.lang.Double.longBitsToDouble(angVelocity_y);
////    	        angVelocity_z_double = java.lang.Double.longBitsToDouble(angVelocity_z);
//    	        mag_x_double = java.lang.Double.longBitsToDouble(mag_x);
//    	        mag_y_double = java.lang.Double.longBitsToDouble(mag_y);
//    	        mag_z_double = java.lang.Double.longBitsToDouble(mag_z);
////    	        angleGyroStep_x_double = java.lang.Double.longBitsToDouble(angleGyroStep_x);
////    	        angleGyroStep_y_double = java.lang.Double.longBitsToDouble(angleGyroStep_y);
////    	        angleGyroStep_z_double = java.lang.Double.longBitsToDouble(angleGyroStep_z);
//    	        gyro_x_double = java.lang.Double.longBitsToDouble(gyro_x);
//    	        gyro_y_double = java.lang.Double.longBitsToDouble(gyro_y);
//    	        gyro_z_double = java.lang.Double.longBitsToDouble(gyro_z);
////    	        angleAccMagCalc_x_double = java.lang.Double.longBitsToDouble(angleAccMagCalc_x);
////    	        angleAccMagCalc_y_double = java.lang.Double.longBitsToDouble(angleAccMagCalc_y);
////    	        angleAccMagCalc_z_double = java.lang.Double.longBitsToDouble(angleAccMagCalc_z);
//    	        temp_double = java.lang.Double.longBitsToDouble(temp);
//    	        pressure_double = java.lang.Double.longBitsToDouble(pressure);
//    	        kalman_x_double = java.lang.Double.longBitsToDouble(kalman_x);
//    	        kalman_y_double = java.lang.Double.longBitsToDouble(kalman_y);
//    	        kalman_z_double = java.lang.Double.longBitsToDouble(kalman_z);
//    	        compl_x_double = java.lang.Double.longBitsToDouble(compl_x);
//    	        compl_y_double = java.lang.Double.longBitsToDouble(compl_y);
//    	        compl_z_double = java.lang.Double.longBitsToDouble(compl_z);
//    	        
//    	        
////    	        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(receivePacket.getData()));
////    	        //System.out.println(Arrays.toString(receivePacket.getData()));
//
//    	        System.out.println(String.format("Acceleration %f %f %f", acc_x_double, acc_y_double, acc_z_double));
//    	        System.out.println(String.format("Magnetic %f %f %f", mag_x_double, mag_y_double, mag_z_double));
//    	        System.out.println(String.format("Orientation %f %f %f", gyro_x_double, gyro_y_double, gyro_z_double));
////    	        System.out.println(String.format("Temperature %f ", temp_double));
////    	        System.out.println(String.format("Pressure %f ", pressure_double));
//    	        System.out.println(String.format("Kalman filter Orientation %f %f %f", kalman_x_double, kalman_y_double, kalman_z_double));
//    	        System.out.println(String.format("Complementary filter Orientation %f %f %f", compl_x_double, compl_y_double, compl_z_double));
//    	        System.out.println("Measurements end");
//
//    	        InetAddress IPAddress = receivePacket.getAddress();
//    	        int port = receivePacket.getPort();
//    	        helikopterElements[0] = acc_x_double;
//    	        helikopterElements[1] = acc_y_double;
//    	        helikopterElements[2] = acc_z_double;
//
//    	        sentence[0] = acc_x_double;
//    	        sentence[1] = acc_y_double;
//    	        sentence[2] = acc_z_double;
////    	        sentence[0] = angVelocity_x_double;
////    	        sentence[1] = angVelocity_y_double;
////    	        sentence[2] = angVelocity_z_double;
//    	        sentence[3] = mag_x_double;
//    	        sentence[4] = mag_y_double;
//    	        sentence[5] = mag_z_double;
////    	        sentence[3] = angleGyroStep_x_double;
////    	        sentence[4] = angleGyroStep_y_double;
////    	        sentence[5] = angleGyroStep_z_double;
//    	        sentence[0] = gyro_x_double;
//    	        sentence[1] = gyro_y_double;
//    	        sentence[2] = gyro_z_double;
//    	        sentence[3] = kalman_x_double;
//    	        sentence[4] = kalman_y_double;
//    	        sentence[5] = kalman_z_double;
//    	        sentence[6] = compl_x_double;
//    	        sentence[7] = compl_y_double;
//    	        sentence[8] = compl_z_double;
//    	        
////    	        String capitalizedSentence = sentence.toUpperCase();
////    	        capitalizedSentence = capitalizedSentence + '\n';
////    	        sendData = capitalizedSentence.getBytes();
////    	        DatagramPacket sendPacket =
////    	        	new DatagramPacket(sendData, sendData.length, IPAddress, port);
////    	        serverSocket.send(sendPacket);
//    	        
//    		 catch (SocketTimeoutException e1) {
//    			System.out.println("nothing to do yet");
//    			//e1.printStackTrace();
//    			sentence = null;
//    		}
//    		
//	        return sentence;
//        }
        public void disconnectPort()
        {
        	System.out.println("Closing socket");
        	serverSocket.close();
        }
        
}

