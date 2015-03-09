package scope.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.util.*;

import javax.swing.JFrame;






import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;



public class XYChart {
	private static final Paint panelColor = new Color(50, 50, 50);

	private static int xyChartCount = 0;
//	private static List<XYPlot> xyPlotColl = new ArrayList<>();
	
	private static JFreeChart jfreechart = null;
	private static XYDataset xyData = null;
	private static XYPlot xyPlot = null;
	private static XYStepRenderer xyStepRenderer = null;
	private static NumberAxis numberAxis = null;
	
	private double interval = 60;
	 
	
	public XYChart(){
		XYChart.jfreechart = ChartFactory.createXYStepChart("XY Chart", "x-axis", "y-axis", null,
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
		mainAxis.setVisible(true);
		mainAxis.setLabelPaint(jfreechart.getXYPlot().getRenderer()
				.getItemPaint(0, 0));
		mainAxis.setLabel("Main Axis");
		mainAxis.setTickLabelPaint(jfreechart.getXYPlot().getRenderer()
				.getItemPaint(0, 0));
		mainAxis.setRange(0, 10);
		
		xyChartCount += xyChartCount;
		
		System.out.println("XYChart Constructor: JFreeChart set up.");
	}
	
	public static JFreeChart getJFreeChart(){
		return XYChart.jfreechart;		
	}
	
	public static void createNewXYPlot(){
		xyPlot.setRangeAxis(xyChartCount, numberAxis);
		numberAxis.setLabel("Byte 1");
		numberAxis.setTickLabelsVisible(true);
		numberAxis.setVisible(true);
		xyPlot.setDataset(xyChartCount, xyData);
		xyPlot.mapDatasetToRangeAxis(xyChartCount, xyChartCount);
		xyStepRenderer = new XYStepRenderer();
		xyPlot.setRenderer(xyChartCount, xyStepRenderer);
		XYChart.jfreechart.getXYPlot().getRenderer(xyChartCount).setBaseSeriesVisible(true);
		numberAxis.setLabelPaint(XYChart.jfreechart.getXYPlot().getRenderer(xyChartCount).getItemPaint(0, 0));
		numberAxis.setTickLabelPaint(XYChart.jfreechart.getXYPlot().getRenderer(xyChartCount).getItemPaint(0, 0));
		xyPlot.setRangeAxis(xyChartCount, numberAxis);
		numberAxis.setRange(-5, 260);
		
		// TODO save created XYPlot in an ArryList 
		xyPlotColl.add(xyPlot);
		
		xyChartCount += xyChartCount;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
