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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;



public class XYChart {
	private static final Paint panelColor = new Color(50, 50, 50);

	private static int xyChartCount = 0;
	private static List<XYPlot> xyPlotColl = new ArrayList<>();
	
	private static JFreeChart jfreechart = null;
	private static XYSeriesCollection xyDataSetCollection = new XYSeriesCollection();
	private static XYPlot xyPlot = null;
	private static XYStepRenderer xyStepRenderer = null;
	private static NumberAxis numberAxis = null;
	
	private double interval = 60;
	 
	
	public XYChart(){
		XYChart.jfreechart = ChartFactory.createXYStepChart("XY Chart", "x-axis", "y-axis", xyDataSetCollection,
				PlotOrientation.VERTICAL, false, false, false);
		XYChart.jfreechart.setBackgroundPaint(panelColor);
		
		XYChart.jfreechart.getXYPlot().setBackgroundPaint(new Color(0, 25, 0));
		XYChart.jfreechart.getXYPlot().setDomainGridlinePaint(new Color(0, 50, 0));
		XYChart.jfreechart.getXYPlot().setDomainGridlineStroke(new BasicStroke(1f));
		XYChart.jfreechart.getXYPlot().setRangeGridlinePaint(new Color(0, 50, 0));
		XYChart.jfreechart.getXYPlot().setRangeGridlineStroke(new BasicStroke(1f));
		XYChart.jfreechart.getXYPlot().setDomainPannable(true);
		XYChart.jfreechart.getXYPlot().setRangePannable(true);
		
		XYChart.jfreechart.getXYPlot().setDomainAxis(new NumberAxis());
		XYChart.jfreechart.getXYPlot().getDomainAxis().setVisible(true);
		XYChart.jfreechart.getXYPlot().getDomainAxis().setLabelPaint(Color.BLACK);
		XYChart.jfreechart.getXYPlot().getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
		XYChart.jfreechart.getXYPlot().getDomainAxis().setFixedAutoRange(interval);
		
		XYChart.jfreechart.getXYPlot().getRangeAxis(0).setRange(0, 10);
		XYChart.jfreechart.getXYPlot().getRangeAxis(0).setVisible(false);
		
		/* mainAxis is created to draw the coordinate axis of JFreeChart
		   but is not used to display data */
//		final NumberAxis mainAxis =
//				(NumberAxis) XYChart.jfreechart.getXYPlot().getRangeAxis();
//		jfreechart.getXYPlot().getRenderer().setBaseSeriesVisible(true);
//		mainAxis.setVisible(true);
//		mainAxis.setLabelPaint(jfreechart.getXYPlot().getRenderer()
//				.getItemPaint(0, 0));
//		mainAxis.setLabel("Main Axis");
//		mainAxis.setTickLabelPaint(jfreechart.getXYPlot().getRenderer()
//				.getItemPaint(0, 0));
//		mainAxis.setRange(0, 10);
		
		xyChartCount++;
		
		System.out.println("XYChart Constructor: JFreeChart set up.");
		System.out.println("xyChartCount: "+xyChartCount);
	}
	
	public static JFreeChart getJFreeChart(){
		return XYChart.jfreechart;		
	}
	
	public static void createNewXYSerie(String serieName){
		if (serieName == null){
			serieName = "chart"+xyChartCount;
		}
		
		System.out.println(serieName);
		
		XYSeries serie = new XYSeries(serieName);
		xyDataSetCollection.addSeries(serie);

		xyPlot = XYChart.jfreechart.getXYPlot();
		xyPlot.setDataset(xyChartCount, xyDataSetCollection);
		xyPlot.mapDatasetToDomainAxis(xyChartCount, xyChartCount);
		
		XYStepRenderer renderer = new XYStepRenderer();
		xyPlot.setRenderer(xyChartCount, renderer);
		
		NumberAxis domainAxis   = (NumberAxis) xyPlot.getDomainAxis();
		NumberAxis rangeAxis    = new NumberAxis();
		rangeAxis.setRange(0, 255);
		rangeAxis.setLabel(serieName);
		rangeAxis.setTickLabelPaint(renderer.getItemPaint(0, 0));
		rangeAxis.setTickLabelsVisible(true);
		rangeAxis.setLabelPaint(renderer.getItemPaint(0, 0));
		rangeAxis.setVisible(true);
		xyPlot.setRangeAxis(xyChartCount, rangeAxis);
		
		
		xyChartCount++;		
//		XYPlot plot = new XYPlot(serie, domainAxis, rangeAxis, renderer);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
