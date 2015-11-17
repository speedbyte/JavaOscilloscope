package scope.udp;
import java.net.*;
import scope.mutex.*;

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
    		String sentence = null;    		
    		try {
        		serverSocket.receive(receivePacket);
    	        sentence = new String( receivePacket.getData());
    	        System.out.print(receivePacket.getData()[0]);
    	        InetAddress IPAddress = receivePacket.getAddress();
    	        int port = receivePacket.getPort();
    	        String capitalizedSentence = sentence.toUpperCase();
    	        capitalizedSentence = capitalizedSentence + '\n';
    	        sendData = capitalizedSentence.getBytes();
    	        DatagramPacket sendPacket =
    	        	new DatagramPacket(sendData, sendData.length, IPAddress, port);
    	        //serverSocket.send(sendPacket);
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

