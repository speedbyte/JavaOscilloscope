package scope.gps;


import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.TooManyListenersException;


public class GPSReader extends Thread implements SerialPortEventListener {

    private GPSData currentPosition;
    private long portScanTime=2000;
    private long updateTime=200;
    private boolean found=false;
    private InputStream inputStream;
    private GPSDataEventListener dataListener;
    private SerialPort gpsPort = null;
    private boolean running=true;
    
    private static final String regExp = "((\\$GPGGA)|(\\$GPRMC)|(\\$GPGSA)|(\\$GPGLL)|(\\$GPGSV)|(\\$GPVTG)).*";

    public boolean findPort() {
        Enumeration enumer = CommPortIdentifier.getPortIdentifiers();
        while(enumer.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier)enumer.nextElement();
            if(port.getPortType() == CommPortIdentifier.PORT_SERIAL && !port.isCurrentlyOwned()) {
                SerialPort serialPort=null;
                try {
                    serialPort = (SerialPort)port.open("MY_PORT_NAME", 2000);
                    System.out.println("Trying port: " + port.getName());
                    
                    inputStream = null;
                    inputStream = serialPort.getInputStream();
                    if (inputStream == null) {
                        return false;
                    }

                    serialPort.addEventListener(this);
                    
                    serialPort.notifyOnDataAvailable(true);
                    
                    serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    
                    Thread.sleep(getPortScanTime());
                } catch (PortInUseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TooManyListenersException e) {
                    e.printStackTrace();
                } catch (UnsupportedCommOperationException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
 
                if(this.isFound()) {
                    System.out.println("Found Stream on com port " + serialPort.getName());
                    serialPort.removeEventListener();
                    setDataListener(new GPSDataEventListener(inputStream));
                    this.gpsPort = serialPort; 
                    try {
                        serialPort.addEventListener(getDataListener());
                        serialPort.notifyOnDataAvailable(true);
                    } catch (TooManyListenersException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    try {
                        if(inputStream != null) {
                          inputStream.close();    
                        }
                        if(serialPort != null) {
                          serialPort.close();  
                        } 
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("No Stream found!");
        return false;
    }
	
	public static void main(String[] args) {
		GPSReader reader = new GPSReader();
		reader.start();
	}

    @Override
    public void run() {
        try {
            while(isRunning()) {
                if(isFound() && getDataListener() != null && getDataListener().isConnection()) {
                    GPSData data = getDataListener().getLocationData();
                    setCurrentPosition(data);
                } else {
                    if(getDataListener() != null) {
                        setDataListener(null);
                        gpsPort.close();
                        gpsPort.removeEventListener();
                        gpsPort = null;
                        setFound(false);
                    }
                    findPort();
                }
                Thread.sleep(getUpdateTime());
            }
            
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable t ) {
            t.printStackTrace();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        //System.out.println("Event:" + event.getEventType());
        if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE && !found) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream()));
            String line=null;
            try {
                if (reader.ready()) {
                    line = reader.readLine();
                    if(line != null && line.matches(regExp)) {
                        this.setFound(true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	public long getUpdateTime() {
		return updateTime;
	}

    private void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    private void setFound(boolean found) {
        this.found = found;
    }

    public boolean isFound() {
        return found;
    }

    synchronized private void setCurrentPosition(GPSData currentPosition) {
        this.currentPosition = currentPosition;
    }

    synchronized public GPSData getCurrentPosition() {
        return currentPosition;
    }

    public void setPortScanTime(long portScanTime) {
        this.portScanTime = portScanTime;
    }

    public long getPortScanTime() {
        return portScanTime;
    }

    private void setDataListener(GPSDataEventListener dataListener) {
        this.dataListener = dataListener;
    }

    private GPSDataEventListener getDataListener() {
        return dataListener;
    }

    public void setRunning(boolean run) {
        this.running = run;
    }

    public boolean isRunning() {
        return running;
    }
    
    public boolean isConnection() {
        return isFound() && getCurrentPosition() != null && getCurrentPosition().isConnection();
    }


}