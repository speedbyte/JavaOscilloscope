package scope.gui;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * DynamicDataDemo.java
 * --------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited).
 * Contributor(s):   -;
 *
 * $Id: DynamicDataDemo.java,v 1.12 2004/05/07 16:09:03 mungady Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
 *
 */

//package org.jfree.chart.demo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import scope.gui.MMInterface;

/**
 * A demonstration application showing a time series chart where you can
 * dynamically add (random) data by clicking on a button.
 * 
 */
public class DynamicDataDemo extends ApplicationFrame implements
		ActionListener, ViewInterface {

	MMInterface model;

	/** The time series data. */
	private XYSeries series;
	private XYSeries series2;
	private XYSeries series3;
	private XYSeriesCollection dataset = new XYSeriesCollection();

	/** The most recent value added. */
	private double lastValue = 100.0;
	private double xValue = 0;

	private int seriesCount = 3;

	@Override
	public void notifyDataChange() {
		LinkedList<double[]> dataSetArrayQueue = model.getData();
		double[] dataSetArray;
		while ((dataSetArray = dataSetArrayQueue.poll()) != null) {
			for (int arrayIndex = 1, seriesIndex = 0; arrayIndex <= dataset.getSeriesCount();) {
				System.out.println("serie" + seriesIndex + "	"
						+ dataSetArray[0] + ", "+ dataSetArray[arrayIndex]);
				dataset.getSeries(seriesIndex).add(dataSetArray[0],dataSetArray[arrayIndex]);
				seriesIndex++;
				arrayIndex++;
			}
		}
	}

	/**
	 * Constructs a new demonstration application.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public DynamicDataDemo(final String title) {

		super(title);
		this.series = new XYSeries("Random Data");
		this.series.setMaximumItemCount(1000);// keeps series form clogging up so the display does not slow down
		this.series2 = new XYSeries("2nd Random Data");
		this.series2.setMaximumItemCount(1000);// keeps series form clogging up so the display does not slow down
		this.series3 = new XYSeries("3rd Random Data");
		this.series3.setMaximumItemCount(1000);// keeps series form clogging up so the display does not slow down
		this.dataset.addSeries(series);
		this.dataset.addSeries(series2);
		this.dataset.addSeries(series3);
		final JFreeChart chart = createChart(dataset);

		final ChartPanel chartPanel = new ChartPanel(chart);

		final JButton button1 = new JButton("Add New Data Item");
		button1.setActionCommand("ADD_DATA");
		button1.addActionListener(this);

		final JButton button2 = new JButton("Draw Refresh");
		button2.setActionCommand("REFRESH");
		button2.addActionListener(this);

		final JPanel content = new JPanel(new BorderLayout());
		final JPanel panelButtons = new JPanel(new BorderLayout());

		content.add(chartPanel, BorderLayout.NORTH);
		content.add(panelButtons, BorderLayout.SOUTH);
		panelButtons.add(button1, BorderLayout.NORTH);
		panelButtons.add(button2, BorderLayout.SOUTH);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(content);

		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	public void setModel(MMInterface model) {
		this.model = model;
		this.model.registerObserver(this);
	}

	/**
	 * Creates a sample chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return A sample chart.
	 */
	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart result = ChartFactory.createXYStepChart(
				"Dynamic Data Demo", "Time", "Value", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		final XYPlot plot = result.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(10);
		axis = plot.getRangeAxis();
		axis.setRange(0.0, 200.0);
		return result;
	}

	// ****************************************************************************
	// * JFREECHART DEVELOPER GUIDE *
	// * The JFreeChart Developer Guide, written by David Gilbert, is available
	// *
	// * to purchase from Object Refinery Limited: *
	// * *
	// * http://www.object-refinery.com/jfreechart/guide.html *
	// * *
	// * Sales are used to provide funding for the JFreeChart project - please *
	// * support us so that we can continue developing free software. *
	// ****************************************************************************

	/**
	 * Handles a click on the button by adding new (random) data.
	 * 
	 * @param e
	 *            the action event.
	 */
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals("ADD_DATA")) {
			for (int i = 0; i < 10; i++) {
				final double factor = 0.90 + 0.2 * Math.random();
				this.lastValue = this.lastValue * factor;
				final Millisecond now = new Millisecond();
				System.out.println("Now = " + now.toString());

				synchronized ("series update gate") {
					this.series.add(xValue++, this.lastValue);
				}
			}
		}
		if (e.getActionCommand().equals("REFRESH")) {
			System.out.println("Draw Refresh");
			model.notifyObservers();
		}
	}
}