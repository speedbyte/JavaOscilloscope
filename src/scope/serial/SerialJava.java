package scope.serial;


import gnu.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import scope.mutex.Mutex;



public class SerialJava{

	/**
	 * 
	 */

	CommPortIdentifier serialPortId;
	Enumeration enumComm;
	SerialPort serialPort;
	//OutputStream outputStream;
	InputStream inputStream;
	BufferedReader input;
	Boolean serialPortGeoeffnet = false;

	static double currenttime_second;
	double current;
	int baudrate = 38400;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	String portName = "COM1";

	byte[] data_array = null;
	long[] data_copied = null;

	Boolean all_data_read = true;
	
	public Mutex mutex = null;
	
	int pointer = 0;
			
	int secondsRuntime = 60;
	Boolean serialPortDatenVerfuegbar = false; 
	
	String serial_line = null;

	public SerialJava()
	{
		data_copied = new long[100];
		data_array = new byte[100];
		mutex = new Mutex();
		System.out.println("Konstruktor: EinfachSenden");
	}
	
    public void run()
    {
        Integer secondsRemaining = secondsRuntime;
        if (oeffneSerialPort(portName) != true)
        	return;
        
		while (secondsRemaining > 0) {
			System.out.println("Sekunden verbleiben: " + secondsRemaining.toString() );
			secondsRemaining--;
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) { }
		}
		schliesseSerialPort();
    	
    }
    
	public boolean oeffneSerialPort(String portName)
	{
		Boolean foundPort = false;
		if (serialPortGeoeffnet != false) {
			System.out.println("Serialport bereits geöffnet");
			return false;
		}
		System.out.println("Öffne Serialport");
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while(enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if (portName.contentEquals(serialPortId.getName())) {
				foundPort = true;
				break;
			}
		}
		if (foundPort != true) {
			System.out.println("Serialport nicht gefunden: " + portName);
			return false;
		}
		else
		{
			System.out.println("Serialport gefunden: " + portName);
		}
		try {
			serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
		} catch (PortInUseException e) {
			System.out.println("Port belegt");
		}
/*
		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			System.out.println("Keinen Zugriff auf OutputStream");
		}
*/
		try {
			inputStream = serialPort.getInputStream();
			input = new BufferedReader(new InputStreamReader(inputStream));
		} catch (IOException e) {
			System.out.println("Keinen Zugriff auf InputStream");
		}
		try {
			serialPort.addEventListener(new serialPortEventListener());
		} catch (TooManyListenersException e) {
			System.out.println("TooManyListenersException für Serialport");
		}
		serialPort.notifyOnDataAvailable(true);
		try {
			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		} catch(UnsupportedCommOperationException e) {
			System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
		}
		
		serialPortGeoeffnet = true;
		return serialPortGeoeffnet;
	}

	public void schliesseSerialPort()
	{
		if ( serialPortGeoeffnet == true) {
			System.out.println("Schließe Serialport");
			serialPort.close();
			serialPortGeoeffnet = false;
		} else {
			System.out.println("Serialport bereits geschlossen");
		}
	}
	
	public int getSerialData() {
		int read_bytes = 0;
		if ( serialPortDatenVerfuegbar == true ) 
		{
			read_bytes = pointer;
		}
		for ( int j = 0; j < pointer; j++)
		{
			//data_copied_again[j] = data_copied[j];
			//System.out.printf("new data arrived = %d at %d\n", data_array[pointer], pointer);
		}
		// Send all data received from the serial port
		all_data_read = true;
		serialPortDatenVerfuegbar = false;
		return read_bytes;
	}

	public String getSerialLine() {
		if ( serialPortDatenVerfuegbar == true ) 
		{
			all_data_read = true;
			serialPortDatenVerfuegbar = false;
			return serial_line;
		}
		return null;
	}

	
	public long[] SerialByteReader()
	{
		return data_copied;
	}
	

	public long[] SerialLineReader()
	{
		return data_copied;
	}
	
	class serialPortEventListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			switch (event.getEventType()) {
//			case SerialPortEvent.DATA_AVAILABLE:
//				mutex.lock();
//				if ( all_data_read == true )
//				{
//					pointer = 0;
//				}
//				all_data_read = false;
//				//current = (double) (new Date()).getTime();
//				//time_array[pointer] = current;
//				int num = 0;
//				try {
//					num = inputStream.read(data_array,0,data_array.length);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				for ( int j = 0; j < num; j++)
//				{
//					data_copied[pointer] = (long)data_array[j] & 0x000000FFL;
//					//System.out.printf("new data arrived = %d at %d\n", data_copied[pointer], pointer);
//					pointer++;
//				}
//				serialPortDatenVerfuegbar = true;				
//				mutex.unlock();
//				break;
			case SerialPortEvent.DATA_AVAILABLE:
				mutex.lock();
				//current = (double) (new Date()).getTime();
				//time_array[pointer] = current;
				try {
					if ( input.ready())
					{
						String inputLine=input.readLine();
						all_data_read = true;
						// Copy to another string
						serial_line = inputLine;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					all_data_read = false;
				}
				serialPortDatenVerfuegbar = true;				
				mutex.unlock();
				break;
			case SerialPortEvent.BI:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.FE:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			case SerialPortEvent.PE:
			case SerialPortEvent.RI:
			default:
			}
		}
	}	
}


/*
How mutex helps
new data coming in
recvd data = 97
number of bytes read 1
recvd message 97
all data read

new data coming in
recvd data = 98
recvd data = 99
recvd data = 36
number of bytes read 3
recvd message 98
recvd message 99
recvd message 36
all data read
*/