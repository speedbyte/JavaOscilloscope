package scope.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

import javax.swing.JFrame;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;



public class XYChart {
	private static final Paint panelColor = new Color(50, 50, 50);

	private static int xyChartCount = 0;
	
	private static JFreeChart jfreechart = null;
	
	private double interval = 60;
	 
	
	public XYChart(){
		XYChart.jfreechart = ChartFactory.createXYStepChart(null, null, null, null,
				PlotOrientation.VERTICAL, false, false, false);
		XYChart.jfreechart.setBackgroundPaint(panelColor);
		
		XYChart.jfreechart.getXYPlot().setDomainAxis(new NumberAxis());
		XYChart.jfreechart.getXYPlot().setBackgroundPaint(new Color(0, 25, 0));
		XYChart.jfreechart.getXYPlot().setDomainGridlinePaint(new Color(0, 50, 0));
		XYChart.jfreechart.getXYPlot().setDomainGridlineStroke(new BasicStroke(1f));
		XYChart.jfreechart.getXYPlot().setRangeGridlinePaint(new Color(0, 50, 0));
		XYChart.jfreechart.getXYPlot().setRangeGridlineStroke(new BasicStroke(1f));
		XYChart.jfreechart.getXYPlot().setDomainPannable(true);
		XYChart.jfreechart.getXYPlot().setRangePannable(true);
		
		XYChart.jfreechart.getXYPlot().getDomainAxis().setLabelPaint(Color.BLACK);
		XYChart.jfreechart.getXYPlot().getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
		XYChart.jfreechart.getXYPlot().getDomainAxis().setFixedAutoRange(interval);
		
		/* mainAxis is created to draw the coordinate axis of JFreeChart
		   but is not used to display data */
		final NumberAxis mainAxis =
				(NumberAxis) XYChart.jfreechart.getXYPlot().getRangeAxis();
		jfreechart.getXYPlot().getRenderer().setBaseSeriesVisible(true);
		mainAxis.setVisible(false);
		
		xyChartCount += xyChartCount;
		
		System.out.println("XYChart Constructor: JFreeChart set up.");
	}
	
	public static JFreeChart getJFreeChart(){
		return XYChart.jfreechart;		
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
