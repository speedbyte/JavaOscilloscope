package scope.graphic;

// Imports
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
//import java.nio.charset.Charset;
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
import scope.gui.MMInterface;
import scope.gui.ViewInterface;
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
	public static String display_string = null;

	private static boolean activateCanLogging = false;
	private static boolean activateZigbeeLogging = false;
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
			if (activateCanLogging == true) {
				if (flagStartReading) {}
			} else if (activateZigbeeLogging == true) {
				if (flagStartReading) {
					if (!timeStartFlag) {
						start = (double) (new Date()).getTime();
						timeStartFlag = true;
					}
					current = (double) (new Date()).getTime();
					currenttime_second = (current - start) / 1000;

					int num = 0;
					int length = 0;
					oSerialJava.mutex.lock();
					if (single_byte_oscilloscope == true) {
						num = oSerialJava.getSerialData();
						length = num;
					} else {
						display_string = oSerialJava.getSerialLine();
					}
					// this code is to develop the oscilloscope for single bytes
					if (num != 0) { 
						System.out.printf("number of bytes read %d\n", num);
						while (num != 0) {
							// the protocol will be executed from this point.
							// store all the variables till a $ is received.
							data_serialport[index_dollar] = oSerialJava.SerialByteReader()[length - num];
							num--;
							// System.out.printf("recvd message %d\n",
							// data_serialport[index_dollar]);
							if (data_serialport[index_dollar] == '\n') {
								System.out.println(Arrays.toString(data_serialport));
								// System.out.printf("Trigger oscilloscope, $ at %d\n",
								// (index_dollar+1));
								df.applyPattern(pattern);
								// writerLog.append(" " + v1);
								// value1 = Integer.parseInt(v1, 16);
								// value1 = (int) (Math.random()*256);
								value1 = data_serialport[0];
								value2 = data_serialport[1];
								value3 = data_serialport[2];
								value4 = data_serialport[3];
								value5 = data_serialport[4];
								value6 = data_serialport[5];
								value7 = data_serialport[6];
								value8 = data_serialport[7];
								// TODO
								double[] dataArray = new double[9];
								dataArray[0] = currenttime_second;
								dataArray[1] = value1;
								dataArray[2] = value2;
								dataArray[3] = value3;
								dataArray[4] = value4;
								dataArray[5] = value5;
								dataArray[6] = value6;
								dataArray[7] = value7;
								dataArray[8] = value8;
								mm.pushDataArray(dataArray);
								index_dollar = 0;
								Arrays.fill(data_serialport, (byte) 0);
							} else {
								index_dollar++;
							}
						}
					} else if (display_string != null) {
						System.out.println(display_string);
						String[] parts = new String[10];
						parts = display_string.split("#");
						for (int i = 0; i < parts.length; i++) {
							// System.out.printf("counter = %d at %s", i,
							// parts[i]);
							// abraca#dsfsd#sdfdsfdsfsd#Run#MF:52;101;48#further
							try {
								if (i == 4 && parts.length == 6) {
									String[] data_magnet = new String[6];
									data_magnet = parts[i].split(";");

									value1 = Integer.parseInt((data_magnet[0].split(":"))[1]);
									value2 = Integer.parseInt(data_magnet[1]);
									value3 = Integer.parseInt(data_magnet[2]);
									value4 = Integer.parseInt(data_magnet[3]);
									value5 = Integer.parseInt(data_magnet[4]);

									// TODO
									double[] dataArray = new double[9];
									dataArray[0] = currenttime_second;
									dataArray[1] = value1;
									dataArray[2] = value2;
									dataArray[3] = value3;
									dataArray[4] = value4;
									dataArray[5] = value5;

									mm.pushDataArray(dataArray);
									
								} 
							} catch (Exception e) {
							}
						}
					} else {
//						serie0.add(currenttime_second, null);

					}
					oSerialJava.mutex.unlock();
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

				if (activateCanLogging == true) {
					/* BT CAN */
					oVciJava = new VciJava();
					oBalObject = oVciJava.InitCanBlue();
					oCanMsgReader = oVciJava.StartCan(oBalObject, (short) 0);
					if (oCanMsgReader != null) {
						flagStartReading = true;
						if (!logFlag) {
							// Create the log file if not already created
							Date date = new Date();
							Log = new File("output/Log_"
									+ formatter.format(date) + ".asc");
							logFlag = true;
							headerFlag = false;
							// Add data to files
							writerLog = new PrintWriter(new FileWriter(Log,
									true));
							writerLog.append("date "
									+ formatterHeader.format(date)
									+ "\r\nbase hex  timestamps absolute"
									+ "\r\ninternal events logged");
							// writerLog.close();
							headerFlag = true;
						}
					} else {
						flagStartReading = false;
					}
				}
				/* Serial */
				else if (activateZigbeeLogging == true) {
					oSerialJava = new SerialJava();
					flagStartReading = oSerialJava.oeffneSerialPort("COM6");
				}
				return null;
			}
		};
		worker.execute();
	}


	@Override
	public void startReading() {
		if (activateCanLogging || activateZigbeeLogging) {
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
		if (activateCanLogging == true) {
			oVciJava.StopCan(oBalObject, oCanMsgReader);
			oVciJava.ResetDeviceIndex();
			activateCanLogging = false;
			writerLog.close();
		}
		if (activateZigbeeLogging == true) {
			oSerialJava.schliesseSerialPort();
//			activateZigbeeLogging = false;
		}
		flagStartReading = false;
		flagLogFile = true;
		flagStatus = true;
		timeStartFlag = false;
	}

	@Override
	public void setReadBluetooth() {
		DataReader.activateZigbeeLogging = false;
		DataReader.activateImportFile = false;
		DataReader.activateCanLogging = true;
	}

	@Override
	public void setReadZigbee() {
		DataReader.activateCanLogging = false;
		DataReader.activateImportFile = false;
		DataReader.activateZigbeeLogging = true;
	}

	@Override
	public void setImportFile(File selectedLogFile) {
		selLogFile = selectedLogFile;
		DataReader.activateZigbeeLogging = false;
		DataReader.activateCanLogging = false;
		DataReader.activateImportFile = true;
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