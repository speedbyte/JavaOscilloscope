package scope.udp;
import java.net.*;
import java.util.Arrays;
import scope.mutex.*;
import javax.xml.bind.DatatypeConverter;;

public class UdpJava
{
		static byte[] receiveData = new byte[1024];
		static byte[] sendData = new byte[1024];
		static DatagramSocket serverSocket;
		static DatagramPacket receivePacket;
		public Mutex udpMutex = null;

		public UdpJava()
		{
			System.out.println("constructor Receive UDP Data");
			udpMutex = new Mutex();
		}
            
        public Boolean startServer() throws Exception
		{
        	serverSocket = new DatagramSocket(5000);			
			//serverSocket.setSoTimeout(1000);
	        return true;
		}
        
        public String receiveNonBlocking() throws Exception
        {
    		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    		byte[] tv_sec_array = {0,0,0,0};
    		byte[] tv_nsec_array = {0,0,0,0};
    		byte[] dummy_array_1 = {0,0,0,0};
    		byte[] dummy_array_2 = {0,0,0,0};
    		byte[] acc_x_array = {0,0,0,0};
    		String sentence = null;
    		int tv_sec = 0;
    		int tv_nsec = 0;
    		int dummy_1 = 0;
    		int dummy_2 = 0;
    		long final_dummy = 0;
    		long tmp_dummy_long = 0L;
    		int acc_x_1 = 0;
    		int acc_x_2 = 0;
    		int i = 0;
    		try {
        		serverSocket.receive(receivePacket);
    	        sentence = new String( receivePacket.getData());
    	        for ( i = 0; i< 4; i++)
    	        {
    	        	tv_sec_array[i] = 	receivePacket.getData()[i];
    	        	tv_nsec_array[i] = 	receivePacket.getData()[i+4];
    	        	dummy_array_1[i] = 	receivePacket.getData()[i+8];
    	        	dummy_array_2[i] = 	receivePacket.getData()[i+12];

    	        	tv_sec |= ((tv_sec_array[i] & 0xFF) << i*8);  
    	        	tv_nsec |= ((tv_nsec_array[i] & 0xFF) << i*8);  
    	        	dummy_1 |= ((dummy_array_1[i] & 0xFF) << i*8);  
    	        	dummy_2 |= ((dummy_array_2[i] & 0xFF) << i*8);  
    	        }
    	        
    	        final_dummy = (long)dummy_1 | (long)(dummy_2<<32);

    	        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(receivePacket.getData()));
    	        //System.out.println(Arrays.toString(receivePacket.getData()));
    	        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(tv_sec_array));
    	        System.out.println(String.format("Time since epoch = %08X %d", tv_sec, tv_sec));

    	        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(tv_nsec_array));
    	        System.out.println(String.format("Time elapsed since last sec = %08X %d", tv_nsec, tv_nsec));

    	        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(dummy_array_1));
    	        System.out.println(String.format("Dummy_1 = %08X ", dummy_1));

    	        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(dummy_array_2));
    	        System.out.println(String.format("Dummy_2 = %08X", dummy_2));

    	        System.out.println(String.format("Dummy 1 = %08X", dummy_1));
    	        System.out.println(String.format("Dummy 2 = %08X", dummy_2));
    	        System.out.println(String.format("Finaly Dummy = %08X", final_dummy));

    	        InetAddress IPAddress = receivePacket.getAddress();
    	        int port = receivePacket.getPort();
    	        
    	        
//    	        String capitalizedSentence = sentence.toUpperCase();
//    	        capitalizedSentence = capitalizedSentence + '\n';
//    	        sendData = capitalizedSentence.getBytes();
//    	        DatagramPacket sendPacket =
//    	        	new DatagramPacket(sendData, sendData.length, IPAddress, port);
//    	        serverSocket.send(sendPacket);
    	        
    	        
    		} catch (SocketTimeoutException e1) {
    			System.out.println("nothing to do yet");
    			//e1.printStackTrace();
    			sentence = null;
    		}
	        return sentence;
        }
        public void disconnectPort()
        {
        	serverSocket = null;
        }
}

