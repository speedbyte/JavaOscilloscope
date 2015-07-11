package scope.graphic;

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
import scope.gui.MMInterface;
import scope.gui.ViewInterface;
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
			if (activateCanLogging == true) {
				if (flagStartReading) {
					if (!timeStartFlag) {
						start = (double) (new Date()).getTime();
						timeStartFlag = true;
					}
					current = (double) (new Date()).getTime();
					currenttime_second = (current - start) / 1000;
					oCanMsg = oVciJava.CanMessageReader(oCanMsgReader);
					if (oCanMsg != null) {
						canLine = oCanMsg.toString();
						System.out.printf("recvd message %s\n", canLine);
						// recvd message Timestamp: 13756081 Flags: ID:
						// 0x00000100 Data: 0xFF 0xEE 0xDD 0xCC 0xBB 0xAA 0x99
						// 0x88
						lineToFile = ("" + canLine);

						if (lineToFile != null) {
							int byteCtrl = 0;
							String[] CanStringSplitted = lineToFile
									.split("\\s+");
							lineToFile = null;
							if (CanStringSplitted[4].startsWith("I")) {
								byteCtrl = 5;
							} else {
								byteCtrl = 4;
							}
							String x_id = CanStringSplitted[byteCtrl]
									.substring(7, 10);
							// Read CAN ID from lineToFile as well as from
							// JTextField
							x = Integer.parseInt(x_id, 16);
							
							//TODO
							idx0 = view.getTextID();

							if (idx0.isEmpty()) {
								idx0 = "0";
								continue;
							} else {
							}

							try {
								id = Integer.parseInt(idx0, 16);
							} catch (Exception e) {
							}
							
							/* data array which will be pushed to ModelMediator */
							double[] dataArray = new double[9];
							dataArray[0] = currenttime_second;


							df.applyPattern(pattern);
							int dataLength = CanStringSplitted.length
									- byteCtrl - 2;

							if (CanStringSplitted.length >= byteCtrl + 2) {
								writerLog.append("\r\n   "
										+ df.format(currenttime_second)
										+ " 1  " + x_id
										+ "             Rx   d " + dataLength);
							}

							if (CanStringSplitted.length >= byteCtrl + 3) {
								v1 = CanStringSplitted[byteCtrl + 2].substring(
										2, 4);
								writerLog.append(" " + v1);
								if (x == id) {
									// value1 = Integer.parseInt(v1, 16);
									value1 = (int) (Math.random() * 256) * 1000;
//									serie1.add(currenttime_second, value1);
									dataArray[1] = value1;
								}
							}

							if (CanStringSplitted.length >= byteCtrl + 4) {
								v2 = CanStringSplitted[byteCtrl + 3].substring(
										2, 4);
								writerLog.append(" " + v2);
								if (x == id) {
									// value2 = Integer.parseInt(v2, 16);
									value2 = (int) (Math.random() * 256);
//									serie2.add(currenttime_second, value2);
									dataArray[2] = value2;
								}
							}

							if (CanStringSplitted.length >= byteCtrl + 5) {
								v3 = CanStringSplitted[byteCtrl + 4].substring(
										2, 4);
								writerLog.append(" " + v3);
								if (x == id) {
									// value3 = Integer.parseInt(v3, 16);
									value3 = (int) (Math.random() * 256);
//									serie3.add(currenttime_second, value3);
									dataArray[3] = value3;
								}
							}

							if (CanStringSplitted.length >= byteCtrl + 6) {
								v4 = CanStringSplitted[byteCtrl + 5].substring(
										2, 4);
								writerLog.append(" " + v4);
								if (x == id) {
									// value4 = Integer.parseInt(v4, 16);
									value4 = (int) (Math.random() * 256);
//									serie4.add(currenttime_second, value4);
									dataArray[4] = value4;
								}
							}

							if (CanStringSplitted.length >= byteCtrl + 7) {
								v5 = CanStringSplitted[byteCtrl + 6].substring(
										2, 4);
								writerLog.append(" " + v5);
								if (x == id) {
									// value5 = Integer.parseInt(v5, 16);
									value5 = (int) (Math.random() * 256);
//									serie5.add(currenttime_second, value5);
									dataArray[5] = value5;
								}
							}

							if (CanStringSplitted.length >= byteCtrl + 8) {
								v6 = CanStringSplitted[byteCtrl + 7].substring(
										2, 4);
								writerLog.append(" " + v6);
								if (x == id) {
									// value6 = Integer.parseInt(v6, 16);
									value6 = (int) (Math.random() * 256);
//									serie6.add(currenttime_second, value6);
									dataArray[6] = value6;
								}
							}

							if (CanStringSplitted.length >= byteCtrl + 9) {
								v7 = CanStringSplitted[byteCtrl + 8].substring(
										2, 4);
								writerLog.append(" " + v7);
								if (x == id) {
									// value7 = Integer.parseInt(v7, 16);
									value7 = (int) (Math.random() * 256);
//									serie7.add(currenttime_second, value7);
									dataArray[7] = value7;
								}
							}

							if (CanStringSplitted.length >= byteCtrl + 10) {
								v8 = CanStringSplitted[byteCtrl + 9].substring(
										2, 4);
								writerLog.append(" " + v8);
								if (x == id) {
									// value8 = Integer.parseInt(v8, 16);
									value8 = (int) (Math.random() * 256);
//									serie8.add(currenttime_second, value8);
									dataArray[8] = value8;
								}
							}
							
							/* dataArray pushed to ModelMediator */
							mm.pushDataArray(dataArray );
						}
//					} else {
//						serie0.add(currenttime_second, null);
					}
				}
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
					oSerialJava.serialMutex.lock();
					if (single_byte_oscilloscope == true) {
						num = oSerialJava.getSerialData();
						length = num;
					} else {
						display_string = oSerialJava.getSerialLine();
					}
					if (num != 0) {
						System.out.printf("number of bytes read %d\n", num);
						while (num != 0) {
							// the protocol will be executed from this point.
							// store all the variables till a $ is received.
							data_serialport[index_dollar] = oSerialJava
									.SerialByteReader()[length - num];
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
								
//								serie1.add(currenttime_second, value1);
//								serie2.add(currenttime_second, value2);
//								serie3.add(currenttime_second, value3);
//								serie4.add(currenttime_second, value4);
//								serie5.add(currenttime_second, value5);
//								serie6.add(currenttime_second, value6);
//								serie7.add(currenttime_second, value7);
//								serie8.add(currenttime_second, value8);

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
									// System.out.println((data_magnet[0].split(":"))[1]);
									// System.out.println(data_magnet[1]);
									// System.out.println(data_magnet[2]);

									value1 = Integer.parseInt((data_magnet[0]
											.split(":"))[1]);
									value2 = Integer.parseInt(data_magnet[1]);
									value3 = Integer.parseInt(data_magnet[2]);
									value4 = Integer.parseInt(data_magnet[3]);
									value5 = Integer.parseInt(data_magnet[4]);
									
//									serie1.add(currenttime_second, value1);
//									serie2.add(currenttime_second, value2);
//									serie3.add(currenttime_second, value3);

									// TODO
									double[] dataArray = new double[9];
									dataArray[0] = currenttime_second;
									dataArray[1] = value1;
									dataArray[2] = value2;
									dataArray[3] = value3;
									dataArray[4] = value4;
									dataArray[5] = value5;

									mm.pushDataArray(dataArray);
									
								} else if (i == 5 && parts.length == 7) {
									String[] data_magnet = new String[6];
									data_magnet = parts[i].split(";");
									// System.out.println((data_magnet[0].split(":"))[1]);
									// System.out.println(data_magnet[1]);
									// System.out.println(data_magnet[2]);
									
									value1 = Integer.parseInt((data_magnet[0]
											.split(":"))[1]);
									value2 = Integer.parseInt(data_magnet[1]);
									value3 = Integer.parseInt(data_magnet[2]);
									
//									serie1.add(currenttime_second, value1);
//									serie2.add(currenttime_second, value2);
//									serie3.add(currenttime_second, value3);

									// TODO
									double[] dataArray = new double[9];
									dataArray[0] = currenttime_second;
									dataArray[1] = value1;
									dataArray[2] = value2;
									dataArray[3] = value3;

									mm.pushDataArray(dataArray);
								}
							} catch (Exception e) {

							}
						}
					} else {
//						serie0.add(currenttime_second, null);

					}
					oSerialJava.serialMutex.unlock();
				}
			}
			else if ( activateUdpLogging == true ) {

				if (flagStartReading) {
					if (!timeStartFlag) {
						start = (double) (new Date()).getTime();
						timeStartFlag = true;
					}
					current = (double) (new Date()).getTime();
					currenttime_second = (current - start) / 1000;

					int num = 0;
					int length = 0;
					oUdpJava.udpMutex.lock();
					try {
						display_string = oUdpJava.receiveNonBlocking();
		    		} catch (Exception e1) {
		    			//System.out.println("following exception");
		    			e1.printStackTrace();
		    		}
					if (display_string != null) {
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
									// System.out.println((data_magnet[0].split(":"))[1]);
									// System.out.println(data_magnet[1]);
									// System.out.println(data_magnet[2]);

									value1 = Integer.parseInt((data_magnet[0]
											.split(":"))[1]);
									value2 = Integer.parseInt(data_magnet[1]);
									value3 = Integer.parseInt(data_magnet[2]);
									//value4 = Integer.parseInt(data_magnet[3]);
									//value5 = Integer.parseInt(data_magnet[4]);
									
//									serie1.add(currenttime_second, value1);
//									serie2.add(currenttime_second, value2);
//									serie3.add(currenttime_second, value3);

									// TODO
									double[] dataArray = new double[9];
									dataArray[0] = currenttime_second;
									dataArray[1] = value1;
									dataArray[2] = value2;
									dataArray[3] = value3;
									//dataArray[4] = value4;
									//dataArray[5] = value5;
									System.out.println(value3);
									mm.pushDataArray(dataArray);
									
								}
							} catch (Exception e) {
								e.printStackTrace();

							}
						}
					} else {
//						serie0.add(currenttime_second, null);

					}
					oUdpJava.udpMutex.unlock();
				}
							
			}
		}
	}

//	public static void ReadFrameNull() {
//
//		serie1.add(currenttime_second, null);
//		serie2.add(currenttime_second, null);
//		serie3.add(currenttime_second, null);
//		serie4.add(currenttime_second, null);
//		serie5.add(currenttime_second, null);
//		serie6.add(currenttime_second, null);
//		serie7.add(currenttime_second, null);
//		serie8.add(currenttime_second, null);
//
//	Test
//	}

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
				else if (activateUdpLogging == true) {
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
		if (activateCanLogging || activateZigbeeLogging || activateUdpLogging) {
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
		if (activateUdpLogging == true) {
			oUdpJava.disconnectPort();
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
		DataReader.activateUdpLogging = false;
		DataReader.activateImportFile = false;
		DataReader.activateCanLogging = true;
	}

	@Override
	public void setReadZigbee() {
		DataReader.activateCanLogging = false;
		DataReader.activateImportFile = false;
		DataReader.activateZigbeeLogging = true;
		DataReader.activateUdpLogging = false;
	}

	@Override
	public void setReadUdp() {
		DataReader.activateCanLogging = false;
		DataReader.activateImportFile = false;
		DataReader.activateUdpLogging = true;
		DataReader.activateZigbeeLogging = false;
	}

	@Override
	public void setImportFile(File selectedLogFile) {
		selLogFile = selectedLogFile;
		DataReader.activateZigbeeLogging = false;
		DataReader.activateCanLogging = false;
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