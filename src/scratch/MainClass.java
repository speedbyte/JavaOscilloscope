package scratch;

// Imports
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import de.ixxat.vci3.bal.IBalObject;
import de.ixxat.vci3.bal.can.CanMessage;
import de.ixxat.vci3.bal.can.ICanMessageReader;
import scope.data.ImportButton;
import scope.udp.UdpJava;
import scope.vci.VciJava;
import scope.serial.SerialJava;

//Main Class
@SuppressWarnings("serial")
public class MainClass extends JFrame implements Runnable, ActionListener {

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
	
	public static UdpJava oUdpJava = null;
	
	private static boolean activateCanLogging = false;
	private static boolean activateZigbeeLogging = false;
	private static boolean activateUdpLogging = false;

	static XYSeries serie0 = new XYSeries("");
	static XYSeries serie1 = new XYSeries("Byte 1");
	static XYSeries serie2 = new XYSeries("Byte 2");
	static XYSeries serie3 = new XYSeries("Byte 3");
	static XYSeries serie4 = new XYSeries("Byte 4");
	static XYSeries serie5 = new XYSeries("Byte 5");
	static XYSeries serie6 = new XYSeries("Byte 6");
	static XYSeries serie7 = new XYSeries("Byte 7");
	static XYSeries serie8 = new XYSeries("Byte 8");

	static XYSeries[] serie_array = new XYSeries[9];
	
	static XYDataset data = null;
	static XYDataset data0 = new XYSeriesCollection(serie0);
	static XYDataset data1 = new XYSeriesCollection(serie1);
	static XYDataset data2 = new XYSeriesCollection(serie2);
	static XYDataset data3 = new XYSeriesCollection(serie3);
	static XYDataset data4 = new XYSeriesCollection(serie4);
	static XYDataset data5 = new XYSeriesCollection(serie5);
	static XYDataset data6 = new XYSeriesCollection(serie6);
	static XYDataset data7 = new XYSeriesCollection(serie7);
	static XYDataset data8 = new XYSeriesCollection(serie8);

	static NumberAxis axis;
	static NumberAxis axis1 = new NumberAxis("Byte 1");
	static NumberAxis axis2 = new NumberAxis("Byte 2");
	static NumberAxis axis3 = new NumberAxis("Byte 3");
	static NumberAxis axis4 = new NumberAxis("Byte 4");
	static NumberAxis axis5 = new NumberAxis("Byte 5");
	static NumberAxis axis6 = new NumberAxis("Byte 6");
	static NumberAxis axis7 = new NumberAxis("Byte 7");
	static NumberAxis axis8 = new NumberAxis("Byte 8");
	static NumberAxis axis9 = new NumberAxis("");

	static ValueAxis valueaxis = null;
	//final static Charset ENCODING = StandardCharsets.UTF_8;
	String lineToFile = null;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	SimpleDateFormat formatterHeader = new SimpleDateFormat(
			"EEE MMM d hh:mm:ss a yyyy", Locale.ENGLISH);
	PrintWriter writerLog = null;
	File selLogFile;
	File Log;

	static XYPlot xyplot;
	static JFreeChart jfreechart = null;
	static XYStepRenderer renderer = null;
	final static XYStepRenderer renderer1 = null;
	final static XYStepRenderer renderer2 = null;
	final static XYStepRenderer renderer3 = null;
	final static XYStepRenderer renderer4 = null;
	final static XYStepRenderer renderer5 = null;
	final static XYStepRenderer renderer6 = null;
	final static XYStepRenderer renderer7 = null;
	final static XYStepRenderer renderer8 = null;

	double interval = 60;
	int frequency = 1;
	double time = 1;
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

	final static JButton btnStart = new JButton("START");
	final JPanel panel = new JPanel();
	final JButton btnStop = new JButton("STOP");
	JSpinner spinner_2 = new JSpinner();
	private JTextField id_txt;
	JRadioButton rdbtnBluetooth;
	JRadioButton rdbtnZigbee;
	JRadioButton rdbtnUdp;
	JRadioButton rdbtnImportFile;

	final JCheckBox[] chckbx = new JCheckBox[10];
	{
		chckbx[1] = new JCheckBox("Speed");
		chckbx[2] = new JCheckBox("Height");
		chckbx[3] = new JCheckBox("Acceleration");
		chckbx[4] = new JCheckBox("");
		chckbx[5] = new JCheckBox("");
		chckbx[6] = new JCheckBox("");
		chckbx[7] = new JCheckBox("");
		chckbx[8] = new JCheckBox("");
		chckbx[9] = new JCheckBox("Everything");
	}
	Color panelColor = new Color(50, 50, 50);

	// Main method
	public static void main(String[] args) {
		MainClass XYSeriesChart = new MainClass();
		RefineryUtilities.centerFrameOnScreen(XYSeriesChart);
		XYSeriesChart.setVisible(true);
		thread1.start();
	}

	// MainClass constructor, the chart is created
	public MainClass() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				closeMenu();
			}
		});
		setMinimumSize(new Dimension(900, 600));
		setPreferredSize(new Dimension(900, 600));
		thread1 = new Thread(this);
		jfreechart = ChartFactory.createXYStepChart("", "", "", data0,
				PlotOrientation.VERTICAL, false, false, false);
		jfreechart.setBackgroundPaint(panelColor);
		jfreechart.getXYPlot().setDomainAxis(new NumberAxis());

		xyplot = jfreechart.getXYPlot();
		xyplot.setBackgroundPaint(new Color(0, 25, 0));
		xyplot.setDomainGridlinePaint(new Color(0, 50, 0));
		xyplot.setDomainGridlineStroke(new BasicStroke(1f));
		xyplot.setRangeGridlinePaint(new Color(0, 50, 0));
		xyplot.setRangeGridlineStroke(new BasicStroke(1f));
		xyplot.setDomainPannable(true);
		xyplot.setRangePannable(true);

		valueaxis = xyplot.getDomainAxis();
		valueaxis.setLabelPaint(Color.black);
		valueaxis.setTickLabelPaint(Color.LIGHT_GRAY);
		valueaxis.setFixedAutoRange(interval);

		// Axis0 is created as main axis of JFreeChart but is not used
		final NumberAxis axis0 = (NumberAxis) xyplot.getRangeAxis();
		jfreechart.getXYPlot().getRenderer(0).setBaseSeriesVisible(true);
		axis0.setLabelPaint(jfreechart.getXYPlot().getRenderer(0)
				.getItemPaint(0, 0));
		axis0.setTickLabelPaint(jfreechart.getXYPlot().getRenderer(0)
				.getItemPaint(0, 0));
		axis0.setVisible(false);
		axis0.setRange(0, 10);

		// AxisConfiguration method is called in order to configure used axis
		axisConfiguration();

		ChartPanel chartpanel = new PanningChartPanel(jfreechart);
		getContentPane().add(chartpanel);
		chartpanel.setDomainZoomable(true);
		chartpanel.setRangeZoomable(true);

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setTitle("Oscilloscope");

		// Jpanel and Swing elements are configured
		JPanel jpanel = new JPanel();
		jpanel.setBackground(panelColor);
		jpanel.setMinimumSize(new Dimension(800, 190));
		jpanel.setPreferredSize(new Dimension(800, 190));
		getContentPane().add(jpanel, "South");
		jpanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_1 = new JPanel();
		panel_1.setMinimumSize(new Dimension(800, 190));
		panel_1.setPreferredSize(new Dimension(800, 190));
		jpanel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		panel.setPreferredSize(new Dimension(800, 25));
		panel.setMinimumSize(new Dimension(800, 25));
		panel_1.add(panel);
		panel.setBackground(panelColor);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		int x = 1;
		while (x <= 9) {
			chckbx[x].setForeground(Color.LIGHT_GRAY);
			chckbx[x].setBackground(panelColor);
			panel.add(chckbx[x]);
			x++;
		}

		ImportButton btnImportValues = new ImportButton("Upload File");
		panel.add(btnImportValues.button);
		btnImportValues.button.setFont(new Font("Tahoma", Font.PLAIN, 12));
		JPanel buttonPanel = new JPanel();
		panel_1.add(buttonPanel);
		buttonPanel.setBackground(panelColor);
		buttonPanel.setPreferredSize(new Dimension(800, 140));
		buttonPanel.setLayout(null);

		btnStart.setBackground(Color.GREEN);
		btnStart.setBounds(687, 79, 92, 20);
		btnStart.setForeground(Color.BLACK);
		btnStart.setVisible(true);
		btnStart.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				clear();
				if (rdbtnBluetooth.isSelected()) {
					runVci(null);
					activateCanLogging = true;
				}
				if (rdbtnZigbee.isSelected()) {
					runVci(null);
					activateZigbeeLogging = true;
				}
				if (rdbtnUdp.isSelected()) {
					runVci(null);
					activateUdpLogging = true;
				}
				if (rdbtnImportFile.isSelected()) {
					flagLogFile = false;
					//runLog(); Please see in scratch.
				}

				timeStartFlag = false;
				btnStop.setEnabled(true);
				btnStop.setVisible(true);
				btnStart.setEnabled(false);
				btnStart.setVisible(false);
				rdbtnBluetooth.setEnabled(false);
				rdbtnZigbee.setEnabled(false);
				rdbtnUdp.setEnabled(false);
				rdbtnImportFile.setEnabled(false);
			}

		});
		buttonPanel.add(btnStart);

		btnStop.setEnabled(false);
		btnStop.setForeground(Color.BLACK);
		btnStop.setBackground(Color.RED);
		btnStop.setBounds(687, 79, 92, 20);
		btnStop.setVisible(false);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopMenu();
			}
		});
		buttonPanel.add(btnStop);

		JButton btn1P = new JButton("MORE");
		btn1P.setHorizontalAlignment(SwingConstants.LEFT);
		btn1P.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn1P.setIcon(new ImageIcon(MainClass.class
				.getResource("/resourses_images/Wave_1.png")));
		btn1P.setBounds(30, 70, 95, 25);
		btn1P.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				axis.resizeRange(0.8);
			}
		});
		buttonPanel.add(btn1P);

		JButton btn1M = new JButton("LESS");
		btn1M.setHorizontalAlignment(SwingConstants.LEFT);
		btn1M.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn1M.setIcon(new ImageIcon(MainClass.class
				.getResource("/resourses_images/Wave_2.png")));
		btn1M.setBounds(30, 105, 95, 25);
		btn1M.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				axis.resizeRange(1.2);
			}
		});
		buttonPanel.add(btn1M);

		JButton btn2P = new JButton("UP");
		btn2P.setHorizontalAlignment(SwingConstants.LEFT);
		btn2P.setIcon(new ImageIcon(MainClass.class
				.getResource("/resourses_images/ArrowUp.png")));
		btn2P.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn2P.setActionCommand("");
		btn2P.setBounds(135, 70, 84, 25);
		btn2P.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				axis.pan(-0.1);
			}
		});
		buttonPanel.add(btn2P);

		JButton btn2M = new JButton("DOWN");
		btn2M.setHorizontalAlignment(SwingConstants.LEFT);
		btn2M.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn2M.setIcon(new ImageIcon(MainClass.class
				.getResource("/resourses_images/ArrowDown.png")));
		btn2M.setBounds(135, 105, 84, 25);
		btn2M.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				axis.pan(0.1);
			}
		});
		buttonPanel.add(btn2M);

		final JButton pause = new JButton("Pause");
		pause.setBounds(341, 79, 92, 20);
		buttonPanel.add(pause);

		final JButton continueButton = new JButton("Continue");
		continueButton.setBounds(341, 110, 92, 20);
		buttonPanel.add(continueButton);

		JButton auto = new JButton("Autoset");
		auto.setBounds(341, 48, 92, 20);
		auto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		auto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (axis == axis9) {
					axis1.setAutoRange(true);
					axis2.setAutoRange(true);
					axis3.setAutoRange(true);
					axis4.setAutoRange(true);
					axis5.setAutoRange(true);
					axis6.setAutoRange(true);
					axis7.setAutoRange(true);
					axis8.setAutoRange(true);
					valueaxis.setAutoRange(true);
					axis1.setAutoRange(false);
					axis2.setAutoRange(false);
					axis3.setAutoRange(false);
					axis4.setAutoRange(false);
					axis5.setAutoRange(false);
					axis6.setAutoRange(false);
					axis7.setAutoRange(false);
					axis8.setAutoRange(false);
				} else {
					axis.setAutoRange(true);
					axis.setAutoRange(false);
				}
			}
		});
		buttonPanel.add(auto);
		JLabel lblIndividualControls = new JLabel("Control");
		lblIndividualControls.setForeground(new Color(153, 204, 204));
		lblIndividualControls.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblIndividualControls.setBounds(30, 10, 84, 25);
		buttonPanel.add(lblIndividualControls);

		JLabel lblAmplitude = new JLabel("Y Gain");
		lblAmplitude.setForeground(Color.WHITE);
		lblAmplitude.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblAmplitude.setBounds(48, 40, 48, 23);
		buttonPanel.add(lblAmplitude);

		JLabel lblYPosition = new JLabel("Y Position");
		lblYPosition.setForeground(Color.WHITE);
		lblYPosition.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblYPosition.setBounds(145, 40, 65, 23);
		buttonPanel.add(lblYPosition);

		JButton btn3P = new JButton("MORE");
		btn3P.setHorizontalAlignment(SwingConstants.LEFT);
		btn3P.setIcon(new ImageIcon(MainClass.class
				.getResource("/resourses_images/Wave_3.png")));
		btn3P.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn3P.setBounds(229, 70, 95, 25);
		btn3P.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				interval = interval * 2;
				valueaxis.setFixedAutoRange(interval);
				time = time * 2;
			}
		});
		buttonPanel.add(btn3P);

		JButton btn3M = new JButton("LESS");
		btn3M.setHorizontalAlignment(SwingConstants.LEFT);
		btn3M.setIcon(new ImageIcon(MainClass.class
				.getResource("/resourses_images/Wave_1.png")));
		btn3M.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn3M.setBounds(229, 105, 95, 25);
		btn3M.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				interval = interval / 2;
				valueaxis.setFixedAutoRange(interval);
				time = time / 2;
			}
		});
		buttonPanel.add(btn3M);

		JLabel lblAmplitude2 = new JLabel("Frequency");
		lblAmplitude2.setForeground(Color.WHITE);
		lblAmplitude2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblAmplitude2.setBounds(247, 40, 70, 23);
		buttonPanel.add(lblAmplitude2);

		JLabel lblGeneralControl = new JLabel("Configuration");
		lblGeneralControl.setForeground(new Color(153, 204, 204));
		lblGeneralControl.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblGeneralControl.setBounds(668, 10, 122, 25);
		buttonPanel.add(lblGeneralControl);

		JLabel label = new JLabel("");
		label.setForeground(new Color(153, 204, 204));
		label.setFont(new Font("Segoe UI", Font.BOLD, 18));
		label.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		label.setBounds(20, 40, 423, 100);
		buttonPanel.add(label);

		id_txt = new JTextField();
		id_txt.setColumns(3);
		id_txt.setToolTipText("ID");
		id_txt.setText("100");
		id_txt.setBounds(725, 49, 54, 20);
		buttonPanel.add(id_txt);

		JLabel lblId = new JLabel("ID :");
		lblId.setForeground(Color.WHITE);
		lblId.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblId.setBounds(687, 48, 30, 20);
		buttonPanel.add(lblId);

		JButton btnClr = new JButton("Clear");
		btnClr.setForeground(Color.BLACK);
		btnClr.setBackground(Color.LIGHT_GRAY);
		btnClr.setBounds(687, 110, 92, 20);
		btnClr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane optionPane = new JOptionPane(
						"Do you really want to clear the chart?",
						JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
				JDialog dialog = optionPane.createDialog(null,
						"Manual Creation");
				dialog.setVisible(true);

				int exitMenuItem = ((Integer) optionPane.getValue()).intValue();
				switch (exitMenuItem) {
				case JOptionPane.NO_OPTION:
					break;
				case JOptionPane.YES_OPTION:
					clear();
				}
			}
		});
		buttonPanel.add(btnClr);

		JLabel label_1 = new JLabel("");
		label_1.setForeground(new Color(153, 204, 204));
		label_1.setFont(new Font("Segoe UI", Font.BOLD, 18));
		label_1.setBorder(new LineBorder(Color.BLACK, 1, true));
		label_1.setBounds(668, 40, 122, 100);
		buttonPanel.add(label_1);
		ButtonGroup group = new ButtonGroup();

		rdbtnBluetooth = new JRadioButton("Bluetooth");
		rdbtnBluetooth.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		rdbtnBluetooth.setForeground(Color.LIGHT_GRAY);
		rdbtnBluetooth.setBackground(panelColor);
		rdbtnBluetooth.setBounds(496, 48, 109, 25);
		buttonPanel.add(rdbtnBluetooth);
		group.add(rdbtnBluetooth);
		rdbtnBluetooth.setSelected(false);

		rdbtnZigbee = new JRadioButton("ZigBee");
		rdbtnZigbee.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		rdbtnZigbee.setBackground(panelColor);
		rdbtnZigbee.setForeground(Color.LIGHT_GRAY);
		rdbtnZigbee.setBounds(496, 78, 109, 25);
		buttonPanel.add(rdbtnZigbee);
		group.add(rdbtnZigbee);
		rdbtnZigbee.setSelected(false);

		rdbtnUdp = new JRadioButton("UDP");
		rdbtnUdp.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		rdbtnUdp.setBackground(panelColor);
		rdbtnUdp.setForeground(Color.LIGHT_GRAY);
		rdbtnUdp.setBounds(496, 108, 109, 25);
		buttonPanel.add(rdbtnUdp);
		group.add(rdbtnUdp);
		rdbtnUdp.setSelected(true);

		
		rdbtnImportFile = new JRadioButton("Import File");
		rdbtnImportFile.setForeground(Color.LIGHT_GRAY);
		rdbtnImportFile.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		rdbtnImportFile.setBackground(panelColor);
		rdbtnImportFile.setBounds(496, 138, 109, 25);
		rdbtnImportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooserLog = new JFileChooser("user.home");
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Log Files", "txt", "text", "asc");
				chooserLog.setFileFilter(filter);
				chooserLog.showOpenDialog(null);
				selLogFile = chooserLog.getSelectedFile();
			}
		});
		buttonPanel.add(rdbtnImportFile);
		group.add(rdbtnImportFile);
		rdbtnImportFile.setSelected(false);

		JLabel lblProtocol = new JLabel("Protocol");
		lblProtocol.setForeground(new Color(153, 204, 204));
		lblProtocol.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblProtocol.setBounds(483, 10, 122, 25);
		buttonPanel.add(lblProtocol);

		JLabel lblUploadFile = new JLabel("");
		lblUploadFile.setForeground(new Color(153, 204, 204));
		lblUploadFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblUploadFile.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		lblUploadFile.setBounds(483, 40, 141, 100);
		buttonPanel.add(lblUploadFile);

		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				valueaxis.resizeRange(1);
			}
		});

		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				valueaxis.setAutoRange(true);
			}
		});

		chckbx[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chckbx[1].isSelected()) {
					jfreechart.getXYPlot().getRenderer(1).setBaseSeriesVisible(true);
					axis1.setTickLabelsVisible(true);
					axis1.setVisible(true);
					axis = axis1;
					SetBackColorCheck();
					chckbx[1].setBackground(new Color(0, 0, 20));
				} else {
					jfreechart.getXYPlot().getRenderer(1).setBaseSeriesVisible(false);
					axis1.setTickLabelsVisible(false);
					axis1.setVisible(false);
				}
			
			}
		});
		chckbx[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chckbx[2].isSelected()) {
					jfreechart.getXYPlot().getRenderer(2).setBaseSeriesVisible(true);
					axis2.setTickLabelsVisible(true);
					axis2.setVisible(true);
					axis = axis2;
					SetBackColorCheck();
					chckbx[2].setBackground(new Color(0, 0, 20));
				} else {
					jfreechart.getXYPlot().getRenderer(2).setBaseSeriesVisible(false);
					axis2.setTickLabelsVisible(false);
					axis2.setVisible(false);
				}
			
			}
		});
		chckbx[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chckbx[3].isSelected()) {
					jfreechart.getXYPlot().getRenderer(3).setBaseSeriesVisible(true);
					axis3.setTickLabelsVisible(true);
					axis3.setVisible(true);
					axis = axis3;
					SetBackColorCheck();
					chckbx[3].setBackground(new Color(0, 0, 20));
				} else {
					jfreechart.getXYPlot().getRenderer(3).setBaseSeriesVisible(false);
					axis3.setTickLabelsVisible(false);
					axis3.setVisible(false);
				}
			
			}
		});
		chckbx[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chckbx[4].isSelected()) {
					jfreechart.getXYPlot().getRenderer(4).setBaseSeriesVisible(true);
					axis4.setTickLabelsVisible(true);
					axis4.setVisible(true);
					axis = axis4;
					SetBackColorCheck();
					chckbx[4].setBackground(new Color(0, 0, 20));
				} else {
					jfreechart.getXYPlot().getRenderer(4).setBaseSeriesVisible(false);
					axis4.setTickLabelsVisible(false);
					axis4.setVisible(false);
				}
			
			}
		});
		chckbx[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chckbx[5].isSelected()) {
					jfreechart.getXYPlot().getRenderer(5).setBaseSeriesVisible(true);
					axis5.setTickLabelsVisible(true);
					axis5.setVisible(true);
					axis = axis5;
					SetBackColorCheck();
					chckbx[5].setBackground(new Color(0, 0, 20));
				} else {
					jfreechart.getXYPlot().getRenderer(5).setBaseSeriesVisible(false);
					axis5.setTickLabelsVisible(false);
					axis5.setVisible(false);
				}
			
			}
		});
		chckbx[6].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chckbx[6].isSelected()) {
					jfreechart.getXYPlot().getRenderer(6).setBaseSeriesVisible(true);
					axis6.setTickLabelsVisible(true);
					axis6.setVisible(true);
					axis = axis6;
					SetBackColorCheck();
					chckbx[6].setBackground(new Color(0, 0, 20));
				} else {
					jfreechart.getXYPlot().getRenderer(6).setBaseSeriesVisible(false);
					axis6.setTickLabelsVisible(false);
					axis6.setVisible(false);
				}
			
			}
		});
		chckbx[7].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chckbx[7].isSelected()) {
					jfreechart.getXYPlot().getRenderer(7).setBaseSeriesVisible(true);
					axis7.setTickLabelsVisible(true);
					axis7.setVisible(true);
					axis = axis7;
					SetBackColorCheck();
					chckbx[7].setBackground(new Color(0, 0, 20));
				} else {
					jfreechart.getXYPlot().getRenderer(7).setBaseSeriesVisible(false);
					axis7.setTickLabelsVisible(false);
					axis7.setVisible(false);
				}
			
			}
		});
		chckbx[8].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chckbx[8].isSelected()) {
					jfreechart.getXYPlot().getRenderer(8).setBaseSeriesVisible(true);
					axis8.setTickLabelsVisible(true);
					axis8.setVisible(true);
					axis = axis8;
					SetBackColorCheck();
					chckbx[8].setBackground(new Color(0, 0, 20));
				} else {
					jfreechart.getXYPlot().getRenderer(8).setBaseSeriesVisible(false);
					axis8.setTickLabelsVisible(false);
					axis8.setVisible(false);
				}
			
			}
		});

		chckbx[9].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbx[9].isSelected()) {
					axis = axis9;

					jfreechart.getXYPlot().getRenderer(1)
							.setBaseSeriesVisible(true);
					jfreechart.getXYPlot().getRenderer(2)
							.setBaseSeriesVisible(true);
					jfreechart.getXYPlot().getRenderer(3)
							.setBaseSeriesVisible(true);
					jfreechart.getXYPlot().getRenderer(4)
							.setBaseSeriesVisible(true);
					jfreechart.getXYPlot().getRenderer(5)
							.setBaseSeriesVisible(true);
					jfreechart.getXYPlot().getRenderer(6)
							.setBaseSeriesVisible(true);
					jfreechart.getXYPlot().getRenderer(7)
							.setBaseSeriesVisible(true);
					jfreechart.getXYPlot().getRenderer(8)
							.setBaseSeriesVisible(true);

					axis1.setTickLabelsVisible(true);
					axis2.setTickLabelsVisible(true);
					axis3.setTickLabelsVisible(true);
					axis4.setTickLabelsVisible(true);
					axis5.setTickLabelsVisible(true);
					axis6.setTickLabelsVisible(true);
					axis7.setTickLabelsVisible(true);
					axis8.setTickLabelsVisible(true);

					axis1.setVisible(true);
					axis2.setVisible(true);
					axis3.setVisible(true);
					axis4.setVisible(true);
					axis5.setVisible(true);
					axis6.setVisible(true);
					axis7.setVisible(true);
					axis8.setVisible(true);
					
					chckbx[1].setSelected(true);
					chckbx[2].setSelected(true);
					chckbx[3].setSelected(true);
					chckbx[4].setSelected(true);
					chckbx[5].setSelected(true);
					chckbx[6].setSelected(true);
					chckbx[7].setSelected(true);
					chckbx[8].setSelected(true);
			
				} else {

					jfreechart.getXYPlot().getRenderer(1)
							.setBaseSeriesVisible(false);
					jfreechart.getXYPlot().getRenderer(2)
							.setBaseSeriesVisible(false);
					jfreechart.getXYPlot().getRenderer(3)
							.setBaseSeriesVisible(false);
					jfreechart.getXYPlot().getRenderer(4)
							.setBaseSeriesVisible(false);
					jfreechart.getXYPlot().getRenderer(5)
							.setBaseSeriesVisible(false);
					jfreechart.getXYPlot().getRenderer(6)
							.setBaseSeriesVisible(false);
					jfreechart.getXYPlot().getRenderer(7)
							.setBaseSeriesVisible(false);
					jfreechart.getXYPlot().getRenderer(8)
							.setBaseSeriesVisible(false);

					axis1.setTickLabelsVisible(false);
					axis2.setTickLabelsVisible(false);
					axis3.setTickLabelsVisible(false);
					axis4.setTickLabelsVisible(false);
					axis5.setTickLabelsVisible(false);
					axis6.setTickLabelsVisible(false);
					axis7.setTickLabelsVisible(false);
					axis8.setTickLabelsVisible(false);

					axis1.setVisible(false);
					axis2.setVisible(false);
					axis3.setVisible(false);
					axis4.setVisible(false);
					axis5.setVisible(false);
					axis6.setVisible(false);
					axis7.setVisible(false);
					axis8.setVisible(false);

					chckbx[1].setSelected(false);
					chckbx[2].setSelected(false);
					chckbx[3].setSelected(false);
					chckbx[4].setSelected(false);
					chckbx[5].setSelected(false);
					chckbx[6].setSelected(false);
					chckbx[7].setSelected(false);
					chckbx[8].setSelected(false);
			
				}
			}
		});

		jpanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		BorderFactory.createLineBorder(Color.black);
		chartpanel.setPreferredSize(new Dimension(800, 400));
	}

	private void SetBackColorCheck() {
		for (int x = 1; x <= 9; x++) {
			chckbx[x].setBackground(panelColor);
		}
	}

	// Run method
	public void run() {
		while (true) 
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			// Log file Log+date+.asc is created
			if (activateCanLogging == true )
			{
	        	if ( flagStartReading )
	            {
					if (!timeStartFlag) {
						start = (double) (new Date()).getTime();
						timeStartFlag = true;
					}
					current = (double) (new Date()).getTime();
					currenttime_second = (current - start) / 1000;
	        		oCanMsg = oVciJava.CanMessageReader(oCanMsgReader);
	    			if ( oCanMsg != null )
	    			{
	            		canLine = oCanMsg.toString();
	            		System.out.printf("recvd message %s\n", canLine);
	            		//recvd message Timestamp: 13756081 Flags:      ID: 0x00000100 Data: 0xFF 0xEE 0xDD 0xCC 0xBB 0xAA 0x99 0x88
	            		lineToFile = ("" + canLine);

	        			if (lineToFile != null ) {
        					int byteCtrl = 0;
        					String[] CanStringSplitted = lineToFile.split("\\s+");
	        				lineToFile = null;
        					if (CanStringSplitted[4].startsWith("I")) {
        						byteCtrl = 5;
        					} else {
        						byteCtrl = 4;
        					}
        					String x_id = CanStringSplitted[byteCtrl].substring(7, 10);
							// Read CAN ID from lineToFile as well as from JTextField
							x = Integer.parseInt(x_id, 16);
							idx0 = id_txt.getText();

							if (idx0.isEmpty()) {
								idx0 = "0";
								continue;
							} else {
							}

							try {
								id = Integer.parseInt(idx0, 16);
							} catch (Exception e) {
							}

        					df.applyPattern(pattern);
        					int dataLength = CanStringSplitted.length - byteCtrl - 2;
        					
        					if (CanStringSplitted.length >= byteCtrl + 2) {
        						writerLog.append("\r\n   " + df.format(currenttime_second) + " 1  "
        								+ x_id + "             Rx   d " + dataLength);
        					}

        					if (CanStringSplitted.length >= byteCtrl + 3) {
        						v1 = CanStringSplitted[byteCtrl + 2].substring(2, 4);
        						writerLog.append(" " + v1);
    							if (x == id) {
	        						//value1 = Integer.parseInt(v1, 16);
									value1 = (int) (Math.random()*256)*1000;
									serie1.add(currenttime_second, value1);
    							}
        					}

        					if (CanStringSplitted.length >= byteCtrl + 4) {
        						v2 = CanStringSplitted[byteCtrl + 3].substring(2, 4);
        						writerLog.append(" " + v2);
    							if (x == id) {
	        						//value2 = Integer.parseInt(v2, 16);
									value2 = (int) (Math.random()*256);
									serie2.add(currenttime_second, value2);
    							}
        					}

        					if (CanStringSplitted.length >= byteCtrl + 5) {
        						v3 = CanStringSplitted[byteCtrl + 4].substring(2, 4);
        						writerLog.append(" " + v3);
    							if (x == id) {
									//value3 = Integer.parseInt(v3, 16);
									value3 = (int) (Math.random()*256);
									serie3.add(currenttime_second, value3);
    							}
        					}

        					if (CanStringSplitted.length >= byteCtrl + 6) {
        						v4 = CanStringSplitted[byteCtrl + 5].substring(2, 4);
        						writerLog.append(" " + v4);
    							if (x == id) {
									//value4 = Integer.parseInt(v4, 16);
									value4 = (int) (Math.random()*256);
									serie4.add(currenttime_second, value4);
    							}
        					}

        					if (CanStringSplitted.length >= byteCtrl + 7) {
        						v5 = CanStringSplitted[byteCtrl + 6].substring(2, 4);
        						writerLog.append(" " + v5);
    							if (x == id) {
									//value5 = Integer.parseInt(v5, 16);
									value5 = (int) (Math.random()*256);
									serie5.add(currenttime_second, value5);
    							}
        					}

        					if (CanStringSplitted.length >= byteCtrl + 8) {
        						v6 = CanStringSplitted[byteCtrl + 7].substring(2, 4);
        						writerLog.append(" " + v6);
    							if (x == id) {
									//value6 = Integer.parseInt(v6, 16);
									value6 = (int) (Math.random()*256);
									serie6.add(currenttime_second, value6);
    							}
        					}

        					if (CanStringSplitted.length >= byteCtrl + 9) {
        						v7 = CanStringSplitted[byteCtrl + 8].substring(2, 4);
        						writerLog.append(" " + v7);
    							if (x == id) {
									//value7 = Integer.parseInt(v7, 16);
									value7 = (int) (Math.random()*256);
									serie7.add(currenttime_second, value7);
    							}
        					}

        					if (CanStringSplitted.length >= byteCtrl + 10) {
        						v8 = CanStringSplitted[byteCtrl + 9].substring(2, 4);
        						writerLog.append(" " + v8);
    							if (x == id) {
									//value8 = Integer.parseInt(v8, 16);
									value8 = (int) (Math.random()*256);
									serie8.add(currenttime_second, value8);
    							}
        					}
	        			}	            		
	    			}
	    			else
	    			{
	    				serie0.add(currenttime_second, null);
	    			}
	            }
			}
			else if ( activateZigbeeLogging == true )
			{
	        	if ( flagStartReading )
	            {
					if (!timeStartFlag) {
						start = (double) (new Date()).getTime();
						timeStartFlag = true;
					}
					current = (double) (new Date()).getTime();
					currenttime_second = (current - start) / 1000;
					
					int num = 0;
					int length = 0;
					oSerialJava.serialMutex.lock();
					if ( single_byte_oscilloscope == true )
					{
		        		num = oSerialJava.getSerialData();
						length = num;
					}
					else
					{
		        		display_string = oSerialJava.getSerialLine();
					}
					if ( num != 0)
	    			{
        				System.out.printf("number of bytes read %d\n", num);
	    				while ( num != 0 )
	    				{
		    				// the protocol will be executed from this point.
	    					// store all the variables till a $ is received.
	    					data_serialport[index_dollar] = oSerialJava.SerialByteReader()[length-num];
		    				num--;
		    				//System.out.printf("recvd message %d\n", data_serialport[index_dollar]);
		    				if ( data_serialport[index_dollar] == '\n' )
		    				{
		    					System.out.println(Arrays.toString(data_serialport));
			    				//System.out.printf("Trigger oscilloscope, $ at %d\n", (index_dollar+1));
		    					df.applyPattern(pattern);
								
		    					//writerLog.append(" " + v1);
								//value1 = Integer.parseInt(v1, 16);
								//value1 = (int) (Math.random()*256);
		    					value1 = data_serialport[0];
								serie1.add(currenttime_second, value1);
								value2 = data_serialport[1];
								serie2.add(currenttime_second, value2);
								value3 = data_serialport[2];
								serie3.add(currenttime_second, value3);
								value4 = data_serialport[3];
								serie4.add(currenttime_second, value4);
								value5 = data_serialport[4];
								serie5.add(currenttime_second, value5);
								value6 = data_serialport[5];
								serie6.add(currenttime_second, value6);
								value7 = data_serialport[6];
								serie7.add(currenttime_second, value7);
								value8 = data_serialport[7];
								serie8.add(currenttime_second, value8);
								index_dollar = 0;
								Arrays.fill(data_serialport, (byte)0);
		    				}
		    				else
		    				{
		    					index_dollar++;
		    				}
	    				}
	    			}
					else if ( display_string != null )
					{
		        		System.out.println(display_string);
		        		String[] parts = new String[10];
						parts = display_string.split("#");
						for ( int i = 0; i < parts.length; i++)
						{
							//System.out.printf("counter = %d at %s", i, parts[i]);
							//abraca#dsfsd#sdfdsfdsfsd#Run#MF:52;101;48#further
							try
							{
								if ( i == 4 && parts.length == 6 )
								{
									String[] data_magnet = new String[6];
									data_magnet = parts[i].split(";");
//									System.out.println((data_magnet[0].split(":"))[1]);
//									System.out.println(data_magnet[1]);
//									System.out.println(data_magnet[2]);
			    					value1 = Integer.parseInt((data_magnet[0].split(":"))[1]);
									serie1.add(currenttime_second, value1);
									value2 = Integer.parseInt(data_magnet[1]);
									serie2.add(currenttime_second, value2);
									value3 = Integer.parseInt(data_magnet[2]);
									serie3.add(currenttime_second, value3);
								}
								else if ( i == 5 && parts.length == 7 )
								{
									String[] data_magnet = new String[6];
									data_magnet = parts[i].split(";");
	//									System.out.println((data_magnet[0].split(":"))[1]);
	//									System.out.println(data_magnet[1]);
	//									System.out.println(data_magnet[2]);
			    					value1 = Integer.parseInt((data_magnet[0].split(":"))[1]);
									serie1.add(currenttime_second, value1);
									value2 = Integer.parseInt(data_magnet[1]);
									serie2.add(currenttime_second, value2);
									value3 = Integer.parseInt(data_magnet[2]);
									serie3.add(currenttime_second, value3);
								}
							}
							catch (Exception e)
							{
								
							}
						}
					}
	    			else
	    			{
	    				serie0.add(currenttime_second, null);
	    			}
	        		oSerialJava.serialMutex.unlock();
	            }
			}
			else if ( activateUdpLogging == true )
			{
				if ( flagStartReading )
	            {
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
						oUdpJava.receiveNonBlocking();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if ( single_byte_oscilloscope == true )
					{
		        		num = oSerialJava.getSerialData();
						length = num;
					}
					else
					{
		        		display_string = oSerialJava.getSerialLine();
					}
					if ( num != 0)
	    			{
        				System.out.printf("number of bytes read %d\n", num);
	    				while ( num != 0 )
	    				{
		    				// the protocol will be executed from this point.
	    					// store all the variables till a $ is received.
	    					data_serialport[index_dollar] = oSerialJava.SerialByteReader()[length-num];
		    				num--;
		    				//System.out.printf("recvd message %d\n", data_serialport[index_dollar]);
		    				if ( data_serialport[index_dollar] == '\n' )
		    				{
		    					System.out.println(Arrays.toString(data_serialport));
			    				//System.out.printf("Trigger oscilloscope, $ at %d\n", (index_dollar+1));
		    					df.applyPattern(pattern);
								
		    					//writerLog.append(" " + v1);
								//value1 = Integer.parseInt(v1, 16);
								//value1 = (int) (Math.random()*256);
		    					value1 = data_serialport[0];
								serie1.add(currenttime_second, value1);
								value2 = data_serialport[1];
								serie2.add(currenttime_second, value2);
								value3 = data_serialport[2];
								serie3.add(currenttime_second, value3);
								value4 = data_serialport[3];
								serie4.add(currenttime_second, value4);
								value5 = data_serialport[4];
								serie5.add(currenttime_second, value5);
								value6 = data_serialport[5];
								serie6.add(currenttime_second, value6);
								value7 = data_serialport[6];
								serie7.add(currenttime_second, value7);
								value8 = data_serialport[7];
								serie8.add(currenttime_second, value8);
								index_dollar = 0;
								Arrays.fill(data_serialport, (byte)0);
		    				}
		    				else
		    				{
		    					index_dollar++;
		    				}
	    				}
	    			}
					else if ( display_string != null )
					{
		        		System.out.println(display_string);
		        		String[] parts = new String[10];
						parts = display_string.split("#");
						for ( int i = 0; i < parts.length; i++)
						{
							//System.out.printf("counter = %d at %s", i, parts[i]);
							//abraca#dsfsd#sdfdsfdsfsd#Run#MF:52;101;48#further
							try
							{
								if ( i == 4 && parts.length == 6 )
								{
									String[] data_magnet = new String[6];
									data_magnet = parts[i].split(";");
//									System.out.println((data_magnet[0].split(":"))[1]);
//									System.out.println(data_magnet[1]);
//									System.out.println(data_magnet[2]);
			    					value1 = Integer.parseInt((data_magnet[0].split(":"))[1]);
									serie1.add(currenttime_second, value1);
									value2 = Integer.parseInt(data_magnet[1]);
									serie2.add(currenttime_second, value2);
									value3 = Integer.parseInt(data_magnet[2]);
									serie3.add(currenttime_second, value3);
								}
								else if ( i == 5 && parts.length == 7 )
								{
									String[] data_magnet = new String[6];
									data_magnet = parts[i].split(";");
	//									System.out.println((data_magnet[0].split(":"))[1]);
	//									System.out.println(data_magnet[1]);
	//									System.out.println(data_magnet[2]);
			    					value1 = Integer.parseInt((data_magnet[0].split(":"))[1]);
									serie1.add(currenttime_second, value1);
									value2 = Integer.parseInt(data_magnet[1]);
									serie2.add(currenttime_second, value2);
									value3 = Integer.parseInt(data_magnet[2]);
									serie3.add(currenttime_second, value3);
								}
							}
							catch (Exception e)
							{
								
							}
						}
					}
	    			else
	    			{
	    				serie0.add(currenttime_second, null);
	    			}
	        		oSerialJava.serialMutex.unlock();
	            }
			}
			
		}
	}

	public static void ReadFrameNull() {

		serie1.add(currenttime_second, null);
		serie2.add(currenttime_second, null);
		serie3.add(currenttime_second, null);
		serie4.add(currenttime_second, null);
		serie5.add(currenttime_second, null);
		serie6.add(currenttime_second, null);
		serie7.add(currenttime_second, null);
		serie8.add(currenttime_second, null);
		
	}

	// RunVci method
	public void runVci(String[] args) {
		final SwingWorker<?, ?> worker = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				logFlag = false;
				flagStatus = false;
		    	
				if ( activateCanLogging == true )
				{
					/* BT CAN */
					oVciJava = new VciJava();
			    	oBalObject = oVciJava.InitCanBlue();
			    	oCanMsgReader = oVciJava.StartCan(oBalObject, (short)0);
			    	if(oCanMsgReader != null)
			    	{
			    		flagStartReading = true;
			    		if (!logFlag) {
							// Create the log file if not already created
							Date date = new Date();
							Log = new File("output/Log_" + formatter.format(date) + ".asc");
							logFlag = true;
							headerFlag = false;
							// Add data to files
							writerLog = new PrintWriter(new FileWriter(Log, true));
							writerLog.append("date " + formatterHeader.format(date)
									+ "\r\nbase hex  timestamps absolute"
									+ "\r\ninternal events logged");
							//writerLog.close();				
							headerFlag = true;
						}
			    	}
			    	else
			    	{
			    		flagStartReading = false;
			    	}
				}
				/* Serial */
				else if ( activateZigbeeLogging == true )
				{
			    	oSerialJava = new SerialJava();
			    	flagStartReading = oSerialJava.oeffneSerialPort("COM6");
				}
				/* UDP */
				else if ( activateUdpLogging == true )
				{
			    	oUdpJava = new UdpJava();
			    	flagStartReading = oUdpJava.startServer();
				}
		    	return null;
			}
		};
		worker.execute();
	}

	// AxisConfiguration method
	public static void axisConfiguration() {

		int axisCtrl = 1;
		while (axisCtrl <= 8) {
			if (axisCtrl == 1) {
				axis = axis1;
				renderer = renderer1;
				data = data1;
			}
			if (axisCtrl == 2) {
				axis = axis2;
				renderer = renderer2;
				data = data2;
			}
			if (axisCtrl == 3) {
				axis = axis3;
				renderer = renderer3;
				data = data3;
			}
			if (axisCtrl == 4) {
				axis = axis4;
				renderer = renderer4;
				data = data4;
			}
			if (axisCtrl == 5) {
				axis = axis5;
				renderer = renderer5;
				data = data5;
			}
			if (axisCtrl == 6) {
				axis = axis6;
				renderer = renderer6;
				data = data6;
			}
			if (axisCtrl == 7) {
				axis = axis7;
				renderer = renderer7;
				data = data7;
			}
			if (axisCtrl == 8) {
				axis = axis8;
				renderer = renderer8;
				data = data8;
			}

			xyplot.setRangeAxis(axisCtrl, axis);
			axis.setTickLabelsVisible(false);
			axis.setVisible(false);
			xyplot.setDataset(axisCtrl, data);
			xyplot.mapDatasetToRangeAxis(axisCtrl, axisCtrl);
			renderer = new XYStepRenderer();
			xyplot.setRenderer(axisCtrl, renderer);
			jfreechart.getXYPlot().getRenderer(axisCtrl)
					.setBaseSeriesVisible(false);
			axis.setLabelPaint(jfreechart.getXYPlot().getRenderer(axisCtrl)
					.getItemPaint(0, 0));
			axis.setTickLabelPaint(jfreechart.getXYPlot().getRenderer(axisCtrl)
					.getItemPaint(0, 0));
			xyplot.setRangeAxis(axisCtrl, axis);
			axis.setRange(-5, 260);
			axisCtrl++;
		}
	}

	// CloseMenu method
	private void closeMenu() {

		JOptionPane optionPane = new JOptionPane("Do you really want to exit?",
				JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
		JDialog dialog = optionPane.createDialog(null, "Manual Creation");
		dialog.setVisible(true);

		int exitMenuItem = ((Integer) optionPane.getValue()).intValue();
		switch (exitMenuItem) {
		case JOptionPane.NO_OPTION:
			break;
		case JOptionPane.YES_OPTION:
			System.exit(0);
		}
	}

	// StopMenu method
	private void stopMenu() {

		JOptionPane optionPane = new JOptionPane("Do you really want to stop?",
				JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
		JDialog dialog = optionPane.createDialog(null, "Manual Creation");
		dialog.setVisible(true);

		int exitMenuItem = ((Integer) optionPane.getValue()).intValue();
		switch (exitMenuItem) {
		case JOptionPane.NO_OPTION:
			break;
		case JOptionPane.YES_OPTION:
			YesOption();
		}
	}

	// YesOption method
	private void YesOption() {
		
		if ( activateCanLogging == true )
		{
			oVciJava.StopCan(oBalObject, oCanMsgReader);
			oVciJava.ResetDeviceIndex();
			activateCanLogging = false;
			writerLog.close();
		}
		if ( activateZigbeeLogging == true )
		{
			oSerialJava.schliesseSerialPort();
			activateZigbeeLogging = false;
		}
		if ( activateUdpLogging == true )
		{
			oUdpJava.disconnectPort();
			activateUdpLogging = false;
		}
		flagStartReading = false;
		flagLogFile = true;
		flagStatus = true;
		timeStartFlag = false;
		btnStop.setVisible(false);
		btnStop.setEnabled(false);
		btnStart.setVisible(true);
		btnStart.setEnabled(true);
		rdbtnBluetooth.setEnabled(true);
		rdbtnZigbee.setEnabled(true);
		rdbtnUdp.setEnabled(true);
		rdbtnImportFile.setEnabled(true);
	}

	// Clear method
	private void clear() {
		serie1.clear();
		serie2.clear();
		serie3.clear();
		serie4.clear();
		serie5.clear();
		serie6.clear();
		serie7.clear();
		serie8.clear();

	}

	// ActionPerformed method
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("EXIT")) {
			thread1.interrupt();
			System.exit(0);
		}
	}

	// GetMonthNum method
	public int getMonthNum(String month) {
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };
		for (int i = 0; i < months.length; i++) {
			if (month.equals(months[i]) == true) {
				return i + 1;
			}
		}
		return -1;
	}
}