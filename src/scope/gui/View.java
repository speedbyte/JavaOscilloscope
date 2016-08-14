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
import java.sql.SQLException;
import java.sql.Timestamp;
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
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Millisecond;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.omg.Messaging.SyncScopeHelper;

import de.ixxat.vci3.bal.IBalObject;
import de.ixxat.vci3.bal.can.CanMessage;
import de.ixxat.vci3.bal.can.ICanMessageReader;
import scope.data.ImportButton;
import scope.data.SQL;
import scope.vci.VciJava;
import scope.serial.SerialJava;
//import scope.gui.PanningChartPanel;
//import scope.gui.DataReaderInterface;

//View Class
@SuppressWarnings("serial")
public class View extends JFrame implements ViewInterface, ActionListener {

	// Variables
	private MMInterface model;
	private static int lastPlotCtrIndex = 0;
	private static int initDatasetCount = 0;
	private Configuration config = new Configuration();
	private boolean[] checkboxSelected = new boolean[24];
	private static int selectedCheckboxes = 0;
	private static int lastSelectedCheckboxes = 0;
	
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
	JButton btnImportConfig;
	JButton btnImportLogFile;
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
		//setPreferredSize(new Dimension(1920, 1080));
		setSize(new Dimension(Integer.parseInt(config.getDefaultIni().get("general", "dimensionX")), Integer.parseInt(config.getDefaultIni().get("general", "dimensionY"))));
		
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
//		DateAxis axis = (DateAxis) xyplot.getDomainAxis();
//		axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy HH:mm"));
		
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
					checkBoxPanel.revalidate();
					checkBoxPanel.repaint();
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
		btnStart.setBounds(443, 49, 92, 20);
		btnStart.setForeground(Color.BLACK);
		btnStart.setVisible(true);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(SQL.createConfigConnection(config)){
					try {
						SQL.readTable(new Timestamp(-1));
						SQL.readFlag = true;
					} catch (SQLException e2) {
						e2.printStackTrace();
					}
				}

			}

		});
		buttonPanel.add(btnStart);

		btnStop.setEnabled(true);
		btnStop.setForeground(Color.BLACK);
		btnStop.setBackground(Color.RED);
		btnStop.setBounds(443, 110, 92, 20);
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
//				lastSelectedAxis.setAutoRange(true);
//				lastSelectedAxis.setAutoRange(false);
				adjustRange();
//				applyConfig();
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

		JLabel label = new JLabel("");
		label.setForeground(new Color(153, 204, 204));
		label.setFont(new Font("Segoe UI", Font.BOLD, 18));
		label.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		label.setBounds(20, 40, 529, 100);
		buttonPanel.add(label);

		JButton btnClr = new JButton("Clear");
		btnClr.setForeground(Color.BLACK);
		btnClr.setBackground(Color.LIGHT_GRAY);
		btnClr.setBounds(443, 79, 92, 20);
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

		ButtonGroup group = new ButtonGroup();
		btnImportConfig = new JButton("Import Config");
		btnImportConfig.setForeground(Color.BLACK);
		btnImportConfig.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnImportConfig.setBounds(591, 48, 114, 25);
		btnImportConfig.addActionListener(new ActionListener() {
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
						"properties", "ini");
				chooserLog.setFileFilter(filter);
				*/
				chooserLog.showOpenDialog(null);
				File configFile = chooserLog.getSelectedFile();
				config.loadFile(configFile);
				applyConfig();
			}
		});
		buttonPanel.add(btnImportConfig);
		group.add(btnImportConfig);
		btnImportConfig.setSelected(false);
		
//		btnImportLogFile = new JButton("Import Logfile");
//		btnImportLogFile.setForeground(Color.BLACK);
//		btnImportLogFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//		btnImportLogFile.setBounds(591, 81, 114, 25);
//		btnImportLogFile.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				JFileChooser chooserLog = new JFileChooser("user.home");
//				FileNameExtensionFilter filter = new FileNameExtensionFilter(
//						"Log Files", "txt", "text", "asc");
//				chooserLog.setFileFilter(filter);
//				chooserLog.showOpenDialog(null);
//				File selLogFile = chooserLog.getSelectedFile();
//				reader.setImportFile(selLogFile);	
//			}
//		});
//		buttonPanel.add(btnImportLogFile);
//		group.add(btnImportLogFile);
//		btnImportLogFile.setSelected(false);
		
		JLabel lblProtocol = new JLabel("Configuration");
		lblProtocol.setForeground(new Color(153, 204, 204));
		lblProtocol.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblProtocol.setBounds(581, 10, 141, 25);
		buttonPanel.add(lblProtocol);

		JLabel label_2 = new JLabel("");
		label_2.setForeground(new Color(153, 204, 204));
		label_2.setFont(new Font("Segoe UI", Font.BOLD, 18));
		label_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		label_2.setBounds(581, 40, 133, 100);
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
		
		applyConfig();
		
		
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
		btnStop.setVisible(false);
		btnStop.setEnabled(false);
		btnStart.setVisible(true);
		btnStart.setEnabled(true);
		btnImportConfig.setEnabled(true);
		btnImportLogFile.setEnabled(true);
		btnAddDataset.setEnabled(true);
		btnRemoveDataset.setEnabled(true);
	}

	// ActionPerformed method
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("EXIT")) {
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
		final JCheckBox checkBox = new JCheckBox(config.getShortLabel(plotCtrIndex));
		checkBox.setForeground(Color.LIGHT_GRAY);
		checkBox.setBackground(panelColor);
		//checkBox.setSelected(true);
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (checkBox.isSelected()) {
					View.xyplot.getRenderer(plotCtrIndex).setBaseSeriesVisible(true);
					/* TODO description */
					lastSelectedAxis = (NumberAxis) View.xyplot.getRangeAxis(plotCtrIndex);
					lastSelectedAxis.setVisible(true);
					int maxCompIndex = checkBoxPanel.getComponentCount()-1;
					for (int index = 0; index <= maxCompIndex; index++) {
						checkBoxPanel.getComponent(index).setBackground(panelColor);
					}
					
					checkBox.setBackground(new Color(0, 0, 20));
					checkboxSelected[plotCtrIndex] = true;
					selectedCheckboxes++;
					adjustRange();
				} else {
					View.xyplot.getRenderer(plotCtrIndex).setBaseSeriesVisible(false);
					lastSelectedAxis = (NumberAxis) View.xyplot.getRangeAxis(plotCtrIndex);
					lastSelectedAxis.setVisible(false);
					checkboxSelected[plotCtrIndex] = false;
					selectedCheckboxes--;
					adjustRange();
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
		
		if (plotCtrIndex >= View.xyplot.getDatasetCount()) {

			//String name = new String("Data " + String.valueOf(plotCtrIndex));
			String name = new String(config.getLabel(plotCtrIndex));
			
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
		}
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
	

	public void applyConfig() {
		
		setSize(new Dimension(Integer.parseInt(config.getDefaultIni().get("general", "dimensionX")), Integer.parseInt(config.getDefaultIni().get("general", "dimensionY"))));
		
		for (int i = 0; i < checkBoxPanel.getComponentCount()+2; i++) {
//			Works, but something is still wrong
//			System.out.println("Componentcount: " + checkBoxPanel.getComponentCount());
			int latestCheckBoxIndex = checkBoxPanel.getComponentCount();
			System.out.println("lastcheckboxindex: " + latestCheckBoxIndex);
			if (latestCheckBoxIndex > 0) {
				checkBoxPanel.getComponent(latestCheckBoxIndex-1).validate();
				checkBoxPanel.remove(latestCheckBoxIndex-1);
				checkBoxPanel.revalidate();
				checkBoxPanel.repaint();
				removeDataset();
				System.out.println("Removed Checkbox with number: " + latestCheckBoxIndex);
			}
		}
			
		for(int index = 0; index < config.getDatasets(); index++){
			int plotCtrIndex = checkBoxPanel.getComponentCount()+1;
			JCheckBox checkBox = createCheckBox(plotCtrIndex);
			checkBoxPanel.add(checkBox);
			checkBox.setSelected(true);
			
			checkboxSelected[plotCtrIndex] = true;
			selectedCheckboxes++;

			addDataset();
			
			View.xyplot.getRenderer(plotCtrIndex).setBaseSeriesVisible(true);
			lastSelectedAxis = (NumberAxis) View.xyplot.getRangeAxis(plotCtrIndex);
			int maxCompIndex = checkBoxPanel.getComponentCount()-1;
			for (int i = 0; i <= maxCompIndex; i++) {
				checkBoxPanel.getComponent(i).setBackground(panelColor);
			}
			checkBox.setBackground(new Color(0, 0, 20));
			checkBox.revalidate();
			checkBox.repaint();
			int numberOfDatasets = config.getDatasets();
			
			int lowerR = config.getLowerRange(index);
			int upperR = config.getUpperRange(index);
			
			for (int i = 1; i < numberOfDatasets+1; i++) {
				if(plotCtrIndex < i){
					lowerR -= Math.abs(config.getLowerRange(plotCtrIndex-1))+Math.abs(config.getUpperRange(plotCtrIndex-1));
				}
				if(plotCtrIndex > i){
					upperR += Math.abs(config.getLowerRange(plotCtrIndex-1))+Math.abs(config.getUpperRange(plotCtrIndex-1));
				}
			}
			
			Range range = new Range(lowerR, upperR);
			View.xyplot.getRangeAxis(plotCtrIndex).setRange(range);

		}
	}
	
	public void adjustRange() {
		for(int index = 0; index < config.getDatasets(); index++){
			int plotCtrIndex = index+1;			
			if(checkboxSelected[plotCtrIndex]){
				int numberOfDatasets = config.getDatasets();
				int lowerR = config.getLowerRange(index);
				int upperR = config.getUpperRange(index);
				lastSelectedAxis = (NumberAxis) View.xyplot.getRangeAxis(plotCtrIndex);
				
				//Expand range only where checkboxes are selected
				for (int i = 1; i < numberOfDatasets+1; i++) {
					if(plotCtrIndex < i && checkboxSelected[i]){
						lowerR -= Math.abs(config.getLowerRange(plotCtrIndex-1))+Math.abs(config.getUpperRange(plotCtrIndex-1));
					}
					if(plotCtrIndex > i && checkboxSelected[i]){
						upperR += Math.abs(config.getLowerRange(plotCtrIndex-1))+Math.abs(config.getUpperRange(plotCtrIndex-1));
					}
				}
				
				Range range = new Range(lowerR, upperR);
				View.xyplot.getRangeAxis(plotCtrIndex).setRange(range);
			}
		}
	}
	
	@Override
	public void notifyDataChange() {
		
		LinkedList<double[]> dataArrayQueue = model.getData();
		double[] dataArray;
		while ((dataArray = dataArrayQueue.poll()) != null) {
			Timestamp ts = new Timestamp((long) dataArray[0]);
			View.serie0.add(ts.getTime(), null);
			//View.serie0.add(dataArray[0], null);
			for (int plotCtrIndex = 0; plotCtrIndex < lastPlotCtrIndex /*dataArray.length*/; plotCtrIndex++) {
				try{
					XYSeries xys = ((XYSeriesCollection) View.xyplot.getDataset(plotCtrIndex+1)).getSeries(0);
					xys.add(dataArray[0], dataArray[plotCtrIndex+1]);
				} catch (ArrayIndexOutOfBoundsException e){
//					System.out.println("Out of Bounds");
//					e.printStackTrace();
				} catch (NullPointerException e){
					System.out.println("Nullpointer in notifyDataChange");
					e.printStackTrace();
				}
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
	public String getTextID() {
		return id_txt.getText();
	}
}