package scope.gui;

// Imports
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.swing.SwingWorker;



import de.ixxat.vci3.bal.IBalObject;
import de.ixxat.vci3.bal.can.CanMessage;
import de.ixxat.vci3.bal.can.ICanMessageReader;
import scope.udp.UdpJava;
import scope.vci.VciJava;
import scope.serial.SerialJava;

/* SerialReader Class */
public class DataReader implements DataReaderInterface, Runnable {
	// Variables
	static Thread thread1;
	public static VciJava oVciJava = null;
	public static IBalObject oBalObject = null;
	public static ICanMessageReader oCanMsgReader = null;
	public static Boolean flagStartReading = false;
	public static CanMessage oCanMsg = null;
	static String canLine = null;
	public static Boolean single_byte_oscilloscope = false;
	public static SerialJava oSerialJava = null;
	public static UdpJava oUdpJava = null;
	public static String display_string = null;
	public static StringBuffer helikopterString = null;

	private static boolean activateCanLogging = false;
	private static boolean activateZigbeeLogging = false;
	private static boolean activateUdpLogging = false;
	private static boolean activateImportFile = false;


	//final static Charset ENCODING = StandardCharsets.UTF_8;
	String lineToFile = null;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	SimpleDateFormat formatterHeader = new SimpleDateFormat(
			"EEE MMM d hh:mm:ss a yyyy", Locale.ENGLISH);
	PrintWriter writerLog = null;
	File selLogFile;
	File Log;

	
	int index_dollar = 0;
	long[] data_serialport = new long[200];

	static boolean flagStatus = false;
	static boolean flagLogFile = false;
	static boolean logFlag = false;
	boolean headerFlag = false;
	public boolean dataFlag;
	boolean timeStartFlag = false;

	int a;
	int x;
	int id;
	String idx0 = null;
	String CanStringSplitted[];
	String v1, v2, v3, v4, v5, v6, v7, v8;
	long value1, value2, value3, value4, value5, value6, value7, value8;

	static double currenttime_second;
	double timeStamp = 0;
	static double start;
	double current = 0;

	Locale mylocale = Locale.ENGLISH;
	String pattern = "0.000000";
	NumberFormat nf = NumberFormat.getNumberInstance(mylocale);
	DecimalFormat df = (DecimalFormat) nf;


	/* variables for a test */
	static MMInterface mm;
	static ViewInterface view;


	// MainClass constructor, the chart is created
	public DataReader() {
		thread1 = new Thread(this);
		thread1.start();
	}

	// Run method
	public void run() {
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			// Log file Log+date+.asc is created
			if ( activateUdpLogging == true ) {
				if (flagStartReading) {
					if (!timeStartFlag) {
						start = (double) (new Date()).getTime();
						timeStartFlag = true;
					}
					current = (double) (new Date()).getTime();
					currenttime_second = (current - start) / 1000;

					int num = 0;
					int length = 0;
					double[] combinedDataDouble = new double[19];
					oUdpJava.udpMutex.lock();
					try {
						combinedDataDouble = oUdpJava.receiveNonBlocking();
		    		} catch (Exception e1) {
		    			e1.printStackTrace();
		    		}
					if (combinedDataDouble[0] != 0) {
						//System.out.println(helikopterString);
						if (combinedDataDouble[0] != 0 ) {
							try
							{
								double[] dataArray = new double[20];
								dataArray[0] = currenttime_second;
								System.arraycopy(combinedDataDouble, 0, dataArray, 1, combinedDataDouble.length);
								mm.pushDataArray(dataArray);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
					} 
					else {
//						serie0.add(currenttime_second, null);
					}
					oUdpJava.udpMutex.unlock();
				}
			}
		}
	}

	// RunVci method
	public void runVci(String[] args) {
		final SwingWorker<?, ?> worker = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				logFlag = false;
				flagStatus = false;
				if (activateUdpLogging == true) {
					oUdpJava = new UdpJava();
					flagStartReading = oUdpJava.startServer();
				}
				return null;
			}
		};
		worker.execute();
	}

	@Override
	public void startReading() {
		if (activateUdpLogging) {
			runVci(null);
		}
		if (activateImportFile) {
			flagLogFile = false;
			// runLog(); Please see in scratch.
		}

		timeStartFlag = false;
	}

	@Override
	public void stopReading() {
		if (activateUdpLogging == true) {
			oUdpJava.disconnectPort();
		}
		flagStartReading = false;
		flagLogFile = true;
		flagStatus = true;
		timeStartFlag = false;
	}

	@Override
	public void setReadUdp() {
		DataReader.activateImportFile = false;
		DataReader.activateUdpLogging = true;
	}

	@Override
	public void setImportFile(File selectedLogFile) {
		selLogFile = selectedLogFile;
		DataReader.activateImportFile = true;
		DataReader.activateUdpLogging = false;
	}

	@Override
	public void terminateReader() {
		DataReader.thread1.interrupt();
	}

	@Override
	public void setView(ViewInterface view) {
		DataReader.view = view;		
	}

	@Override
	public void setModel(MMInterface model) {
		DataReader.mm = model;		
	}
}