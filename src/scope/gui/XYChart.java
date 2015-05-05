package scope.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.util.*;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;



public class XYChart {
	private static final Paint panelColor = new Color(50, 50, 50);

	private static int xyChartCount = 0;
	private static List<PlotObj> xyPlotCollection = new ArrayList<PlotObj>();
	private static JFreeChart jfreechart = null;
	private static XYPlot xyPlot = null;

	private static double interval = 60;
	 
	
	public XYChart(){
		XYChart.jfreechart = ChartFactory.createXYStepChart(
				"XY Chart", "main x-axis", "main y-axis", null,
				PlotOrientation.VERTICAL, false, false, false);
		XYChart.jfreechart.setBackgroundPaint(panelColor);
		
		XYChart.jfreechart.getXYPlot().setBackgroundPaint(new Color(0, 25, 0));
		XYChart.jfreechart.getXYPlot().setDomainGridlinePaint(new Color(0, 50, 0));
		XYChart.jfreechart.getXYPlot().setDomainGridlineStroke(new BasicStroke(1f));
		XYChart.jfreechart.getXYPlot().setRangeGridlinePaint(new Color(0, 50, 0));
		XYChart.jfreechart.getXYPlot().setRangeGridlineStroke(new BasicStroke(1f));
		XYChart.jfreechart.getXYPlot().setDomainPannable(true);
		XYChart.jfreechart.getXYPlot().setRangePannable(true);
		
		//DomainAxis (x-coordinate) setup,
		//this axis will be used for all following xyPlots
		//using this DomainAxis and its own RangeAxis (y-coordinate)
		XYChart.jfreechart.getXYPlot().setDomainAxis(new NumberAxis());
		XYChart.jfreechart.getXYPlot().getDomainAxis().setVisible(true);
		XYChart.jfreechart.getXYPlot().getDomainAxis().setLabelPaint(Color.BLACK);
		XYChart.jfreechart.getXYPlot().getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
		XYChart.jfreechart.getXYPlot().getDomainAxis().setFixedAutoRange(interval);
		
		XYChart.jfreechart.getXYPlot().getRangeAxis(0).setRange(0, 10);
		XYChart.jfreechart.getXYPlot().getRangeAxis(0).setVisible(false);
		
		XYChart.xyPlot = XYChart.jfreechart.getXYPlot();
		
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
	
	public static void createNewXYPlot(String plotName){
		if (plotName == ""){
			plotName = "xyPlot "+xyChartCount;
		}
		
		XYChart.PlotObj plotObj = new XYChart.PlotObj(plotName);
		
		xyPlotCollection.add(plotObj);
		System.out.println("ArrayList size: "+xyPlotCollection.size());
		
		//temp test, does not work
		plotObj.serie.add(10,10);		
		plotObj.serie.add(20,20);		
		plotObj.serie.add(100,100);
		XYSeries tempSerie = plotObj.serie;
	}
	
	public static XYSeries getSerieByName(String plotName){
		System.out.println("getSerieByName entered");
		for (PlotObj iter : xyPlotCollection){
			if (iter.plotName.equals(plotName))
				System.out.print(iter.plotName + " hat Daten erhalten");
				return iter.serie;
		}
		return null;
	}
	
	public static XYSeries getSeriesByIndex(int index){
		System.out.println("getSerieByIndex entered");
		if (xyPlotCollection.isEmpty()) return null;
		return xyPlotCollection.get(index).serie;
	}
	
	private static class PlotObj{
		int axisIndex				= -1;
		String plotName				= null;
		XYSeries serie				= null;
		XYSeriesCollection dataSet	= null;
		NumberAxis domainAxis		= null;
		NumberAxis rangeAxis 		= null;
		XYStepRenderer renderer 	= null;
		
		public PlotObj(String plotName){
			this.axisIndex = xyChartCount;
			this.plotName = plotName;
			this.serie = new XYSeries(plotName);
			this.dataSet = new XYSeriesCollection(this.serie);
			this.domainAxis = (NumberAxis) xyPlot.getDomainAxis(); 
			this.rangeAxis = new NumberAxis();
			this.renderer = new XYStepRenderer();
			
			xyPlot.setDataset(xyChartCount, this.dataSet);
			xyPlot.mapDatasetToDomainAxis(xyChartCount, xyChartCount);
			
			xyPlot.setRenderer(xyChartCount, this.renderer);
			
			this.rangeAxis.setRange(0, 255);
			this.rangeAxis.setLabel(this.plotName);
			this.rangeAxis.setTickLabelPaint(this.renderer.getItemPaint(0, 0));
			this.rangeAxis.setTickLabelsVisible(true);
			this.rangeAxis.setLabelPaint(this.renderer.getItemPaint(0, 0));
			this.rangeAxis.setVisible(true);
			xyPlot.setRangeAxis(xyChartCount, this.rangeAxis);
			
			
			xyChartCount++;
			System.out.println(this.plotName);
			System.out.println("xyChartCount: "+xyChartCount);
		}
	}
}
