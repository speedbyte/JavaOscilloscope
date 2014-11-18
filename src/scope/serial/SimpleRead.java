package scope.serial;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
//import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

//import OeffnenUndSenden.serialPortEventListener;



public class SimpleRead{

	/**
	 * @param args
	 */
//	public static void main(String[] args)
//	{
//		//Runnable runnable = new SimpleRead();
//		new Thread(runnable).start();
//		System.out.println("main finished");
//	}
	
	/**
	 * 
	 */

	CommPortIdentifier serialPortId;
	Enumeration enumComm;
	SerialPort serialPort;
	//OutputStream outputStream;
	InputStream inputStream;
	Boolean serialPortGeoeffnet = false;

	int baudrate = 9600;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	String portName = "COM24";
	byte[] data = null;
	int secondsRuntime = 60;

	public SimpleRead()
	{
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
			System.out.println("Serialport bereits ge�ffnet");
			return false;
		}
		System.out.println("�ffne Serialport");
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
		try {
			serialPort = (SerialPort) serialPortId.open("�ffnen und Senden", 500);
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
		} catch (IOException e) {
			System.out.println("Keinen Zugriff auf InputStream");
		}
		try {
			serialPort.addEventListener(new serialPortEventListener());
		} catch (TooManyListenersException e) {
			System.out.println("TooManyListenersException f�r Serialport");
		}
		serialPort.notifyOnDataAvailable(true);
		try {
			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		} catch(UnsupportedCommOperationException e) {
			System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
		}
		
		serialPortGeoeffnet = true;
		return true;
	}

	public void schliesseSerialPort()
	{
		if ( serialPortGeoeffnet == true) {
			System.out.println("Schlie�e Serialport");
			serialPort.close();
			serialPortGeoeffnet = false;
		} else {
			System.out.println("Serialport bereits geschlossen");
		}
	}
	
	void serialPortDatenVerfuegbar() {
		try {
			data = new byte[1150];
			int num;
			while(inputStream.available() > 0) {
				num = inputStream.read(data, 0, data.length);
				System.out.printf("%s", new String(data, 0, num));
			}
		} catch (IOException e) {
			System.out.println("Fehler beim Lesen empfangener Daten");
		}
	}

	public byte[] SerialMessageReader()
	{
		return data;
	}
	
	class serialPortEventListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			//System.out.println("serialPortEventlistener");
			switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				serialPortDatenVerfuegbar();
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
