package scope.gui;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.Stack;

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
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.omg.Messaging.SyncScopeHelper;

import de.ixxat.vci3.bal.IBalObject;
import de.ixxat.vci3.bal.can.CanMessage;
import de.ixxat.vci3.bal.can.ICanMessageReader;
import scope.data.ImportButton;
import scope.vci.VciJava;
import scope.serial.SerialJava;
import scope.graphic.PanningChartPanel;
import scope.graphic.DataReaderInterface;

//View Class
@SuppressWarnings("serial")
public class View extends JFrame implements ViewInterface, ActionListener {

	// Variables
	private MMInterface model;
	private DataReaderInterface reader;
	private static int lastPlotCtrIndex = 0;
	private static int initDatasetCount = 0;
	private static Properties properties = new Properties();
	private Configuration config = new Configuration(properties);
	
	static XYSeries serie0 = new XYSeries("Dummy Serie");
	static XYDataset data0 = new XYSeriesCollection(serie0);

	static NumberAxis lastSelectedAxis;

	static ValueAxis valueaxis = null;

	static XYPlot xyplot;
	static JFreeChart jfreechart = null;

	double interval = 60;
	int frequency = 1;
	double time = 1;

	final static JButton btnStart = new JButton("START");
	final JPanel panel = new JPanel();	
	final JPanel checkBoxPanel = new JPanel();
	final JPanel upperLeftButtonsPanel = new JPanel();

	
	final JButton btnStop = new JButton("STOP");
	JSpinner spinner_2 = new JSpinner();
	private JTextField id_txt;
	JRadioButton rdbtnUdp;
	JButton btnImportFile;
	JButton btnAddDataset;
	JButton btnRemoveDataset;
			
	Color panelColor = new Color(50, 50, 50);

	// View constructor, the chart is created
	public View() {
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				closeMenu();
			}
		});

		setMinimumSize(new Dimension(900, 600));
		setPreferredSize(new Dimension(900, 600));

		jfreechart = ChartFactory.createXYStepChart(null, null, null, null,
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

		/* dummy data set to advance domain/x-axis if no data is received */
		xyplot.setDataset(0, data0);

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
		panel.setBackground(panelColor);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_1.add(panel);

		checkBoxPanel.setBackground(panelColor);
		checkBoxPanel.setLayout (new FlowLayout(FlowLayout.LEFT, 0, 0));
		panel.add(checkBoxPanel);
		
		upperLeftButtonsPanel.setBackground(panelColor);
		upperLeftButtonsPanel.setLayout (new FlowLayout(FlowLayout.RIGHT, 0, 0));
		panel.add(upperLeftButtonsPanel);
		
		btnAddDataset = new JButton("Add Data");
		btnAddDataset.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAddDataset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int plotCtrIndex = checkBoxPanel.getComponentCount()+1;
				
				JCheckBox checkBox = createCheckBox(plotCtrIndex);
				checkBoxPanel.add(checkBox);
				checkBox.revalidate();
				checkBox.repaint();
				
				addDataset();
			}
		});
		upperLeftButtonsPanel.add(btnAddDataset);
		
		btnRemoveDataset = new JButton("remove Data");
		btnRemoveDataset.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRemoveDataset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int latestCheckBoxIndex = checkBoxPanel.getComponentCount()-1;
				
				if (latestCheckBoxIndex >= 0) {
					checkBoxPanel.getComponent(latestCheckBoxIndex).validate();
					checkBoxPanel.remove(latestCheckBoxIndex);
					
					removeDataset();
				}
			}
		});
		upperLeftButtonsPanel.add(btnRemoveDataset);
		
		ImportButton btnImportValues = new ImportButton("Upload File");
		btnImportValues.button.setFont(new Font("Tahoma", Font.PLAIN, 12));
		upperLeftButtonsPanel.add(btnImportValues.button);
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(panelColor);
		buttonPanel.setPreferredSize(new Dimension(800, 140));
		buttonPanel.setLayout(null);
		panel_1.add(buttonPanel);

		btnStart.setBackground(Color.GREEN);
		btnStart.setBounds(687, 79, 92, 20);
		btnStart.setForeground(Color.BLACK);
		btnStart.setVisible(true);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearAllSeries();
				reader.startReading();
				
				btnStop.setEnabled(true);
				btnStop.setVisible(true);
				btnStart.setEnabled(false);
				btnStart.setVisible(false);
				rdbtnUdp.setEnabled(false);
				btnImportFile.setEnabled(false);
				btnAddDataset.setEnabled(false);
				btnRemoveDataset.setEnabled(false);
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
		btn1P.setIcon(new ImageIcon(View.class
				.getResource("/resourses_images/Wave_1.png")));
		btn1P.setBounds(30, 70, 95, 25);
		btn1P.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lastSelectedAxis.resizeRange(0.8);
			}
		});
		buttonPanel.add(btn1P);

		JButton btn1M = new JButton("LESS");
		btn1M.setHorizontalAlignment(SwingConstants.LEFT);
		btn1M.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn1M.setIcon(new ImageIcon(View.class
				.getResource("/resourses_images/Wave_2.png")));
		btn1M.setBounds(30, 105, 95, 25);
		btn1M.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lastSelectedAxis.resizeRange(1.2);
			}
		});
		buttonPanel.add(btn1M);

		JButton btn2P = new JButton("UP");
		btn2P.setHorizontalAlignment(SwingConstants.LEFT);
		btn2P.setIcon(new ImageIcon(View.class
				.getResource("/resourses_images/ArrowUp.png")));
		btn2P.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn2P.setActionCommand("");
		btn2P.setBounds(135, 70, 84, 25);
		btn2P.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lastSelectedAxis.pan(-0.1);
			}
		});
		buttonPanel.add(btn2P);

		JButton btn2M = new JButton("DOWN");
		btn2M.setHorizontalAlignment(SwingConstants.LEFT);
		btn2M.setFont(new Font("Tahoma", Font.BOLD, 11));
		btn2M.setIcon(new ImageIcon(View.class
				.getResource("/resourses_images/ArrowDown.png")));
		btn2M.setBounds(135, 105, 84, 25);
		btn2M.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lastSelectedAxis.pan(0.1);
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
				lastSelectedAxis.setAutoRange(true);
				lastSelectedAxis.setAutoRange(false);
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
		btn3P.setIcon(new ImageIcon(View.class
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
		btn3M.setIcon(new ImageIcon(View.class
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
		//Rename accordingly
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
					clearAllSeries();
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
		
		//Protocol type will be set from the config file
		/*
		rdbtnUdp = new JRadioButton("UDP");
		rdbtnUdp.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		rdbtnUdp.setBackground(panelColor);
		rdbtnUdp.setForeground(Color.LIGHT_GRAY);
		rdbtnUdp.setBounds(496, 48, 109, 25);
		buttonPanel.add(rdbtnUdp);
		group.add(rdbtnUdp);
		rdbtnUdp.setSelected(true);
		rdbtnUdp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reader.setReadUdp();
			}
		});
		*/
		btnImportFile = new JButton("Import Config");
		btnImportFile.setForeground(Color.BLACK);
		btnImportFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		//btnImportFile.setBackground(Color.LIGHT_GRAY);
		btnImportFile.setBounds(503, 44, 109, 25);
		btnImportFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Dont Delete, set to another button
				/*
				JFileChooser chooserLog = new JFileChooser("user.home");
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Log Files", "txt", "text", "asc");
				chooserLog.setFileFilter(filter);
				chooserLog.showOpenDialog(null);
				File selLogFile = chooserLog.getSelectedFile();
				reader.setImportFile(selLogFile);
				*/
				
				JFileChooser chooserLog = new JFileChooser();
				/*
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"properties", "cfg");
				chooserLog.setFileFilter(filter);
				*/
				chooserLog.showOpenDialog(null);
				File configFile = chooserLog.getSelectedFile();
				Configuration.readFile(configFile, properties);
				System.out.println("lastplot " + lastPlotCtrIndex);
				for (int i = 0; i < lastPlotCtrIndex; i++) {
					int latestCheckBoxIndex = checkBoxPanel.getComponentCount()-1;
					System.out.println("latestcheck " + latestCheckBoxIndex);
					checkBoxPanel.getComponent(latestCheckBoxIndex).validate();
					checkBoxPanel.remove(latestCheckBoxIndex);
					
					removeDataset();
				}
								
				for(int indx = 0; indx < Integer.parseInt(properties.getProperty("dataset")); indx++){
					int plotCtrIndex = checkBoxPanel.getComponentCount()+1;
					
					JCheckBox checkBox = createCheckBox(plotCtrIndex);
					checkBoxPanel.add(checkBox);
					checkBox.revalidate();
					checkBox.repaint();
					
					addDataset();
				};
				
			}
		});
		buttonPanel.add(btnImportFile);
		group.add(btnImportFile);
		btnImportFile.setSelected(false);

		JLabel lblProtocol = new JLabel("Configuration");
		lblProtocol.setForeground(new Color(153, 204, 204));
		lblProtocol.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblProtocol.setBounds(483, 10, 175, 25);
		buttonPanel.add(lblProtocol);
		
		/*
		JLabel lblUploadFile = new JLabel("");
		lblUploadFile.setForeground(new Color(153, 204, 204));
		lblUploadFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblUploadFile.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		lblUploadFile.setBounds(483, 40, 141, 41);
		buttonPanel.add(lblUploadFile);
		*/
		JLabel label_2 = new JLabel("");
		label_2.setForeground(new Color(153, 204, 204));
		label_2.setFont(new Font("Segoe UI", Font.BOLD, 18));
		label_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		label_2.setBounds(483, 40, 141, 93);
		buttonPanel.add(label_2);

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

		jpanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		BorderFactory.createLineBorder(Color.black);
		chartpanel.setPreferredSize(new Dimension(800, 400));
		
		this.setVisible(true);
		RefineryUtilities.centerFrameOnScreen(this);
		//Test insertion of datasets, can be deleted anytime
		
		for(int indx = 0; indx < Integer.parseInt(properties.getProperty("dataset")); indx++){
			int plotCtrIndex = checkBoxPanel.getComponentCount()+1;
			
			JCheckBox checkBox = createCheckBox(plotCtrIndex);
			checkBoxPanel.add(checkBox);
			checkBox.revalidate();
			checkBox.repaint();
			
			addDataset();
		};
		
	}
	/* End of View constructor */


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
		reader.stopReading();
		btnStop.setVisible(false);
		btnStop.setEnabled(false);
		btnStart.setVisible(true);
		btnStart.setEnabled(true);
		btnImportFile.setEnabled(true);
		btnAddDataset.setEnabled(true);
		btnRemoveDataset.setEnabled(true);
	}

	// ActionPerformed method
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("EXIT")) {
			reader.terminateReader();
			System.exit(0);
		}
	}
		
	/* Clear all Data stored in Series */
	private void clearAllSeries() {
		int datasetCount = View.xyplot.getDatasetCount();
		for (int plotCtrIndex = 0; plotCtrIndex < datasetCount; plotCtrIndex++) {
			((XYSeriesCollection) View.xyplot
					.getDataset(plotCtrIndex)).getSeries(0).clear();
		}
	}
	
	private JCheckBox createCheckBox(final int plotCtrIndex) {
		final JCheckBox checkBox = new JCheckBox("Data " + String.valueOf(plotCtrIndex));
		checkBox.setForeground(Color.LIGHT_GRAY);
		checkBox.setBackground(panelColor);
		
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (checkBox.isSelected()) {
					View.xyplot.getRenderer(plotCtrIndex).setBaseSeriesVisible(true);
					/* TODO description */
					lastSelectedAxis = (NumberAxis) View.xyplot.getRangeAxis(plotCtrIndex);
					
					int maxCompIndex = checkBoxPanel.getComponentCount()-1;
					for (int index = 0; index <= maxCompIndex; index++) {
						checkBoxPanel.getComponent(index).setBackground(panelColor);
					}
					
					checkBox.setBackground(new Color(0, 0, 20));
					
				} else {
					View.xyplot.getRenderer(plotCtrIndex).setBaseSeriesVisible(false);
				}
			}
		});		
		return checkBox;		
	}
	
	private void addDataset() {
		int plotCtrIndex = ++lastPlotCtrIndex;

		/*
		 * Creating an axis, dataset container, serie and renderer for one data set
		 * to represent (e.g. speed)
		 */
		//if (plotCtrIndex >= View.xyplot.getDatasetCount()) {
			//Get from config file
			String name = new String("Data " + String.valueOf(plotCtrIndex));
	
			XYStepRenderer xyRenderer = new XYStepRenderer();
			NumberAxis numberAxis = new NumberAxis(name);
			XYDataset dataset = new XYSeriesCollection();
			XYSeries serie = new XYSeries(name);
	
			/* Configuring xyRenderer */
			xyRenderer.setBaseSeriesVisible(false);
			/* Associate xyRenderer with the corresponding plot control index */
			View.xyplot.setRenderer(plotCtrIndex, xyRenderer);
	
			/* Configuring the numberAxis */
			numberAxis.setTickLabelsVisible(false);
			numberAxis.setVisible(false);
			numberAxis.setRange(-5, 260);
			numberAxis.setLabelPaint(xyRenderer.getItemPaint(0, 0));
			numberAxis.setTickLabelPaint(xyRenderer.getItemPaint(0, 0));
			/*
			 * Associate numberAxis with the corresponding plot control index,
			 * setting it up as rangeAxis (y-axis)
			 */
			View.xyplot.setRangeAxis(plotCtrIndex, numberAxis);
	
			/* Associate dataset with the corresponding plot control index */
			View.xyplot.setDataset(plotCtrIndex, dataset);
			/* Binding dataset to previously created numberAxis */
			View.xyplot.mapDatasetToRangeAxis(plotCtrIndex, plotCtrIndex);
	
			/*
			 * Adds the serie to the created and set up dataset, serie will be
			 * storing one incoming data set (e.g. speed) while representation of
			 * this data set is defined by the xyRenderer, dataset and numberAxis.
			 */
			((XYSeriesCollection) dataset).addSeries(serie);
		//}
		View.xyplot.getRangeAxis(plotCtrIndex).setTickLabelsVisible(true);
		View.xyplot.getRangeAxis(plotCtrIndex).setVisible(true);
	}
	
	private void removeDataset() {
		/*
		 * REmoving axis, dataset container, serie and renderer of one data set
		 */
		int plotCtrIndex = lastPlotCtrIndex;	
		
		if (plotCtrIndex > 0) {
			View.xyplot.getRangeAxis(plotCtrIndex).setTickLabelsVisible(false);
			View.xyplot.getRangeAxis(plotCtrIndex).setVisible(false);
			View.xyplot.getRenderer(plotCtrIndex).setBaseSeriesVisible(false);

			((XYSeriesCollection)View.xyplot.getDataset(plotCtrIndex)).getSeries(0).clear();

			lastPlotCtrIndex--;
		}
	}
	
	@Override
	public void notifyDataChange() {
		LinkedList<double[]> dataArrayQueue = model.getData();
		double[] dataArray;
		while ((dataArray = dataArrayQueue.poll()) != null) {
			View.serie0.add(dataArray[0], null);
			for (int plotCtrIndex = 1; plotCtrIndex < lastPlotCtrIndex /*dataArray.length*/; plotCtrIndex++) {
				((XYSeriesCollection) View.xyplot.getDataset(plotCtrIndex))
						.getSeries(0).add(dataArray[0], dataArray[plotCtrIndex]);
			}
		}
	}

	@Override
	public void initDatasetCapacity(int arrayDataCount) {
		this.initDatasetCount = arrayDataCount;
		
		for (int plotCtrIndex = 1; plotCtrIndex <= arrayDataCount; plotCtrIndex++) {
			/*
			 * Creating one axis, dataset container, serie and renderer for one
			 * data set to represent (e.g. speed)
			 */
			String name = new String("Data " + String.valueOf(plotCtrIndex));

			XYStepRenderer xyRenderer = new XYStepRenderer();
			NumberAxis numberAxis = new NumberAxis(name);
			XYDataset dataset = new XYSeriesCollection();
			XYSeries serie = new XYSeries(name);

			/* Configuring xyRenderer */
			xyRenderer.setBaseSeriesVisible(false);
			/* Associate xyRenderer with the corresponding plot control index */
			View.xyplot.setRenderer(plotCtrIndex, xyRenderer);

			/* Configuring the numberAxis */
			numberAxis.setTickLabelsVisible(false);
			numberAxis.setVisible(false);
			numberAxis.setRange(-5, 260);
			numberAxis.setLabelPaint(xyRenderer.getItemPaint(0, 0));
			numberAxis.setTickLabelPaint(xyRenderer.getItemPaint(0, 0));
			/* Associate numberAxis with the corresponding plot control index,
			 * setting it up as rangeAxis (y-axis) */
			View.xyplot.setRangeAxis(plotCtrIndex, numberAxis);

			/* Associate dataset with the corresponding plot control index */
			View.xyplot.setDataset(plotCtrIndex, dataset);
			/* Binding dataset to previously created numberAxis */
			View.xyplot.mapDatasetToRangeAxis(plotCtrIndex, plotCtrIndex);

			/*
			 * Adds the serie to the created and set up dataset, serie will be
			 * storing one incoming data set (e.g. speed) while representation
			 * of this data set is defined by the xyRenderer, dataset and
			 * numberAxis.
			 */
			((XYSeriesCollection) dataset).addSeries(serie);
		}
	}


	@Override
	public void setModel(MMInterface model) {
		this.model = model;
		this.model.registerObserver(this);
	}


	@Override
	public void setReader(DataReaderInterface reader) {
		this.reader = reader;
		this.reader.setReadUdp();
	}


	@Override
	public String getTextID() {
		return id_txt.getText();
	}
}